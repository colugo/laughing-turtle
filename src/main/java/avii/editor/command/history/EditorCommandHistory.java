package main.java.avii.editor.command.history;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;

import org.simpleframework.xml.Attribute;
import org.simpleframework.xml.Element;
import org.simpleframework.xml.ElementList;
import org.simpleframework.xml.Root;

import main.java.avii.editor.command.BaseEditorCommand;
import main.java.avii.editor.command.EditorCommand_CreateDomain;
import main.java.avii.editor.command.IEditorCommandResult;
import main.java.avii.editor.metamodel.entities.EntityDomain;
import main.java.avii.editor.service.IUUIDIdentifier;

@Root
public class EditorCommandHistory {

	@ElementList
	private ArrayList<BaseEditorCommand> _commands = new ArrayList<BaseEditorCommand>();
	private EditorCommand_CreateDomain _createDomainCommand = new EditorCommand_CreateDomain();
	private EntityDomain _domain = null;
	@Attribute
	private String _domainName = "";
	@Element
	private IUUIDIdentifier _uuid;
	@Attribute
	private Date _createdTimestamp = new Date();
	
	public void setCreatedTime(Date timestamp)
	{
		this._createdTimestamp   =  timestamp;
	}
	
	public Date getCreatedTime()
	{
		return this._createdTimestamp;
	}

	public EditorCommandHistory(@Attribute(name="_domainName") String domainName) {
		this._domainName = domainName;
		_createDomainCommand.setDomainName(this._domainName);
	}

	private Collection<BaseEditorCommand> getDateSortedCommands()
	{
		ArrayList<BaseEditorCommand> commands = new ArrayList<BaseEditorCommand>();
		for(BaseEditorCommand command : this._commands)
		{
			commands.add(command);
		}
		
		Collections.sort(commands, new BaseEditorCommandComparitor());
		
		return commands;
	}
	
	public void playAll() throws EditorCommandHistoryPlaybackException {
		playCreateDomainCommand();

		IEditorCommandResult result = null;
		for(BaseEditorCommand command : this.getDateSortedCommands())
		{
			command.setDomain(this._domain);
			result = command.doCommand();
			if(result.returnStatus() == false)
			{
				throw new EditorCommandHistoryPlaybackException(result);
			}
		}
	}

	private void playCreateDomainCommand() {
		_createDomainCommand.doCommand();
		this._domain = this._createDomainCommand.getDomain();
	}

	public EntityDomain getDomain() {
		if(this._domain == null )
		{
			this.playCreateDomainCommand();
		}
		return this._domain;
	}

	public IEditorCommandResult performCommand(BaseEditorCommand command) {
		command.setDomain(this.getDomain());
		IEditorCommandResult canDoCommandResult = command.canDoCommand();
		if(canDoCommandResult.returnStatus() == true)
		{
			command.doCommand();
			insertCommand(command);
		}
		return canDoCommandResult;
	}

	private void insertCommand(BaseEditorCommand command) {
		
		this._commands.add(command);
	}

	public IUUIDIdentifier getUUID() {
		return this._uuid;
	}
	
	public void setUUID(IUUIDIdentifier uuid)
	{
		this._uuid = uuid;
	}
}
