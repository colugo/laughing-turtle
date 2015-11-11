package test.java.editorCommandTests;

import test.java.helper.TestHelper;
import test.java.helper.TestingUUID;
import junit.framework.Assert;
import junit.framework.TestCase;
import main.java.avii.editor.command.EditorCommand_AddClassToDomain;
import main.java.avii.editor.command.EditorCommand_AddRelationBetweenClasses;
import main.java.avii.editor.command.EditorCommand_DeleteRelation;
import main.java.avii.editor.command.history.EditorCommandHistory;
import main.java.avii.editor.metamodel.entities.EntityClass;
import main.java.avii.editor.metamodel.entities.EntityDomain;
import main.java.avii.editor.metamodel.entities.EntityRelation;

public class DeleteRelationCommandTests extends TestCase {
	public DeleteRelationCommandTests(String name) {
		super(name);
	}
	
	public void test_can_delete_relation()
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
		
		Assert.assertEquals(false, user.hasRelation("Relation_"+relationId));
		
		Assert.assertEquals(true, addRelation.doCommand().returnStatus());
		
		EntityRelation r1 = addRelation.getNewRelation();
		Assert.assertEquals("Relation_relationId", r1.getName());
		
		Assert.assertEquals(true, user.hasRelation("Relation_"+relationId));
		
		EditorCommand_DeleteRelation deleteRelation = new EditorCommand_DeleteRelation();
		deleteRelation.setDomain(domain);
		deleteRelation.setRelationId(relationId);
		
		Assert.assertEquals(true, deleteRelation.doCommand().returnStatus());
		
		Assert.assertEquals(false, user.hasRelation("Relation_"+relationId));
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
		
		EditorCommand_DeleteRelation deleteRelation = new EditorCommand_DeleteRelation();
		deleteRelation.setRelationId("relationId");
		history.performCommand(deleteRelation);
		
		EditorCommandHistory history2 = TestHelper.roundTripEditorHistoryCommandHelper(history);
		Assert.assertEquals(false, history2.getDomain().hasRelationWithId("relationId"));

	}
	
}
