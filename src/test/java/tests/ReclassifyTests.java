package test.java.tests;

import test.java.helper.DomainBus;
import test.java.helper.TestHelper;

import javax.naming.NameAlreadyBoundException;

import junit.framework.Assert;
import junit.framework.TestCase;
import main.java.avii.editor.contracts.metamodel.actionLanguage.syntax.InvalidActionLanguageSyntaxException;
import main.java.avii.editor.metamodel.actionLanguage.validation.error.syntax.EntityClassIsNotPartOfAGeneralisationValidationError;
import main.java.avii.editor.metamodel.actionLanguage.validation.error.syntax.EntityClassNotFoundInDomainValidationError;
import main.java.avii.editor.metamodel.actionLanguage.validation.error.syntax.EntityClassesAreNotInTheSameGeneralisationValidationError;
import main.java.avii.editor.metamodel.entities.EntityClass;
import main.java.avii.editor.metamodel.entities.EntityDomain;
import main.java.avii.editor.metamodel.entities.EntityProcedure;
import main.java.avii.editor.metamodel.entities.EntityState;

public class ReclassifyTests extends TestCase {

	public ReclassifyTests(String name) {
		super(name);
	}
	
	public void test_cant_reclassify_if_class_cant_be_identified() throws InvalidActionLanguageSyntaxException, NameAlreadyBoundException
	{
		EntityDomain busDomain = DomainBus.getBusDomain();
		EntityClass car = busDomain.getEntityClassWithName("Car");
		
		String proc = "";
		proc += "# self is a car\n";
		proc += "RECLASSIFY TO cantfind;\n";
		
		EntityState initialState = new EntityState("Initial");
		car.addState(initialState);
		EntityProcedure procedure = new EntityProcedure(initialState);
		procedure.setProcedure(proc);
		
		Assert.assertTrue(!procedure.validate());
		Assert.assertEquals(1, TestHelper.countValidationErrors(procedure));
		Assert.assertTrue(TestHelper.checkValidationResultsForAnErrorOfType(procedure, EntityClassNotFoundInDomainValidationError.class));
	}

	public void test_cant_reclassify_if_instance_and_class_arent_in_the_same_generalisation() throws InvalidActionLanguageSyntaxException, NameAlreadyBoundException
	{
		EntityDomain busDomain = DomainBus.getBusDomain();
		EntityClass car = busDomain.getEntityClassWithName("Car");
		
		String proc = "";
		proc += "#self is a car\n";
		proc += "RECLASSIFY TO SnowTyre;\n";
		
		EntityState initialState = new EntityState("Initial");
		car.addState(initialState);
		EntityProcedure procedure = new EntityProcedure(initialState);
		procedure.setProcedure(proc);
		
		Assert.assertTrue(!procedure.validate());
		Assert.assertEquals(1, TestHelper.countValidationErrors(procedure));
		Assert.assertTrue(TestHelper.checkValidationResultsForAnErrorOfType(procedure, EntityClassesAreNotInTheSameGeneralisationValidationError.class));
	}
	
	public void test_can_reclassify_if_instance_and_class_aren_in_the_same_generalisation_but_no_common_parentage() throws InvalidActionLanguageSyntaxException, NameAlreadyBoundException
	{
		EntityDomain busDomain = DomainBus.getBusDomain();
		EntityClass dummyParent = new EntityClass("DummyParent");
		EntityClass dummyChild = new EntityClass("DummyChild");
		EntityClass personal = busDomain.getEntityClassWithName("Personal");
		busDomain.addClass(dummyParent);
		busDomain.addClass(dummyChild);
		dummyChild.addSuperClass(dummyParent);
		personal.addSuperClass(dummyParent);
		
		
		EntityClass commercial = busDomain.getEntityClassWithName("Commercial");
		
		String proc = "";
		proc += "#self is a Commercial\n";
		proc += "RECLASSIFY TO DummyChild;\n";
		
		EntityState initialState = new EntityState("Initial");
		commercial.addState(initialState);
		EntityProcedure procedure = new EntityProcedure(initialState);
		procedure.setProcedure(proc);
		
		Assert.assertTrue(procedure.validate());
	}
	
	
	public void test_cant_reclassify_if_class_is_not_in_generalisation() throws InvalidActionLanguageSyntaxException, NameAlreadyBoundException
	{
		EntityDomain busDomain = DomainBus.getBusDomain();
		EntityClass car = busDomain.getEntityClassWithName("Car");
		
		String proc = "";
		proc += "# self is Car\n";
		proc += "RECLASSIFY TO Dummy;\n";
		
		EntityState initialState = new EntityState("Initial");
		car.addState(initialState);
		EntityProcedure procedure = new EntityProcedure(initialState);
		procedure.setProcedure(proc);
		
		Assert.assertTrue(!procedure.validate());
		Assert.assertEquals(1, TestHelper.countValidationErrors(procedure));
		Assert.assertTrue(TestHelper.checkValidationResultsForAnErrorOfType(procedure, EntityClassIsNotPartOfAGeneralisationValidationError.class));
	}

	public void test_cant_reclassify_if_instance_is_not_in_generalisation() throws InvalidActionLanguageSyntaxException, NameAlreadyBoundException
	{
		EntityDomain busDomain = DomainBus.getBusDomain();
		EntityClass dummy = busDomain.getEntityClassWithName("Dummy");
		
		String proc = "";
		proc += "#self is Dummy\n";
		proc += "RECLASSIFY TO SnowTyre;\n";
		
		EntityState initialState = new EntityState("Initial");
		dummy.addState(initialState);
		EntityProcedure procedure = new EntityProcedure(initialState);
		procedure.setProcedure(proc);
		
		Assert.assertTrue(!procedure.validate());
		Assert.assertEquals(1, TestHelper.countValidationErrors(procedure));
		Assert.assertTrue(TestHelper.checkValidationResultsForAnErrorOfType(procedure, EntityClassIsNotPartOfAGeneralisationValidationError.class));

	}
	
	public void test_can_reclassify() throws InvalidActionLanguageSyntaxException, NameAlreadyBoundException
	{
		EntityDomain busDomain = DomainBus.getBusDomain();
		EntityClass snowTyre = busDomain.getEntityClassWithName("SnowTyre");
		
		String proc = "";
		proc += "#self is SnowTyre\n";
		proc += "RECLASSIFY TO RoadTyre;\n";
		
		EntityState initialState = new EntityState("Initial");
		snowTyre.addState(initialState);
		EntityProcedure procedure = new EntityProcedure(initialState);
		procedure.setProcedure(proc);
		
		Assert.assertTrue(procedure.validate());
	}
	

}

