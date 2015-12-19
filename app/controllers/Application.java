package controllers;

import implementation.ApplicationImplementation;
import implementation.ConcreteUUIDIdentifier;
import implementation.storage.IStorage;
import implementation.storage.StorageException;
import implementation.storage.XmlStorage;
import models.JsonActionParser;
import models.JsonClassDiagram;

import org.codehaus.jackson.node.ObjectNode;

import play.libs.Json;
import play.mvc.BodyParser;
import play.mvc.Controller;
import play.mvc.Result;
import main.java.avii.editor.command.history.EditorCommandHistory;
import main.java.avii.editor.service.DomainNotFoundException;
import main.java.avii.editor.service.IUUIDIdentifier;
import contracts.IApplicationImplementation;

public class Application extends Controller {
	
	
	//private static IApplicationImplementation _application = new MockApplicationImplementation();
	private static IApplicationImplementation _application = new ApplicationImplementation();
	
	private static IStorage _storage = new XmlStorage();
	
	public static Result index() {
		return ok("Your new application is ready.");
	}

	public static Result newDomain(String domainName){
		EditorCommandHistory newHistory = _application.newDomain(domainName);
		_storage.saveHistory(newHistory, newHistory.getUUID());
		return ok(newHistory.getUUID().getUUIDString());
	}
	
	@BodyParser.Of(BodyParser.Json.class)
	public static Result getClassDiagram(String projectId){
		JsonClassDiagram jsonClassDiagram;
		try {
			ConcreteUUIDIdentifier uuid = new ConcreteUUIDIdentifier(projectId);
			EditorCommandHistory retrievedHistory = getHistoryForUUID(uuid);
			
			jsonClassDiagram = _application.getClassDiagram(retrievedHistory.getDomain());
			ObjectNode result = Json.newObject();
			result.put("diagram", Json.toJson(jsonClassDiagram));
			return ok(result);
		} catch (DomainNotFoundException e) {
			return badRequest("No domain with id : " + projectId);
		}
		
	}

	private static EditorCommandHistory getHistoryForUUID(IUUIDIdentifier uuid) throws DomainNotFoundException
	{
		if(_storage.doesUUIDExist(uuid))
		{
			try {
				return _storage.getHistory(uuid);
			} catch (StorageException e) {
				throw new DomainNotFoundException(uuid);
			}
		}
		throw new DomainNotFoundException(uuid);
	}
	
	
	public static Result saveClassDiagramActions(String projectId){
		String jsonText = new String(request().body().asRaw().asBytes());
		
		System.out.println("--saveClassDiagramActions--\n" + jsonText);
		
		// this fires callbacks into _application
		JsonActionParser actionParser = new JsonActionParser(_application);
		try {
			
			ConcreteUUIDIdentifier uuid = new ConcreteUUIDIdentifier(projectId);
			EditorCommandHistory retrievedHistory = getHistoryForUUID(uuid);
			// do the work
			actionParser.parse(jsonText, retrievedHistory);
			_storage.saveHistory(retrievedHistory, uuid);
			
		} catch(Exception e) {
			ObjectNode result = Json.newObject();
			result.put("message", Json.toJson(e.getMessage()));
			result.put("okActions", actionParser.getSuccessfulActions());
			e.printStackTrace();
			return Controller.internalServerError(result);
		}
		return ok();
	}

}