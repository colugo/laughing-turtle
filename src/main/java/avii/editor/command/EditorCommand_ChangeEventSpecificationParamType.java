package main.java.avii.editor.command;

import org.simpleframework.xml.Attribute;
import org.simpleframework.xml.Element;

import main.java.avii.editor.command.result.SuccessfulEditorCommand;
import main.java.avii.editor.contracts.metamodel.entities.datatypes.IEntityDatatype;
import main.java.avii.editor.metamodel.entities.EntityClass;
import main.java.avii.editor.metamodel.entities.EntityEventParam;
import main.java.avii.editor.metamodel.entities.EntityEventSpecification;
import main.java.avii.editor.metamodel.entities.datatypes.AvailableDatatypes;

@Element
public class EditorCommand_ChangeEventSpecificationParamType extends BaseEditorCommand {

	@Attribute
	private String _classId;
	@Attribute
	private String _specId;
	@Attribute
	private String _paramId;
	private IEntityDatatype _datatype;
	@Attribute
	private String _EntityDatatypeClassName;

	@Override
	protected IEditorCommandResult concreteDoCommand() {
		if(this._datatype == null)
		{
			this.getDatatype();
		}

		EntityClass theClass = this._domain.getEntityClassWithId(this._classId);
		EntityEventSpecification theSpec = theClass.getEventSpecificationWithId(this._specId);
		EntityEventParam theParam = theSpec.getParamWithId(this._paramId);
		theParam.setType(this._datatype);
		return new SuccessfulEditorCommand();

	}

	private void getDatatype()
	{
		this._datatype = AvailableDatatypes.getInstanceBasedOnClassName(this._EntityDatatypeClassName);
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
	
	public void setDatatype(IEntityDatatype attributeDatatype) {
		this._datatype = attributeDatatype;
		this._EntityDatatypeClassName = _datatype.getClass().getName();
	}

}
