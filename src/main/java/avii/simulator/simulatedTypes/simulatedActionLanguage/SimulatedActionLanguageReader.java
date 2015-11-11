package main.java.avii.simulator.simulatedTypes.simulatedActionLanguage;

import java.io.InputStream;

import main.java.avii.editor.contracts.metamodel.actionLanguage.syntax.IActionLanguageSyntax;
import main.java.avii.editor.metamodel.actionLanguage.reader.ActionLanguageReader;
import main.java.avii.simulator.ISimulator;
import main.java.avii.simulator.InstructionManager;

public class SimulatedActionLanguageReader extends ActionLanguageReader {

	private ISimulator _simulator;
	private InstructionManager _instructionManager;
	
	public SimulatedActionLanguageReader(InputStream inputStream, ISimulator simulator, InstructionManager instructionManager) {
		super(inputStream);
		this._simulator = simulator;
		this._instructionManager = instructionManager;
	}
	
	protected void foundSyntaxLine(IActionLanguageSyntax lineSyntax, int lineNumber, int fileOffset, String line) {
		try {
			BaseSimulatedActionLanguage simulatedLanguage = SimulatedSyntaxFactory.getSimulatedSyntax(this._simulator, lineSyntax);
			this._instructionManager.pushSyntax(simulatedLanguage);
		} catch (NoMatchingSimulatedActionLanguageException e) {
			e.printStackTrace();
		}
		
	}

}
