package main.java.avii.editor.metamodel.actionLanguage.reader;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Stack;

import javax.naming.NameNotFoundException;
import javax.naming.OperationNotSupportedException;

import main.java.avii.editor.contracts.metamodel.actionLanguage.InstanceBean.InstanceCreateAssociationBean;
import main.java.avii.editor.contracts.metamodel.actionLanguage.InstanceBean.InstanceCreateBean;
import main.java.avii.editor.contracts.metamodel.actionLanguage.InstanceBean.InstanceDestroyBean;
import main.java.avii.editor.contracts.metamodel.actionLanguage.InstanceBean.InstanceSelectRelatedByBean;
import main.java.avii.editor.contracts.metamodel.actionLanguage.rationaliser.IInstanceCreateActionLanguage;
import main.java.avii.editor.contracts.metamodel.actionLanguage.rationaliser.IInstanceCreateAssociationActionLanguage;
import main.java.avii.editor.contracts.metamodel.actionLanguage.rationaliser.IInstanceCreateDestroyActionLanguage;
import main.java.avii.editor.contracts.metamodel.actionLanguage.rationaliser.IInstanceCreateFromInstanceSetActionLanguage;
import main.java.avii.editor.contracts.metamodel.actionLanguage.rationaliser.IInstanceDestroyActionLanguage;
import main.java.avii.editor.contracts.metamodel.actionLanguage.rationaliser.IInstanceDestroyFromInstanceSetActionLanguage;
import main.java.avii.editor.contracts.metamodel.actionLanguage.rationaliser.IInstanceSelectActionLanguage;
import main.java.avii.editor.contracts.metamodel.actionLanguage.rationaliser.IInstanceSelectRelatedByActionLanguage;
import main.java.avii.editor.contracts.metamodel.actionLanguage.rationaliser.IInstanceWhereLogicSelectedActionLanguage;
import main.java.avii.editor.contracts.metamodel.actionLanguage.syntax.IActionLanguageSyntax;
import main.java.avii.editor.contracts.metamodel.actionLanguage.syntax.ICloseIfSyntax;
import main.java.avii.editor.contracts.metamodel.actionLanguage.syntax.IGenerateActionLanguageSyntax;
import main.java.avii.editor.contracts.metamodel.actionLanguage.syntax.IOpenIfSyntax;
import main.java.avii.editor.metamodel.actionLanguage.codeBlock.BaseCodeBlock;
import main.java.avii.editor.metamodel.actionLanguage.codeBlock.CodeBlockCloseFor;
import main.java.avii.editor.metamodel.actionLanguage.codeBlock.CodeBlockCloseIf;
import main.java.avii.editor.metamodel.actionLanguage.codeBlock.CodeBlockElse;
import main.java.avii.editor.metamodel.actionLanguage.codeBlock.CodeBlockManager;
import main.java.avii.editor.metamodel.actionLanguage.codeBlock.CodeBlockOpenFor;
import main.java.avii.editor.metamodel.actionLanguage.codeBlock.CodeBlockOpenIf;
import main.java.avii.editor.metamodel.actionLanguage.eventManager.GeneratedEventManager;
import main.java.avii.editor.metamodel.actionLanguage.instanceLifespan.StateInstanceLifespanManager;
import main.java.avii.editor.metamodel.actionLanguage.syntax.Syntax_Else;
import main.java.avii.editor.metamodel.actionLanguage.syntax.Syntax_TempExpression;
import main.java.avii.editor.metamodel.actionLanguage.tempLifespan.TempVariableLifespanManager;
import main.java.avii.editor.metamodel.actionLanguage.validation.error.syntax.CodeBlockMismatchValidationError;
import main.java.avii.editor.metamodel.actionLanguage.validation.error.syntax.IActionLanguageValidationError;
import main.java.avii.editor.metamodel.entities.EntityDomain;
import main.java.avii.editor.metamodel.entities.EntityRelation;
import main.java.avii.editor.metamodel.entities.helpers.EntityRelationChainHelper;

public class ActionLanguageSyntaxRationaliserReader extends ActionLanguageReader {

	private EntityDomain domain;
	private StateInstanceLifespanManager _instanceLifespanManager = null;
	private TempVariableLifespanManager _tempLifespanManager = new TempVariableLifespanManager();
	private GeneratedEventManager _generatedEventManager = new GeneratedEventManager();
	private Stack<String> _instanceSetNames = new Stack<String>();
	private CodeBlockManager _codeBlockManager = new CodeBlockManager();
	private ArrayList<IActionLanguageValidationError> _errors = new ArrayList<IActionLanguageValidationError>();
	private HashMap<Integer, IActionLanguageSyntax> _syntaxMap = new HashMap<Integer, IActionLanguageSyntax>();

	public ActionLanguageSyntaxRationaliserReader(InputStream in, EntityDomain theDomain, StateInstanceLifespanManager instanceLifespanManager) {
		super(in);
		this.domain = theDomain;
		this._instanceLifespanManager = instanceLifespanManager;
	}

	private void addError(IActionLanguageValidationError error) {
		if (!doesErrorListHaveErrorAlready(error)) {
			_errors.add(error);
		}
	}
	
	public boolean isValid()
	{
		return _errors.isEmpty();
	}
	
	public ArrayList<IActionLanguageValidationError> getValidationErrors() {
		return _errors;
	}
	
	private boolean doesErrorListHaveErrorAlready(IActionLanguageValidationError theNewError) {
		String newErrorString = theNewError.explainError();
		for (IActionLanguageValidationError error : _errors) {
			if (error.explainError().equals(newErrorString)) {
				return true;
			}
		}
		return false;
	}
	
	@Override
	public void foundSyntaxLine(IActionLanguageSyntax lineSyntax, int lineNumber, int fileOffset, String line) {

		try {

			rationaliseSyntaxLine(lineSyntax, lineNumber);
			this._syntaxMap.put(lineNumber, lineSyntax);

		} catch (NameNotFoundException nfne) {
			nfne.printStackTrace();
		} catch (OperationNotSupportedException onse) {
			onse.printStackTrace();
		}
	}

	private void rationaliseSyntaxLine(IActionLanguageSyntax lineSyntax, int lineNumber) throws OperationNotSupportedException, NameNotFoundException {
		if (lineSyntax instanceof IInstanceCreateDestroyActionLanguage) {
			manageSimpleCreateOrDestroyInstanceLifespan(lineSyntax, lineNumber);
		}

		if (lineSyntax instanceof IInstanceCreateAssociationActionLanguage) {
			manageCreateAssociationLifespan(lineSyntax, lineNumber);
		}

		if (lineSyntax instanceof IInstanceSelectActionLanguage) {
			manageSelectInstanceLifespan(lineSyntax, lineNumber);
		}

		if (lineSyntax instanceof Syntax_TempExpression) {
			manageTempExpression((Syntax_TempExpression) lineSyntax, lineNumber);
		}
		
		if (lineSyntax instanceof IGenerateActionLanguageSyntax) {
			manageEventGeneration((IGenerateActionLanguageSyntax) lineSyntax, lineNumber);
		}
		
		if (lineSyntax instanceof ICloseIfSyntax) {
			// close first
			manageCloseIfSyntax(lineSyntax, lineNumber);
		}
		
		if (lineSyntax instanceof IOpenIfSyntax) {
			// close first
			manageOpenIfSyntax(lineSyntax, lineNumber);
		}

	}

	private void manageCloseIfSyntax(IActionLanguageSyntax lineSyntax, int lineNumber) {
		// find the line of the current open block
		// close all declarations that were opened between the open and this line number
		BaseCodeBlock currentBlock = _codeBlockManager.getCurrentCodeBlock();
		if(lineSyntax instanceof Syntax_Else)
		{
			_codeBlockManager.addBlock(new CodeBlockElse(), lineNumber);
		}
		else
		{
			_codeBlockManager.addBlock(new CodeBlockCloseIf(), lineNumber);
		}

		try {
			int openingLine = currentBlock.getLineNumber();
			this._instanceLifespanManager.closeAllOpenDeclarationsThatWereOpenedOnOrAfterLine(openingLine, lineNumber);
		} catch (Exception e) {}
	}

	private void manageOpenIfSyntax(IActionLanguageSyntax lineSyntax, int lineNumber) {
		if(lineSyntax instanceof Syntax_Else)
		{
			_codeBlockManager.addBlock(new CodeBlockElse(), lineNumber);
		}
		else
		{
			_codeBlockManager.addBlock(new CodeBlockOpenIf(), lineNumber);
		}
		
	}

	private void manageEventGeneration(IGenerateActionLanguageSyntax lineSyntax, int lineNumber) {
		String eventName = lineSyntax.eventName();
		this._generatedEventManager.registerEvent(eventName);
	}

	private void manageTempExpression(Syntax_TempExpression lineSyntax, int lineNumber) {
		_tempLifespanManager.declareTemp(lineSyntax, lineNumber);
	}

	private void manageCreateAssociationLifespan(IActionLanguageSyntax lineSyntax, int lineNumber) throws NameNotFoundException, OperationNotSupportedException {
		InstanceCreateAssociationBean lifespanBean = ((IInstanceCreateAssociationActionLanguage) lineSyntax).getInstanceCreateBean();
		if (domain.hasRelationWithName(lifespanBean.getRelationName())) {
			EntityRelation relation = domain.getRelationWithName(lifespanBean.getRelationName());
			if (relation.hasAssociation()) {
				String associationClassName = relation.getAssociation().getName();
				_instanceLifespanManager.beginDeclaration(lifespanBean.getInstanceName(), associationClassName, false, lineNumber);
			}
		}
	}

	private void manageSelectInstanceLifespan(IActionLanguageSyntax lineSyntax, int lineNumber) {
		if (lineSyntax instanceof IInstanceSelectRelatedByActionLanguage) {
			try {
				InstanceSelectRelatedByBean instanceBean = ((IInstanceSelectRelatedByActionLanguage) lineSyntax).getSelectBean();
				String instanceName = instanceBean.getCreatedInstanceName();
				String startPointName = instanceBean.getStartingInstanceName();
				String startPointClass = _instanceLifespanManager.identifyInstance(startPointName, lineNumber);
				String endPointClass = EntityRelationChainHelper.determineEndpointType(domain, instanceBean.getOrderedRelationNames(), startPointClass);
				_instanceLifespanManager.beginDeclaration(instanceName, endPointClass, instanceBean.isInstanceSet(), lineNumber);
				
				// also add the 'selected' instance to be of t ype endPointClass
				_instanceLifespanManager.beginDeclaration("selected", endPointClass, false, lineNumber);
				_instanceLifespanManager.endDeclaration("selected", lineNumber);
				
			} catch (Exception e) {
			}
		}
	}

	private void manageSimpleCreateOrDestroyInstanceLifespan(IActionLanguageSyntax lineSyntax, int lineNumber) throws OperationNotSupportedException,
			NameNotFoundException {
		// destroy must come first, for reclassify
		if (lineSyntax instanceof IInstanceDestroyActionLanguage) {
			InstanceDestroyBean instanceBean = ((IInstanceDestroyActionLanguage) lineSyntax).getDestroyInstance();
			_instanceLifespanManager.endDeclaration(instanceBean.getInstanceName(), lineNumber);
		}
		if (lineSyntax instanceof IInstanceCreateActionLanguage) {
			InstanceCreateBean instanceBean = ((IInstanceCreateActionLanguage) lineSyntax).getCreateInstance();
			int effectiveLineNumber = determineEffectiveCreateLineNumber(lineNumber, instanceBean);
			_instanceLifespanManager.beginDeclaration(instanceBean.getInstanceName(), instanceBean.getEntityClassName(), instanceBean.isInstanceSet(),
					effectiveLineNumber);
		}
		if (lineSyntax instanceof IInstanceCreateFromInstanceSetActionLanguage) {
			InstanceCreateBean instanceBean = ((IInstanceCreateFromInstanceSetActionLanguage) lineSyntax).getCreateInstance();
			String instanceName = instanceBean.getInstanceName();
			String instanceSetName = instanceBean.getEntityClassName();

			// don't allow invalid code blocks to be declared in the instance manager
			boolean didAddingOpenForLoopKeepCodeBlockValid = _codeBlockManager.addBlock(new CodeBlockOpenFor(), lineNumber);
			if (didAddingOpenForLoopKeepCodeBlockValid && willAddingNameToInstanceSetStackKeepItValid(instanceName)) {
				try {
					String instanceSetClass = _instanceLifespanManager.identifyInstance(instanceSetName, lineNumber);
					_instanceLifespanManager.beginDeclaration(instanceName, instanceSetClass, instanceBean.isInstanceSet(), lineNumber);
					_instanceSetNames.push(instanceName);
				} catch (NameNotFoundException nfne) {
					// this will prevent an exception from being thrown here
					// when the instanceset cant be found
				}
			}
			else
			{
				addError(new CodeBlockMismatchValidationError(new CodeBlockOpenFor()));
			}
		}
		if (lineSyntax instanceof IInstanceDestroyFromInstanceSetActionLanguage) {
			// if adding the close for is ok, pop the instance name from the stack and issue a endDeclaration
			boolean didAddingCloseForLoopKeepCodeBlockValid = _codeBlockManager.addBlock(new CodeBlockCloseFor(), lineNumber);
			if (didAddingCloseForLoopKeepCodeBlockValid) {
				try {
					
					// close all declarations since the open block
					BaseCodeBlock currentBlock = _codeBlockManager.getCurrentCodeBlock();
					int openingLine = currentBlock.getLineNumber();
					this._instanceLifespanManager.closeAllOpenDeclarationsThatWereOpenedOnOrAfterLine(openingLine, lineNumber);
					
				} catch (Exception nfne) {
					// this will prevent an exception from being thrown here
					// when the instanceset cant be found
					
				}
			}
		}
		if (lineSyntax instanceof IInstanceWhereLogicSelectedActionLanguage) {
			InstanceCreateBean instanceBean = ((IInstanceWhereLogicSelectedActionLanguage) lineSyntax).getWhereSelectedInstance();
			_instanceLifespanManager.beginDeclaration(instanceBean.getInstanceName(), instanceBean.getEntityClassName(), instanceBean.isInstanceSet(),
					lineNumber);
			_instanceLifespanManager.endDeclaration(instanceBean.getInstanceName(), lineNumber);
		}

	}

	private boolean willAddingNameToInstanceSetStackKeepItValid(String instanceSetName)
	{
		return !_instanceSetNames.contains(instanceSetName);
	}
	
	private int determineEffectiveCreateLineNumber(int lineNumber, InstanceCreateBean bean) {
		if (bean.isItEffectiveFromTheNextLine()) {
			return lineNumber + 1;
		}
		return lineNumber;
	}

	public StateInstanceLifespanManager getInstanceLifespanManager() {
		return _instanceLifespanManager;
	}

	public TempVariableLifespanManager getTempVariableLifespanManager() {
		return _tempLifespanManager;
	}

	public GeneratedEventManager getGeneratedEventManager() {
		return _generatedEventManager;
	}

	public int closingIfLineNumber(int lineNumber) {
		return this._codeBlockManager.getLineOfCloseBlockForOpenBlockOnLine(lineNumber);	
	}

	public int openingForLineNumber(int lineNumber) {
		return this._codeBlockManager.getLineOfOpenBlockForCloseBlockOnLine(lineNumber);	
	}

	public HashMap<Integer, IActionLanguageSyntax> getSyntaxMap()
	{
		return this._syntaxMap;
	}
	
}
