package main.java.avii.editor.metamodel.actionLanguage.validation.error.structure;

import main.java.avii.editor.metamodel.entities.EntityAttribute;
import main.java.avii.editor.metamodel.entities.EntityClass;

public class EntityAttributeIsNotSetValidationError implements IValidationWarning {

	private EntityClass _class;
	private EntityAttribute _attribute;

	public EntityAttributeIsNotSetValidationError(EntityClass theClass, EntityAttribute theAttribute)
	{
		this._class = theClass;
		this._attribute = theAttribute;
	}
	
	public String explainError() {
		return "Class : " + this._class.getName() + " has an attribute that is never set : " + this._attribute.getName();
	}

}
