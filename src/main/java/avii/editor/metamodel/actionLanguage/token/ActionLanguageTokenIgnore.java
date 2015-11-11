package main.java.avii.editor.metamodel.actionLanguage.token;

import main.java.avii.editor.contracts.metamodel.actionLanguage.token.IActionLanguageToken;

public class ActionLanguageTokenIgnore implements IActionLanguageToken {
	private String _value;
	
	public ActionLanguageTokenIgnore(String value)
	{
		this._value = value;
	}
	
	public String getValue()
	{
		return _value;
	}

	public String AsString() {
		return _value;
	}
}
