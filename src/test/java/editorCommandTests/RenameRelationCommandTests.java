package test.java.editorCommandTests;

import test.java.helper.TestHelper;
import test.java.helper.TestingUUID;
import junit.framework.Assert;
import junit.framework.TestCase;
import main.java.avii.editor.command.EditorCommand_AddClassToDomain;
import main.java.avii.editor.command.EditorCommand_AddRelationBetweenClasses;
import main.java.avii.editor.command.EditorCommand_RenameRelation;
import main.java.avii.editor.command.history.EditorCommandHistory;
import main.java.avii.editor.metamodel.entities.EntityClass;
import main.java.avii.editor.metamodel.entities.EntityDomain;
import main.java.avii.editor.metamodel.entities.EntityRelation;
import main.java.avii.editor.metamodel.entities.EntityRelation.CardinalityType;

public class RenameRelationCommandTests extends TestCase {
	public RenameRelationCommandTests(String name) {
		super(name);
	}
	
	public void test_can_rename_relation()
	{
		String relationId = "relationId";
		EntityDomain domain = new EntityDomain("TestDomain");
		EntityClass user = new EntityClass("User");
		domain.addClass(user);
		EntityClass task = new EntityClass("Task");
		domain.addClass(task);
				
		EditorCommand_AddRelationBetweenClasses addRelation = new EditorCommand_AddRelationBetweenClasses();
		addRelation.setDomain(domain);
		addRelation.setClassAId("User");
		addRelation.setClassBId("Task");
		addRelation.setRelationUUID(relationId);
		
		Assert.assertEquals(true, addRelation.doCommand().returnStatus());
		
		EntityRelation r1 = addRelation.getNewRelation();
		Assert.assertEquals("Relation_relationId", r1.getName());

		EditorCommand_RenameRelation command = new EditorCommand_RenameRelation();
		command.setDomain(domain);
		command.setRelationId(relationId);
		command.setNewName("R1");
		Assert.assertEquals(true, command.doCommand().returnStatus());
		
		Assert.assertEquals("R1", r1.getName());
	}
	
	public void test_cant_rename_relation_to_existing_name()
	{
		String relationId = "relationId";
		EntityDomain domain = new EntityDomain("TestDomain");
		EntityClass user = new EntityClass("User");
		domain.addClass(user);
		EntityClass task = new EntityClass("Task");
		domain.addClass(task);
				
		EntityRelation relationR1 = new EntityRelation("R1");
		relationR1.setEndA(user, CardinalityType.ONE_TO_MANY);
		relationR1.setEndB(task, CardinalityType.ONE_TO_MANY);
		
		EditorCommand_AddRelationBetweenClasses addRelation = new EditorCommand_AddRelationBetweenClasses();
		addRelation.setDomain(domain);
		addRelation.setClassAId("User");
		addRelation.setClassBId("Task");
		addRelation.setRelationUUID(relationId);
		
		Assert.assertEquals(true, addRelation.doCommand().returnStatus());
		
		EntityRelation r1 = addRelation.getNewRelation();
		Assert.assertEquals("Relation_relationId", r1.getName());

		EditorCommand_RenameRelation command = new EditorCommand_RenameRelation();
		command.setDomain(domain);
		command.setRelationId(relationId);
		command.setNewName("R1");
		Assert.assertEquals(false, command.doCommand().returnStatus());
		
		
		Assert.assertEquals("A relation is already named 'R1'", command.doCommand().explainResult());
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
		
		EditorCommand_AddClassToDomain addClassDummy = new EditorCommand_AddClassToDomain();
		addClassDummy.setClassId("DUMMY");
		history.performCommand(addClassDummy);
		
		EditorCommand_AddRelationBetweenClasses addRelation = new EditorCommand_AddRelationBetweenClasses();
		addRelation.setClassAId("IDUser");
		addRelation.setClassBId("IDTask");
		addRelation.setRelationUUID("relationId");
		history.performCommand(addRelation);
		
		EditorCommand_RenameRelation renameRelationCommand = new EditorCommand_RenameRelation();
		renameRelationCommand.setRelationId("relationId");
		renameRelationCommand.setNewName("R1");
		history.performCommand(renameRelationCommand);
		
		EditorCommandHistory history2 = TestHelper.roundTripEditorHistoryCommandHelper(history);
		Assert.assertEquals(true, history2.getDomain().hasRelationWithName("R1"));

	}
	
}
