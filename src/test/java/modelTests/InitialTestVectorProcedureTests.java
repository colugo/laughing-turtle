package test.java.modelTests;

import test.java.helper.TestHelper;
import main.java.avii.editor.contracts.metamodel.actionLanguage.syntax.InvalidActionLanguageSyntaxException;
import main.java.avii.editor.metamodel.actionLanguage.validation.EntityDomainValidator;
import main.java.avii.editor.metamodel.actionLanguage.validation.error.syntax.CouldNotIdentifyInstanceValidationError;
import main.java.avii.editor.metamodel.actionLanguage.validation.error.syntax.EventNotFoundInClassValidationError;
import main.java.avii.editor.metamodel.entities.EntityClass;
import main.java.avii.editor.metamodel.entities.EntityDomain;
import main.java.avii.scenario.AssertionTestVectorProcedure;
import main.java.avii.scenario.TestScenario;
import main.java.avii.scenario.TestVector;
import main.java.avii.scenario.TestVectorClassTable;
import main.java.avii.scenario.TestVectorProcedure;
import junit.framework.Assert;
import junit.framework.TestCase;

public class InitialTestVectorProcedureTests extends TestCase {
	public InitialTestVectorProcedureTests(String name) {
		super(name);
	}
	
	public void test_can_add_generate_events_action_language_to_initial_test_vector() throws InvalidActionLanguageSyntaxException
	{
		EntityDomain domain = new EntityDomain("TestDomain");
		EntityClass klass = new EntityClass("Klass");
		domain.addClass(klass);
		
		TestScenario scenario = new TestScenario();
		TestVector vector = new TestVector();
		
		domain.addScenario(scenario);
		scenario.addVector(vector);
		
		TestVectorClassTable table = new TestVectorClassTable(klass);
		table.createInstance();
		vector.addClassTable(table);
		
		vector.createInitialProcedureFromTables();
		String initialGenerateActionLanguage = "";
		initialGenerateActionLanguage += "GENERATE event1() TO Klass_001;\n";
		vector.addInitialGenerateLanguage(initialGenerateActionLanguage);
		
		
		TestVectorProcedure proc = vector.getInitialProcedure();
		String rawProc = proc.getRawText();
		Assert.assertEquals("CREATE Klass_001 FROM Klass;\n#HOOK FOR STATE CHANGE\nGENERATE event1() TO Klass_001;\n", rawProc);
		Assert.assertEquals(false, proc.validate());
		Assert.assertEquals(1, proc.getValidationErrors().size());
		Assert.assertTrue(TestHelper.checkValidationResultsForAnErrorOfType(proc, EventNotFoundInClassValidationError.class));
	}
	
	public void test_validate_initial_procedures_for_test_scenarios_and_vectors() throws InvalidActionLanguageSyntaxException
	{
		EntityDomain domain = new EntityDomain("TestDomain");
		EntityClass klass = new EntityClass("Klass");
		domain.addClass(klass);
		
		TestScenario scenario = new TestScenario();
		TestVector vector = new TestVector();
		
		domain.addScenario(scenario);
		scenario.addVector(vector);
		
		TestVectorClassTable table = new TestVectorClassTable(klass);
		table.createInstance();
		vector.addClassTable(table);
		
		vector.createInitialProcedureFromTables();
		String initialGenerateActionLanguage = "";
		initialGenerateActionLanguage += "GENERATE event1() TO Klass_001;\n";
		vector.addInitialGenerateLanguage(initialGenerateActionLanguage);
		
		EntityDomainValidator validator = new EntityDomainValidator(domain);
	
		Assert.assertEquals(false, validator.validate());
		Assert.assertEquals(1, validator.getValidationErrors().size());
		Assert.assertTrue(TestHelper.checkValidationResultsForAnErrorOfType(validator, EventNotFoundInClassValidationError.class));
	}
	
	public void test_validate_assertion_procedures_for_test_scenarios_and_vectors() throws InvalidActionLanguageSyntaxException
	{
		EntityDomain domain = new EntityDomain("TestDomain");
		EntityClass klass = new EntityClass("Klass");
		domain.addClass(klass);
		
		TestScenario scenario = new TestScenario();
		TestVector vector = new TestVector();
		
		domain.addScenario(scenario);
		scenario.addVector(vector);
		
		TestVectorClassTable table = new TestVectorClassTable(klass);
		table.createInstance();
		vector.addClassTable(table);
		
		vector.createInitialProcedureFromTables();

		AssertionTestVectorProcedure assertProcedure = new AssertionTestVectorProcedure();
		assertProcedure.setVector(vector);
		assertProcedure.setProcedure("fred.Age = 2;\n");
		
		
		EntityDomainValidator validator = new EntityDomainValidator(domain);
		
		Assert.assertEquals(false, validator.validate());
		Assert.assertEquals(1, validator.getValidationErrors().size());
		Assert.assertTrue(TestHelper.checkValidationResultsForAnErrorOfType(validator, CouldNotIdentifyInstanceValidationError.class));
	}
}
