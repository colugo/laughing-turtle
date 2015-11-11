package main.java.avii.diagnostics;

import java.util.ArrayList;

import javax.imageio.metadata.IIOMetadataNode;
import javax.naming.NameNotFoundException;

import main.java.avii.editor.metamodel.entities.EntityEventParam;
import main.java.avii.editor.metamodel.entities.datatypes.StringEntityDatatype;
import main.java.avii.simulator.simulatedTypes.SimulatedEventInstance;
import main.java.avii.simulator.simulatedTypes.SimulatedInstance;
import main.java.avii.simulator.simulatedTypes.SimulatedInstanceIdentifier;
import main.java.avii.util.Text;

public class EventConsumedInstanceLifecycleStage extends BaseInstanceLifecycleStage {

	private SimulatedEventInstance _event;
	private boolean _wasIgnored = false;
	private EventQueueDiagnostics _eventDiagnostics;
	private RelationshipDiagnostics _relationshipDiagnostics;
	private InstanceSetDiagnostics _instanceDiagnostics;

	public EventConsumedInstanceLifecycleStage(SimulatedEventInstance event, SimulatedInstance instance) {
		super(instance);
		this._event = event;
	}
	
	protected EventConsumedInstanceLifecycleStage(SimulatedInstance instance)
	{
		super(instance);
	}
	
	public boolean wasIgnored()
	{
		return this._wasIgnored ;
	}

	public String getTriggeringEventNameAndParams() {
		return this._event.describe();
	}

	public void ignore() {
		this._wasIgnored = true;		
	}
	
	@Override
	public String getStageLog() {
		String instanceDescription = this._instance.toString() + " : ";
		String eventName = this.getTriggeringEventNameAndParams();
		String currentStateName = this.getStateName();
		if(wasIgnored())
		{
			return instanceDescription + eventName + " was ignored";
		}
		return instanceDescription + eventName + " -> " + currentStateName;
	}

	@Override
	public String toString()
	{
		String output = getStageLog();
		if(!this._procedureDiagnostics.isEmpty())
		{
			output += "\n";
		}
		for(StatementExecutedDiagnosticsType type : this._procedureDiagnostics)
		{
			output += "\t" + type + "\n";
		}
		return output;
	}
	
	public void setEventDiagnostics(EventQueueDiagnostics eventQueueDiagnostics) {
		this._eventDiagnostics = eventQueueDiagnostics;		
	}
	
	public EventQueueDiagnostics getEventDiagnostics()
	{
		return this._eventDiagnostics;
	}

	@Override
	public boolean hasWaitingEvents()
	{
		boolean areEventsWaiting = this._eventDiagnostics.containsEventsFor(this._instance.getIdentifier());
		return areEventsWaiting;
	}
	
	@Override
	public ArrayList<SimulatedEventInstance> getWaitingEventsForInstance(SimulatedInstanceIdentifier id) {
		return this._eventDiagnostics.getEventsForInstance(id);
	}
	
	@Override
	public IIOMetadataNode serialise(int sequence) {
		IIOMetadataNode node = this.createBaseNode("Event consumed", sequence);
		node.setAttribute("instanceId", this._instance.getIdentifier().toString());
		node.setAttribute("event", this._event.getSpecification().getConcreteEvent().getName());
		node.setAttribute("ignored", Boolean.toString(this.wasIgnored()));
		
		IIOMetadataNode eventParams = new IIOMetadataNode("parameters");
		
		for(String paramName : this._event.getParamNames())
		{
			String paramValue = this._event.getParam(paramName).toString();
			try {
				EntityEventParam eventParam = this._event.getSpecification().getConcreteEvent().getParamWithName(paramName);
				if(eventParam.getType() instanceof StringEntityDatatype)
				{
					paramValue = Text.quote(paramValue);
				}
				IIOMetadataNode paramNode = new IIOMetadataNode("parameter");
				paramNode.setAttribute("name", "rcvd_event."+paramName);
				paramNode.setAttribute("value", paramValue);
				eventParams.appendChild(paramNode);
			} catch (NameNotFoundException e) {
			}
		}
		
		node.appendChild(eventParams);
		
		IIOMetadataNode entry = new IIOMetadataNode("entry");
		
		if(this._instanceDiagnostics != null)
		{
			entry.appendChild(this._instanceDiagnostics.serialise());
		}
		if(this._relationshipDiagnostics != null)
		{
			entry.appendChild(this._relationshipDiagnostics.serialise());
		}
		if(this._eventDiagnostics != null)
		{
			entry.appendChild(this._eventDiagnostics.serialise());
		}
		
		node.appendChild(entry);
		
		this._procedureDiagnostics.prepareForIteration();
		while(this._procedureDiagnostics.forward())
		{
			node.appendChild(this._procedureDiagnostics.serialise());
		}
		
		return node;
	}

	public void setRelationshipDiagnostics(RelationshipDiagnostics relationshipDiagnostics) {
		this._relationshipDiagnostics = relationshipDiagnostics;		
	}

	public void setInstanceDiagnostics(InstanceSetDiagnostics diagnosticInstanceSet) {
		this._instanceDiagnostics = diagnosticInstanceSet;
	}
	
}
