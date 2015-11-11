package main.java.avii.editor.command;

import org.simpleframework.xml.Attribute;
import org.simpleframework.xml.Element;

import main.java.avii.editor.command.result.SuccessfulEditorCommand;
import main.java.avii.editor.metamodel.entities.EntityClass;
import main.java.avii.editor.metamodel.entities.EntityRelation;
import main.java.avii.editor.metamodel.entities.EntityRelation.CardinalityType;

@Element
public class EditorCommand_ChangeRelation extends BaseEditorCommand {

	@Attribute
	private String _relationId;
	@Attribute
	private String _classAId;
	@Attribute
	private String _verbA;
	@Attribute
	private String _cardinalityA;
	@Attribute
	private String _classBId;
	@Attribute
	private String _verbB;
	@Attribute
	private String _cardinalityB;

	@Override
	protected IEditorCommandResult concreteDoCommand() {
		EntityRelation relation = this._domain.getRelationWithId(this._relationId);
		EntityClass newClassA = this._domain.getEntityClassWithId(this._classAId);
		relation.setEndA(newClassA, CardinalityType.fromHuman(this._cardinalityA), this._verbA);
		EntityClass newClassB = this._domain.getEntityClassWithId(this._classBId);
		relation.setEndB(newClassB, CardinalityType.fromHuman(this._cardinalityB), this._verbB);
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
		if(!this._domain.hasRelationWithId(this._relationId))
		{
			return failWhenRelationIdNotFound(this._relationId);
		}
		return new SuccessfulEditorCommand();
	}

	public void setRelationId(String relationId) {
		this._relationId = relationId;
	}

	public void setClassAId(String classAId) {
		this._classAId = classAId;
	}

	public void setClassAVerb(String verbA) {
		this._verbA = verbA;
	}

	public void setClassACardinality(String cardinalityA) {
		this._cardinalityA = cardinalityA;
	}

	public void setClassBId(String classBId) {
		this._classBId = classBId;
	}

	public void setClassBVerb(String verbB) {
		this._verbB = verbB;
	}

	public void setClassBCardinality(String cardinalityB) {
		this._cardinalityB = cardinalityB;
	}

}
