package main.java.avii.editor.metamodel.actionLanguage.validation.error.syntax;

import main.java.avii.editor.contracts.metamodel.actionLanguage.syntax.IActionLanguageSyntax;
import main.java.avii.editor.contracts.metamodel.entities.datatypes.IEntityDatatype;

public class EntityDatatypesCannotBeCompared extends BaseActionLanguageValidationError {

	
	private IActionLanguageSyntax _lineSyntax;
	private int _lineNumber;
	
	private IEntityDatatype _datatype1;
	private IEntityDatatype _datatype2;

	public EntityDatatypesCannotBeCompared(IEntityDatatype datatype1, IEntityDatatype datatype2, IActionLanguageSyntax lineSyntax, int lineNumber) {
		this._lineSyntax = lineSyntax;
		this._lineNumber = lineNumber;
		this._datatype1 = datatype1;
		this._datatype2 = datatype2;
	}
	
	public String explainError()
	{
		return "line : " + _lineNumber + " -> '" + _lineSyntax + "' - Datatype " + _datatype1.getClass().getName() + " cannot be compared to " + _datatype2 ;
	}

}
