package main.java.avii.editor.contracts.metamodel.actionLanguage.token;

import main.java.avii.editor.contracts.metamodel.entities.datatypes.IEntityDatatype;

public interface IActionLanguageTokenIdentifier {
	public IEntityDatatype getDatatypeForToken(IActionLanguageToken token);
	
}
