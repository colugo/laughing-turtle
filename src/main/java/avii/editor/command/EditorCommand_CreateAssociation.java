package main.java.avii.editor.command;

import org.simpleframework.xml.Attribute;
import org.simpleframework.xml.Element;

import main.java.avii.editor.command.result.SuccessfulEditorCommand;
import main.java.avii.editor.metamodel.entities.EntityClass;
import main.java.avii.editor.metamodel.entities.EntityRelation;

@Element
public class EditorCommand_CreateAssociation extends BaseEditorCommand {

	@Attribute
	private String _relationId;
	@Attribute(required=false)
	private String _associationClassId;

	@Override
	protected IEditorCommandResult concreteDoCommand() {
		EntityRelation relation = this._domain.getRelationWithId(this._relationId);
		if(this._associationClassId == null){
			relation.clearAssociation();			
		}
		else{
			EntityClass association = this._domain.getEntityClassWithId(this._associationClassId);
			relation.setAssociation(association);
		}
		return new SuccessfulEditorCommand();
	}

	@Override
	public IEditorCommandResult canDoCommand() {
		if(!this._domain.hasRelationWithId(this._relationId))
		{
			return failWhenRelationIdNotFound(this._relationId);
		}
		if(this._associationClassId != null && !this._domain.hasEntityClassWithId(this._associationClassId))
		{
			return failWhenClassNotFound(this._associationClassId);
		}
		return new SuccessfulEditorCommand();
	}

	public void setRelationId(String relationId) {
		this._relationId = relationId;
	}
	
	public void setAssociationClassId(String associationClassId) {
		this._associationClassId = associationClassId;
	}

}
