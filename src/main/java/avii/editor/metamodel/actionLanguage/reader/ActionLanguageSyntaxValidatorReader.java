package main.java.avii.editor.metamodel.actionLanguage.reader;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;

import main.java.avii.editor.contracts.metamodel.actionLanguage.syntax.IActionLanguageSyntax;
import main.java.avii.editor.metamodel.actionLanguage.instanceLifespan.StateInstanceLifespanManager;
import main.java.avii.editor.metamodel.actionLanguage.tempLifespan.TempVariableLifespanManager;
import main.java.avii.editor.metamodel.actionLanguage.validation.ActionLanguageSyntaxValidator;
import main.java.avii.editor.metamodel.actionLanguage.validation.IActionLanguageValidatorVisitor;
import main.java.avii.editor.metamodel.actionLanguage.validation.IVisitableActionLanguage;
import main.java.avii.editor.metamodel.actionLanguage.validation.error.syntax.IActionLanguageValidationError;
import main.java.avii.editor.metamodel.entities.EntityDomain;
import main.java.avii.editor.metamodel.entities.EntityProcedure;
import main.java.avii.editor.metamodel.entities.EntityState;

public class ActionLanguageSyntaxValidatorReader extends ActionLanguageReader {

	private EntityDomain domain;
	private StateInstanceLifespanManager _instanceLifespanManager = new StateInstanceLifespanManager();
	private IActionLanguageValidatorVisitor _validator = null;
	private StateInstanceLifespanManager _lifespanManager;
	private TempVariableLifespanManager _tempManager;
	private EntityState _state = null;

	public ActionLanguageSyntaxValidatorReader(InputStream in, EntityDomain theDomain, StateInstanceLifespanManager lifespanManager, TempVariableLifespanManager tempManager, HashMap<Integer, IActionLanguageSyntax> syntaxMap) {
		super(in);
		this.domain = theDomain;
		this._lifespanManager = lifespanManager;
		this._tempManager = tempManager;
		this._validator = new ActionLanguageSyntaxValidator(this.domain, this._lifespanManager, this._tempManager, syntaxMap);
	}

	@Override
	public void foundSyntaxLine(IActionLanguageSyntax lineSyntax, int lineNumber, int fileOffset, String line) {
		if (lineSyntax instanceof IVisitableActionLanguage) {
			IVisitableActionLanguage validatableActionLanguage = (IVisitableActionLanguage) lineSyntax;
			validatableActionLanguage.accept(_validator, lineNumber);
		}
	}
	
	@Override
	protected void readingComplete()
	{
		_validator.postValidate();
	}

	public StateInstanceLifespanManager getInstanceLifespanManager() {
		return _instanceLifespanManager;
	}

	public boolean isValid() {
		return _validator.getValidationErrors().isEmpty();
	}

	public ArrayList<IActionLanguageValidationError> getValidationErrors() {
		return _validator.getValidationErrors();
	}

	public void readingProcedureForState(EntityState theState) {
		this._state  = theState;
		_validator.validatingForState(this._state);
	}

	public void setProcedure(EntityProcedure entityProcedure) {
		this._validator.setProcedure(entityProcedure);
	}
	
}
