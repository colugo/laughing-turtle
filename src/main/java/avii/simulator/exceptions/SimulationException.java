package main.java.avii.simulator.exceptions;

@SuppressWarnings("serial")
public abstract class SimulationException extends BaseException {

	private int _lineNumber;
	private String _currentSyntax;

	public int getLineNumber() {
		return this._lineNumber;
	}

	public String getActionLanguage() {
		return this._currentSyntax;
	}

	public void setLineNumber(int lineNumber) {
		this._lineNumber = lineNumber;
	}

	public void setSyntax(String currentSyntax) {
		this._currentSyntax = currentSyntax;		
	}

}
