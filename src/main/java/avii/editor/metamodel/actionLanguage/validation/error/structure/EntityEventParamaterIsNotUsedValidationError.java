package main.java.avii.editor.metamodel.actionLanguage.validation.error.structure;

import main.java.avii.editor.metamodel.entities.EntityClass;
import main.java.avii.editor.metamodel.entities.EntityEventParam;
import main.java.avii.editor.metamodel.entities.EntityEventSpecification;

public class EntityEventParamaterIsNotUsedValidationError implements IValidationWarning {

	private EntityClass _class;
	private EntityEventSpecification _spec;
	private EntityEventParam _param;

	public EntityEventParamaterIsNotUsedValidationError(EntityClass theClass, EntityEventSpecification theSpec, EntityEventParam theParam)
	{
		this._class = theClass;
		this._spec = theSpec;
		this._param = theParam;
	}
	
	public String explainError() {
		return "Class : " + this._class.getName() + " has event : " + this._spec.getName() + " with unused paramater : " + this._param.getName();
	}

}
