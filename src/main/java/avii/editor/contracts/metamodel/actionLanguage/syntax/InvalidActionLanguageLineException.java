package main.java.avii.editor.contracts.metamodel.actionLanguage.syntax;

@SuppressWarnings("serial")
public class InvalidActionLanguageLineException extends Exception {

	String _Line = null;
	public InvalidActionLanguageLineException(String line)
	{
		this._Line = line;
	}
	
	public String toString()
	{
		return "Could not parse action language line = " + this._Line;
	}
	
}
