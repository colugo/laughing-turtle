package main.java.avii.simulator.simulatedTypes.simulatedActionLanguage;

import java.util.Collection;

import main.java.avii.editor.contracts.metamodel.actionLanguage.syntax.IActionLanguageSyntax;
import main.java.avii.editor.metamodel.actionLanguage.syntax.Syntax_ReclassifyInstanceToClass;
import main.java.avii.simulator.Simulator;
import main.java.avii.simulator.simulatedTypes.SimulatedHierarchyClass;
import main.java.avii.simulator.simulatedTypes.SimulatedHierarchyInstance;
import main.java.avii.simulator.simulatedTypes.SimulatedInstanceIdentifier;
import main.java.avii.simulator.simulatedTypes.SimulatedState;

public class SimulatedSyntax_ReclassifyToClass extends BaseSimulatedActionLanguage implements IRelationAlteringSyntax {

	private Syntax_ReclassifyInstanceToClass _specificSyntax;

	public SimulatedSyntax_ReclassifyToClass(Simulator theSimulator, IActionLanguageSyntax theConcreteSyntax) {
		super(theSimulator, theConcreteSyntax);
		this._specificSyntax = (Syntax_ReclassifyInstanceToClass) theConcreteSyntax;
	}

	public SimulatedSyntax_ReclassifyToClass() {
	}

	@Override
	public void simulate() {
		SimulatedState simulatingState = this._simulator.getSimulatingState();
		String classNameToReclassifyTo = this._specificSyntax.get_Class();
		SimulatedHierarchyClass classToReclassifyTo = (SimulatedHierarchyClass) this._simulator.getSimulatedClass(classNameToReclassifyTo);
		
		SimulatedHierarchyInstance self = (SimulatedHierarchyInstance) this.getInstanceWithName("self");
		Collection<SimulatedInstanceIdentifier> previousSelfHierarchyIdentifiers = self.getHierarchyIdentifiers();
				
		SimulatedHierarchyInstance newSelf = (self).reclassifyTo(classToReclassifyTo);
		simulatingState.registerInstance("self", newSelf);
		
		// redirect events to the new instance
		this._simulator.getEventQueue().redirectHierarchyEvents(previousSelfHierarchyIdentifiers, newSelf.getRootInstance().getIdentifier());
	}

}
