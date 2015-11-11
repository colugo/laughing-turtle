package main.java.avii.editor.contracts.metamodel.actionLanguage.syntax.languageConstructs.logic;

import main.java.avii.editor.contracts.metamodel.actionLanguage.syntax.languageConstructs.logic.ILogicToken;

public interface IMatchedToken {

	public boolean isMatchFound();

	public ILogicToken getMatchedTokenType();

	public String getMatch();

}