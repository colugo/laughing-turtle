package main.java.avii.editor.command.history;

import java.io.StringWriter;

import org.simpleframework.xml.Serializer;
import org.simpleframework.xml.core.Persister;

public class EditorCommandHistorySerializer {

	public static String serialize(EditorCommandHistory editorCommandHistory) throws Exception
	{
		String output = null;
		Serializer serializer = new Persister();
		StringWriter stringWriter = new StringWriter();
		serializer.write(editorCommandHistory,stringWriter);
		output = stringWriter.toString();
		return output;
	}
	
	public static EditorCommandHistory deSerialize(String historyString) throws Exception
	{
		Serializer serializer = new Persister();
		EditorCommandHistory editorHistoryCommand = serializer.read(EditorCommandHistory.class, historyString);
		return editorHistoryCommand;
	}
	
}
