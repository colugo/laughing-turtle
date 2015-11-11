/**
 * @author Alastair Parker
 * Copyright Alastair Parker 2011
 */
package main.java.avii.editor.metamodel.actionLanguage.syntax;

import main.java.avii.editor.contracts.metamodel.actionLanguage.syntax.IActionLanguageSyntax;
import main.java.avii.editor.contracts.metamodel.actionLanguage.syntax.IDescriptiveActionLanguage;
import main.java.avii.editor.metamodel.actionLanguage.validation.IActionLanguageVisitor;
import main.java.avii.editor.metamodel.actionLanguage.validation.IVisitableActionLanguage;

public class Syntax_Comment implements IActionLanguageSyntax, IVisitableActionLanguage, IDescriptiveActionLanguage {

	private String comment;
	private static final String regex = "#(.*)";

	public IActionLanguageSyntax populateSyntax(String line) {
		comment = line;
		return this;
	}

	@Override
	public String toString() {
		return this.comment;
	}

	public String getRegex() {
		return regex;
	}

	public boolean matchesLine(String line) {
		return line.matches(regex);
	}

	public void accept(IActionLanguageVisitor visitor, int lineNumber) {
		// this method has been intentionally left blank
	}
}
