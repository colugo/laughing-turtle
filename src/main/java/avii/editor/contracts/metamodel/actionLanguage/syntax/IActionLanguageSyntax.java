/**
 * @author Alastair Parker
 * Copyright Alastair Parker 2011
 */

package main.java.avii.editor.contracts.metamodel.actionLanguage.syntax;

public interface IActionLanguageSyntax {
	
	public static final String HELPER_NO_LEADING_DIGITS = "[a-zA-Z]{1}([a-zA-Z0-9_])+";
	public static final String HELPER_BASIC_EVENT = "(\\S+)\\((.*)\\)";
	
	public boolean matchesLine(String line);
	public IActionLanguageSyntax populateSyntax(String line);
	public String getRegex();
}
