package test.java.tests;

import test.java.helper.TestHelper;

import javax.naming.NameAlreadyBoundException;

import junit.framework.Assert;
import junit.framework.TestCase;
import main.java.avii.editor.contracts.metamodel.actionLanguage.syntax.InvalidActionLanguageSyntaxException;
import main.java.avii.editor.metamodel.actionLanguage.validation.error.syntax.CodeCannotFollowReturnStatementValidationError;
import main.java.avii.editor.metamodel.entities.EntityClass;
import main.java.avii.editor.metamodel.entities.EntityDomain;
import main.java.avii.editor.metamodel.entities.EntityProcedure;
import main.java.avii.editor.metamodel.entities.EntityState;

public class ReturnTests extends TestCase {

	public ReturnTests(String name)
	{
		super(name);
	}
	
	public void test_return_as_only_procedure_is_valid() throws NameAlreadyBoundException, InvalidActionLanguageSyntaxException
	{
		String proc = "";
		proc += "RETURN;\n";
		
		EntityDomain microwave = ValidationTests.getMicrowaveDomain();
		EntityClass heater = microwave.getEntityClassWithName("Heater");
		
		EntityState state = new EntityState("Initial");
		
		heater.addState(state);
		
		EntityProcedure procedure = new EntityProcedure(state);
		procedure.setProcedure(proc);

		Assert.assertTrue(procedure.validate());
		Assert.assertEquals(0, TestHelper.countValidationErrors(procedure));
	}
	
	
	public void test_code_cannot_follow_return() throws NameAlreadyBoundException, InvalidActionLanguageSyntaxException
	{
		String proc = "";
		proc += "RETURN;\n";
		proc += "temp = 5;\n";
		
		EntityDomain microwave = ValidationTests.getMicrowaveDomain();
		EntityClass heater = microwave.getEntityClassWithName("Heater");
		
		EntityState state = new EntityState("Initial");
		
		heater.addState(state);
		
		EntityProcedure procedure = new EntityProcedure(state);
		procedure.setProcedure(proc);

		Assert.assertTrue(!procedure.validate());
		Assert.assertEquals(1, TestHelper.countValidationErrors(procedure));
		Assert.assertTrue(TestHelper.checkValidationResultsForAnErrorOfType(procedure, CodeCannotFollowReturnStatementValidationError.class));
	}
	
	
	public void test_return_is_valid_as_end_of_if_block() throws NameAlreadyBoundException, InvalidActionLanguageSyntaxException
	{
		String proc = "";
		proc += "IF 1 == 1 THEN\n";
		proc += "	RETURN;\n";
		proc += "END IF;\n";
		proc += "temp = 5;\n";
		
		EntityDomain microwave = ValidationTests.getMicrowaveDomain();
		EntityClass heater = microwave.getEntityClassWithName("Heater");
		
		EntityState state = new EntityState("Initial");
		
		heater.addState(state);
		
		EntityProcedure procedure = new EntityProcedure(state);
		procedure.setProcedure(proc);

		Assert.assertTrue(procedure.validate());
		Assert.assertEquals(0, TestHelper.countValidationErrors(procedure));
	}
	
	public void test_return_is_valid_as_end_of_else_block() throws NameAlreadyBoundException, InvalidActionLanguageSyntaxException
	{
		String proc = "";
		proc += "IF 1 == 1 THEN\n";
		proc += "	temp = 2;\n";
		proc += "ELSE\n";
		proc += "	RETURN;\n";
		proc += "END IF;\n";
		proc += "temp = 5;\n";
		
		EntityDomain microwave = ValidationTests.getMicrowaveDomain();
		EntityClass heater = microwave.getEntityClassWithName("Heater");
		
		EntityState state = new EntityState("Initial");
		
		heater.addState(state);
		
		EntityProcedure procedure = new EntityProcedure(state);
		procedure.setProcedure(proc);

		Assert.assertTrue(procedure.validate());
		Assert.assertEquals(0, TestHelper.countValidationErrors(procedure));
	}
	
}
