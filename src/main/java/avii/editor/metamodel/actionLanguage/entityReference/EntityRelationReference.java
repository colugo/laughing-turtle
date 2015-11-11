package main.java.avii.editor.metamodel.actionLanguage.entityReference;

import main.java.avii.editor.contracts.metamodel.actionLanguage.syntax.IActionLanguageSyntax;
import main.java.avii.editor.metamodel.entities.EntityRelation;

public class EntityRelationReference extends BaseEntityReference {

	private EntityRelation _relation;

	public EntityRelationReference(int lineNumber, IActionLanguageSyntax syntax, EntityRelation relation) {
		super(lineNumber, syntax);
		this._relation = relation;
	}
	
	public EntityRelation getRelation()
	{
		return this._relation;
	}

}
