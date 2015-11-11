package main.java.avii.editor.metamodel.actionLanguage.token;

import main.java.avii.editor.contracts.metamodel.actionLanguage.token.IActionLanguageToken;


public class ActionLanguageTokenRcvdEvent implements IActionLanguageToken {
	private String _paramName;
	
	public ActionLanguageTokenRcvdEvent(String theParamName)
	{
		this._paramName = theParamName;
	}
	
	public String getParamName()
	{
		return _paramName;
	}
	
	public String AsString() {
		return "rcvd_event." + _paramName;
	}
}
