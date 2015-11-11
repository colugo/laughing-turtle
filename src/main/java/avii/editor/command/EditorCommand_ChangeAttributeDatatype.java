package main.java.avii.editor.command;

import org.simpleframework.xml.Attribute;
import org.simpleframework.xml.Element;

import main.java.avii.editor.command.result.FailureEditorCommand;
import main.java.avii.editor.command.result.SuccessfulEditorCommand;
import main.java.avii.editor.contracts.metamodel.entities.datatypes.IEntityDatatype;
import main.java.avii.editor.metamodel.entities.EntityAttribute;
import main.java.avii.editor.metamodel.entities.EntityClass;
import main.java.avii.editor.metamodel.entities.datatypes.AvailableDatatypes;

@Element
public class EditorCommand_ChangeAttributeDatatype extends BaseEditorCommand {

	@Attribute
	private String _attributeId;
	@Attribute
	private String _classId;
	private IEntityDatatype _datatype;
	@Attribute
	private String _EntityDatatypeClassName;

	@Override
	protected IEditorCommandResult concreteDoCommand() {
		if(this._datatype == null)
		{
			this.getDatatype();
		}
		try {
			EntityClass theClass = this._domain.getEntityClassWithId(this._classId);
			EntityAttribute theAttribute = theClass.getAttributeWithId(this._attributeId);
			theAttribute.setType(this._datatype);
			return new SuccessfulEditorCommand();
		} catch (Exception e) {
			return new FailureEditorCommand("Error changing attribute datatype : " + e.getMessage());
		}
	}

	private void getDatatype()
	{
		this._datatype = AvailableDatatypes.getInstanceBasedOnClassName(this._EntityDatatypeClassName);
	}

	@Override
	public IEditorCommandResult canDoCommand() {
		if (this._domain.hasEntityClassWithId(this._classId)) {
			EntityClass theClass = this._domain.getEntityClassWithId(this._classId);
			if (theClass.hasAttributeWithID(this._attributeId)) {
				return new SuccessfulEditorCommand();
			} else {
				return failWhenAttributeNotOnClass(this._attributeId, this._classId);
			}
		} else {
			return failWhenClassNotFound(this._classId);
		}
	}

	public void setClassId(String theClassId) {
		this._classId = theClassId;
	}

	public void setDatatype(IEntityDatatype attributeDatatype) {
		this._datatype = attributeDatatype;
		this._EntityDatatypeClassName = _datatype.getClass().getName();
	}

	public void setAttributeId(String attributeId) {
		this._attributeId = attributeId;
	}

}
