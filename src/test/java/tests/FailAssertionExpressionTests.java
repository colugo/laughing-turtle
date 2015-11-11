package test.java.tests;

import test.java.helper.DomainTTD;
import test.java.helper.TestHelper;

import javax.naming.NameAlreadyBoundException;

import junit.framework.Assert;
import junit.framework.TestCase;
import main.java.avii.editor.contracts.metamodel.actionLanguage.syntax.InvalidActionLanguageSyntaxException;
import main.java.avii.editor.metamodel.entities.EntityDomain;
import main.java.avii.scenario.AssertionTestVectorProcedure;
import main.java.avii.scenario.TestScenario;
import main.java.avii.scenario.TestVector;

public class FailAssertionExpressionTests extends TestCase {
	public FailAssertionExpressionTests(String name)
	{
		super(name);
	}
	
	
	public void test_can_validate_passing_fail_assertion() throws InvalidActionLanguageSyntaxException, NameAlreadyBoundException
	{
		String proc = "FAIL \"this was wrong\";\n";
		
		EntityDomain ttdDomain = DomainTTD.getTTDDomain();
		TestScenario scenario = new TestScenario();
		ttdDomain.addScenario(scenario);
		TestVector vector = new TestVector();
		scenario.addVector(vector);
		
		AssertionTestVectorProcedure procedure = new AssertionTestVectorProcedure();
		procedure.setVector(vector);
		
		procedure.setProcedure(proc);
		
		Assert.assertTrue(procedure.validate());
	}
	
	public void test_can_validate_invalid_fail_assertion() throws InvalidActionLanguageSyntaxException, NameAlreadyBoundException
	{
		String proc = "FAIL \"this was wrong ;\n";
		
		EntityDomain ttdDomain = DomainTTD.getTTDDomain();
		TestScenario scenario = new TestScenario();
		ttdDomain.addScenario(scenario);
		TestVector vector = new TestVector();
		scenario.addVector(vector);
		
		AssertionTestVectorProcedure procedure = new AssertionTestVectorProcedure();
		procedure.setVector(vector);
		
		procedure.setProcedure(proc);

		Assert.assertTrue(!procedure.validate());
		Assert.assertEquals(2, TestHelper.countValidationErrors(procedure));
	}
	
	public void test_can_use_temp_value_in_assertion() throws InvalidActionLanguageSyntaxException, NameAlreadyBoundException
	{
		String proc = "";
		proc += "reason = \"this was wrong\";\n";
		proc += "FAIL reason;\n";
		
		EntityDomain ttdDomain = DomainTTD.getTTDDomain();
		TestScenario scenario = new TestScenario();
		ttdDomain.addScenario(scenario);
		TestVector vector = new TestVector();
		scenario.addVector(vector);
		
		AssertionTestVectorProcedure procedure = new AssertionTestVectorProcedure();
		procedure.setVector(vector);
		
		procedure.setProcedure(proc);
		
		Assert.assertTrue(procedure.validate());
		
		Assert.assertTrue(procedure.validate());
	}
	
}
