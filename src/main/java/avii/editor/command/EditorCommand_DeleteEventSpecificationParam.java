package main.java.avii.editor.command;

import org.simpleframework.xml.Attribute;
import org.simpleframework.xml.Element;

import main.java.avii.editor.command.result.SuccessfulEditorCommand;
import main.java.avii.editor.metamodel.entities.EntityClass;
import main.java.avii.editor.metamodel.entities.EntityEventSpecification;

@Element
public class EditorCommand_DeleteEventSpecificationParam extends BaseEditorCommand {

	@Attribute
	private String _classId;
	@Attribute
	private String _specId;
	@Attribute
	private String _paramId;

	@Override
	protected IEditorCommandResult concreteDoCommand() {
		EntityClass theClass = this._domain.getEntityClassWithId(this._classId);
		EntityEventSpecification theSpec = theClass.getEventSpecificationWithId(this._specId);
		theSpec.removeEventParamWithId(this._paramId);
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
		EntityEventSpecification theSpec = theClass.getEventSpecificationWithId(this._specId );
		if(!theSpec.hasParamWithId(this._paramId))
		{
			return BaseEditorCommand.failWhenParamNotFound(this._paramId);
		}
		
		return new SuccessfulEditorCommand();
	}

	public void setSpecId(String specId) {
		this._specId = specId;
	}

	public void setClassId(String classId) {
		this._classId = classId;
	}

	public void setParamId(String paramId) {
		this._paramId = paramId;
	}

}
