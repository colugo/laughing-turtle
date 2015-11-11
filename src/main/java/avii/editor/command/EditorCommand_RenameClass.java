package main.java.avii.editor.command;

import javax.naming.NamingException;

import org.simpleframework.xml.Attribute;
import org.simpleframework.xml.Element;

import main.java.avii.editor.command.result.FailureEditorCommand;
import main.java.avii.editor.command.result.SuccessfulEditorCommand;
import main.java.avii.editor.contracts.metamodel.entities.CannotRefactorInvalidDomainException;

@Element
public class EditorCommand_RenameClass extends BaseEditorCommand {

	@Attribute
	private String _classId;
	@Attribute
	private String _newClassName;
	
	@Override
	protected IEditorCommandResult concreteDoCommand() {
		try {
			this._domain.renameClass(this._classId, this._newClassName);
		} catch (NamingException e) {
			return new FailureEditorCommand(e.getMessage());
		} catch (CannotRefactorInvalidDomainException e) {
			return new FailureEditorCommand(e.getMessage());
		}
		
		return new SuccessfulEditorCommand();
	}


	@Override
	public IEditorCommandResult canDoCommand() {
		boolean domainHasClassWithId = this._domain.hasEntityClassWithId(this._classId);
		boolean domainHasClassWithNewName = this._domain.hasEntityClassWithName(this._newClassName);
		
		if( domainHasClassWithId && !domainHasClassWithNewName)
		{
			return new SuccessfulEditorCommand();
		}
		if(domainHasClassWithNewName)
		{
			return failWhenClassExists(this._newClassName);
		}
		return failWhenClassExists(this._classId);	
	}

	public void setClassId(String classId) {
		this._classId = classId;		
	}

	public void setNewClassName(String theNewClassName) {
		this._newClassName = theNewClassName;
		
	}

}
