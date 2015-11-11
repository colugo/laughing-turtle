package main.java.avii.editor.metamodel.actionLanguage.validation.error.structure;

import main.java.avii.editor.metamodel.entities.EntityClass;
import main.java.avii.editor.metamodel.entities.EntityState;

public class EntityClassCannotHaveIslandStatesValidationError implements IValidationError {

	private EntityClass _class = null;
	private EntityState _state = null;
	
	public EntityClassCannotHaveIslandStatesValidationError(EntityClass theClass, EntityState theState)
	{
		this._class = theClass;
		this._state = theState;
	}
	
	public String explainError() {
		return "EntityClass '" + this._class.getName() + "' has an island state '" + this._state.getName() + "'";
	}

}
