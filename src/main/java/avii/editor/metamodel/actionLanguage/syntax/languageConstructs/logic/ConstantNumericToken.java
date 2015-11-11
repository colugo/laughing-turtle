/**
 * @author Alastair Parker
 * Copyright Alastair Parker 2011
 */
package main.java.avii.editor.metamodel.actionLanguage.syntax.languageConstructs.logic;


public class ConstantNumericToken extends ConstantToken {
	String regex = "^(-)?([0-9]+)+(\\.([0-9]+))?";

	public String getRegex()
	{
		return this.regex;
	}
	
}
