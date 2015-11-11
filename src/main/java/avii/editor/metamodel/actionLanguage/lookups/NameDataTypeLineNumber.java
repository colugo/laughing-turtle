package main.java.avii.editor.metamodel.actionLanguage.lookups;

import main.java.avii.editor.contracts.metamodel.entities.datatypes.IEntityDatatype;

public class NameDataTypeLineNumber {
	private String _name;
	private IEntityDatatype _dataType;
	private int _lineNumber;
	
	public NameDataTypeLineNumber(String name, IEntityDatatype datatype, int line)
	{
		this._name = name;
		this._dataType = datatype;
		this._lineNumber = line;
	}
	
	public String getName() {
		return _name;
	}
	public IEntityDatatype getDataType() {
		return _dataType;
	}
	public int getLineNumber() {
		return _lineNumber;
	}

}
