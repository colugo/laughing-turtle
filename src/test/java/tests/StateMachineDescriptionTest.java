package test.java.tests;

import javax.naming.NameAlreadyBoundException;

import junit.framework.Assert;
import junit.framework.TestCase;
import main.java.avii.editor.metamodel.entities.EntityClass;
import main.java.avii.editor.metamodel.entities.EntityEventInstance;
import main.java.avii.editor.metamodel.entities.EntityEventSpecification;
import main.java.avii.editor.metamodel.entities.EntityState;

public class StateMachineDescriptionTest extends TestCase {

	public StateMachineDescriptionTest(String name){
		super(name);
	}
	
	public void test_can_describe_simple_state_machine() throws NameAlreadyBoundException
	{
		EntityClass klass = new EntityClass("Class");
		EntityState initial = new EntityState("Initial");
		klass.addState(initial);
	
		EntityState next = new EntityState("Next");
		klass.addState(next);
		
		EntityEventSpecification goToNext = new EntityEventSpecification(klass, "goToNext");
		EntityEventInstance goToNextInstance = new EntityEventInstance(goToNext, initial, next);
		klass.addEventInstance(goToNext, goToNextInstance);
		
		String expected = "Initial;\nNext;\nInitial->Next[label=\"goToNext\"];\n";
		Assert.assertEquals(expected, klass.describeStateMachine());
	}
	
}
