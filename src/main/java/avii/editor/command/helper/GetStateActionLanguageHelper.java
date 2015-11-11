package main.java.avii.editor.command.helper;

import main.java.avii.editor.command.BaseEditorCommand;
import main.java.avii.editor.command.IEditorCommandResult;
import main.java.avii.editor.command.result.SuccessfulEditorCommand;
import main.java.avii.editor.metamodel.entities.EntityClass;
import main.java.avii.editor.metamodel.entities.EntityDomain;
import main.java.avii.editor.metamodel.entities.EntityState;

public class GetStateActionLanguageHelper{

	private String _actionLanguage;

	public IEditorCommandResult doCommand(EntityDomain domain, String classId, String stateId ) {
		if(!domain.hasEntityClassWithId(classId))
		{
			return BaseEditorCommand.failWhenClassNotFound(classId);
		}
		EntityClass theClass = domain.getEntityClassWithId(classId);
		if(!theClass.hasStateWithId(stateId))
		{
			return BaseEditorCommand.failWhenStateNotFound(classId, stateId);
		}
		EntityState theState = theClass.getStateWithId(stateId);
		this._actionLanguage = theState.getRawText();
		
		return new SuccessfulEditorCommand();
	}

	public String getActionLanguage() {
		return this._actionLanguage;
	}
	
}
