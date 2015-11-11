package test.java.tests;

import test.java.helper.DomainTTD;
import test.java.helper.TestHelper;
import junit.framework.Assert;
import junit.framework.TestCase;
import main.java.avii.editor.contracts.metamodel.actionLanguage.syntax.InvalidActionLanguageSyntaxException;
import main.java.avii.editor.metamodel.actionLanguage.validation.error.syntax.CouldNotIdentifyInstanceValidationError;
import main.java.avii.editor.metamodel.actionLanguage.validation.error.syntax.EntityRelationDoesNotConnectClassesValidationError;
import main.java.avii.editor.metamodel.actionLanguage.validation.error.syntax.EntityRelationNotFoundInDomainValidationError;
import main.java.avii.editor.metamodel.entities.EntityClass;
import main.java.avii.editor.metamodel.entities.EntityDomain;
import main.java.avii.editor.metamodel.entities.EntityProcedure;
import main.java.avii.editor.metamodel.entities.EntityState;

public class UnrelateTests extends TestCase {

	public UnrelateTests(String name) {
		super(name);
	}
	///////////////// SELECT ONE ////////////////////////
	
	public void test_can_unrelate_over_relationship_with_association() throws InvalidActionLanguageSyntaxException
	{
		String proc = "";
		proc += "CREATE user FROM User;\n";
		proc += "CREATE task FROM Task;\n";
		proc += "UNRELATE user FROM task ACROSS R1;\n";

		EntityDomain ttdd = DomainTTD.getTTDDomain();
		EntityClass user = ttdd.getEntityClassWithName("Task");
		EntityState state = new EntityState("Initial");
		user.addState(state);
		EntityProcedure procedure = new EntityProcedure(state);
		procedure.setProcedure(proc);

		Assert.assertTrue(procedure.validate());
		Assert.assertEquals(0, TestHelper.countValidationErrors(procedure));
	}
	
	public void test_can_unrelate_over_relationship_with_association_and_instances_flipped() throws InvalidActionLanguageSyntaxException
	{
		String proc = "";
		proc += "CREATE user FROM User;\n";
		proc += "CREATE task FROM Task;\n";
		proc += "UNRELATE task FROM user ACROSS R1;\n";

		EntityDomain ttdd = DomainTTD.getTTDDomain();
		EntityClass user = ttdd.getEntityClassWithName("Task");
		EntityState state = new EntityState("Initial");
		user.addState(state);
		EntityProcedure procedure = new EntityProcedure(state);
		procedure.setProcedure(proc);

		Assert.assertTrue(procedure.validate());
		Assert.assertEquals(0, TestHelper.countValidationErrors(procedure));
	}
	
	public void test_can_unrelate_over_relationship() throws InvalidActionLanguageSyntaxException
	{
		String proc = "";
		proc += "CREATE step FROM Step;\n";
		proc += "CREATE task FROM Task;\n";
		proc += "UNRELATE step FROM task ACROSS R2;\n";

		EntityDomain ttdd = DomainTTD.getTTDDomain();
		EntityClass user = ttdd.getEntityClassWithName("Task");
		EntityState state = new EntityState("Initial");
		user.addState(state);
		EntityProcedure procedure = new EntityProcedure(state);
		procedure.setProcedure(proc);

		Assert.assertTrue(procedure.validate());
		Assert.assertEquals(0, TestHelper.countValidationErrors(procedure));
	}
	
	public void test_can_unrelate_over_reflexive_relationship_with_association() throws InvalidActionLanguageSyntaxException
	{
		String proc = "";
		proc += "CREATE step1 FROM Step;\n";
		proc += "CREATE step2 FROM Step;\n";
		proc += "UNRELATE step1 FROM step2 ACROSS R3;\n";

		EntityDomain ttdd = DomainTTD.getTTDDomain();
		EntityClass user = ttdd.getEntityClassWithName("Task");
		EntityState state = new EntityState("Initial");
		user.addState(state);
		EntityProcedure procedure = new EntityProcedure(state);
		procedure.setProcedure(proc);

		Assert.assertTrue(procedure.validate());
		Assert.assertEquals(0, TestHelper.countValidationErrors(procedure));
	}
	
	public void test_can_unrelate_over_reflexive_relationship_without_association() throws InvalidActionLanguageSyntaxException
	{
		String proc = "";
		proc += "CREATE task1 FROM Task;\n";
		proc += "CREATE task2 FROM Task;\n";
		proc += "UNRELATE task1 FROM task2 ACROSS R4;\n";

		EntityDomain ttdd = DomainTTD.getTTDDomain();
		EntityClass user = ttdd.getEntityClassWithName("Task");
		EntityState state = new EntityState("Initial");
		user.addState(state);
		EntityProcedure procedure = new EntityProcedure(state);
		procedure.setProcedure(proc);

		Assert.assertTrue(procedure.validate());
		Assert.assertEquals(0, TestHelper.countValidationErrors(procedure));
	}
	
	public void test_cant_unrelate_if_left_instance_cant_be_identified() throws InvalidActionLanguageSyntaxException
	{
		String proc = "";
		proc += "CREATE user FROM User;\n";
		proc += "CREATE task FROM Task;\n";
		proc += "UNRELATE notAnInstance FROM task ACROSS R1;\n";

		EntityDomain ttdd = DomainTTD.getTTDDomain();
		EntityClass user = ttdd.getEntityClassWithName("Task");
		EntityState state = new EntityState("Initial");
		user.addState(state);
		EntityProcedure procedure = new EntityProcedure(state);
		procedure.setProcedure(proc);

		Assert.assertTrue(!procedure.validate());
		Assert.assertEquals(1, TestHelper.countValidationErrors(procedure));
		Assert.assertTrue(TestHelper.checkValidationResultsForAnErrorOfType(procedure, CouldNotIdentifyInstanceValidationError.class));
	}
	
	public void test_cant_unrelate_if_right_instance_cant_be_identified() throws InvalidActionLanguageSyntaxException
	{
		String proc = "";
		proc += "CREATE user FROM User;\n";
		proc += "CREATE task FROM Task;\n";
		proc += "UNRELATE task FROM notAnInstance ACROSS R1;\n";

		EntityDomain ttdd = DomainTTD.getTTDDomain();
		EntityClass user = ttdd.getEntityClassWithName("Task");
		EntityState state = new EntityState("Initial");
		user.addState(state);
		EntityProcedure procedure = new EntityProcedure(state);
		procedure.setProcedure(proc);

		Assert.assertTrue(!procedure.validate());
		Assert.assertEquals(1, TestHelper.countValidationErrors(procedure));
		Assert.assertTrue(TestHelper.checkValidationResultsForAnErrorOfType(procedure, CouldNotIdentifyInstanceValidationError.class));
	}
	
	public void test_cant_unrelate_if_there_is_no_relationship_between_instances() throws InvalidActionLanguageSyntaxException
	{
		String proc = "";
		proc += "CREATE user FROM User;\n";
		proc += "CREATE task FROM Task;\n";
		proc += "UNRELATE task FROM user ACROSS R2;\n";

		EntityDomain ttdd = DomainTTD.getTTDDomain();
		EntityClass user = ttdd.getEntityClassWithName("Task");
		EntityState state = new EntityState("Initial");
		user.addState(state);
		
		EntityProcedure procedure = new EntityProcedure(state);
		procedure.setProcedure(proc);

		Assert.assertTrue(!procedure.validate());
		Assert.assertEquals(1, TestHelper.countValidationErrors(procedure));
		Assert.assertTrue(TestHelper.checkValidationResultsForAnErrorOfType(procedure, EntityRelationDoesNotConnectClassesValidationError.class));
	}
	
	public void test_cant_unrelate_if_relationship_does_not_exist() throws InvalidActionLanguageSyntaxException
	{
		String proc = "";
		proc += "CREATE user FROM User;\n";
		proc += "CREATE task FROM Task;\n";
		proc += "UNRELATE task FROM user ACROSS notARelation;\n";

		EntityDomain ttdd = DomainTTD.getTTDDomain();
		EntityClass user = ttdd.getEntityClassWithName("Task");
		EntityState state = new EntityState("Initial");
		user.addState(state);
		EntityProcedure procedure = new EntityProcedure(state);
		procedure.setProcedure(proc);

		Assert.assertTrue(!procedure.validate());
		Assert.assertEquals(1, TestHelper.countValidationErrors(procedure));
		Assert.assertTrue(TestHelper.checkValidationResultsForAnErrorOfType(procedure, EntityRelationNotFoundInDomainValidationError.class));
	}
	
}

