package main.java.avii.editor.command;

public interface IEditorCommandResult {
	public boolean returnStatus();
	public String explainResult();
	public void prependText(String text);
}
