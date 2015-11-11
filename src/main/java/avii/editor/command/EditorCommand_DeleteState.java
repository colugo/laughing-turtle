package main.java.avii.editor.command;

import org.simpleframework.xml.Attribute;
import org.simpleframework.xml.Element;

import main.java.avii.editor.command.result.SuccessfulEditorCommand;
import main.java.avii.editor.metamodel.entities.EntityClass;
import main.java.avii.editor.metamodel.entities.EntityState;

@Element
public class EditorCommand_DeleteState extends BaseEditorCommand {

	@Attribute
	private String _stateId;
	@Attribute
	private String _classId;

	@Override
	protected IEditorCommandResult concreteDoCommand() {
		EntityClass theClass = this._domain.getEntityClassWithId(this._classId);
		EntityState theState = theClass.getStateWithId(this._stateId);
		theClass.removeState(theState);
		return new SuccessfulEditorCommand();
	}

	@Override
	public IEditorCommandResult canDoCommand() {
		if(!this._domain.hasEntityClassWithId(this._classId))
		{
			return BaseEditorCommand.failWhenClassNotFound(this._classId);
		}
		EntityClass theClass = this._domain.getEntityClassWithId(this._classId);
		if(!theClass.hasStateWithId(this._stateId))
		{
			return BaseEditorCommand.failWhenStateNotFound(this._classId, this._stateId);
		}
		return new SuccessfulEditorCommand();
	}

	public void setStateId(String stateId) {
		this._stateId = stateId;
	}

	public void setClassId(String classId) {
		this._classId = classId;
	}

}
