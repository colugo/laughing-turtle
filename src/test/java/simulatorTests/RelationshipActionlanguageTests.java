package test.java.simulatorTests;

import test.java.helper.DomainTTD;
import test.java.helper.TestHelper;

import javax.naming.NameAlreadyBoundException;
import javax.naming.NameNotFoundException;

import junit.framework.Assert;
import junit.framework.TestCase;
import main.java.avii.editor.contracts.metamodel.actionLanguage.syntax.InvalidActionLanguageSyntaxException;
import main.java.avii.editor.metamodel.entities.EntityAttribute;
import main.java.avii.editor.metamodel.entities.EntityClass;
import main.java.avii.editor.metamodel.entities.EntityDomain;
import main.java.avii.editor.metamodel.entities.EntityProcedure;
import main.java.avii.editor.metamodel.entities.EntityState;
import main.java.avii.editor.metamodel.entities.datatypes.IntegerEntityDatatype;
import main.java.avii.simulator.Simulator;
import main.java.avii.simulator.exceptions.CannotSimulateDomainThatIsInvalidException;
import main.java.avii.simulator.relations.SimulatedRelationship;
import main.java.avii.simulator.simulatedTypes.NullSimulatedInstance;
import main.java.avii.simulator.simulatedTypes.SimulatedInstance;

public class RelationshipActionlanguageTests extends TestCase {
	public RelationshipActionlanguageTests(String name)
	{
		super(name);
	}
	
	public void test_can_create_non_reflexive_no_association_relation_instance_via_actionlanguage() throws NameAlreadyBoundException, InvalidActionLanguageSyntaxException, CannotSimulateDomainThatIsInvalidException, NameNotFoundException
	{
		EntityDomain domain = DomainTTD.getTTDDomain();
		EntityState initialState = new EntityState("initial");
		domain.getEntityClassWithName("Task").addState(initialState);
		EntityProcedure initialProcedure = new EntityProcedure(initialState);
		
		String procedureText/* 0*/ = "";
		procedureText += /* 1*/ "CREATE task1 FROM Task;\n";
		procedureText += /* 2*/ "CREATE step1 FROM Step;\n";
		procedureText += /* 3*/ "RELATE task1 TO step1 ACROSS R2;\n";
		
		initialProcedure.setProcedure(procedureText);

		TestHelper.addNewInitialStateToClassWithEventToPreviousInitialState(domain.getEntityClassWithName("Task"));
		
		Simulator simulator = new Simulator(domain);
		simulator.setSimulatingState(initialState);

		simulator.getSimulatingState().simulate();
		
		SimulatedRelationship sR2 = simulator.getRelationshipWithName("R2");
		SimulatedInstance task1 = simulator.getSimulatingState().getInstanceWithName("task1");
		SimulatedInstance step1 = simulator.getSimulatingState().getInstanceWithName("step1");
		Assert.assertEquals(true, sR2.doesRelationshipExistBetween(task1.getIdentifier(), step1.getIdentifier()));
		Assert.assertEquals(true, sR2.doesRelationshipExistBetween(step1.getIdentifier(), task1.getIdentifier()));
	}
	
	public void test_can_create_non_reflexive_no_association_relation_instance_via_actionlanguage_in_other_direction() throws NameAlreadyBoundException, InvalidActionLanguageSyntaxException, CannotSimulateDomainThatIsInvalidException, NameNotFoundException
	{
		EntityDomain domain = DomainTTD.getTTDDomain();
		EntityState initialState = new EntityState("initial");
		domain.getEntityClassWithName("Task").addState(initialState);
		EntityProcedure initialProcedure = new EntityProcedure(initialState);
		
		String procedureText/* 0*/ = "";
		procedureText += /* 1*/ "CREATE task1 FROM Task;\n";
		procedureText += /* 2*/ "CREATE step1 FROM Step;\n";
		procedureText += /* 3*/ "RELATE step1 TO task1 ACROSS R2;\n";
		
		initialProcedure.setProcedure(procedureText);
		
		TestHelper.addNewInitialStateToClassWithEventToPreviousInitialState(domain.getEntityClassWithName("Task"));
		
		Simulator simulator = new Simulator(domain);
		simulator.setSimulatingState(initialState);

		simulator.getSimulatingState().simulate();
		
		SimulatedRelationship sR2 = simulator.getRelationshipWithName("R2");
		SimulatedInstance task1 = simulator.getSimulatingState().getInstanceWithName("task1");
		SimulatedInstance step1 = simulator.getSimulatingState().getInstanceWithName("step1");
		Assert.assertEquals(true, sR2.doesRelationshipExistBetween(task1.getIdentifier(), step1.getIdentifier()));
		Assert.assertEquals(true, sR2.doesRelationshipExistBetween(step1.getIdentifier(), task1.getIdentifier()));
	}
	
	public void test_can_create_reflexive_no_association_relation_instance_via_actionlanguage() throws NameAlreadyBoundException, InvalidActionLanguageSyntaxException, CannotSimulateDomainThatIsInvalidException, NameNotFoundException
	{
		EntityDomain domain = DomainTTD.getTTDDomain();
		EntityState initialState = new EntityState("initial");
		domain.getEntityClassWithName("Task").addState(initialState);
		EntityProcedure initialProcedure = new EntityProcedure(initialState);
		
		String procedureText/* 0*/ = "";
		procedureText += /* 1*/ "CREATE task1 FROM Task;\n";
		procedureText += /* 2*/ "CREATE task2 FROM Task;\n";
		procedureText += /* 3*/ "RELATE task1 TO task2 ACROSS R4.\"Leads\";\n";
		
		initialProcedure.setProcedure(procedureText);

		TestHelper.addNewInitialStateToClassWithEventToPreviousInitialState(domain.getEntityClassWithName("Task"));
		
		Simulator simulator = new Simulator(domain);
		simulator.setSimulatingState(initialState);

		simulator.getSimulatingState().simulate();
		
		SimulatedRelationship sR4 = simulator.getRelationshipWithName("R4");
		SimulatedInstance task1 = simulator.getSimulatingState().getInstanceWithName("task1");
		SimulatedInstance task2 = simulator.getSimulatingState().getInstanceWithName("task2");
		Assert.assertEquals(true, sR4.doesRelationshipExistBetween(task1.getIdentifier(), "Leads", task2.getIdentifier(), "Follows"));
		Assert.assertEquals(true, sR4.doesRelationshipExistBetween(task2.getIdentifier(), "Follows", task1.getIdentifier(), "Leads"));
		Assert.assertEquals(false, sR4.doesRelationshipExistBetween(task2.getIdentifier(), "Leads", task1.getIdentifier(), "Follows"));
		Assert.assertEquals(false, sR4.doesRelationshipExistBetween(task1.getIdentifier(), "Follows", task2.getIdentifier(), "Leads"));
	}
	
	public void test_can_create_reflexive_no_association_relation_instance_via_actionlanguage_in_other_direction() throws NameAlreadyBoundException, InvalidActionLanguageSyntaxException, CannotSimulateDomainThatIsInvalidException, NameNotFoundException
	{
		EntityDomain domain = DomainTTD.getTTDDomain();
		EntityState initialState = new EntityState("initial");
		domain.getEntityClassWithName("Task").addState(initialState);
		EntityProcedure initialProcedure = new EntityProcedure(initialState);
		
		String procedureText/* 0*/ = "";
		procedureText += /* 1*/ "CREATE task1 FROM Task;\n";
		procedureText += /* 2*/ "CREATE task2 FROM Task;\n";
		procedureText += /* 3*/ "RELATE task2 TO task1 ACROSS R4.\"Follows\";\n";
		
		initialProcedure.setProcedure(procedureText);

		TestHelper.addNewInitialStateToClassWithEventToPreviousInitialState(domain.getEntityClassWithName("Task"));
		
		Simulator simulator = new Simulator(domain);
		simulator.setSimulatingState(initialState);

		simulator.getSimulatingState().simulate();
		
		SimulatedRelationship sR4 = simulator.getRelationshipWithName("R4");
		SimulatedInstance task1 = simulator.getSimulatingState().getInstanceWithName("task1");
		SimulatedInstance task2 = simulator.getSimulatingState().getInstanceWithName("task2");
		Assert.assertEquals(true, sR4.doesRelationshipExistBetween(task1.getIdentifier(), "Leads", task2.getIdentifier(), "Follows"));
		Assert.assertEquals(true, sR4.doesRelationshipExistBetween(task2.getIdentifier(), "Follows", task1.getIdentifier(), "Leads"));
		Assert.assertEquals(false, sR4.doesRelationshipExistBetween(task2.getIdentifier(), "Leads", task1.getIdentifier(), "Follows"));
		Assert.assertEquals(false, sR4.doesRelationshipExistBetween(task1.getIdentifier(), "Follows", task2.getIdentifier(), "Leads"));
	}
	
	public void test_can_unrelate_non_reflexive_no_association_relation_instance_via_actionlanguage() throws NameAlreadyBoundException, InvalidActionLanguageSyntaxException, CannotSimulateDomainThatIsInvalidException, NameNotFoundException
	{
		EntityDomain domain = DomainTTD.getTTDDomain();
		EntityState initialState = new EntityState("initial");
		domain.getEntityClassWithName("Task").addState(initialState);
		EntityProcedure initialProcedure = new EntityProcedure(initialState);
		
		String procedureText/* 0*/ = "";
		procedureText += /* 1*/ "CREATE task1 FROM Task;\n";
		procedureText += /* 2*/ "CREATE step1 FROM Step;\n";
		procedureText += /* 3*/ "RELATE task1 TO step1 ACROSS R2;\n";
		procedureText += /* 4*/ "UNRELATE task1 FROM step1 ACROSS R2;\n";
		
		initialProcedure.setProcedure(procedureText);

		TestHelper.addNewInitialStateToClassWithEventToPreviousInitialState(domain.getEntityClassWithName("Task"));
		
		Simulator simulator = new Simulator(domain);
		simulator.setSimulatingState(initialState);

		simulator.getSimulatingState().simulate();
		
		SimulatedRelationship sR2 = simulator.getRelationshipWithName("R2");
		SimulatedInstance task1 = simulator.getSimulatingState().getInstanceWithName("task1");
		SimulatedInstance step1 = simulator.getSimulatingState().getInstanceWithName("step1");
		Assert.assertEquals(false, sR2.doesRelationshipExistBetween(task1.getIdentifier(), step1.getIdentifier()));
		Assert.assertEquals(false, sR2.doesRelationshipExistBetween(step1.getIdentifier(), task1.getIdentifier()));
	}
	
	public void test_can_unrelate_non_reflexive_no_association_relation_instance_via_actionlanguage_in_other_direction() throws NameAlreadyBoundException, InvalidActionLanguageSyntaxException, CannotSimulateDomainThatIsInvalidException, NameNotFoundException
	{
		EntityDomain domain = DomainTTD.getTTDDomain();
		EntityState initialState = new EntityState("initial");
		domain.getEntityClassWithName("Task").addState(initialState);
		EntityProcedure initialProcedure = new EntityProcedure(initialState);
		
		String procedureText/* 0*/ = "";
		procedureText += /* 1*/ "CREATE task1 FROM Task;\n";
		procedureText += /* 2*/ "CREATE step1 FROM Step;\n";
		procedureText += /* 3*/ "RELATE step1 TO task1 ACROSS R2;\n";
		procedureText += /* 4*/ "UNRELATE task1 FROM step1 ACROSS R2;\n";
		
		initialProcedure.setProcedure(procedureText);

		TestHelper.addNewInitialStateToClassWithEventToPreviousInitialState(domain.getEntityClassWithName("Task"));
		
		Simulator simulator = new Simulator(domain);
		simulator.setSimulatingState(initialState);

		simulator.getSimulatingState().simulate();
		
		SimulatedRelationship sR2 = simulator.getRelationshipWithName("R2");
		SimulatedInstance task1 = simulator.getSimulatingState().getInstanceWithName("task1");
		SimulatedInstance step1 = simulator.getSimulatingState().getInstanceWithName("step1");
		Assert.assertEquals(false, sR2.doesRelationshipExistBetween(task1.getIdentifier(), step1.getIdentifier()));
		Assert.assertEquals(false, sR2.doesRelationshipExistBetween(step1.getIdentifier(), task1.getIdentifier()));
	}
	
	public void test_can_unrelate_reflexive_no_association_relation_instance_via_actionlanguage() throws NameAlreadyBoundException, InvalidActionLanguageSyntaxException, CannotSimulateDomainThatIsInvalidException, NameNotFoundException
	{
		EntityDomain domain = DomainTTD.getTTDDomain();
		EntityState initialState = new EntityState("initial");
		domain.getEntityClassWithName("Task").addState(initialState);
		EntityProcedure initialProcedure = new EntityProcedure(initialState);
		
		String procedureText/* 0*/ = "";
		procedureText += /* 1*/ "CREATE task1 FROM Task;\n";
		procedureText += /* 2*/ "CREATE task2 FROM Task;\n";
		procedureText += /* 3*/ "RELATE task1 TO task2 ACROSS R4.\"Leads\";\n";
		procedureText += /* 4*/ "UNRELATE task1 FROM task2 ACROSS R4.\"Leads\";\n";
		
		initialProcedure.setProcedure(procedureText);

		TestHelper.addNewInitialStateToClassWithEventToPreviousInitialState(domain.getEntityClassWithName("Task"));
		
		Simulator simulator = new Simulator(domain);
		simulator.setSimulatingState(initialState);

		simulator.getSimulatingState().simulate();
		
		SimulatedRelationship sR4 = simulator.getRelationshipWithName("R4");
		SimulatedInstance task1 = simulator.getSimulatingState().getInstanceWithName("task1");
		SimulatedInstance task2 = simulator.getSimulatingState().getInstanceWithName("task2");
		Assert.assertEquals(false, sR4.doesRelationshipExistBetween(task1.getIdentifier(), "Leads", task2.getIdentifier(), "Follows"));
		Assert.assertEquals(false, sR4.doesRelationshipExistBetween(task2.getIdentifier(), "Follows", task1.getIdentifier(), "Leads"));
		Assert.assertEquals(false, sR4.doesRelationshipExistBetween(task2.getIdentifier(), "Leads", task1.getIdentifier(), "Follows"));
		Assert.assertEquals(false, sR4.doesRelationshipExistBetween(task1.getIdentifier(), "Follows", task2.getIdentifier(), "Leads"));
	}
	
	public void test_can_unrelate_reflexive_no_association_relation_instance_via_actionlanguage_in_other_direction() throws NameAlreadyBoundException, InvalidActionLanguageSyntaxException, CannotSimulateDomainThatIsInvalidException, NameNotFoundException
	{
		EntityDomain domain = DomainTTD.getTTDDomain();
		EntityState initialState = new EntityState("initial");
		domain.getEntityClassWithName("Task").addState(initialState);
		EntityProcedure initialProcedure = new EntityProcedure(initialState);
		
		String procedureText/* 0*/ = "";
		procedureText += /* 1*/ "CREATE task1 FROM Task;\n";
		procedureText += /* 2*/ "CREATE task2 FROM Task;\n";
		procedureText += /* 3*/ "RELATE task2 TO task1 ACROSS R4.\"Follows\";\n";
		procedureText += /* 4*/ "UNRELATE task2 FROM task1 ACROSS R4.\"Follows\";\n";
		
		initialProcedure.setProcedure(procedureText);

		TestHelper.addNewInitialStateToClassWithEventToPreviousInitialState(domain.getEntityClassWithName("Task"));
		
		Simulator simulator = new Simulator(domain);
		simulator.setSimulatingState(initialState);

		simulator.getSimulatingState().simulate();
		
		SimulatedRelationship sR4 = simulator.getRelationshipWithName("R4");
		SimulatedInstance task1 = simulator.getSimulatingState().getInstanceWithName("task1");
		SimulatedInstance task2 = simulator.getSimulatingState().getInstanceWithName("task2");
		Assert.assertEquals(false, sR4.doesRelationshipExistBetween(task1.getIdentifier(), "Leads", task2.getIdentifier(), "Follows"));
		Assert.assertEquals(false, sR4.doesRelationshipExistBetween(task2.getIdentifier(), "Follows", task1.getIdentifier(), "Leads"));
		Assert.assertEquals(false, sR4.doesRelationshipExistBetween(task2.getIdentifier(), "Leads", task1.getIdentifier(), "Follows"));
		Assert.assertEquals(false, sR4.doesRelationshipExistBetween(task1.getIdentifier(), "Follows", task2.getIdentifier(), "Leads"));
	}

	//////////////////////////////// association instances //////////////////////////////////
	
	
	public void test_can_create_non_reflexive_with_association_relation_instance_via_actionlanguage() throws NameAlreadyBoundException, InvalidActionLanguageSyntaxException, CannotSimulateDomainThatIsInvalidException, NameNotFoundException
	{
		EntityDomain domain = DomainTTD.getTTDDomain();
		EntityState initialState = new EntityState("initial");
		domain.getEntityClassWithName("Task").addState(initialState);
		EntityProcedure initialProcedure = new EntityProcedure(initialState);
		
		String procedureText/* 0*/ = "";
		procedureText += /* 1*/ "CREATE task1 FROM Task;\n";
		procedureText += /* 2*/ "CREATE user1 FROM User;\n";
		procedureText += /* 3*/ "RELATE task1 TO user1 ACROSS R1 CREATING assignment1;\n";
		
		initialProcedure.setProcedure(procedureText);

		TestHelper.addNewInitialStateToClassWithEventToPreviousInitialState(domain.getEntityClassWithName("Task"));
		
		Simulator simulator = new Simulator(domain);
		simulator.setSimulatingState(initialState);

		simulator.getSimulatingState().simulate();
		
		SimulatedRelationship sR1 = simulator.getRelationshipWithName("R1");
		SimulatedInstance task1 = simulator.getSimulatingState().getInstanceWithName("task1");
		SimulatedInstance user1 = simulator.getSimulatingState().getInstanceWithName("user1");
		Assert.assertEquals(true, sR1.doesRelationshipExistBetween(task1.getIdentifier(), user1.getIdentifier()));
		Assert.assertEquals(true, sR1.doesRelationshipExistBetween(user1.getIdentifier(), task1.getIdentifier()));
		SimulatedInstance assignment1 = simulator.getSimulatingState().getInstanceWithName("assignment1");
		Assert.assertEquals("Assignment", assignment1.getSimulatedClass().getName());
	}
	
	public void test_can_create_non_reflexive_with_association_relation_instance_via_actionlanguage_in_other_direction() throws NameAlreadyBoundException, InvalidActionLanguageSyntaxException, CannotSimulateDomainThatIsInvalidException, NameNotFoundException
	{
		EntityDomain domain = DomainTTD.getTTDDomain();
		EntityState initialState = new EntityState("initial");
		domain.getEntityClassWithName("Task").addState(initialState);
		EntityProcedure initialProcedure = new EntityProcedure(initialState);
		
		String procedureText/* 0*/ = "";
		procedureText += /* 1*/ "CREATE task1 FROM Task;\n";
		procedureText += /* 2*/ "CREATE user1 FROM User;\n";
		procedureText += /* 3*/ "RELATE user1 TO task1 ACROSS R1 CREATING assignment1;\n";
		
		initialProcedure.setProcedure(procedureText);

		TestHelper.addNewInitialStateToClassWithEventToPreviousInitialState(domain.getEntityClassWithName("Task"));
		
		Simulator simulator = new Simulator(domain);
		simulator.setSimulatingState(initialState);

		simulator.getSimulatingState().simulate();
		
		SimulatedRelationship sR1 = simulator.getRelationshipWithName("R1");
		SimulatedInstance task1 = simulator.getSimulatingState().getInstanceWithName("task1");
		SimulatedInstance user1 = simulator.getSimulatingState().getInstanceWithName("user1");
		Assert.assertEquals(true, sR1.doesRelationshipExistBetween(task1.getIdentifier(), user1.getIdentifier()));
		Assert.assertEquals(true, sR1.doesRelationshipExistBetween(user1.getIdentifier(), task1.getIdentifier()));
		SimulatedInstance assignment1 = simulator.getSimulatingState().getInstanceWithName("assignment1");
		Assert.assertEquals("Assignment", assignment1.getSimulatedClass().getName());
	}
	
	public void test_can_create_reflexive_with_association_relation_instance_via_actionlanguage() throws NameAlreadyBoundException, InvalidActionLanguageSyntaxException, CannotSimulateDomainThatIsInvalidException, NameNotFoundException
	{
		EntityDomain domain = DomainTTD.getTTDDomain();
		EntityState initialState = new EntityState("initial");
		domain.getEntityClassWithName("Task").addState(initialState);
		EntityProcedure initialProcedure = new EntityProcedure(initialState);
		
		String procedureText/* 0*/ = "";
		procedureText += /* 1*/ "CREATE step1 FROM Step;\n";
		procedureText += /* 2*/ "CREATE step2 FROM Step;\n";
		procedureText += /* 3*/ "RELATE step1 TO step2 ACROSS R3.\"Leads\" CREATING sequence1;\n";
		
		initialProcedure.setProcedure(procedureText);

		TestHelper.addNewInitialStateToClassWithEventToPreviousInitialState(domain.getEntityClassWithName("Task"));
		
		Simulator simulator = new Simulator(domain);
		simulator.setSimulatingState(initialState);

		simulator.getSimulatingState().simulate();
		
		SimulatedRelationship sR3 = simulator.getRelationshipWithName("R3");
		SimulatedInstance step1 = simulator.getSimulatingState().getInstanceWithName("step1");
		SimulatedInstance step2 = simulator.getSimulatingState().getInstanceWithName("step2");
		Assert.assertEquals(true, sR3.doesRelationshipExistBetween(step1.getIdentifier(), "Leads", step2.getIdentifier(), "Follows"));
		Assert.assertEquals(true, sR3.doesRelationshipExistBetween(step2.getIdentifier(), "Follows", step1.getIdentifier(), "Leads"));
		Assert.assertEquals(false, sR3.doesRelationshipExistBetween(step2.getIdentifier(), "Leads", step1.getIdentifier(), "Follows"));
		Assert.assertEquals(false, sR3.doesRelationshipExistBetween(step1.getIdentifier(), "Follows", step2.getIdentifier(), "Leads"));
		SimulatedInstance sequence1 = simulator.getSimulatingState().getInstanceWithName("sequence1");
		Assert.assertEquals("Sequence", sequence1.getSimulatedClass().getName());
	}
	
	public void test_can_create_reflexive_with_association_relation_instance_via_actionlanguage_in_other_direction() throws NameAlreadyBoundException, InvalidActionLanguageSyntaxException, CannotSimulateDomainThatIsInvalidException, NameNotFoundException
	{
		EntityDomain domain = DomainTTD.getTTDDomain();
		EntityState initialState = new EntityState("initial");
		domain.getEntityClassWithName("Task").addState(initialState);
		EntityProcedure initialProcedure = new EntityProcedure(initialState);
		
		String procedureText/* 0*/ = "";
		procedureText += /* 1*/ "CREATE step1 FROM Step;\n";
		procedureText += /* 2*/ "CREATE step2 FROM Step;\n";
		procedureText += /* 3*/ "RELATE step2 TO step1 ACROSS R3.\"Follows\" CREATING sequence1;\n";
		
		initialProcedure.setProcedure(procedureText);

		TestHelper.addNewInitialStateToClassWithEventToPreviousInitialState(domain.getEntityClassWithName("Task"));
		
		Simulator simulator = new Simulator(domain);
		simulator.setSimulatingState(initialState);

		simulator.getSimulatingState().simulate();
		
		SimulatedRelationship sR3 = simulator.getRelationshipWithName("R3");
		SimulatedInstance step1 = simulator.getSimulatingState().getInstanceWithName("step1");
		SimulatedInstance step2 = simulator.getSimulatingState().getInstanceWithName("step2");
		Assert.assertEquals(true, sR3.doesRelationshipExistBetween(step1.getIdentifier(), "Leads", step2.getIdentifier(), "Follows"));
		Assert.assertEquals(true, sR3.doesRelationshipExistBetween(step2.getIdentifier(), "Follows", step1.getIdentifier(), "Leads"));
		Assert.assertEquals(false, sR3.doesRelationshipExistBetween(step2.getIdentifier(), "Leads", step1.getIdentifier(), "Follows"));
		Assert.assertEquals(false, sR3.doesRelationshipExistBetween(step1.getIdentifier(), "Follows", step2.getIdentifier(), "Leads"));
		SimulatedInstance sequence1 = simulator.getSimulatingState().getInstanceWithName("sequence1");
		Assert.assertEquals("Sequence", sequence1.getSimulatedClass().getName());
	}
	
	//////////// can select associaion instance ///////////////
	
	public void test_can_select_non_reflexive_association_relation_instance_via_actionlanguage() throws NameAlreadyBoundException, InvalidActionLanguageSyntaxException, CannotSimulateDomainThatIsInvalidException, NameNotFoundException
	{
		EntityDomain domain = DomainTTD.getTTDDomain();
		EntityState initialState = new EntityState("initial");
		domain.getEntityClassWithName("Task").addState(initialState);
		EntityProcedure initialProcedure = new EntityProcedure(initialState);
		
		String procedureText/* 0*/ = "";
		procedureText += /* 1*/ "CREATE task1 FROM Task;\n";
		procedureText += /* 2*/ "CREATE user1 FROM User;\n";
		procedureText += /* 3*/ "RELATE task1 TO user1 ACROSS R1 CREATING assignment1;\n";
		procedureText += /* 4*/ "CREATE user2 FROM User;\n";
		procedureText += /* 5*/ "RELATE task1 TO user2 ACROSS R1 CREATING assignment2;\n";
		procedureText += /* 6*/ "SELECT ONE selectedAssignment1 THAT RELATES task1 TO user1 ACROSS R1;\n";
		procedureText += /* 7*/ "SELECT ONE selectedAssignment2 THAT RELATES task1 TO user2 ACROSS R1;\n";
		procedureText += /* 8*/ "SELECT ONE selectedAssignment1a THAT RELATES user1 TO task1 ACROSS R1;\n";
		procedureText += /* 9*/ "SELECT ONE selectedAssignment2a THAT RELATES user2 TO task1 ACROSS R1;\n";
		
		initialProcedure.setProcedure(procedureText);

		TestHelper.addNewInitialStateToClassWithEventToPreviousInitialState(domain.getEntityClassWithName("Task"));
		
		Simulator simulator = new Simulator(domain);
		simulator.setSimulatingState(initialState);

		simulator.getSimulatingState().simulate();
		
		SimulatedInstance assignment1 = simulator.getSimulatingState().getInstanceWithName("assignment1");
		SimulatedInstance assignment2 = simulator.getSimulatingState().getInstanceWithName("assignment2");
		SimulatedInstance selectedAssignment1 = simulator.getSimulatingState().getInstanceWithName("selectedAssignment1");
		SimulatedInstance selectedAssignment2 = simulator.getSimulatingState().getInstanceWithName("selectedAssignment2");
		SimulatedInstance selectedAssignment1a = simulator.getSimulatingState().getInstanceWithName("selectedAssignment1a");
		SimulatedInstance selectedAssignment2a = simulator.getSimulatingState().getInstanceWithName("selectedAssignment2a");
		Assert.assertEquals(assignment1, selectedAssignment1);
		Assert.assertEquals(assignment2, selectedAssignment2);
		Assert.assertEquals(assignment1, selectedAssignment1a);
		Assert.assertEquals(assignment2, selectedAssignment2a);
		
	}
	
	public void test_can_select_non_reflexive_association_relation_instance_via_actionlanguage_in_other_direction() throws NameAlreadyBoundException, InvalidActionLanguageSyntaxException, CannotSimulateDomainThatIsInvalidException, NameNotFoundException
	{
		EntityDomain domain = DomainTTD.getTTDDomain();
		EntityState initialState = new EntityState("initial");
		domain.getEntityClassWithName("Task").addState(initialState);
		EntityProcedure initialProcedure = new EntityProcedure(initialState);
		
		String procedureText/* 0*/ = "";
		procedureText += /* 1*/ "CREATE task1 FROM Task;\n";
		procedureText += /* 2*/ "CREATE user1 FROM User;\n";
		procedureText += /* 3*/ "RELATE user1 TO task1 ACROSS R1 CREATING assignment1;\n";
		procedureText += /* 4*/ "CREATE user2 FROM User;\n";
		procedureText += /* 5*/ "RELATE user2 TO task1 ACROSS R1 CREATING assignment2;\n";
		procedureText += /* 6*/ "SELECT ONE selectedAssignment1 THAT RELATES task1 TO user1 ACROSS R1;\n";
		procedureText += /* 7*/ "SELECT ONE selectedAssignment2 THAT RELATES task1 TO user2 ACROSS R1;\n";
		procedureText += /* 8*/ "SELECT ONE selectedAssignment1a THAT RELATES user1 TO task1 ACROSS R1;\n";
		procedureText += /* 9*/ "SELECT ONE selectedAssignment2a THAT RELATES user2 TO task1 ACROSS R1;\n";
		
		initialProcedure.setProcedure(procedureText);

		TestHelper.addNewInitialStateToClassWithEventToPreviousInitialState(domain.getEntityClassWithName("Task"));
		
		Simulator simulator = new Simulator(domain);
		simulator.setSimulatingState(initialState);

		simulator.getSimulatingState().simulate();
		
		SimulatedInstance assignment1 = simulator.getSimulatingState().getInstanceWithName("assignment1");
		SimulatedInstance assignment2 = simulator.getSimulatingState().getInstanceWithName("assignment2");
		SimulatedInstance selectedAssignment1 = simulator.getSimulatingState().getInstanceWithName("selectedAssignment1");
		SimulatedInstance selectedAssignment2 = simulator.getSimulatingState().getInstanceWithName("selectedAssignment2");
		SimulatedInstance selectedAssignment1a = simulator.getSimulatingState().getInstanceWithName("selectedAssignment1a");
		SimulatedInstance selectedAssignment2a = simulator.getSimulatingState().getInstanceWithName("selectedAssignment2a");
		Assert.assertEquals(assignment1, selectedAssignment1);
		Assert.assertEquals(assignment2, selectedAssignment2);
		Assert.assertEquals(assignment1, selectedAssignment1a);
		Assert.assertEquals(assignment2, selectedAssignment2a);
	}
	
	public void test_can_select_non_reflexive_association_relation_instance_via_actionlanguage_and_get_null_instance() throws NameAlreadyBoundException, InvalidActionLanguageSyntaxException, CannotSimulateDomainThatIsInvalidException, NameNotFoundException
	{
		EntityDomain domain = DomainTTD.getTTDDomain();
		EntityState initialState = new EntityState("initial");
		domain.getEntityClassWithName("Task").addState(initialState);
		EntityProcedure initialProcedure = new EntityProcedure(initialState);
		
		String procedureText/* 0*/ = "";
		procedureText += /* 1*/ "CREATE task1 FROM Task;\n";
		procedureText += /* 2*/ "CREATE user1 FROM User;\n";
		procedureText += /* 3*/ "SELECT ONE selectedAssignment1 THAT RELATES task1 TO user1 ACROSS R1;\n";
		
		initialProcedure.setProcedure(procedureText);

		TestHelper.addNewInitialStateToClassWithEventToPreviousInitialState(domain.getEntityClassWithName("Task"));
		
		Simulator simulator = new Simulator(domain);
		simulator.setSimulatingState(initialState);

		simulator.getSimulatingState().simulate();
		

		SimulatedInstance selectedAssignment1 = simulator.getSimulatingState().getInstanceWithName("selectedAssignment1");
		Assert.assertTrue(selectedAssignment1 instanceof NullSimulatedInstance);
	}
	
	public void test_can_select_reflexive_association_relation_instance_via_actionlanguage() throws NameAlreadyBoundException, InvalidActionLanguageSyntaxException, CannotSimulateDomainThatIsInvalidException, NameNotFoundException
	{
		EntityDomain domain = DomainTTD.getTTDDomain();
		EntityState initialState = new EntityState("initial");
		domain.getEntityClassWithName("Task").addState(initialState);
		EntityProcedure initialProcedure = new EntityProcedure(initialState);
		
		String procedureText/* 0*/ = "";
		procedureText += /* 1*/ "CREATE step1 FROM Step;\n";
		procedureText += /* 2*/ "CREATE step2 FROM Step;\n";
		procedureText += /* 3*/ "RELATE step1 TO step2 ACROSS R3.\"Leads\" CREATING sequence1;\n";
		procedureText += /* 4*/ "CREATE step3 FROM Step;\n";
		procedureText += /* 5*/ "RELATE step3 TO step2 ACROSS R3.\"Leads\" CREATING sequence2;\n";
		procedureText += /* 7*/ "SELECT ONE selectedSequence1 THAT RELATES step1 TO step2 ACROSS R3.\"Leads\";\n";
		procedureText += /* 8*/ "SELECT ONE selectedSequence2 THAT RELATES step2 TO step1 ACROSS R3.\"Follows\";\n";
		
		initialProcedure.setProcedure(procedureText);

		TestHelper.addNewInitialStateToClassWithEventToPreviousInitialState(domain.getEntityClassWithName("Task"));
		
		Simulator simulator = new Simulator(domain);
		simulator.setSimulatingState(initialState);

		simulator.getSimulatingState().simulate();
		
		SimulatedInstance sequence1 = simulator.getSimulatingState().getInstanceWithName("sequence1");
		SimulatedInstance selectedSequence1 = simulator.getSimulatingState().getInstanceWithName("selectedSequence1");
		SimulatedInstance selectedSequence2 = simulator.getSimulatingState().getInstanceWithName("selectedSequence2");
		Assert.assertEquals(sequence1, selectedSequence1);
		Assert.assertEquals(sequence1, selectedSequence2);
		Assert.assertNotNull(sequence1);
		
	}
	
	public void test_can_select_reflexive_association_relation_instance_via_actionlanguage_in_other_direction() throws NameAlreadyBoundException, InvalidActionLanguageSyntaxException, CannotSimulateDomainThatIsInvalidException, NameNotFoundException
	{
		EntityDomain domain = DomainTTD.getTTDDomain();
		EntityState initialState = new EntityState("initial");
		domain.getEntityClassWithName("Task").addState(initialState);
		EntityProcedure initialProcedure = new EntityProcedure(initialState);
		
		String procedureText/* 0*/ = "";
		procedureText += /* 1*/ "CREATE step1 FROM Step;\n";
		procedureText += /* 2*/ "CREATE step2 FROM Step;\n";
		procedureText += /* 3*/ "RELATE step2 TO step1 ACROSS R3.\"Follows\" CREATING sequence1;\n";
		procedureText += /* 4*/ "CREATE step3 FROM Step;\n";
		procedureText += /* 5*/ "RELATE step3 TO step2 ACROSS R3.\"Follows\" CREATING sequence2;\n";
		procedureText += /* 7*/ "SELECT ONE selectedSequence1 THAT RELATES step2 TO step1 ACROSS R3.\"Follows\";\n";
		procedureText += /* 8*/ "SELECT ONE selectedSequence2 THAT RELATES step1 TO step2 ACROSS R3.\"Leads\";\n";
		
		initialProcedure.setProcedure(procedureText);

		TestHelper.addNewInitialStateToClassWithEventToPreviousInitialState(domain.getEntityClassWithName("Task"));
		
		Simulator simulator = new Simulator(domain);
		simulator.setSimulatingState(initialState);

		simulator.getSimulatingState().simulate();

		SimulatedInstance sequence1 = simulator.getSimulatingState().getInstanceWithName("sequence1");
		SimulatedInstance selectedSequence1 = simulator.getSimulatingState().getInstanceWithName("selectedSequence1");
		SimulatedInstance selectedSequence2 = simulator.getSimulatingState().getInstanceWithName("selectedSequence2");
		Assert.assertEquals(sequence1, selectedSequence1);
		Assert.assertEquals(sequence1, selectedSequence2);
		Assert.assertNotNull(sequence1);
	}
	
	public void test_reflexive_relationship_select_one_passes_with_correct_verb_and_fails_with_incorrect() throws NameAlreadyBoundException, NameNotFoundException, InvalidActionLanguageSyntaxException, CannotSimulateDomainThatIsInvalidException
	{
		EntityDomain domain = DomainTTD.getTTDDomain();
		EntityState initialState = new EntityState("initial");
		domain.getEntityClassWithName("Task").addState(initialState);
		EntityProcedure initialProcedure = new EntityProcedure(initialState);
		
		String procedureText/* 0*/ = "";
		procedureText += /* 1*/ "CREATE step1 FROM Step;\n";
		procedureText += /* 2*/ "CREATE step2 FROM Step;\n";
		procedureText += /* 3*/ "RELATE step1 TO step2 ACROSS R3.\"Leads\" CREATING sequence1;\n";
		procedureText += /* 4*/ "SELECT ONE selectedSequence1 THAT RELATES step1 TO step2 ACROSS R3.\"Follows\";\n";
		procedureText += /* 5*/ "SELECT ONE selectedSequence2 THAT RELATES step1 TO step2 ACROSS R3.\"Leads\";\n";
		
		initialProcedure.setProcedure(procedureText);

		TestHelper.addNewInitialStateToClassWithEventToPreviousInitialState(domain.getEntityClassWithName("Task"));
		
		Simulator simulator = new Simulator(domain);
		simulator.setSimulatingState(initialState);

		simulator.getSimulatingState().simulate();

		SimulatedInstance selectedSequence1 = simulator.getSimulatingState().getInstanceWithName("selectedSequence1");
		Assert.assertTrue(selectedSequence1 instanceof NullSimulatedInstance);
		SimulatedInstance selectedSequence2 = simulator.getSimulatingState().getInstanceWithName("selectedSequence2");
		Assert.assertTrue(!(selectedSequence2 instanceof NullSimulatedInstance));
	}
	
	public void test_reflexive_relationship_can_have_an_association_instance_for_either_verb() throws NameAlreadyBoundException, InvalidActionLanguageSyntaxException, NameNotFoundException, CannotSimulateDomainThatIsInvalidException
	{
		EntityDomain domain = DomainTTD.getTTDDomain();
		EntityState initialState = new EntityState("initial");
		domain.getEntityClassWithName("Task").addState(initialState);
		EntityProcedure initialProcedure = new EntityProcedure(initialState);
		
		String procedureText/* 0*/ = "";
		procedureText += /* 1*/ "CREATE step1 FROM Step;\n";
		procedureText += /* 2*/ "CREATE step2 FROM Step;\n";
		procedureText += /* 3*/ "RELATE step2 TO step1 ACROSS R3.\"Follows\" CREATING sequence1;\n";
		procedureText += /* 4*/ "RELATE step2 TO step1 ACROSS R3.\"Leads\" CREATING sequence2;\n";
		procedureText += /* 5*/ "SELECT ONE selectedSequence1 THAT RELATES step2 TO step1 ACROSS R3.\"Follows\";\n";
		procedureText += /* 6*/ "SELECT ONE selectedSequence2 THAT RELATES step1 TO step2 ACROSS R3.\"Leads\";\n";
		procedureText += /* 7*/ "SELECT ONE selectedSequence3 THAT RELATES step1 TO step2 ACROSS R3.\"Follows\";\n";
		procedureText += /* 8*/ "SELECT ONE selectedSequence4 THAT RELATES step2 TO step1 ACROSS R3.\"Leads\";\n";
		
		initialProcedure.setProcedure(procedureText);

		TestHelper.addNewInitialStateToClassWithEventToPreviousInitialState(domain.getEntityClassWithName("Task"));
		
		Simulator simulator = new Simulator(domain);
		simulator.setSimulatingState(initialState);

		simulator.getSimulatingState().simulate();

		SimulatedInstance sequence1 = simulator.getSimulatingState().getInstanceWithName("sequence1");
		SimulatedInstance sequence2 = simulator.getSimulatingState().getInstanceWithName("sequence2");
		SimulatedInstance selectedSequence1 = simulator.getSimulatingState().getInstanceWithName("selectedSequence1");
		SimulatedInstance selectedSequence2 = simulator.getSimulatingState().getInstanceWithName("selectedSequence2");
		SimulatedInstance selectedSequence3 = simulator.getSimulatingState().getInstanceWithName("selectedSequence3");
		SimulatedInstance selectedSequence4 = simulator.getSimulatingState().getInstanceWithName("selectedSequence4");
		
		Assert.assertEquals(sequence1, selectedSequence1);
		Assert.assertEquals(sequence1, selectedSequence2);

		Assert.assertEquals(sequence2, selectedSequence3);
		Assert.assertEquals(sequence2, selectedSequence4);
		
		Assert.assertTrue(!sequence1.equals(sequence2));
		
		Assert.assertNotNull(sequence1);
		Assert.assertNotNull(sequence2);
	}
	
	public void test_can_get_null_instance_from_reflexive_relationship_select_one() throws NameAlreadyBoundException, InvalidActionLanguageSyntaxException, NameNotFoundException, CannotSimulateDomainThatIsInvalidException
	{
		EntityDomain domain = DomainTTD.getTTDDomain();
		EntityState initialState = new EntityState("initial");
		domain.getEntityClassWithName("Task").addState(initialState);
		EntityProcedure initialProcedure = new EntityProcedure(initialState);
		
		String procedureText/* 0*/ = "";
		procedureText += /* 1*/ "CREATE step1 FROM Step;\n";
		procedureText += /* 2*/ "CREATE step2 FROM Step;\n";
		procedureText += /* 3*/ "SELECT ONE selectedSequence1 THAT RELATES step2 TO step1 ACROSS R3.\"Follows\";\n";
		
		initialProcedure.setProcedure(procedureText);

		TestHelper.addNewInitialStateToClassWithEventToPreviousInitialState(domain.getEntityClassWithName("Task"));
		
		Simulator simulator = new Simulator(domain);
		simulator.setSimulatingState(initialState);

		simulator.getSimulatingState().simulate();

		SimulatedInstance selectedSequence1 = simulator.getSimulatingState().getInstanceWithName("selectedSequence1");
		Assert.assertTrue(selectedSequence1 instanceof NullSimulatedInstance);
		
	}

	public void test_when_unrelating_association_relations_the_association_is_deleted() throws NameAlreadyBoundException, InvalidActionLanguageSyntaxException, CannotSimulateDomainThatIsInvalidException
	{
		EntityDomain domain = DomainTTD.getTTDDomain();
		EntityState initialState = new EntityState("initial");
		domain.getEntityClassWithName("Task").addState(initialState);
		EntityProcedure initialProcedure = new EntityProcedure(initialState);
		
		String procedureText = "";
		procedureText += "wasAgeSet = false;\n";
		procedureText += "wasAssingmentDeleted = false;\n";
		
		procedureText += "CREATE user1 FROM User;\n";
		procedureText += "CREATE task1 FROM Task;\n";
		procedureText += "RELATE task1 TO user1 ACROSS R1 CREATING assignment1;\n";
		procedureText += "assignment1.Age = 5;\n";
		
		procedureText += "SELECT ANY assignmentForCheck FROM INSTANCES OF Assignment;\n";
		procedureText += "IF NOT EMPTY assignmentForCheck THEN\n";
		procedureText += "	IF assignmentForCheck.Age == 5 THEN\n";
		procedureText += "		wasAgeSet = true;\n";
		procedureText += "	END IF;\n";
		procedureText += "END IF;\n";
		
		procedureText += "UNRELATE task1 FROM user1 ACROSS R1;\n";
		
		procedureText += "SELECT ANY assignmentForDelete FROM INSTANCES OF Assignment;\n";
		procedureText += "IF EMPTY assignmentForDelete THEN\n";
		procedureText += "	wasAssignmentDeleted = true;\n";
		procedureText += "END IF;\n";
		
		initialProcedure.setProcedure(procedureText);

		TestHelper.addNewInitialStateToClassWithEventToPreviousInitialState(domain.getEntityClassWithName("Task"));
		
		Simulator simulator = new Simulator(domain);
		simulator.setSimulatingState(initialState);

		simulator.getSimulatingState().simulate();
		
		Assert.assertEquals(true, simulator.getSimulatingState().getTempVariable("wasAgeSet"));
		Assert.assertEquals(true, simulator.getSimulatingState().getTempVariable("wasAssignmentDeleted"));
	}
	
	public void test_when_unrelating_association_reflexive_relations_the_association_is_deleted() throws NameAlreadyBoundException, InvalidActionLanguageSyntaxException, CannotSimulateDomainThatIsInvalidException
	{
		EntityDomain domain = DomainTTD.getTTDDomain();
		EntityState initialState = new EntityState("initial");
		domain.getEntityClassWithName("Task").addState(initialState);
		EntityProcedure initialProcedure = new EntityProcedure(initialState);
		
		String procedureText = "";
		procedureText += "wasOrderSet = false;\n";
		procedureText += "wasSequenceDeleted = false;\n";
		
		procedureText += "CREATE step1 FROM Step;\n";
		procedureText += "CREATE step2 FROM Step;\n";
		procedureText += "RELATE step1 TO step2 ACROSS R3.\"Leads\" CREATING sequence1;\n";
		procedureText += "sequence1.Order = 5;\n";
		
		procedureText += "SELECT ANY sequenceForCheck FROM INSTANCES OF Sequence;\n";
		procedureText += "IF NOT EMPTY sequenceForCheck THEN\n";
		procedureText += "	IF sequenceForCheck.Order == 5 THEN\n";
		procedureText += "		wasOrderSet = true;\n";
		procedureText += "	END IF;\n";
		procedureText += "END IF;\n";
		
		procedureText += "UNRELATE step1 FROM step2 ACROSS R3.\"Leads\";\n";
		
		procedureText += "SELECT ANY sequenceForDelete FROM INSTANCES OF Sequence;\n";
		procedureText += "IF EMPTY sequenceForDelete THEN\n";
		procedureText += "	wasSequenceDeleted = true;\n";
		procedureText += "END IF;\n";
		
		initialProcedure.setProcedure(procedureText);

		TestHelper.addNewInitialStateToClassWithEventToPreviousInitialState(domain.getEntityClassWithName("Task"));
		
		Simulator simulator = new Simulator(domain);
		simulator.setSimulatingState(initialState);

		simulator.getSimulatingState().simulate();
		
		Assert.assertEquals(true, simulator.getSimulatingState().getTempVariable("wasOrderSet"));
		Assert.assertEquals(true, simulator.getSimulatingState().getTempVariable("wasSequenceDeleted"));
	}
	
	public void test_can_select_over_relations() throws NameAlreadyBoundException, InvalidActionLanguageSyntaxException, CannotSimulateDomainThatIsInvalidException, NameNotFoundException
	{
		EntityDomain domain = DomainTTD.getTTDDomain();
		EntityState initialState = new EntityState("initial");
		domain.getEntityClassWithName("Task").addState(initialState);
		EntityProcedure initialProcedure = new EntityProcedure(initialState);
		
		String procedureText = "";
		procedureText += "CREATE user1 FROM User;\n";
		procedureText += "CREATE task1 FROM Task;\n";
		procedureText += "CREATE step1 FROM Step;\n";
		procedureText += "RELATE task1 TO step1 ACROSS R2;\n";
		procedureText += "RELATE task1 TO user1 ACROSS R1 CREATING assignment1;\n";
		procedureText += "SELECT ANY selectedStep RELATED BY user1->R1->R2;\n";

		
		initialProcedure.setProcedure(procedureText);

		TestHelper.addNewInitialStateToClassWithEventToPreviousInitialState(domain.getEntityClassWithName("Task"));
		
		Simulator simulator = new Simulator(domain);
		simulator.setSimulatingState(initialState);

		simulator.getSimulatingState().simulate();
		
		SimulatedInstance selectedStep = simulator.getSimulatingState().getInstanceWithName("selectedStep");
		SimulatedInstance step1 = simulator.getSimulatingState().getInstanceWithName("step1");
		
		Assert.assertEquals(step1, selectedStep);
	}
	
	public void test_can_select_over_reflexive_relation() throws NameAlreadyBoundException, InvalidActionLanguageSyntaxException, CannotSimulateDomainThatIsInvalidException, NameNotFoundException
	{
		EntityDomain domain = DomainTTD.getTTDDomain();
		EntityState initialState = new EntityState("initial");
		domain.getEntityClassWithName("Task").addState(initialState);
		EntityProcedure initialProcedure = new EntityProcedure(initialState);
		
		String procedureText = "";
		procedureText += "CREATE user1 FROM User;\n";
		procedureText += "CREATE task1 FROM Task;\n";
		procedureText += "CREATE step1 FROM Step;\n";
		procedureText += "CREATE step2 FROM Step;\n";
		procedureText += "RELATE task1 TO step1 ACROSS R2;\n";
		procedureText += "RELATE task1 TO user1 ACROSS R1 CREATING assignment1;\n";
		procedureText += "RELATE step1 TO step2 ACROSS R3.\"Leads\" CREATING sequence1;\n";
		procedureText += "SELECT ANY selectedStep RELATED BY user1->R1->R2->R3.\"Leads\";\n";

		
		initialProcedure.setProcedure(procedureText);

		TestHelper.addNewInitialStateToClassWithEventToPreviousInitialState(domain.getEntityClassWithName("Task"));
		
		Simulator simulator = new Simulator(domain);
		simulator.setSimulatingState(initialState);

		simulator.getSimulatingState().simulate();
		
		SimulatedInstance selectedStep = simulator.getSimulatingState().getInstanceWithName("selectedStep");
		SimulatedInstance step2 = simulator.getSimulatingState().getInstanceWithName("step2");
		
		Assert.assertEquals(step2, selectedStep);
	}
	
	public void test_can_select_over_reflexive_relation_and_can_get_null_instance() throws NameAlreadyBoundException, InvalidActionLanguageSyntaxException, CannotSimulateDomainThatIsInvalidException, NameNotFoundException
	{
		EntityDomain domain = DomainTTD.getTTDDomain();
		EntityState initialState = new EntityState("initial");
		domain.getEntityClassWithName("Task").addState(initialState);
		EntityProcedure initialProcedure = new EntityProcedure(initialState);
		
		String procedureText = "";
		procedureText += "CREATE user1 FROM User;\n";
		procedureText += "CREATE task1 FROM Task;\n";
		procedureText += "CREATE step1 FROM Step;\n";
		procedureText += "CREATE step2 FROM Step;\n";
		procedureText += "RELATE task1 TO step1 ACROSS R2;\n";
		procedureText += "RELATE task1 TO user1 ACROSS R1 CREATING assignment1;\n";
		procedureText += "RELATE step1 TO step2 ACROSS R3.\"Leads\" CREATING sequence1;\n";
		procedureText += "SELECT ANY selectedStep RELATED BY user1->R1->R2->R3.\"Follows\";\n";

		
		initialProcedure.setProcedure(procedureText);

		TestHelper.addNewInitialStateToClassWithEventToPreviousInitialState(domain.getEntityClassWithName("Task"));
		
		Simulator simulator = new Simulator(domain);
		simulator.setSimulatingState(initialState);

		simulator.getSimulatingState().simulate();
		
		SimulatedInstance selectedStep = simulator.getSimulatingState().getInstanceWithName("selectedStep");
		Assert.assertTrue(selectedStep instanceof NullSimulatedInstance);
	}
	
	public void test_can_select_over_intermediate_reflexive_relation() throws NameAlreadyBoundException, InvalidActionLanguageSyntaxException, CannotSimulateDomainThatIsInvalidException, NameNotFoundException
	{
		EntityDomain domain = DomainTTD.getTTDDomain();
		EntityState initialState = new EntityState("initial");
		domain.getEntityClassWithName("Task").addState(initialState);
		EntityProcedure initialProcedure = new EntityProcedure(initialState);
		
		String procedureText = "";
		procedureText += "CREATE user1 FROM User;\n";
		procedureText += "CREATE task1 FROM Task;\n";
		procedureText += "CREATE task2 FROM Task;\n";
		procedureText += "CREATE task3 FROM Task;\n";
		procedureText += "CREATE step1 FROM Step;\n";
		
		procedureText += "RELATE user1 TO task1 ACROSS R1 CREATING assignment1;\n";
		procedureText += "RELATE task1 TO task2 ACROSS R4.\"Leads\";\n";
		procedureText += "RELATE task2 TO task3 ACROSS R4.\"Leads\";\n";
		procedureText += "RELATE task3 TO step1 ACROSS R2;\n";
		
		procedureText += "SELECT ANY selectedStep RELATED BY user1->R1->R4.\"Leads\"->R4.\"Leads\"->R2;\n";

		initialProcedure.setProcedure(procedureText);

		TestHelper.addNewInitialStateToClassWithEventToPreviousInitialState(domain.getEntityClassWithName("Task"));
		
		Simulator simulator = new Simulator(domain);
		simulator.setSimulatingState(initialState);

		simulator.getSimulatingState().simulate();
		
		SimulatedInstance selectedStep = simulator.getSimulatingState().getInstanceWithName("selectedStep");
		SimulatedInstance step1 = simulator.getSimulatingState().getInstanceWithName("step1");
		
		Assert.assertEquals(step1, selectedStep);
	}
	
	public void test_can_select_many_over_intermediate_reflexive_relation() throws NameAlreadyBoundException, InvalidActionLanguageSyntaxException, CannotSimulateDomainThatIsInvalidException, NameNotFoundException
	{
		EntityDomain domain = DomainTTD.getTTDDomain();
		EntityState initialState = new EntityState("initial");
		domain.getEntityClassWithName("Task").addState(initialState);
		EntityProcedure initialProcedure = new EntityProcedure(initialState);
		
		String procedureText = "";
		procedureText += "CREATE user1 FROM User;\n";
		procedureText += "CREATE task1 FROM Task;\n";
		procedureText += "CREATE task2 FROM Task;\n";
		procedureText += "CREATE task3 FROM Task;\n";
		procedureText += "CREATE step1 FROM Step;\n";
		procedureText += "CREATE step2 FROM Step;\n";
		procedureText += "CREATE step3 FROM Step;\n";
		
		procedureText += "RELATE user1 TO task1 ACROSS R1 CREATING assignment1;\n";
		procedureText += "RELATE task1 TO task2 ACROSS R4.\"Leads\";\n";
		procedureText += "RELATE task2 TO task3 ACROSS R4.\"Leads\";\n";
		procedureText += "RELATE task3 TO step1 ACROSS R2;\n";
		procedureText += "RELATE task3 TO step2 ACROSS R2;\n";
		
		procedureText += "SELECT MANY selectedSteps RELATED BY user1->R1->R4.\"Leads\"->R4.\"Leads\"->R2;\n";
		procedureText += "FOR selectedStep IN selectedSteps DO\n";
		procedureText += "	selectedStep.Complete = true;\n";
		procedureText += "END FOR;\n";

		initialProcedure.setProcedure(procedureText);

		TestHelper.addNewInitialStateToClassWithEventToPreviousInitialState(domain.getEntityClassWithName("Task"));
		
		Simulator simulator = new Simulator(domain);
		simulator.setSimulatingState(initialState);

		simulator.getSimulatingState().simulate();
		
		SimulatedInstance step1 = simulator.getSimulatingState().getInstanceWithName("step1");
		SimulatedInstance step2 = simulator.getSimulatingState().getInstanceWithName("step2");
		SimulatedInstance step3 = simulator.getSimulatingState().getInstanceWithName("step3");
		
		Assert.assertEquals(true, step1.getAttribute("Complete"));
		Assert.assertEquals(true, step2.getAttribute("Complete"));
		Assert.assertEquals(false, step3.getAttribute("Complete"));
	}
	
	public void test_can_select_many_over_intermediate_reflexive_relation_where_logic() throws NameAlreadyBoundException, InvalidActionLanguageSyntaxException, CannotSimulateDomainThatIsInvalidException, NameNotFoundException
	{
		EntityDomain domain = DomainTTD.getTTDDomain();
		EntityState initialState = new EntityState("initial");
		domain.getEntityClassWithName("Task").addState(initialState);
		EntityProcedure initialProcedure = new EntityProcedure(initialState);
		
		EntityClass step = domain.getEntityClassWithName("Step");
		step.addAttribute(new EntityAttribute("Tag", IntegerEntityDatatype.getInstance()));
		
		String procedureText = "";
		procedureText += "CREATE user1 FROM User;\n";
		procedureText += "CREATE task1 FROM Task;\n";
		procedureText += "CREATE task2 FROM Task;\n";
		procedureText += "CREATE task3 FROM Task;\n";
		procedureText += "CREATE step1 FROM Step;\n";
		procedureText += "CREATE step2 FROM Step;\n";
		procedureText += "CREATE step3 FROM Step;\n";
		
		procedureText += "step1.Tag = 10;\n";
		procedureText += "step2.Tag = 20;\n";
		procedureText += "step3.Tag = 30;\n";
		
		procedureText += "RELATE user1 TO task1 ACROSS R1 CREATING assignment1;\n";
		procedureText += "RELATE task1 TO task2 ACROSS R4.\"Leads\";\n";
		procedureText += "RELATE task2 TO task3 ACROSS R4.\"Leads\";\n";
		procedureText += "RELATE task3 TO step1 ACROSS R2;\n";
		procedureText += "RELATE task3 TO step2 ACROSS R2;\n";
		
		procedureText += "SELECT MANY selectedSteps RELATED BY user1->R1->R4.\"Leads\"->R4.\"Leads\"->R2 WHERE selected.Tag >= 20;\n";
		procedureText += "FOR selectedStep IN selectedSteps DO\n";
		procedureText += "	selectedStep.Complete = true;\n";
		procedureText += "END FOR;\n";

		initialProcedure.setProcedure(procedureText);

		TestHelper.addNewInitialStateToClassWithEventToPreviousInitialState(domain.getEntityClassWithName("Task"));
		
		Simulator simulator = new Simulator(domain);
		simulator.setSimulatingState(initialState);

		simulator.getSimulatingState().simulate();
		
		SimulatedInstance step1 = simulator.getSimulatingState().getInstanceWithName("step1");
		SimulatedInstance step2 = simulator.getSimulatingState().getInstanceWithName("step2");
		SimulatedInstance step3 = simulator.getSimulatingState().getInstanceWithName("step3");
		
		Assert.assertEquals(false, step1.getAttribute("Complete"));
		Assert.assertEquals(true, step2.getAttribute("Complete"));
		Assert.assertEquals(false, step3.getAttribute("Complete"));
	}
	
	public void test_can_select_many_over_intermediate_reflexive_relation_where_logic_null_instance() throws NameAlreadyBoundException, InvalidActionLanguageSyntaxException, CannotSimulateDomainThatIsInvalidException, NameNotFoundException
	{
		EntityDomain domain = DomainTTD.getTTDDomain();
		EntityState initialState = new EntityState("initial");
		domain.getEntityClassWithName("Task").addState(initialState);
		EntityProcedure initialProcedure = new EntityProcedure(initialState);
		
		EntityClass step = domain.getEntityClassWithName("Step");
		step.addAttribute(new EntityAttribute("Tag", IntegerEntityDatatype.getInstance()));
		
		String procedureText = "";
		procedureText += "CREATE user1 FROM User;\n";
		procedureText += "CREATE task1 FROM Task;\n";
		procedureText += "CREATE task2 FROM Task;\n";
		procedureText += "CREATE task3 FROM Task;\n";
		procedureText += "CREATE step1 FROM Step;\n";
		procedureText += "CREATE step2 FROM Step;\n";
		procedureText += "CREATE step3 FROM Step;\n";
		
		procedureText += "step1.Tag = 10;\n";
		procedureText += "step2.Tag = 20;\n";
		procedureText += "step3.Tag = 30;\n";
		
		procedureText += "RELATE user1 TO task1 ACROSS R1 CREATING assignment1;\n";
		procedureText += "RELATE task1 TO task2 ACROSS R4.\"Leads\";\n";
		procedureText += "RELATE task2 TO task3 ACROSS R4.\"Leads\";\n";
		procedureText += "RELATE task3 TO step1 ACROSS R2;\n";
		procedureText += "RELATE task3 TO step2 ACROSS R2;\n";
		
		procedureText += "SELECT ANY selectedStep RELATED BY user1->R1->R4.\"Leads\"->R4.\"Leads\"->R2 WHERE selected.Tag >= 200;\n";

		initialProcedure.setProcedure(procedureText);

		TestHelper.addNewInitialStateToClassWithEventToPreviousInitialState(domain.getEntityClassWithName("Task"));
		
		Simulator simulator = new Simulator(domain);
		simulator.setSimulatingState(initialState);

		simulator.getSimulatingState().simulate();
		
		SimulatedInstance selectedStep = simulator.getSimulatingState().getInstanceWithName("selectedStep");
		Assert.assertTrue(selectedStep instanceof NullSimulatedInstance);
		Assert.assertEquals("Step", selectedStep.getSimulatedClass().getName());
	}
	
}
