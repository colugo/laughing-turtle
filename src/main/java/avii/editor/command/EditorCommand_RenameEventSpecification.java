package main.java.avii.editor.command;

import org.simpleframework.xml.Attribute;
import org.simpleframework.xml.Element;

import main.java.avii.editor.command.result.SuccessfulEditorCommand;
import main.java.avii.editor.metamodel.entities.EntityClass;
import main.java.avii.editor.metamodel.entities.EntityEventSpecification;

@Element
public class EditorCommand_RenameEventSpecification extends BaseEditorCommand {

	@Attribute
	private String _classId;
	@Attribute
	private String _newSpecName;
	@Attribute
	private String _specId;

	@Override
	protected IEditorCommandResult concreteDoCommand() {
		EntityClass theClass = this._domain.getEntityClassWithId(this._classId);
		EntityEventSpecification theSpec = theClass.getEventSpecificationWithId(this._specId);
		theSpec.setName(this._newSpecName);
		return new SuccessfulEditorCommand();
	}

	@Override
	public IEditorCommandResult canDoCommand() {
		if(!this._domain.hasEntityClassWithId(this._classId))
		{
			return BaseEditorCommand.failWhenClassNotFound(this._classId);
		}
		EntityClass theClass = this._domain.getEntityClassWithId(this._classId);
		if(!theClass.hasEventSpecificationWithId(this._specId))
		{
			return BaseEditorCommand.failWhenSpecNotFound(this._classId, this._specId);
		}
		
		if(theClass.hasEventSpecificationWithName(this._newSpecName))
		{
			return BaseEditorCommand.failWhenSpecWithNameAlreadyExists(this._newSpecName);
		}
		
		return new SuccessfulEditorCommand();
	}

	public void setSpecId(String specId) {
		this._specId = specId;
	}

	public void setNewSpecName(String specName) {
		this._newSpecName = specName;
	}

	public void setClassId(String classId) {
		this._classId = classId;
		
	}

}
