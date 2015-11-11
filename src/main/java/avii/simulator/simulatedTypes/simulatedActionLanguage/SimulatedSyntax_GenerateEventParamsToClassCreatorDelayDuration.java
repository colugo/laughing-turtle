package main.java.avii.simulator.simulatedTypes.simulatedActionLanguage;

import main.java.avii.editor.contracts.metamodel.actionLanguage.syntax.IActionLanguageSyntax;
import main.java.avii.editor.metamodel.actionLanguage.syntax.Syntax_GenerateEventParamsToClassCreatorDelayDuration;
import main.java.avii.simulator.Simulator;
import main.java.avii.simulator.events.SimulatedEvent;
import main.java.avii.simulator.simulatedTypes.SimulatedClass;
import main.java.avii.simulator.simulatedTypes.SimulatedEventInstance;
import main.java.avii.simulator.simulatedTypes.SimulatedInstance;

public class SimulatedSyntax_GenerateEventParamsToClassCreatorDelayDuration extends BaseSimulatedGenerateActionLanguage {

	private Syntax_GenerateEventParamsToClassCreatorDelayDuration _generateSyntax;

	public SimulatedSyntax_GenerateEventParamsToClassCreatorDelayDuration(Simulator theSimulator, IActionLanguageSyntax theConcreteSyntax) {
		super(theSimulator, theConcreteSyntax);
		this._generateSyntax = (Syntax_GenerateEventParamsToClassCreatorDelayDuration) theConcreteSyntax;
	}

	public SimulatedSyntax_GenerateEventParamsToClassCreatorDelayDuration() {
	}

	@Override
	public void simulate() {
		SimulatedInstance self = null;
		self = this._simulator.getSimulatingInstance();
				
		String className = this._generateSyntax.get_Class();
		String eventName = this._generateSyntax.eventName();
		SimulatedClass selfClass = self.getSimulatedClass();
		SimulatedEvent eventSpec = selfClass.getEventWithName(eventName);
		
		SimulatedClass creatorClass = this._simulator.getSimulatedClass(className);
		SimulatedInstance instance = creatorClass.createInstance();
		
		SimulatedEventInstance eventInstance = new SimulatedEventInstance(eventSpec, self.getIdentifier(), instance.getIdentifier());
		eventInstance.setName(eventName);
		
		this.addParamsToEventInstance(this._generateSyntax.getParams(), eventInstance);
		
		eventInstance.setDelay(Integer.parseInt(this._generateSyntax.getDelayQuantity()), this._generateSyntax.getDelayUnit());
		
		this._simulator.registerEventInstance(eventInstance);
	}

}
