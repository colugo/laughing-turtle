package test.java.diagnosticsTests;

import junit.framework.Assert;
import junit.framework.TestCase;
import test.java.mock.MockSimulatedClass;
import main.java.avii.diagnostics.InstanceSetDiagnostics;
import main.java.avii.simulator.simulatedTypes.SimulatedInstance;

public class InstanceDiagnosticsTests extends TestCase {
	public InstanceDiagnosticsTests(String name)
	{
		super(name);
	}
	
	// DiagnosticInstance has already been unit tested
	
	public void test_can_add_instance_to_diagnostic_isntance_set()
	{
		
		MockSimulatedClass room = new MockSimulatedClass("Room");
		SimulatedInstance room_1 = room.createInstance();
		SimulatedInstance room_2 = room.createInstance();
		
		InstanceSetDiagnostics dis = new InstanceSetDiagnostics();
		Assert.assertEquals(false, dis.hasDiagnosticsForInstance(room_1.getIdentifier()));
		Assert.assertEquals(false, dis.hasDiagnosticsForInstance(room_2.getIdentifier()));
		dis.addInstance(room_1);
		Assert.assertEquals(true, dis.hasDiagnosticsForInstance(room_1.getIdentifier()));
	}
}
