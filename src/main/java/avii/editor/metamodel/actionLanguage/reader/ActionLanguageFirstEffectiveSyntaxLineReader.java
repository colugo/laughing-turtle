package main.java.avii.editor.metamodel.actionLanguage.reader;

import java.io.InputStream;

import main.java.avii.editor.contracts.metamodel.actionLanguage.syntax.IActionLanguageSyntax;
import main.java.avii.editor.contracts.metamodel.actionLanguage.syntax.IDescriptiveActionLanguage;

public class ActionLanguageFirstEffectiveSyntaxLineReader extends ActionLanguageReader {

	IActionLanguageSyntax _firstSyntax = null;
	
	public ActionLanguageFirstEffectiveSyntaxLineReader(InputStream in) {
		super(in);
	}
	
	@Override
	public void foundSyntaxLine(IActionLanguageSyntax lineSyntax, int lineNumber, int fileOffset, String line) {
		if(_firstSyntax == null)
		{
			if(!(lineSyntax instanceof IDescriptiveActionLanguage))
			{
				_firstSyntax = lineSyntax;
			}
		}
	}

	public IActionLanguageSyntax getFirstEffectiveSyntax() {
		return this._firstSyntax;
	}

}
