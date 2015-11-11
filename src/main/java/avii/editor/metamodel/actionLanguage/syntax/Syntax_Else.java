/**
 * @author Alastair Parker
 * Copyright Alastair Parker 2011
 */
package main.java.avii.editor.metamodel.actionLanguage.syntax;

import main.java.avii.editor.contracts.metamodel.actionLanguage.syntax.IActionLanguageSyntax;
import main.java.avii.editor.contracts.metamodel.actionLanguage.syntax.ICloseIfSyntax;
import main.java.avii.editor.contracts.metamodel.actionLanguage.syntax.IOpenIfSyntax;
import main.java.avii.editor.metamodel.actionLanguage.validation.IActionLanguageVisitor;
import main.java.avii.editor.metamodel.actionLanguage.validation.IVisitableActionLanguage;

public class Syntax_Else implements IActionLanguageSyntax, IVisitableActionLanguage, IOpenIfSyntax, ICloseIfSyntax {

	private static final String regex = "ELSE$";

	public IActionLanguageSyntax populateSyntax(String line) {
		return this;
	}

	public String getRegex() {
		return regex;
	}

	@Override
	public String toString() {
		return "ELSE";
	}

	public boolean matchesLine(String line) {
		return line.matches(regex);
	}

	public void accept(IActionLanguageVisitor visitor, int lineNumber) {
		visitor.visit(this, lineNumber);
	}

}
