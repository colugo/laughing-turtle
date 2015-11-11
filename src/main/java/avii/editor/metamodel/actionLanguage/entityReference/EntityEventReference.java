package main.java.avii.editor.metamodel.actionLanguage.entityReference;

import main.java.avii.editor.contracts.metamodel.actionLanguage.syntax.IActionLanguageSyntax;
import main.java.avii.editor.metamodel.entities.EntityEventSpecification;

public class EntityEventReference extends BaseEntityReference {

	private EntityEventSpecification _event;

	public EntityEventReference(int lineNumber, IActionLanguageSyntax syntax, EntityEventSpecification event) {
		super(lineNumber, syntax);
		this._event = event;
	}
	
	public EntityEventSpecification getEvent()
	{
		return this._event;
	}

}
