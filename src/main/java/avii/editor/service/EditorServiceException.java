package main.java.avii.editor.service;

@SuppressWarnings("serial")
public class EditorServiceException extends Exception {

	private String _message;
	
	public EditorServiceException(String message)
	{
		super(message);
		this._message = message;
	}
	
	public String toString()
	{
		return this._message;
	}
	
}
