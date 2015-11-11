package test.java.tests;

import test.java.helper.DomainBus;
import test.java.helper.DomainShoppingCart;

import java.util.ArrayList;

import javax.naming.NameAlreadyBoundException;
import javax.naming.NameNotFoundException;
import javax.naming.NamingException;
import javax.naming.OperationNotSupportedException;

import junit.framework.Assert;
import junit.framework.TestCase;
import main.java.avii.editor.contracts.metamodel.actionLanguage.syntax.InvalidActionLanguageSyntaxException;
import main.java.avii.editor.contracts.metamodel.entities.CannotCreateAttributeCalledStateException;
import main.java.avii.editor.contracts.metamodel.entities.CannotRefactorInvalidDomainException;
import main.java.avii.editor.metamodel.actionLanguage.instanceLifespan.InstanceLifespanManager;
import main.java.avii.editor.metamodel.actionLanguage.instanceLifespan.InstanceLifespanRange;
import main.java.avii.editor.metamodel.actionLanguage.instanceLifespan.StateInstanceLifespanManager;
import main.java.avii.editor.metamodel.entities.EntityAttribute;
import main.java.avii.editor.metamodel.entities.EntityClass;
import main.java.avii.editor.metamodel.entities.EntityDomain;
import main.java.avii.editor.metamodel.entities.EntityEventInstance;
import main.java.avii.editor.metamodel.entities.EntityEventParam;
import main.java.avii.editor.metamodel.entities.EntityEventSpecification;
import main.java.avii.editor.metamodel.entities.EntityProcedure;
import main.java.avii.editor.metamodel.entities.EntityRelation;
import main.java.avii.editor.metamodel.entities.EntityRelation.CardinalityType;
import main.java.avii.editor.metamodel.entities.EntityState;
import main.java.avii.editor.metamodel.entities.datatypes.BooleanEntityDatatype;
import main.java.avii.editor.metamodel.entities.datatypes.FloatingEntityDatatype;
import main.java.avii.editor.metamodel.entities.datatypes.IntegerEntityDatatype;
import main.java.avii.editor.metamodel.entities.datatypes.StringEntityDatatype;
import main.java.avii.editor.metamodel.entities.helpers.EntityRelationChainHelper;

public class EntityTests extends TestCase {

	EntityDomain dummyDomain = new EntityDomain("dummyDomain");

	public EntityTests(String name) {
		super(name);
	}

	public void test_can_create_entity_domain_and_get_name() {
		String domainName = "TestDomain";
		EntityDomain entityDomain = new EntityDomain(domainName);

		Assert.assertTrue("DomainName was different then set", domainName.equals(entityDomain.getName()));
	}

	public void test_can_modify_entity_domain_name() {
		String domainName = "TestDomain";
		EntityDomain entityDomain = new EntityDomain(domainName);
		String newDomainName = "NewtestDomain";
		entityDomain.setName(newDomainName);

		Assert.assertTrue("DomainName was different then set", newDomainName.equals(entityDomain.getName()));
	}

	public void test_initial_domain_has0_classes() {
		EntityDomain entityDomain = new EntityDomain("domian");

		Assert.assertEquals("New domain did not have 0 classes!", 0, entityDomain.howManyClasses());
	}

	public void test_adding_class_to_domain_increases_class_count() {
		EntityDomain entityDomain = new EntityDomain("domian");
		int classCount = entityDomain.howManyClasses();
		EntityClass entityClass = new EntityClass("class");

		entityDomain.addClass(entityClass);

		Assert.assertEquals("New domain did not have 0 classes!", classCount + 1, entityDomain.howManyClasses());
	}

	public void test_creating_class_and_getting_name() {
		String className = "newClass";
		EntityClass entityClass = new EntityClass(className);

		Assert.assertTrue(entityClass.getName().equals(className));
	}

	public void test_setting_new_class_name() {
		String className = "newClass";
		EntityClass entityClass = new EntityClass(className);
		String newClassName = "AnotherClassName";
		entityClass.setName(newClassName);

		Assert.assertTrue(entityClass.getName().equals(newClassName));
	}
	
	
	
	public void test_setting_new_class_name_on_domain() throws NamingException, CannotRefactorInvalidDomainException {
		EntityDomain domain = new EntityDomain("a domain");
		String className = "className";
		String newClassName = "AnotherClassName";
		
		EntityClass entityClass = new EntityClass(className);
		entityClass.setId("id1");
		domain.addClass(entityClass);
		Assert.assertTrue(domain.hasEntityClassWithName(className));
		Assert.assertTrue(domain.hasEntityClassWithId("id1"));
		Assert.assertTrue(!domain.hasEntityClassWithName(newClassName));
		
		domain.renameClass("id1", newClassName);

		Assert.assertTrue(entityClass.getName().equals(newClassName));
		Assert.assertTrue(domain.hasEntityClassWithName(newClassName));
	}

	public void test_added_class_has_correct_domain_reference() {
		EntityDomain entityDomain = new EntityDomain("domain");
		EntityClass entityClass = new EntityClass("class");

		entityDomain.addClass(entityClass);

		Assert.assertEquals(entityDomain, entityClass.getDomain());
	}

	public void test_new_class_is_not_an_association_class() {
		EntityClass klass = new EntityClass("class");
		Assert.assertEquals(false, klass.isAssociation());
	}

	public void test_class_can_be_association_class() {
		EntityClass klass = new EntityClass("class");
		EntityRelation relation = new EntityRelation("r");
		klass.setAssociationRelation(relation);

		Assert.assertTrue(klass.isAssociation());
	}

	public void test_association_relation_has_association_class() {
		EntityClass klass = new EntityClass("class");
		dummyDomain.addClass(klass);
		EntityRelation relation = new EntityRelation("r");
		klass.setAssociationRelation(relation);

		Assert.assertEquals(klass, relation.getAssociation());
	}

	public void test_association_class_has_association_relation() {
		EntityClass klass = new EntityClass("class");
		dummyDomain.addClass(klass);
		EntityRelation relation = new EntityRelation("r");

		relation.setAssociation(klass);

		Assert.assertEquals(relation, klass.getAssociationRelation());
	}

	public void test_can_clear_association_for_relation() {
		EntityClass klass = new EntityClass("class");
		dummyDomain.addClass(klass);
		EntityRelation relation = new EntityRelation("r");
		relation.setAssociation(klass);
		Assert.assertEquals(relation, klass.getAssociationRelation());
		Assert.assertTrue(relation.hasAssociation());
		Assert.assertTrue(klass.isAssociation());
		relation.clearAssociation();
		Assert.assertTrue(!relation.hasAssociation());
		Assert.assertTrue(!klass.isAssociation());
	}
	
	
	public void test_can_change_association_class_has_association_relation() {
		EntityClass klass = new EntityClass("class");
		dummyDomain.addClass(klass);
		EntityRelation relation1 = new EntityRelation("r1");
		EntityRelation relation2 = new EntityRelation("r2");
		relation1.setAssociation(klass);
		Assert.assertEquals(relation1, klass.getAssociationRelation());
		Assert.assertTrue(relation1.hasAssociation());
		Assert.assertTrue(!relation2.hasAssociation());
		Assert.assertEquals(klass, relation1.getAssociation());
		relation2.setAssociation(klass);
		Assert.assertEquals(relation2, klass.getAssociationRelation());
		Assert.assertTrue(!relation1.hasAssociation());
		Assert.assertTrue(relation2.hasAssociation());
		Assert.assertEquals(klass, relation2.getAssociation());
	}
	
	
	
	
	public void test_can_create_relation_with_name() {
		EntityClass guest = new EntityClass("Guest");
		EntityClass room = new EntityClass("Room");
		EntityRelation r1 = new EntityRelation("R1");
		r1.setEndA(guest, CardinalityType.ZERO_TO_ONE);
		r1.setEndB(room, CardinalityType.ONE_TO_ONE);

		Assert.assertEquals(guest, r1.getClassA());
		Assert.assertEquals(room, r1.getClassB());
		Assert.assertTrue(!r1.isReflexive());
		
	}
	
	
	public void test_setting_end_of_relation_to_same_class_doesnt_increment_relations_for_class() {
		EntityClass guest = new EntityClass("Guest");
		EntityClass room = new EntityClass("Room");
		EntityClass dummy = new EntityClass("Room");
		
		Assert.assertEquals(0, guest.getRelations().size());
		Assert.assertEquals(0, room.getRelations().size());
		Assert.assertEquals(0, dummy.getRelations().size());
		
		EntityRelation r1 = new EntityRelation("R1");
		r1.setEndA(guest, CardinalityType.ZERO_TO_ONE);
		r1.setEndB(room, CardinalityType.ONE_TO_ONE);
		
		Assert.assertEquals(1, guest.getRelations().size());
		Assert.assertEquals(1, room.getRelations().size());
		Assert.assertEquals(0, dummy.getRelations().size());

		r1.setEndA(guest, CardinalityType.ZERO_TO_ONE);
		
		Assert.assertEquals(1, guest.getRelations().size());
		Assert.assertEquals(1, room.getRelations().size());
		Assert.assertEquals(0, dummy.getRelations().size());
		
		r1.setEndA(dummy, CardinalityType.ZERO_TO_ONE);

		Assert.assertEquals(0, guest.getRelations().size());
		Assert.assertEquals(1, room.getRelations().size());
		Assert.assertEquals(1, dummy.getRelations().size());
		
	}
	
	public void test_can_get_other_end_of_relation() throws NameNotFoundException
	{
		EntityClass guest = new EntityClass("Guest");
		EntityClass room = new EntityClass("Room");
		EntityRelation r1 = new EntityRelation("R1");
		r1.setEndA(guest, CardinalityType.ZERO_TO_ONE);
		r1.setEndB(room, CardinalityType.ONE_TO_ONE);
		
		Assert.assertEquals(guest, r1.getOppositeClass("Room"));
		Assert.assertEquals(room, r1.getOppositeClass("Guest"));
		try
		{
			r1.getOppositeClass("NotAClass");
			fail("Should not get here");
		}catch(NameNotFoundException nfne)
		{}
	}
	

	public void test_does_class_know_what_relations_it_has() {
		EntityClass guest = new EntityClass("Guest");
		EntityRelation r1 = new EntityRelation("R1");
		guest.addRelation(r1);
		Assert.assertTrue(guest.hasRelation("R1"));
	}

	public void test_if_reflexive_relation_is_reflexive() {
		EntityClass entityClass = new EntityClass("carridge");
		EntityRelation r1 = new EntityRelation("r");

		r1.setEndA(entityClass, CardinalityType.ZERO_TO_MANY);
		r1.setEndB(entityClass, CardinalityType.ZERO_TO_MANY);

		Assert.assertTrue(r1.isReflexive());
	}
	
	public void test_reflexive_relation_has_verb_phrase() {
		EntityClass entityClass = new EntityClass("carridge");
		EntityRelation r1 = new EntityRelation("r");

		r1.setEndA(entityClass, CardinalityType.ZERO_TO_MANY, "From");
		r1.setEndB(entityClass, CardinalityType.ZERO_TO_MANY, "To");

		Assert.assertTrue(r1.isReflexive());
		Assert.assertTrue(r1.acceptsVerbPhrase("From"));
		Assert.assertTrue(r1.acceptsVerbPhrase("To"));
		Assert.assertTrue(!r1.acceptsVerbPhrase("NotAVerbPhrase"));
	}
	

	public void test_class_handles_reflexive_relations() {
		EntityClass entityClass = new EntityClass("carridge");
		EntityRelation r1 = new EntityRelation("r");

		r1.setEndA(entityClass, CardinalityType.ZERO_TO_MANY);
		r1.setEndB(entityClass, CardinalityType.ZERO_TO_MANY);

		Assert.assertTrue(entityClass.hasRelation("r"));
		Assert.assertTrue(entityClass.hasRelation(r1));
	}

	public void test_can_relation_identify_other_class() throws NameNotFoundException {
		EntityClass guest = new EntityClass("Guest");
		EntityClass room = new EntityClass("Room");
		EntityRelation r1 = new EntityRelation("R1");
		r1.setEndA(guest, CardinalityType.ZERO_TO_ONE);
		r1.setEndB(room, CardinalityType.ONE_TO_ONE);

		Assert.assertEquals(guest, r1.getClassA());
		Assert.assertEquals(room, r1.getClassB());
		Assert.assertTrue(!r1.isReflexive());
		Assert.assertEquals(room, r1.getOppositeClass(guest));
	}
	
	
	public void test_relation_with_no_classes_reports_that_it_has_no_classes() throws NameNotFoundException {
		EntityClass guest = new EntityClass("Guest");
		EntityClass room = new EntityClass("Room");
		EntityRelation r1 = new EntityRelation("R1");

		Assert.assertEquals(null, r1.getClassA());
		Assert.assertEquals(null, r1.getClassB());
		Assert.assertTrue(!r1.isReflexive());
		Assert.assertTrue(!r1.hasClassWithName(guest.getName()));
		Assert.assertTrue(!r1.hasClassWithName(room.getName()));
	}
	
	
	public void test_relation_can_get_opposite_class_if_opposite_class_is_a_sub_class() throws NameNotFoundException
	{
		EntityDomain busDomain = DomainBus.getBusDomain();
		EntityClass bus = busDomain.getEntityClassWithName("Bus");
		EntityClass tyre = busDomain.getEntityClassWithName("Tyre");
		EntityClass personal = busDomain.getEntityClassWithName("Personal");
		EntityClass roadTyre = busDomain.getEntityClassWithName("RoadTyre");
		EntityRelation r1 = busDomain.getRelationWithName("R1");
		
		Assert.assertEquals(bus, r1.getOppositeClass(roadTyre));
		Assert.assertEquals(tyre, r1.getOppositeClass(personal));
	}
	

	public void test_reflexive_relations_can_identify_opposite_class() throws NameNotFoundException {
		EntityClass entityClass = new EntityClass("carridge");
		EntityRelation r1 = new EntityRelation("r");

		r1.setEndA(entityClass, CardinalityType.ZERO_TO_MANY);
		r1.setEndB(entityClass, CardinalityType.ZERO_TO_MANY);

		Assert.assertTrue(entityClass.hasRelation("r"));
		Assert.assertTrue(entityClass.hasRelation(r1));
		Assert.assertTrue(r1.isReflexive());
		Assert.assertEquals(entityClass, r1.getOppositeClass(entityClass));
	}

	public void test_can_create_state_and_class_gets_state() throws NameNotFoundException, NameAlreadyBoundException {
		EntityClass klass = new EntityClass("room");
		Assert.assertTrue(!klass.hasStateWithName("state1"));
		EntityState state = new EntityState( "state1");
		klass.addState(state);
		Assert.assertTrue(klass.hasStateWithName("state1"));
		Assert.assertEquals(state, klass.getStateWithName("state1"));
		Assert.assertTrue(!klass.hasStateWithName("state2"));
		Assert.assertEquals(null, klass.getStateWithName("state2"));
	}
	
	public void test_is_line_within_range() {
		InstanceLifespanRange range = new InstanceLifespanRange("Person", 3, false);
		Assert.assertTrue(range.isLineWithinRange(5));
	}

	public void test_is_line_not_within_range() {
		InstanceLifespanRange range = new InstanceLifespanRange("Person", 3, false);
		Assert.assertTrue(!range.isLineWithinRange(1));
	}

	public void test_is_line_within_range_on_upper_limit() {
		InstanceLifespanRange range = new InstanceLifespanRange("Person", 3, false);
		Assert.assertTrue(range.isLineWithinRange(3));
	}

	public void test_is_line_within_range_with_upper_limit() {
		InstanceLifespanRange range = new InstanceLifespanRange("Person", 3, false);
		range.addEndLine(5);
		Assert.assertTrue(range.isLineWithinRange(4));
	}

	public void test_cant_add_end_line_less_then_start_line() {
		InstanceLifespanRange range = new InstanceLifespanRange("Person", 3, false);
		try {
			range.addEndLine(2);
			fail("2 is less then 3");
		} catch (IndexOutOfBoundsException e) {

		}
	}

	public void test_can_add_end_line_equal_to_then_start_line() {
		InstanceLifespanRange range = new InstanceLifespanRange("Person", 3, false);
		range.addEndLine(3);
	}

	public void test_can_check_if_life_span_was_created_on_or_after_line_number() throws NameNotFoundException, OperationNotSupportedException {
		InstanceLifespanManager lifespan = new InstanceLifespanManager("fred");
		lifespan.start(4, "Person", false);
		Assert.assertTrue(lifespan.wasStartedOnOrAfterLineNumber(4));
		Assert.assertTrue(lifespan.wasStartedOnOrAfterLineNumber(3));
		Assert.assertTrue(!lifespan.wasStartedOnOrAfterLineNumber(5));
		lifespan.end(6);
		Assert.assertTrue(!lifespan.wasStartedOnOrAfterLineNumber(6));
		Assert.assertTrue(!lifespan.wasStartedOnOrAfterLineNumber(7));
	}
	
	
	public void test_can_add_enable_instance_modifier() throws NameNotFoundException, OperationNotSupportedException {
		InstanceLifespanManager lifespan = new InstanceLifespanManager("fred");
		lifespan.start(4, "Person", false);
		Assert.assertEquals("Person", lifespan.getClassNameAtLine(5));
	}

	public void test_can_add_disable_instance_modifier() throws NameNotFoundException, OperationNotSupportedException {
		InstanceLifespanManager lifespan = new InstanceLifespanManager("fred");
		lifespan.start(4, "Person", false);
		lifespan.end(6);
		Assert.assertEquals("Person", lifespan.getClassNameAtLine(5));
	}

	public void test_cant_add_second_enable_instance_modifier_before_close() throws NameNotFoundException, OperationNotSupportedException {
		InstanceLifespanManager lifespan = new InstanceLifespanManager("fred");
		lifespan.start(4, "Person", false);
		try {
			lifespan.start(6, "Person2", false);
			fail();
		} catch (OperationNotSupportedException e) {
		}
		Assert.assertEquals("Person", lifespan.getClassNameAtLine(5));
	}

	public void test_cant_add_second_disable_instance_modifier_before_open() throws NameNotFoundException, OperationNotSupportedException {
		InstanceLifespanManager lifespan = new InstanceLifespanManager("fred");
		lifespan.start(4, "Person", false);
		lifespan.end(6);
		try {
			lifespan.end(8);
			fail();
		} catch (OperationNotSupportedException e) {
		}
		Assert.assertEquals("Person", lifespan.getClassNameAtLine(5));
	}

	public void test_can_add_disable_instance_modifier_and_get_invalid_line() throws NameNotFoundException, OperationNotSupportedException {
		InstanceLifespanManager lifespan = new InstanceLifespanManager("fred");
		lifespan.start(4, "Person", false);
		lifespan.end(6);
		try {
			lifespan.getClassNameAtLine(7);
			fail();
		} catch (NameNotFoundException e) {

		}
	}

	public void test_can_add_second_enable_instance_modifier() throws NameNotFoundException, OperationNotSupportedException {
		InstanceLifespanManager lifespan = new InstanceLifespanManager("fred");
		lifespan.start(4, "Person", false);
		lifespan.end(6);
		lifespan.start(10, "Worker", false);
		Assert.assertEquals("Person", lifespan.getClassNameAtLine(5));
		Assert.assertEquals("Worker", lifespan.getClassNameAtLine(11));
	}

	public void test_can_add_second_disable_instance_modifier() throws NameNotFoundException, OperationNotSupportedException {
		InstanceLifespanManager lifespan = new InstanceLifespanManager("fred");
		lifespan.start(4, "Person", false);
		lifespan.end(6);
		lifespan.start(10, "Worker", false);
		lifespan.end(15);
		Assert.assertEquals("Person", lifespan.getClassNameAtLine(5));
		Assert.assertEquals("Worker", lifespan.getClassNameAtLine(11));
	}

	public void test_can_add_second_disable_instance_modifier_and_line_larger() throws NameNotFoundException, OperationNotSupportedException {
		InstanceLifespanManager lifespan = new InstanceLifespanManager("fred");
		lifespan.start(4, "Person", false);
		lifespan.end(6);
		lifespan.start(10, "Worker", false);
		lifespan.end(15);
		Assert.assertEquals("Person", lifespan.getClassNameAtLine(5));
		try {
			lifespan.getClassNameAtLine(16);
			fail();
		} catch (NameNotFoundException e) {
		}
	}

	public void test_cant_add_overlapping_instance_modifier() throws NameNotFoundException, OperationNotSupportedException {
		InstanceLifespanManager lifespan = new InstanceLifespanManager("fred");
		lifespan.start(4, "Person", false);
		lifespan.end(10);
		try {
			lifespan.start(8, "Worker", false);
			fail("Can't add overlapping ranges");
		} catch (OperationNotSupportedException e) {
		}
	}

	public void test_can_add_single_instnce_to_state_instance_manager() throws NameNotFoundException, OperationNotSupportedException {
		StateInstanceLifespanManager stateInstance = new StateInstanceLifespanManager();
		stateInstance.beginDeclaration("fred", "Person", false, 4);
		Assert.assertEquals("Person", stateInstance.identifyInstance("fred", 10));
	}

	public void test_can_add_single_instnce_end_declaration_to_state_instance_manager() throws NameNotFoundException, OperationNotSupportedException {
		StateInstanceLifespanManager stateInstance = new StateInstanceLifespanManager();
		stateInstance.beginDeclaration("fred", "Person", false, 4);
		stateInstance.endDeclaration("fred", 15);
		Assert.assertEquals("Person", stateInstance.identifyInstance("fred", 10));
	}

	public void test_can_add_multiple_instnce_to_state_instance_manager() throws NameNotFoundException, OperationNotSupportedException {
		StateInstanceLifespanManager stateInstance = new StateInstanceLifespanManager();
		stateInstance.beginDeclaration("fred", "Person", false, 4);
		stateInstance.endDeclaration("fred", 15);

		stateInstance.beginDeclaration("fred", "Person", false, 20);
		stateInstance.endDeclaration("fred", 30);

		Assert.assertEquals("Person", stateInstance.identifyInstance("fred", 10));
		Assert.assertEquals("Person", stateInstance.identifyInstance("fred", 25));
	}

	public void test_can_add_multiple_overlapping_instnce_to_state_instance_manager() throws NameNotFoundException, OperationNotSupportedException {
		StateInstanceLifespanManager stateInstance = new StateInstanceLifespanManager();
		stateInstance.beginDeclaration("fred", "Person", false, 4);
		stateInstance.endDeclaration("fred", 15);

		stateInstance.beginDeclaration("sam", "Person", false, 3);
		stateInstance.endDeclaration("sam", 10);

		Assert.assertEquals("Person", stateInstance.identifyInstance("fred", 10));
		Assert.assertEquals("Person", stateInstance.identifyInstance("sam", 7));
	}

	public void test_can_add_multiple_interleaved_overlapping_instnce_to_state_instance_manager() throws NameNotFoundException, OperationNotSupportedException {
		StateInstanceLifespanManager stateInstance = new StateInstanceLifespanManager();
		stateInstance.beginDeclaration("fred", "Person", false, 4);
		stateInstance.beginDeclaration("sam", "Person", false, 3);

		stateInstance.endDeclaration("fred", 15);
		stateInstance.endDeclaration("sam", 10);

		Assert.assertEquals("Person", stateInstance.identifyInstance("fred", 10));
		Assert.assertEquals("Person", stateInstance.identifyInstance("sam", 7));
	}
	
	
	public void test_can_close_all_instance_declarations_that_have_occured_after_a_line_number() throws NameNotFoundException, OperationNotSupportedException {
		StateInstanceLifespanManager stateInstance = new StateInstanceLifespanManager();
		stateInstance.beginDeclaration("fred", "Person", false, 4);
		stateInstance.closeAllOpenDeclarationsThatWereOpenedOnOrAfterLine(2,10);
		Assert.assertEquals("Person", stateInstance.identifyInstance("fred", 9));
		try
		{
			Assert.assertEquals("Person", stateInstance.identifyInstance("fred", 20));
			fail();
		}catch(Exception e){}
	}
	

	public void test_determine_class_from_instance_calculating_lifespan() throws NameNotFoundException, OperationNotSupportedException,
			InvalidActionLanguageSyntaxException, NameAlreadyBoundException {
		String proc = "CREATE fred FROM Person;";
		EntityDomain domain = new EntityDomain("domain");
		EntityClass dummy = new EntityClass("Person");
		domain.addClass(dummy);

		EntityState state = new EntityState("state");
		dummy.addState(state);
		state.getProcedure().setProcedure(proc);

		Assert.assertEquals("Person", state.getProcedure().identifyClass("fred", 1).getName());
	}

	public void test_determine_multiple_classes_from_instance_calculating_lifespan() throws NameNotFoundException, OperationNotSupportedException,
			InvalidActionLanguageSyntaxException, NameAlreadyBoundException {
		String proc = "CREATE fred FROM Person;\n";
		proc += "CREATE r112 FROM Room;";
		EntityDomain domain = new EntityDomain("domain");

		EntityClass person = new EntityClass("Person");
		EntityClass room = new EntityClass("Room");
		domain.addClass(person);
		domain.addClass(room);

		EntityState state = new EntityState("state");
		person.addState(state);
		state.getProcedure().setProcedure(proc);

		Assert.assertEquals("Person", state.getProcedure().identifyClass("fred", 1).getName());
		Assert.assertEquals("Room", state.getProcedure().identifyClass("r112", 2).getName());
	}

	public void test_determine_multiple_classes_from_instance_calculating_lifespan_with_invalid_line() throws NameNotFoundException, OperationNotSupportedException,
			InvalidActionLanguageSyntaxException, NameAlreadyBoundException {
		String proc = "CREATE fred FROM Person;\n";
		proc += "CREATE r112 FROM Room;";
		EntityDomain domain = new EntityDomain("domain");

		EntityClass person = new EntityClass("Person");
		EntityClass room = new EntityClass("Room");
		domain.addClass(person);
		domain.addClass(room);

		EntityState state = new EntityState("state");
		person.addState(state);
		state.getProcedure().setProcedure(proc);

		Assert.assertEquals("Person", state.getProcedure().identifyClass("fred", 1).getName());
		try {
			state.getProcedure().identifyClass("r112", 1);
			fail();
		} catch (NameNotFoundException e) {

		}
	}

	public void test_destroy_calculating_lifespan() throws NameNotFoundException, OperationNotSupportedException, InvalidActionLanguageSyntaxException, NameAlreadyBoundException {
		String proc = "CREATE fred FROM Person;\n";
		proc += "DELETE fred;";

		EntityDomain domain = new EntityDomain("domain");
		EntityClass dummy = new EntityClass("Person");
		domain.addClass(dummy);

		EntityState state = new EntityState("state");
		dummy.addState(state);
		state.getProcedure().setProcedure(proc);

		Assert.assertEquals("Person", state.getProcedure().identifyClass("fred", 1).getName());
		try {
			state.getProcedure().identifyClass("fred", 3);
			fail();
		} catch (Exception e) {
		}
	}

	public void test_destroy_while_preserving_calculating_lifespan() throws NameNotFoundException, OperationNotSupportedException,
			InvalidActionLanguageSyntaxException, NameAlreadyBoundException {
		String proc = "CREATE fred FROM Person;\n";
		proc += "CREATE bob FROM Person;\n";
		proc += "DELETE bob;\n";
		proc += "DELETE fred;";

		EntityDomain domain = new EntityDomain("domain");
		EntityClass fred = new EntityClass("Person");
		domain.addClass(fred);

		EntityClass bob = new EntityClass("Person");
		domain.addClass(bob);

		EntityState state = new EntityState("state");
		fred.addState(state);
		state.getProcedure().setProcedure(proc);

		Assert.assertEquals("Person", state.getProcedure().identifyClass("fred", 1).getName());
		Assert.assertEquals("Person", state.getProcedure().identifyClass("bob", 2).getName());
		Assert.assertEquals("Person", state.getProcedure().identifyClass("fred", 4).getName());

		try {
			state.getProcedure().identifyClass("bob", 4);
			fail();
		} catch (Exception e) {
		}
	}

	public void test_relation_knows_which_class_has_exiting_relation() throws NameNotFoundException {
		EntityDomain domain = new EntityDomain("domain");
		EntityClass guest = new EntityClass("Guest");
		EntityClass room = new EntityClass("Room");

		domain.addClass(guest);
		domain.addClass(room);

		EntityRelation r1 = new EntityRelation("R1");
		r1.setEndA(guest, CardinalityType.ZERO_TO_ONE);
		r1.setEndB(room, CardinalityType.ONE_TO_ONE);

		EntityRelation rGuest = new EntityRelation("RGuest");
		rGuest.setEndA(guest, CardinalityType.ZERO_TO_ONE);
		rGuest.setEndB(guest, CardinalityType.ONE_TO_ONE);

		EntityRelation rRoom = new EntityRelation("RRoom");
		rRoom.setEndA(room, CardinalityType.ZERO_TO_ONE);
		rRoom.setEndB(room, CardinalityType.ONE_TO_ONE);

		Assert.assertEquals(guest, r1.getClassA());
		Assert.assertEquals(room, r1.getClassB());
		Assert.assertTrue(!r1.isReflexive());
		Assert.assertEquals(room, r1.getOppositeClass(guest));
		Assert.assertEquals(guest, r1.getOppositeClass(room));

		Assert.assertEquals(guest, r1.getClassWithRelation("RGuest"));
		Assert.assertEquals(room, r1.getClassWithRelation("RRoom"));
	}

	public void test_relation_fails_when_neither_class_has_exiting_relation() throws NameNotFoundException {
		EntityClass guest = new EntityClass("Guest");
		EntityClass room = new EntityClass("Room");

		EntityRelation r1 = new EntityRelation("R1");
		r1.setEndA(guest, CardinalityType.ZERO_TO_ONE);
		r1.setEndB(room, CardinalityType.ONE_TO_ONE);

		EntityRelation rGuest = new EntityRelation("RGuest");
		rGuest.setEndA(guest, CardinalityType.ZERO_TO_ONE);
		rGuest.setEndB(guest, CardinalityType.ONE_TO_ONE);

		EntityRelation rRoom = new EntityRelation("RRoom");
		rRoom.setEndA(guest, CardinalityType.ZERO_TO_ONE);
		rRoom.setEndB(guest, CardinalityType.ONE_TO_ONE);

		Assert.assertEquals(guest, r1.getClassA());
		Assert.assertEquals(room, r1.getClassB());
		Assert.assertTrue(!r1.isReflexive());
		Assert.assertEquals(room, r1.getOppositeClass(guest));
		Assert.assertEquals(guest, r1.getOppositeClass(room));

		try {
			r1.getClassWithRelation("RFail");
			fail();
		} catch (Exception e) {
		}
	}

	public void test_domain_knows_about_relations() throws NameNotFoundException {
		EntityDomain domain = new EntityDomain("domain");
		EntityClass guest = new EntityClass("Guest");
		EntityClass room = new EntityClass("Room");
		domain.addClass(guest);
		domain.addClass(room);

		EntityRelation r1 = new EntityRelation("R1");
		r1.setEndA(guest, CardinalityType.ZERO_TO_ONE);
		r1.setEndB(room, CardinalityType.ONE_TO_ONE);

		EntityRelation foundRelation = domain.getRelationWithName(r1.getName());

		Assert.assertEquals(guest, foundRelation.getClassA());
		Assert.assertEquals(room, foundRelation.getClassB());
		Assert.assertTrue(!foundRelation.isReflexive());
	}

	public void test_relation_can_identify_class_at_end_of_chain() throws NameNotFoundException {
		EntityDomain domain = new EntityDomain("domainName");
		EntityClass a = new EntityClass("a");
		EntityClass b = new EntityClass("b");
		EntityClass c = new EntityClass("c");
		EntityClass d = new EntityClass("d");

		domain.addClass(a);
		domain.addClass(b);
		domain.addClass(c);
		domain.addClass(d);

		EntityRelation ab = new EntityRelation("ab");
		ab.setEndA(a, CardinalityType.ZERO_TO_ONE);
		ab.setEndB(b, CardinalityType.ONE_TO_ONE);

		EntityRelation bc = new EntityRelation("bc");
		bc.setEndA(b, CardinalityType.ZERO_TO_ONE);
		bc.setEndB(c, CardinalityType.ONE_TO_ONE);

		EntityRelation cd = new EntityRelation("cd");
		cd.setEndA(d, CardinalityType.ZERO_TO_ONE);
		cd.setEndB(c, CardinalityType.ONE_TO_ONE);

		// a->ab->bc->cd
		Assert.assertEquals(b, ab.getClassWithRelation("bc"));
		Assert.assertEquals(c, bc.getClassWithRelation("cd"));
		Assert.assertEquals(d, cd.getClassWithRelation("cd"));
	}

	public void test_entity_relation_chain_can_identify_end() throws NameNotFoundException {
		// someA->ab->bc->cd
		ArrayList<String> relations = new ArrayList<String>();
		relations.add("ab");
		relations.add("bc");
		relations.add("cd");

		String startingPointClass = "a";
		String expectedEndpointType = "d";

		EntityDomain domain = new EntityDomain("domainName");
		EntityClass a = new EntityClass("a");
		EntityClass b = new EntityClass("b");
		EntityClass c = new EntityClass("c");
		EntityClass d = new EntityClass("d");

		domain.addClass(a);
		domain.addClass(b);
		domain.addClass(c);
		domain.addClass(d);

		EntityRelation ab = new EntityRelation("ab");
		ab.setEndA(a, CardinalityType.ZERO_TO_ONE);
		ab.setEndB(b, CardinalityType.ONE_TO_ONE);

		EntityRelation bc = new EntityRelation("bc");
		bc.setEndA(b, CardinalityType.ZERO_TO_ONE);
		bc.setEndB(c, CardinalityType.ONE_TO_ONE);

		EntityRelation cd = new EntityRelation("cd");
		cd.setEndA(d, CardinalityType.ZERO_TO_ONE);
		cd.setEndB(c, CardinalityType.ONE_TO_ONE);

		String foundEndPointType = EntityRelationChainHelper.determineEndpointType(domain, relations, startingPointClass);
		Assert.assertEquals(expectedEndpointType, foundEndPointType);
	}

	public void test_entity_relation_chain_can_identify_end_of_reflexive_relation() throws NameNotFoundException {
		// someA->ab->bb->bc->cd
		ArrayList<String> relations = new ArrayList<String>();
		relations.add("ab");
		relations.add("bb");
		relations.add("bb");
		relations.add("bb");
		relations.add("bc");
		relations.add("cd");

		String startingPointClass = "a";
		String expectedEndpointType = "d";

		EntityDomain domain = new EntityDomain("domainName");
		EntityClass a = new EntityClass("a");
		EntityClass b = new EntityClass("b");
		EntityClass c = new EntityClass("c");
		EntityClass d = new EntityClass("d");

		domain.addClass(a);
		domain.addClass(b);
		domain.addClass(c);
		domain.addClass(d);

		EntityRelation ab = new EntityRelation("ab");
		ab.setEndA(a, CardinalityType.ZERO_TO_ONE);
		ab.setEndB(b, CardinalityType.ONE_TO_ONE);

		EntityRelation bb = new EntityRelation("bb");
		bb.setEndA(b, CardinalityType.ZERO_TO_ONE);
		bb.setEndB(b, CardinalityType.ONE_TO_ONE);

		EntityRelation bc = new EntityRelation("bc");
		bc.setEndA(b, CardinalityType.ZERO_TO_ONE);
		bc.setEndB(c, CardinalityType.ONE_TO_ONE);

		EntityRelation cd = new EntityRelation("cd");
		cd.setEndA(d, CardinalityType.ZERO_TO_ONE);
		cd.setEndB(c, CardinalityType.ONE_TO_ONE);

		String foundEndPointType = EntityRelationChainHelper.determineEndpointType(domain, relations, startingPointClass);
		Assert.assertEquals(expectedEndpointType, foundEndPointType);
	}

	public void test_entity_relation_chain_can_identify_end_using_lifespan() throws NameNotFoundException, OperationNotSupportedException,
			InvalidActionLanguageSyntaxException, NameAlreadyBoundException {
		// someA->ab->bc->cd
		ArrayList<String> relations = new ArrayList<String>();
		relations.add("ab");
		relations.add("bc");
		relations.add("cd");

		String expectedEndpointType = "d";

		EntityDomain domain = new EntityDomain("domainName");
		EntityClass a = new EntityClass("a");
		EntityClass b = new EntityClass("b");
		EntityClass c = new EntityClass("c");
		EntityClass d = new EntityClass("d");

		domain.addClass(a);
		domain.addClass(b);
		domain.addClass(c);
		domain.addClass(d);

		EntityRelation ab = new EntityRelation("ab");
		ab.setEndA(a, CardinalityType.ZERO_TO_ONE);
		ab.setEndB(b, CardinalityType.ONE_TO_ONE);

		EntityRelation bc = new EntityRelation("bc");
		bc.setEndA(b, CardinalityType.ZERO_TO_ONE);
		bc.setEndB(c, CardinalityType.ONE_TO_ONE);

		EntityRelation cd = new EntityRelation("cd");
		cd.setEndA(d, CardinalityType.ZERO_TO_ONE);
		cd.setEndB(c, CardinalityType.ONE_TO_ONE);

		String proc = "";
		proc = "CREATE startA FROM a;\n";
		proc += "SELECT ANY end RELATED BY startA->ab->bc->cd;\n";

		EntityState state = new EntityState("state");
		a.addState(state);
		state.getProcedure().setProcedure(proc);

		String foundEndPointType = state.getProcedure().identifyClass("end", 3).getName();
		Assert.assertEquals(expectedEndpointType, foundEndPointType);
	}

	public void test_entity_relation_chain_can_identify_end_using_lifespan_on_reflexive() throws NameNotFoundException, OperationNotSupportedException,
			InvalidActionLanguageSyntaxException, NameAlreadyBoundException {
		// someA->ab->bb->bc->cd
		ArrayList<String> relations = new ArrayList<String>();
		relations.add("ab");
		relations.add("bb");
		relations.add("bb");
		relations.add("bb");
		relations.add("bc");
		relations.add("cd");

		String expectedEndpointType = "d";

		EntityDomain domain = new EntityDomain("domainName");
		EntityClass a = new EntityClass("a");
		EntityClass b = new EntityClass("b");
		EntityClass c = new EntityClass("c");
		EntityClass d = new EntityClass("d");

		domain.addClass(a);
		domain.addClass(b);
		domain.addClass(c);
		domain.addClass(d);

		EntityRelation ab = new EntityRelation("ab");
		ab.setEndA(a, CardinalityType.ZERO_TO_ONE);
		ab.setEndB(b, CardinalityType.ONE_TO_ONE);

		EntityRelation bb = new EntityRelation("bb");
		bb.setEndA(b, CardinalityType.ZERO_TO_ONE);
		bb.setEndB(b, CardinalityType.ONE_TO_ONE);

		EntityRelation bc = new EntityRelation("bc");
		bc.setEndA(b, CardinalityType.ZERO_TO_ONE);
		bc.setEndB(c, CardinalityType.ONE_TO_ONE);

		EntityRelation cd = new EntityRelation("cd");
		cd.setEndA(d, CardinalityType.ZERO_TO_ONE);
		cd.setEndB(c, CardinalityType.ONE_TO_ONE);

		String proc = "";
		proc = "CREATE startA FROM a;\n";
		proc += "SELECT ANY end RELATED BY startA->ab->bb->bb->bb->bc->cd;\n";

		EntityState state = new EntityState("state");
		a.addState(state);
		state.getProcedure().setProcedure(proc);

		String foundEndPointType = state.getProcedure().identifyClass("end", 3).getName();
		Assert.assertEquals(expectedEndpointType, foundEndPointType);
	}

	public void test_determine_class_from_instance_calculating_lifespan_for_each() throws NameNotFoundException, OperationNotSupportedException,
			InvalidActionLanguageSyntaxException, NameAlreadyBoundException {
		String proc = "CREATE fred FROM Person;\n";
		proc += "FOR f IN fred DO\n";

		EntityDomain domain = new EntityDomain("domain");
		EntityClass dummy = new EntityClass("Person");
		domain.addClass(dummy);

		EntityState state = new EntityState("state");
		dummy.addState(state);
		state.getProcedure().setProcedure(proc);

		Assert.assertEquals("Person", state.getProcedure().identifyClass("f", 3).getName());
	}

	public void test_determine_class_from_instance_calculating_lifespan_for_reclassify() throws NameNotFoundException, OperationNotSupportedException,
			InvalidActionLanguageSyntaxException, NameAlreadyBoundException {
		
		String proc = "#self is a Person;\n";
		proc += "RECLASSIFY TO Cpu;\n";

		EntityDomain domain = new EntityDomain("domain");
		EntityClass person = new EntityClass("Person");
		EntityClass cpu = new EntityClass("Cpu");
		domain.addClass(person);
		domain.addClass(cpu);

		EntityState state = new EntityState("state");
		person.addState(state);
		state.getProcedure().setProcedure(proc);

		Assert.assertEquals("Person", state.getProcedure().identifyClass("self", 1).getName());
		Assert.assertEquals("Cpu", state.getProcedure().identifyClass("self", 3).getName());
	}

	public void test_can_create_and_identify_association_instance() throws InvalidActionLanguageSyntaxException, NameNotFoundException,
			OperationNotSupportedException, NameAlreadyBoundException {
		EntityDomain domain = new EntityDomain("Hotel");
		EntityClass guest = new EntityClass("Guest");
		EntityClass room = new EntityClass("Room");
		EntityClass booking = new EntityClass("Booking");

		domain.addClass(guest);
		domain.addClass(room);
		domain.addClass(booking);

		EntityRelation r1 = new EntityRelation("r1");

		r1.setEndA(guest, CardinalityType.ZERO_TO_MANY);
		r1.setEndB(room, CardinalityType.ZERO_TO_MANY);
		r1.setAssociation(booking);

		Assert.assertTrue(guest.hasRelation("r1"));
		Assert.assertTrue(room.hasRelation(r1));
		Assert.assertTrue(!r1.isReflexive());
		Assert.assertEquals(guest, r1.getOppositeClass(room));
		Assert.assertEquals(room, r1.getOppositeClass(guest));
		Assert.assertEquals(booking, r1.getAssociation());

		String proc = "";
		proc += "RELATE guest TO room ACROSS r1 CREATING aBooking;\n";

		EntityState state = new EntityState("state");
		guest.addState(state);
		state.getProcedure().setProcedure(proc);

		Assert.assertEquals("Booking", state.getProcedure().identifyClass("aBooking", 2).getName());
	}

	public void test_can_identify_association_instance() throws InvalidActionLanguageSyntaxException, NameNotFoundException, OperationNotSupportedException, NameAlreadyBoundException {
		EntityDomain domain = new EntityDomain("Hotel");
		EntityClass guest = new EntityClass("Guest");
		EntityClass room = new EntityClass("Room");
		EntityClass booking = new EntityClass("Booking");

		domain.addClass(guest);
		domain.addClass(room);
		domain.addClass(booking);
		EntityRelation r1 = new EntityRelation("r1");

		r1.setEndA(guest, CardinalityType.ZERO_TO_MANY);
		r1.setEndB(room, CardinalityType.ZERO_TO_MANY);
		r1.setAssociation(booking);

		Assert.assertTrue(guest.hasRelation("r1"));
		Assert.assertTrue(room.hasRelation(r1));
		Assert.assertTrue(!r1.isReflexive());
		Assert.assertEquals(guest, r1.getOppositeClass(room));
		Assert.assertEquals(room, r1.getOppositeClass(guest));
		Assert.assertEquals(booking, r1.getAssociation());

		String proc = "";
		proc += "SELECT ONE aBooking THAT RELATES guest TO room ACROSS r1;\n";

		EntityState state = new EntityState("state");
		guest.addState(state);
		state.getProcedure().setProcedure(proc);

		Assert.assertEquals("Booking", state.getProcedure().identifyClass("aBooking", 2).getName());
	}

	public void test_can_create_attribute() throws NameAlreadyBoundException {
		EntityClass person = new EntityClass("Person");
		EntityAttribute attribute = new EntityAttribute("Age", IntegerEntityDatatype.getInstance());
		person.addAttribute(attribute);
		Assert.assertTrue(person.hasAttribute("Age"));
	}
	
	
	public void test_cant_create_attribute_with_conflicting_name() throws NameAlreadyBoundException {
		EntityClass person = new EntityClass("Person");
		EntityAttribute attribute = new EntityAttribute("Age", IntegerEntityDatatype.getInstance());
		person.addAttribute(attribute);
		Assert.assertTrue(person.hasAttribute("Age"));
		try
		{
			EntityAttribute attribute2 = new EntityAttribute("Age", IntegerEntityDatatype.getInstance());
			person.addAttribute(attribute2);
			fail();
		}
		catch(Exception e)
		{}
	}
	
	
	public void test_can_chagnge_attribute_type() throws NameAlreadyBoundException {
		EntityClass person = new EntityClass("Person");
		EntityAttribute attribute = new EntityAttribute("Age", IntegerEntityDatatype.getInstance());
		person.addAttribute(attribute);
		Assert.assertEquals(IntegerEntityDatatype.getInstance(), attribute.getType());
		attribute.setType(FloatingEntityDatatype.getInstance());
		Assert.assertEquals(FloatingEntityDatatype.getInstance(), attribute.getType());
	}

	public void test_can_chagnge_attribute_name() throws NameNotFoundException, NameAlreadyBoundException {
		EntityClass person = new EntityClass("Person");
		EntityAttribute attribute = new EntityAttribute("Age", IntegerEntityDatatype.getInstance());
		person.addAttribute(attribute);
		Assert.assertTrue(person.hasAttribute("Age"));
		attribute.rename("GregorianAge");
		Assert.assertTrue(!person.hasAttribute("Age"));
		Assert.assertTrue(person.hasAttribute("GregorianAge"));
	}

	public void test_can_delete_attribute() throws NameNotFoundException, NameAlreadyBoundException {
		EntityClass person = new EntityClass("Person");
		EntityAttribute attribute = new EntityAttribute("Age", IntegerEntityDatatype.getInstance());
		person.addAttribute(attribute);
		Assert.assertTrue(person.hasAttribute("Age"));
		person.deleteAttribute(attribute);
		Assert.assertTrue(!person.hasAttribute("Age"));
	}
	
	@SuppressWarnings("unused")
	public void test_cant_delete_attribute_if_not_on_class() throws NameAlreadyBoundException{
		EntityClass person = new EntityClass("Person");
		EntityClass person2 = new EntityClass("Person2");
		EntityAttribute attribute = new EntityAttribute("Age", IntegerEntityDatatype.getInstance());
		person.addAttribute(attribute);
		EntityAttribute attribute2 = new EntityAttribute("Age2", IntegerEntityDatatype.getInstance());
		Assert.assertTrue(person.hasAttribute("Age"));
		try {
			person.deleteAttribute(attribute2);
			fail("Should have failed");
		} catch (NameNotFoundException e) {
		}
	}
	
	public void test_can_delete_attribute_by_name() throws NameNotFoundException, NameAlreadyBoundException {
		EntityClass person = new EntityClass("Person");
		EntityAttribute attribute = new EntityAttribute("Age", IntegerEntityDatatype.getInstance());
		person.addAttribute(attribute);
		Assert.assertTrue(person.hasAttribute("Age"));
		person.deleteAttributeWithName("Age");
		Assert.assertTrue(!person.hasAttribute("Age"));
	}
	
	public void test_cant_delete_attribute_by_name_if_it_doesnt_exist() throws NameAlreadyBoundException {
		EntityClass person = new EntityClass("Person");
		EntityAttribute attribute = new EntityAttribute("Age", IntegerEntityDatatype.getInstance());
		person.addAttribute(attribute);
		Assert.assertTrue(person.hasAttribute("Age"));
		Assert.assertTrue(!person.hasAttribute("Age2"));
		try {
			person.deleteAttributeWithName("Age2");
			fail("Should have failed");
		} catch (NameNotFoundException e) {
		}
		Assert.assertTrue(!person.hasAttribute("Age2"));
	}

	public void test_attribues_can_accept_values() throws InvalidActionLanguageSyntaxException, NameAlreadyBoundException {

		EntityDomain domain = new EntityDomain("domain");
		EntityClass person = new EntityClass("Person");
		EntityAttribute age = new EntityAttribute("Age", IntegerEntityDatatype.getInstance());
		person.addAttribute(age);
		domain.addClass(person);

		Assert.assertTrue(age.acceptsValue("9"));
	}

	public void test_attribues_can_reject_values() throws InvalidActionLanguageSyntaxException, NameAlreadyBoundException {

		EntityDomain domain = new EntityDomain("domain");
		EntityClass person = new EntityClass("Person");
		EntityAttribute age = new EntityAttribute("Age", IntegerEntityDatatype.getInstance());
		person.addAttribute(age);
		domain.addClass(person);

		Assert.assertTrue(!age.acceptsValue("9a"));
	}

	@SuppressWarnings("unused")
	public void test_adding_sub_class_makes_super_class_a_generalisation() {
		EntityDomain domain = new EntityDomain("Domain");
		EntityClass superClass = new EntityClass("SuperClass");
		EntityClass subClass = new EntityClass("SubClass");

		superClass.addSubClass(subClass);

		Assert.assertTrue(superClass.isGeneralisation());
		Assert.assertTrue(subClass.isGeneralisation());
	}

	public void test_determine_class_from_select_where_logic() throws NameNotFoundException, OperationNotSupportedException, InvalidActionLanguageSyntaxException, NameAlreadyBoundException {
		String proc = "SELECT ANY completedStep FROM INSTANCES OF Person WHERE selected.Complete == true;";
		EntityDomain domain = new EntityDomain("domain");
		EntityClass dummy = new EntityClass("Person");
		domain.addClass(dummy);

		EntityState state = new EntityState("state");
		dummy.addState(state);
		state.getProcedure().setProcedure(proc);

		Assert.assertEquals("Person", state.getProcedure().identifyClass("selected", 1).getName());
	}

	public void test_can_add_enable_instance_set_modifier() throws NameNotFoundException, OperationNotSupportedException {
		InstanceLifespanManager lifespan = new InstanceLifespanManager("fred");
		lifespan.start(4, "Person", true);
		Assert.assertTrue(lifespan.isInstanceSetAtLine(5));
	}

	public void test_can_add_disable_instance_set_modifier() throws NameNotFoundException, OperationNotSupportedException {
		InstanceLifespanManager lifespan = new InstanceLifespanManager("fred");
		lifespan.start(4, "Person", true);
		lifespan.end(6);
		Assert.assertTrue(lifespan.isInstanceSetAtLine(5));
	}
	
	
	public void test_can_add_failing_enable_instance_set_modifier() throws NameNotFoundException, OperationNotSupportedException {
		InstanceLifespanManager lifespan = new InstanceLifespanManager("fred");
		lifespan.start(4, "Person", false);
		Assert.assertTrue(!lifespan.isInstanceSetAtLine(5));
	}

	public void test_can_add_failing_disable_instance_set_modifier() throws NameNotFoundException, OperationNotSupportedException {
		InstanceLifespanManager lifespan = new InstanceLifespanManager("fred");
		lifespan.start(4, "Person", false);
		lifespan.end(6);
		Assert.assertTrue(!lifespan.isInstanceSetAtLine(5));
	}
	
	public void test_can_state_instance_manager_identify_if_instance_is_instance_set() throws NameNotFoundException, OperationNotSupportedException {
		StateInstanceLifespanManager stateInstance = new StateInstanceLifespanManager();
		stateInstance.beginDeclaration("fred", "Person", true, 4);
		stateInstance.endDeclaration("fred", 15);
		Assert.assertTrue(stateInstance.isInstanceAnInstanceSetOnLine("fred", 10));
	}
	
	public void test_can_state_instance_manager_identify_if_instance_is_not_instance_set() throws NameNotFoundException, OperationNotSupportedException {
		StateInstanceLifespanManager stateInstance = new StateInstanceLifespanManager();
		stateInstance.beginDeclaration("fred", "Person", false, 4);
		stateInstance.endDeclaration("fred", 15);
		Assert.assertTrue(!stateInstance.isInstanceAnInstanceSetOnLine("fred", 10));
	}
	
	public void test_every_class_gets_a_default_event_specification() {
		EntityClass klass = new EntityClass("class");
		Assert.assertEquals(1, klass.getEventSpecifications().size());
		EntityEventSpecification defaultEvent = klass.getEventSpecifications().get(0);
		new EntityEventSpecification(klass, "generate");
		Assert.assertEquals(2, klass.getEventSpecifications().size());
		Assert.assertTrue(klass.hasEventSpecificationWithName("generate"));
		Assert.assertEquals( defaultEvent, klass.getDefaultEventSpecification()); 
	}

	@SuppressWarnings("unused")
	public void test_can_create_event_specification() {
		EntityClass klass = new EntityClass("class");
		EntityEventSpecification event = new EntityEventSpecification(klass, "generate");
		Assert.assertTrue(klass.hasEventSpecificationWithName("generate"));
	}
	
	@SuppressWarnings("unused")
	public void test_adding_mutliple_of_the_same_event_spec_does_nothing() {
		EntityClass klass = new EntityClass("class");
		EntityEventSpecification event = new EntityEventSpecification(klass, "generate");
		Assert.assertTrue(klass.hasEventSpecificationWithName("generate"));
		EntityEventSpecification event2 = new EntityEventSpecification(klass, "generate");
	}

	public void test_can_get_created_event_specification() throws NameNotFoundException {
		EntityClass klass = new EntityClass("class");
		EntityEventSpecification newEvent = new EntityEventSpecification(klass, "generate");
		Assert.assertTrue(klass.hasEventSpecificationWithName("generate"));
		EntityEventSpecification foundEvent = klass.getEventSpecificationWithName("generate");
		Assert.assertEquals(newEvent, foundEvent);
		try{
			klass.getEventSpecificationWithName("notAnEvent");
			fail("Should not get here");
		}catch(NameNotFoundException e)
		{}
	}
	
	
	public void test_can_create_event_specification_with_params() throws NameNotFoundException {
		EntityClass klass = new EntityClass("class");

		EntityEventSpecification event = new EntityEventSpecification(klass, "generate");
		EntityEventParam name = new EntityEventParam("Name",StringEntityDatatype.getInstance());
		event.addEventParam(name);
		
		Assert.assertEquals(event, name.getEvent());
		Assert.assertTrue(event.hasParamWithName("Name"));
		Assert.assertEquals(name, event.getParamWithName("Name"));
		Assert.assertTrue(klass.hasEventSpecificationWithName("generate"));

		Assert.assertTrue(!event.hasParamWithName("NotAParam"));
	}
	
	public void test_can_create_event_specification_with_different_name_params() throws NameNotFoundException {
		EntityClass klass = new EntityClass("class");

		EntityEventSpecification event = new EntityEventSpecification(klass, "generate");
		EntityEventParam name = new EntityEventParam("Name",StringEntityDatatype.getInstance());
		event.addEventParam(name);
		
		EntityEventParam age = new EntityEventParam("Age",IntegerEntityDatatype.getInstance());
		event.addEventParam(age);
		
		Assert.assertEquals(event, name.getEvent());
		Assert.assertTrue(event.hasParamWithName("Name"));
		Assert.assertEquals(name, event.getParamWithName("Name"));
		
		Assert.assertTrue(event.hasParamWithName("Age"));
		Assert.assertEquals(age, event.getParamWithName("Age"));
		
		
		Assert.assertEquals(2, event.getEventParams().size());
		
	}
	
	public void test_can_remove_param_from_event_specification() throws NameNotFoundException {
		EntityClass klass = new EntityClass("class");

		EntityEventSpecification event = new EntityEventSpecification(klass, "generate");
		EntityEventParam name = new EntityEventParam("Name",StringEntityDatatype.getInstance());
		event.addEventParam(name);
		
		Assert.assertEquals(event, name.getEvent());
		Assert.assertTrue(event.hasParamWithName("Name"));
		
		Assert.assertEquals(1, event.getEventParams().size());
		
		event.removeEventParamWithName("Name");
		Assert.assertTrue(!event.hasParamWithName("Name"));
		Assert.assertEquals(0, event.getEventParams().size());
	}
	
	public void test_cant_create_event_specification_with_same_name_params() throws NameNotFoundException {
		EntityClass klass = new EntityClass("class");

		EntityEventSpecification event = new EntityEventSpecification(klass, "generate");
		EntityEventParam name = new EntityEventParam("Name",StringEntityDatatype.getInstance());
		event.addEventParam(name);
		
		EntityEventParam age = new EntityEventParam("Name",IntegerEntityDatatype.getInstance());
		event.addEventParam(age);
		
		Assert.assertEquals(event, name.getEvent());
		Assert.assertTrue(event.hasParamWithName("Name"));
		
		Assert.assertEquals(1, event.getEventParams().size());
	}


	// event instance
	public void test_can_create_event_instance() throws NameAlreadyBoundException {
		EntityClass klass = new EntityClass("class");
		EntityState from = new EntityState("from");
		klass.addState(from);
		EntityState to = new EntityState("to");
		klass.addState(to);

		EntityEventSpecification eventSpec = new EntityEventSpecification(klass, "generate");
		Assert.assertTrue(klass.hasEventSpecificationWithName("generate"));
		
		EntityEventInstance eventInstance = new EntityEventInstance(eventSpec,from,to);
		Assert.assertEquals(eventSpec, eventInstance.getSpecification());
		
		Assert.assertEquals(from, eventInstance.getFromState());
		Assert.assertEquals(to, eventInstance.getToState());

	}

	@SuppressWarnings("unused")
	public void test_can_get_created_event_instance() throws NameNotFoundException, NameAlreadyBoundException {
		EntityClass klass = new EntityClass("class");
		EntityState from = new EntityState("from");
		klass.addState(from);
		EntityState to = new EntityState("to");
		klass.addState(to);
		
		EntityEventSpecification eventSpec = new EntityEventSpecification(klass, "generate");
		Assert.assertTrue(klass.hasEventSpecificationWithName("generate"));
		EntityEventInstance eventInstance = new EntityEventInstance(eventSpec,from,to);
		
		Assert.assertTrue(klass.hasEventInstance("generate","from","to"));
		Assert.assertTrue(!klass.hasEventInstance("generate1","from","to"));
		Assert.assertTrue(!klass.hasEventInstance("generate","from1","to"));
		Assert.assertTrue(!klass.hasEventInstance("generate","from","to1"));
		
		EntityEventInstance foundEventInstance = klass.getEventInstance("generate","from","to");
		Assert.assertEquals(from, foundEventInstance.getFromState());
		Assert.assertEquals(to, foundEventInstance.getToState());

	}
	
	public void test_cant_get_not_created_event_instance(){
		EntityClass klass = new EntityClass("class");
		try {
			klass.getEventInstance("generate","from","to");
			fail("Should not get here");
		} catch (NameNotFoundException e) {
		}
	}
	
	public void test_can_delete_event_instance() throws NameNotFoundException, NameAlreadyBoundException {
		EntityClass klass = new EntityClass("class");
		EntityState from = new EntityState("from");
		klass.addState(from);
		EntityState to = new EntityState("to");
		klass.addState(to);
		
		EntityEventSpecification eventSpec = new EntityEventSpecification(klass, "generate");
		Assert.assertTrue(klass.hasEventSpecificationWithName("generate"));
		EntityEventInstance eventInstance = new EntityEventInstance(eventSpec,from,to);
		
		Assert.assertTrue(klass.hasEventInstance("generate","from","to"));

		EntityEventInstance foundEventInstance = klass.getEventInstance("generate","from","to");
		Assert.assertEquals(from, foundEventInstance.getFromState());
		Assert.assertEquals(to, foundEventInstance.getToState());
		
		eventSpec.removeEventInstance(eventInstance);
		Assert.assertTrue(!klass.hasEventInstance("generate","from","to"));

	}
	
	public void test_can_update_event_instance() throws NameNotFoundException, NameAlreadyBoundException {
		EntityClass klass = new EntityClass("class");
		EntityState from = new EntityState("from");
		EntityState to = new EntityState("to");
		EntityEventSpecification eventSpec = new EntityEventSpecification(klass, "generate");
		EntityEventSpecification newEventSpec = new EntityEventSpecification(klass, "newEvent");
		
		klass.addState(from);
		klass.addState(to);
		Assert.assertTrue(klass.hasEventSpecificationWithName("generate"));
		
		EntityEventInstance eventInstance = new EntityEventInstance(eventSpec,from,to);
		eventInstance.setId("NotTheDefaultInstance");
		
		Assert.assertTrue(klass.hasEventInstance("generate","from","to"));
		Assert.assertTrue(!klass.hasEventInstance("newEvent","from","to"));

		EntityEventInstance foundEventInstance = klass.getEventInstance("generate","from","to");
		Assert.assertEquals(from, foundEventInstance.getFromState());
		Assert.assertEquals(to, foundEventInstance.getToState());
		String oldInstanceId = eventInstance.getId();
		
		eventInstance = klass.changeEventInstance(eventInstance, newEventSpec, from, to);
		
		Assert.assertEquals(oldInstanceId, eventInstance.getId());
		
		Assert.assertTrue(!klass.hasEventInstance("generate","from","to"));
		Assert.assertTrue(klass.hasEventInstance("newEvent","from","to"));

	}

	
	
	public void test_cant_delete_event_instance_if_not_from_same_class() throws NameNotFoundException, NameAlreadyBoundException {
		EntityClass klass = new EntityClass("class");
		EntityState from = new EntityState("from");
		klass.addState(from);
		EntityState to = new EntityState("to");
		klass.addState(to);
		
		
		EntityClass klass2 = new EntityClass("class2");
		EntityState from2 = new EntityState("from2");
		klass2.addState(from2);
		EntityState to2 = new EntityState("to2");
		klass2.addState(to2);
		
		EntityEventSpecification eventSpec = new EntityEventSpecification(klass, "generate");
		Assert.assertTrue(klass.hasEventSpecificationWithName("generate"));
		EntityEventInstance eventInstance = new EntityEventInstance(eventSpec,from,to);
		new EntityEventInstance(eventSpec,from2,to2);
		Assert.assertTrue(klass.hasEventInstance("generate","from","to"));

		EntityEventInstance foundEventInstance = klass.getEventInstance("generate","from","to");
		Assert.assertEquals(from, foundEventInstance.getFromState());
		Assert.assertEquals(to, foundEventInstance.getToState());
		
		eventSpec.removeEventInstance(eventInstance);
		Assert.assertTrue(!klass.hasEventInstance("generate","from","to"));
		
		Assert.assertTrue(!klass2.hasEventInstanceFromState(eventSpec, from2));
		Assert.assertTrue(!klass2.hasEventInstanceToState(eventSpec, from2));
		
		try{
			eventSpec.removeEventInstance(eventInstance);
			fail();
		}
		catch(Exception e)
		{}

	}
	
	
	@SuppressWarnings("unused")
	public void test_class_can_tell_if_there_is_an_event_instance_from_a_particular_state() throws NameNotFoundException, NameAlreadyBoundException {
		EntityClass klass = new EntityClass("class");
		EntityState from = new EntityState("from");
		klass.addState(from);
		EntityState to = new EntityState("to");
		klass.addState(to);
		
		EntityEventSpecification eventSpec = new EntityEventSpecification(klass, "generate");
		Assert.assertTrue(klass.hasEventSpecificationWithName("generate"));
		EntityEventInstance eventInstance = new EntityEventInstance(eventSpec,from,to);
		
		Assert.assertTrue(klass.hasEventInstance("generate","from","to"));

		EntityEventInstance foundEventInstance = klass.getEventInstance("generate","from","to");
		Assert.assertEquals(from, foundEventInstance.getFromState());
		Assert.assertEquals(to, foundEventInstance.getToState());
		
		Assert.assertTrue(klass.hasEventInstanceFromState(eventSpec,from));
		Assert.assertTrue(!klass.hasEventInstanceFromState(eventSpec,to));
		Assert.assertTrue(!klass.hasEventInstanceToState(eventSpec,from));
		Assert.assertTrue(klass.hasEventInstanceToState(eventSpec,to));
		
	}

	
	public void test_entity_class_can_identify_list_of_event_names_generated() throws NameNotFoundException, InvalidActionLanguageSyntaxException
	{
		String initialProc = "";
		initialProc += "CREATE cart FROM ShoppingCart;\n";
		initialProc += "CANCEL cantFindEvent FROM self TO cart;\n";
		
		String addStockToOrderProc = "";
		addStockToOrderProc += "CREATE cart FROM ShoppingCart;\n";
		addStockToOrderProc += "GENERATE addSelection(itemName=\"item1\", quantity=5) TO cart;";
		
		EntityDomain cartDomain = DomainShoppingCart.getShoppingCartDomain();
		EntityClass shoppingCart = cartDomain.getEntityClassWithName("ShoppingCart");
		
		EntityState addStockToOrderState = shoppingCart.getStateWithName("Adding Selection To Order"); 
		EntityProcedure addStockToOrderProcedure = new EntityProcedure(addStockToOrderState);
		addStockToOrderProcedure.setProcedure(addStockToOrderProc);
		
		EntityState initialState = shoppingCart.getInitialState();
		EntityProcedure initialProcedure = new EntityProcedure(initialState);
		initialProcedure.setProcedure(initialProc);
		
		ArrayList<String> generatedEventNames = shoppingCart.getGeneratedEventNames();
		Assert.assertTrue(generatedEventNames.contains("addSelection"));
	}
	
	public void test_generalisation_leaf_has_all_parents_as_super_classes()
	{
		EntityDomain busDomain = DomainBus.getBusDomain();
		EntityClass commercial = busDomain.getEntityClassWithName("Commercial");
		EntityClass bus = busDomain.getEntityClassWithName("Bus");
		EntityClass vehicle = busDomain.getEntityClassWithName("Vehicle");
		
		ArrayList<EntityClass> superClasses = commercial.getAllSuperClasses();
		Assert.assertTrue(superClasses.contains(bus));
		Assert.assertTrue(superClasses.contains(vehicle));
	}
	
	public void test_generalisation_classes_can_tell_if_they_share_a_common_parent()
	{
		EntityDomain busDomain = DomainBus.getBusDomain();
		EntityClass commercial = busDomain.getEntityClassWithName("Commercial");
		EntityClass car = busDomain.getEntityClassWithName("Car");
		
		Assert.assertTrue(commercial.isInGeneralisationWith(car));
		Assert.assertTrue(car.isInGeneralisationWith(commercial));
	}
	
	public void test_generalisation_classes_can_tell_if_they_dont_share_a_common_parent()
	{
		EntityDomain busDomain = DomainBus.getBusDomain();
		EntityClass snowTyre = busDomain.getEntityClassWithName("SnowTyre");
		EntityClass car = busDomain.getEntityClassWithName("Car");
		
		Assert.assertTrue(!snowTyre.isInGeneralisationWith(car));
		Assert.assertTrue(!car.isInGeneralisationWith(snowTyre));
	}
	
	
	public void test_non_generalisation_classes_cant_share_a_common_parent()
	{
		EntityDomain busDomain = DomainBus.getBusDomain();
		EntityClass dummy = busDomain.getEntityClassWithName("Dummy");
		EntityClass car = busDomain.getEntityClassWithName("Car");
		
		Assert.assertTrue(!dummy.isInGeneralisationWith(car));
		Assert.assertTrue(!car.isInGeneralisationWith(dummy));
	}
	
	
	public void test_generalisation_classes_can_get_attributes_of_parents()
	{
		EntityDomain busDomain = DomainBus.getBusDomain();
		EntityClass Personal = busDomain.getEntityClassWithName("Personal");
		Assert.assertTrue(Personal.hasAttribute("LeaseAmount"));
		Assert.assertTrue(Personal.hasAttribute("HasToilet"));
		Assert.assertTrue(Personal.hasAttribute("License"));
		Assert.assertTrue(!Personal.hasAttribute("NumberDoors"));
	}
	
	public void test_classes_can_identify_if_a_class_is_in_their_parent_path()
	{
		EntityDomain busDomain = DomainBus.getBusDomain();
		EntityClass Personal = busDomain.getEntityClassWithName("Personal");
		EntityClass Vehicle = busDomain.getEntityClassWithName("Vehicle");
		EntityClass Car = busDomain.getEntityClassWithName("Car");
		
		Assert.assertTrue(Personal.isClassInParentPath(Personal));
		Assert.assertTrue(Personal.isClassInParentPath(Vehicle));
		Assert.assertTrue(!Personal.isClassInParentPath(Car));
		Assert.assertTrue(!Vehicle.isClassInParentPath(Personal));
	}
	
	@SuppressWarnings("unused")
	public void test_classes_can_identify_if_a_class_name_is_in_its_sub_classes()
	{
		EntityDomain busDomain = DomainBus.getBusDomain();
		EntityClass Personal = busDomain.getEntityClassWithName("Personal");
		EntityClass Vehicle = busDomain.getEntityClassWithName("Vehicle");
		EntityClass Car = busDomain.getEntityClassWithName("Car");
		
		Assert.assertTrue(!Personal.hasSubClassWithName("Personal"));
		Assert.assertTrue(!Personal.hasSubClassWithName("Car"));
		Assert.assertTrue(!Personal.hasSubClassWithName("Vehicle"));
		
		Assert.assertTrue(Vehicle.hasSubClassWithName("Car"));
		Assert.assertTrue(Vehicle.hasSubClassWithName("Bus"));
		
		Assert.assertTrue(Vehicle.hasSubClassWithName("Personal"));
		Assert.assertTrue(Vehicle.hasSubClassWithName("Commercial"));
	}

	public void test_remove_event_instance() throws NameNotFoundException, InvalidActionLanguageSyntaxException
	{
		EntityDomain shoppingCartDomain = DomainShoppingCart.getShoppingCartDomainWithActionLanguage();
		EntityClass shoppingCart = shoppingCartDomain.getEntityClassWithName("ShoppingCart");
		Assert.assertEquals("ShoppingCart", shoppingCart.getName());
	
		
		EntityEventSpecification addSelectionSpec = shoppingCart.getEventSpecificationWithName("addSelection");
		EntityState scAddingSelectionState = shoppingCart.getStateWithName("Adding Selection To Order");
		
		
		Assert.assertTrue(shoppingCart.hasEventInstanceToState(addSelectionSpec, scAddingSelectionState));
		Assert.assertTrue(shoppingCart.hasEventInstance(addSelectionSpec.getName(), "New Order", "Adding Selection To Order"));
		Assert.assertTrue(scAddingSelectionState.hasNonReflexiveTriggeringEvent());
		
		EntityEventInstance addSelectionInstance = shoppingCart.getEventInstance(addSelectionSpec.getName(), "New Order", "Adding Selection To Order");
		shoppingCart.removeEventInstance(addSelectionSpec, addSelectionInstance);
		
		Assert.assertTrue(!shoppingCart.hasEventInstance(addSelectionSpec.getName(), "New Order", "Adding Selection To Order"));
		// in this scenario, this state does still have an event instance of this spec!!
		Assert.assertTrue(shoppingCart.hasEventInstanceToState(addSelectionSpec, scAddingSelectionState));
		Assert.assertTrue(!scAddingSelectionState.hasNonReflexiveTriggeringEvent());
	}

	public void test_adding_first_state_becomes_initial_state() throws NameAlreadyBoundException
	{
		EntityClass klass = new EntityClass("class");
		
		Assert.assertTrue(!klass.hasStates());
		Assert.assertTrue(!klass.hasInitial());
		
		EntityState from = new EntityState("from");
		klass.addState(from);

		Assert.assertTrue(klass.hasStates());
		Assert.assertTrue(klass.hasInitial());
		Assert.assertEquals(from, klass.getInitialState());
	}
	
	public void test_adding_second_does_not_change_initial_state() throws NameAlreadyBoundException
	{
		EntityClass klass = new EntityClass("class");
		
		Assert.assertTrue(!klass.hasStates());
		Assert.assertTrue(!klass.hasInitial());
		
		EntityState from = new EntityState("from");
		klass.addState(from);

		Assert.assertTrue(klass.hasStates());
		Assert.assertTrue(klass.hasInitial());
		Assert.assertEquals(from, klass.getInitialState());
		
		EntityState to = new EntityState("to");
		klass.addState(to);
		
		Assert.assertTrue(klass.hasStates());
		Assert.assertTrue(klass.hasInitial());
		Assert.assertEquals(from, klass.getInitialState());
	}
	
	public void test_removing_second_does_not_change_initial_state() throws NameAlreadyBoundException
	{
		EntityClass klass = new EntityClass("class");
		
		Assert.assertTrue(!klass.hasStates());
		Assert.assertTrue(!klass.hasInitial());
		
		EntityState from = new EntityState("from");
		klass.addState(from);

		Assert.assertTrue(klass.hasStates());
		Assert.assertTrue(klass.hasInitial());
		Assert.assertEquals(from, klass.getInitialState());
		
		Assert.assertNotNull(klass.getStateWithName(from.getName()));
		
		
		EntityState to = new EntityState("to");
		klass.addState(to);
		
		Assert.assertNotNull(klass.getStateWithName(to.getName()));
		
		
		Assert.assertTrue(klass.hasStates());
		Assert.assertTrue(klass.hasInitial());
		Assert.assertEquals(from, klass.getInitialState());
		
		klass.deleteState(to);
		
		Assert.assertTrue(klass.hasStates());
		Assert.assertTrue(klass.hasInitial());
		Assert.assertEquals(from, klass.getInitialState());
		Assert.assertNotNull(klass.getStateWithName(from.getName()));
		Assert.assertNull(klass.getStateWithName(to.getName()));
		
	}
	
	public void test_removing_first_state_changes_next_state_to_initial_state() throws NameAlreadyBoundException
	{
		EntityClass klass = new EntityClass("class");
		
		Assert.assertTrue(!klass.hasStates());
		Assert.assertTrue(!klass.hasInitial());
		
		EntityState from = new EntityState("from");
		klass.addState(from);

		Assert.assertTrue(klass.hasStates());
		Assert.assertTrue(klass.hasInitial());
		Assert.assertEquals(from, klass.getInitialState());
		
		
		Assert.assertNotNull(klass.getStateWithName(from.getName()));
		
		
		EntityState to = new EntityState("to");
		klass.addState(to);
		
		Assert.assertTrue(klass.hasStates());
		Assert.assertTrue(klass.hasInitial());
		Assert.assertEquals(from, klass.getInitialState());
		
		Assert.assertNotNull(klass.getStateWithName(to.getName()));
		
		klass.deleteState(from);
		
		Assert.assertNull(klass.getStateWithName(from.getName()));
		Assert.assertNotNull(klass.getStateWithName(to.getName()));

		
		Assert.assertTrue(klass.hasStates());
		Assert.assertTrue(klass.hasInitial());
		Assert.assertEquals(to, klass.getInitialState());
	}

	
	public void test_cant_add_the_same_attribute_twice() throws NameAlreadyBoundException
	{
		EntityClass klass = new EntityClass("class");
		EntityAttribute a1 = new EntityAttribute("a1", StringEntityDatatype.getInstance());
		klass.addAttribute(a1);
		try
		{
			klass.addAttribute(a1);
			fail();
		}
		catch(Exception e)
		{}
	}
	
	public void test_attributes_cannot_have_the_same_name() throws NameAlreadyBoundException
	{
		EntityClass klass = new EntityClass("class");
		EntityAttribute a1 = new EntityAttribute("a1", StringEntityDatatype.getInstance());
		klass.addAttribute(a1);
		
		EntityAttribute a2 = new EntityAttribute(a1.getName(), StringEntityDatatype.getInstance());
		
		
		try
		{
			klass.addAttribute(a2);
			fail();
		}
		catch(Exception e)
		{}
	}

	public void test_cant_rename_attribute_to_be_the_same_name_as_another() throws NameAlreadyBoundException
	{
		EntityClass klass = new EntityClass("class");
		EntityAttribute a1 = new EntityAttribute("a1", StringEntityDatatype.getInstance());
		klass.addAttribute(a1);
		
		EntityAttribute a2 = new EntityAttribute("a2", StringEntityDatatype.getInstance());
		klass.addAttribute(a2);
		
		try
		{
			a2.rename(a1.getName());
			fail();
		}
		catch(Exception e)
		{}
	}

	public void test_can_get_all_entity_event_instances_to_a_state() throws NameAlreadyBoundException
	{
		EntityClass klass = new EntityClass("class");
		EntityEventSpecification es1 = new EntityEventSpecification(klass, "es1");
		EntityEventSpecification es2 = new EntityEventSpecification(klass, "es2");

		EntityState s1 = new EntityState("s1");
		klass.addState(s1);
		
		EntityState s2 = new EntityState("s2");
		klass.addState(s2);
		
		EntityEventInstance ei1 = new EntityEventInstance(es1, s1, s1);
		EntityEventInstance ei2 = new EntityEventInstance(es2, s1, s2);
		EntityEventInstance ei3 = new EntityEventInstance(es2, s2, s2);
		
		Assert.assertTrue(s1.getAllTriggeringEventInstances().contains(ei1));
		Assert.assertTrue(!s1.getAllTriggeringEventInstances().contains(ei2));
		Assert.assertTrue(!s1.getAllTriggeringEventInstances().contains(ei3));
		
		Assert.assertTrue(!s2.getAllTriggeringEventInstances().contains(ei1));
		Assert.assertTrue(s2.getAllTriggeringEventInstances().contains(ei2));
		Assert.assertTrue(s2.getAllTriggeringEventInstances().contains(ei3));
	}
	
	public void test_can_get_unique_entity_specs_to_a_state() throws NameAlreadyBoundException
	{
		EntityClass klass = new EntityClass("class");
		EntityEventSpecification es1 = new EntityEventSpecification(klass, "es1");
		EntityEventSpecification es2 = new EntityEventSpecification(klass, "es2");

		EntityState s1 = new EntityState("s1");
		klass.addState(s1);
		
		EntityState s2 = new EntityState("s2");
		klass.addState(s2);
		
		new EntityEventInstance(es1, s1, s1);
		new EntityEventInstance(es1, s1, s2);
		new EntityEventInstance(es2, s2, s2);
		
		Assert.assertTrue(s1.getAllTriggeringEventSpecs().contains(es1));
		Assert.assertTrue(!s1.getAllTriggeringEventSpecs().contains(es2));
		
		Assert.assertTrue(s2.getAllTriggeringEventSpecs().contains(es1));
		Assert.assertTrue(s2.getAllTriggeringEventSpecs().contains(es2));
	}

	public void test_event_specs_with_no_params_are_equivalent()
	{
		EntityClass klass = new EntityClass("class");

		EntityEventSpecification es1 = new EntityEventSpecification(klass, "es1");
		EntityEventSpecification es2 = new EntityEventSpecification(klass, "es2");
		
		Assert.assertTrue(es1.isEquivalentTo(es2));
		Assert.assertTrue(es2.isEquivalentTo(es1));
		Assert.assertTrue(es1.isEquivalentTo(es1));
		Assert.assertTrue(es2.isEquivalentTo(es2));
		
	}
	
	public void test_event_specs_with_different_params_are_not_equivalent()
	{
		EntityClass klass = new EntityClass("class");

		EntityEventSpecification es1 = new EntityEventSpecification(klass, "es1");
		EntityEventSpecification es2 = new EntityEventSpecification(klass, "es2");
		EntityEventParam name = new EntityEventParam("Name",StringEntityDatatype.getInstance());
		es1.addEventParam(name);
		
		Assert.assertTrue(!es1.isEquivalentTo(es2));
		Assert.assertTrue(!es2.isEquivalentTo(es1));
		Assert.assertTrue(es1.isEquivalentTo(es1));
		Assert.assertTrue(es2.isEquivalentTo(es2));
	}
	
	public void test_event_specs_with_different_params_are_not_equivalent2()
	{
		EntityClass klass = new EntityClass("class");

		EntityEventSpecification es1 = new EntityEventSpecification(klass, "es1");
		EntityEventSpecification es2 = new EntityEventSpecification(klass, "es2");
		
		EntityEventParam name = new EntityEventParam("Name",StringEntityDatatype.getInstance());
		es1.addEventParam(name);
		
		EntityEventParam name2 = new EntityEventParam("Name",BooleanEntityDatatype.getInstance());
		es2.addEventParam(name2);
		
		Assert.assertTrue(!es1.isEquivalentTo(es2));
		Assert.assertTrue(!es2.isEquivalentTo(es1));
		Assert.assertTrue(es1.isEquivalentTo(es1));
		Assert.assertTrue(es2.isEquivalentTo(es2));
	}
	
	
	public void test_event_specs_with_different_params_are_not_equivalent3()
	{
		EntityClass klass = new EntityClass("class");

		EntityEventSpecification es1 = new EntityEventSpecification(klass, "es1");
		EntityEventSpecification es2 = new EntityEventSpecification(klass, "es2");
		
		EntityEventParam name = new EntityEventParam("Name",StringEntityDatatype.getInstance());
		es1.addEventParam(name);
		EntityEventParam other = new EntityEventParam("Other",StringEntityDatatype.getInstance());
		es1.addEventParam(other);
		
		EntityEventParam name2 = new EntityEventParam("Name",StringEntityDatatype.getInstance());
		es2.addEventParam(name2);
		EntityEventParam other2 = new EntityEventParam("Other2",StringEntityDatatype.getInstance());
		es2.addEventParam(other2);
		
		Assert.assertTrue(!es1.isEquivalentTo(es2));
		Assert.assertTrue(!es2.isEquivalentTo(es1));
		Assert.assertTrue(es1.isEquivalentTo(es1));
		Assert.assertTrue(es2.isEquivalentTo(es2));
	}
	
	public void test_event_specs_with_the_same_params_are_equivalent()
	{
		EntityClass klass = new EntityClass("class");

		EntityEventSpecification es1 = new EntityEventSpecification(klass, "es1");
		EntityEventSpecification es2 = new EntityEventSpecification(klass, "es2");
		
		EntityEventParam name = new EntityEventParam("Name",StringEntityDatatype.getInstance());
		es1.addEventParam(name);
		EntityEventParam other = new EntityEventParam("Other",StringEntityDatatype.getInstance());
		es1.addEventParam(other);

		EntityEventParam other2 = new EntityEventParam("Other",StringEntityDatatype.getInstance());
		es2.addEventParam(other2);
		EntityEventParam name2 = new EntityEventParam("Name",StringEntityDatatype.getInstance());
		es2.addEventParam(name2);

		
		Assert.assertTrue(es1.isEquivalentTo(es2));
		Assert.assertTrue(es2.isEquivalentTo(es1));
		Assert.assertTrue(es1.isEquivalentTo(es1));
		Assert.assertTrue(es2.isEquivalentTo(es2));
	}
	
	public void test_relation_to_superclass_is_reflexive()
	{
		EntityClass superClass = new EntityClass("SuperClass");
		EntityClass subClass1 = new EntityClass("SubClass1");
		EntityClass subClass2 = new EntityClass("SubClass2");

		superClass.addSubClass(subClass1);
		superClass.addSubClass(subClass2);
		
		EntityRelation r1 = new EntityRelation("R1");
		r1.setEndA(superClass, CardinalityType.ONE_TO_ONE);
		r1.setEndB(subClass1, CardinalityType.ONE_TO_MANY);
		
		Assert.assertEquals(true, r1.isReflexive());
	}
	
	public void test_relation_between_hierarchy_classes_where_one_isnt_the_others_superclass_arent_reflexive()
	{
		EntityClass superClass = new EntityClass("SuperClass");
		EntityClass subClass1 = new EntityClass("SubClass1");
		EntityClass subClass2 = new EntityClass("SubClass2");

		superClass.addSubClass(subClass1);
		superClass.addSubClass(subClass2);
		
		EntityRelation r1 = new EntityRelation("R1");
		r1.setEndA(subClass2, CardinalityType.ONE_TO_ONE);
		r1.setEndB(subClass1, CardinalityType.ONE_TO_MANY);
		
		Assert.assertEquals(false, r1.isReflexive());
	}
	
	public void test_all_classes_get_string_attribute_called_state() throws NameNotFoundException
	{
		EntityClass dummy = new EntityClass("Dummy");
		Assert.assertEquals(true, dummy.hasAttribute(EntityAttribute.STATE_ATTRIBUTE_NAME));
		EntityAttribute stateAttribute = dummy.getAttributeWithName(EntityAttribute.STATE_ATTRIBUTE_NAME);
		Assert.assertEquals(StringEntityDatatype.class, stateAttribute.getType().getClass());
	}
	
	public void test_no_other_attribute_can_be_called_state() {
		EntityClass dummy = new EntityClass("Dummy");
		EntityAttribute state = new EntityAttribute(EntityAttribute.STATE_ATTRIBUTE_NAME, StringEntityDatatype.getInstance());
		try {
			dummy.addAttribute(state);
			fail("this should not work");
		} catch (NameAlreadyBoundException e) {
			fail("wrong error type");
		}
		catch(CannotCreateAttributeCalledStateException e)
		{}
	}

	public void test_deleting_class_from_domain_decreases_class_count() {
		EntityDomain entityDomain = new EntityDomain("domian");
		int classCount = entityDomain.howManyClasses();
		EntityClass entityClass = new EntityClass("class");
		entityDomain.addClass(entityClass);
		Assert.assertEquals(classCount + 1, entityDomain.howManyClasses());
		entityDomain.deleteClass(entityClass);
	}

	public void test_can_delete_relation() {
		EntityDomain domain = new EntityDomain("");
		EntityClass guest = new EntityClass("Guest");
		EntityClass room = new EntityClass("Room");
		EntityClass booking = new EntityClass("Booking");
		domain.addClass(guest);
		domain.addClass(room);
		domain.addClass(booking);
		
		EntityRelation r1 = new EntityRelation("R1");
		r1.setEndA(guest, CardinalityType.ZERO_TO_ONE);
		r1.setEndB(room, CardinalityType.ONE_TO_ONE);
		r1.setAssociation(booking);

		Assert.assertEquals(false, r1.isReflexive());
		Assert.assertEquals(guest, r1.getClassA());
		Assert.assertEquals(room, r1.getClassB());
		Assert.assertEquals(true, r1.hasAssociation());
		Assert.assertEquals(booking, r1.getAssociation());
		Assert.assertEquals(true, booking.isAssociation());
		Assert.assertEquals(r1, booking.getAssociationRelation());
		Assert.assertEquals(true, domain.hasRelationWithName(r1.getName()));
		Assert.assertEquals(true, domain.hasRelationWithId(r1.getId()));
		Assert.assertEquals(true, room.hasRelation(r1));
		Assert.assertEquals(true, guest.hasRelation(r1));
		
		domain.deleteRelationWithId(r1.getId());
		
		Assert.assertEquals(false, r1.hasAssociation());
		Assert.assertEquals(false, booking.isAssociation());
		Assert.assertEquals(false, domain.hasRelationWithName(r1.getName()));
		Assert.assertEquals(false, domain.hasRelationWithId(r1.getId()));
		Assert.assertEquals(false, room.hasRelation(r1));
		Assert.assertEquals(false, guest.hasRelation(r1));
	}
	
	public void test_can_delete_relation_if_one_class_is_in_hierarchy() throws NameNotFoundException
	{
		EntityDomain busDomain = DomainBus.getBusDomain();
		EntityClass bus = busDomain.getEntityClassWithName("Bus");
		EntityClass tyre = busDomain.getEntityClassWithName("Tyre");
		EntityClass personal = busDomain.getEntityClassWithName("Personal");
		EntityClass roadTyre = busDomain.getEntityClassWithName("RoadTyre");
		EntityRelation r1 = busDomain.getRelationWithName("R1");
		
		Assert.assertEquals(bus, r1.getOppositeClass(roadTyre));
		Assert.assertEquals(tyre, r1.getOppositeClass(personal));
		Assert.assertEquals(bus, r1.getClassA());
		Assert.assertEquals(tyre, r1.getClassB());
		Assert.assertEquals(true, busDomain.hasRelationWithName(r1.getName()));
		Assert.assertEquals(true, busDomain.hasRelationWithId(r1.getId()));
		Assert.assertEquals(true, bus.hasRelation(r1));
		Assert.assertEquals(true, tyre.hasRelation(r1));
		
		busDomain.deleteRelationWithId(r1.getId());
		
		Assert.assertEquals(false, busDomain.hasRelationWithName(r1.getName()));
		Assert.assertEquals(false, busDomain.hasRelationWithId(r1.getId()));
		Assert.assertEquals(false, bus.hasRelation(r1));
		Assert.assertEquals(false, tyre.hasRelation(r1));
	}
	
	public void test_can_delete_reflexive_relation() {
		EntityDomain domain = new EntityDomain("");
		EntityClass guest = new EntityClass("Guest");
		EntityClass booking = new EntityClass("Booking");
		domain.addClass(guest);
		domain.addClass(booking);
		
		EntityRelation r1 = new EntityRelation("R1");
		r1.setEndA(guest, CardinalityType.ZERO_TO_ONE);
		r1.setEndB(guest, CardinalityType.ONE_TO_ONE);
		r1.setAssociation(booking);

		Assert.assertEquals(true, r1.isReflexive());
		Assert.assertEquals(guest, r1.getClassA());
		Assert.assertEquals(guest, r1.getClassB());
		Assert.assertEquals(true, r1.hasAssociation());
		Assert.assertEquals(booking, r1.getAssociation());
		Assert.assertEquals(true, booking.isAssociation());
		Assert.assertEquals(r1, booking.getAssociationRelation());
		Assert.assertEquals(true, domain.hasRelationWithName(r1.getName()));
		Assert.assertEquals(true, domain.hasRelationWithId(r1.getId()));
		
		Assert.assertEquals(true, guest.hasRelation(r1));
		
		domain.deleteRelationWithId(r1.getId());
		
		Assert.assertEquals(false, r1.hasAssociation());
		Assert.assertEquals(false, booking.isAssociation());
		Assert.assertEquals(false, domain.hasRelationWithName(r1.getName()));
		Assert.assertEquals(false, domain.hasRelationWithId(r1.getId()));
		Assert.assertEquals(false, guest.hasRelation(r1));
	}

	public void test_deleting_a_class_deletes_used_relations(){
		EntityDomain domain = new EntityDomain("");
		EntityClass guest = new EntityClass("Guest");
		EntityClass room = new EntityClass("Room");
		domain.addClass(guest);
		domain.addClass(room);
		
		EntityRelation r1 = new EntityRelation("R1");
		r1.setEndA(guest, CardinalityType.ZERO_TO_ONE);
		r1.setEndB(room, CardinalityType.ONE_TO_ONE);
		
		Assert.assertEquals(true, guest.hasRelation(r1));
		Assert.assertEquals(true, domain.hasRelationWithId(r1.getId()));
		
		domain.deleteClass(room);
		
		Assert.assertEquals(false, guest.hasRelation(r1));
		Assert.assertEquals(false, domain.hasRelationWithId(r1.getId()));
	}

	@SuppressWarnings("unused")
	public void test_when_deleting_event_spec_all_instances_go_to_default_spec() throws NameNotFoundException, NameAlreadyBoundException {
		EntityClass klass = new EntityClass("class");
		EntityState from = new EntityState("from");
		klass.addState(from);
		EntityState to = new EntityState("to");
		klass.addState(to);
		
		EntityEventSpecification generateSpec = new EntityEventSpecification(klass, "generate");
		Assert.assertTrue(klass.hasEventSpecificationWithName("generate"));
		EntityEventSpecification compileSpec = new EntityEventSpecification(klass, "compile");
		Assert.assertTrue(klass.hasEventSpecificationWithName("compile"));
		
		EntityEventInstance generateInstance = new EntityEventInstance(generateSpec,from,to);
		EntityEventInstance compileInstance = new EntityEventInstance(compileSpec,from,to);
		
		Assert.assertTrue(klass.hasEventInstance("generate","from","to"));
		Assert.assertTrue(klass.hasEventInstance("compile","from","to"));
		Assert.assertFalse(klass.hasEventInstance(EntityClass.DEFAULT_EVENT_SPEC_ID,"from","to"));
		
		EntityEventInstance foundGenerateInstance = klass.getEventInstance("generate","from","to");
		Assert.assertEquals(from, foundGenerateInstance.getFromState());
		Assert.assertEquals(to, foundGenerateInstance.getToState());
		
		EntityEventInstance foundCompileInstance = klass.getEventInstance("compile","from","to");
		Assert.assertEquals(from, foundCompileInstance.getFromState());
		Assert.assertEquals(to, foundCompileInstance.getToState());

		klass.deleteSpecification(generateSpec);
		
		Assert.assertFalse(klass.hasEventInstance("generate","from","to"));
		Assert.assertTrue(klass.hasEventInstance("compile","from","to"));
		Assert.assertTrue(klass.hasEventInstance(EntityClass.DEFAULT_EVENT_SPEC_ID,"from","to"));
		
		EntityEventInstance foundDefaultInstance = klass.getEventInstance(EntityClass.DEFAULT_EVENT_SPEC_ID,"from","to");
		Assert.assertEquals(from, foundDefaultInstance.getFromState());
		Assert.assertEquals(to, foundDefaultInstance.getToState());
		
	}
	
	public void test_when_deleting_then_changing_spec_duplicate_transitions_arent_created(){
		EntityClass klass = new EntityClass("class");
		EntityState from = new EntityState("from");
		klass.addState(from);
		EntityState to = new EntityState("to");
		klass.addState(to);

		Assert.assertEquals(0, klass.getAllEntityEventInstances().size());
		
		EntityEventInstance instance = new EntityEventInstance(klass.getDefaultEventSpecification(), from, to);
		Assert.assertEquals(1, klass.getAllEntityEventInstances().size());
		Assert.assertEquals(klass.getDefaultEventSpecification(),instance.getSpecification());

		EntityEventSpecification spec1 = new EntityEventSpecification(klass, "spec1");
		EntityEventSpecification spec2 = new EntityEventSpecification(klass, "spec2");
		
		instance = klass.changeEventInstance(instance, spec1, from, to);
		Assert.assertEquals(1, klass.getAllEntityEventInstances().size());
		Assert.assertEquals(spec1,instance.getSpecification());
		
		klass.deleteSpecification(spec1);
		Assert.assertEquals(1, klass.getAllEntityEventInstances().size());
		Assert.assertEquals(klass.getDefaultEventSpecification(),instance.getSpecification());
		
		instance = klass.changeEventInstance(instance, spec2, from, to);
		Assert.assertEquals(1, klass.getAllEntityEventInstances().size());
		Assert.assertEquals(spec2,instance.getSpecification());
	}
	
}

