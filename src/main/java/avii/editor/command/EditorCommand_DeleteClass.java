package main.java.avii.editor.command;

import org.simpleframework.xml.Attribute;
import org.simpleframework.xml.Element;

import main.java.avii.editor.command.result.SuccessfulEditorCommand;
import main.java.avii.editor.metamodel.entities.EntityClass;

@Element
public class EditorCommand_DeleteClass extends BaseEditorCommand {

	@Attribute
	private String _classId;

	@Override
	protected IEditorCommandResult concreteDoCommand() {
		EntityClass classToDelete = this._domain.getEntityClassWithId(this._classId);
		this._domain.deleteClass(classToDelete);
		return new SuccessfulEditorCommand();
	}

	@Override
	public IEditorCommandResult canDoCommand() {
		if(this._domain.hasEntityClassWithId(this._classId))
		{
			return new SuccessfulEditorCommand();
		}
		return failWhenClassNotFound(this._classId);
		
	}

	public void setClassId(String classId) {
		this._classId = classId;		
	}

}
