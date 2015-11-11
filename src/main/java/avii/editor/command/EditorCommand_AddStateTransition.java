package main.java.avii.editor.command;

import org.simpleframework.xml.Attribute;

import main.java.avii.editor.command.result.SuccessfulEditorCommand;
import main.java.avii.editor.metamodel.entities.EntityClass;
import main.java.avii.editor.metamodel.entities.EntityEventInstance;
import main.java.avii.editor.metamodel.entities.EntityState;

public class EditorCommand_AddStateTransition extends BaseEditorCommand {

	@Attribute
	private String _classId;
	@Attribute
	private String _fromStateId;
	@Attribute
	private String _toStateId;
	@Attribute
	private String _instanceId;
	

	@Override
	protected IEditorCommandResult concreteDoCommand() {
		EntityClass theClass = this._domain.getEntityClassWithId(this._classId);
		EntityState fromState = theClass.getStateWithId(this._fromStateId);
		EntityState toState = theClass.getStateWithId(this._toStateId);
		EntityEventInstance instance = new EntityEventInstance(theClass.getDefaultEventSpecification(), fromState, toState);
		instance.setId(this._instanceId);
		return new SuccessfulEditorCommand();
	}

	@Override
	public IEditorCommandResult canDoCommand() {
		if (!this._domain.hasEntityClassWithId(this._classId)) {
			return failWhenClassNotFound(this._classId);
		}
		EntityClass theClass = this._domain.getEntityClassWithId(this._classId);
		if(!theClass.hasStateWithId(this._fromStateId)){
			return failWhenStateNotFound(this._fromStateId);
		}
		if(!theClass.hasStateWithId(this._toStateId)){
			return failWhenStateNotFound(this._toStateId);
		}
		return new SuccessfulEditorCommand();
	}

	public void setClassId(String classId) {
		this._classId = classId;
	}

	public void setFromStateId(String fromStateId) {
		this._fromStateId = fromStateId;
	}

	public void setToStateId(String toStateId) {
		this._toStateId = toStateId;
	}

	public void setInstanceId(String instanceUUID) {
		this._instanceId = instanceUUID;
	}

}
