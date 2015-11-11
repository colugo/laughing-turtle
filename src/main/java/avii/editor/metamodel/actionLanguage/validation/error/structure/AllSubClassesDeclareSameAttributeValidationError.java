package main.java.avii.editor.metamodel.actionLanguage.validation.error.structure;

import main.java.avii.editor.metamodel.entities.EntityClass;

public class AllSubClassesDeclareSameAttributeValidationError implements IValidationWarning {

	private EntityClass _class;
	private String _attributeName;

	public AllSubClassesDeclareSameAttributeValidationError(EntityClass superClass, String attributeName)
	{
		this._class = superClass;
		this._attributeName = attributeName;
	}
	
	public String explainError() {
		String message = this._class.getName() + " is a superclass, and all it's subclasses declare the same attribute '" + this._attributeName + "' - move it to the super class.";
		return message;
	}

}
