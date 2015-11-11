package test.java.editorCommandTests;

import test.java.helper.TestHelper;
import test.java.helper.TestingUUID;
import junit.framework.Assert;
import junit.framework.TestCase;
import main.java.avii.editor.command.EditorCommand_AddClassToDomain;
import main.java.avii.editor.command.EditorCommand_AddRelationBetweenClasses;
import main.java.avii.editor.command.history.EditorCommandHistory;
import main.java.avii.editor.metamodel.entities.EntityClass;
import main.java.avii.editor.metamodel.entities.EntityDomain;
import main.java.avii.editor.metamodel.entities.EntityRelation;

public class AddRelationCommandTests extends TestCase {
	public AddRelationCommandTests(String name) {
		super(name);
	}
	
	public void test_can_add_relation_between_classes()
	{
		EntityDomain domain = new EntityDomain("TestDomain");
		EntityClass user = new EntityClass("User");
		domain.addClass(user);
		EntityClass task = new EntityClass("Task");
		domain.addClass(task);
		
		EditorCommand_AddRelationBetweenClasses addRelation = new EditorCommand_AddRelationBetweenClasses();
		addRelation.setDomain(domain);
		addRelation.setClassAId("User");
		addRelation.setClassBId("Task");
		addRelation.setRelationUUID("relationId");
		
		Assert.assertEquals(false, user.hasRelation("Relation_relationId"));
		
		Assert.assertEquals(true, addRelation.doCommand().returnStatus());
		
		EntityRelation r1 = addRelation.getNewRelation();
		Assert.assertEquals("Relation_relationId", r1.getName());
		
		Assert.assertEquals(true, user.hasRelation("Relation_relationId"));
	}
	
	public void test_round_trip_serialisation() throws Exception
	{
		String domainName = "NewDomain";
		String classAId = "IDUser";
		String classBId = "IDTask";

		EditorCommandHistory history = new EditorCommandHistory(domainName);
		history.setUUID(new TestingUUID());
		
		EditorCommand_AddClassToDomain addClassA = new EditorCommand_AddClassToDomain();
		addClassA.setClassId(classAId);
		history.performCommand(addClassA);
		
		EditorCommand_AddClassToDomain addClassB = new EditorCommand_AddClassToDomain();
		addClassB.setClassId(classBId);
		history.performCommand(addClassB);
		
		EditorCommand_AddRelationBetweenClasses addRelation = new EditorCommand_AddRelationBetweenClasses();
		addRelation.setClassAId("IDUser");
		addRelation.setClassBId("IDTask");
		addRelation.setRelationUUID("relationId");
		history.performCommand(addRelation);
		
		EditorCommandHistory history2 = TestHelper.roundTripEditorHistoryCommandHelper(history);
		Assert.assertEquals(true, history2.getDomain().getRelationWithName("Relation_relationId").getClassA().getId().equals(classAId));
		Assert.assertEquals(true, history2.getDomain().getRelationWithName("Relation_relationId").getClassB().getId().equals(classBId));
	}
	
	public void test_cant_add_relation_to_classA_that_doesnt_exist()
	{
		EntityDomain domain = new EntityDomain("TestDomain");
		EntityClass task = new EntityClass("Task");
		domain.addClass(task);
		
		EditorCommand_AddRelationBetweenClasses addRelation = new EditorCommand_AddRelationBetweenClasses();
		addRelation.setDomain(domain);
		addRelation.setClassAId("User");
		addRelation.setClassBId("Task");
		addRelation.setRelationUUID("relationId");
		
		Assert.assertEquals(false, addRelation.doCommand().returnStatus());
	}
	
	public void test_cant_add_relation_to_classB_that_doesnt_exist()
	{
		EntityDomain domain = new EntityDomain("TestDomain");
		EntityClass user = new EntityClass("User");
		domain.addClass(user);
		
		EditorCommand_AddRelationBetweenClasses addRelation = new EditorCommand_AddRelationBetweenClasses();
		addRelation.setDomain(domain);
		addRelation.setClassAId("User");
		addRelation.setClassBId("Task");
		addRelation.setRelationUUID("relationId");
		
		Assert.assertEquals(false, addRelation.doCommand().returnStatus());
	}

}
