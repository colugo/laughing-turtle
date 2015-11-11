/**
 * @author Alastair Parker
 * Copyright Alastair Parker 2011
 */
package main.java.avii.editor.metamodel.actionLanguage.syntax.languageConstructs.logic;


public class ConstantStringToken extends ConstantToken {
	String regex = "^((?!NOT)(?!OR)(?!AND)([a-zA-Z])+([\"a-zA-Z0-9_])*(\\.([a-zA-Z])+([\"a-zA-Z0-9_])*)?)";

	public String getRegex()
	{
		return this.regex;
	}
	
}
