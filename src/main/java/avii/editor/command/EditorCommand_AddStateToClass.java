package main.java.avii.editor.command;

import org.simpleframework.xml.Attribute;
import org.simpleframework.xml.Element;

import main.java.avii.editor.command.result.SuccessfulEditorCommand;
import main.java.avii.editor.metamodel.entities.EntityClass;
import main.java.avii.editor.metamodel.entities.EntityState;

@Element
public class EditorCommand_AddStateToClass extends BaseEditorCommand {

	@Attribute
	private String _classId;
	@Attribute
	private String _newStateId;
	private EntityState _newEntityState;

	public void setStateId(String newStateName) {
		this._newStateId = newStateName;
	}

	public EntityState getNewState() {
		return this._newEntityState;
	}

	public void setClassId(String classId) {
		this._classId = classId;
	}

	@Override
	protected IEditorCommandResult concreteDoCommand() {
		EntityClass theClass = this._domain.getEntityClassWithId(this._classId);
		this._newEntityState = new EntityState("State_" + this._newStateId);
		this._newEntityState.setId(this._newStateId);
		theClass.addState(this._newEntityState);
		return new SuccessfulEditorCommand();
	}


	@Override
	public IEditorCommandResult canDoCommand() {
		if (!this._domain.hasEntityClassWithId(this._classId)) {
			return failWhenClassNotFound(this._classId);
		}

		EntityClass theClass = this._domain.getEntityClassWithId(this._classId);
		if (theClass.hasStateWithName("State_" + this._newStateId)) {
			return failWhenStateWithNameAlreadyExists("State_" + this._newStateId);
		}

		return new SuccessfulEditorCommand();
	}

}
