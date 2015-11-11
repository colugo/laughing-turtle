package main.java.avii.editor.command;

import org.simpleframework.xml.Attribute;
import org.simpleframework.xml.Element;

import main.java.avii.editor.command.result.SuccessfulEditorCommand;
import main.java.avii.editor.metamodel.entities.EntityClass;
import main.java.avii.editor.metamodel.entities.EntityEventParam;
import main.java.avii.editor.metamodel.entities.EntityEventSpecification;
import main.java.avii.editor.metamodel.entities.datatypes.StringEntityDatatype;

@Element
public class EditorCommand_AddEventParam extends BaseEditorCommand {

	@Attribute
	private String _classId;
	@Attribute
	private String _specId;
	@Attribute
	private String _paramId;
	
	public void setClassId(String classId) {
		this._classId = classId;
	}

	public void setSpecId(String specId) {
		this._specId = specId;
	}

	public void setParamId(String paramId) {
		this._paramId = paramId;
	}
	
	@Override
	protected IEditorCommandResult concreteDoCommand() {
		EntityClass klass = this._domain.getEntityClassWithId(this._classId);
		EntityEventSpecification spec = klass.getEventSpecificationWithId(this._specId);
		EntityEventParam param = new EntityEventParam("EventParam_" + this._paramId, StringEntityDatatype.getInstance());
		param.setId(this._paramId);
		spec.addEventParam(param);
		
		return new SuccessfulEditorCommand();
	}

	@Override
	public IEditorCommandResult canDoCommand() {
		if(!this._domain.hasEntityClassWithId(this._classId))
		{
			return failWhenClassNotFound(this._classId);
		}
		EntityClass theClass = this._domain.getEntityClassWithId(this._classId);
		if(!theClass.hasEventSpecificationWithId(this._specId))
		{
			return failWhenSpecNotFound(this._classId, this._specId);
		}
		
		return new SuccessfulEditorCommand();
	}



	
}
