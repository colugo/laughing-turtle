package test.java.editorCommandTests;

import test.java.helper.TestHelper;
import test.java.helper.TestingUUID;
import junit.framework.Assert;
import junit.framework.TestCase;
import main.java.models.JsonClassDiagramCoordinatesHelper;
import main.java.models.JsonClassDiagramCoordinatesHelperEntityClass;
import main.java.models.JsonClassDiagramCoordinatesHelperEntityRelation;
import main.java.models.JsonClassDiagramCoordinatesHelperEntityState;
import main.java.avii.editor.command.EditorCommand_AddClassToDomain;
import main.java.avii.editor.command.EditorCommand_AddRelationBetweenClasses;
import main.java.avii.editor.command.EditorCommand_AddStateToClass;
import main.java.avii.editor.command.EditorCommand_ChangeClassDiagramCoordinates;
import main.java.avii.editor.command.history.EditorCommandHistory;
import main.java.avii.editor.metamodel.entities.EntityClass;
import main.java.avii.editor.metamodel.entities.EntityDomain;
import main.java.avii.editor.metamodel.entities.EntityRelation;
import main.java.avii.editor.metamodel.entities.EntityRelation.CardinalityType;
import main.java.avii.editor.metamodel.entities.EntityState;

public class ChangeClassDiagramCoordinatesCommandTests extends TestCase {
	public ChangeClassDiagramCoordinatesCommandTests(String name) {
		super(name);
	}
	
	public void test_can_add_relation_between_classes()
	{
		EntityDomain domain = new EntityDomain("TestDomain");
		
		EntityClass user = new EntityClass("User");
		domain.addClass(user);
		Assert.assertEquals("User", user.getId());
		
		
		EntityClass task = new EntityClass("Task");
		domain.addClass(task);
		Assert.assertEquals("Task", task.getId());
		
		EntityState state = new EntityState("State");
		task.addState(state);
		Assert.assertEquals("State", task.getStates().get(0).getId());
		
		EntityRelation r1 = new EntityRelation("R1");
		Assert.assertEquals("R1", r1.getId());
		r1.setEndA(user, CardinalityType.ONE_TO_MANY);
		r1.setEndB(task, CardinalityType.ONE_TO_MANY);
		r1.endAIndex = 0;
		r1.endBIndex = 0;
		
		JsonClassDiagramCoordinatesHelper coordHelper = new JsonClassDiagramCoordinatesHelper();
		coordHelper.classes.add( new JsonClassDiagramCoordinatesHelperEntityClass("User",10.0, 11.0, 4));
		coordHelper.classes.add( new JsonClassDiagramCoordinatesHelperEntityClass("Task",100.0, 110.0, 0));
		coordHelper.classes.get(1).states.add( new JsonClassDiagramCoordinatesHelperEntityState("State", 110.0, 120.0));
		coordHelper.relations.add( new JsonClassDiagramCoordinatesHelperEntityRelation("R1", 4, 10,1,2,3,4));
		
		EditorCommand_ChangeClassDiagramCoordinates coordCommand = new EditorCommand_ChangeClassDiagramCoordinates();
		coordCommand.setDomain(domain);
		coordCommand.setCoordinatesHelper(coordHelper);
		
		Assert.assertEquals(0.0, user.x);
		Assert.assertEquals(0.0, user.y);
		Assert.assertEquals(0.0, task.x);
		Assert.assertEquals(0.0, task.y);
		Assert.assertEquals(0, r1.endAIndex);
		Assert.assertEquals(0, r1.endBIndex);
		Assert.assertEquals(0, r1.verbAOffsetX);
		Assert.assertEquals(0, r1.verbAOffsetY);
		Assert.assertEquals(0, r1.verbBOffsetX);
		Assert.assertEquals(0, r1.verbBOffsetY);
		Assert.assertEquals(0, user.superClassTriangleIndex);
		Assert.assertEquals(0.0, task.getStates().get(0).x);
		Assert.assertEquals(0.0, task.getStates().get(0).y);
		
		Assert.assertEquals(true, coordCommand.doCommand().returnStatus());
		
		Assert.assertEquals(10.0, user.x);
		Assert.assertEquals(11.0, user.y);
		Assert.assertEquals(100.0, task.x);
		Assert.assertEquals(110.0, task.y);
		Assert.assertEquals(4, r1.endAIndex);
		Assert.assertEquals(10, r1.endBIndex);
		Assert.assertEquals(1, r1.verbAOffsetX);
		Assert.assertEquals(2, r1.verbAOffsetY);
		Assert.assertEquals(3, r1.verbBOffsetX);
		Assert.assertEquals(4, r1.verbBOffsetY);
		Assert.assertEquals(4, user.superClassTriangleIndex);
		Assert.assertEquals(110.0, task.getStates().get(0).x);
		Assert.assertEquals(120.0, task.getStates().get(0).y);
		
	}
	
	public void test_round_trip_serialisation() throws Exception
	{
		String domainName = "NewDomain";
		String classAId = "IDUser";
		String classBId = "IDTask";
		String stateId = "IDState";

		EditorCommandHistory history = new EditorCommandHistory(domainName);
		history.setUUID(new TestingUUID());
		
		EditorCommand_AddClassToDomain addClassA = new EditorCommand_AddClassToDomain();
		addClassA.setClassId(classAId);
		history.performCommand(addClassA);
		
		EditorCommand_AddClassToDomain addClassB = new EditorCommand_AddClassToDomain();
		addClassB.setClassId(classBId);
		history.performCommand(addClassB);
		
		EditorCommand_AddRelationBetweenClasses addRelation = new EditorCommand_AddRelationBetweenClasses();
		addRelation.setClassAId(classAId);
		addRelation.setClassBId(classBId);
		addRelation.setRelationUUID("R1");
		history.performCommand(addRelation);
		
		EditorCommand_AddStateToClass addState = new EditorCommand_AddStateToClass();
		addState.setClassId(classBId);
		addState.setStateId(stateId);
		history.performCommand(addState);
		
		
		JsonClassDiagramCoordinatesHelper coordHelper = new JsonClassDiagramCoordinatesHelper();
		coordHelper.classes.add( new JsonClassDiagramCoordinatesHelperEntityClass("IDUser",10.0, 11.0, 4));
		coordHelper.classes.add( new JsonClassDiagramCoordinatesHelperEntityClass("IDTask",100.0, 110.0, 0));
		coordHelper.classes.get(1).states.add( new JsonClassDiagramCoordinatesHelperEntityState("IDState", 110.0, 120.0));
		coordHelper.relations.add( new JsonClassDiagramCoordinatesHelperEntityRelation("R1", 4, 10, 1, 2, 3, 4));
		
		EditorCommand_ChangeClassDiagramCoordinates coordCommand = new EditorCommand_ChangeClassDiagramCoordinates();
		coordCommand.setCoordinatesHelper(coordHelper);
		history.performCommand(coordCommand);
		
		EditorCommandHistory history2 = TestHelper.roundTripEditorHistoryCommandHelper(history);
		Assert.assertEquals(history2.getDomain().getEntityClassWithId("IDUser").x, 10.0);
		Assert.assertEquals(history2.getDomain().getEntityClassWithId("IDUser").y, 11.0);
		Assert.assertEquals(history2.getDomain().getEntityClassWithId("IDUser").superClassTriangleIndex, 4);
		Assert.assertEquals(history2.getDomain().getEntityClassWithId("IDTask").x, 100.0);
		Assert.assertEquals(history2.getDomain().getEntityClassWithId("IDTask").y, 110.0);
		Assert.assertEquals(110.0, history2.getDomain().getEntityClassWithId("IDTask").getStates().get(0).x);
		Assert.assertEquals(120.0, history2.getDomain().getEntityClassWithId("IDTask").getStates().get(0).y);
		
		Assert.assertEquals(4, history2.getDomain().getRelationWithId("R1").endAIndex);
		Assert.assertEquals(10, history2.getDomain().getRelationWithId("R1").endBIndex);
		Assert.assertEquals(1, history2.getDomain().getRelationWithId("R1").verbAOffsetX);
		Assert.assertEquals(2, history2.getDomain().getRelationWithId("R1").verbAOffsetY);
		Assert.assertEquals(3, history2.getDomain().getRelationWithId("R1").verbBOffsetX);
		Assert.assertEquals(4, history2.getDomain().getRelationWithId("R1").verbBOffsetY);
	}

}
