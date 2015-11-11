package main.java.avii.editor.command;

import javax.naming.NameNotFoundException;

import org.simpleframework.xml.Attribute;
import org.simpleframework.xml.Element;

import main.java.avii.editor.command.result.SuccessfulEditorCommand;
import main.java.avii.editor.contracts.metamodel.entities.datatypes.IEntityDatatype;
import main.java.avii.editor.metamodel.entities.EntityAttribute;
import main.java.avii.editor.metamodel.entities.EntityClass;
import main.java.avii.editor.metamodel.entities.datatypes.StringEntityDatatype;

@Element
public class EditorCommand_DeleteAttributeFromClass extends BaseEditorCommand {

	@Attribute
	private String _classId;
	@Attribute
	private String _attributeUUID;
	private IEntityDatatype _datatype = new StringEntityDatatype();
	private EntityAttribute _attribute;

	
	public void setClassId(String classId) {
		this._classId = classId;
	}

	public void setAttributeUUID(String attributeUUID) {
		this._attributeUUID = attributeUUID;
	}
	
	public EntityAttribute getNewAttribute() {
		return this._attribute;
	}
	
	
	@Override
	protected IEditorCommandResult concreteDoCommand() {
		this._attribute = new EntityAttribute("Attribute_" + this._attributeUUID, this._datatype);
		this._attribute.setId(this._attributeUUID);
		
		EntityClass klass = null;
		if(!this._domain.hasEntityClassWithId(this._classId))
		{
			return failWhenClassNotFound(this._classId);
		}
		
		klass = this._domain.getEntityClassWithId(this._classId);
		
		try {
			klass.deleteAttribute(this._attribute);
		} catch (NameNotFoundException e) {
			return failWhenAttributeNotOnClass(this._attribute.getName(), this._classId);
		}
		return new SuccessfulEditorCommand();
	}

	@Override
	public IEditorCommandResult canDoCommand() {
		if(this._domain.hasEntityClassWithId(this._classId))
		{
			EntityClass theClass = this._domain.getEntityClassWithId(this._classId);
			if(theClass.hasAttributeWithID(this._attributeUUID))
			{
				return new SuccessfulEditorCommand();
			}
		}
		else
		{
			return failWhenClassNotFound(this._classId);
		}
		return failWhenAttributeAlreadyExists(this._attribute.getName(), this._classId);
	}

	
}
