package main.java.avii.simulator.simulatedTypes.simulatedActionLanguage;

import javax.naming.NameNotFoundException;

import main.java.avii.editor.contracts.metamodel.actionLanguage.syntax.IActionLanguageSyntax;
import main.java.avii.editor.metamodel.actionLanguage.syntax.Syntax_IfEmptyInstancesetThen;
import main.java.avii.simulator.Simulator;
import main.java.avii.simulator.simulatedTypes.NullSimulatedInstance;
import main.java.avii.simulator.simulatedTypes.SimulatedInstance;
import main.java.avii.simulator.simulatedTypes.SimulatedInstanceSet;

public class SimulatedSyntax_IfEmptyInstancesetThen extends BaseSimulatedActionLanguage {

	private Syntax_IfEmptyInstancesetThen _specificSyntax;
	private boolean _wasIfTrue;

	public SimulatedSyntax_IfEmptyInstancesetThen(Simulator theSimulator, IActionLanguageSyntax theConcreteSyntax) {
		super(theSimulator, theConcreteSyntax);
		this._specificSyntax = (Syntax_IfEmptyInstancesetThen)theConcreteSyntax;
	}

	public SimulatedSyntax_IfEmptyInstancesetThen() {
	}

	@Override
	public void simulate() {
		try
		{
			String instanceName = this._specificSyntax.get_Instance();
			SimulatedInstance instance = this._simulator.getSimulatingState().getInstanceWithName(instanceName);
			if(instance instanceof NullSimulatedInstance)
			{
				this._wasIfTrue = true;
			}
			else
			{
				this._wasIfTrue = false;
			}
		}
		catch(NameNotFoundException nfne)
		{
			String instanceSetName = this._specificSyntax.get_Instance();
			SimulatedInstanceSet instanceSet = this._simulator.getSimulatingState().getInstanceSetWithName(instanceSetName);
			if(instanceSet.size() == 0)
			{
				this._wasIfTrue = true;
			}
			else
			{
				this._wasIfTrue = false;
			}
		}
	}

	public int getNextInstructionLineNumber()
	{
		if(this._wasIfTrue)
		{
			return this._lineNumber + 1;	
		}
		int closeIfBlock = this._simulator.getSimulatingState().getProcedure().closingBlockLineNumber(this._lineNumber);
		// else statements are only executed when the top body of the if statement has ended.
		// when the if statement is false, it jumps AFTER the else statement
		// but before the end if
		return closeIfBlock;
	}

}
