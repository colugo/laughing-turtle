/**
 * @author Alastair Parker
 * Copyright Alastair Parker 2011
 */
package main.java.avii.editor.metamodel.actionLanguage.syntax.languageConstructs.logic;

import main.java.avii.editor.contracts.metamodel.actionLanguage.syntax.languageConstructs.logic.ILogicToken;
import main.java.avii.editor.contracts.metamodel.actionLanguage.syntax.languageConstructs.logic.IMatchedToken;

public class MatchedToken implements IMatchedToken {

	public boolean matchFound;
	public ILogicToken matchedTokenType;
	public String match;
	
	public boolean isMatchFound() {
		return matchFound;
	}


	public ILogicToken getMatchedTokenType() {
		return matchedTokenType;
	}


	public String getMatch() {
		return match;
	}


	
	public static IMatchedToken NoMatch()
	{
		MatchedToken noMatch = new MatchedToken(null,null);
		noMatch.matchFound = false;
		return noMatch;
	}
	
	
	public MatchedToken(ILogicToken type, String match)
	{
		this.matchFound = true;
		this.matchedTokenType = type;
		this.match = match;
	}
	
}
