package test.java.tests;

import test.java.helper.DomainBus;
import test.java.helper.DomainTTD;
import test.java.helper.TestHelper;
import junit.framework.Assert;
import junit.framework.TestCase;
import main.java.avii.editor.contracts.metamodel.actionLanguage.syntax.InvalidActionLanguageSyntaxException;
import main.java.avii.editor.metamodel.actionLanguage.validation.error.syntax.AttributeNotFoundInClassValidationError;
import main.java.avii.editor.metamodel.actionLanguage.validation.error.syntax.CouldNotIdentifyDatatypeValidationError;
import main.java.avii.editor.metamodel.actionLanguage.validation.error.syntax.CouldNotIdentifyInstanceValidationError;
import main.java.avii.editor.metamodel.actionLanguage.validation.error.syntax.EntityClassDoesNotHaveExitingRelationValidationError;
import main.java.avii.editor.metamodel.actionLanguage.validation.error.syntax.EntityClassNotFoundInDomainValidationError;
import main.java.avii.editor.metamodel.actionLanguage.validation.error.syntax.EntityDatatypesCannotBeCompared;
import main.java.avii.editor.metamodel.actionLanguage.validation.error.syntax.EntityRelationDoesNotConnectClassesValidationError;
import main.java.avii.editor.metamodel.actionLanguage.validation.error.syntax.EntityRelationDoesNotHaveAnAssociationClassValidationError;
import main.java.avii.editor.metamodel.actionLanguage.validation.error.syntax.EntityRelationNotFoundInDomainValidationError;
import main.java.avii.editor.metamodel.actionLanguage.validation.error.syntax.GenericLogicExpressionFailureValidationError;
import main.java.avii.editor.metamodel.actionLanguage.validation.error.syntax.InstanceNameFromSelectIsAnyAndShouldBeASingleValidationError;
import main.java.avii.editor.metamodel.actionLanguage.validation.error.syntax.InstanceNameFromSelectIsManyAndShouldBeAPluralValidationError;
import main.java.avii.editor.metamodel.actionLanguage.validation.error.syntax.InstanceSetCannotBeTreatedAsAnInstance;
import main.java.avii.editor.metamodel.entities.EntityClass;
import main.java.avii.editor.metamodel.entities.EntityDomain;
import main.java.avii.editor.metamodel.entities.EntityProcedure;
import main.java.avii.editor.metamodel.entities.EntityState;




public class SelectTests extends TestCase {

	public SelectTests(String name) {
		super(name);
	}

	public void test_select_from_instances_with_known_class() throws InvalidActionLanguageSyntaxException {
		String proc = "";
		proc += "SELECT ANY user FROM INSTANCES OF User;\n";

		EntityDomain ttd = DomainTTD.getTTDDomain();
		EntityClass user = ttd.getEntityClassWithName("User");
		EntityState state = new EntityState("Initial");

		user.addState(state);
		EntityProcedure procedure = new EntityProcedure(state);
		procedure.setProcedure(proc);

		Assert.assertTrue(procedure.validate());
		Assert.assertEquals(0, TestHelper.countValidationErrors(procedure));
	}

	public void test_select_from_instances_with_unknown_class() throws InvalidActionLanguageSyntaxException {
		String proc = "";
		proc += "SELECT ANY user FROM INSTANCES OF NotAClassInThisDomain;\n";

		EntityDomain ttd = DomainTTD.getTTDDomain();
		EntityClass user = ttd.getEntityClassWithName("User");
		EntityState state = new EntityState("Initial");
		
		user.addState(state);
		
		EntityProcedure procedure = new EntityProcedure(state);
		procedure.setProcedure(proc);

		Assert.assertTrue(!procedure.validate());
		Assert.assertEquals(1, TestHelper.countValidationErrors(procedure));
		Assert.assertTrue(TestHelper.checkValidationResultsForAnErrorOfType(procedure, EntityClassNotFoundInDomainValidationError.class));
	}
	
	public void test_select_any_from_instances_with_known_class_with_plural_name_fails() throws InvalidActionLanguageSyntaxException {
		String proc = "";
		proc += "SELECT ANY users FROM INSTANCES OF User;\n";

		EntityDomain ttd = DomainTTD.getTTDDomain();
		EntityClass user = ttd.getEntityClassWithName("User");
		EntityState state = new EntityState("Initial");
		
		user.addState(state);
		
		
		EntityProcedure procedure = new EntityProcedure(state);
		procedure.setProcedure(proc);

		Assert.assertTrue(!procedure.validate());
		Assert.assertEquals(1, TestHelper.countValidationErrors(procedure));
		Assert.assertTrue(TestHelper.checkValidationResultsForAnErrorOfType(procedure, InstanceNameFromSelectIsAnyAndShouldBeASingleValidationError.class));
	}
	
	
	public void test_select_many_from_instances_with_known_class_with_singular_name_fails() throws InvalidActionLanguageSyntaxException {
		String proc = "";
		proc += "SELECT MANY user FROM INSTANCES OF User;\n";

		EntityDomain ttd = DomainTTD.getTTDDomain();
		EntityClass user = ttd.getEntityClassWithName("User");
		EntityState state = new EntityState("Initial");
		
		user.addState(state);
		
		
		EntityProcedure procedure = new EntityProcedure(state);
		procedure.setProcedure(proc);

		Assert.assertTrue(!procedure.validate());
		Assert.assertEquals(1, TestHelper.countValidationErrors(procedure));
		Assert.assertTrue(TestHelper.checkValidationResultsForAnErrorOfType(procedure, InstanceNameFromSelectIsManyAndShouldBeAPluralValidationError.class));
	}

	public void test_select_from_instances_with_where_clause_fails_with_empty_logic() throws InvalidActionLanguageSyntaxException {
		String proc = "";
		proc += "SELECT ANY completedStep FROM INSTANCES OF Step WHERE ;\n";

		EntityDomain ttd = DomainTTD.getTTDDomain();
		EntityClass user = ttd.getEntityClassWithName("User");
		EntityState state = new EntityState("Initial");
		
		user.addState(state);
		
		
		EntityProcedure procedure = new EntityProcedure(state);
		procedure.setProcedure(proc);

		Assert.assertTrue(!procedure.validate());
		Assert.assertEquals(1, TestHelper.countValidationErrors(procedure));
		Assert.assertTrue(TestHelper.checkValidationResultsForAnErrorOfType(procedure, GenericLogicExpressionFailureValidationError.class));
	}
	
	public void test_select_from_instances_with_where_clause_fails_with_unknown_token_type_root() throws InvalidActionLanguageSyntaxException {
		String proc = "";
		proc += "SELECT ANY completedStep FROM INSTANCES OF Step WHERE notATempVariable;\n";

		EntityDomain ttd = DomainTTD.getTTDDomain();
		EntityClass user = ttd.getEntityClassWithName("User");
		EntityState state = new EntityState("Initial");
		
		user.addState(state);
		
		
		EntityProcedure procedure = new EntityProcedure(state);
		procedure.setProcedure(proc);

		Assert.assertTrue(!procedure.validate());
		Assert.assertEquals(1, TestHelper.countValidationErrors(procedure));
		Assert.assertTrue(TestHelper.checkValidationResultsForAnErrorOfType(procedure, CouldNotIdentifyDatatypeValidationError.class));
	}
	
	public void test_select_from_instances_with_known_class_with_where_clause_can_identify_simple_attribute_type() throws InvalidActionLanguageSyntaxException {
		String proc = "";
		proc += "SELECT ANY completedStep FROM INSTANCES OF Step WHERE selected.Complete == true;\n";

		EntityDomain ttd = DomainTTD.getTTDDomain();
		EntityClass user = ttd.getEntityClassWithName("User");
		EntityState state = new EntityState("Initial");
		
		user.addState(state);
		
		
		EntityProcedure procedure = new EntityProcedure(state);
		procedure.setProcedure(proc);

		Assert.assertTrue(procedure.validate());
		Assert.assertEquals(0, TestHelper.countValidationErrors(procedure));
	}
	
	
	public void test_select_from_instances_with_known_class_and_where_clause_with_operand_type_mismatch() throws InvalidActionLanguageSyntaxException {
		String proc = "";
		proc += "SELECT ANY completedStep FROM INSTANCES OF Step WHERE selected.Complete == 1;\n";

		EntityDomain ttd = DomainTTD.getTTDDomain();
		EntityClass user = ttd.getEntityClassWithName("User");
		EntityState state = new EntityState("Initial");
		
		user.addState(state);
		
		
		EntityProcedure procedure = new EntityProcedure(state);
		procedure.setProcedure(proc);

		Assert.assertTrue(!procedure.validate());
		Assert.assertEquals(2, TestHelper.countValidationErrors(procedure));
	}
	
	public void test_select_from_instances_with_known_class_and_where_clause_with_selected_attribute_not_found() throws InvalidActionLanguageSyntaxException {
		String proc = "";
		proc += "SELECT ANY completedStep FROM INSTANCES OF Step WHERE selected.notAnAttribute == 1;\n";

		EntityDomain ttd = DomainTTD.getTTDDomain();
		EntityClass user = ttd.getEntityClassWithName("User");
		EntityState state = new EntityState("Initial");
		
		user.addState(state);
		
		
		EntityProcedure procedure = new EntityProcedure(state);
		procedure.setProcedure(proc);

		Assert.assertTrue(!procedure.validate());
		Assert.assertEquals(2, TestHelper.countValidationErrors(procedure));
		Assert.assertTrue(TestHelper.checkValidationResultsForAnErrorOfType(procedure, AttributeNotFoundInClassValidationError.class));
		Assert.assertTrue(TestHelper.checkValidationResultsForAnErrorOfType(procedure, CouldNotIdentifyDatatypeValidationError.class));
	}
	
	
	public void test_select_related_by_with_known_class_and_where_clause_with_selected_attribute_not_found() throws InvalidActionLanguageSyntaxException {
		String proc = "";
		proc += "SELECT ANY completedStep RELATED BY self->R1 WHERE selected.notAnAttribute == 1;\n";

		EntityDomain ttd = DomainTTD.getTTDDomain();
		EntityClass user = ttd.getEntityClassWithName("User");
		EntityState state = new EntityState("Initial");
		
		user.addState(state);
		
		
		EntityProcedure procedure = new EntityProcedure(state);
		procedure.setProcedure(proc);

		Assert.assertTrue(!procedure.validate());
		Assert.assertEquals(2, TestHelper.countValidationErrors(procedure));
		Assert.assertTrue(TestHelper.checkValidationResultsForAnErrorOfType(procedure, AttributeNotFoundInClassValidationError.class));
		Assert.assertTrue(TestHelper.checkValidationResultsForAnErrorOfType(procedure, CouldNotIdentifyDatatypeValidationError.class));
	}
	
	
	
	public void test_select_from_instances_with_known_class_and_boolean_where_clause() throws InvalidActionLanguageSyntaxException {
		String proc = "";
		proc += "SELECT ANY completedStep FROM INSTANCES OF Step WHERE selected.Complete == true;\n";

		EntityDomain ttd = DomainTTD.getTTDDomain();
		EntityClass user = ttd.getEntityClassWithName("User");
		EntityState state = new EntityState("Initial");
		
		user.addState(state);
		
		
		EntityProcedure procedure = new EntityProcedure(state);
		procedure.setProcedure(proc);

		Assert.assertTrue(procedure.validate());
		Assert.assertEquals(0, TestHelper.countValidationErrors(procedure));
	}

	public void test_select_from_instances_with_known_class_and_integer_where_clause() throws InvalidActionLanguageSyntaxException {
		String proc = "";
		proc += "SELECT ANY wornTyre FROM INSTANCES OF Tyre WHERE selected.DaysWear >100;\n";

		EntityDomain busd = DomainBus.getBusDomain();
		EntityClass user = busd.getEntityClassWithName("Tyre");
		EntityState state = new EntityState("Initial");
		
		user.addState(state);
		
		
		EntityProcedure procedure = new EntityProcedure(state);
		procedure.setProcedure(proc);

		Assert.assertTrue(procedure.validate());
		Assert.assertEquals(0, TestHelper.countValidationErrors(procedure));
	}

	public void test_select_from_instances_with_known_class_and_string_where_clause() throws InvalidActionLanguageSyntaxException {
		String proc = "";
		proc += "SELECT ANY wornTyre FROM INSTANCES OF Vehicle WHERE selected.License == \"BUS001\";\n";

		EntityDomain busd = DomainBus.getBusDomain();
		EntityClass user = busd.getEntityClassWithName("Vehicle");
		EntityState state = new EntityState("Initial");
		
		user.addState(state);
		
		
		EntityProcedure procedure = new EntityProcedure(state);
		procedure.setProcedure(proc);

		Assert.assertTrue(procedure.validate());
		Assert.assertEquals(0, TestHelper.countValidationErrors(procedure));
	}
	
	public void test_select_from_instances_with_a_known_class_with_or_where_clause() throws InvalidActionLanguageSyntaxException {
		String proc = "";
		proc += "SELECT ANY wornTyre FROM INSTANCES OF Vehicle WHERE selected.License == \"BUS001\" OR selected.License == \"BUS002\";\n";

		EntityDomain busd = DomainBus.getBusDomain();
		EntityClass user = busd.getEntityClassWithName("Vehicle");
		EntityState state = new EntityState("Initial");
		
		user.addState(state);
		
		
		EntityProcedure procedure = new EntityProcedure(state);
		procedure.setProcedure(proc);

		Assert.assertTrue(procedure.validate());
		Assert.assertEquals(0, TestHelper.countValidationErrors(procedure));
	}
	
	public void test_select_from_instances_with_a_known_class_with_invalid_or_where_clause() throws InvalidActionLanguageSyntaxException {
		String proc = "";
		proc += "SELECT ANY wornTyre FROM INSTANCES OF Vehicle WHERE selected.License == \"BUS001\" OR selected.License == 2;\n";

		EntityDomain busd = DomainBus.getBusDomain();
		EntityClass user = busd.getEntityClassWithName("Vehicle");
		EntityState state = new EntityState("Initial");
		
		user.addState(state);
		
		
		EntityProcedure procedure = new EntityProcedure(state);
		procedure.setProcedure(proc);

		Assert.assertTrue(!procedure.validate());
		Assert.assertEquals(3, TestHelper.countValidationErrors(procedure));
		Assert.assertTrue(TestHelper.checkValidationResultsForAnErrorOfType(procedure, EntityDatatypesCannotBeCompared.class));
	}

	public void test_select_from_instances_with_bracketed_expression() throws InvalidActionLanguageSyntaxException {
		String proc = "";
		proc += "SELECT ANY wornTyre FROM INSTANCES OF Tyre WHERE (selected.DaysWear > 100.5 AND selected.DaysWear < 120) OR selected.DaysWear == 5;\n";

		EntityDomain busd = DomainBus.getBusDomain();
		EntityClass user = busd.getEntityClassWithName("Tyre");
		EntityState state = new EntityState("Initial");
		
		user.addState(state);
		
		
		EntityProcedure procedure = new EntityProcedure(state);
		procedure.setProcedure(proc);

		Assert.assertTrue(procedure.validate());
		Assert.assertEquals(0, TestHelper.countValidationErrors(procedure));
	}

	public void test_select_from_instances_with_known_class_and_floating_where_clause() throws InvalidActionLanguageSyntaxException {
		String proc = "";
		proc += "SELECT ANY wornTyre FROM INSTANCES OF Personal WHERE selected.LeaseAmount > 100.5;\n";

		EntityDomain busd = DomainBus.getBusDomain();
		EntityClass user = busd.getEntityClassWithName("Tyre");
		EntityState state = new EntityState("Initial");
		
		user.addState(state);
		
		
		EntityProcedure procedure = new EntityProcedure(state);
		procedure.setProcedure(proc);

		Assert.assertTrue(procedure.validate());
		Assert.assertEquals(0, TestHelper.countValidationErrors(procedure));
	}
	
	public void test_select_from_instances_with_known_class_with_int_attribute_and_floating_where_clause() throws InvalidActionLanguageSyntaxException {
		String proc = "";
		proc += "SELECT ANY wornTyre FROM INSTANCES OF Tyre WHERE selected.DaysWear > 100.5;\n";

		EntityDomain busd = DomainBus.getBusDomain();
		EntityClass user = busd.getEntityClassWithName("Tyre");
		EntityState state = new EntityState("Initial");
		
		user.addState(state);
		
		
		EntityProcedure procedure = new EntityProcedure(state);
		procedure.setProcedure(proc);

		Assert.assertTrue(procedure.validate());
		Assert.assertEquals(0, TestHelper.countValidationErrors(procedure));
	}

	public void test_select_from_instances_with_where_clause_can_identify_compound_datatypes() throws InvalidActionLanguageSyntaxException
	{
		// test for (1 + 2) == self.age
		String proc = "";
		proc += "SELECT ANY wornTyre FROM INSTANCES OF Tyre WHERE (selected.DaysWear > (100.5 + 100)) OR true;\n";

		EntityDomain busd = DomainBus.getBusDomain();
		EntityClass user = busd.getEntityClassWithName("Tyre");
		EntityState state = new EntityState("Initial");
		
		user.addState(state);
		
		
		EntityProcedure procedure = new EntityProcedure(state);
		procedure.setProcedure(proc);

		TestHelper.printValidationErrors(procedure);
		
		Assert.assertTrue(procedure.validate());
		Assert.assertEquals(0, TestHelper.countValidationErrors(procedure));
	}
	
	public void test_select_from_instances_with_where_clause_can_identify_invalid_compound_datatypes() throws InvalidActionLanguageSyntaxException
	{
		// test for (1 + 2) == self.age
		String proc = "";
		proc += "SELECT ANY wornTyre FROM INSTANCES OF Tyre WHERE selected.DaysWear > (100.5 + true);\n";

		EntityDomain busd = DomainBus.getBusDomain();
		EntityClass user = busd.getEntityClassWithName("Tyre");
		EntityState state = new EntityState("Initial");
		
		user.addState(state);
		
		
		EntityProcedure procedure = new EntityProcedure(state);
		procedure.setProcedure(proc);

		Assert.assertTrue(!procedure.validate());
		Assert.assertEquals(3, TestHelper.countValidationErrors(procedure));
		Assert.assertTrue(TestHelper.checkValidationResultsForAnErrorOfType(procedure, EntityDatatypesCannotBeCompared.class));
	}
	
	public void test_select_from_instances_of_unknown_generalisation_class() throws InvalidActionLanguageSyntaxException
	{
		String proc = "";
		proc += "SELECT ANY wornTyre FROM INSTANCES OF NotAClass WHERE selected.DaysWear > (100.5 + true);\n";

		EntityDomain busd = DomainBus.getBusDomain();
		EntityClass user = busd.getEntityClassWithName("Tyre");
		EntityState state = new EntityState("Initial");
		
		user.addState(state);
		
		
		EntityProcedure procedure = new EntityProcedure(state);
		procedure.setProcedure(proc);

		Assert.assertTrue(!procedure.validate());
		Assert.assertEquals(1, TestHelper.countValidationErrors(procedure));
		Assert.assertTrue(TestHelper.checkValidationResultsForAnErrorOfType(procedure, EntityClassNotFoundInDomainValidationError.class));
	}
	
	///////////////// SELECT ANYMANY instance RELATED BY initialInstance->R1->R3 WHERE selected.name = fred
	
	public void test_relation_in_domain_is_valid() throws InvalidActionLanguageSyntaxException
	{
		String proc = "";
		proc += "CREATE personal FROM Personal;\n";
		proc += "SELECT ANY mountedTyre RELATED BY personal->R1;\n";

		EntityDomain busd = DomainBus.getBusDomain();
		EntityClass user = busd.getEntityClassWithName("Tyre");
		EntityState state = new EntityState("Initial");
		
		user.addState(state);
		
		
		EntityProcedure procedure = new EntityProcedure(state);
		procedure.setProcedure(proc);

		// should be able to traverse parent relationships
		TestHelper.printValidationErrors(procedure);
		Assert.assertTrue(procedure.validate());
		Assert.assertEquals(0, TestHelper.countValidationErrors(procedure));
	}
	
	public void test_relation_from_incorrect_parent_is_invalid() throws InvalidActionLanguageSyntaxException
	{
		String proc = "";
		proc += "CREATE car FROM Car;\n";
		proc += "SELECT ANY mountedTyre RELATED BY car->R1;\n";

		EntityDomain busd = DomainBus.getBusDomain();
		EntityClass user = busd.getEntityClassWithName("Tyre");
		EntityState state = new EntityState("Initial");
		
		user.addState(state);
		
		
		EntityProcedure procedure = new EntityProcedure(state);
		procedure.setProcedure(proc);

		// should be able to traverse parent relationships
		Assert.assertTrue(!procedure.validate());
		Assert.assertTrue(TestHelper.checkValidationResultsForAnErrorOfType(procedure, EntityClassDoesNotHaveExitingRelationValidationError.class));
	}
	
	public void test_relation_not_in_domain_isnt_valid() throws InvalidActionLanguageSyntaxException
	{
		String proc = "";
		proc += "CREATE personal FROM Personal;\n";
		proc += "SELECT ANY mountedTyre RELATED BY personal->NotARelation;\n";

		EntityDomain busd = DomainBus.getBusDomain();
		EntityClass user = busd.getEntityClassWithName("Tyre");
		EntityState state = new EntityState("Initial");
		
		user.addState(state);
		
		
		EntityProcedure procedure = new EntityProcedure(state);
		procedure.setProcedure(proc);

		Assert.assertTrue(!procedure.validate());
		Assert.assertEquals(1, TestHelper.countValidationErrors(procedure));
		Assert.assertTrue(TestHelper.checkValidationResultsForAnErrorOfType(procedure, EntityRelationNotFoundInDomainValidationError.class));
	}
	
	public void test_instance_type_from_select_related() throws InvalidActionLanguageSyntaxException
	{
		String proc = "";
		proc += "CREATE personal FROM Personal;\n";
		proc += "SELECT ANY mountedTyre RELATED BY personal->R1;\n";
		proc += "mountedTyre.DaysWear = 5;\n";

		EntityDomain busd = DomainBus.getBusDomain();
		EntityClass user = busd.getEntityClassWithName("Tyre");
		EntityState state = new EntityState("Initial");
		
		user.addState(state);
		
		
		EntityProcedure procedure = new EntityProcedure(state);
		procedure.setProcedure(proc);

		TestHelper.printValidationErrors(procedure);
		Assert.assertTrue(procedure.validate());
		Assert.assertEquals(0, TestHelper.countValidationErrors(procedure));
	}
	
	public void test_instance_type_from_select_related_will_fail_if_not_correct() throws InvalidActionLanguageSyntaxException
	{
		String proc = "";
		proc += "CREATE personal FROM Personal;\n";
		proc += "SELECT ANY mountedTyre RELATED BY personal->R1;\n";
		proc += "mountedTyre.notAnAttribute = 5;\n";

		EntityDomain busd = DomainBus.getBusDomain();
		EntityClass user = busd.getEntityClassWithName("Tyre");
		EntityState state = new EntityState("Initial");
		
		user.addState(state);
		
		
		EntityProcedure procedure = new EntityProcedure(state);
		procedure.setProcedure(proc);

		Assert.assertTrue(!procedure.validate());
		Assert.assertEquals(1, TestHelper.countValidationErrors(procedure));
		Assert.assertTrue(TestHelper.checkValidationResultsForAnErrorOfType(procedure, AttributeNotFoundInClassValidationError.class));
	}
	
	public void test_instace_set_type_from_select_related() throws InvalidActionLanguageSyntaxException
	{
		String proc = "";
		proc += "CREATE personal FROM Personal;\n";
		proc += "SELECT MANY mountedTyre RELATED BY personal->R1;\n";
		proc += "mountedTyre.DaysWear = 5;\n";

		// can't treat instanceSet like an instance
		
		EntityDomain busd = DomainBus.getBusDomain();
		EntityClass user = busd.getEntityClassWithName("Tyre");
		EntityState state = new EntityState("Initial");
		
		user.addState(state);
		
		
		EntityProcedure procedure = new EntityProcedure(state);
		procedure.setProcedure(proc);

		Assert.assertTrue(!procedure.validate());
		Assert.assertEquals(1, TestHelper.countValidationErrors(procedure));
		Assert.assertTrue(TestHelper.checkValidationResultsForAnErrorOfType(procedure, InstanceSetCannotBeTreatedAsAnInstance.class));
	}
	
	
	public void test_instance_type_from_select_related_on_reflexive() throws InvalidActionLanguageSyntaxException
	{
		String proc = "";
		proc += "CREATE user FROM User;\n";
		proc += "SELECT ANY mountedTyre RELATED BY user->R1->R4.\"Leads\"->R4.\"Leads\"->R4.\"Leads\";\n";
		proc += "mountedTyre.Status = \"Open\";\n";

		EntityDomain ttdd = DomainTTD.getTTDDomain();
		EntityClass user = ttdd.getEntityClassWithName("Task");
		EntityState state = new EntityState("Initial");
		
		user.addState(state);
		
		
		EntityProcedure procedure = new EntityProcedure(state);
		procedure.setProcedure(proc);

		Assert.assertTrue(procedure.validate());
		Assert.assertEquals(0, TestHelper.countValidationErrors(procedure));
	}
	
	
	public void test_traversing_reflexive_relation_must_specify_verb_phrase() throws InvalidActionLanguageSyntaxException
	{
		String proc = "";
		proc += "CREATE user FROM User;\n";
		proc += "SELECT ANY mountedTyre RELATED BY user->R1->R4->R4->R4;\n";
		proc += "mountedTyre.Status = \"Open\";\n";

		EntityDomain ttdd = DomainTTD.getTTDDomain();
		EntityClass user = ttdd.getEntityClassWithName("Task");
		EntityState state = new EntityState("Initial");
		
		user.addState(state);
		
		
		EntityProcedure procedure = new EntityProcedure(state);
		procedure.setProcedure(proc);

		Assert.assertTrue(!procedure.validate());
		Assert.assertEquals(1, TestHelper.countValidationErrors(procedure));
	}
	
	public void test_traversing_reflexive_relation_must_specify_correct_verb_phrase() throws InvalidActionLanguageSyntaxException
	{
		String proc = "";
		proc += "CREATE user FROM User;\n";
		proc += "SELECT ANY mountedTyre RELATED BY user->R1->R4.\"notaverb\";\n";
		proc += "mountedTyre.Status = \"Open\";\n";

		EntityDomain ttdd = DomainTTD.getTTDDomain();
		EntityClass user = ttdd.getEntityClassWithName("Task");
		EntityState state = new EntityState("Initial");
		
		user.addState(state);
		
		
		EntityProcedure procedure = new EntityProcedure(state);
		procedure.setProcedure(proc);

		Assert.assertTrue(!procedure.validate());
		Assert.assertEquals(1, TestHelper.countValidationErrors(procedure));
	}
	
	public void test_initial_instance_in_select_must_exist() throws InvalidActionLanguageSyntaxException
	{
		String proc = "";
		proc += "CREATE personal FROM Personal;\n";
		proc += "SELECT ANY mountedTyre RELATED BY notARealInstance->R1;\n";

		EntityDomain busd = DomainBus.getBusDomain();
		EntityClass user = busd.getEntityClassWithName("Tyre");
		EntityState state = new EntityState("Initial");
		
		user.addState(state);
		
		
		EntityProcedure procedure = new EntityProcedure(state);
		procedure.setProcedure(proc);

		Assert.assertTrue(!procedure.validate());
		Assert.assertEquals(1, TestHelper.countValidationErrors(procedure));
		Assert.assertTrue(TestHelper.checkValidationResultsForAnErrorOfType(procedure, CouldNotIdentifyInstanceValidationError.class));
	}
	
	public void test_initial_instance_must_have_exiting_relation() throws InvalidActionLanguageSyntaxException
	{
		String proc = "";
		proc += "CREATE user FROM User;\n";
		proc += "SELECT ANY mountedTyre RELATED BY user->R4.\"Leads\";\n";

		EntityDomain ttdd = DomainTTD.getTTDDomain();
		EntityClass user = ttdd.getEntityClassWithName("Task");
		EntityState state = new EntityState("Initial");
		
		user.addState(state);
		
		
		EntityProcedure procedure = new EntityProcedure(state);
		procedure.setProcedure(proc);

		
		Assert.assertTrue(!procedure.validate());
		Assert.assertEquals(1, TestHelper.countValidationErrors(procedure));
	}
	
	public void test_cant_select_over_impossible_relation_path() throws InvalidActionLanguageSyntaxException
	{
		String proc = "";
		proc += "CREATE user FROM User;\n";
		proc += "SELECT ANY mountedTyre RELATED BY user->R1->R3.\"Leads\";\n";
		
		EntityDomain ttdd = DomainTTD.getTTDDomain();
		EntityClass user = ttdd.getEntityClassWithName("Task");
		EntityState state = new EntityState("Initial");
		
		user.addState(state);
		
		
		EntityProcedure procedure = new EntityProcedure(state);
		procedure.setProcedure(proc);

		Assert.assertTrue(!procedure.validate());
		Assert.assertEquals(1, TestHelper.countValidationErrors(procedure));
	}
	
	
	//////////////// WHERE CLAUSES ///////////////////////
	public void test_relation_in_domain_is_valid_with_where() throws InvalidActionLanguageSyntaxException
	{
		String proc = "";
		proc += "CREATE personal FROM Personal;\n";
		proc += "SELECT ANY mountedTyre RELATED BY personal->R1 WHERE selected.DaysWear == 1;\n";

		EntityDomain busd = DomainBus.getBusDomain();
		EntityClass user = busd.getEntityClassWithName("Tyre");
		EntityState state = new EntityState("Initial");
		
		user.addState(state);
		
		
		EntityProcedure procedure = new EntityProcedure(state);
		procedure.setProcedure(proc);

		Assert.assertTrue(procedure.validate());
		Assert.assertEquals(0, TestHelper.countValidationErrors(procedure));
	}
	
	public void test_relation_not_in_domain_isnt_valid_with_where() throws InvalidActionLanguageSyntaxException
	{
		String proc = "";
		proc += "CREATE personal FROM Personal;\n";
		proc += "SELECT ANY mountedTyre RELATED BY personal->NotARelation WHERE selected.DaysWear == 1;\n";

		EntityDomain busd = DomainBus.getBusDomain();
		EntityClass user = busd.getEntityClassWithName("Tyre");
		EntityState state = new EntityState("Initial");
		
		user.addState(state);
		
		
		EntityProcedure procedure = new EntityProcedure(state);
		procedure.setProcedure(proc);

		Assert.assertTrue(!procedure.validate());
		Assert.assertEquals(3, TestHelper.countValidationErrors(procedure));
		Assert.assertTrue(TestHelper.checkValidationResultsForAnErrorOfType(procedure, EntityRelationNotFoundInDomainValidationError.class));
		Assert.assertTrue(TestHelper.checkValidationResultsForAnErrorOfType(procedure, CouldNotIdentifyInstanceValidationError.class));
		Assert.assertTrue(TestHelper.checkValidationResultsForAnErrorOfType(procedure, CouldNotIdentifyDatatypeValidationError.class));
	}
	
	public void test_instance_type_from_select_related_with_where() throws InvalidActionLanguageSyntaxException
	{
		String proc = "";
		proc += "CREATE personal FROM Personal;\n";
		proc += "SELECT ANY mountedTyre RELATED BY personal->R1 WHERE selected.DaysWear == 1;\n";
		proc += "mountedTyre.DaysWear = 5;\n";

		EntityDomain busd = DomainBus.getBusDomain();
		EntityClass user = busd.getEntityClassWithName("Tyre");
		EntityState state = new EntityState("Initial");
		
		user.addState(state);
		
		
		EntityProcedure procedure = new EntityProcedure(state);
		procedure.setProcedure(proc);

		Assert.assertTrue(procedure.validate());
		Assert.assertEquals(0, TestHelper.countValidationErrors(procedure));
	}
	
	public void test_instance_type_from_select_related_will_fail_if_not_correct_with_where() throws InvalidActionLanguageSyntaxException
	{
		String proc = "";
		proc += "CREATE personal FROM Personal;\n";
		proc += "SELECT ANY mountedTyre RELATED BY personal->R1 WHERE selected.DaysWear == 1;\n";
		proc += "mountedTyre.notAnAttribute = 5;\n";

		EntityDomain busd = DomainBus.getBusDomain();
		EntityClass user = busd.getEntityClassWithName("Tyre");
		EntityState state = new EntityState("Initial");
		
		user.addState(state);
		
		
		EntityProcedure procedure = new EntityProcedure(state);
		procedure.setProcedure(proc);

		Assert.assertTrue(!procedure.validate());
		Assert.assertEquals(1, TestHelper.countValidationErrors(procedure));
		Assert.assertTrue(TestHelper.checkValidationResultsForAnErrorOfType(procedure, AttributeNotFoundInClassValidationError.class));
	}
	
	public void test_instace_set_type_from_select_related_with_where() throws InvalidActionLanguageSyntaxException
	{
		String proc = "";
		proc += "CREATE personal FROM Personal;\n";
		proc += "SELECT MANY mountedTyre RELATED BY personal->R1 WHERE selected.DaysWear == 1;\n";
		proc += "mountedTyre.DaysWear = 5;\n";

		// can't treat instanceSet like an instance
		
		EntityDomain busd = DomainBus.getBusDomain();
		EntityClass user = busd.getEntityClassWithName("Tyre");
		EntityState state = new EntityState("Initial");
		
		user.addState(state);
		
		
		EntityProcedure procedure = new EntityProcedure(state);
		procedure.setProcedure(proc);

		Assert.assertTrue(!procedure.validate());
		Assert.assertEquals(1, TestHelper.countValidationErrors(procedure));
		Assert.assertTrue(TestHelper.checkValidationResultsForAnErrorOfType(procedure, InstanceSetCannotBeTreatedAsAnInstance.class));
	}
	
	
	public void test_instance_type_from_select_related_on_reflexive_with_where() throws InvalidActionLanguageSyntaxException
	{
		String proc = "";
		proc += "CREATE user FROM User;\n";
		proc += "SELECT ANY mountedTyre RELATED BY user->R1->R4.\"Leads\"->R4.\"Leads\"->R4.\"Leads\" WHERE selected.Status == \"open\";\n";
		proc += "mountedTyre.Status = \"Open\";\n";

		EntityDomain ttdd = DomainTTD.getTTDDomain();
		EntityClass user = ttdd.getEntityClassWithName("Task");
		EntityState state = new EntityState("Initial");
		
		user.addState(state);
		
		
		EntityProcedure procedure = new EntityProcedure(state);
		procedure.setProcedure(proc);

		Assert.assertTrue(procedure.validate());
		Assert.assertEquals(0, TestHelper.countValidationErrors(procedure));
	}
	
	
	public void test_traversing_reflexive_relation_must_specify_verb_phrase_with_where() throws InvalidActionLanguageSyntaxException
	{
		String proc = "";
		proc += "CREATE user FROM User;\n";
		proc += "SELECT ANY mountedTyre RELATED BY user->R1->R4->R4->R4 WHERE selected.Status == \"open\";\n";
		proc += "mountedTyre.Status = \"Open\";\n";

		EntityDomain ttdd = DomainTTD.getTTDDomain();
		EntityClass user = ttdd.getEntityClassWithName("Task");
		EntityState state = new EntityState("Initial");
		
		user.addState(state);
		
		
		EntityProcedure procedure = new EntityProcedure(state);
		procedure.setProcedure(proc);

		Assert.assertTrue(!procedure.validate());
		Assert.assertEquals(1, TestHelper.countValidationErrors(procedure));
	}
	
	public void test_traversing_reflexive_relation_must_specify_correct_verb_phrase_with_where() throws InvalidActionLanguageSyntaxException
	{
		String proc = "";
		proc += "CREATE user FROM User;\n";
		proc += "SELECT ANY mountedTyre RELATED BY user->R1->R4.\"notaverb\" WHERE selected.Status == \"Open\";\n";
		proc += "mountedTyre.Status = \"Open\";\n";

		EntityDomain ttdd = DomainTTD.getTTDDomain();
		EntityClass user = ttdd.getEntityClassWithName("Task");
		EntityState state = new EntityState("Initial");
		
		user.addState(state);
		
		
		EntityProcedure procedure = new EntityProcedure(state);
		procedure.setProcedure(proc);

		Assert.assertTrue(!procedure.validate());
		Assert.assertEquals(1, TestHelper.countValidationErrors(procedure));
	}
	
	public void test_initial_instance_in_select_must_exist_with_where() throws InvalidActionLanguageSyntaxException
	{
		String proc = "";
		proc += "CREATE personal FROM Personal;\n";
		proc += "SELECT ANY mountedTyre RELATED BY notARealInstance->R1 WHERE selected.DaysWear == 1;\n";

		EntityDomain busd = DomainBus.getBusDomain();
		EntityClass user = busd.getEntityClassWithName("Tyre");
		EntityState state = new EntityState("Initial");
		
		user.addState(state);
		
		
		EntityProcedure procedure = new EntityProcedure(state);
		procedure.setProcedure(proc);

		Assert.assertTrue(!procedure.validate());
		Assert.assertEquals(3, TestHelper.countValidationErrors(procedure));
		Assert.assertTrue(TestHelper.checkValidationResultsForAnErrorOfType(procedure, CouldNotIdentifyInstanceValidationError.class));
	}
	
	public void test_initial_instance_must_have_exiting_relation_with_where() throws InvalidActionLanguageSyntaxException
	{
		String proc = "";
		proc += "CREATE user FROM User;\n";
		proc += "SELECT ANY mountedTyre RELATED BY user->R4.\"Leads\" WHERE selected.Status == \"Open\";\n";

		EntityDomain ttdd = DomainTTD.getTTDDomain();
		EntityClass user = ttdd.getEntityClassWithName("Task");
		EntityState state = new EntityState("Initial");
		
		user.addState(state);
		
		
		EntityProcedure procedure = new EntityProcedure(state);
		procedure.setProcedure(proc);

		
		Assert.assertTrue(!procedure.validate());
		Assert.assertEquals(3, TestHelper.countValidationErrors(procedure));
		Assert.assertTrue(TestHelper.checkValidationResultsForAnErrorOfType(procedure, EntityClassDoesNotHaveExitingRelationValidationError.class));
		Assert.assertTrue(TestHelper.checkValidationResultsForAnErrorOfType(procedure, CouldNotIdentifyInstanceValidationError.class));
		Assert.assertTrue(TestHelper.checkValidationResultsForAnErrorOfType(procedure, CouldNotIdentifyDatatypeValidationError.class));
	}
	
	public void test_cant_select_over_impossible_relation_path_with_where() throws InvalidActionLanguageSyntaxException
	{
		String proc = "";
		proc += "CREATE user FROM User;\n";
		proc += "SELECT ANY mountedTyre RELATED BY user->R1->R3.\"Leads\" WHERE selected.Complete == true;\n";
		
		EntityDomain ttdd = DomainTTD.getTTDDomain();
		EntityClass user = ttdd.getEntityClassWithName("Task");
		EntityState state = new EntityState("Initial");
		
		user.addState(state);
		
		
		EntityProcedure procedure = new EntityProcedure(state);
		procedure.setProcedure(proc);

		Assert.assertTrue(!procedure.validate());
		Assert.assertEquals(3, TestHelper.countValidationErrors(procedure));
		Assert.assertTrue(TestHelper.checkValidationResultsForAnErrorOfType(procedure, EntityClassDoesNotHaveExitingRelationValidationError.class));
		Assert.assertTrue(TestHelper.checkValidationResultsForAnErrorOfType(procedure, CouldNotIdentifyInstanceValidationError.class));
		Assert.assertTrue(TestHelper.checkValidationResultsForAnErrorOfType(procedure, CouldNotIdentifyDatatypeValidationError.class));
	}

	public void test_instance_type_from_select_related_with_complex_where() throws InvalidActionLanguageSyntaxException
	{
		String proc = "";
		proc += "CREATE personal FROM Personal;\n";
		proc += "SELECT ANY mountedTyre RELATED BY personal->R1 WHERE selected.DaysWear == 1 AND personal.HasToilet == false;\n";
		proc += "mountedTyre.DaysWear = 5;\n";

		EntityDomain busd = DomainBus.getBusDomain();
		EntityClass user = busd.getEntityClassWithName("Tyre");
		EntityState state = new EntityState("Initial");
		
		user.addState(state);
		
		
		EntityProcedure procedure = new EntityProcedure(state);
		procedure.setProcedure(proc);

		Assert.assertTrue(procedure.validate());
		Assert.assertEquals(0, TestHelper.countValidationErrors(procedure));
	}
	
	
	///////////////// SELECT ONE ////////////////////////
	
	public void test_can_select_one_over_relationship_with_association() throws InvalidActionLanguageSyntaxException
	{
		String proc = "";
		proc += "CREATE user FROM User;\n";
		proc += "CREATE task FROM Task;\n";
		proc += "SELECT ONE assignment THAT RELATES user TO task ACROSS R1;\n";
		proc += "assignment.Age = 1;\n";

		EntityDomain ttdd = DomainTTD.getTTDDomain();
		EntityClass user = ttdd.getEntityClassWithName("Task");
		EntityState state = new EntityState("Initial");
		
		user.addState(state);
		
		
		EntityProcedure procedure = new EntityProcedure(state);
		procedure.setProcedure(proc);

		Assert.assertTrue(procedure.validate());
		Assert.assertEquals(0, TestHelper.countValidationErrors(procedure));
	}
	
	public void test_can_select_one_over_relationship_with_association_and_instances_flipped() throws InvalidActionLanguageSyntaxException
	{
		String proc = "";
		proc += "CREATE user FROM User;\n";
		proc += "CREATE task FROM Task;\n";
		proc += "SELECT ONE assignment THAT RELATES task TO user ACROSS R1;\n";
		proc += "assignment.Age = 1;\n";

		EntityDomain ttdd = DomainTTD.getTTDDomain();
		EntityClass user = ttdd.getEntityClassWithName("Task");
		EntityState state = new EntityState("Initial");
		
		user.addState(state);
		
		
		EntityProcedure procedure = new EntityProcedure(state);
		procedure.setProcedure(proc);

		Assert.assertTrue(procedure.validate());
		Assert.assertEquals(0, TestHelper.countValidationErrors(procedure));
	}
	
	public void test_cant_select_one_over_relationship_without_association() throws InvalidActionLanguageSyntaxException
	{
		String proc = "";
		proc += "CREATE step FROM Step;\n";
		proc += "CREATE task FROM Task;\n";
		proc += "SELECT ONE assignment THAT RELATES task TO step ACROSS R2;\n";

		EntityDomain ttdd = DomainTTD.getTTDDomain();
		EntityClass user = ttdd.getEntityClassWithName("Task");
		EntityState state = new EntityState("Initial");
		
		user.addState(state);
		
		
		EntityProcedure procedure = new EntityProcedure(state);
		procedure.setProcedure(proc);

		Assert.assertTrue(!procedure.validate());
		Assert.assertEquals(1, TestHelper.countValidationErrors(procedure));
		Assert.assertTrue(TestHelper.checkValidationResultsForAnErrorOfType(procedure, EntityRelationDoesNotHaveAnAssociationClassValidationError.class));
	}
	
	public void test_can_select_one_over_reflexive_relationship_with_association() throws InvalidActionLanguageSyntaxException
	{
		String proc = "";
		proc += "CREATE step1 FROM Step;\n";
		proc += "CREATE step2 FROM Step;\n";
		proc += "SELECT ONE assignment THAT RELATES step1 TO step2 ACROSS R3;\n";
		proc += "assignment.Order = 1;\n";

		EntityDomain ttdd = DomainTTD.getTTDDomain();
		EntityClass user = ttdd.getEntityClassWithName("Task");
		EntityState state = new EntityState("Initial");
		
		user.addState(state);
		
		
		EntityProcedure procedure = new EntityProcedure(state);
		procedure.setProcedure(proc);

		Assert.assertTrue(procedure.validate());
		Assert.assertEquals(0, TestHelper.countValidationErrors(procedure));
	}
	
	public void test_cant_select_one_over_reflexive_relationship_without_association() throws InvalidActionLanguageSyntaxException
	{
		String proc = "";
		proc += "CREATE task1 FROM Task;\n";
		proc += "CREATE task2 FROM Task;\n";
		proc += "SELECT ONE sequence THAT RELATES task1 TO task2 ACROSS R4;\n";

		EntityDomain ttdd = DomainTTD.getTTDDomain();
		EntityClass user = ttdd.getEntityClassWithName("Task");
		EntityState state = new EntityState("Initial");
		
		user.addState(state);
		
		
		EntityProcedure procedure = new EntityProcedure(state);
		procedure.setProcedure(proc);

		Assert.assertTrue(!procedure.validate());
		Assert.assertEquals(1, TestHelper.countValidationErrors(procedure));
	}
	
	public void test_cant_select_one_if_left_instance_cant_be_identified() throws InvalidActionLanguageSyntaxException
	{
		String proc = "";
		proc += "CREATE user FROM User;\n";
		proc += "CREATE task FROM Task;\n";
		proc += "SELECT ONE assignment THAT RELATES notAnInstance TO task ACROSS R1;\n";
		proc += "assignment.Age = 1;\n";

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
	
	public void test_cant_select_one_if_right_instance_cant_be_identified() throws InvalidActionLanguageSyntaxException
	{
		String proc = "";
		proc += "CREATE user FROM User;\n";
		proc += "CREATE task FROM Task;\n";
		proc += "SELECT ONE assignment THAT RELATES task TO notAnInstance ACROSS R1;\n";
		proc += "assignment.Age = 1;\n";

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
	
	public void test_cant_select_one_if_there_is_no_relationship_between_instances() throws InvalidActionLanguageSyntaxException
	{
		String proc = "";
		proc += "CREATE user FROM User;\n";
		proc += "CREATE task FROM Task;\n";
		proc += "SELECT ONE assignment THAT RELATES task TO user ACROSS R2;\n";

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
	
	public void test_cant_select_one_if_relationship_does_not_exist() throws InvalidActionLanguageSyntaxException
	{
		String proc = "";
		proc += "CREATE user FROM User;\n";
		proc += "CREATE task FROM Task;\n";
		proc += "SELECT ONE assignment THAT RELATES task TO user ACROSS notARelation;\n";

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

