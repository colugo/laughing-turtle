package test.java.tests;
import test.java.helper.DomainBus;
import test.java.helper.DomainWarehouse;
import test.java.helper.TestHelper;

import javax.naming.NameAlreadyBoundException;
import javax.naming.NameNotFoundException;

import junit.framework.Assert;
import junit.framework.TestCase;
import main.java.avii.editor.contracts.metamodel.actionLanguage.syntax.InvalidActionLanguageSyntaxException;
import main.java.avii.editor.metamodel.actionLanguage.validation.EntityClassValidator;
import main.java.avii.editor.metamodel.actionLanguage.validation.EntityDomainValidator;
import main.java.avii.editor.metamodel.actionLanguage.validation.error.structure.AllSubClassesDeclareSameAttributeValidationError;
import main.java.avii.editor.metamodel.actionLanguage.validation.error.structure.AssociationClassCannotHaveSubClassesValidationError;
import main.java.avii.editor.metamodel.actionLanguage.validation.error.structure.GeneralisationClassWithStatesMustHaveInitialStateValidationError;
import main.java.avii.editor.metamodel.actionLanguage.validation.error.structure.InitialStateCannotHaveActionLanguageValidationError;
import main.java.avii.editor.metamodel.actionLanguage.validation.error.structure.ReflexiveEntityRelationMustHaveDifferingVerbPhrasesValidationError;
import main.java.avii.editor.metamodel.actionLanguage.validation.error.structure.ReflexiveEntityRelationMustHaveVerbPhrasesValidationError;
import main.java.avii.editor.metamodel.actionLanguage.validation.error.syntax.AttributeNotFoundInClassValidationError;
import main.java.avii.editor.metamodel.actionLanguage.validation.error.syntax.BooleanOrEqualityOperatorUsedInArithmeticExpression;
import main.java.avii.editor.metamodel.actionLanguage.validation.error.syntax.CouldNotIdentifyDatatypeValidationError;
import main.java.avii.editor.metamodel.actionLanguage.validation.error.syntax.CouldNotIdentifyInstanceValidationError;
import main.java.avii.editor.metamodel.actionLanguage.validation.error.syntax.EntityClassNotFoundInDomainValidationError;
import main.java.avii.editor.metamodel.actionLanguage.validation.error.syntax.EntityDatatypesCannotBeSet;
import main.java.avii.editor.metamodel.actionLanguage.validation.error.syntax.EntityRelationDoesNotConnectClassesValidationError;
import main.java.avii.editor.metamodel.actionLanguage.validation.error.syntax.FailSyntaxCanOnlyBeUsedInAssertionProceduresValidationError;
import main.java.avii.editor.metamodel.actionLanguage.validation.error.syntax.LogicTreeValidationError;
import main.java.avii.editor.metamodel.actionLanguage.validation.error.syntax.ReflexiveEntityRelationMustHaveSameClassesValidationError;
import main.java.avii.editor.metamodel.actionLanguage.validation.error.syntax.StateAttributeCanOnlyBeUsedInAssertionProceduresValidationError;
import main.java.avii.editor.metamodel.entities.EntityAttribute;
import main.java.avii.editor.metamodel.entities.EntityClass;
import main.java.avii.editor.metamodel.entities.EntityDomain;
import main.java.avii.editor.metamodel.entities.EntityEventInstance;
import main.java.avii.editor.metamodel.entities.EntityEventSpecification;
import main.java.avii.editor.metamodel.entities.EntityProcedure;
import main.java.avii.editor.metamodel.entities.EntityRelation;
import main.java.avii.editor.metamodel.entities.EntityRelation.CardinalityType;
import main.java.avii.editor.metamodel.entities.EntityState;
import main.java.avii.editor.metamodel.entities.datatypes.BooleanEntityDatatype;
import main.java.avii.editor.metamodel.entities.datatypes.IntegerEntityDatatype;
import main.java.avii.editor.metamodel.entities.datatypes.StringEntityDatatype;
import main.java.avii.scenario.AssertionTestVectorProcedure;
import main.java.avii.scenario.TestScenario;
import main.java.avii.scenario.TestVector;


public class ValidationTests extends TestCase {

	public ValidationTests(String name) {
		super(name);
	}

	public static EntityDomain getMicrowaveDomain()
	{
		EntityDomain microwave = new EntityDomain("Microwave");
		addClassesToDomain(microwave);
		
		return microwave;
	}


	public static void addClassesToDomain(EntityDomain microwave) {
		microwave.addClass(getMicrowaveOven());
		microwave.addClass(getMicrowaveHeater());
	}
	
	private static EntityClass getMicrowaveOven()
	{
		EntityClass oven = new EntityClass("Oven");
		return oven;
	}
	
	private static EntityClass getMicrowaveHeater()
	{
		EntityClass heater = new EntityClass("Heater");
		try{
			EntityAttribute isHeaterOn = new EntityAttribute("IsHeaterOn",BooleanEntityDatatype.getInstance());
			heater.addAttribute(isHeaterOn);
			
			EntityAttribute heatSetting = new EntityAttribute("HeatSetting",IntegerEntityDatatype.getInstance());
			heater.addAttribute(heatSetting);
			
			EntityAttribute tag = new EntityAttribute("Tag",StringEntityDatatype.getInstance());
			heater.addAttribute(tag);
		}catch(Exception e)
		{}
	
		return heater;
	}
	
	public void test_microwave_has_oven_class()
	{
		EntityDomain microwave = getMicrowaveDomain();
		Assert.assertTrue(microwave.hasEntityClassWithName("Oven"));
	}
	
	public void test_microwave_has_heater_class()
	{
		EntityDomain microwave = getMicrowaveDomain();
		Assert.assertTrue(microwave.hasEntityClassWithName("Heater"));
		EntityClass heater = microwave.getEntityClassWithName("Heater");
		Assert.assertTrue(heater.hasAttribute("IsHeaterOn"));
	}

	
	public void test_validate_domain_attribute_set_with_valid_value() throws InvalidActionLanguageSyntaxException
	{

		String proc = "";
		proc += "CREATE heat FROM Heater;\n";
		proc += "heat.IsHeaterOn = FALSE;";
		
		EntityDomain microwave = getMicrowaveDomain();
		EntityClass heater = microwave.getEntityClassWithName("Heater");
		EntityState state = new EntityState("Initial");
		heater.addState(state);
		EntityProcedure procedure = new EntityProcedure(state);
		procedure.setProcedure(proc);
		
		Assert.assertTrue(procedure.validate());
		Assert.assertEquals(0, TestHelper.countValidationErrors(procedure));
	}
	
	
	public void test_validate_domain_attribute_set_with_invalid_value() throws InvalidActionLanguageSyntaxException
	{

		String proc = "";
		proc += "CREATE heat FROM Heater;\n";
		proc += "heat.IsHeaterOn = \"something\";";
		
		EntityDomain microwave = getMicrowaveDomain();
		EntityClass heater = microwave.getEntityClassWithName("Heater");
		EntityState state = new EntityState("Initial");
		heater.addState(state);
		EntityProcedure procedure = new EntityProcedure(state);
		procedure.setProcedure(proc);
		
		Assert.assertTrue(!procedure.validate());
		Assert.assertTrue(TestHelper.checkValidationResultsForAnErrorOfType(procedure, EntityDatatypesCannotBeSet.class));
		Assert.assertEquals(1, TestHelper.countValidationErrors(procedure));
	}
	
	
	public void test_validate_domain_instance_not_found() throws InvalidActionLanguageSyntaxException
	{

		String proc = "";
		proc += "CREATE heat FROM Heater;\n";
		proc += "heat3.IsHeaterOn = \"something\";";
		
		EntityDomain microwave = getMicrowaveDomain();
		EntityClass heater = microwave.getEntityClassWithName("Heater");
		EntityState state = new EntityState("Initial");
		heater.addState(state);
		EntityProcedure procedure = new EntityProcedure(state);
		procedure.setProcedure(proc);
		
		Assert.assertTrue(!procedure.validate());
		Assert.assertTrue(TestHelper.checkValidationResultsForAnErrorOfType(procedure, CouldNotIdentifyInstanceValidationError.class));
		Assert.assertEquals(1, TestHelper.countValidationErrors(procedure));
	}
	
	
	public void test_validate_domain_attribute_not_found() throws InvalidActionLanguageSyntaxException
	{

		String proc = "";
		proc += "CREATE heat FROM Heater;\n";
		proc += "heat.IsHeaterOn2 = \"something\";";
		
		EntityDomain microwave = getMicrowaveDomain();
		EntityClass heater = microwave.getEntityClassWithName("Heater");
		EntityState state = new EntityState("Initial");
		heater.addState(state);
		EntityProcedure procedure = new EntityProcedure(state);
		procedure.setProcedure(proc);
		
		Assert.assertTrue(!procedure.validate());
		Assert.assertTrue(TestHelper.checkValidationResultsForAnErrorOfType(procedure, AttributeNotFoundInClassValidationError.class));
		Assert.assertEquals(1, TestHelper.countValidationErrors(procedure));
	}
	
	public void test_validate_domain_class_not_found() throws InvalidActionLanguageSyntaxException
	{

		String proc = "";
		proc += "CREATE heat FROM Heater2;\n";
		proc += "heat.IsHeaterOn = \"something\";";
		
		EntityDomain microwave = getMicrowaveDomain();
		EntityClass heater = microwave.getEntityClassWithName("Heater");
		EntityState state = new EntityState("Initial");
		heater.addState(state);
		EntityProcedure procedure = new EntityProcedure(state);
		procedure.setProcedure(proc);
		
		Assert.assertTrue(!procedure.validate());
		Assert.assertTrue(TestHelper.checkValidationResultsForAnErrorOfType(procedure, EntityClassNotFoundInDomainValidationError.class));
		Assert.assertEquals(2, TestHelper.countValidationErrors(procedure));
	}
	
	
	public void test_validate_delete_instance() throws InvalidActionLanguageSyntaxException
	{
		String proc = "";
		proc += "CREATE heat FROM Heater;\n";
		proc += "DELETE heat;";
		
		EntityDomain microwave = getMicrowaveDomain();
		EntityClass heater = microwave.getEntityClassWithName("Heater");
		EntityState state = new EntityState("Initial");
		heater.addState(state);
		EntityProcedure procedure = new EntityProcedure(state);
		procedure.setProcedure(proc);
		
		Assert.assertTrue(procedure.validate());
		Assert.assertEquals(0, TestHelper.countValidationErrors(procedure));
	}
	
	public void test_validate_delete_instance_wrong_instance_name() throws InvalidActionLanguageSyntaxException
	{
		String proc = "";
		proc += "CREATE heat FROM Heater;\n";
		proc += "DELETE heat2;";
		
		EntityDomain microwave = getMicrowaveDomain();
		EntityClass heater = microwave.getEntityClassWithName("Heater");
		EntityState state = new EntityState("Initial");
		heater.addState(state);
		EntityProcedure procedure = new EntityProcedure(state);
		procedure.setProcedure(proc);
		
		Assert.assertTrue(!procedure.validate());
		Assert.assertTrue(TestHelper.checkValidationResultsForAnErrorOfType(procedure, CouldNotIdentifyInstanceValidationError.class));
		Assert.assertEquals(1, TestHelper.countValidationErrors(procedure));
	}

	public void test_validate_attribute_accepts_correct_typed_literal() throws InvalidActionLanguageSyntaxException
	{
		String proc = "";
		proc += "CREATE heat FROM Heater;\n";
		proc += "heat.HeatSetting = 5;";
		
		EntityDomain microwave = getMicrowaveDomain();
		EntityClass heater = microwave.getEntityClassWithName("Heater");
		EntityState state = new EntityState("Initial");
		heater.addState(state);
		
		EntityProcedure procedure = new EntityProcedure(state);
		procedure.setProcedure(proc);
		
		Assert.assertTrue(procedure.validate());
		Assert.assertEquals(0, TestHelper.countValidationErrors(procedure));
	}
	
	
	public void test_validate_attribute_does_not_accept_incorrect_typed_literal() throws InvalidActionLanguageSyntaxException
	{
		String proc = "";
		proc += "CREATE heat FROM Heater;\n";
		proc += "heat.HeatSetting = \"fred\";";
		
		EntityDomain microwave = getMicrowaveDomain();
		EntityClass heater = microwave.getEntityClassWithName("Heater");
		EntityState state = new EntityState("Initial");
		heater.addState(state);
		EntityProcedure procedure = new EntityProcedure(state);
		procedure.setProcedure(proc);
		
		Assert.assertTrue(!procedure.validate());
		Assert.assertEquals(1, TestHelper.countValidationErrors(procedure));
		Assert.assertTrue(TestHelper.checkValidationResultsForAnErrorOfType(procedure, EntityDatatypesCannotBeSet.class));
	}
	
	
	
	public void test_validate_attribute_arithmetic_accepts_correct_typed_literal() throws InvalidActionLanguageSyntaxException
	{
		String proc = "";
		proc += "CREATE heat FROM Heater;\n";
		proc += "heat.HeatSetting = 5;\n";
		proc += "heat.HeatSetting = heat.HeatSetting + 1;\n";
		
		EntityDomain microwave = getMicrowaveDomain();
		EntityClass heater = microwave.getEntityClassWithName("Heater");
		EntityState state = new EntityState("Initial");
		heater.addState(state);
		EntityProcedure procedure = new EntityProcedure(state);
		procedure.setProcedure(proc);
		
		Assert.assertTrue(procedure.validate());
		Assert.assertEquals(0, TestHelper.countValidationErrors(procedure));
	}
	
	public void test_validate_attribute_arithmetic_does_not_accept_inorrect_typed_literal_string() throws InvalidActionLanguageSyntaxException
	{
		String proc = "";
		proc += "CREATE heat FROM Heater;\n";
		proc += "heat.HeatSetting = 5;\n";
		proc += "heat.HeatSetting = heat.HeatSetting + \"fred\";\n";
		
		EntityDomain microwave = getMicrowaveDomain();
		EntityClass heater = microwave.getEntityClassWithName("Heater");
		EntityState state = new EntityState("Initial");
		heater.addState(state);
		EntityProcedure procedure = new EntityProcedure(state);
		procedure.setProcedure(proc);
		
		Assert.assertTrue(!procedure.validate());
		Assert.assertEquals(1, TestHelper.countValidationErrors(procedure));
		Assert.assertTrue(TestHelper.checkValidationResultsForAnErrorOfType(procedure, EntityDatatypesCannotBeSet.class));
	}
	
	public void test_validate_attribute_arithmetic_does_not_accept_inorrect_typed_literal_float() throws InvalidActionLanguageSyntaxException
	{
		String proc = "";
		proc += "CREATE heat FROM Heater;\n";
		proc += "heat.HeatSetting = 5;\n";
		proc += "heat.HeatSetting = heat.HeatSetting + 1.1;\n";
		
		EntityDomain microwave = getMicrowaveDomain();
		EntityClass heater = microwave.getEntityClassWithName("Heater");
		EntityState state = new EntityState("Initial");
		heater.addState(state);
		EntityProcedure procedure = new EntityProcedure(state);
		procedure.setProcedure(proc);
		
		Assert.assertTrue(!procedure.validate());
		Assert.assertEquals(1, TestHelper.countValidationErrors(procedure));
		Assert.assertTrue(TestHelper.checkValidationResultsForAnErrorOfType(procedure, EntityDatatypesCannotBeSet.class));
	}
	
	
	public void test_validate_attribute_arithmetic_does_not_accept_inorrect_typed_literal_boolean() throws InvalidActionLanguageSyntaxException
	{
		String proc = "";
		proc += "CREATE heat FROM Heater;\n";
		proc += "heat.HeatSetting = 5;\n";
		proc += "heat.HeatSetting = heat.HeatSetting + FALSE;\n";
		
		EntityDomain microwave = getMicrowaveDomain();
		EntityClass heater = microwave.getEntityClassWithName("Heater");
		EntityState state = new EntityState("Initial");
		heater.addState(state);
		EntityProcedure procedure = new EntityProcedure(state);
		procedure.setProcedure(proc);
		
		Assert.assertTrue(!procedure.validate());
		Assert.assertEquals(1, TestHelper.countValidationErrors(procedure));
		Assert.assertTrue(TestHelper.checkValidationResultsForAnErrorOfType(procedure, EntityDatatypesCannotBeSet.class));
	}
	
	public void test_validate_attribute_arithmetic_does_not_accept_unknown_token() throws InvalidActionLanguageSyntaxException
	{
		String proc = "";
		proc += "CREATE heat FROM Heater;\n";
		proc += "heat.HeatSetting = 5;\n";
		proc += "heat.HeatSetting = heat.HeatSetting + whatIsThis;\n";
		
		EntityDomain microwave = getMicrowaveDomain();
		EntityClass heater = microwave.getEntityClassWithName("Heater");
		EntityState state = new EntityState("Initial");
		heater.addState(state);
		EntityProcedure procedure = new EntityProcedure(state);
		procedure.setProcedure(proc);
		
		Assert.assertTrue(!procedure.validate());
		Assert.assertEquals(2, TestHelper.countValidationErrors(procedure));
		Assert.assertTrue(TestHelper.checkValidationResultsForAnErrorOfType(procedure, EntityDatatypesCannotBeSet.class));
		Assert.assertTrue(TestHelper.checkValidationResultsForAnErrorOfType(procedure, CouldNotIdentifyDatatypeValidationError.class));
	}
	
	
	public void test_validate_attribute_arithmetic_accepts_correct_typed_literal_but_cant_find_setting_attribute() throws InvalidActionLanguageSyntaxException
	{
		String proc = "";
		proc += "CREATE heat FROM Heater;\n";
		proc += "heat.HeatSetting = 5;\n";
		proc += "heat.notAnAttribute = heat.HeatSetting + 1;\n";
		
		EntityDomain microwave = getMicrowaveDomain();
		EntityClass heater = microwave.getEntityClassWithName("Heater");
		EntityState state = new EntityState("Initial");
		heater.addState(state);
		EntityProcedure procedure = new EntityProcedure(state);
		procedure.setProcedure(proc);
		
		Assert.assertTrue(!procedure.validate());
		Assert.assertEquals(1, TestHelper.countValidationErrors(procedure));
		Assert.assertTrue(TestHelper.checkValidationResultsForAnErrorOfType(procedure, AttributeNotFoundInClassValidationError.class));
	}
	
	
	public void test_validate_attribute_arithmetic_accepts_correct_typed_literal_but_cant_find_setting_instance() throws InvalidActionLanguageSyntaxException
	{
		String proc = "";
		proc += "CREATE heat FROM Heater;\n";
		proc += "heat.HeatSetting = 5;\n";
		proc += "notAnInstance.HeatSetting = heat.HeatSetting + 1;\n";
		
		EntityDomain microwave = getMicrowaveDomain();
		EntityClass heater = microwave.getEntityClassWithName("Heater");
		EntityState state = new EntityState("Initial");
		heater.addState(state);
		EntityProcedure procedure = new EntityProcedure(state);
		procedure.setProcedure(proc);
		
		Assert.assertTrue(!procedure.validate());
		Assert.assertEquals(1, TestHelper.countValidationErrors(procedure));
		Assert.assertTrue(TestHelper.checkValidationResultsForAnErrorOfType(procedure, CouldNotIdentifyInstanceValidationError.class));
	}
	
	
	public void test_validate_attribute_arithmetic_accepts_correct_typed_literal_but_cant_find_getting_attribute() throws InvalidActionLanguageSyntaxException
	{
		String proc = "";
		proc += "CREATE heat FROM Heater;\n";
		proc += "heat.HeatSetting = 5;\n";
		proc += "heat.HeatSetting = heat.NotAnAttribute + 1;\n";
		
		EntityDomain microwave = getMicrowaveDomain();
		EntityClass heater = microwave.getEntityClassWithName("Heater");
		EntityState state = new EntityState("Initial");
		heater.addState(state);
		EntityProcedure procedure = new EntityProcedure(state);
		procedure.setProcedure(proc);
		
		Assert.assertTrue(!procedure.validate());
		Assert.assertEquals(3, TestHelper.countValidationErrors(procedure));
		Assert.assertTrue(TestHelper.checkValidationResultsForAnErrorOfType(procedure, AttributeNotFoundInClassValidationError.class));
		Assert.assertTrue(TestHelper.checkValidationResultsForAnErrorOfType(procedure, CouldNotIdentifyDatatypeValidationError.class));
		Assert.assertTrue(TestHelper.checkValidationResultsForAnErrorOfType(procedure, EntityDatatypesCannotBeSet.class));
	}
	
	
	public void test_validate_attribute_arithmetic_accepts_correct_typed_literal_but_cant_find_getting_instance() throws InvalidActionLanguageSyntaxException
	{
		String proc = "";
		proc += "CREATE heat FROM Heater;\n";
		proc += "heat.HeatSetting = 5;\n";
		proc += "heat.HeatSetting = notAnInstance.HeatSetting + 1;\n";
		
		EntityDomain microwave = getMicrowaveDomain();
		EntityClass heater = microwave.getEntityClassWithName("Heater");
		EntityState state = new EntityState("Initial");
		heater.addState(state);
		EntityProcedure procedure = new EntityProcedure(state);
		procedure.setProcedure(proc);
		
		Assert.assertTrue(!procedure.validate());
		Assert.assertEquals(3, TestHelper.countValidationErrors(procedure));
		Assert.assertTrue(TestHelper.checkValidationResultsForAnErrorOfType(procedure, CouldNotIdentifyInstanceValidationError.class));
		Assert.assertTrue(TestHelper.checkValidationResultsForAnErrorOfType(procedure, EntityDatatypesCannotBeSet.class));
		Assert.assertTrue(TestHelper.checkValidationResultsForAnErrorOfType(procedure, CouldNotIdentifyDatatypeValidationError.class));
	}
	
	
	
	public void test_validate_attribute_arithmetic_accepts_correct_typed_literal_but_fails_if_attribute_operand_has_incorrect_type() throws InvalidActionLanguageSyntaxException
	{
		String proc = "";
		proc += "CREATE heat FROM Heater;\n";
		proc += "heat.HeatSetting = 5;\n";
		proc += "heat.HeatSetting = heat.IsHeaterOn + 1;\n";
		
		EntityDomain microwave = getMicrowaveDomain();
		EntityClass heater = microwave.getEntityClassWithName("Heater");
		EntityState state = new EntityState("Initial");
		heater.addState(state);
		EntityProcedure procedure = new EntityProcedure(state);
		procedure.setProcedure(proc);
		
		Assert.assertTrue(!procedure.validate());
		Assert.assertEquals(1, TestHelper.countValidationErrors(procedure));
	}
	
	
	
	public void test_validate_attribute_arithmetic_accepts_correct_typed_literal_with_temp() throws InvalidActionLanguageSyntaxException
	{
		String proc = "";
		proc += "CREATE heat FROM Heater;\n";
		proc += "preHeatAmount = 3;\n";
		proc += "heat.HeatSetting = 5;\n";
		proc += "heat.HeatSetting = preHeatAmount + 1;\n";
		
		EntityDomain microwave = getMicrowaveDomain();
		EntityClass heater = microwave.getEntityClassWithName("Heater");
		EntityState state = new EntityState("Initial");
		heater.addState(state);
		EntityProcedure procedure = new EntityProcedure(state);
		procedure.setProcedure(proc);
		
		Assert.assertTrue(procedure.validate());
		Assert.assertEquals(0, TestHelper.countValidationErrors(procedure));
	}
	
	
	public void test_validate_attribute_arithmetic_accepts_correct_typed_literal_with_temp_attribute() throws InvalidActionLanguageSyntaxException
	{
		String proc = "";
		proc += "CREATE heat FROM Heater;\n";
		proc += "heat.HeatSetting = 5;\n";
		proc += "preHeatAmount = heat.HeatSetting;\n";
		proc += "heat.HeatSetting = preHeatAmount + 1;\n";
		
		EntityDomain microwave = getMicrowaveDomain();
		EntityClass heater = microwave.getEntityClassWithName("Heater");
		EntityState state = new EntityState("Initial");
		heater.addState(state);
		EntityProcedure procedure = new EntityProcedure(state);
		procedure.setProcedure(proc);
		
		Assert.assertTrue(procedure.validate());
		Assert.assertEquals(0, TestHelper.countValidationErrors(procedure));
	}
	
	
	public void test_validate_attribute_arithmetic_does_not_accept_incorrect_typed_literal_with_temp_attribute() throws InvalidActionLanguageSyntaxException
	{
		String proc = "";
		proc += "CREATE heat FROM Heater;\n";
		proc += "heat.IsHeaterOn = true;\n";
		proc += "preHeatAmount = heat.IsHeaterOn;\n";
		proc += "heat.HeatSetting = preHeatAmount + 1;\n";
		
		EntityDomain microwave = getMicrowaveDomain();
		EntityClass heater = microwave.getEntityClassWithName("Heater");
		EntityState state = new EntityState("Initial");
		heater.addState(state);
		EntityProcedure procedure = new EntityProcedure(state);
		procedure.setProcedure(proc);
		
		Assert.assertTrue(!procedure.validate());
		Assert.assertEquals(1, TestHelper.countValidationErrors(procedure));
		Assert.assertTrue(TestHelper.checkValidationResultsForAnErrorOfType(procedure, EntityDatatypesCannotBeSet.class));
	}
	
	public void test_validate_temp_arithmetic_accepts_correct_typed_literal() throws InvalidActionLanguageSyntaxException
	{
		String proc = "";
		proc += "HUGE = 1;\n";
		proc += "HUGE = HUGE + 1;\n";
		
		EntityDomain microwave = getMicrowaveDomain();
		EntityClass heater = microwave.getEntityClassWithName("Heater");
		EntityState state = new EntityState("Initial");
		heater.addState(state);
		EntityProcedure procedure = new EntityProcedure(state);
		procedure.setProcedure(proc);
		
		Assert.assertTrue(procedure.validate());
		Assert.assertEquals(0, TestHelper.countValidationErrors(procedure));
	}
	
	
	public void test_validate_temp_arithmetic_cant_have_boolean_in_expression() throws InvalidActionLanguageSyntaxException
	{
		String proc = "";
		proc += "HUGE = 1 + 1;\n";
		proc += "HUGE = HUGE + 1 AND 1 + 1;\n";
		
		EntityDomain microwave = getMicrowaveDomain();
		EntityClass heater = microwave.getEntityClassWithName("Heater");
		EntityState state = new EntityState("Initial");
		heater.addState(state);
		EntityProcedure procedure = new EntityProcedure(state);
		procedure.setProcedure(proc);

		Assert.assertTrue(!procedure.validate());
		Assert.assertEquals(2, TestHelper.countValidationErrors(procedure));
		Assert.assertTrue(TestHelper.checkValidationResultsForAnErrorOfType(procedure, BooleanOrEqualityOperatorUsedInArithmeticExpression.class));
	}
	
	public void test_validate_temp_arithmetic_cant_identify_temp_from_initial_arithmetic() throws InvalidActionLanguageSyntaxException
	{
		String proc = "";
		proc += "HUGE = 1 + 1;\n";
		proc += "HUGE = HUGE + 1;\n";
		
		EntityDomain microwave = getMicrowaveDomain();
		EntityClass heater = microwave.getEntityClassWithName("Heater");
		EntityState state = new EntityState("Initial");
		heater.addState(state);
		EntityProcedure procedure = new EntityProcedure(state);
		procedure.setProcedure(proc);
		
		Assert.assertTrue(procedure.validate());
	}
	
	
	public void test_validate_temp_arithmetic_does_not_accept_incorrect_typed_literal_float() throws InvalidActionLanguageSyntaxException
	{
		String proc = "";
		proc += "HUGE = 1;\n";
		proc += "HUGE = HUGE + 1.1;\n";
		
		EntityDomain microwave = getMicrowaveDomain();
		EntityClass heater = microwave.getEntityClassWithName("Heater");
		EntityState state = new EntityState("Initial");
		heater.addState(state);
		EntityProcedure procedure = new EntityProcedure(state);
		procedure.setProcedure(proc);
		
		Assert.assertTrue(!procedure.validate());
		Assert.assertEquals(1, TestHelper.countValidationErrors(procedure));
		Assert.assertTrue(TestHelper.checkValidationResultsForAnErrorOfType(procedure, EntityDatatypesCannotBeSet.class));
	}
	
	
	public void test_validate_temp_arithmetic_does_not_accept_incorrect_typed_literal_string() throws InvalidActionLanguageSyntaxException
	{
		String proc = "";
		proc += "HUGE = 1;\n";
		proc += "HUGE = HUGE + \"abc\";\n";
		
		EntityDomain microwave = getMicrowaveDomain();
		EntityClass heater = microwave.getEntityClassWithName("Heater");
		EntityState state = new EntityState("Initial");
		heater.addState(state);
		EntityProcedure procedure = new EntityProcedure(state);
		procedure.setProcedure(proc);
		
		Assert.assertTrue(!procedure.validate());
		Assert.assertEquals(1, TestHelper.countValidationErrors(procedure));
		Assert.assertTrue(TestHelper.checkValidationResultsForAnErrorOfType(procedure, EntityDatatypesCannotBeSet.class));
	}
	
	public void test_validate_temp_arithmetic_does_not_accept_incorrect_typed_literal_bool() throws InvalidActionLanguageSyntaxException
	{
		String proc = "";
		proc += "HUGE = 1;\n";
		proc += "HUGE = HUGE + true;\n";
		
		EntityDomain microwave = getMicrowaveDomain();
		EntityClass heater = microwave.getEntityClassWithName("Heater");
		EntityState state = new EntityState("Initial");
		heater.addState(state);
		EntityProcedure procedure = new EntityProcedure(state);
		procedure.setProcedure(proc);
		
		Assert.assertTrue(!procedure.validate());
		Assert.assertEquals(2, TestHelper.countValidationErrors(procedure));
		Assert.assertTrue(TestHelper.checkValidationResultsForAnErrorOfType(procedure, EntityDatatypesCannotBeSet.class));
	}
	
	
	public void test_validate_temp_arithmetic_does_not_accept_unknown() throws InvalidActionLanguageSyntaxException
	{
		String proc = "";
		proc += "HUGE = 1;\n";
		proc += "HUGE = HUGE + whatIsThis;\n";
		
		EntityDomain microwave = getMicrowaveDomain();
		EntityClass heater = microwave.getEntityClassWithName("Heater");
		EntityState state = new EntityState("Initial");
		heater.addState(state);
		EntityProcedure procedure = new EntityProcedure(state);
		procedure.setProcedure(proc);
		
		Assert.assertTrue(!procedure.validate());
		Assert.assertEquals(2, TestHelper.countValidationErrors(procedure));
		Assert.assertTrue(TestHelper.checkValidationResultsForAnErrorOfType(procedure, EntityDatatypesCannotBeSet.class));
		Assert.assertTrue(TestHelper.checkValidationResultsForAnErrorOfType(procedure, CouldNotIdentifyDatatypeValidationError.class));
	}
	
	
	public void test_validate_temp_arithmetic_accepts_attribute_value_from_initial_literal() throws InvalidActionLanguageSyntaxException
	{
		String proc = "";
		proc += "CREATE heat FROM Heater;\n";
		proc += "preHeatAmount = 0;\n";
		proc += "preHeatAmount = heat.HeatSetting + 1;\n";
		
		EntityDomain microwave = getMicrowaveDomain();
		EntityClass heater = microwave.getEntityClassWithName("Heater");
		EntityState state = new EntityState("Initial");
		heater.addState(state);
		EntityProcedure procedure = new EntityProcedure(state);
		procedure.setProcedure(proc);
		
		Assert.assertTrue(procedure.validate());
		Assert.assertEquals(0, TestHelper.countValidationErrors(procedure));
	}
	
	public void test_validate_temp_arithmetic_from_initial_literal_does_not_accept_attribute_value_with_invalid_dataytpe() throws InvalidActionLanguageSyntaxException
	{
		String proc = "";
		proc += "CREATE heat FROM Heater;\n";
		proc += "preHeatAmount = 0;\n";
		proc += "preHeatAmount = heat.IsHeaterOn + 1;\n";
		
		EntityDomain microwave = getMicrowaveDomain();
		EntityClass heater = microwave.getEntityClassWithName("Heater");
		EntityState state = new EntityState("Initial");
		heater.addState(state);
		EntityProcedure procedure = new EntityProcedure(state);
		procedure.setProcedure(proc);
		
		Assert.assertTrue(!procedure.validate());
		Assert.assertEquals(2, TestHelper.countValidationErrors(procedure));
		Assert.assertTrue(TestHelper.checkValidationResultsForAnErrorOfType(procedure, EntityDatatypesCannotBeSet.class));
	}
	
	
	public void test_validate_temp_arithmetic_accepts_attribute_value_from_initial_attribute() throws InvalidActionLanguageSyntaxException
	{
		String proc = "";
		proc += "CREATE heat FROM Heater;\n";
		proc += "preHeatAmount = heat.HeatSetting;\n";
		proc += "preHeatAmount = heat.HeatSetting + 1;\n";
		
		EntityDomain microwave = getMicrowaveDomain();
		EntityClass heater = microwave.getEntityClassWithName("Heater");
		EntityState state = new EntityState("Initial");
		heater.addState(state);
		EntityProcedure procedure = new EntityProcedure(state);
		procedure.setProcedure(proc);
		
		Assert.assertTrue(procedure.validate());
		Assert.assertEquals(0, TestHelper.countValidationErrors(procedure));
	}
	
	public void test_validate_temp_arithmetic_from_initial_attribute_does_not_accept_attribute_value_with_invalid_dataytpe() throws InvalidActionLanguageSyntaxException
	{
		String proc = "";
		proc += "CREATE heat FROM Heater;\n";
		proc += "preHeatAmount = heat.HeatSetting;\n";
		proc += "preHeatAmount = heat.IsHeaterOn + 1;\n";
		
		EntityDomain microwave = getMicrowaveDomain();
		EntityClass heater = microwave.getEntityClassWithName("Heater");
		EntityState state = new EntityState("Initial");
		heater.addState(state);
		EntityProcedure procedure = new EntityProcedure(state);
		procedure.setProcedure(proc);
		
		Assert.assertTrue(!procedure.validate());
		Assert.assertEquals(2, TestHelper.countValidationErrors(procedure));
		Assert.assertTrue(TestHelper.checkValidationResultsForAnErrorOfType(procedure, EntityDatatypesCannotBeSet.class));
	}
	
	
	public void test_validate_temp_variable_fails_when_attribute_setter_cant_be_found() throws InvalidActionLanguageSyntaxException
	{
		String proc = "";
		proc += "CREATE heat FROM Heater;\n";
		proc += "preHeatAmount = heat.NotAnAttribute;\n";
		
		EntityDomain microwave = getMicrowaveDomain();
		EntityClass heater = microwave.getEntityClassWithName("Heater");
		EntityState state = new EntityState("Initial");
		heater.addState(state);
		EntityProcedure procedure = new EntityProcedure(state);
		procedure.setProcedure(proc);
		
		Assert.assertTrue(!procedure.validate());
		Assert.assertEquals(2, TestHelper.countValidationErrors(procedure));
		Assert.assertTrue(TestHelper.checkValidationResultsForAnErrorOfType(procedure, AttributeNotFoundInClassValidationError.class));
		Assert.assertTrue(TestHelper.checkValidationResultsForAnErrorOfType(procedure, CouldNotIdentifyDatatypeValidationError.class));

	}
	
	
	public void test_validate_temp_arithmetic_will_fail_if_operand_is_attribute_and_cant_be_found() throws InvalidActionLanguageSyntaxException
	{
		String proc = "";
		proc += "CREATE heat FROM Heater;\n";
		proc += "preHeatAmount = heat.HeatSetting;\n";
		proc += "preHeatAmount = heat.NotAnAttribute + 1;\n";
		
		EntityDomain microwave = getMicrowaveDomain();
		EntityClass heater = microwave.getEntityClassWithName("Heater");
		EntityState state = new EntityState("Initial");
		heater.addState(state);
		EntityProcedure procedure = new EntityProcedure(state);
		procedure.setProcedure(proc);
		
		Assert.assertTrue(!procedure.validate());
		Assert.assertEquals(3, TestHelper.countValidationErrors(procedure));
		Assert.assertTrue(TestHelper.checkValidationResultsForAnErrorOfType(procedure, AttributeNotFoundInClassValidationError.class));
		Assert.assertTrue(TestHelper.checkValidationResultsForAnErrorOfType(procedure, CouldNotIdentifyDatatypeValidationError.class));
		Assert.assertTrue(TestHelper.checkValidationResultsForAnErrorOfType(procedure, EntityDatatypesCannotBeSet.class));

	}
	
	
	public void test_validate_temp_arithmetic_will_fail_if_operand_instance_cant_be_found() throws InvalidActionLanguageSyntaxException
	{
		String proc = "";
		proc += "CREATE heat FROM Heater;\n";
		proc += "preHeatAmount = heat.HeatSetting;\n";
		proc += "preHeatAmount = heat2.NotAnAttribute + 1;\n";
		
		EntityDomain microwave = getMicrowaveDomain();
		EntityClass heater = microwave.getEntityClassWithName("Heater");
		EntityState state = new EntityState("Initial");
		heater.addState(state);
		EntityProcedure procedure = new EntityProcedure(state);
		procedure.setProcedure(proc);
		
		Assert.assertTrue(!procedure.validate());
		Assert.assertEquals(3, TestHelper.countValidationErrors(procedure));
		Assert.assertTrue(TestHelper.checkValidationResultsForAnErrorOfType(procedure, CouldNotIdentifyInstanceValidationError.class));
		Assert.assertTrue(TestHelper.checkValidationResultsForAnErrorOfType(procedure, CouldNotIdentifyDatatypeValidationError.class));
		Assert.assertTrue(TestHelper.checkValidationResultsForAnErrorOfType(procedure, EntityDatatypesCannotBeSet.class));

	}
	
	public void test_validate_temp_arithmetic_accepts_attribute_value_from_initial_attribute_on_strings() throws InvalidActionLanguageSyntaxException, NameAlreadyBoundException
	{
		String proc = "";
		proc += "CREATE heat FROM Heater;\n";
		proc += "serial = heat.Serial;\n";
		proc += "serial = heat.Serial + \"fred\" + 3;\n";
		
		EntityDomain microwave = getMicrowaveDomain();
		EntityClass heater = microwave.getEntityClassWithName("Heater");
		EntityAttribute serial = new EntityAttribute("Serial", StringEntityDatatype.getInstance());
		heater.addAttribute(serial);
		
		EntityState state = new EntityState("Initial");
		heater.addState(state);
		EntityProcedure procedure = new EntityProcedure(state);
		procedure.setProcedure(proc);

		Assert.assertTrue(procedure.validate());
		Assert.assertEquals(0, TestHelper.countValidationErrors(procedure));
	}
	
	public void test_dont_allow_minus_in_expression_when_attribute_is_a_string() throws NameAlreadyBoundException, InvalidActionLanguageSyntaxException
	{
		String proc = "";
		proc += "CREATE heat FROM Heater;\n";
		proc += "serial = heat.Serial;\n";
		proc += "serial = heat.Serial - \"fred\" + 3;\n";
		
		EntityDomain microwave = getMicrowaveDomain();
		EntityClass heater = microwave.getEntityClassWithName("Heater");
		EntityAttribute serial = new EntityAttribute("Serial", StringEntityDatatype.getInstance());
		heater.addAttribute(serial);
		
		EntityState state = new EntityState("Initial");
		heater.addState(state);
		EntityProcedure procedure = new EntityProcedure(state);
		procedure.setProcedure(proc);

		Assert.assertTrue(!procedure.validate());
		Assert.assertEquals(1, TestHelper.countValidationErrors(procedure));
		Assert.assertTrue(TestHelper.checkValidationResultsForAnErrorOfType(procedure, LogicTreeValidationError.class));
	}
	
	public void test_dont_allow_multiply_in_expression_when_attribute_is_a_string() throws NameAlreadyBoundException, InvalidActionLanguageSyntaxException
	{
		String proc = "";
		proc += "CREATE heat FROM Heater;\n";
		proc += "serial = heat.Serial;\n";
		proc += "serial = heat.Serial * \"fred\" + 3;\n";
		
		EntityDomain microwave = getMicrowaveDomain();
		EntityClass heater = microwave.getEntityClassWithName("Heater");
		EntityAttribute serial = new EntityAttribute("Serial", StringEntityDatatype.getInstance());
		heater.addAttribute(serial);
		
		EntityState state = new EntityState("Initial");
		heater.addState(state);
		EntityProcedure procedure = new EntityProcedure(state);
		procedure.setProcedure(proc);

		Assert.assertTrue(!procedure.validate());
		Assert.assertEquals(1, TestHelper.countValidationErrors(procedure));

	}
	
	public void test_dont_allow_divide_in_expression_when_attribute_is_a_string() throws NameAlreadyBoundException, InvalidActionLanguageSyntaxException
	{
		String proc = "";
		proc += "CREATE heat FROM Heater;\n";
		proc += "serial = heat.Serial;\n";
		proc += "serial = heat.Serial / 3 + \"fred\";\n";
		
		EntityDomain microwave = getMicrowaveDomain();
		EntityClass heater = microwave.getEntityClassWithName("Heater");
		EntityAttribute serial = new EntityAttribute("Serial", StringEntityDatatype.getInstance());
		heater.addAttribute(serial);
		
		EntityState state = new EntityState("Initial");
		heater.addState(state);
		EntityProcedure procedure = new EntityProcedure(state);
		procedure.setProcedure(proc);

		Assert.assertTrue(!procedure.validate());
		Assert.assertEquals(1, TestHelper.countValidationErrors(procedure));

	}

	public void test_all_generalisation_classes_with_states_must_have_an_initial_state() throws NameAlreadyBoundException
	{
		EntityDomain warehouseDomain = DomainWarehouse.getWarehouseDomainWithoutInitialStates();
		EntityDomainValidator validator = new EntityDomainValidator(warehouseDomain);
		Assert.assertEquals(false, validator.validate());
		Assert.assertEquals(3, validator.getValidationErrors().size());
		Assert.assertTrue( validator.getValidationErrors().get(0) instanceof GeneralisationClassWithStatesMustHaveInitialStateValidationError);
		Assert.assertTrue( validator.getValidationErrors().get(1) instanceof GeneralisationClassWithStatesMustHaveInitialStateValidationError);
		Assert.assertTrue( validator.getValidationErrors().get(2) instanceof GeneralisationClassWithStatesMustHaveInitialStateValidationError);
	}
	
	public void test_initial_state_shouldnt_have_actionlanguage() throws NameAlreadyBoundException, InvalidActionLanguageSyntaxException
	{
		// I am undecided about this being a warning or an error
		// go error for now
		
		EntityClass klass = new EntityClass("klass");
		EntityState initial = new EntityState("initial");
		EntityState second = new EntityState("second");
		
		klass.addState(initial);
		klass.addState(second);
		
		initial.setInitial();
		
		Assert.assertEquals(true, initial.isInitial());
		Assert.assertEquals(false, second.isInitial());
		
		EntityEventSpecification spec = new EntityEventSpecification(klass, "some event");
		klass.addEventSpecification(spec);
		
		EntityEventInstance instance = new EntityEventInstance(spec, initial, second);
		klass.addEventInstance(spec, instance);
		
		EntityClassValidator validator = new EntityClassValidator(klass);
		Assert.assertTrue(validator.validate());
		Assert.assertEquals(0, validator.getValidationErrors().size());
		
		EntityProcedure proc = new EntityProcedure(initial);
		proc.setProcedure("# some procedure");
		initial.setProcedure(proc);
		
		Assert.assertEquals(false, validator.validate());
		Assert.assertEquals(1, validator.getValidationErrors().size());
		
		Assert.assertEquals(InitialStateCannotHaveActionLanguageValidationError.class, validator.getValidationErrors().get(0).getClass());
		
	}
	
	public void test_hierarchial_reflexive_relation_passes_validation()
	{
		EntityDomain domain = new EntityDomain("Domain");
		EntityClass superClass = new EntityClass("SuperClass");
		EntityClass subClass1 = new EntityClass("SubClass1");
		EntityClass subClass2 = new EntityClass("SubClass2");

		domain.addClass(superClass);
		domain.addClass(subClass1);
		domain.addClass(subClass2);
		
		superClass.addSubClass(subClass1);
		superClass.addSubClass(subClass2);
		
		EntityRelation r1 = new EntityRelation("R1");
		r1.setEndA(superClass, CardinalityType.ONE_TO_ONE, "leads");
		r1.setEndB(subClass1, CardinalityType.ONE_TO_MANY, "follows");
		
		Assert.assertEquals(true, r1.isReflexive());
		
		EntityDomainValidator validator = new EntityDomainValidator(domain);
		Assert.assertEquals(true, validator.validate());
		Assert.assertEquals(0, validator.getValidationErrors().size());
	}
	
	public void test_reflexive_relation_fails_validation_without_verb_phrases()
	{
		EntityDomain domain = new EntityDomain("Domain");
		EntityClass superClass = new EntityClass("SuperClass");
		EntityClass subClass1 = new EntityClass("SubClass1");
		EntityClass subClass2 = new EntityClass("SubClass2");

		domain.addClass(superClass);
		domain.addClass(subClass1);
		domain.addClass(subClass2);
		
		superClass.addSubClass(subClass1);
		superClass.addSubClass(subClass2);
		
		EntityRelation r1 = new EntityRelation("R1");
		r1.setEndA(superClass, CardinalityType.ONE_TO_ONE);
		r1.setEndB(subClass1, CardinalityType.ONE_TO_MANY);
		
		Assert.assertEquals(true, r1.isReflexive());
		
		EntityDomainValidator validator = new EntityDomainValidator(domain);
		Assert.assertEquals(false, validator.validate());
		Assert.assertEquals(1, validator.getValidationErrors().size());
		Assert.assertTrue(TestHelper.checkValidationResultsForAnErrorOfType(validator, ReflexiveEntityRelationMustHaveVerbPhrasesValidationError.class));
	}
	
	public void test_reflexive_relation_fails_validation_without_differing_verb_phrases()
	{
		EntityDomain domain = new EntityDomain("Domain");
		EntityClass superClass = new EntityClass("SuperClass");
		EntityClass subClass1 = new EntityClass("SubClass1");
		EntityClass subClass2 = new EntityClass("SubClass2");

		domain.addClass(superClass);
		domain.addClass(subClass1);
		domain.addClass(subClass2);
		
		superClass.addSubClass(subClass1);
		superClass.addSubClass(subClass2);
		
		EntityRelation r1 = new EntityRelation("R1");
		r1.setEndA(superClass, CardinalityType.ONE_TO_ONE, "leads");
		r1.setEndB(subClass1, CardinalityType.ONE_TO_MANY, "leads");
		
		Assert.assertEquals(true, r1.isReflexive());
		
		EntityDomainValidator validator = new EntityDomainValidator(domain);
		Assert.assertEquals(false, validator.validate());
		Assert.assertEquals(1, validator.getValidationErrors().size());
		Assert.assertTrue(TestHelper.checkValidationResultsForAnErrorOfType(validator, ReflexiveEntityRelationMustHaveDifferingVerbPhrasesValidationError.class));
	}
	
	
	public void test_can_relate_hierarchial_reflexive_relation() throws InvalidActionLanguageSyntaxException
	{
		EntityDomain domain = new EntityDomain("Domain");
		EntityClass superClass = new EntityClass("SuperClass");
		EntityClass subClass1 = new EntityClass("SubClass1");
		EntityClass subClass2 = new EntityClass("SubClass2");

		domain.addClass(superClass);
		domain.addClass(subClass1);
		domain.addClass(subClass2);
		
		superClass.addSubClass(subClass1);
		superClass.addSubClass(subClass2);
		
		EntityRelation r1 = new EntityRelation("R1");
		r1.setEndA(superClass, CardinalityType.ONE_TO_ONE, "leads");
		r1.setEndB(subClass1, CardinalityType.ONE_TO_MANY, "follows");
		
		String proc = "";
		proc += "CREATE sub FROM SubClass1;\n";
		proc += "SELECT ANY super FROM INSTANCES OF SuperClass;\n";
		proc += "RELATE sub TO super ACROSS R1.\"follows\";\n";
		
		EntityState state = new EntityState("Initial");

		subClass2.addState(state);

		EntityProcedure procedure = new EntityProcedure(state);
		procedure.setProcedure(proc);
		
		EntityDomainValidator validator = new EntityDomainValidator(domain);

		Assert.assertEquals(false, validator.validate());
		Assert.assertEquals(3, validator.getValidationErrors().size());
		Assert.assertFalse(TestHelper.checkValidationResultsForAnErrorOfType(validator, ReflexiveEntityRelationMustHaveSameClassesValidationError.class));
	}

	public void test_can_relate_road_to_snow_tyre() throws NameAlreadyBoundException, InvalidActionLanguageSyntaxException
	{
		EntityDomain domain = DomainBus.getBusDomain();
		EntityClass tyre = domain.getEntityClassWithName("Tyre");
		EntityClass roadTyre = domain.getEntityClassWithName("RoadTyre");
		EntityRelation r2 = new EntityRelation("R2");
		r2.setEndA(tyre, CardinalityType.ONE_TO_ONE, "leads");
		r2.setEndB(roadTyre, CardinalityType.ONE_TO_MANY, "follows");
		Assert.assertEquals(true, r2.isReflexive());
		
		String proc = "";
		proc += "CREATE roadTyre1 FROM RoadTyre;\n";
		proc += "SELECT ANY tyre1 FROM INSTANCES OF Tyre;\n";
		proc += "CREATE snowTyre1 FROM SnowTyre;\n";
		
		proc += "RELATE snowTyre1 TO roadTyre1 ACROSS R2.\"leads\";\n";
		proc += "SELECT ANY roadTyre2 RELATED BY snowTyre1->R2.\"leads\";\n";
		proc += "SELECT ANY snowTyre2 RELATED BY roadTyre1->R2.\"follows\";\n";
		proc += "SELECT ANY roadTyre3 RELATED BY snowTyre1->R2.\"follows\";\n";
		proc += "SELECT ANY snowTyre3 RELATED BY roadTyre1->R2.\"leads\";\n";
		TestHelper.addNewDummyClassWithProcedure(domain, proc);
		
		EntityDomainValidator validator = new EntityDomainValidator(domain);

		TestHelper.printValidationErrors(validator);
		
		Assert.assertEquals(true, validator.validate());
		Assert.assertEquals(0, validator.getValidationErrors().size());
	}

	
	public void test_cant_relate_road_to_snow_tyre() throws NameAlreadyBoundException, InvalidActionLanguageSyntaxException
	{
		EntityDomain domain = DomainBus.getBusDomain();
		EntityClass tyre = domain.getEntityClassWithName("Tyre");
		EntityClass roadTyre = domain.getEntityClassWithName("RoadTyre");
		EntityRelation r2 = new EntityRelation("R2");
		r2.setEndA(tyre, CardinalityType.ONE_TO_ONE, "leads");
		r2.setEndB(roadTyre, CardinalityType.ONE_TO_MANY, "follows");
		Assert.assertEquals(true, r2.isReflexive());
		
		String proc = "";
		proc += "CREATE roadTyre1 FROM RoadTyre;\n";
		proc += "SELECT ANY tyre1 FROM INSTANCES OF Tyre;\n";
		proc += "CREATE snowTyre1 FROM SnowTyre;\n";
		
		proc += "RELATE snowTyre1 TO roadTyre1 ACROSS R2.\"follows\";\n";
		proc += "SELECT ANY roadTyre2 RELATED BY snowTyre1->R2.\"leads\";\n";
		proc += "SELECT ANY snowTyre2 RELATED BY roadTyre1->R2.\"follows\";\n";
		proc += "SELECT ANY roadTyre3 RELATED BY snowTyre1->R2.\"follows\";\n";
		proc += "SELECT ANY snowTyre3 RELATED BY roadTyre1->R2.\"leads\";\n";
		TestHelper.addNewDummyClassWithProcedure(domain, proc);
		
		EntityDomainValidator validator = new EntityDomainValidator(domain);

		Assert.assertEquals(false, validator.validate());
		Assert.assertEquals(1, validator.getValidationErrors().size());
		Assert.assertTrue(TestHelper.checkValidationResultsForAnErrorOfType(validator, EntityRelationDoesNotConnectClassesValidationError.class));
	}

	
	public void test_cant_relate_snowtyre_to_snowtyre() throws NameAlreadyBoundException, InvalidActionLanguageSyntaxException, NameNotFoundException
	{
		EntityDomain domain = DomainBus.getBusDomain();
		EntityClass tyre = domain.getEntityClassWithName("Tyre");
		EntityClass roadTyre = domain.getEntityClassWithName("RoadTyre");
		EntityRelation r2 = new EntityRelation("R2");
		r2.setEndA(tyre, CardinalityType.ONE_TO_ONE, "leads");
		r2.setEndB(roadTyre, CardinalityType.ONE_TO_MANY, "follows");
		
		String proc = "";
		proc += "CREATE snowTyre1 FROM SnowTyre;\n";
		proc += "CREATE snowTyre2 FROM SnowTyre;\n";
		proc += "RELATE snowTyre1 TO snowTyre2 ACROSS R2.\"leads\";\n";
		
		TestHelper.addNewDummyClassWithProcedure(domain, proc);

		EntityDomainValidator validator = new EntityDomainValidator(domain);

		Assert.assertEquals(false, validator.validate());
		Assert.assertEquals(1, validator.getValidationErrors().size());	
		Assert.assertTrue(TestHelper.checkValidationResultsForAnErrorOfType(validator, EntityRelationDoesNotConnectClassesValidationError.class));
	}
	
	public void test_hierarchial_relations_with_associations() throws NameAlreadyBoundException, InvalidActionLanguageSyntaxException
	{
		EntityDomain domain = DomainBus.getBusDomain();
		EntityClass tyre = domain.getEntityClassWithName("Tyre");
		EntityClass roadTyre = domain.getEntityClassWithName("RoadTyre");
		EntityClass car = domain.getEntityClassWithName("Car");
		EntityRelation r2 = new EntityRelation("R2");
		r2.setEndA(tyre, CardinalityType.ONE_TO_ONE, "leads");
		r2.setEndB(roadTyre, CardinalityType.ONE_TO_MANY, "follows");
		r2.setAssociation(car);
		
		
		String proc = "";
		proc += "CREATE snowTyre1 FROM SnowTyre;\n";
		proc += "CREATE snowTyre2 FROM SnowTyre;\n";
		proc += "RELATE snowTyre1 TO snowTyre2 ACROSS R2.\"leads\" CREATING car;\n";
		
		TestHelper.addNewDummyClassWithProcedure(domain, proc);

		EntityDomainValidator validator = new EntityDomainValidator(domain);

		Assert.assertEquals(false, validator.validate());
		Assert.assertEquals(1, validator.getValidationErrors().size());	
		Assert.assertTrue(TestHelper.checkValidationResultsForAnErrorOfType(validator, EntityRelationDoesNotConnectClassesValidationError.class));
	}

	
	public void test_association_class_cannot_have_sub_classes()
	{
		EntityDomain domain = DomainBus.getBusDomain();
		EntityClass tyre = domain.getEntityClassWithName("Tyre");
		//EntityClass roadTyre = domain.getEntityClassWithName("RoadTyre");
		
		EntityClass classA = new EntityClass("A");
		domain.addClass(classA);
		EntityClass classB = new EntityClass("B");
		domain.addClass(classB);
		EntityRelation R2 = new EntityRelation("R2");
		R2.setEndA(classA, CardinalityType.ONE_TO_MANY);
		R2.setEndB(classB, CardinalityType.ONE_TO_MANY);
		R2.setAssociation(tyre);
		
		EntityDomainValidator validator = new EntityDomainValidator(domain);
		
		Assert.assertEquals(false, validator.validate());
		Assert.assertEquals(1, validator.getValidationErrors().size());	
		Assert.assertTrue(TestHelper.checkValidationResultsForAnErrorOfType(validator, AssociationClassCannotHaveSubClassesValidationError.class));
	}
	
	public void test_association_class_can_be_sub_class()
	{
		EntityDomain domain = DomainBus.getBusDomain();
		EntityClass roadTyre = domain.getEntityClassWithName("RoadTyre");
		
		EntityClass classA = new EntityClass("A");
		domain.addClass(classA);
		EntityClass classB = new EntityClass("B");
		domain.addClass(classB);
		EntityRelation R2 = new EntityRelation("R2");
		R2.setEndA(classA, CardinalityType.ONE_TO_MANY);
		R2.setEndB(classB, CardinalityType.ONE_TO_MANY);
		R2.setAssociation(roadTyre);
		
		EntityDomainValidator validator = new EntityDomainValidator(domain);
		
		Assert.assertEquals(true, validator.validate());
		Assert.assertEquals(0, validator.getValidationErrors().size());	
	}

	public void test_if_all_sub_classes_have_same_attribute_fail_as_it_should_be_on_super_class() throws NameAlreadyBoundException
	{
		EntityDomain domain = new EntityDomain("domain");
		EntityClass sup = new EntityClass("Super");
		EntityClass sub1 = new EntityClass("Sub1");
		EntityClass sub2 = new EntityClass("Sub2");
		
		domain.addClass(sup);
		domain.addClass(sub1);
		domain.addClass(sub2);
		
		sup.addSubClass(sub1);
		sup.addSubClass(sub2);
		
		EntityAttribute age1 = new EntityAttribute("Age", IntegerEntityDatatype.getInstance());
		EntityAttribute age2 = new EntityAttribute("Age", IntegerEntityDatatype.getInstance());
		
		Assert.assertEquals(age1, age2);
		
		sub1.addAttribute(age1);
		sub2.addAttribute(age2);
		
		Assert.assertEquals(age1, age2);
		
		EntityDomainValidator validator = new EntityDomainValidator(domain);
		
		Assert.assertEquals(true, validator.validate());
		
		Assert.assertEquals(5, validator.getValidationWarnings().size());
		Assert.assertTrue(TestHelper.checkValidationResultsForAWarningOfType(validator, AllSubClassesDeclareSameAttributeValidationError.class));
	}
	
	public void test_if_not_all_sub_classes_have_same_attribute_do_not_fail_as_it_cant_be_on_super_class() throws NameAlreadyBoundException
	{
		EntityDomain domain = new EntityDomain("domain");
		EntityClass sup = new EntityClass("Super");
		EntityClass sub1 = new EntityClass("Sub1");
		EntityClass sub2 = new EntityClass("Sub2");
		
		domain.addClass(sup);
		domain.addClass(sub1);
		domain.addClass(sub2);
		
		sup.addSubClass(sub1);
		sup.addSubClass(sub2);
		
		EntityAttribute age1 = new EntityAttribute("Age", IntegerEntityDatatype.getInstance());
		
		sub1.addAttribute(age1);
		
		EntityDomainValidator validator = new EntityDomainValidator(domain);
		
		Assert.assertEquals(true, validator.validate());
		Assert.assertEquals(0, validator.getValidationErrors().size());
	}
	
	
	public void test_if_all_sub_classes_have_same_name_attribute_but_different_types_it_should_not_fail() throws NameAlreadyBoundException
	{
		EntityDomain domain = new EntityDomain("domain");
		EntityClass sup = new EntityClass("Super");
		EntityClass sub1 = new EntityClass("Sub1");
		EntityClass sub2 = new EntityClass("Sub2");
		
		domain.addClass(sup);
		domain.addClass(sub1);
		domain.addClass(sub2);
		
		sup.addSubClass(sub1);
		sup.addSubClass(sub2);
		
		EntityAttribute age1 = new EntityAttribute("Age", IntegerEntityDatatype.getInstance());
		EntityAttribute age2 = new EntityAttribute("Age", StringEntityDatatype.getInstance());
		
		sub1.addAttribute(age1);
		sub2.addAttribute(age2);
		
		EntityDomainValidator validator = new EntityDomainValidator(domain);
		
		Assert.assertEquals(true, validator.validate());
		Assert.assertEquals(0, validator.getValidationErrors().size());
	}

	
	public void test_cant_reference_state_attribute_when_not_in_assertion_procedure() throws InvalidActionLanguageSyntaxException
	{
		String proc = "";
		proc += "CREATE heat FROM Heater;\n";
		proc += "heat.Tag = \"something\";\n";
		proc += "heat.Tag = heat.state;\n";
		
		EntityDomain microwave = getMicrowaveDomain();
		EntityClass heater = microwave.getEntityClassWithName("Heater");
		EntityState state = new EntityState("Initial");
		heater.addState(state);
		EntityProcedure procedure = new EntityProcedure(state);
		procedure.setProcedure(proc);
		
		Assert.assertTrue(!procedure.validate());
		Assert.assertTrue(TestHelper.checkValidationResultsForAnErrorOfType(procedure, StateAttributeCanOnlyBeUsedInAssertionProceduresValidationError.class));
		Assert.assertEquals(1, TestHelper.countValidationErrors(procedure));
	}
	
	public void test_can_reference_state_attribute_when_in_assertion_procedure() throws InvalidActionLanguageSyntaxException
	{
		String proc = "";
		proc += "CREATE heat FROM Heater;\n";
		proc += "heat.Tag = \"something\";\n";
		proc += "heat.Tag = heat.state;\n";
		
		
		EntityDomain microwave = getMicrowaveDomain();
		TestScenario scenario = new TestScenario();
		microwave.addScenario(scenario);
		TestVector vector = new TestVector();
		scenario.addVector(vector);
		
		AssertionTestVectorProcedure procedure = new AssertionTestVectorProcedure();
		procedure.setVector(vector);
		
		procedure.setProcedure(proc);
		
		Assert.assertTrue(procedure.validate());
		Assert.assertEquals(0, TestHelper.countValidationErrors(procedure));
	}
	
	public void test_cant_use_fail_syntax_when_not_in_assertion_procedure() throws InvalidActionLanguageSyntaxException
	{
		String proc = "";
		proc += "CREATE heat FROM Heater;\n";
		proc += "heat.Tag = \"something\";\n";
		proc += "FAIL 5;\n";
		
		EntityDomain microwave = getMicrowaveDomain();
		EntityClass heater = microwave.getEntityClassWithName("Heater");
		EntityState state = new EntityState("Initial");
		heater.addState(state);
		EntityProcedure procedure = new EntityProcedure(state);
		procedure.setProcedure(proc);
		
		Assert.assertTrue(!procedure.validate());
		Assert.assertTrue(TestHelper.checkValidationResultsForAnErrorOfType(procedure, FailSyntaxCanOnlyBeUsedInAssertionProceduresValidationError.class));
		Assert.assertEquals(1, TestHelper.countValidationErrors(procedure));
	}
	
	public void test_can_use_fail_syntax_when_in_assertion_procedure() throws InvalidActionLanguageSyntaxException
	{
		String proc = "";
		proc += "CREATE heat FROM Heater;\n";
		proc += "heat.Tag = \"something\";\n";
		proc += "FAIL 5;\n";
		
		EntityDomain microwave = getMicrowaveDomain();
		TestScenario scenario = new TestScenario();
		microwave.addScenario(scenario);
		TestVector vector = new TestVector();
		scenario.addVector(vector);
		
		AssertionTestVectorProcedure procedure = new AssertionTestVectorProcedure();
		procedure.setVector(vector);
		
		procedure.setProcedure(proc);
		
		Assert.assertTrue(procedure.validate());
		Assert.assertEquals(0, TestHelper.countValidationErrors(procedure));
	}
	
}

