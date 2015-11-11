package test.java.tests;

import javax.naming.NameAlreadyBoundException;

import test.java.helper.DomainTTD;
import test.java.helper.TestHelper;
import junit.framework.Assert;
import junit.framework.TestCase;
import main.java.avii.editor.contracts.metamodel.actionLanguage.syntax.InvalidActionLanguageSyntaxException;
import main.java.avii.editor.metamodel.actionLanguage.validation.error.syntax.CouldNotIdentifyInstanceSetValidationError;
import main.java.avii.editor.metamodel.actionLanguage.validation.error.syntax.InstanceCannotBeTreatedAsAnInstanceSet;
import main.java.avii.editor.metamodel.entities.EntityClass;
import main.java.avii.editor.metamodel.entities.EntityDomain;
import main.java.avii.editor.metamodel.entities.EntityProcedure;
import main.java.avii.editor.metamodel.entities.EntityState;

public class ForInsranceSetTests extends TestCase {

	public ForInsranceSetTests(String name) {
		super(name);
	}
	
	public void test_for_each_fails_if_instance_set_cant_be_identified() throws InvalidActionLanguageSyntaxException, NameAlreadyBoundException
	{
		String proc = "";
		proc += "FOR task IN tasks DO\n";
		proc += "END FOR;";
		
		EntityDomain ttdDomain = DomainTTD.getTTDDomain();
		EntityClass user = ttdDomain.getEntityClassWithName("User");
		EntityState state = new EntityState( "Initial");
		user.addState(state);
		EntityProcedure procedure = new EntityProcedure(state);
		procedure.setProcedure(proc);
		
		Assert.assertTrue(!procedure.validate());
		Assert.assertEquals(1, TestHelper.countValidationErrors(procedure));
		Assert.assertTrue(TestHelper.checkValidationResultsForAnErrorOfType(procedure, CouldNotIdentifyInstanceSetValidationError.class));
	}

	
	public void test_for_each_fails_if_instance_set_is_not_a_set() throws InvalidActionLanguageSyntaxException, NameAlreadyBoundException
	{
		String proc = "";
		proc += "CREATE tasks FROM Task;\n";
		proc += "FOR task IN tasks DO\n";
		proc += "END FOR;";
		
		EntityDomain ttdDomain = DomainTTD.getTTDDomain();
		EntityClass user = ttdDomain.getEntityClassWithName("User");
		EntityState state = new EntityState( "Initial");
		user.addState(state);
		EntityProcedure procedure = new EntityProcedure(state);
		procedure.setProcedure(proc);
		
		Assert.assertTrue(!procedure.validate());
		Assert.assertEquals(1, TestHelper.countValidationErrors(procedure));
		Assert.assertTrue(TestHelper.checkValidationResultsForAnErrorOfType(procedure, InstanceCannotBeTreatedAsAnInstanceSet.class));
	}
	
	public void test_for_each_with_any_select() throws InvalidActionLanguageSyntaxException, NameAlreadyBoundException
	{
		String proc = "";
		proc += "SELECT ANY tasks RELATED BY self->R1;\n";
		proc += "FOR task IN tasks DO\n";
		proc += "END FOR;";
		
		EntityDomain ttdDomain = DomainTTD.getTTDDomain();
		EntityClass user = ttdDomain.getEntityClassWithName("User");
		EntityState state = new EntityState( "Initial");
		user.addState(state);
		EntityProcedure procedure = new EntityProcedure(state);
		procedure.setProcedure(proc);
		
		Assert.assertTrue(!procedure.validate());
		Assert.assertEquals(1, TestHelper.countValidationErrors(procedure));
		Assert.assertTrue(TestHelper.checkValidationResultsForAnErrorOfType(procedure, InstanceCannotBeTreatedAsAnInstanceSet.class));
	}
	
	public void test_for_each() throws InvalidActionLanguageSyntaxException, NameAlreadyBoundException
	{
		String proc = "";
		proc += "SELECT MANY tasks RELATED BY self->R1;\n";
		proc += "FOR task IN tasks DO\n";
		proc += "END FOR;";
		
		EntityDomain ttdDomain = DomainTTD.getTTDDomain();
		EntityClass user = ttdDomain.getEntityClassWithName("User");
		EntityState state = new EntityState( "Initial");
		user.addState(state);
		EntityProcedure procedure = new EntityProcedure(state);
		procedure.setProcedure(proc);
		
		TestHelper.printValidationErrors(procedure);
		Assert.assertTrue(procedure.validate());
		Assert.assertEquals(0, TestHelper.countValidationErrors(procedure));
	}
	
}

