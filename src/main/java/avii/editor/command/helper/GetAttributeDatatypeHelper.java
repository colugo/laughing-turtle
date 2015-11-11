package main.java.avii.editor.command.helper;

import javax.naming.NameNotFoundException;

import main.java.avii.editor.command.BaseEditorCommand;
import main.java.avii.editor.command.IEditorCommandResult;
import main.java.avii.editor.command.result.SuccessfulEditorCommand;
import main.java.avii.editor.metamodel.entities.EntityAttribute;
import main.java.avii.editor.metamodel.entities.EntityClass;
import main.java.avii.editor.metamodel.entities.EntityDomain;

public class GetAttributeDatatypeHelper {

	private String _attributeType;

	public String getAttributeName() {
		return this._attributeType;
	}

	public IEditorCommandResult doCommand(EntityDomain domain, String classId, String attributeId) {
		EntityClass theClass = null;

		if(!domain.hasEntityClassWithId(classId))
		{
			BaseEditorCommand.failWhenClassNotFound(classId);
		}
		theClass = domain.getEntityClassWithId(classId);

		EntityAttribute theAttribute = null;
		try {
			theAttribute = theClass.getAttributeWithId(attributeId);
		} catch (NameNotFoundException e) {
			return BaseEditorCommand.failWhenAttributeNotOnClass(attributeId, classId);
		}
		this._attributeType = theAttribute.getType().getHumanName();
		
		return new SuccessfulEditorCommand();
	}

}
