package test.java.simulatorTests;

import test.java.helper.TestHelper;

import javax.naming.NameAlreadyBoundException;

import main.java.avii.editor.contracts.metamodel.actionLanguage.syntax.InvalidActionLanguageSyntaxException;
import main.java.avii.editor.metamodel.entities.EntityClass;
import main.java.avii.editor.metamodel.entities.EntityDomain;
import main.java.avii.editor.metamodel.entities.EntityProcedure;
import main.java.avii.editor.metamodel.entities.EntityState;
import main.java.avii.simulator.Simulator;
import main.java.avii.simulator.exceptions.CannotSimulateDomainThatIsInvalidException;
import junit.framework.Assert;
import junit.framework.TestCase;

public class SimulatedReturnTests extends TestCase {
	public SimulatedReturnTests(String name)
	{
		super(name);
	}
	
	public void test_can_return_based_on_if() throws NameAlreadyBoundException, InvalidActionLanguageSyntaxException, CannotSimulateDomainThatIsInvalidException
	{
		EntityDomain testDomain = new EntityDomain("testDomain");
		EntityClass user = new EntityClass("User");
		testDomain.addClass(user);
		EntityClass task = new EntityClass("Task");
		testDomain.addClass(task);
		
		EntityState initialUserState = new EntityState("initial");
		user.addState(initialUserState);
		EntityProcedure initialUserProcedure = new EntityProcedure(initialUserState);
		
		String procedureText = "";
		procedureText += "flag = 0;\n";
		procedureText += "IF true THEN\n";
		procedureText += "	flag = 1;\n";
		procedureText += "	RETURN;\n";
		procedureText += "END IF;\n";
		procedureText += "flag = 6;\n";
		
		initialUserProcedure.setProcedure(procedureText);

		TestHelper.addNewInitialStateToClassWithEventToPreviousInitialState(user);
		
		Simulator simulator = new Simulator(testDomain);
		simulator.setSimulatingState(initialUserState);

		simulator.getSimulatingState().simulate();
		
		double flagValue = (Double) simulator.getSimulatingState().getTempVariable("flag");
		Assert.assertEquals(1.0, flagValue);
	}
	
}
