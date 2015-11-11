package main.java.avii.editor.command;

import main.java.models.JsonClassDiagramCoordinatesHelper;
import main.java.models.JsonClassDiagramCoordinatesHelperEntityClass;
import main.java.models.JsonClassDiagramCoordinatesHelperEntityRelation;
import main.java.models.JsonClassDiagramCoordinatesHelperEntityState;
import main.java.models.JsonClassDiagramCoordinatesHelperEventInstance;

import org.simpleframework.xml.Element;

import main.java.avii.editor.command.result.SuccessfulEditorCommand;
import main.java.avii.editor.metamodel.entities.EntityClass;
import main.java.avii.editor.metamodel.entities.EntityEventInstance;
import main.java.avii.editor.metamodel.entities.EntityRelation;
import main.java.avii.editor.metamodel.entities.EntityState;

@Element
public class EditorCommand_ChangeClassDiagramCoordinates extends BaseEditorCommand {

	@Element
	private JsonClassDiagramCoordinatesHelper _coordinatesHelper;

	@Override
	protected IEditorCommandResult concreteDoCommand() {
		for(JsonClassDiagramCoordinatesHelperEntityClass classHelper : _coordinatesHelper.classes)
		{
			EntityClass theClass = this._domain.getEntityClassWithId(classHelper.uuid);
			theClass.x = classHelper.x;
			theClass.y = classHelper.y;
			theClass.superClassTriangleIndex = classHelper.superClassTriangleIndex;
			for(JsonClassDiagramCoordinatesHelperEntityState stateHelper : classHelper.states){
				EntityState theState = theClass.getStateWithId(stateHelper.uuid);
				theState.x = stateHelper.x;
				theState.y = stateHelper.y;
			}
			for(JsonClassDiagramCoordinatesHelperEventInstance instanceHelper : classHelper.instances){
				EntityEventInstance instance = theClass.getEventInstanceWithId(instanceHelper.uuid);
				instance.fromIndex = instanceHelper.fromIndex;
				instance.toIndex = instanceHelper.toIndex;
				instance.fromDragX = instanceHelper.fromDragX;
				instance.fromDragY = instanceHelper.fromDragY;
				instance.toDragX = instanceHelper.toDragX;
				instance.toDragY = instanceHelper.toDragY;
			}
		}
		for(JsonClassDiagramCoordinatesHelperEntityRelation relationHelper : _coordinatesHelper.relations)
		{
			EntityRelation theRelation = this._domain.getRelationWithId(relationHelper.uuid);
			theRelation.endAIndex = relationHelper.endAIndex;
			theRelation.endBIndex = relationHelper.endBIndex;
			
			theRelation.verbAOffsetX = relationHelper.verbAOffsetX;
			theRelation.verbAOffsetY = relationHelper.verbAOffsetY;
			theRelation.verbBOffsetX = relationHelper.verbBOffsetX;
			theRelation.verbBOffsetY = relationHelper.verbBOffsetY;
		}
		return new SuccessfulEditorCommand();
	}

	@Override
	public IEditorCommandResult canDoCommand() {
		return new SuccessfulEditorCommand();
	}

	public void setCoordinatesHelper(JsonClassDiagramCoordinatesHelper coordinatesHelper) {
		this._coordinatesHelper = coordinatesHelper;
	}
	
}
