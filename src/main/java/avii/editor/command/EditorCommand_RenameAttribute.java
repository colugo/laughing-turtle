package main.java.avii.editor.command;

import org.simpleframework.xml.Attribute;
import org.simpleframework.xml.Element;

import main.java.avii.editor.command.result.FailureEditorCommand;
import main.java.avii.editor.command.result.SuccessfulEditorCommand;
import main.java.avii.editor.metamodel.entities.EntityAttribute;
import main.java.avii.editor.metamodel.entities.EntityClass;

@Element
public class EditorCommand_RenameAttribute extends BaseEditorCommand {

	@Attribute
	private String _newAttributeName;
	@Attribute
	private String _attributeId;
	@Attribute
	private String _classId;

	@Override
	protected IEditorCommandResult concreteDoCommand() {
		try {
			EntityClass theClass = this._domain.getEntityClassWithId(this._classId);
			EntityAttribute theAttribute = theClass.getAttributeWithId(this._attributeId);
			theAttribute.rename(this._newAttributeName);
			return new SuccessfulEditorCommand();
		} catch (Exception e) {
			return new FailureEditorCommand("Error renaming Attribute : " + e.getMessage());
		}
	}

	@Override
	public IEditorCommandResult canDoCommand() {
		if (this._domain.hasEntityClassWithId(this._classId)) {
			EntityClass theClass = this._domain.getEntityClassWithId(this._classId);
			if (theClass.hasAttributeWithID(this._attributeId)) {
				if (theClass.hasAttribute(this._newAttributeName)) {
					return new FailureEditorCommand("Attribute '" + this._newAttributeName + "' already exists in class '" + this._classId + "'.");
				} else {
					return new SuccessfulEditorCommand();
				}
			} else {
				return failWhenAttributeNotOnClass(this._newAttributeName, this._classId);
			}
		} else {
			return failWhenClassNotFound(this._classId);
		}
	}

	public void setClassId(String theClassName) {
		this._classId = theClassName;
	}

	public void setAttributeIdToRename(String attributeId) {
		this._attributeId = attributeId;
	}

	public void setNewAttributeName(String newAttributeName) {
		this._newAttributeName = newAttributeName;
	}

}
