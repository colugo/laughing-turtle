package main.java.avii.editor.command.helper;

import java.util.Collection;

import main.java.avii.editor.command.BaseEditorCommand;
import main.java.avii.editor.command.IEditorCommandResult;
import main.java.avii.editor.command.result.SuccessfulEditorCommand;
import main.java.avii.editor.metamodel.entities.EntityClass;
import main.java.avii.editor.metamodel.entities.EntityDomain;

public class GetStateNamesForClassHelper {

	private Collection<String> _stateNames;

	public IEditorCommandResult doCommand(EntityDomain domain, String classId) {
		if(!domain.hasEntityClassWithId(classId))
		{
			return BaseEditorCommand.failWhenClassNotFound(classId);
		}
		EntityClass theClass = domain.getEntityClassWithId(classId);
		this._stateNames = theClass.getStateNames();
		
		return new SuccessfulEditorCommand();
	}

	public Collection<String> getStateNames() {
		return this._stateNames ;
	}

}
