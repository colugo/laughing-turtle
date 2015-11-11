package test.java.editorCommandTests;

import test.java.helper.TestHelper;
import test.java.helper.TestingUUID;
import junit.framework.Assert;
import junit.framework.TestCase;
import main.java.avii.editor.command.EditorCommand_AddClassToDomain;
import main.java.avii.editor.command.EditorCommand_AddRelationBetweenClasses;
import main.java.avii.editor.command.EditorCommand_CreateAssociation;
import main.java.avii.editor.command.history.EditorCommandHistory;
import main.java.avii.editor.metamodel.entities.EntityClass;
import main.java.avii.editor.metamodel.entities.EntityDomain;
import main.java.avii.editor.metamodel.entities.EntityRelation;
import main.java.avii.editor.metamodel.entities.EntityRelation.CardinalityType;

public class CreateAssociationCommandTests extends TestCase {
	public CreateAssociationCommandTests(String name) {
		super(name);
	}
	
	public void test_can_create_association()
	{
		EntityDomain domain = new EntityDomain("TestDomain");
		EntityClass user = new EntityClass("User");
		domain.addClass(user);
		EntityClass task = new EntityClass("Task");
		domain.addClass(task);
		
		EntityRelation r1 = new EntityRelation("R1");
		r1.setId("relation_id");
		r1.setEndA(user, CardinalityType.ONE_TO_MANY);
		r1.setEndB(user, CardinalityType.ONE_TO_MANY);
		
		Assert.assertEquals(true, domain.hasEntityClassWithId(user.getId()));
		Assert.assertEquals(true, domain.hasEntityClassWithId(task.getId()));
		Assert.assertEquals(true, domain.hasRelationWithId(r1.getId()));
		
		Assert.assertEquals(false, r1.hasAssociation());
		Assert.assertEquals(false, task.isAssociation());
		
		EditorCommand_CreateAssociation createAssociation = new EditorCommand_CreateAssociation();
		createAssociation.setDomain(domain);
		createAssociation.setRelationId(r1.getId());
		createAssociation.setAssociationClassId(task.getId());

		Assert.assertEquals(true, createAssociation.doCommand().returnStatus());
		Assert.assertEquals(true, r1.hasAssociation());
		Assert.assertEquals(true, task.isAssociation());		
	}

	public void test_can_delete_association()
	{
		EntityDomain domain = new EntityDomain("TestDomain");
		EntityClass user = new EntityClass("User");
		domain.addClass(user);
		EntityClass task = new EntityClass("Task");
		domain.addClass(task);
		
		EntityRelation r1 = new EntityRelation("R1");
		r1.setId("relation_id");
		r1.setEndA(user, CardinalityType.ONE_TO_MANY);
		r1.setEndB(user, CardinalityType.ONE_TO_MANY);
		r1.setAssociation(task);
		
		Assert.assertEquals(true, domain.hasEntityClassWithId(user.getId()));
		Assert.assertEquals(true, domain.hasEntityClassWithId(task.getId()));
		Assert.assertEquals(true, domain.hasRelationWithId(r1.getId()));
		
		Assert.assertEquals(true, r1.hasAssociation());
		Assert.assertEquals(true, task.isAssociation());
		
		EditorCommand_CreateAssociation createAssociation = new EditorCommand_CreateAssociation();
		createAssociation.setDomain(domain);
		createAssociation.setRelationId(r1.getId());
		createAssociation.setAssociationClassId(null);

		Assert.assertEquals(true, createAssociation.doCommand().returnStatus());
		Assert.assertEquals(false, r1.hasAssociation());
		Assert.assertEquals(false, task.isAssociation());		
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
		addRelation.setClassBId("IDUser");
		addRelation.setRelationUUID("relationId");
		history.performCommand(addRelation);
		
		EditorCommand_CreateAssociation createAssociation = new EditorCommand_CreateAssociation();
		createAssociation.setRelationId("relationId");
		createAssociation.setAssociationClassId("IDTask");
		history.performCommand(createAssociation);

		EditorCommandHistory history2 = TestHelper.roundTripEditorHistoryCommandHelper(history);
		Assert.assertEquals("IDTask", history2.getDomain().getRelationWithId("relationId").getAssociation().getId());
	}
	
	public void test_round_trip_serialisation_delete_association() throws Exception
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
		addRelation.setClassBId("IDUser");
		addRelation.setRelationUUID("relationId");
		history.performCommand(addRelation);
		
		EditorCommand_CreateAssociation createAssociation = new EditorCommand_CreateAssociation();
		createAssociation.setRelationId("relationId");
		createAssociation.setAssociationClassId("IDTask");
		history.performCommand(createAssociation);
		
		EditorCommand_CreateAssociation createAssociation2 = new EditorCommand_CreateAssociation();
		createAssociation2.setRelationId("relationId");
		createAssociation2.setAssociationClassId(null);
		history.performCommand(createAssociation2);

		EditorCommandHistory history2 = TestHelper.roundTripEditorHistoryCommandHelper(history);
		Assert.assertEquals(false, history2.getDomain().getRelationWithId("relationId").hasAssociation());
	}
	
}
