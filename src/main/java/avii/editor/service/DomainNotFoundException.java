package main.java.avii.editor.service;

@SuppressWarnings("serial")
public class DomainNotFoundException extends Exception {
	private IUUIDIdentifier _uuid;
	
	public DomainNotFoundException(IUUIDIdentifier uuid)
	{
		this._uuid = uuid;
	}
	
	public String toString()
	{
		String message = "No saved domain with identifier found : " + this._uuid.getUUIDString();
		return message;
	}
}
