package test.java.diagnosticsTests;

import java.util.ArrayList;
import java.util.Collection;

import junit.framework.Assert;
import junit.framework.TestCase;
import test.java.mock.MockSimulatedEventInstance;
import main.java.avii.diagnostics.EventQueueDiagnostics;
import main.java.avii.simulator.simulatedTypes.SimulatedEventInstance;
import main.java.avii.simulator.simulatedTypes.SimulatedInstanceIdentifier;

public class EventQueueDiagnosticsTests extends TestCase {
	public EventQueueDiagnosticsTests(String name) {
		super(name);
	}
	
	public void test_event_queue_diagnostics_is_initialised_by_collection_of_simulated_event_instances()
	{
		Collection<SimulatedEventInstance> events = new ArrayList<SimulatedEventInstance>();
		MockSimulatedEventInstance mockEventInstance1 = new MockSimulatedEventInstance();
		
		SimulatedInstanceIdentifier id1 = new SimulatedInstanceIdentifier(0, null);
		SimulatedInstanceIdentifier id2 = new SimulatedInstanceIdentifier(0, null);
		
		mockEventInstance1.setDestination(id1);
		events.add(mockEventInstance1);
		
		EventQueueDiagnostics eqd = new EventQueueDiagnostics(events);
		
		Assert.assertEquals(true, eqd.containsEventsFor(id1));
		Assert.assertEquals(false, eqd.containsEventsFor(id2));
	}
	
}
