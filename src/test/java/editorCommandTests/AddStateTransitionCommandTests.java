package test.java.editorCommandTests;

import test.java.helper.TestHelper;
import test.java.helper.TestingUUID;
import junit.framework.Assert;
import junit.framework.TestCase;
import main.java.avii.editor.command.EditorCommand_AddClassToDomain;
import main.java.avii.editor.command.EditorCommand_AddStateToClass;
import main.java.avii.editor.command.EditorCommand_AddStateTransition;
import main.java.avii.editor.command.history.EditorCommandHistory;
import main.java.avii.editor.metamodel.entities.EntityClass;
import main.java.avii.editor.metamodel.entities.EntityDomain;
import main.java.avii.editor.metamodel.entities.EntityEventSpecification;
import main.java.avii.editor.metamodel.entities.EntityState;

public class AddStateTransitionCommandTests extends TestCase {
	public AddStateTransitionCommandTests(String name) {
		super(name);
	}
	
	public void test_can_add_state_transition()
	{
		String classId = "User";
		String fromStateId = "FromState";
		String toStateId = "ToState";
		
		EntityDomain domain = new EntityDomain("TestDomain");

		
		EditorCommand_AddClassToDomain addClass = new EditorCommand_AddClassToDomain();
		addClass.setDomain(domain);
		addClass.setClassId(classId);
		Assert.assertEquals(true, addClass.doCommand().returnStatus());
		
		EditorCommand_AddStateToClass addFromState = new EditorCommand_AddStateToClass();
		addFromState.setDomain(domain);
		addFromState.setStateId(fromStateId);
		addFromState.setClassId(classId);
		Assert.assertEquals(true, addFromState.doCommand().returnStatus());
		
		EditorCommand_AddStateToClass addToState = new EditorCommand_AddStateToClass();
		addToState.setDomain(domain);
		addToState.setStateId(toStateId);
		addToState.setClassId(classId);
		Assert.assertEquals(true, addToState.doCommand().returnStatus());

		EntityClass theClass = domain.getEntityClassWithId(classId);
		Assert.assertEquals(theClass.getEventSpecifications().size(), 1);
		EntityEventSpecification eventSpec = theClass.getEventSpecifications().get(0);
		
		Assert.assertEquals(true, theClass.hasStateWithId(fromStateId));
		Assert.assertEquals(true, theClass.hasStateWithId(toStateId));
		EntityState fromState = theClass.getStateWithId(fromStateId);
		EntityState toState = theClass.getStateWithId(toStateId);
		
		Assert.assertEquals(null, fromState.getNextStateForEventSpecification(eventSpec));
		
		
		EditorCommand_AddStateTransition addTransition = new EditorCommand_AddStateTransition();
		addTransition.setDomain(domain);
		addTransition.setClassId(classId);
		addTransition.setFromStateId(fromStateId);
		addTransition.setToStateId(toStateId);
		Assert.assertEquals(true, addTransition.doCommand().returnStatus());
		
		Assert.assertEquals(toState, fromState.getNextStateForEventSpecification(eventSpec));
	
	}
	
	public void test_round_trip_serialisation() throws Exception
	{
		String classId = "User";
		String fromStateId = "FromState";
		String toStateId = "ToState";
		String transitionId = "TransitionId";
		
		EditorCommandHistory history = new EditorCommandHistory("TestDomain");
		history.setUUID(new TestingUUID());
		
		EditorCommand_AddClassToDomain addClass = new EditorCommand_AddClassToDomain();
		addClass.setClassId(classId);
		history.performCommand(addClass);
		
		EditorCommand_AddStateToClass addFromState = new EditorCommand_AddStateToClass();
		addFromState.setStateId(fromStateId);
		addFromState.setClassId(classId);
		history.performCommand(addFromState);
		
		EditorCommand_AddStateToClass addToState = new EditorCommand_AddStateToClass();
		addToState.setStateId(toStateId);
		addToState.setClassId(classId);
		history.performCommand(addToState);
		
		EditorCommand_AddStateTransition addTransition = new EditorCommand_AddStateTransition();
		addTransition.setClassId(classId);
		addTransition.setFromStateId(fromStateId);
		addTransition.setToStateId(toStateId);
		addTransition.setInstanceId(transitionId);
		history.performCommand(addTransition);
		
		EditorCommandHistory history2 = TestHelper.roundTripEditorHistoryCommandHelper(history);
		EntityState toState = history2.getDomain().getEntityClassWithId(classId).getStateWithId(toStateId);
		EntityState fromState = history2.getDomain().getEntityClassWithId(classId).getStateWithId(fromStateId);
		EntityEventSpecification defaultSpec = history2.getDomain().getEntityClassWithId(classId).getDefaultEventSpecification();

		Assert.assertEquals(toState, fromState.getNextStateForEventSpecification(defaultSpec));
	}
	
}
