package main.java.avii.simulator.simulatedTypes.simulatedActionLanguage;

import main.java.avii.editor.contracts.metamodel.actionLanguage.syntax.IActionLanguageSyntax;
import main.java.avii.editor.metamodel.actionLanguage.syntax.Syntax_GenerateEventParamsToInstance;
import main.java.avii.simulator.Simulator;
import main.java.avii.simulator.events.SimulatedEvent;
import main.java.avii.simulator.simulatedTypes.SimulatedClass;
import main.java.avii.simulator.simulatedTypes.SimulatedEventInstance;
import main.java.avii.simulator.simulatedTypes.SimulatedInstance;

public class SimulatedSyntax_GenerateEventParamsToInstance extends BaseSimulatedGenerateActionLanguage {

	private Syntax_GenerateEventParamsToInstance _generateSyntax;

	public SimulatedSyntax_GenerateEventParamsToInstance(Simulator theSimulator, IActionLanguageSyntax theConcreteSyntax) {
		super(theSimulator, theConcreteSyntax);
		this._generateSyntax = (Syntax_GenerateEventParamsToInstance) theConcreteSyntax;
	}

	public SimulatedSyntax_GenerateEventParamsToInstance() {
	}

	@Override
	public void simulate() {
		SimulatedInstance self = null;
		self = this._simulator.getSimulatingInstance();
		
		String destinationName = this._generateSyntax.get_Instance();
		String eventName = this._generateSyntax.eventName();
		
		SimulatedInstance instance = this.getInstanceWithName(destinationName);
		
		SimulatedClass destinationClass = instance.getSimulatedClass();
		SimulatedEvent eventSpec = destinationClass.getEventWithName(eventName);
			
		SimulatedEventInstance eventInstance = new SimulatedEventInstance(eventSpec, self.getIdentifier(), instance.getIdentifier());
		eventInstance.setName(eventName);
		
		this.addParamsToEventInstance(this._generateSyntax.getParams(), eventInstance);
		
		this._simulator.registerEventInstance(eventInstance);
	}

}
