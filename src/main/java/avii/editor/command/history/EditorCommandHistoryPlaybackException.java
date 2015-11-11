package main.java.avii.editor.command.history;

import main.java.avii.editor.command.IEditorCommandResult;

@SuppressWarnings("serial")
public class EditorCommandHistoryPlaybackException extends Exception {

	private IEditorCommandResult _result;

	public EditorCommandHistoryPlaybackException(IEditorCommandResult result)
	{
		this._result = result;
	}
	
	@Override
	public String toString()
	{
		return this._result.explainResult();
	}
	
}
