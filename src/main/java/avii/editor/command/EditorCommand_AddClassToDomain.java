package main.java.avii.editor.command;

import org.simpleframework.xml.Attribute;
import org.simpleframework.xml.Element;

import main.java.avii.editor.command.result.SuccessfulEditorCommand;
import main.java.avii.editor.metamodel.entities.EntityClass;

@Element
public class EditorCommand_AddClassToDomain extends BaseEditorCommand {


	@Attribute
	private String _className;
	@Attribute
	private String _classId;
	private EntityClass _newClass;

	
	@Override
	protected IEditorCommandResult concreteDoCommand() {
		this._newClass = new EntityClass(this._className);
		this._newClass.setId(this._classId);
		this._domain.addClass(this._newClass);
		return new SuccessfulEditorCommand();
	}

	public void setClassId(String newClassId) {
		this._classId = newClassId;
		this._className = "Class_" + this._classId;
	}

	public EntityClass getNewClass() {
		return _newClass;
	}

	@Override
	public IEditorCommandResult canDoCommand() {
		if(!this._domain.hasEntityClassWithName(this._className))
		{
			return new SuccessfulEditorCommand();
		}
		return failWhenClassExists(this._className);

	}



}
