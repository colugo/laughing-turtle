package main.java.avii.editor.metamodel.actionLanguage.reader;

import java.io.InputStream;

import main.java.avii.editor.contracts.metamodel.actionLanguage.syntax.IActionLanguageSyntax;
import main.java.avii.editor.metamodel.actionLanguage.instanceLifespan.StateInstanceLifespanManager;
import main.java.avii.editor.metamodel.actionLanguage.validation.ClassAttributeAndEventParamUsageChecker;
import main.java.avii.editor.metamodel.actionLanguage.validation.IVisitableActionLanguage;
import main.java.avii.editor.metamodel.entities.EntityAttribute;
import main.java.avii.editor.metamodel.entities.EntityClass;
import main.java.avii.editor.metamodel.entities.EntityDomain;
import main.java.avii.editor.metamodel.entities.EntityEventParam;

public class ActionLanguageSyntaxAttributeAndEventParamUsageReader extends ActionLanguageReader {

	private EntityAttribute _attribute;
	private ClassAttributeAndEventParamUsageChecker _checker;
	private StateInstanceLifespanManager _instanceLifespanManager;
	private EntityDomain _domain;
	private EntityClass _class;
	private EntityEventParam _eventParam;

	public ActionLanguageSyntaxAttributeAndEventParamUsageReader(InputStream in, EntityDomain domain, EntityClass theClass, EntityAttribute theAttribute, StateInstanceLifespanManager instanceLifespanManager) {
		super(in);
		this._attribute = theAttribute;
		this._class = theClass;
		this._instanceLifespanManager = instanceLifespanManager;
		this._domain = domain;
		this._checker = new ClassAttributeAndEventParamUsageChecker(_class,_attribute,_domain, _instanceLifespanManager);
	}

	public ActionLanguageSyntaxAttributeAndEventParamUsageReader(InputStream in, EntityDomain domain, EntityEventParam eventParam, StateInstanceLifespanManager instanceLifespanManager) {
		super(in);
		this._eventParam = eventParam;
		this._instanceLifespanManager = instanceLifespanManager;
		this._domain = domain;
		this._checker = new ClassAttributeAndEventParamUsageChecker(_eventParam,_domain, _instanceLifespanManager);
	}

	@Override
	public void foundSyntaxLine(IActionLanguageSyntax lineSyntax, int lineNumber, int fileOffset, String line) {
		if (lineSyntax instanceof IVisitableActionLanguage) {
			IVisitableActionLanguage validatableActionLanguage = (IVisitableActionLanguage) lineSyntax;
			validatableActionLanguage.accept(_checker, lineNumber);
		}
	}

	public boolean wasAttributeSet() {
		return this._checker.wasAttributeSet();
	}

	public boolean wasAttributeRead() {
		return this._checker.wasAttributeRead();
	}

	public boolean wasEventParamRead() {
		return this._checker.wasEventParamRead();
	}
}
