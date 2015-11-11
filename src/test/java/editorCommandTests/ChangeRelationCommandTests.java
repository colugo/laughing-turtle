package test.java.editorCommandTests;

import test.java.helper.TestHelper;
import test.java.helper.TestingUUID;
import junit.framework.Assert;
import junit.framework.TestCase;
import main.java.avii.editor.command.EditorCommand_AddClassToDomain;
import main.java.avii.editor.command.EditorCommand_AddRelationBetweenClasses;
import main.java.avii.editor.command.EditorCommand_ChangeRelation;
import main.java.avii.editor.command.history.EditorCommandHistory;
import main.java.avii.editor.metamodel.entities.EntityClass;
import main.java.avii.editor.metamodel.entities.EntityDomain;
import main.java.avii.editor.metamodel.entities.EntityRelation;
import main.java.avii.editor.metamodel.entities.EntityRelation.CardinalityType;

public class ChangeRelationCommandTests extends TestCase {
	public ChangeRelationCommandTests(String name) {
		super(name);
	}
	
	public void test_can_change_class_a_in_relation()
	{
		String relationId = "relationId";
		EntityDomain domain = new EntityDomain("TestDomain");
		EntityClass user = new EntityClass("User");
		domain.addClass(user);
		EntityClass task = new EntityClass("Task");
		domain.addClass(task);
		EntityClass dummy = new EntityClass("Dummy");
		domain.addClass(dummy);
				
		EditorCommand_AddRelationBetweenClasses addRelation = new EditorCommand_AddRelationBetweenClasses();
		addRelation.setDomain(domain);
		addRelation.setClassAId("User");
		addRelation.setClassBId("Task");
		addRelation.setRelationUUID(relationId);
		
		Assert.assertEquals(true, addRelation.doCommand().returnStatus());
		
		EntityRelation r1 = addRelation.getNewRelation();
		Assert.assertEquals("Relation_relationId", r1.getName());
		Assert.assertEquals(user, r1.getClassA());
		Assert.assertEquals("leads", r1.getClassAVerb());
		Assert.assertEquals(CardinalityType.ZERO_TO_MANY, r1.getCardinalityB());
		Assert.assertEquals(task, r1.getClassB());
		Assert.assertEquals("follows", r1.getClassBVerb());
		Assert.assertEquals(CardinalityType.ZERO_TO_MANY, r1.getCardinalityB());
		
		EditorCommand_ChangeRelation command = new EditorCommand_ChangeRelation();
		command.setDomain(domain);
		command.setRelationId(relationId);
		command.setClassAId(dummy.getId());
		command.setClassAVerb(r1.getClassAVerb());
		command.setClassACardinality(CardinalityType.toHuman(r1.getCardinalityA()));
		command.setClassBId(r1.getClassB().getId());
		command.setClassBVerb(r1.getClassBVerb());
		command.setClassBCardinality(CardinalityType.toHuman(r1.getCardinalityB()));

		Assert.assertEquals(true, command.doCommand().returnStatus());
		
		Assert.assertEquals("Relation_relationId", r1.getName());
		Assert.assertEquals(dummy, r1.getClassA());
		Assert.assertEquals("leads", r1.getClassAVerb());
		Assert.assertEquals(CardinalityType.ZERO_TO_MANY, r1.getCardinalityB());
		Assert.assertEquals(task, r1.getClassB());
		Assert.assertEquals("follows", r1.getClassBVerb());
		Assert.assertEquals(CardinalityType.ZERO_TO_MANY, r1.getCardinalityB());
	}
	
	public void test_can_change_class_b_in_relation()
	{
		String relationId = "relationId";
		EntityDomain domain = new EntityDomain("TestDomain");
		EntityClass user = new EntityClass("User");
		domain.addClass(user);
		EntityClass task = new EntityClass("Task");
		domain.addClass(task);
		EntityClass dummy = new EntityClass("Dummy");
		domain.addClass(dummy);
				
		EditorCommand_AddRelationBetweenClasses addRelation = new EditorCommand_AddRelationBetweenClasses();
		addRelation.setDomain(domain);
		addRelation.setClassAId("User");
		addRelation.setClassBId("Task");
		addRelation.setRelationUUID(relationId);
		
		Assert.assertEquals(true, addRelation.doCommand().returnStatus());
		
		EntityRelation r1 = addRelation.getNewRelation();
		Assert.assertEquals("Relation_relationId", r1.getName());
		Assert.assertEquals(user, r1.getClassA());
		Assert.assertEquals("leads", r1.getClassAVerb());
		Assert.assertEquals(CardinalityType.ZERO_TO_MANY, r1.getCardinalityB());
		Assert.assertEquals(task, r1.getClassB());
		Assert.assertEquals("follows", r1.getClassBVerb());
		Assert.assertEquals(CardinalityType.ZERO_TO_MANY, r1.getCardinalityB());
		
		EditorCommand_ChangeRelation command = new EditorCommand_ChangeRelation();
		command.setDomain(domain);
		command.setRelationId(relationId);
		command.setClassAId(r1.getClassA().getId());
		command.setClassAVerb(r1.getClassAVerb());
		command.setClassACardinality(CardinalityType.toHuman(r1.getCardinalityA()));
		command.setClassBId(dummy.getId());
		command.setClassBVerb(r1.getClassBVerb());
		command.setClassBCardinality(CardinalityType.toHuman(r1.getCardinalityB()));

		Assert.assertEquals(true, command.doCommand().returnStatus());
		
		Assert.assertEquals("Relation_relationId", r1.getName());
		Assert.assertEquals(user, r1.getClassA());
		Assert.assertEquals("leads", r1.getClassAVerb());
		Assert.assertEquals(CardinalityType.ZERO_TO_MANY, r1.getCardinalityB());
		Assert.assertEquals(dummy, r1.getClassB());
		Assert.assertEquals("follows", r1.getClassBVerb());
		Assert.assertEquals(CardinalityType.ZERO_TO_MANY, r1.getCardinalityB());
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
		
		EditorCommand_ChangeRelation changeRelationCommand = new EditorCommand_ChangeRelation();
		changeRelationCommand.setRelationId("relationId");
		changeRelationCommand.setClassAId("DUMMY");
		changeRelationCommand.setClassAVerb("leads");
		changeRelationCommand.setClassACardinality("0..*");
		changeRelationCommand.setClassBId("IDTask");
		changeRelationCommand.setClassBVerb("follows");
		changeRelationCommand.setClassBCardinality("0..*");
		history.performCommand(changeRelationCommand);
		
		EditorCommandHistory history2 = TestHelper.roundTripEditorHistoryCommandHelper(history);
		Assert.assertEquals(true, history2.getDomain().getRelationWithName("Relation_relationId").getClassA().getId().equals("DUMMY"));
		Assert.assertEquals(true, history2.getDomain().getRelationWithName("Relation_relationId").getClassB().getId().equals(classBId));
	}
	
}
