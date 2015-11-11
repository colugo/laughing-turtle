package main.java.avii.diagnostics;

import java.util.ArrayList;
import java.util.Iterator;

import javax.imageio.metadata.IIOMetadataNode;

import org.w3c.dom.Node;

import main.java.avii.simulator.simulatedTypes.SimulatedEventInstance;
import main.java.avii.simulator.simulatedTypes.SimulatedInstanceIdentifier;


public class ProcedureDiagnostics implements Iterable<StatementExecutedDiagnosticsType> {
	ArrayList<StatementExecutedDiagnosticsType> _statements = new ArrayList<StatementExecutedDiagnosticsType>();
	int _statementPointer = 0;

	private StatementExecutedDiagnosticsType currentStatement()
	{
		return this._statements.get(this._statementPointer);
	}
	
	public void addStatement(StatementExecutedDiagnosticsType type) {
		this._statements.add(type);
	}
	
	public String getSyntaxString() {
		String syntaxString = currentStatement().getStatement().asString();
		syntaxString = syntaxString.replace("\n", "");
		return syntaxString;
	}

	public int getSyntaxLineNumber() {
		int syntaxLineNumber = currentStatement().getStatement().getLineNumber();
		return syntaxLineNumber;
	}

	public void prepareForIteration()
	{
		this._statementPointer = -1;
	}
	
	public boolean forward() {
		this._statementPointer++;
		return this._statementPointer < this._statements.size();
	}

	public void back() {
		this._statementPointer--;
	}

	@Override
	public Iterator<StatementExecutedDiagnosticsType> iterator() {
		return this._statements.iterator();
	}

	public boolean isEmpty() {
		return this._statements.isEmpty();
	}

	public ArrayList<SimulatedEventInstance> getWaitingEventsForInstance(SimulatedInstanceIdentifier id) {
		return currentStatement().getWaitingEventsForInstance(id);
	}

	public RelationshipDiagnostics getRelaionshipDiagnostics() {
		return currentStatement().getRelationshipDiagnostics();
	}

	public InstanceSetDiagnostics getInstanceDiagnostics() {
		return currentStatement().getInstanceDiagnostics();
	}

	public NamedInstanceAndVariableDiagnostics getNamedInstanceAndVariableDiagnostics() {
		return currentStatement().getNamedInstanceAndVariableDiagnostics();
	}

	public Node serialise() {
		IIOMetadataNode statement = new IIOMetadataNode("statement");
		statement.setAttribute("lineNumber", Integer.toString(this.getSyntaxLineNumber()));
		statement.setAttribute("text", this.getSyntaxString());
		statement.appendChild(getNamedInstanceAndVariableDiagnostics().serialise());
		statement.appendChild(getInstanceDiagnostics().serialise());
		statement.appendChild(getRelaionshipDiagnostics().serialise());
		statement.appendChild(currentStatement().getEventQueueDiagnostics().serialise());
		return statement;
	}
}
