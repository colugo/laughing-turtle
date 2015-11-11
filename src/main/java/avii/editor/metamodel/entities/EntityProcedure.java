package main.java.avii.editor.metamodel.entities;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;

import javax.naming.NameNotFoundException;
import javax.naming.OperationNotSupportedException;

import main.java.avii.editor.contracts.metamodel.actionLanguage.syntax.IActionLanguageSyntax;
import main.java.avii.editor.contracts.metamodel.actionLanguage.syntax.ITempDeclarationActionLanguageSyntax;
import main.java.avii.editor.contracts.metamodel.actionLanguage.syntax.InvalidActionLanguageSyntaxException;
import main.java.avii.editor.contracts.metamodel.entities.datatypes.IEntityDatatype;
import main.java.avii.editor.metamodel.actionLanguage.entityReference.EntityRenameManager;
import main.java.avii.editor.metamodel.actionLanguage.entityReference.EntityUsageReferenceManager;
import main.java.avii.editor.metamodel.actionLanguage.eventManager.GeneratedEventManager;
import main.java.avii.editor.metamodel.actionLanguage.instanceLifespan.StateInstanceLifespanManager;
import main.java.avii.editor.metamodel.actionLanguage.lookups.NameDataTypeLineNumber;
import main.java.avii.editor.metamodel.actionLanguage.reader.ActionLanguageEntityReferenceReader;
import main.java.avii.editor.metamodel.actionLanguage.reader.ActionLanguageFirstEffectiveSyntaxLineReader;
import main.java.avii.editor.metamodel.actionLanguage.reader.ActionLanguageSyntaxAttributeAndEventParamUsageReader;
import main.java.avii.editor.metamodel.actionLanguage.reader.ActionLanguageSyntaxRationaliserReader;
import main.java.avii.editor.metamodel.actionLanguage.reader.ActionLanguageSyntaxValidatorReader;
import main.java.avii.editor.metamodel.actionLanguage.tempLifespan.TempVariableLifespanManager;
import main.java.avii.editor.metamodel.actionLanguage.validation.error.syntax.IActionLanguageValidationError;

public class EntityProcedure {

	protected String _rawText;
	private EntityState _state;
	protected StateInstanceLifespanManager _instanceLifespanManager = new StateInstanceLifespanManager();
	private TempVariableLifespanManager _tempLifespanManager = new TempVariableLifespanManager();
	private GeneratedEventManager _generatedEventManager = new GeneratedEventManager();
	protected ActionLanguageSyntaxRationaliserReader _syntaxRationaliserReader = null;
	protected HashMap<Integer, IActionLanguageSyntax> _syntaxMap = null;
	private ArrayList<NameDataTypeLineNumber> _nameDatatypeLineNumbers = new ArrayList<NameDataTypeLineNumber>();

	public EntityProcedure(EntityState theState) {
		this._state = theState;
		this._state.setProcedure(this);
	}

	protected EntityProcedure() {
		// hidden constructor for TestVectorProcedure sub class
	}

	public String getRawText()
	{
		if(this._rawText == null)
		{
			return "";
		}
		// make a new string, prevents outside modification
		return this._rawText + "";
	}
	
	public void setProcedure(String procedure) throws InvalidActionLanguageSyntaxException {
		this._rawText = procedure;
		calculateInstanceLifespanMap();
	}

	protected void initialiseInstanceLifespanManager() {
		_instanceLifespanManager = new StateInstanceLifespanManager();
		addSelfToInstanceLifeSpanManager();
	}

	private void addSelfToInstanceLifeSpanManager() {
		String owningClassName = this._state.getOwningClass().getName();
		try {
			_instanceLifespanManager.beginDeclaration("self", owningClassName, false, 0);
		} catch (Exception e) {
			// this cant fail
			e.printStackTrace();
		}
	}

	public void setTempVariableLifespanManager(TempVariableLifespanManager lifespanManager) {
		_tempLifespanManager = lifespanManager;
	}

	public void setGeneratedEventManager(GeneratedEventManager generatedEventManager) {
		this._generatedEventManager = generatedEventManager;
	}

	public EntityClass identifyClass(String instanceName, int lineNumber) throws NameNotFoundException, OperationNotSupportedException {
		String className = _instanceLifespanManager.identifyInstance(instanceName, lineNumber);
		EntityDomain domain = getDomain();
		EntityClass foundClass = domain.getEntityClassWithName(className);
		return foundClass;
	}

	public ITempDeclarationActionLanguageSyntax identifyTempDatatype(String tempName, int lineNumber) throws NameNotFoundException {
		return _tempLifespanManager.identifyTempSyntax(tempName, lineNumber);
	}

	public void calculateInstanceLifespanMap() throws InvalidActionLanguageSyntaxException {
		if (this._rawText == null) {
			return;
		}
		initialiseInstanceLifespanManager();
		_syntaxRationaliserReader = new ActionLanguageSyntaxRationaliserReader(getInputStreamFromRawText(), getDomain(), _instanceLifespanManager);
		_syntaxRationaliserReader.read();
		this._syntaxMap = _syntaxRationaliserReader.getSyntaxMap();
		setTempVariableLifespanManager(_syntaxRationaliserReader.getTempVariableLifespanManager());
		setGeneratedEventManager(_syntaxRationaliserReader.getGeneratedEventManager());
	}

	public boolean validate() throws InvalidActionLanguageSyntaxException {
		if (this._rawText == null) {
			return true;
		}
		ActionLanguageSyntaxValidatorReader reader = validateProcedure();
		reader.read();
		boolean isValidationValid = reader.isValid();
		boolean isRationaliserValid = _syntaxRationaliserReader.isValid();
		return isValidationValid && isRationaliserValid;
	}

	public boolean doesProcedureSetValueToAttribute(EntityClass theClass, EntityAttribute theAttribute) throws InvalidActionLanguageSyntaxException {
		ActionLanguageSyntaxAttributeAndEventParamUsageReader reader = new ActionLanguageSyntaxAttributeAndEventParamUsageReader(getInputStreamFromRawText(),
				getDomain(), theClass, theAttribute, _instanceLifespanManager);
		reader.read();
		return reader.wasAttributeSet();
	}

	public boolean doesProcedureReadAttributeValue(EntityClass theClass, EntityAttribute theAttribute) throws InvalidActionLanguageSyntaxException {
		ActionLanguageSyntaxAttributeAndEventParamUsageReader reader = new ActionLanguageSyntaxAttributeAndEventParamUsageReader(getInputStreamFromRawText(),
				getDomain(), theClass, theAttribute, _instanceLifespanManager);
		reader.read();
		return reader.wasAttributeRead();
	}

	public boolean doesProcedureReadEventParam(EntityEventSpecification eventSpec, EntityEventParam eventParam) throws InvalidActionLanguageSyntaxException {
		if (this._state.getAllTriggeringEventSpecs().contains(eventSpec)) {
			if (!checkEventParamComesFromSpecification(eventSpec, eventParam)) {
				return false;
			}

			ActionLanguageSyntaxAttributeAndEventParamUsageReader reader = new ActionLanguageSyntaxAttributeAndEventParamUsageReader(
					getInputStreamFromRawText(), getDomain(), eventParam, _instanceLifespanManager);
			reader.read();
			return reader.wasEventParamRead();
		}
		return false;
	}

	private boolean checkEventParamComesFromSpecification(EntityEventSpecification eventSpec, EntityEventParam eventParam) {
		try {
			if (!eventSpec.getParamWithName(eventParam.getName()).equals(eventParam)) {
				return false;
			}
			return true;
		} catch (NameNotFoundException nfne) {
			// I don't care about this exception
		}
		return false;
	}

	private ActionLanguageSyntaxValidatorReader validateProcedure() throws InvalidActionLanguageSyntaxException {
		ActionLanguageSyntaxValidatorReader reader = new ActionLanguageSyntaxValidatorReader(getInputStreamFromRawText(), getDomain(),
				_instanceLifespanManager, _tempLifespanManager, this._syntaxMap);
		reader.readingProcedureForState(this._state);
		reader.setProcedure(this);
		reader.read();
		return reader;
	}

	public ArrayList<IActionLanguageValidationError> getValidationErrors() throws InvalidActionLanguageSyntaxException {
		ActionLanguageSyntaxValidatorReader reader = validateProcedure();
		reader.read();
		ArrayList<IActionLanguageValidationError> errors = new ArrayList<IActionLanguageValidationError>();
		errors.addAll(reader.getValidationErrors());
		errors.addAll(_syntaxRationaliserReader.getValidationErrors());
		return errors;
	}

	protected EntityDomain getDomain() {
		return _state.getOwningClass().getDomain();
	}

	protected ByteArrayInputStream getInputStreamFromRawText() {
		String rawProcedureText = _rawText;
		if (rawProcedureText == null || rawProcedureText.length() == 0) {
			rawProcedureText = "";
		}
		return new ByteArrayInputStream(rawProcedureText.getBytes());
	}

	public GeneratedEventManager getGeneratedEventManager() {
		return this._generatedEventManager;
	}

	public InputStream getInputStream() {

		try {
			InputStream procedureInputStream = new ByteArrayInputStream(this._rawText.getBytes("UTF-8"));
			return procedureInputStream;
		} catch (Exception e) {
			return null;
		}
	}

	public EntityUsageReferenceManager getEntityUsageManager() throws InvalidActionLanguageSyntaxException {
		InputStream is = this.getInputStream();

		EntityUsageReferenceManager entityUsageReferenceManager = new EntityUsageReferenceManager(this._state.getOwningClass().getDomain());

		ActionLanguageEntityReferenceReader reader = new ActionLanguageEntityReferenceReader(is, this._instanceLifespanManager, entityUsageReferenceManager);
		reader.read();

		return entityUsageReferenceManager;
	}

	public boolean hasContent() {
		return this.getInputStream() != null;
	}

	public EntityRenameManager getEntityRenameManager() throws InvalidActionLanguageSyntaxException {
		InputStream is = this.getInputStream();

		EntityRenameManager entityRenameManager = new EntityRenameManager(this._state.getOwningClass().getDomain());

		ActionLanguageEntityReferenceReader reader = new ActionLanguageEntityReferenceReader(is, this._instanceLifespanManager, entityRenameManager);
		reader.read();
		entityRenameManager.setSyntaxList(reader.getFoundSyntax());
		entityRenameManager.setInstanceLifespanManager(this._instanceLifespanManager);
		return entityRenameManager;
	}

	public int closingBlockLineNumber(int lineNumber) {
		return this._syntaxRationaliserReader.closingIfLineNumber(lineNumber);
	}

	public int openingBlockLineNumber(int lineNumber) {
		return this._syntaxRationaliserReader.openingForLineNumber(lineNumber);
	}

	public IActionLanguageSyntax getFirstEffectiveSyntax() throws InvalidActionLanguageSyntaxException {
		InputStream is = this.getInputStream();
		ActionLanguageFirstEffectiveSyntaxLineReader reader = new ActionLanguageFirstEffectiveSyntaxLineReader(is);
		reader.read();
		IActionLanguageSyntax firstSyntax = reader.getFirstEffectiveSyntax();
		return firstSyntax;
	}

	public void addNameDatatypeLineNumber(NameDataTypeLineNumber ndtl) {
		this._nameDatatypeLineNumbers .add(ndtl);
	}

	public IEntityDatatype getDatatypeForNameOnLine(String name, int lineNumber) {
		for(NameDataTypeLineNumber ndtl : this._nameDatatypeLineNumbers)
		{
			if(ndtl.getName().equals(name) && lineNumber == ndtl.getLineNumber())
			{
				return ndtl.getDataType();
			}
		}
		return null;
	}

}
