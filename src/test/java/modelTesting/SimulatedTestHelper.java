package test.java.modelTesting;

import main.java.avii.simulator.simulatedTypes.SimulatedTestVector;
import test.java.tests.SimulatedTestScenario;
import test.java.tests.TestHarness;

public class SimulatedTestHelper {

	public static void printHarnessResults(TestHarness harness) {
		if(harness.allAssertionsPassed() && !harness.wereExceptionsRaised())
		{
			return;
		}
		
		for(SimulatedTestScenario scenario : harness.getScenarios())
		{
			if(scenario.allAssertionsPassed() && !scenario.wereExceptionsRaised())
			{
				continue;
			}
			System.out.println("SCENARIO : " + scenario.getScenario().getName());
			for(SimulatedTestVector vector : scenario.getSimulatedVectors())
			{
				if(vector.hasExceptionBeenRaised())
				{
					System.out.println("\tVECTOR : " + vector.getVector().getDescription());
					System.out.println("\t\t Exception : " + vector.getException().getMessage());
					continue;
				}
				if(vector.hasAssertionFailed())
				{
					System.out.println("\tVECTOR : " + vector.getVector().getDescription());
					System.out.println("\t\t ASSERTION : " + vector.getAssertionFail().toString());
				}
				
			}
		}
	}

}
