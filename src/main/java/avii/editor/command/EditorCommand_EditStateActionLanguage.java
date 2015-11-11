package main.java.avii.editor.command;

import java.io.IOException;

import org.simpleframework.xml.Attribute;
import org.simpleframework.xml.Element;
import org.simpleframework.xml.Text;

import main.java.avii.editor.command.result.FailureEditorCommand;
import main.java.avii.editor.command.result.SuccessfulEditorCommand;
import main.java.avii.editor.contracts.metamodel.actionLanguage.syntax.InvalidActionLanguageSyntaxException;
import main.java.avii.editor.metamodel.entities.EntityClass;
import main.java.avii.editor.metamodel.entities.EntityState;
import main.java.avii.util.Base64;

@Element
public class EditorCommand_EditStateActionLanguage extends BaseEditorCommand {

	@Attribute
	private String _classId;
	@Attribute
	private String _stateId;
	@Text
	private String _base64ActionLanguage;

	private String actionLanguage()
	{
		try {
			return new String(Base64.decode(this._base64ActionLanguage));
		} catch (IOException e) {
		}
		return "Could not Base64 decode!";
	}
	
	@Override
	protected IEditorCommandResult concreteDoCommand() {
		EntityClass theClass = this._domain.getEntityClassWithId(this._classId);
		EntityState theState = theClass.getStateWithId(this._stateId);
		try {
			theState.setProcedureText(this.actionLanguage());
		} catch (InvalidActionLanguageSyntaxException e) {
			return new FailureEditorCommand(e.getMessage());
		}
		return new SuccessfulEditorCommand();
	}

	@Override
	public IEditorCommandResult canDoCommand() {
		if(!this._domain.hasEntityClassWithId(this._classId))
		{
			return failWhenClassNotFound(this._classId);
		}
		EntityClass theClass = this._domain.getEntityClassWithId(this._classId);
		if(!theClass.hasStateWithId(this._stateId))
		{
			return failWhenStateNotFound(this._classId, this._stateId);
		}
		
		EntityState theState = theClass.getStateWithId(this._stateId);
		try {
			theState.setProcedureText(this.actionLanguage());
		} catch (InvalidActionLanguageSyntaxException e) {
			return new FailureEditorCommand(e.getMessage());
		}
		
		return new SuccessfulEditorCommand();
	}


	public void setStateId(String newStateId) {
		this._stateId = newStateId;		
	}

	public void setClassId(String classId) {
		this._classId = classId;
	}

	public void setActionLanguage(String actionLanguage) {
		this._base64ActionLanguage = Base64.encodeBytes(actionLanguage.getBytes());
	}

}
