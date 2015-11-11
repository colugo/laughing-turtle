package main.java.avii.editor.command;

import org.simpleframework.xml.Attribute;

import main.java.avii.editor.command.result.SuccessfulEditorCommand;
import main.java.avii.editor.metamodel.entities.EntityDomain;

public class EditorCommand_CreateDomain implements IEditorCommand {

	private String _domainName;
	private EntityDomain _domain;
	@Attribute


	public IEditorCommandResult doCommand() {
		_domain = new EntityDomain(this._domainName);
		return new SuccessfulEditorCommand();
	}
	
	public void setDomainName(String newDomainName) {
		this._domainName = newDomainName;
	}

	public EntityDomain getDomain() {
		return this._domain;
	}

}
