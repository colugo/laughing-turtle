package main.java.avii.simulator;

import java.util.ArrayList;

import main.java.avii.simulator.simulatedTypes.simulatedActionLanguage.BaseSimulatedActionLanguage;
import main.java.avii.simulator.simulatedTypes.simulatedActionLanguage.IRelationAlteringSyntax;
import main.java.avii.simulator.simulatedTypes.simulatedActionLanguage.ISimulatedActionLanguage;

public class InstructionManager {
	
	private final static int INITIAL_PROGRAM_COUNTER_VALUE  = 1;
	
	private int _programCounter = INITIAL_PROGRAM_COUNTER_VALUE;
	private ArrayList<BaseSimulatedActionLanguage> _instructions = new ArrayList<BaseSimulatedActionLanguage>();
	
	public void pushSyntax(BaseSimulatedActionLanguage simulatedLanguage) {
		this._instructions.add(simulatedLanguage);
		int lineNumber = this._instructions.size();
		simulatedLanguage.setLineNumber(lineNumber);
	}

	public BaseSimulatedActionLanguage getSyntaxOnLine(int lineNumber) {
		return this._instructions.get(lineNumber - 1);
	}

	public int getProgramCounter() {
		return _programCounter;
	}

	public ISimulatedActionLanguage getCurrentSyntax() {
		int programCounter = this.getProgramCounter();
		ISimulatedActionLanguage currentSyntax = this.getSyntaxOnLine(programCounter);
		return currentSyntax;
	}

	public void simulateCurrentSyntax() {
		ISimulatedActionLanguage currentSyntax = this.getCurrentSyntax();
		currentSyntax.simulate();
		int nextInstructionAddress = this.calculateNextInstructionAddress();
		this._programCounter = nextInstructionAddress;
	}

	private int calculateNextInstructionAddress() {
		ISimulatedActionLanguage currentSyntax = this.getCurrentSyntax();
		return currentSyntax.getNextInstructionLineNumber();
	}

	public boolean hasMoreInstructions() {
		int noMoreInstructionsIndex = this._instructions.size() + 1;
		return this._programCounter < noMoreInstructionsIndex;
	}

	public boolean doesSyntaxAlterRelations() {
		for(BaseSimulatedActionLanguage actionLanguage : this._instructions)
		{
			if(actionLanguage instanceof IRelationAlteringSyntax)
			{
				return true;
			}
		}
		return false;
	}

}
