package main.java.avii.editor.metamodel.actionLanguage.entityReference;

import main.java.avii.editor.contracts.metamodel.actionLanguage.syntax.IActionLanguageSyntax;
import main.java.avii.editor.metamodel.entities.EntityEventParam;

public class EntityEventParamReference extends BaseEntityReference {

	private EntityEventParam _eventParam;

	public EntityEventParamReference(int lineNumber, IActionLanguageSyntax syntax, EntityEventParam theParam) {
		super(lineNumber, syntax);
		this._eventParam = theParam;
	}
	
	public EntityEventParam getEventParam()
	{
		return this._eventParam;
	}

}
