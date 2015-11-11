package main.java.avii.editor.command.history;

import java.util.Comparator;

import main.java.avii.editor.command.BaseEditorCommand;

public class BaseEditorCommandComparitor implements Comparator<BaseEditorCommand> {

	public int compare(BaseEditorCommand command1, BaseEditorCommand command2) {
		return command1.getCreatedTime().compareTo(command2.getCreatedTime());
	}

}
