/**
 * @author Alastair Parker
 * Copyright Alastair Parker 2011
 */
package main.java.avii.editor.contracts.metamodel.actionLanguage.syntax.languageConstructs.logic;

public interface ILogicToken {
	public String tokenType = null;
	
	/**
	 * Presents a string to each of the logic constructs, which attempt to match the starting portion of it
	 * @param line string to match
	 * @return a token representing the match
	 */
	public IMatchedToken matchesString(String line, int offset);
	public String toString();
}
