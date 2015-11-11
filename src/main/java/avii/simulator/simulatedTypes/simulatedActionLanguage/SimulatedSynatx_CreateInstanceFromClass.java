package main.java.avii.simulator.simulatedTypes.simulatedActionLanguage;

import main.java.avii.editor.contracts.metamodel.actionLanguage.syntax.IActionLanguageSyntax;
import main.java.avii.editor.metamodel.actionLanguage.syntax.Syntax_CreateInstanceFromClass;
import main.java.avii.simulator.Simulator;
import main.java.avii.simulator.simulatedTypes.SimulatedClass;
import main.java.avii.simulator.simulatedTypes.SimulatedInstance;

public class SimulatedSynatx_CreateInstanceFromClass extends BaseSimulatedActionLanguage {

	private Syntax_CreateInstanceFromClass _createSyntax = null;
	private SimulatedInstance _simulatedInstance;
	
	public SimulatedSynatx_CreateInstanceFromClass(Simulator theSimulator, IActionLanguageSyntax theConcreteSyntax) {
		super(theSimulator, theConcreteSyntax);
		this._createSyntax = (Syntax_CreateInstanceFromClass) this._concreteSyntax;
	}

	public SimulatedSynatx_CreateInstanceFromClass() {
	}

	public SimulatedInstance getCreatedInstance() {
		return this._simulatedInstance;
	}
	public String getCreatedInstanceName() {
		return this._createSyntax.get_Instance();
	}

	@Override
	public void simulate() {
		String classNameToCreate = this._createSyntax.get_Class();
		SimulatedClass theSimulatedClass = this._simulator.getSimulatedClass(classNameToCreate);
		this._simulatedInstance = theSimulatedClass.createInstance();
		this._simulator.getSimulatingState().registerInstance(this.getCreatedInstanceName(), this._simulatedInstance);
	}

}
