package main.java.avii.editor.command;

import org.simpleframework.xml.Attribute;
import org.simpleframework.xml.Element;

import main.java.avii.editor.command.result.SuccessfulEditorCommand;
import main.java.avii.editor.metamodel.entities.EntityRelation;

@Element
public class EditorCommand_RenameRelation extends BaseEditorCommand {

	@Attribute
	private String _relationId;
	@Attribute
	private String _newName;
	

	@Override
	protected IEditorCommandResult concreteDoCommand() {
		EntityRelation relation = this._domain.getRelationWithId(this._relationId);
		relation.setName(this._newName);
		return new SuccessfulEditorCommand();
	}

	@Override
	public IEditorCommandResult canDoCommand() {
		if(!this._domain.hasRelationWithId(this._relationId))
		{
			return failWhenRelationIdNotFound(this._relationId);
		}
		if(this._domain.hasRelationWithName(this._newName))
		{
			return failWhenRelationWithNameExists(this._newName);
		}
		return new SuccessfulEditorCommand();
	}

	public void setRelationId(String relationId) {
		this._relationId = relationId;
	}

	public void setNewName(String newName) {
		this._newName = newName;
	}

}
