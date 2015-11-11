package main.java.avii.editor.contracts.metamodel.entities.helpers.datatypes;

@SuppressWarnings("serial")
public class CouldNotDetermineDatatypeFromValueException extends Exception {
	private String _value;
	public CouldNotDetermineDatatypeFromValueException(String value)
	{
		_value = value;
	}
	public String getValue()
	{
		return _value;
	}
}
