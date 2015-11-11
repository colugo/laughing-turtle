package main.java.avii.editor.command.result;

public class FailureEditorCommand extends BaseEditorCommandResult {

	public FailureEditorCommand(String explanation) {
		this._explanation = explanation;
		this._returnStatus = false;
	}
	
	public FailureEditorCommand() {
		this._returnStatus = false;
		this._explanation = "EditorCommand failed ?";
	}
}
