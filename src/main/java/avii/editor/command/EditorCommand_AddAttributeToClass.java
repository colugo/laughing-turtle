package main.java.avii.editor.command;

import javax.naming.NameAlreadyBoundException;

import org.simpleframework.xml.Attribute;
import org.simpleframework.xml.Element;

import main.java.avii.editor.command.result.FailureEditorCommand;
import main.java.avii.editor.command.result.SuccessfulEditorCommand;
import main.java.avii.editor.contracts.metamodel.entities.datatypes.IEntityDatatype;
import main.java.avii.editor.metamodel.entities.EntityAttribute;
import main.java.avii.editor.metamodel.entities.EntityClass;
import main.java.avii.editor.metamodel.entities.datatypes.StringEntityDatatype;

@Element
public class EditorCommand_AddAttributeToClass extends BaseEditorCommand {

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
			klass.addAttribute(this._attribute);
		} catch (NameAlreadyBoundException e) {
			return new FailureEditorCommand(e.getMessage());
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
				return failWhenAttributeAlreadyExists(this._attribute.getName(), this._classId);
			}
		}
		else
		{
			return failWhenClassNotFound(this._classId);
		}
		return new SuccessfulEditorCommand();
	}

	
}
