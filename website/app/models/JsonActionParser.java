package models;


import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map;

import main.java.avii.editor.command.history.EditorCommandHistory;
import main.java.avii.editor.service.DomainNotFoundException;
import main.java.avii.editor.service.EditorServiceException;
import main.java.models.JsonClassDiagramCoordinatesHelper;

import org.codehaus.jackson.JsonParseException;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;

import contracts.IApplicationImplementation;

public class JsonActionParser {

	private enum Actions {
		NewClassAction, RenameClassAction, DeleteClassAction,
		AddAttributeAction, DeleteAttributeAction, RenameAttributeAction,
		ChangeAttributeTypeAction, NewRelationAction, ChangeRelationAction,
		RenameRelationAction, DeleteRelationAction, CreateAssociationAction,
		ChangeSuperClassAction, SaveClassDiagramCoordinatesAction, AddStateAction,
		RenameStateAction, NewStateTransitionAction, ChangeStateTransitionAction,
		NewEventSpecificationAction, RenameSpecAction, AddParamAction,
		RenameSpecParamAction, DeleteSpecParamAction, ChangeParamTypeAction,
		DeleteSpecAction;
		
		public static Actions toAction(String str)
	    {
			return valueOf(str);
	    };	
	}

	private IApplicationImplementation _application;
	private int _successfulActions = 0;
	
	public JsonActionParser(IApplicationImplementation _application) {
		this._application = _application;
	}

	public int getSuccessfulActions()
	{
		return this._successfulActions;
	}

	
	@SuppressWarnings("unchecked")
	public void parse(String jsonText, EditorCommandHistory retrievedHistory) throws JsonParseException, JsonMappingException, IOException, SecurityException, NoSuchMethodException, IllegalArgumentException, IllegalAccessException, InvocationTargetException, DomainNotFoundException, EditorServiceException
	{
		ObjectMapper mapper = new ObjectMapper();
		Map<String, ArrayList<Map<String,Object>>> rootAsMap = mapper.readValue(jsonText, Map.class);
		ArrayList<Map<String,Object>> actions = rootAsMap.get("actions");
		for(Map<String,Object> map : actions)
		{
			String type = (String) map.get("type");
			//reflection isn't working out well here - go with ugly if block
			switch(Actions.toAction(type)){
			
			case NewClassAction:
				this._application.Action_NewClassAction(retrievedHistory, (String) map.get("uuidOfClass"));
				break;
				
			case RenameClassAction:
				this._application.Action_RenameClass(retrievedHistory, (String)map.get("uuidOfClass"), (String)map.get("newName"));
				break;
				
			case DeleteClassAction:
				this._application.Action_DeleteClass(retrievedHistory, (String)map.get("uuidToDelete"));
				break;
				
			case AddAttributeAction:
				this._application.Action_AddAttribute(retrievedHistory, (String)map.get("uuidOfClass"), (String)map.get("uuidOfAttribute"));
				break;
				
			case DeleteAttributeAction:
				this._application.Action_DeleteAttribute(retrievedHistory, (String)map.get("uuidOfClass"), (String)map.get("uuidOfAttribute"));
				break;
				
			case RenameAttributeAction:
				this._application.Action_RenameAttribute(retrievedHistory, (String)map.get("uuidOfClass"), (String)map.get("uuidOfAttribute"), (String)map.get("newAttributeName"));
				break;
				
			case ChangeAttributeTypeAction:
				this._application.Action_ChangeAttributeType(retrievedHistory, (String)map.get("uuidOfClass"), (String)map.get("uuidOfAttribute"), (String)map.get("newType"));
				break;
				
			case NewRelationAction:
				this._application.Action_CreateRelation(retrievedHistory, (String)map.get("uuid"), (String)map.get("uuidA"), (String)map.get("uuidB"));
				break;
				
			case ChangeRelationAction:
				this._application.Action_ChangeRelation(retrievedHistory, (String)map.get("theRelationId"), (String)map.get("classAId"), (String)map.get("classAVerb"), (String)map.get("classACardinality"), (String)map.get("classBId"), (String)map.get("classBVerb"), (String)map.get("classBCardinality"));
				break;
				
			case RenameRelationAction:
				this._application.Action_RenameRelation(retrievedHistory, (String)map.get("uuidOfRelation"), (String)map.get("newRelationName"));
				break;
				
			case DeleteRelationAction:
				this._application.Action_DeleteRelation(retrievedHistory, (String)map.get("uuidOfRelation"));
				break;
				
			case CreateAssociationAction:
				this._application.Action_CreateAssociation(retrievedHistory, (String)map.get("uuidOfRelation"), (String)map.get("uuidOfAssociationClass"));
				break;
				
			case ChangeSuperClassAction:
				this._application.Action_ChangeSuperClass(retrievedHistory, (String)map.get("uuidOfClass"), (String)map.get("uuidOfSuperClass"));
				break;
			
			case SaveClassDiagramCoordinatesAction:
				// this should not count as a successful action
				this._successfulActions--;
				JsonClassDiagramCoordinatesHelper coordinatesHelper = new JsonClassDiagramCoordinatesHelper();
				JsonCoordinateHelper.helpEntityClasses((LinkedHashMap<String, Object>)map.get("entityClassPositions"), coordinatesHelper);
				
				this._application.Action_ChangeClassDiagramCoordinates(retrievedHistory, coordinatesHelper);
				break;
			
			case AddStateAction:
				this._application.Action_AddState(retrievedHistory, (String)map.get("uuidOfClass"), (String)map.get("uuidOfState"));
				break;

			case RenameStateAction:
				this._application.Action_RenameState(retrievedHistory, (String)map.get("uuidOfClass"), (String)map.get("uuidOfState"), (String)map.get("newName"));
				break;

			case NewStateTransitionAction:
				this._application.Action_CreateStateTransition(retrievedHistory, (String)map.get("classUUID"), (String)map.get("fromUUID"), (String)map.get("toUUID"), (String)map.get("instanceUUID"));
				break;
				
			case ChangeStateTransitionAction:
				this._application.Action_ChangeStateTransition(retrievedHistory, (String)map.get("instanceUUID"), (String)map.get("fromUUID"), (String)map.get("toUUID"), (String)map.get("specUUID"), (String)map.get("classUUID"));
				break;
				
			case NewEventSpecificationAction:
				this._application.Action_CreateEventSpecification(retrievedHistory, (String)map.get("classUUID"), (String)map.get("specificationUUID"));
				break;
				
			case RenameSpecAction:
				this._application.Action_RenameSpec(retrievedHistory, (String)map.get("uuidOfClass"), (String)map.get("uuidOfSpec"), (String)map.get("newName"));
				break;	
				
			case AddParamAction:
				this._application.Action_AddParam(retrievedHistory, (String)map.get("uuidOfClass"), (String)map.get("uuidOfSpec"), (String)map.get("uuidOfParam"));
				break;
				
			case RenameSpecParamAction:
				this._application.Action_RenameSpecParam(retrievedHistory, (String)map.get("uuidOfClass"), (String)map.get("uuidOfSpec"), (String)map.get("uuidOfParam"), (String)map.get("newName"));
				break;
				
			case DeleteSpecParamAction:
				this._application.Action_DeleteSpecParam(retrievedHistory, (String)map.get("uuidOfClass"), (String)map.get("uuidOfSpec"), (String)map.get("uuidOfParam"));
				break;
				
			case ChangeParamTypeAction:
				this._application.Action_ChangeSpecParamType(retrievedHistory, (String)map.get("uuidOfClass"), (String)map.get("uuidOfSpec"), (String)map.get("uuidOfParam"), (String)map.get("newType"));
				break;
				
			case DeleteSpecAction:
				this._application.Action_DeleteSpec(retrievedHistory, (String)map.get("uuidOfClass"), (String)map.get("uuidOfSpec"));
				break;
				
			}
			this._successfulActions++;
		}
	}


}
