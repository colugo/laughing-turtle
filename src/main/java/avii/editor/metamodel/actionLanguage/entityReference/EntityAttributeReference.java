package main.java.avii.editor.metamodel.actionLanguage.entityReference;

import main.java.avii.editor.contracts.metamodel.actionLanguage.syntax.IActionLanguageSyntax;
import main.java.avii.editor.metamodel.entities.EntityAttribute;

public class EntityAttributeReference extends BaseEntityReference {

	private EntityAttribute _attribute;

	public EntityAttributeReference(int lineNumber, IActionLanguageSyntax syntax, EntityAttribute theAttribute) {
		super(lineNumber, syntax);
		this._attribute = theAttribute;
	}
	
	public EntityAttribute getAttribute()
	{
		return this._attribute;
	}

}
