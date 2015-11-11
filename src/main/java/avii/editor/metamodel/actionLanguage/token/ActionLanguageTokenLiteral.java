package main.java.avii.editor.metamodel.actionLanguage.token;

import main.java.avii.editor.contracts.metamodel.actionLanguage.token.IActionLanguageToken;
import main.java.avii.editor.contracts.metamodel.entities.datatypes.IEntityDatatype;

public class ActionLanguageTokenLiteral implements IActionLanguageToken {
	private IEntityDatatype _datatype;
	private String _value;
	
	public ActionLanguageTokenLiteral(IEntityDatatype datatype, String value)
	{
		this._datatype = datatype;
		this._value = value;
	}
	
	public IEntityDatatype getDatatype()
	{
		return _datatype;
	}
	
	public String getValue()
	{
		return _value;
	}

	public String AsString() {
		return _value;
	}
}
