package test.java.simulatorTests;

import test.java.helper.DomainBus;
import test.java.helper.TestHelper;

import javax.naming.NameAlreadyBoundException;
import javax.naming.NameNotFoundException;

import junit.framework.Assert;
import junit.framework.TestCase;
import main.java.avii.editor.contracts.metamodel.actionLanguage.syntax.InvalidActionLanguageSyntaxException;
import main.java.avii.editor.metamodel.entities.EntityClass;
import main.java.avii.editor.metamodel.entities.EntityDomain;
import main.java.avii.editor.metamodel.entities.EntityRelation;
import main.java.avii.editor.metamodel.entities.EntityState;
import main.java.avii.editor.metamodel.entities.EntityRelation.CardinalityType;
import main.java.avii.simulator.Simulator;
import main.java.avii.simulator.exceptions.CannotSimulateDomainThatIsInvalidException;
import main.java.avii.simulator.simulatedTypes.NullSimulatedInstance;
import main.java.avii.simulator.simulatedTypes.SimulatedHierarchyClass;
import main.java.avii.simulator.simulatedTypes.SimulatedHierarchyInstance;
import main.java.avii.simulator.simulatedTypes.SimulatedInstance;
import main.java.avii.simulator.simulatedTypes.SimulatedInstanceSet;
import main.java.avii.simulator.simulatedTypes.SimulatedState;

public class HierarchyClassRelationTests extends TestCase {

	public HierarchyClassRelationTests(String name)
	{
		super(name);
	}
	
	/**
	 * 
	 * Specialisation of the bus class 
           vehicle
              .
             /|\
              |
         .---------.  
        car       bus -------------R1-------- tyre
                   .                            .
                  /|\                          /|\
                   |                            |
             .-----------.               .-------------.  
         commercial   personal       roadtyre       snowtyre
         
                       Figure 2.

      
R3 is a relation between the specialised bus class, and the generic tyre class.
The only type of instances that will be returned when traversing R3 will be Bus
or Tyre.
Never will you get (or be able to get) the generic Vehicle for a Bus, or a specialised
road or snow tyre from Tyre.
                        
As an example, a Bus has 4 Tyres(2 road, 2 snow)

Note the results from the following action language.

SELECT MANY attachedTyres RELATED BY bus->R3;
[tyre1,tyre2,tyre3,tyre4]

SELECT MANY attachedTyres RELATED BY vehicle(with specialised bus instance)->R3;
[tyre1,tyre2,tyre3,tyre4]

SELECT ANY someVehicle RELATED BY snowTyre1->R3;
[bus1]

SELECT ANY someVehicle RELATED BY roadTyre1->R3;
[bus1]

SELECT MANY someVehicle RELATED by someTyre(with a specialised roadtyre instance)->R3;
[bus1]


Similarly, in Figure 2.
SELECT MANY personalBusses by someTyre(with a specialised roadtyre instance)->R3;
[bus1] // not necessarily a personalBus!!
	 * @throws NameAlreadyBoundException 
	 * @throws InvalidActionLanguageSyntaxException 
	 * @throws CannotSimulateDomainThatIsInvalidException 
	 * @throws NameNotFoundException 

	 * 
	 * 
	 */
	public void test_hierarchy_class_relations_as_per_documentation() throws NameAlreadyBoundException, InvalidActionLanguageSyntaxException, CannotSimulateDomainThatIsInvalidException, NameNotFoundException
	{
		EntityDomain domain = DomainBus.getBusDomain();
		
		String proc = "";
		proc += "CREATE personal FROM Personal;\n";
		proc += "CREATE roadTyre1 FROM RoadTyre;\n";
		proc += "CREATE roadTyre2 FROM RoadTyre;\n";
		proc += "CREATE snowTyre1 FROM SnowTyre;\n";
		proc += "CREATE snowTyre2 FROM SnowTyre;\n";
		
		proc += "RELATE personal TO roadTyre1 ACROSS R1;\n";
		proc += "RELATE personal TO roadTyre2 ACROSS R1;\n";
		proc += "RELATE personal TO snowTyre1 ACROSS R1;\n";
		proc += "RELATE personal TO snowTyre2 ACROSS R1;\n";
		
		proc += "SELECT MANY attachedTyres RELATED BY personal->R1;\n";
		//[tyre1,tyre2,tyre3,tyre4] - not [roadTyre1, roadTyre2, snowTyre1, snowTyre2]
		
		proc += "SELECT ANY bus1 FROM INSTANCES OF Bus;\n";
		proc += "SELECT MANY busAttachedTyres RELATED BY bus1->R1;\n";
		//[tyre1,tyre2,tyre3,tyre4] - not [roadTyre1, roadTyre2, snowTyre1, snowTyre2]
		
		proc += "SELECT ANY someVehicle1 RELATED BY snowTyre1->R1;\n";
		// [bus1]
		
		proc += "SELECT ANY someVehicle2 RELATED BY roadTyre1->R1;\n";
		// [bus1]
		
		proc += "SELECT ANY someTyre FROM INSTANCES OF Tyre;\n";
		proc += "SELECT ANY personalBusses RELATED BY someTyre->R1;\n";
		// [bus1]

		
		EntityState state = TestHelper.addNewDummyClassWithProcedure(domain, proc);
		
		Simulator simulator = new Simulator(domain);
		
		SimulatedHierarchyClass busClass = (SimulatedHierarchyClass) simulator.getSimulatedClass("Bus");
		SimulatedHierarchyClass tyreClass = (SimulatedHierarchyClass) simulator.getSimulatedClass("Tyre");

		
		simulator.setSimulatingState(state);
		SimulatedState sState = simulator.getSimulatingState();
		sState.simulate();
		
		
		SimulatedHierarchyInstance personal = (SimulatedHierarchyInstance) sState.getInstanceWithName("personal");
		SimulatedInstance bus = personal.getHierarchyInstanceForClass(busClass);
		
		SimulatedHierarchyInstance roadTyre1 = (SimulatedHierarchyInstance) sState.getInstanceWithName("roadTyre1");
		SimulatedInstance tyre1 = roadTyre1.getHierarchyInstanceForClass(tyreClass);
		
		SimulatedHierarchyInstance roadTyre2 = (SimulatedHierarchyInstance) sState.getInstanceWithName("roadTyre2");
		SimulatedInstance tyre2 = roadTyre2.getHierarchyInstanceForClass(tyreClass);
		
		SimulatedHierarchyInstance snowTyre1 = (SimulatedHierarchyInstance) sState.getInstanceWithName("snowTyre1");
		SimulatedInstance tyre3 = snowTyre1.getHierarchyInstanceForClass(tyreClass);
		
		SimulatedHierarchyInstance snowTyre2 = (SimulatedHierarchyInstance) sState.getInstanceWithName("snowTyre2");
		SimulatedInstance tyre4 = snowTyre2.getHierarchyInstanceForClass(tyreClass);
		
		Assert.assertEquals(0, tyre1.getIdentifier().getSequence());
		Assert.assertEquals(1, tyre2.getIdentifier().getSequence());
		Assert.assertEquals(2, tyre3.getIdentifier().getSequence());
		Assert.assertEquals(3, tyre4.getIdentifier().getSequence());
		
		SimulatedInstanceSet attachedTyres = sState.getInstanceSetWithName("attachedTyres");
		Assert.assertEquals(4, attachedTyres.size());
		Assert.assertEquals(false, attachedTyres.containsInstance(roadTyre1));
		Assert.assertEquals(false, attachedTyres.containsInstance(roadTyre2));
		Assert.assertEquals(false, attachedTyres.containsInstance(snowTyre1));
		Assert.assertEquals(false, attachedTyres.containsInstance(snowTyre2));
		Assert.assertEquals(true, attachedTyres.containsInstance(tyre1));
		Assert.assertEquals(true, attachedTyres.containsInstance(tyre2));
		Assert.assertEquals(true, attachedTyres.containsInstance(tyre3));
		Assert.assertEquals(true, attachedTyres.containsInstance(tyre4));
		
		SimulatedInstanceSet busAttachedTyres = sState.getInstanceSetWithName("busAttachedTyres");
		Assert.assertEquals(4, busAttachedTyres.size());
		Assert.assertEquals(false, busAttachedTyres.containsInstance(roadTyre1));
		Assert.assertEquals(false, busAttachedTyres.containsInstance(roadTyre2));
		Assert.assertEquals(false, busAttachedTyres.containsInstance(snowTyre1));
		Assert.assertEquals(false, busAttachedTyres.containsInstance(snowTyre2));
		Assert.assertEquals(true, busAttachedTyres.containsInstance(tyre1));
		Assert.assertEquals(true, busAttachedTyres.containsInstance(tyre2));
		Assert.assertEquals(true, busAttachedTyres.containsInstance(tyre3));
		Assert.assertEquals(true, busAttachedTyres.containsInstance(tyre4));
		
		SimulatedInstance someVehicle1 = sState.getInstanceWithName("someVehicle1");
		SimulatedInstance someVehicle2 = sState.getInstanceWithName("someVehicle2");
		SimulatedInstance personalBusses = sState.getInstanceWithName("personalBusses");
		
		Assert.assertEquals(bus, someVehicle1);
		Assert.assertEquals(bus, someVehicle2);
		Assert.assertEquals(bus, personalBusses);
		
	}
	
	public void test_bootstrap_domain_for_execution() throws NameAlreadyBoundException, InvalidActionLanguageSyntaxException, CannotSimulateDomainThatIsInvalidException{
		EntityDomain domain = DomainBus.getBusDomain();
		
		String proc = "temp = 1;\n";
		EntityState state = TestHelper.addNewDummyClassWithProcedure(domain, proc);
		
		Simulator simulator = new Simulator(domain);
		simulator.setSimulatingState(state);
		simulator.getSimulatingState().simulate();
		
		Assert.assertEquals(1.0, simulator.getSimulatingState().getTempVariable("temp"));
	}
	
	public void test_relationships_work_between_2_non_descendant_classes_in_the_same_hierarchy() throws NameAlreadyBoundException, InvalidActionLanguageSyntaxException, CannotSimulateDomainThatIsInvalidException, NameNotFoundException
	{
		EntityDomain domain = DomainBus.getBusDomain();
		EntityClass snowTyre = domain.getEntityClassWithName("SnowTyre");
		EntityClass roadTyre = domain.getEntityClassWithName("RoadTyre");
		EntityRelation r2 = new EntityRelation("R2");
		r2.setEndA(snowTyre, CardinalityType.ONE_TO_ONE);
		r2.setEndB(roadTyre, CardinalityType.ONE_TO_MANY);
		
		String proc = "";
		proc += "CREATE roadTyre1 FROM RoadTyre;\n";
		proc += "CREATE snowTyre1 FROM SnowTyre;\n";
		proc += "CREATE snowTyre2 FROM SnowTyre;\n";
		proc += "CREATE snowTyre3 FROM SnowTyre;\n";
		proc += "RELATE roadTyre1 TO snowTyre1 ACROSS R2;\n";
		proc += "RELATE roadTyre1 TO snowTyre2 ACROSS R2;\n";
		proc += "SELECT MANY snowTyres RELATED BY roadTyre1->R2;\n";
		proc += "SELECT ANY roadTyre2 RELATED BY snowTyre1->R2;\n";
		proc += "SELECT ANY roadTyre3 RELATED BY snowTyre2->R2;\n";
		proc += "SELECT ANY roadTyre4 RELATED BY snowTyre3->R2;\n";
		
		EntityState state = TestHelper.addNewDummyClassWithProcedure(domain, proc);
		Simulator simulator = new Simulator(domain);
		simulator.setSimulatingState(state);
		simulator.getSimulatingState().simulate();
		
		SimulatedInstanceSet snowTyres = simulator.getSimulatingState().getInstanceSetWithName("snowTyres");
		SimulatedInstance snowTyre1 = simulator.getSimulatingState().getInstanceWithName("snowTyre1");
		SimulatedInstance snowTyre2 = simulator.getSimulatingState().getInstanceWithName("snowTyre2");
		SimulatedInstance snowTyre3 = simulator.getSimulatingState().getInstanceWithName("snowTyre3");
		SimulatedInstance roadTyre1 = simulator.getSimulatingState().getInstanceWithName("roadTyre1");
		SimulatedInstance roadTyre2 = simulator.getSimulatingState().getInstanceWithName("roadTyre2");
		SimulatedInstance roadTyre3 = simulator.getSimulatingState().getInstanceWithName("roadTyre3");
		SimulatedInstance roadTyre4 = simulator.getSimulatingState().getInstanceWithName("roadTyre4");
		
		Assert.assertEquals(2, snowTyres.size());
		Assert.assertEquals(true, snowTyres.containsInstance(snowTyre1));
		Assert.assertEquals(true, snowTyres.containsInstance(snowTyre2));
		Assert.assertEquals(false, snowTyres.containsInstance(snowTyre3));
		
		Assert.assertEquals(false, roadTyre1 instanceof NullSimulatedInstance);
		Assert.assertEquals(roadTyre1, roadTyre2);
		Assert.assertEquals(roadTyre1, roadTyre3);
		Assert.assertEquals(true, roadTyre4 instanceof NullSimulatedInstance);
		
	}
	
	public void test_relationships_work_between_2_descendant_classes_in_the_same_hierarchy() throws NameAlreadyBoundException, InvalidActionLanguageSyntaxException, CannotSimulateDomainThatIsInvalidException, NameNotFoundException
	{
		EntityDomain domain = DomainBus.getBusDomain();
		EntityClass tyre = domain.getEntityClassWithName("Tyre");
		EntityClass roadTyre = domain.getEntityClassWithName("RoadTyre");
		EntityRelation r2 = new EntityRelation("R2");
		r2.setEndA(tyre, CardinalityType.ONE_TO_ONE, "leads");
		r2.setEndB(roadTyre, CardinalityType.ONE_TO_MANY, "follows");
		
		String proc = "";
		proc += "CREATE roadTyre1 FROM RoadTyre;\n";
		proc += "SELECT ANY tyre1 FROM INSTANCES OF Tyre;\n";
		proc += "CREATE roadTyre2 FROM RoadTyre;\n";
		proc += "CREATE roadTyre3 FROM RoadTyre;\n";
		
		proc += "RELATE tyre1 TO roadTyre2 ACROSS R2.\"leads\";\n";
		proc += "RELATE tyre1 TO roadTyre3 ACROSS R2.\"leads\";\n";
		
		proc += "SELECT MANY roadTyres RELATED BY tyre1->R2.\"leads\";\n";
		proc += "SELECT ANY tyre2 RELATED BY roadTyre1->R2.\"follows\";\n";
		proc += "SELECT ANY tyre3 RELATED BY roadTyre2->R2.\"follows\";\n";
		proc += "SELECT ANY tyre4 RELATED BY roadTyre3->R2.\"follows\";\n";

		EntityState state = TestHelper.addNewDummyClassWithProcedure(domain, proc);
		Simulator simulator = new Simulator(domain);
		simulator.setSimulatingState(state);
		simulator.getSimulatingState().simulate();
		
		SimulatedInstanceSet roadTyres = simulator.getSimulatingState().getInstanceSetWithName("roadTyres");
		SimulatedInstance tyre1 = simulator.getSimulatingState().getInstanceWithName("tyre1");
		SimulatedInstance tyre2 = simulator.getSimulatingState().getInstanceWithName("tyre2");
		SimulatedInstance tyre3 = simulator.getSimulatingState().getInstanceWithName("tyre3");
		SimulatedInstance tyre4 = simulator.getSimulatingState().getInstanceWithName("tyre4");
		SimulatedInstance roadTyre1 = simulator.getSimulatingState().getInstanceWithName("roadTyre1");
		SimulatedInstance roadTyre2 = simulator.getSimulatingState().getInstanceWithName("roadTyre2");
		SimulatedInstance roadTyre3 = simulator.getSimulatingState().getInstanceWithName("roadTyre3");
		
		
		Assert.assertEquals(2, roadTyres.size());
		Assert.assertEquals(true, roadTyres.containsInstance(roadTyre2));
		Assert.assertEquals(true, roadTyres.containsInstance(roadTyre3));
		Assert.assertEquals(false, roadTyres.containsInstance(roadTyre1));
		
		Assert.assertEquals(false, tyre1 instanceof NullSimulatedInstance);
		Assert.assertEquals(tyre1, tyre3);
		Assert.assertEquals(tyre1, tyre4);
		Assert.assertEquals(true, tyre2 instanceof NullSimulatedInstance);
	
	}
	
	
	public void test_can_relate_snowtyre_to_roadtyre_via_superclass() throws NameAlreadyBoundException, InvalidActionLanguageSyntaxException, CannotSimulateDomainThatIsInvalidException, NameNotFoundException
	{
		EntityDomain domain = DomainBus.getBusDomain();
		EntityClass tyre = domain.getEntityClassWithName("Tyre");
		EntityClass roadTyre = domain.getEntityClassWithName("RoadTyre");
		EntityRelation r2 = new EntityRelation("R2");
		r2.setEndA(tyre, CardinalityType.ONE_TO_ONE, "leads");
		r2.setEndB(roadTyre, CardinalityType.ONE_TO_MANY, "follows");
		
		String proc = "";
		proc += "CREATE snowTyre1 FROM SnowTyre;\n";
		proc += "SELECT ANY snowTyreTyre FROM INSTANCES OF Tyre;\n";
		proc += "CREATE roadTyre1 FROM RoadTyre;\n";
		
		proc += "RELATE snowTyre1 TO roadTyre1 ACROSS R2.\"leads\";\n";
		proc += "SELECT ANY roadTyre2 RELATED BY snowTyre1->R2.\"leads\";\n";
		proc += "SELECT ANY snowTyre2 RELATED BY roadTyre1->R2.\"follows\";\n";
		proc += "SELECT ANY roadTyre3 RELATED BY snowTyre1->R2.\"follows\";\n";
		proc += "SELECT ANY snowTyre3 RELATED BY roadTyre1->R2.\"leads\";\n";
		
		EntityState state = TestHelper.addNewDummyClassWithProcedure(domain, proc);
		Simulator simulator = new Simulator(domain);
		simulator.setSimulatingState(state);
		simulator.getSimulatingState().simulate();
		
		
		SimulatedInstance snowTyreTyre = simulator.getSimulatingState().getInstanceWithName("snowTyreTyre");
		SimulatedInstance roadTyre1 = simulator.getSimulatingState().getInstanceWithName("roadTyre1");
		SimulatedInstance roadTyre2 = simulator.getSimulatingState().getInstanceWithName("roadTyre2");
		SimulatedInstance snowTyre2 = simulator.getSimulatingState().getInstanceWithName("snowTyre2");
		SimulatedInstance roadTyre3 = simulator.getSimulatingState().getInstanceWithName("roadTyre3");
		SimulatedInstance snowTyre3 = simulator.getSimulatingState().getInstanceWithName("snowTyre3");
		

		Assert.assertEquals(roadTyre1, roadTyre2);
		Assert.assertEquals(snowTyre2, snowTyreTyre);
		
		Assert.assertEquals(true, roadTyre3 instanceof NullSimulatedInstance);
		Assert.assertEquals(true, snowTyre3 instanceof NullSimulatedInstance);	
	}
	
}
