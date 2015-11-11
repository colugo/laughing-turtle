package test.java.editorCommandTests;

import test.java.helper.TestHelper;
import test.java.helper.TestingUUID;
import junit.framework.Assert;
import junit.framework.TestCase;
import main.java.avii.editor.command.EditorCommand_AddClassToDomain;
import main.java.avii.editor.command.EditorCommand_AddEventParam;
import main.java.avii.editor.command.EditorCommand_CreateEventSpecification;
import main.java.avii.editor.command.history.EditorCommandHistory;
import main.java.avii.editor.metamodel.entities.EntityClass;
import main.java.avii.editor.metamodel.entities.EntityDomain;
import main.java.avii.editor.metamodel.entities.EntityEventSpecification;

public class AddEventParamCommandTests extends TestCase {
	public AddEventParamCommandTests(String name) {
		super(name);
	}
	
	public void test_can_add_param_to_event()
	{
		String paramId = "param1";
		EntityDomain domain = new EntityDomain("TestDomain");
		EntityClass klass = new EntityClass("Class");
		domain.addClass(klass);
		EntityEventSpecification spec = klass.getDefaultEventSpecification();
		
		Assert.assertEquals(0, spec.getEventParams().size());

		EditorCommand_AddEventParam addParam = new EditorCommand_AddEventParam();
		addParam.setDomain(domain);
		addParam.setClassId(klass.getId());
		addParam.setSpecId(spec.getId());
		addParam.setParamId(paramId);
		Assert.assertEquals(true, addParam.doCommand().returnStatus());
		
		Assert.assertEquals(true, addParam.doCommand().returnStatus());
		Assert.assertEquals(true, spec.hasParamWithId(paramId));
		Assert.assertEquals(1, spec.getEventParams().size());
	}
	
	public void test_round_trip_serialisation() throws Exception
	{
		String domainName = "NewDomain";
		String classId = "class1";
		String specId = "spec1";
		String paramId = "param1";
		
		EditorCommandHistory history = new EditorCommandHistory(domainName);
		history.setUUID(new TestingUUID());
		
		EditorCommand_AddClassToDomain addClassToDomain = new EditorCommand_AddClassToDomain();
		addClassToDomain.setClassId(classId);
		history.performCommand(addClassToDomain);
		
		EditorCommand_CreateEventSpecification createSpec = new EditorCommand_CreateEventSpecification();
		createSpec.setClassId(classId);
		createSpec.setSpecId(specId);
		history.performCommand(createSpec);
		
		EditorCommand_AddEventParam addParam = new EditorCommand_AddEventParam();
		addParam.setClassId(classId);
		addParam.setSpecId(specId);
		addParam.setParamId(paramId);
		history.performCommand(addParam);
		
		EditorCommandHistory history2 = TestHelper.roundTripEditorHistoryCommandHelper(history);
		Assert.assertEquals(true, history2.getDomain().getEntityClassWithId(classId).getEventSpecificationWithId(specId).hasParamWithId(paramId));
	}
	
	
}
