package main.java.avii.editor.command;

import org.simpleframework.xml.Attribute;

import main.java.avii.editor.command.result.SuccessfulEditorCommand;
import main.java.avii.editor.metamodel.entities.EntityClass;
import main.java.avii.editor.metamodel.entities.EntityEventInstance;
import main.java.avii.editor.metamodel.entities.EntityEventSpecification;
import main.java.avii.editor.metamodel.entities.EntityState;

public class EditorCommand_ChangeStateTransition extends BaseEditorCommand {

	@Attribute
	private String _classId;
	@Attribute
	private String _fromStateId;
	@Attribute
	private String _toStateId;
	@Attribute
	private String _instanceId;
	@Attribute
	private String _specId;
	

	@Override
	protected IEditorCommandResult concreteDoCommand() {
		EntityClass theClass = this._domain.getEntityClassWithId(this._classId);
		EntityState fromState = theClass.getStateWithId(this._fromStateId);
		EntityState toState = theClass.getStateWithId(this._toStateId);
		EntityEventInstance instance = theClass.getEventInstanceWithId(this._instanceId);
		EntityEventSpecification spec = theClass.getEventSpecificationWithId(this._specId);

		theClass.changeEventInstance(instance, spec, fromState, toState);
		
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
		if(!theClass.hasEventSpecificationWithId(this._specId)){
			return failWhenEventSpecNotFound(this._specId);
		}
		if(!theClass.hasEventInstanceWithId(this._instanceId)){
			return failWhenEventInstanceNotFound(this._instanceId);
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
	
	public void setSpecId(String specUUID) {
		this._specId = specUUID;
	}

}
