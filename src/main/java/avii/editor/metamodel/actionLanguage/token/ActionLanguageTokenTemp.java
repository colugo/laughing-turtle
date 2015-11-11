package main.java.avii.editor.metamodel.actionLanguage.token;

import main.java.avii.editor.contracts.metamodel.actionLanguage.token.IActionLanguageToken;


public class ActionLanguageTokenTemp implements IActionLanguageToken {
	private String _tempName;
	
	public ActionLanguageTokenTemp(String theTempName)
	{
		this._tempName = theTempName;
	}
	
	public String getName()
	{
		return _tempName;
	}

	public String AsString() {
		return _tempName;
	}
	
	
}
