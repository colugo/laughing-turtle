package test.java.tests;

import test.java.helper.DomainTTD;
import test.java.helper.TestHelper;

import javax.naming.NameAlreadyBoundException;

import junit.framework.Assert;
import junit.framework.TestCase;
import main.java.avii.editor.contracts.metamodel.actionLanguage.syntax.InvalidActionLanguageSyntaxException;
import main.java.avii.editor.metamodel.actionLanguage.validation.error.syntax.AttributeNotFoundInClassValidationError;
import main.java.avii.editor.metamodel.actionLanguage.validation.error.syntax.CouldNotIdentifyDatatypeValidationError;
import main.java.avii.editor.metamodel.actionLanguage.validation.error.syntax.CouldNotIdentifyInstanceValidationError;
import main.java.avii.editor.metamodel.actionLanguage.validation.error.syntax.EntityDatatypesCannotBeSet;
import main.java.avii.editor.metamodel.actionLanguage.validation.error.syntax.StateAttributeCannotBeModifiedValidationError;
import main.java.avii.editor.metamodel.entities.EntityClass;
import main.java.avii.editor.metamodel.entities.EntityDomain;
import main.java.avii.editor.metamodel.entities.EntityProcedure;
import main.java.avii.editor.metamodel.entities.EntityState;

public class AttributeTempVariableSyntaxAssignTests extends TestCase {
	
	
	public AttributeTempVariableSyntaxAssignTests(String name) {
		super(name);
	}

	public void test_fails_if_instance_cant_be_identified() throws InvalidActionLanguageSyntaxException, NameAlreadyBoundException
	{
		String proc = "temp = 3;\n";
		proc += "notAnInstance.notAnAttribute = temp;";
		
		EntityDomain ttdDomain = DomainTTD.getTTDDomain();
		EntityClass user = ttdDomain.getEntityClassWithName("User");
		EntityState state = new EntityState( "Initial");
		user.addState(state);
		EntityProcedure procedure = new EntityProcedure(state);
		procedure.setProcedure(proc);
		
		Assert.assertTrue(!procedure.validate());
		Assert.assertEquals(1, TestHelper.countValidationErrors(procedure));
		Assert.assertTrue(TestHelper.checkValidationResultsForAnErrorOfType(procedure, CouldNotIdentifyInstanceValidationError.class));
	}
	
	public void test_fails_if_attribute_cant_be_found() throws InvalidActionLanguageSyntaxException, NameAlreadyBoundException
	{
		String proc = "temp = 3;\n";
		proc += "CREATE task FROM Task;\n";
		proc += "task.notAnAttribute = temp;\n";
		
		EntityDomain ttdDomain = DomainTTD.getTTDDomain();
		EntityClass user = ttdDomain.getEntityClassWithName("User");
		EntityState state = new EntityState( "Initial");
		user.addState(state);
		EntityProcedure procedure = new EntityProcedure(state);
		procedure.setProcedure(proc);
		
		Assert.assertTrue(!procedure.validate());
		Assert.assertEquals(1, TestHelper.countValidationErrors(procedure));
		Assert.assertTrue(TestHelper.checkValidationResultsForAnErrorOfType(procedure, AttributeNotFoundInClassValidationError.class));
	}
	

	public void test_fails_if_temp_variable_cant_be_found() throws InvalidActionLanguageSyntaxException, NameAlreadyBoundException
	{
		String proc = "CREATE task FROM Task;\n";
		proc += "task.notAnAttribute = temp;\n";
		
		EntityDomain ttdDomain = DomainTTD.getTTDDomain();
		EntityClass user = ttdDomain.getEntityClassWithName("User");
		EntityState state = new EntityState( "Initial");
		user.addState(state);
		EntityProcedure procedure = new EntityProcedure(state);
		procedure.setProcedure(proc);
		
		Assert.assertTrue(!procedure.validate());
		Assert.assertEquals(2, TestHelper.countValidationErrors(procedure));
		Assert.assertTrue(TestHelper.checkValidationResultsForAnErrorOfType(procedure, AttributeNotFoundInClassValidationError.class));
		Assert.assertTrue(TestHelper.checkValidationResultsForAnErrorOfType(procedure, CouldNotIdentifyDatatypeValidationError.class));
	}
	
	public void test_fails_if_temp_datatype_doesnt_match_attribute_datatype() throws InvalidActionLanguageSyntaxException, NameAlreadyBoundException
	{
		String proc = "temp = 4;\n";
		proc += "CREATE task FROM Task;\n";
		proc += "task.Name = temp;\n";
		
		EntityDomain ttdDomain = DomainTTD.getTTDDomain();
		EntityClass user = ttdDomain.getEntityClassWithName("User");
		EntityState state = new EntityState( "Initial");
		user.addState(state);
		EntityProcedure procedure = new EntityProcedure(state);
		procedure.setProcedure(proc);
		
		Assert.assertTrue(!procedure.validate());
		Assert.assertEquals(1, TestHelper.countValidationErrors(procedure));
		Assert.assertTrue(TestHelper.checkValidationResultsForAnErrorOfType(procedure, EntityDatatypesCannotBeSet.class));
	}
	
	
	public void test_passes_if_attribute_can_be_found_temp_can_be_found_and_datatypes_are_correct() throws InvalidActionLanguageSyntaxException, NameAlreadyBoundException
	{
		String proc = "temp = \"taskName\";\n";
		proc += "CREATE task FROM Task;\n";
		proc += "task.Name = temp;\n";
		
		EntityDomain ttdDomain = DomainTTD.getTTDDomain();
		EntityClass user = ttdDomain.getEntityClassWithName("User");
		EntityState state = new EntityState( "Initial");
		user.addState(state);
		EntityProcedure procedure = new EntityProcedure(state);
		procedure.setProcedure(proc);
		
		Assert.assertTrue(procedure.validate());
	}
	
	
	public void test_multiple_dots_dont_work() throws InvalidActionLanguageSyntaxException, NameAlreadyBoundException
	{
		String proc = "";
		proc += "CREATE user FROM User;\n";
		proc += "user.Age = user.Age + user.Age.Age + 1;\n";
		
		EntityDomain ttdDomain = DomainTTD.getTTDDomain();
		EntityClass user = ttdDomain.getEntityClassWithName("User");
		EntityState state = new EntityState( "Initial");
		user.addState(state);
		EntityProcedure procedure = new EntityProcedure(state);
		procedure.setProcedure(proc);
		
		Assert.assertTrue(!procedure.validate());
	}
	
	public void test_can_use_negative_ints() throws InvalidActionLanguageSyntaxException, NameAlreadyBoundException
	{
		String proc = "";
		proc += "CREATE user FROM User;\n";
		proc += "user.Age = -100;\n";
		
		EntityDomain ttdDomain = DomainTTD.getTTDDomain();
		EntityClass user = ttdDomain.getEntityClassWithName("User");
		EntityState state = new EntityState( "Initial");
		user.addState(state);
		EntityProcedure procedure = new EntityProcedure(state);
		procedure.setProcedure(proc);
		
		Assert.assertTrue(procedure.validate());
		
	}

	
	public void test_state_attribute_cannot_be_written_to() throws InvalidActionLanguageSyntaxException, NameAlreadyBoundException
	{
		String proc = "";
		proc += "CREATE task FROM Task;\n";
		proc += "task.state = \"temp\";\n";
		
		EntityDomain ttdDomain = DomainTTD.getTTDDomain();
		EntityClass user = ttdDomain.getEntityClassWithName("User");
		EntityState state = new EntityState( "Initial");
		user.addState(state);
		EntityProcedure procedure = new EntityProcedure(state);
		procedure.setProcedure(proc);
		
		Assert.assertTrue(!procedure.validate());
		Assert.assertEquals(1, TestHelper.countValidationErrors(procedure));
		Assert.assertTrue(TestHelper.checkValidationResultsForAnErrorOfType(procedure, StateAttributeCannotBeModifiedValidationError.class));
	}
	
}

