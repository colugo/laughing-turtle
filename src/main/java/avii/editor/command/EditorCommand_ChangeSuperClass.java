package main.java.avii.editor.command;

import org.simpleframework.xml.Attribute;
import org.simpleframework.xml.Element;

import main.java.avii.editor.command.result.SuccessfulEditorCommand;
import main.java.avii.editor.metamodel.entities.EntityClass;

@Element
public class EditorCommand_ChangeSuperClass extends BaseEditorCommand {

	@Attribute
	private String _classId;
	@Attribute(required=false)
	private String _superClassId;

	@Override
	protected IEditorCommandResult concreteDoCommand() {
		EntityClass theClass = this._domain.getEntityClassWithId(this._classId);
		if(this._superClassId == null){
			theClass.removeSuperClasses();
		}
		else{
			EntityClass superClass = this._domain.getEntityClassWithId(this._superClassId);
			theClass.addSuperClass(superClass);
		}
		return new SuccessfulEditorCommand();
	}

	@Override
	public IEditorCommandResult canDoCommand() {
		if(!this._domain.hasEntityClassWithId(this._classId))
		{
			return failWhenClassNotFound(this._classId);
		}
		if(this._superClassId != null && !this._domain.hasEntityClassWithId(this._superClassId))
		{
			return failWhenClassNotFound(this._superClassId);
		}
		return new SuccessfulEditorCommand();
	}

	public void setClassId(String classId) {
		this._classId = classId;
	}
	
	public void setSuperClassId(String superClassId) {
		this._superClassId = superClassId;
	}

}
