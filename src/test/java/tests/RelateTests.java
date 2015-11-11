package test.java.tests;

import test.java.helper.DomainBus;
import test.java.helper.TestHelper;

import javax.naming.NameNotFoundException;

import junit.framework.Assert;
import junit.framework.TestCase;
import main.java.avii.editor.contracts.metamodel.actionLanguage.syntax.InvalidActionLanguageSyntaxException;
import main.java.avii.editor.metamodel.actionLanguage.validation.error.syntax.CouldNotIdentifyInstanceValidationError;
import main.java.avii.editor.metamodel.actionLanguage.validation.error.syntax.EntityClassNotFoundInRelationValidationError;
import main.java.avii.editor.metamodel.actionLanguage.validation.error.syntax.EntityRelationDoesNotConnectClassesValidationError;
import main.java.avii.editor.metamodel.actionLanguage.validation.error.syntax.EntityRelationIsNotReflexiveValidationError;
import main.java.avii.editor.metamodel.actionLanguage.validation.error.syntax.EntityRelationNeedsAnAssociationClassValidationError;
import main.java.avii.editor.metamodel.actionLanguage.validation.error.syntax.EntityRelationNotFoundInDomainValidationError;
import main.java.avii.editor.metamodel.entities.EntityClass;
import main.java.avii.editor.metamodel.entities.EntityDomain;
import main.java.avii.editor.metamodel.entities.EntityProcedure;
import main.java.avii.editor.metamodel.entities.EntityRelation;
import main.java.avii.editor.metamodel.entities.EntityRelation.CardinalityType;
import main.java.avii.editor.metamodel.entities.EntityState;

	/**
	*     Domain model for relation tests
	* 
	*      +--------+      R1      +------+        R2     +--------+
	*      |  User  |------+-------| Task +---------------|  Step  |----+
	*      +--------+      .       +------+----+          +--------+    | R3
	*                      .           |       | R4             |       |
	*                 +----+-----+     +---+---+                +---+---+
	*                 |Assignment|                                  .
	*                 +----------+                                  .
	*                                                          +----+---+
	*                                                          |Sequence|
	*                                                          +--------+
	* 
	*    (made with JavE -- disable javadoc formatting in eclipse to preserve this)
	*/
public class RelateTests extends TestCase {

	public RelateTests(String name) {
		super(name);
	}

	public void test_domain_setup() throws NameNotFoundException {
		EntityDomain ttd = GetDomain();

		// classes exist
		Assert.assertTrue(ttd.hasEntityClassWithName("User"));
		EntityClass user = ttd.getEntityClassWithName("User");
		Assert.assertTrue(ttd.hasEntityClassWithName("Task"));
		EntityClass task = ttd.getEntityClassWithName("Task");
		Assert.assertTrue(ttd.hasEntityClassWithName("Step"));
		EntityClass step = ttd.getEntityClassWithName("Step");
		Assert.assertTrue(ttd.hasEntityClassWithName("Assignment"));
		EntityClass assignment = ttd.getEntityClassWithName("Assignment");
		Assert.assertTrue(ttd.hasEntityClassWithName("Sequence"));
		EntityClass sequence = ttd.getEntityClassWithName("Sequence");

		
		
		// relations exist
		Assert.assertTrue(ttd.hasRelationWithName("R1"));
		EntityRelation r1 = ttd.getRelationWithName("R1");
		Assert.assertTrue(ttd.hasRelationWithName("R2"));
		EntityRelation r2 = ttd.getRelationWithName("R2");
		Assert.assertTrue(ttd.hasRelationWithName("R3"));
		EntityRelation r3 = ttd.getRelationWithName("R3");
		Assert.assertTrue(ttd.hasRelationWithName("R4"));
		EntityRelation r4 = ttd.getRelationWithName("R4");

		// classes have relations
		Assert.assertTrue(user.hasRelation("R1"));
		Assert.assertTrue(user.hasRelation(r1));
		Assert.assertTrue(task.hasRelation("R1"));
		Assert.assertTrue(task.hasRelation(r1));
		Assert.assertTrue(task.hasRelation("R2"));
		Assert.assertTrue(task.hasRelation(r2));
		Assert.assertTrue(task.hasRelation("R4"));
		Assert.assertTrue(task.hasRelation(r4));
		Assert.assertTrue(step.hasRelation("R2"));
		Assert.assertTrue(step.hasRelation(r2));
		Assert.assertTrue(step.hasRelation("R3"));
		Assert.assertTrue(step.hasRelation(r3));

		// relations have classes
		Assert.assertTrue(r1.hasClassWithName("User"));
		Assert.assertTrue(r1.hasClass(user));
		Assert.assertTrue(r1.hasClassWithName("Task"));
		Assert.assertTrue(r1.hasClass(task));
		Assert.assertTrue(r2.hasClassWithName("Task"));
		Assert.assertTrue(r2.hasClass(task));
		Assert.assertTrue(r2.hasClassWithName("Step"));
		Assert.assertTrue(r2.hasClass(step));
		Assert.assertTrue(r3.hasClassWithName("Step"));
		Assert.assertTrue(r3.hasClass(step));
		Assert.assertTrue(r4.hasClassWithName("Task"));
		Assert.assertTrue(r4.hasClass(task));
		
		// reflexive
		Assert.assertTrue(r3.isReflexive());
		Assert.assertEquals("Leads", r3.getClassAVerb());
		Assert.assertEquals("Follows", r3.getClassBVerb());
		Assert.assertTrue(r4.isReflexive());
		Assert.assertEquals("Leads", r4.getClassAVerb());
		Assert.assertEquals("Follows", r4.getClassBVerb());
		
		
		// association classes
		Assert.assertTrue(r1.hasAssociation());
		Assert.assertEquals(r1.getAssociation(),assignment);
		Assert.assertTrue(r3.hasAssociation());
		Assert.assertEquals(r3.getAssociation(),sequence);
	}

	public EntityDomain GetDomain() {
		EntityDomain ttd = new EntityDomain("TickTickDone");

		// classes
		EntityClass user = new EntityClass("User");
		ttd.addClass(user);
		
		EntityClass task = new EntityClass("Task");
		ttd.addClass(task);
		
		EntityClass step = new EntityClass("Step");
		ttd.addClass(step);
		
		EntityClass assignment = new EntityClass("Assignment");
		ttd.addClass(assignment);
		
		EntityClass sequence = new EntityClass("Sequence");
		ttd.addClass(sequence);
		
		
		// relations
		EntityRelation r1 = new EntityRelation("R1");
		r1.setEndA(user, CardinalityType.ONE_TO_MANY);
		r1.setEndB(task, CardinalityType.ONE_TO_ONE);
		
		EntityRelation r2 = new EntityRelation("R2");
		r2.setEndA(step, CardinalityType.ONE_TO_MANY);
		r2.setEndB(task, CardinalityType.ONE_TO_ONE);
		
		EntityRelation r3 = new EntityRelation("R3");
		r3.setEndA(step, CardinalityType.ONE_TO_ONE,"Leads");
		r3.setEndB(step, CardinalityType.ONE_TO_MANY,"Follows");
		
		EntityRelation r4 = new EntityRelation("R4");
		r4.setEndA(task, CardinalityType.ONE_TO_ONE,"Leads");
		r4.setEndB(task, CardinalityType.ONE_TO_MANY,"Follows");
		
		
		// associations
		r1.setAssociation(assignment);
		r3.setAssociation(sequence);
		
		return ttd;
	}
	
	private String getInitialProc()
	{
		String proc = "";
		proc += "CREATE user FROM User;\n";
		proc += "CREATE task FROM Task;\n";
		proc += "CREATE task2 FROM Task;\n";
		proc += "CREATE step FROM Step;\n";
		proc += "CREATE step2 FROM Step;\n";
		return proc;
	}
	
	public void test_create_relationship_between_defined_classes_with_valid_relation_name() throws InvalidActionLanguageSyntaxException {
		String proc = getInitialProc();
		proc += "RELATE step TO task ACROSS R2;\n";
		
		EntityDomain ttd = GetDomain();
		EntityClass user = ttd.getEntityClassWithName("User");
		EntityState state = new EntityState("Initial");

		user.addState(state);

		EntityProcedure procedure = new EntityProcedure(state);
		procedure.setProcedure(proc);
		
		Assert.assertTrue(procedure.validate());
		Assert.assertEquals(0, TestHelper.countValidationErrors(procedure));
	}
	
	public void test_create_invalid_relationship_between_defined_classes() throws InvalidActionLanguageSyntaxException {
		String proc = getInitialProc();
		proc += "RELATE user TO step ACROSS R2;\n";
		
		EntityDomain ttd = GetDomain();
		EntityClass user = ttd.getEntityClassWithName("User");
		EntityState state = new EntityState("Initial");
		
		user.addState(state);
		
		EntityProcedure procedure = new EntityProcedure(state);
		procedure.setProcedure(proc);
		
		Assert.assertTrue(!procedure.validate());
		Assert.assertEquals(2, TestHelper.countValidationErrors(procedure));
		Assert.assertTrue(TestHelper.checkValidationResultsForAnErrorOfType(procedure, EntityRelationDoesNotConnectClassesValidationError.class));
		Assert.assertTrue(TestHelper.checkValidationResultsForAnErrorOfType(procedure, EntityClassNotFoundInRelationValidationError.class));
	}
	
	public void test_create_invalid_relationship_between_defined_classes_in_opposite_order() throws InvalidActionLanguageSyntaxException {
		String proc = getInitialProc();
		proc += "RELATE step TO user ACROSS R2;\n";
		
		EntityDomain ttd = GetDomain();
		EntityClass user = ttd.getEntityClassWithName("User");
		EntityState state = new EntityState("Initial");
		
		user.addState(state);
		
		EntityProcedure procedure = new EntityProcedure(state);
		procedure.setProcedure(proc);
		
		Assert.assertTrue(!procedure.validate());
		Assert.assertEquals(2, TestHelper.countValidationErrors(procedure));
		Assert.assertTrue(TestHelper.checkValidationResultsForAnErrorOfType(procedure, EntityRelationDoesNotConnectClassesValidationError.class));
		Assert.assertTrue(TestHelper.checkValidationResultsForAnErrorOfType(procedure, EntityClassNotFoundInRelationValidationError.class));
	}

	public void test_create_relationship_between_defined_classes_in_different_order_with_valid_relation_name() throws InvalidActionLanguageSyntaxException {
		String proc = getInitialProc();
		proc += "RELATE task TO step ACROSS R2;\n";
		
		EntityDomain ttd = GetDomain();
		EntityClass user = ttd.getEntityClassWithName("User");
		EntityState state = new EntityState("Initial");
		
		user.addState(state);
		
		EntityProcedure procedure = new EntityProcedure(state);
		procedure.setProcedure(proc);
		
		Assert.assertTrue(procedure.validate());
		Assert.assertEquals(0, TestHelper.countValidationErrors(procedure));
	}

	public void test_create_relationship_between_one_defined_and_one_fake_class_with_valid_relation_name() throws InvalidActionLanguageSyntaxException {
		String proc = getInitialProc();
		proc += "RELATE user TO notAClass ACROSS R2;\n";
		
		EntityDomain ttd = GetDomain();
		EntityClass user = ttd.getEntityClassWithName("User");
		EntityState state = new EntityState("Initial");
		
		user.addState(state);
		
		EntityProcedure procedure = new EntityProcedure(state);
		procedure.setProcedure(proc);
		
		Assert.assertTrue(!procedure.validate());
		Assert.assertEquals(1, TestHelper.countValidationErrors(procedure));
		Assert.assertTrue(TestHelper.checkValidationResultsForAnErrorOfType(procedure, CouldNotIdentifyInstanceValidationError.class));
	}

	public void test_create_relationship_between_one_fake_and_one_defined_class_with_valid_relation_name() throws InvalidActionLanguageSyntaxException {
		String proc = getInitialProc();
		proc += "RELATE notAClass TO fake ACROSS R2;\n";
		
		EntityDomain ttd = GetDomain();
		EntityClass user = ttd.getEntityClassWithName("User");
		EntityState state = new EntityState("Initial");

		user.addState(state);

		EntityProcedure procedure = new EntityProcedure(state);
		procedure.setProcedure(proc);
		
		Assert.assertTrue(!procedure.validate());
		Assert.assertEquals(2, TestHelper.countValidationErrors(procedure));
		Assert.assertTrue(TestHelper.checkValidationResultsForAnErrorOfType(procedure, CouldNotIdentifyInstanceValidationError.class));
		Assert.assertTrue(TestHelper.checkValidationResultsForAnErrorOfType(procedure, CouldNotIdentifyInstanceValidationError.class));
	}

	public void test_create_relationship_between_defined_classes_with_invalid_relation_name() throws InvalidActionLanguageSyntaxException {
		String proc = getInitialProc();
		proc += "RELATE user TO task ACROSS notARelation;\n";
		
		EntityDomain ttd = GetDomain();
		EntityClass user = ttd.getEntityClassWithName("User");
		EntityState state = new EntityState("Initial");
		
		user.addState(state);
		
		EntityProcedure procedure = new EntityProcedure(state);
		procedure.setProcedure(proc);
		
		Assert.assertTrue(!procedure.validate());
		Assert.assertEquals(1, TestHelper.countValidationErrors(procedure));
		Assert.assertTrue(TestHelper.checkValidationResultsForAnErrorOfType(procedure, EntityRelationNotFoundInDomainValidationError.class));
	}

	public void test_create_relationship_between_defined_classes_in_different_order_with_invalid_relation_name() throws InvalidActionLanguageSyntaxException {
		String proc = getInitialProc();
		proc += "RELATE task TO user ACROSS notARelation;\n";
		
		EntityDomain ttd = GetDomain();
		EntityClass user = ttd.getEntityClassWithName("User");
		EntityState state = new EntityState("Initial");
		
		user.addState(state);
		
		EntityProcedure procedure = new EntityProcedure(state);
		procedure.setProcedure(proc);
		
		Assert.assertTrue(!procedure.validate());
		Assert.assertEquals(1, TestHelper.countValidationErrors(procedure));
		Assert.assertTrue(TestHelper.checkValidationResultsForAnErrorOfType(procedure, EntityRelationNotFoundInDomainValidationError.class));
	}

	public void test_create_relationship_between_one_defined_and_one_fake_class_with_invalid_relation_name() throws InvalidActionLanguageSyntaxException {
		String proc = getInitialProc();
		proc += "RELATE task TO notAClass ACROSS notARelation;\n";
		
		EntityDomain ttd = GetDomain();
		EntityClass user = ttd.getEntityClassWithName("User");
		EntityState state = new EntityState("Initial");
		
		user.addState(state);
		
		EntityProcedure procedure = new EntityProcedure(state);
		procedure.setProcedure(proc);
		
		Assert.assertTrue(!procedure.validate());
		Assert.assertEquals(2, TestHelper.countValidationErrors(procedure));
		Assert.assertTrue(TestHelper.checkValidationResultsForAnErrorOfType(procedure, EntityRelationNotFoundInDomainValidationError.class));
		Assert.assertTrue(TestHelper.checkValidationResultsForAnErrorOfType(procedure, CouldNotIdentifyInstanceValidationError.class));
	}

	public void test_create_relationship_between_one_fake_and_one_defined_class_with_invalid_relation_name() throws InvalidActionLanguageSyntaxException {
		String proc = getInitialProc();
		proc += "RELATE notAClass TO task ACROSS notARelation;\n";
		
		EntityDomain ttd = GetDomain();
		EntityClass user = ttd.getEntityClassWithName("User");
		EntityState state = new EntityState("Initial");
		
		user.addState(state);
		
		EntityProcedure procedure = new EntityProcedure(state);
		procedure.setProcedure(proc);
		
		Assert.assertTrue(!procedure.validate());
		Assert.assertEquals(2, TestHelper.countValidationErrors(procedure));
		Assert.assertTrue(TestHelper.checkValidationResultsForAnErrorOfType(procedure, EntityRelationNotFoundInDomainValidationError.class));
		Assert.assertTrue(TestHelper.checkValidationResultsForAnErrorOfType(procedure, CouldNotIdentifyInstanceValidationError.class));
	}

	public void test_relation_cannot_be_reflexive() throws InvalidActionLanguageSyntaxException {
		// this is an internal test, if the relationship is reflexive, then the
		// code to determine syntax
		// has failed
		String proc = getInitialProc();
		proc += "RELATE task TO task2 ACROSS R2;\n";
		
		EntityDomain ttd = GetDomain();
		EntityClass user = ttd.getEntityClassWithName("User");
		EntityState state = new EntityState("Initial");

		user.addState(state);

		EntityProcedure procedure = new EntityProcedure(state);
		procedure.setProcedure(proc);
		
		Assert.assertTrue(!procedure.validate());
		Assert.assertEquals(1, TestHelper.countValidationErrors(procedure));
	}

	
	
	//////////////// REFLEXIVE TESTS //////////////////
	
	
	public void test_refelxive_relation_acceps_relation_to_the_same_class() throws InvalidActionLanguageSyntaxException
	{
		String proc = getInitialProc();
		proc += "RELATE task TO task2 ACROSS R4.\"Leads\";\n";
		
		EntityDomain ttd = GetDomain();
		EntityClass user = ttd.getEntityClassWithName("User");
		EntityState state = new EntityState("Initial");
		
		user.addState(state);
		
		EntityProcedure procedure = new EntityProcedure(state);
		procedure.setProcedure(proc);
		
		Assert.assertTrue(procedure.validate());
		Assert.assertEquals(0, TestHelper.countValidationErrors(procedure));
	}
	
	public void test_reflexive_relation_fails_when_relation_isnt_reflexive() throws InvalidActionLanguageSyntaxException
	{
		String proc = getInitialProc();
		proc += "RELATE step TO step2 ACROSS R2.\"Leads\";\n";
		
		EntityDomain ttd = GetDomain();
		EntityClass user = ttd.getEntityClassWithName("User");
		EntityState state = new EntityState("Initial");
		
		user.addState(state);
		
		EntityProcedure procedure = new EntityProcedure(state);
		procedure.setProcedure(proc);
		
		Assert.assertTrue(!procedure.validate());
		Assert.assertEquals(1, TestHelper.countValidationErrors(procedure));
		Assert.assertTrue(TestHelper.checkValidationResultsForAnErrorOfType(procedure,EntityRelationIsNotReflexiveValidationError.class));
	}
	
	public void test_reflexive_relation_fails_when_different_classes_used() throws InvalidActionLanguageSyntaxException
	{
		String proc = getInitialProc();
		proc += "RELATE task TO step ACROSS R4.\"Leads\";\n";
		
		EntityDomain ttd = GetDomain();
		EntityClass user = ttd.getEntityClassWithName("User");
		EntityState state = new EntityState("Initial");
		
		user.addState(state);
		
		EntityProcedure procedure = new EntityProcedure(state);
		procedure.setProcedure(proc);
		
		Assert.assertTrue(!procedure.validate());
		Assert.assertEquals(1, TestHelper.countValidationErrors(procedure));
	}

	public void test_reflexive_requires_correct_verb_phrases() throws InvalidActionLanguageSyntaxException
	{
		String proc = getInitialProc();
		proc += "RELATE task TO task2 ACROSS R4.\"NotTheCorrectVerbPhrase\";\n";
		
		EntityDomain ttd = GetDomain();
		EntityClass user = ttd.getEntityClassWithName("User");
		EntityState state = new EntityState("Initial");
		
		user.addState(state);
		
		EntityProcedure procedure = new EntityProcedure(state);
		procedure.setProcedure(proc);
		
		Assert.assertTrue(!procedure.validate());
		Assert.assertEquals(1, TestHelper.countValidationErrors(procedure));
	}
	
	
	/////////////////// Association tests ////////////////////
	
	public void test_create_association_relationship_between_defined_classes_with_valid_relation_name() throws InvalidActionLanguageSyntaxException {
		String proc = getInitialProc();
		proc += "RELATE user TO task ACROSS R1 CREATING assignment;\n";
		
		EntityDomain ttd = GetDomain();
		EntityClass user = ttd.getEntityClassWithName("User");
		EntityState state = new EntityState("Initial");
		
		user.addState(state);
		
		EntityProcedure procedure = new EntityProcedure(state);
		procedure.setProcedure(proc);
		
		Assert.assertTrue(procedure.validate());
		Assert.assertEquals(0, TestHelper.countValidationErrors(procedure));
	}
	
	public void test_create_invalid_associationship_relationship_between_defined_classes() throws InvalidActionLanguageSyntaxException {
		String proc = getInitialProc();
		proc += "RELATE user TO step ACROSS R1 CREATING assignment;\n";
		
		EntityDomain ttd = GetDomain();
		EntityClass user = ttd.getEntityClassWithName("User");
		EntityState state = new EntityState("Initial");
		
		user.addState(state);
		
		EntityProcedure procedure = new EntityProcedure(state);
		procedure.setProcedure(proc);
		
		Assert.assertTrue(!procedure.validate());
		Assert.assertEquals(2, TestHelper.countValidationErrors(procedure));
		Assert.assertTrue(TestHelper.checkValidationResultsForAnErrorOfType(procedure, EntityRelationDoesNotConnectClassesValidationError.class));
		Assert.assertTrue(TestHelper.checkValidationResultsForAnErrorOfType(procedure, EntityClassNotFoundInRelationValidationError.class));

	}

	
	public void test_create_invalid_association_relationship_between_defined_classes_in_opposite_order() throws InvalidActionLanguageSyntaxException {
		String proc = getInitialProc();
		proc += "RELATE step TO user ACROSS R1 CREATING assignment;\n";
		
		EntityDomain ttd = GetDomain();
		EntityClass user = ttd.getEntityClassWithName("User");
		EntityState state = new EntityState("Initial");
		
		user.addState(state);
		
		EntityProcedure procedure = new EntityProcedure(state);
		procedure.setProcedure(proc);
		
		Assert.assertTrue(!procedure.validate());
		Assert.assertEquals(2, TestHelper.countValidationErrors(procedure));
		Assert.assertTrue(TestHelper.checkValidationResultsForAnErrorOfType(procedure, EntityRelationDoesNotConnectClassesValidationError.class));
		Assert.assertTrue(TestHelper.checkValidationResultsForAnErrorOfType(procedure, EntityClassNotFoundInRelationValidationError.class));
	}

	public void test_create_association_relationship_between_defined_classes_in_different_order_with_valid_relation_name() throws InvalidActionLanguageSyntaxException {
		String proc = getInitialProc();
		proc += "RELATE task TO user ACROSS R1 CREATING assignment;\n";
		
		EntityDomain ttd = GetDomain();
		EntityClass user = ttd.getEntityClassWithName("User");
		EntityState state = new EntityState("Initial");
		
		user.addState(state);
		
		EntityProcedure procedure = new EntityProcedure(state);
		procedure.setProcedure(proc);
		
		Assert.assertTrue(procedure.validate());
		Assert.assertEquals(0, TestHelper.countValidationErrors(procedure));
	}

	public void test_create_association_relationship_between_one_defined_and_one_fake_class_with_valid_relation_name() throws InvalidActionLanguageSyntaxException {
		String proc = getInitialProc();
		proc += "RELATE user TO notAClass ACROSS R1 CREATING assignment;\n";
		
		EntityDomain ttd = GetDomain();
		EntityClass user = ttd.getEntityClassWithName("User");
		EntityState state = new EntityState("Initial");
		
		user.addState(state);
		
		EntityProcedure procedure = new EntityProcedure(state);
		procedure.setProcedure(proc);
		
		Assert.assertTrue(!procedure.validate());
		Assert.assertEquals(1, TestHelper.countValidationErrors(procedure));
		Assert.assertTrue(TestHelper.checkValidationResultsForAnErrorOfType(procedure, CouldNotIdentifyInstanceValidationError.class));
	}

	public void test_create_association_relationship_between_one_fake_and_one_defined_class_with_valid_relation_name() throws InvalidActionLanguageSyntaxException {
		String proc = getInitialProc();
		proc += "RELATE notAClass TO fake ACROSS R1 CREATING assignment;\n";
		
		EntityDomain ttd = GetDomain();
		EntityClass user = ttd.getEntityClassWithName("User");
		EntityState state = new EntityState("Initial");
		
		user.addState(state);
		
		EntityProcedure procedure = new EntityProcedure(state);
		procedure.setProcedure(proc);
		
		Assert.assertTrue(!procedure.validate());
		Assert.assertEquals(2, TestHelper.countValidationErrors(procedure));
		Assert.assertTrue(TestHelper.checkValidationResultsForAnErrorOfType(procedure, CouldNotIdentifyInstanceValidationError.class));
		Assert.assertTrue(TestHelper.checkValidationResultsForAnErrorOfType(procedure, CouldNotIdentifyInstanceValidationError.class));
	}

	public void test_create_association_relationship_between_defined_classes_with_invalid_relation_name() throws InvalidActionLanguageSyntaxException {
		String proc = getInitialProc();
		proc += "RELATE user TO task ACROSS notARelation CREATING assignment;\n";
		
		EntityDomain ttd = GetDomain();
		EntityClass user = ttd.getEntityClassWithName("User");
		EntityState state = new EntityState("Initial");
		
		user.addState(state);
		
		EntityProcedure procedure = new EntityProcedure(state);
		procedure.setProcedure(proc);
		
		Assert.assertTrue(!procedure.validate());
		Assert.assertEquals(1, TestHelper.countValidationErrors(procedure));
		Assert.assertTrue(TestHelper.checkValidationResultsForAnErrorOfType(procedure, EntityRelationNotFoundInDomainValidationError.class));
	}

	public void test_create_association_relationship_between_defined_classes_in_different_order_with_invalid_relation_name() throws InvalidActionLanguageSyntaxException {
		String proc = getInitialProc();
		proc += "RELATE task TO user ACROSS notARelation CREATING assignment;\n";
		
		EntityDomain ttd = GetDomain();
		EntityClass user = ttd.getEntityClassWithName("User");
		EntityState state = new EntityState("Initial");
		
		user.addState(state);
		
		EntityProcedure procedure = new EntityProcedure(state);
		procedure.setProcedure(proc);
		
		Assert.assertTrue(!procedure.validate());
		Assert.assertEquals(1, TestHelper.countValidationErrors(procedure));
		Assert.assertTrue(TestHelper.checkValidationResultsForAnErrorOfType(procedure, EntityRelationNotFoundInDomainValidationError.class));
	}

	public void test_create_association_relationship_between_one_defined_and_one_fake_class_with_invalid_relation_name() throws InvalidActionLanguageSyntaxException {
		String proc = getInitialProc();
		proc += "RELATE task TO notAClass ACROSS notARelation CREATING assignment;\n";
		
		EntityDomain ttd = GetDomain();
		EntityClass user = ttd.getEntityClassWithName("User");
		EntityState state = new EntityState("Initial");

		user.addState(state);

		EntityProcedure procedure = new EntityProcedure(state);
		procedure.setProcedure(proc);
		
		Assert.assertTrue(!procedure.validate());
		Assert.assertEquals(2, TestHelper.countValidationErrors(procedure));
		Assert.assertTrue(TestHelper.checkValidationResultsForAnErrorOfType(procedure, EntityRelationNotFoundInDomainValidationError.class));
		Assert.assertTrue(TestHelper.checkValidationResultsForAnErrorOfType(procedure, CouldNotIdentifyInstanceValidationError.class));
	}

	public void test_create_association_relationship_between_one_fake_and_one_defined_class_with_invalid_relation_name() throws InvalidActionLanguageSyntaxException {
		String proc = getInitialProc();
		proc += "RELATE notAClass TO task ACROSS notARelation CREATING assignment;\n";
		
		EntityDomain ttd = GetDomain();
		EntityClass user = ttd.getEntityClassWithName("User");
		EntityState state = new EntityState("Initial");
		
		user.addState(state);
		
		EntityProcedure procedure = new EntityProcedure(state);
		procedure.setProcedure(proc);
		
		Assert.assertTrue(!procedure.validate());
		Assert.assertEquals(2, TestHelper.countValidationErrors(procedure));
		Assert.assertTrue(TestHelper.checkValidationResultsForAnErrorOfType(procedure, EntityRelationNotFoundInDomainValidationError.class));
		Assert.assertTrue(TestHelper.checkValidationResultsForAnErrorOfType(procedure, CouldNotIdentifyInstanceValidationError.class));
	}

	public void test_association_relation_cannot_be_reflexive() throws InvalidActionLanguageSyntaxException {
		// this is an internal test, if the relationship is reflexive, then the
		// code to determine syntax
		// has failed
		String proc = getInitialProc();
		proc += "RELATE task TO task2 ACROSS R1 CREATING assignment;\n";
		
		EntityDomain ttd = GetDomain();
		EntityClass user = ttd.getEntityClassWithName("User");
		EntityState state = new EntityState("Initial");
		
		user.addState(state);
		
		EntityProcedure procedure = new EntityProcedure(state);
		procedure.setProcedure(proc);
		
		Assert.assertTrue(!procedure.validate());
		Assert.assertEquals(1, TestHelper.countValidationErrors(procedure));
	}

	
	
	//////////////// REFLEXIVE CREATE TESTS //////////////////
	
	
	public void test_refelxive_association_relation_acceps_relation_to_the_same_class() throws InvalidActionLanguageSyntaxException
	{
		String proc = getInitialProc();
		proc += "RELATE step TO step2 ACROSS R3.\"Leads\" CREATING sequence;\n";
		
		EntityDomain ttd = GetDomain();
		EntityClass user = ttd.getEntityClassWithName("User");
		EntityState state = new EntityState("Initial");
		
		user.addState(state);
		
		EntityProcedure procedure = new EntityProcedure(state);
		procedure.setProcedure(proc);
		
		Assert.assertTrue(procedure.validate());
		Assert.assertEquals(0, TestHelper.countValidationErrors(procedure));
	}
	
	public void test_reflexive_association_relation_fails_when_relation_isnt_reflexive() throws InvalidActionLanguageSyntaxException
	{
		String proc = getInitialProc();
		proc += "RELATE step TO step2 ACROSS R2.\"Leads\" CREATING sequence;\n";
		
		EntityDomain ttd = GetDomain();
		EntityClass user = ttd.getEntityClassWithName("User");
		EntityState state = new EntityState("Initial");
		
		user.addState(state);
		
		EntityProcedure procedure = new EntityProcedure(state);
		procedure.setProcedure(proc);
		
		Assert.assertTrue(!procedure.validate());
		Assert.assertEquals(1, TestHelper.countValidationErrors(procedure));
		Assert.assertTrue(TestHelper.checkValidationResultsForAnErrorOfType(procedure,EntityRelationIsNotReflexiveValidationError.class));
	}
	
	public void test_reflexive_association_relation_fails_when_different_classes_used() throws InvalidActionLanguageSyntaxException
	{
		String proc = getInitialProc();
		proc += "RELATE step TO task ACROSS R3.\"Leads\" CREATING sequence;\n";
		
		EntityDomain ttd = GetDomain();
		EntityClass user = ttd.getEntityClassWithName("User");
		EntityState state = new EntityState("Initial");
		
		user.addState(state);
		
		EntityProcedure procedure = new EntityProcedure(state);
		procedure.setProcedure(proc);
		
		Assert.assertTrue(!procedure.validate());
		Assert.assertEquals(1, TestHelper.countValidationErrors(procedure));
	}

	public void test_reflexive_association_requires_correct_verb_phrases() throws InvalidActionLanguageSyntaxException
	{
		String proc = getInitialProc();
		proc += "RELATE step TO step2 ACROSS R3.\"NotTheCorrectVerbPhrase\" CREATING sequence;\n";
		
		EntityDomain ttd = GetDomain();
		EntityClass user = ttd.getEntityClassWithName("User");
		EntityState state = new EntityState("Initial");
		
		user.addState(state);
		
		EntityProcedure procedure = new EntityProcedure(state);
		procedure.setProcedure(proc);
		
		Assert.assertTrue(!procedure.validate());
		Assert.assertEquals(1, TestHelper.countValidationErrors(procedure));
	}
	
	public void test_creating_reflexive_association_relationship_requires_naming_association_class() throws InvalidActionLanguageSyntaxException
	{
		String proc = getInitialProc();
		proc += "RELATE step TO step2 ACROSS R3.\"Leads\";\n";
		
		EntityDomain ttd = GetDomain();
		EntityClass user = ttd.getEntityClassWithName("User");
		EntityState state = new EntityState("Initial");
		
		user.addState(state);
		
		EntityProcedure procedure = new EntityProcedure(state);
		procedure.setProcedure(proc);
		
		Assert.assertTrue(!procedure.validate());
		Assert.assertEquals(1, TestHelper.countValidationErrors(procedure));
		Assert.assertTrue(TestHelper.checkValidationResultsForAnErrorOfType(procedure,EntityRelationNeedsAnAssociationClassValidationError.class));
	}
	
	public void test_creating_association_relationship_requires_naming_association_class() throws InvalidActionLanguageSyntaxException
	{
		String proc = getInitialProc();
		proc += "RELATE user TO task ACROSS R1;\n";
		
		EntityDomain ttd = GetDomain();
		EntityClass user = ttd.getEntityClassWithName("User");
		EntityState state = new EntityState("Initial");
		
		user.addState(state);
		
		EntityProcedure procedure = new EntityProcedure(state);
		procedure.setProcedure(proc);
		
		Assert.assertTrue(!procedure.validate());
		Assert.assertEquals(1, TestHelper.countValidationErrors(procedure));
		Assert.assertTrue(TestHelper.checkValidationResultsForAnErrorOfType(procedure,EntityRelationNeedsAnAssociationClassValidationError.class));
	}
	
	public void test_can_create_relationship_from_leaf_class_via_super_class_relation() throws InvalidActionLanguageSyntaxException
	{
		String proc = "";
		proc += "CREATE personal FROM Personal;\n";
		proc += "CREATE snowTyre FROM SnowTyre;\n";
		proc += "RELATE personal TO snowTyre ACROSS R1;\n";
		
		EntityDomain busDomain = DomainBus.getBusDomain();
		EntityClass car = busDomain.getEntityClassWithName("Car");
		EntityState state = new EntityState("Initial");
		
		car.addState(state);
		
		EntityProcedure procedure = new EntityProcedure(state);
		procedure.setProcedure(proc);
		
		Assert.assertTrue(procedure.validate());
	}
	
}

