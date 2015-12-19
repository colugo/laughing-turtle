package implementation;

import models.JsonClassDiagram;
import main.java.models.JsonClassDiagramCoordinatesHelper;
import models.JsonEntityAttribute;
import models.JsonEntityClass;
import models.JsonEntityRelation;
import models.JsonEntityState;
import models.JsonEventSpec;
import main.java.avii.editor.command.history.EditorCommandHistory;
import main.java.avii.editor.contracts.metamodel.entities.datatypes.IEntityDatatype;
import main.java.avii.editor.metamodel.entities.EntityAttribute;
import main.java.avii.editor.metamodel.entities.EntityClass;
import main.java.avii.editor.metamodel.entities.EntityDomain;
import main.java.avii.editor.metamodel.entities.EntityEventSpecification;
import main.java.avii.editor.metamodel.entities.EntityRelation;
import main.java.avii.editor.metamodel.entities.EntityRelation.CardinalityType;
import main.java.avii.editor.metamodel.entities.EntityState;
import main.java.avii.editor.metamodel.entities.datatypes.AvailableDatatypes;
import main.java.avii.editor.service.DomainNotFoundException;
import main.java.avii.editor.service.EditorServiceException;
import contracts.IApplicationImplementation;

public class ApplicationImplementation implements IApplicationImplementation {
	EditorService _service = new EditorService();

	public EditorCommandHistory newDomain(String domainName) {
		EditorCommandHistory newHistory = _service.createNewDomain(domainName);
		return newHistory;
	}

	public JsonClassDiagram getClassDiagram(EntityDomain domain) throws DomainNotFoundException {
		JsonClassDiagram jsonClassDiagram = new JsonClassDiagram();
		for(EntityClass theClass : domain.getClasses())
		{
			JsonEntityClass jsonEntityClass = new JsonEntityClass(theClass.getName(), theClass.getId(), theClass.x, theClass.y, theClass.superClassTriangleIndex);
			for(EntityAttribute theAttribute : theClass.getAttributes())
			{
				if(theAttribute.isNotStateAttribute())
				{
					JsonEntityAttribute jsonEntityAttribute = new JsonEntityAttribute(theAttribute.getName(), theAttribute.getId(), theAttribute.getType().getHumanName());
					jsonEntityClass._attributes.add(jsonEntityAttribute);
				}
			}
			
			for(EntityState theState : theClass.getStates()){
				JsonEntityState jsonEntityState = new JsonEntityState(theState);
				jsonEntityClass._states.add(jsonEntityState);
			}
			
			for(EntityEventSpecification theSpec : theClass.getEventSpecifications()){
				JsonEventSpec jsonEventSpec = new JsonEventSpec(theSpec);
				jsonEntityClass._specs.add(jsonEventSpec);
			}
			
			if(theClass.hasSuperClasses()){
				jsonEntityClass.setSuperClass(theClass.getSuperClasses().get(0));
			}
			jsonClassDiagram.add(jsonEntityClass);
		}
		for(EntityRelation relation : domain.getRelations()){
			JsonEntityRelation jsonRelation = new JsonEntityRelation(relation.getName(), relation.getId(), relation.getClassA().getId(), relation.getClassB().getId(), relation.getClassAVerb(), CardinalityType.toHuman(relation.getCardinalityA()), relation.getClassBVerb(), CardinalityType.toHuman(relation.getCardinalityB()), relation.endAIndex, relation.endBIndex, relation.verbAOffsetX, relation.verbAOffsetY, relation.verbBOffsetX, relation.verbBOffsetY);
			if(relation.hasAssociation())
			{
				jsonRelation.setAssociation(relation.getAssociation().getId());
			}
			jsonClassDiagram.add(jsonRelation);
		}
		return jsonClassDiagram;
	}

	public void Action_NewClassAction(EditorCommandHistory retrievedHistory, String uuidOfClass) throws DomainNotFoundException, EditorServiceException {
		_service.addClass(retrievedHistory, uuidOfClass);
	}

	public void Action_RenameClass(EditorCommandHistory retrievedHistory, String uuidOfClass, String newClassName) throws DomainNotFoundException, EditorServiceException {
		_service.changeClassName(retrievedHistory, uuidOfClass, newClassName);
	}

	public void Action_DeleteClass(EditorCommandHistory retrievedHistory, String uuidToDelete) throws DomainNotFoundException, EditorServiceException {
		_service.deleteClass(retrievedHistory, uuidToDelete);
	}

	@Override
	public void Action_AddAttribute(EditorCommandHistory retrievedHistory, String uuidOfClass, String uuidOfAttribute) throws DomainNotFoundException, EditorServiceException {
		_service.addAttribute(retrievedHistory, uuidOfClass, uuidOfAttribute);
	}

	@Override
	public void Action_DeleteAttribute(EditorCommandHistory retrievedHistory, String uuidOfClass, String uuidOfAttribute) throws DomainNotFoundException, EditorServiceException {
		_service.deleteAttribute(retrievedHistory, uuidOfClass, uuidOfAttribute);
	}

	@Override
	public void Action_RenameAttribute(EditorCommandHistory retrievedHistory, String uuidOfClass, String uuidOfAttribute, String newAttributeName) throws DomainNotFoundException, EditorServiceException {
		_service.changeAttributeName(retrievedHistory, uuidOfClass, uuidOfAttribute, newAttributeName);
	}

	@Override
	public void Action_ChangeAttributeType(EditorCommandHistory retrievedHistory, String uuidOfClass, String uuidOfAttribute, String newTypeHumanName)throws DomainNotFoundException, EditorServiceException {
		IEntityDatatype dataType =  AvailableDatatypes.getDatatypeForHumanName(newTypeHumanName);
		_service.changeAttributeDatatype(retrievedHistory, uuidOfClass, uuidOfAttribute, dataType);
	}

	@Override
	public void Action_CreateRelation(EditorCommandHistory retrievedHistory, String relationUUID, String endAUUID, String endBUUID) throws DomainNotFoundException, EditorServiceException {
		_service.createNewRelation(retrievedHistory, relationUUID, endAUUID, endBUUID);
	}

	@Override
	public void Action_ChangeRelation(EditorCommandHistory retrievedHistory, String relationId, String classAId, String verbA, String cardinalityA, String classBId, String verbB, String cardinalityB) throws DomainNotFoundException, EditorServiceException {
		_service.changeRelation(retrievedHistory, relationId, classAId, verbA, cardinalityA, classBId, verbB, cardinalityB);
		
	}

	@Override
	public void Action_RenameRelation(EditorCommandHistory retrievedHistory, String relationId, String newName) throws DomainNotFoundException, EditorServiceException {
		_service.changeRelationName(retrievedHistory, relationId, newName);
	}

	@Override
	public void Action_DeleteRelation(EditorCommandHistory retrievedHistory, String relationId) throws DomainNotFoundException, EditorServiceException {
		_service.deleteRelation(retrievedHistory, relationId);		
	}

	@Override
	public void Action_CreateAssociation(EditorCommandHistory retrievedHistory, String relationId, String associationClassId) throws DomainNotFoundException, EditorServiceException {
		_service.createAssociation(retrievedHistory, relationId, associationClassId);		
	}

	@Override
	public void Action_ChangeSuperClass(EditorCommandHistory retrievedHistory, String classId, String superClassId) throws DomainNotFoundException, EditorServiceException {
		_service.changeSuperClass(retrievedHistory, classId, superClassId);		
	}

	@Override
	public void Action_ChangeClassDiagramCoordinates(EditorCommandHistory retrievedHistory, JsonClassDiagramCoordinatesHelper coordinatesHelper) throws DomainNotFoundException, EditorServiceException {
		_service.changeClassDiagramCoordinates(retrievedHistory, coordinatesHelper);
	}

	@Override
	public void Action_AddState(EditorCommandHistory retrievedHistory, String uuidOfClass, String uuidOfState) throws DomainNotFoundException, EditorServiceException {
		_service.addStateToClass(retrievedHistory, uuidOfClass, uuidOfState);
	}
	
	@Override
	public void Action_RenameState(EditorCommandHistory retrievedHistory, String uuidOfClass, String uuidOfState, String newClassName) throws DomainNotFoundException, EditorServiceException {
		_service.renameState(retrievedHistory, uuidOfClass, uuidOfState, newClassName);
	}
	
	@Override
	public void Action_CreateStateTransition(EditorCommandHistory retrievedHistory, String classUUID, String fromUUID, String toUUID, String instanceUUID) throws DomainNotFoundException, EditorServiceException {
		_service.createNewStateTransition(retrievedHistory, classUUID, fromUUID, toUUID, instanceUUID);
	}
	
	@Override
	public void Action_ChangeStateTransition(EditorCommandHistory retrievedHistory, String instanceUUID, String fromUUID, String toUUID, String specUUID, String classUUID) throws DomainNotFoundException, EditorServiceException {
		_service.changeStateTransition(retrievedHistory, instanceUUID, fromUUID, toUUID, specUUID, classUUID);
	}

	@Override
	public void Action_CreateEventSpecification(EditorCommandHistory retrievedHistory, String classUUID, String specificationUUID) throws DomainNotFoundException, EditorServiceException {
		_service.createEventSpecification(retrievedHistory, classUUID, specificationUUID);
	}
	
	@Override
	public void Action_RenameSpec(EditorCommandHistory retrievedHistory, String uuidOfClass, String uuidOfSpec, String newName) throws DomainNotFoundException, EditorServiceException {
		_service.renameSpec(retrievedHistory, uuidOfClass, uuidOfSpec, newName);
	}

	@Override
	public void Action_AddParam(EditorCommandHistory retrievedHistory, String uuidOfClass, String uuidOfSpec, String uuidOfParam) throws DomainNotFoundException, EditorServiceException {
		_service.addParam(retrievedHistory, uuidOfClass, uuidOfSpec, uuidOfParam);
	}
	
	@Override
	public void Action_RenameSpecParam(EditorCommandHistory retrievedHistory, String uuidOfClass, String uuidOfSpec, String uuidOfParam, String newName) throws DomainNotFoundException, EditorServiceException {
		_service.renameSpecParam(retrievedHistory, uuidOfClass, uuidOfSpec, uuidOfParam, newName);
	}
	
	@Override
	public void Action_DeleteSpecParam(EditorCommandHistory retrievedHistory, String uuidOfClass, String uuidOfSpec, String uuidOfParam) throws DomainNotFoundException, EditorServiceException {
		_service.deleteSpecParam(retrievedHistory, uuidOfClass, uuidOfSpec, uuidOfParam);
	}
	
	@Override
	public void Action_ChangeSpecParamType(EditorCommandHistory retrievedHistory, String uuidOfClass, String uuidOfSpec, String uuidOfParam, String newType) throws DomainNotFoundException, EditorServiceException {
		IEntityDatatype dataType =  AvailableDatatypes.getDatatypeForHumanName(newType);
		_service.changeSpecParamType(retrievedHistory, uuidOfClass, uuidOfSpec, uuidOfParam, dataType);
	}
	
	@Override
	public void Action_DeleteSpec(EditorCommandHistory retrievedHistory, String uuidOfClass, String uuidOfSpec) throws DomainNotFoundException, EditorServiceException {
		_service.deleteSpec(retrievedHistory, uuidOfClass, uuidOfSpec);
	}
	
}
