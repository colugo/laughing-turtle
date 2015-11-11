package main.java.avii.editor.contracts.metamodel.actionLanguage.syntax;

public class InvalidActionLanguageSyntaxException extends Exception{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String _text;
	private int _lineNumber;
	private int _fileOffset;
	
	public InvalidActionLanguageSyntaxException(String text, int lineNumber, int fileOffset)
	{
		this._text = text;
		this._fileOffset = fileOffset;
		this._lineNumber = lineNumber;
	}

	public String get_text() {
		return _text;
	}

	public int get_lineNumber() {
		return _lineNumber;
	}

	public int get_fileOffset() {
		return _fileOffset;
	}
	
	@Override
	public String getMessage()
	{
	
		return "Encounted non-syntax on line " + _lineNumber+ " : '"+ _text +"'. File offset = " + _fileOffset;
	}
}
