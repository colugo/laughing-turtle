package main.java.avii.editor.metamodel.actionLanguage.entityReference;

import main.java.avii.editor.contracts.metamodel.actionLanguage.syntax.IActionLanguageSyntax;
import main.java.avii.editor.metamodel.entities.EntityClass;

public class EntityClassReference extends BaseEntityReference {

	private EntityClass _class;

	public EntityClassReference(int lineNumber, IActionLanguageSyntax syntax, EntityClass theClass) {
		super(lineNumber, syntax);
		this._class = theClass;
	}
	
	public EntityClass getEntityClass()
	{
		return this._class;
	}

}
