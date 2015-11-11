package test.java.simulatorTests;

import test.java.mock.MockSimulator;
import test.java.helper.DomainTTD;
import junit.framework.Assert;
import junit.framework.TestCase;
import main.java.avii.editor.metamodel.entities.EntityClass;
import main.java.avii.editor.metamodel.entities.EntityDomain;
import main.java.avii.simulator.ISimulator;
import main.java.avii.simulator.simulatedTypes.SimulatedClass;
import main.java.avii.simulator.simulatedTypes.SimulatedInstance;
import main.java.avii.simulator.simulatedTypes.SimulatedInstanceSet;

public class SimulatedInstanceSetTests extends TestCase {

	public SimulatedInstanceSetTests(String name) {
		super(name);
	}
	
	public void test_can_create_new_instance_set_based_on_simulated_class_and_can_report_size()
	{
		SimulatedInstanceSet simulatedInstanceSet = new SimulatedInstanceSet();
		Assert.assertEquals(0, simulatedInstanceSet.size());
	}
	
	public void test_can_add_instance_to_instance_set_and_report_size()
	{
		EntityDomain ttdDomain = DomainTTD.getTTDDomain();
		EntityClass user = ttdDomain.getEntityClassWithName("User");
		ISimulator mock = new MockSimulator();
		SimulatedClass simulatedUser = new SimulatedClass(user, mock);
		SimulatedInstance simulatedInstance = simulatedUser.createInstance();
		
		SimulatedInstanceSet simulatedInstanceSet = new SimulatedInstanceSet();
		simulatedInstanceSet.addInstance(simulatedInstance);
		
		Assert.assertEquals(1, simulatedInstanceSet.size());
	}
	
	public void test_cant_add_identical_instance_to_instance_set()
	{
		EntityDomain ttdDomain = DomainTTD.getTTDDomain();
		EntityClass user = ttdDomain.getEntityClassWithName("User");
		ISimulator mock = new MockSimulator();
		SimulatedClass simulatedUser = new SimulatedClass(user, mock);
		SimulatedInstance simulatedInstance = simulatedUser.createInstance();
		
		SimulatedInstanceSet simulatedInstanceSet = new SimulatedInstanceSet();
		simulatedInstanceSet.addInstance(simulatedInstance);
		simulatedInstanceSet.addInstance(simulatedInstance);
		
		Assert.assertEquals(1, simulatedInstanceSet.size());
	}

	public void test_can_get_instance_at_index()
	{
		EntityDomain ttdDomain = DomainTTD.getTTDDomain();
		EntityClass user = ttdDomain.getEntityClassWithName("User");
		ISimulator mock = new MockSimulator();
		SimulatedClass simulatedUser = new SimulatedClass(user, mock);
		SimulatedInstance simulatedInstance = simulatedUser.createInstance();
		SimulatedInstance simulatedInstance1 = simulatedUser.createInstance();
		
		SimulatedInstanceSet simulatedInstanceSet = new SimulatedInstanceSet();
		simulatedInstanceSet.addInstance(simulatedInstance);
		simulatedInstanceSet.addInstance(simulatedInstance1);
		
		Assert.assertEquals(2, simulatedInstanceSet.size());
		Assert.assertEquals(simulatedInstance, simulatedInstanceSet.getInstanceAt(0));
		Assert.assertEquals(simulatedInstance1, simulatedInstanceSet.getInstanceAt(1));
	}

}

