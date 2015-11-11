package main.java.avii.editor.command;

import org.simpleframework.xml.Attribute;
import org.simpleframework.xml.Element;

import main.java.avii.editor.command.result.SuccessfulEditorCommand;
import main.java.avii.editor.metamodel.entities.EntityClass;
import main.java.avii.editor.metamodel.entities.EntityEventSpecification;

@Element
public class EditorCommand_CreateEventSpecification extends BaseEditorCommand {

	@Attribute
	private String _classId;
	@Attribute
	private String _newSpecId;

	public void setSpecId(String specId) {
		this._newSpecId = specId;
	}

	public void setClassId(String classId) {
		this._classId = classId;
	}

	@Override
	protected IEditorCommandResult concreteDoCommand() {
		EntityClass theClass = this._domain.getEntityClassWithId(this._classId);
		EntityEventSpecification spec = new EntityEventSpecification(theClass, "EventSpecification_" + this._newSpecId);
		spec.setId(this._newSpecId);
		return new SuccessfulEditorCommand();
	}


	@Override
	public IEditorCommandResult canDoCommand() {
		if (!this._domain.hasEntityClassWithId(this._classId)) {
			return failWhenClassNotFound(this._classId);
		}

		return new SuccessfulEditorCommand();
	}

}
