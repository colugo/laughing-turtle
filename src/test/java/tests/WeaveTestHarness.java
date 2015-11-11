package test.java.tests;

import main.java.avii.editor.metamodel.actionLanguage.validation.EntityDomainValidator;
import main.java.avii.editor.metamodel.entities.EntityDomain;
import main.java.avii.editor.metamodel.weaving.DomainWeave;
import main.java.avii.simulator.ISimulator;
import main.java.avii.simulator.Simulator;
import main.java.avii.simulator.exceptions.CannotSimulateDomainThatIsInvalidException;
import main.java.avii.simulator.simulatedTypes.SimulatedTestVector;
import main.java.avii.simulator.weave.WeaveSimulator;


public class WeaveTestHarness extends TestHarness {

	private DomainWeave _domainWeave;

	public WeaveTestHarness(DomainWeave domainWeave) throws CannotSimulateDomainThatIsInvalidException {
		this._domainWeave = domainWeave;
		for(EntityDomain domain : domainWeave.getWovenDomains())
		{
			EntityDomainValidator validator = new EntityDomainValidator(domain);
			if (!validator.validate()) {
				throw new CannotSimulateDomainThatIsInvalidException(validator);
			}
			this.createSimulatedScenariosForDomain(domain);
		}
	}
	
	protected ISimulator createSimulatorForExecution(SimulatedTestVector vector) throws CannotSimulateDomainThatIsInvalidException {
		WeaveSimulator weaveSimulator = new WeaveSimulator(this._domainWeave);
		EntityDomain domain = vector.getVector().getScenario().getDomain();
		weaveSimulator.setCurrentDomain(domain);
		
		Simulator simulator = weaveSimulator.getSimulatorForDomain(domain);
		
		return simulator;
	}

	
}
