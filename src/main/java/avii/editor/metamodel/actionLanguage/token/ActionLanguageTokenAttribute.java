package main.java.avii.editor.metamodel.actionLanguage.token;

import main.java.avii.editor.contracts.metamodel.actionLanguage.token.IActionLanguageToken;


public class ActionLanguageTokenAttribute implements IActionLanguageToken {
	private String _instanceName;
	private String _attributeName;
	
	public ActionLanguageTokenAttribute(String theInstanceName, String theAttributeName)
	{
		this._instanceName = theInstanceName;
		this._attributeName = theAttributeName;
	}
	
	public String getInstanceName()
	{
		return _instanceName;
	}
	
	public String getAttributeName()
	{
		return _attributeName;
	}

	public String AsString() {
		return _instanceName + "." + _attributeName;
	}
}
