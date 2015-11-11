package main.java.avii.editor.command;

import org.simpleframework.xml.Attribute;
import org.simpleframework.xml.Element;

import main.java.avii.editor.command.result.SuccessfulEditorCommand;

@Element
public class EditorCommand_DeleteRelation extends BaseEditorCommand {

	@Attribute
	private String _relationId;
	

	@Override
	protected IEditorCommandResult concreteDoCommand() {
		this._domain.deleteRelationWithId(this._relationId);
		return new SuccessfulEditorCommand();
	}

	@Override
	public IEditorCommandResult canDoCommand() {
		if(!this._domain.hasRelationWithId(this._relationId))
		{
			return failWhenRelationIdNotFound(this._relationId);
		}
		return new SuccessfulEditorCommand();
	}

	public void setRelationId(String relationId) {
		this._relationId = relationId;
	}

}
