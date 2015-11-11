package main.java.avii.editor.command.result;

import main.java.avii.editor.command.IEditorCommandResult;

public class BaseEditorCommandResult implements IEditorCommandResult {

	protected boolean _returnStatus = false;
	protected String _explanation = "EditorCommand executed successfully";

	public BaseEditorCommandResult(String explanation)
	{
		this._explanation = explanation;
	}
	
	public BaseEditorCommandResult()
	{}
	
	public boolean returnStatus() {
		return this._returnStatus;
	}

	public String explainResult() {
		return this._explanation;
	}

	
	public void prependText(String text)
	{
		this._explanation = text + "\n" + this._explanation;
	}
}
