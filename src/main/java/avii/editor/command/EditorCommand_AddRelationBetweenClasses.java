package main.java.avii.editor.command;

import org.simpleframework.xml.Attribute;

import main.java.avii.editor.command.result.SuccessfulEditorCommand;
import main.java.avii.editor.metamodel.entities.EntityClass;
import main.java.avii.editor.metamodel.entities.EntityRelation;
import main.java.avii.editor.metamodel.entities.EntityRelation.CardinalityType;

public class EditorCommand_AddRelationBetweenClasses extends BaseEditorCommand {

	@Attribute
	private String _classAId;
	@Attribute
	private String _classBId;
	@Attribute
	private String _relationId;
	private EntityRelation _newRelation;
	

	@Override
	protected IEditorCommandResult concreteDoCommand() {
		EntityClass classA = this._domain.getEntityClassWithId(this._classAId);
		EntityClass classB = this._domain.getEntityClassWithId(this._classBId);
		this._newRelation = new EntityRelation("Relation_" + this._relationId);
		this._newRelation.setEndA(classA, CardinalityType.ZERO_TO_MANY, "leads");
		this._newRelation.setEndB(classB, CardinalityType.ZERO_TO_MANY, "follows");
		this._newRelation.setId(_relationId);
		return new SuccessfulEditorCommand();
	}

	@Override
	public IEditorCommandResult canDoCommand() {
		if (!this._domain.hasEntityClassWithId(this._classAId)) {
			return failWhenClassNotFound(this._classAId);
		}
		if (!this._domain.hasEntityClassWithId(this._classBId)) {
			return failWhenClassNotFound(this._classBId);
		}
		if(this._domain.hasRelationWithId(this._relationId))
		{
			return BaseEditorCommand.failWhenRelationAlreadyExists(this._relationId);
		}
		return new SuccessfulEditorCommand();
	}

	public void setClassAId(String classAId) {
		this._classAId = classAId;
	}

	public void setClassBId(String classBId) {
		this._classBId = classBId;
	}

	public EntityRelation getNewRelation() {
		return this._newRelation;
	}

	public void setRelationUUID(String relationId) {
		this._relationId = relationId;
	}

}
