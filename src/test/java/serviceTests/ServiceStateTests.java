/*
package serviceTests;

import implementation.EditorService;
import junit.framework.Assert;
import junit.framework.TestCase;
import main.java.avii.editor.command.history.EditorCommandHistory;
import main.java.avii.editor.service.DomainNotFoundException;
import main.java.avii.editor.service.EditorServiceException;

public class ServiceStateTests extends TestCase {
	public ServiceStateTests(String name) {
		super(name);
	}

	public void test_can_add_state_to_class() throws DomainNotFoundException, EditorServiceException {
		String classId = "ID1";
		String stateName = "state1";

		EditorService service = new EditorService();
		EditorCommandHistory retrievedHistory = service.createNewDomain("domainName");

		service.addClass(retrievedHistory, classId);
		Assert.assertEquals(false, service.getStateNamesForClass(retrievedHistory, classId).contains(stateName));
		Assert.assertEquals(0, service.getStateNamesForClass(retrievedHistory, classId).size());
		service.addStateToClass(retrievedHistory, classId, stateName);
		Assert.assertEquals(true, service.getStateNamesForClass(retrievedHistory, classId).contains("State_" + stateName));
		Assert.assertEquals(1, service.getStateNamesForClass(retrievedHistory, classId).size());		
		
	}
	
	public void test_cant_add_duplicate_named_state_to_class() throws DomainNotFoundException, EditorServiceException {
		String className = "className";
		String stateName = "state1";

		EditorService service = new EditorService();
		EditorCommandHistory retrievedHistory = service.createNewDomain("domainName");
		
		service.addClass(retrievedHistory, className);
		Assert.assertEquals(false, service.getStateNamesForClass(retrievedHistory, className).contains(stateName));
		Assert.assertEquals(0, service.getStateNamesForClass(retrievedHistory, className).size());
		service.addStateToClass(retrievedHistory, className, stateName);
		Assert.assertEquals(true, service.getStateNamesForClass(retrievedHistory, className).contains("State_" + stateName));
		Assert.assertEquals(1, service.getStateNamesForClass(retrievedHistory, className).size());
		try{
			service.addStateToClass(retrievedHistory, className, stateName);
			fail();
		}
		catch(EditorServiceException e)
		{}
	}

	public void test_can_get_state_procedure() throws DomainNotFoundException, EditorServiceException
	{
		String classId = "ID1";
		String stateName = "state1";

		EditorService service = new EditorService();
		EditorCommandHistory retrievedHistory = service.createNewDomain("domainName");
	
		service.addClass(retrievedHistory, classId);
		service.addStateToClass(retrievedHistory, classId, stateName);

		String actionLanguage = service.getActionLanguage(retrievedHistory, classId, stateName);
		Assert.assertEquals("", actionLanguage);
	}
	
	public void test_can_set_state_procedure() throws DomainNotFoundException, EditorServiceException
	{
		String classId = "ID1";
		String stateId = "state1";
		String actionLanguage = "CREATE bob FROM Persion;\n";

		EditorService service = new EditorService();
		EditorCommandHistory retrievedHistory = service.createNewDomain("domainName");
	
		service.addClass(retrievedHistory, classId);
		service.addStateToClass(retrievedHistory, classId, stateId);
		service.setActionLanguage(retrievedHistory, classId, stateId, actionLanguage);
		
		Assert.assertEquals(actionLanguage, service.getActionLanguage(retrievedHistory, classId, stateId));
	}
	
	public void test_can_rename_state() throws DomainNotFoundException, EditorServiceException {
		String classId = "ID1";
		String stateId = "state1";
		String newStateName = "state2";

		EditorService service = new EditorService();
		EditorCommandHistory retrievedHistory = service.createNewDomain("domainName");
	
		service.addClass(retrievedHistory, classId);
		service.addStateToClass(retrievedHistory, classId, stateId);
		Assert.assertEquals(true, service.getStateNamesForClass(retrievedHistory, classId).contains("State_" + stateId));
		Assert.assertEquals(false, service.getStateNamesForClass(retrievedHistory, classId).contains(newStateName));
		service.renameState(retrievedHistory, classId, stateId, newStateName);
		Assert.assertEquals(false, service.getStateNamesForClass(retrievedHistory, classId).contains("State_" + stateId));
		Assert.assertEquals(true, service.getStateNamesForClass(retrievedHistory, classId).contains(newStateName));
	}

	public void test_can_delete_state() throws DomainNotFoundException, EditorServiceException {
		String classId = "ID1";
		String stateId = "state1";

		EditorService service = new EditorService();
		EditorCommandHistory retrievedHistory = service.createNewDomain("domainName");
	
		service.addClass(retrievedHistory, classId);
		service.addStateToClass(retrievedHistory, classId, stateId);
		Assert.assertEquals(true, service.getStateNamesForClass(retrievedHistory, classId).contains("State_" + stateId));
		service.deleteState(retrievedHistory, classId, stateId);
		Assert.assertEquals(false, service.getStateNamesForClass(retrievedHistory, classId).contains("State_" + stateId));
	}
	
	
}
*/