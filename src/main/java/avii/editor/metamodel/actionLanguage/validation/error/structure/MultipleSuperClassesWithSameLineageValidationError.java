package main.java.avii.editor.metamodel.actionLanguage.validation.error.structure;

import main.java.avii.editor.metamodel.entities.EntityClass;

public class MultipleSuperClassesWithSameLineageValidationError implements IValidationError {

	private EntityClass _subClass;
	private EntityClass _superClass1;
	private EntityClass _superClass2;
	private EntityClass _sharedSuper;

	public MultipleSuperClassesWithSameLineageValidationError(EntityClass subClass, EntityClass superClass1, EntityClass superClass2, EntityClass sharedSuper) {
		this._subClass = subClass;
		this._superClass1 = superClass1;
		this._superClass2 = superClass2;
		this._sharedSuper = sharedSuper;
	}

	public String explainError() {
		return "EntityClass '" + this._subClass.getName() + "' has 2 super classes '" + this._superClass1.getName() + "' & '" + this._superClass2.getName() + "' that share a superClass '"+ this._sharedSuper.getName() +"'";
	}

}
