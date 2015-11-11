package main.java.avii.editor.metamodel.actionLanguage.reader;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import main.java.avii.editor.contracts.metamodel.actionLanguage.syntax.IActionLanguageSyntax;
import main.java.avii.editor.contracts.metamodel.actionLanguage.syntax.InvalidActionLanguageLineException;
import main.java.avii.editor.contracts.metamodel.actionLanguage.syntax.InvalidActionLanguageSyntaxException;
import main.java.avii.editor.metamodel.actionLanguage.ActionLanguageSupportedSyntax;

public class ActionLanguageReader {

	private InputStream inputStream;
	
	public ActionLanguageReader(InputStream in)
	{
		this.inputStream = in;
	}
	
	public void read() throws InvalidActionLanguageSyntaxException {
		int lineNumber = 0;
		int offsetAtBeginnigOfCurrentLine = 0;
		BufferedReader br = new BufferedReader(new InputStreamReader(this.inputStream));
		String line = "setup";
		while (line != null) {
			try {
				line = br.readLine();
				if (line != null) {
					lineNumber++;
					IActionLanguageSyntax lineSyntax = null;
					
					try {
						lineSyntax= ActionLanguageSupportedSyntax.getSyntaxForLine(line);
					} catch (InvalidActionLanguageLineException e) {
						lineSyntax = null;
					}
					
					if (lineSyntax == null) {
						if(line.length() > 0)
						{
							foundNonSyntaxLine(line, lineNumber, offsetAtBeginnigOfCurrentLine);
						}
					} else {
						foundSyntaxLine(lineSyntax, lineNumber, offsetAtBeginnigOfCurrentLine,line);
					}
					offsetAtBeginnigOfCurrentLine += line.length();
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		readingComplete();
	}
	
	protected void readingComplete()
	{
		
	}

	protected void foundNonSyntaxLine(String line, int lineNumber, int fileOffset) throws InvalidActionLanguageSyntaxException {
		throw new InvalidActionLanguageSyntaxException(line, lineNumber, fileOffset);
	}

	protected void foundSyntaxLine(IActionLanguageSyntax lineSyntax, int lineNumber, int fileOffset, String line) {
	}

}
