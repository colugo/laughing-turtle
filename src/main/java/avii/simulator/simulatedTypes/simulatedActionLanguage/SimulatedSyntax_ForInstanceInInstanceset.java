package main.java.avii.simulator.simulatedTypes.simulatedActionLanguage;

import main.java.avii.editor.contracts.metamodel.actionLanguage.syntax.IActionLanguageSyntax;
import main.java.avii.editor.metamodel.actionLanguage.syntax.Syntax_ForInstanceInInstanceset;
import main.java.avii.simulator.Simulator;
import main.java.avii.simulator.simulatedTypes.SimulatedInstance;
import main.java.avii.simulator.simulatedTypes.SimulatedInstanceSet;

public class SimulatedSyntax_ForInstanceInInstanceset extends BaseSimulatedActionLanguage {

	private Syntax_ForInstanceInInstanceset _syntax;
	private int _index = -1;
	boolean _noMoreInstances = false;
	
	public SimulatedSyntax_ForInstanceInInstanceset(Simulator theSimulator, IActionLanguageSyntax theConcreteSyntax) {
		super(theSimulator, theConcreteSyntax);
		this._syntax = (Syntax_ForInstanceInInstanceset) theConcreteSyntax;
	}

	public SimulatedSyntax_ForInstanceInInstanceset() {
	}

	@Override
	public void simulate() {
		String instanceName = this._syntax.get_Instance();
		String instanceSetName = this._syntax.get_Instanceset();
		
		SimulatedInstanceSet instanceSet = this._simulator.getSimulatingState().getInstanceSetWithName(instanceSetName);
		
		this._index++;
		if(this._index < instanceSet.size())
		{
			instanceSet.setIterating(true);
			instanceSet.setCurrentInstanceIndex(this._index);
			SimulatedInstance currentInstance = instanceSet.getInstanceAt(this._index); 
			this._simulator.getSimulatingState().registerInstance(instanceName,currentInstance);
		}
		else
		{
			instanceSet.setIterating(false);
			this._noMoreInstances = true;
			this._simulator.getSimulatingState().deRegisterInstance(instanceName);
		}
	}
	
	public int getNextInstructionLineNumber()
	{
		if(this._noMoreInstances)
		{
			int closeForBlock = this._simulator.getSimulatingState().getProcedure().closingBlockLineNumber(this._lineNumber);
			// end for statements redirect to this for instance
			// when the loop is over, it jumps AFTER the else statement
			// we want to jump over the end for statement, so add 1 to the next line number
			closeForBlock++;
			return closeForBlock;
		}
		return this._lineNumber + 1;
	}

}
