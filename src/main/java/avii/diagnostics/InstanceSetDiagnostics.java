package main.java.avii.diagnostics;

import java.util.ArrayList;
import java.util.HashMap;

import javax.imageio.metadata.IIOMetadataNode;

import org.w3c.dom.Node;

import main.java.avii.editor.metamodel.entities.EntityState;
import main.java.avii.simulator.simulatedTypes.SimulatedClass;
import main.java.avii.simulator.simulatedTypes.SimulatedInstance;
import main.java.avii.simulator.simulatedTypes.SimulatedInstanceIdentifier;

public class InstanceSetDiagnostics {
	private HashMap<SimulatedInstanceIdentifier, DiagnosticInstance> _instances = new HashMap<SimulatedInstanceIdentifier, DiagnosticInstance>();

	public boolean hasDiagnosticsForInstance(SimulatedInstanceIdentifier identifier) {
		boolean hasInstance = this._instances.containsKey(identifier);
		return hasInstance;
	}

	public void addInstance(SimulatedInstance instance) {
		DiagnosticInstance diagnosticInstance = new DiagnosticInstance(instance);
		this._instances.put(instance.getIdentifier(), diagnosticInstance);
	}

	public DiagnosticInstance getDiagnosticsForInstance(SimulatedInstanceIdentifier identifier) {
		return this._instances.get(identifier);
	}

	public Node serialise() {
		IIOMetadataNode instancesNode = new IIOMetadataNode("instances");
		HashMap<SimulatedClass, ArrayList<SimulatedInstanceIdentifier>> classes = new HashMap<SimulatedClass, ArrayList<SimulatedInstanceIdentifier>>();
		for(SimulatedInstanceIdentifier identifier : this._instances.keySet())
		{
			SimulatedClass simulatedClass = identifier.getSimulatedClass();
			if(!classes.containsKey(simulatedClass))
			{
				classes.put(simulatedClass, new ArrayList<SimulatedInstanceIdentifier>());
			}
			classes.get(simulatedClass).add(identifier);
		}

		for(SimulatedClass simulatedClass : classes.keySet())
		{
			IIOMetadataNode classNode = new IIOMetadataNode("class");
			classNode.setAttribute("name", simulatedClass.getName());
			
			for(SimulatedInstanceIdentifier identifier : classes.get(simulatedClass))
			{
				classNode.appendChild(this._instances.get(identifier).serialise());
			}
			
			instancesNode.appendChild(classNode);
		}
		
		return instancesNode;
	}

	public void changeStateOfInstance(SimulatedInstance instance, EntityState nextState) {
		SimulatedInstanceIdentifier id = instance.getIdentifier();
		DiagnosticInstance diagnosticInstance = this._instances.get(id);
		diagnosticInstance.setStateName(nextState.getName());
	}

}
