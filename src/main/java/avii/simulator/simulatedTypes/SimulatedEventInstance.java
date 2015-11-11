package main.java.avii.simulator.simulatedTypes;

import java.util.Collection;
import java.util.HashMap;
import java.util.UUID;

import javax.imageio.metadata.IIOMetadataNode;
import javax.naming.NameNotFoundException;

import org.w3c.dom.Node;

import main.java.avii.editor.contracts.metamodel.actionLanguage.syntax.IGenerateDelayActionLanguageSyntax.DelayUnits;
import main.java.avii.editor.metamodel.actionLanguage.DelayToMillisecondsConverter;
import main.java.avii.editor.metamodel.entities.EntityEventParam;
import main.java.avii.editor.metamodel.entities.datatypes.StringEntityDatatype;
import main.java.avii.simulator.events.SimulatedEvent;
import main.java.avii.simulator.events.SimulatedEventParam;
import main.java.avii.simulator.events.SimulatedEventTimeHelper;
import main.java.avii.util.Text;

public class SimulatedEventInstance {

	private long _goTime = -1;
	private SimulatedInstanceIdentifier _sender;
	private SimulatedInstanceIdentifier _destination;
	private String _name;
	private long _delayQuantity = 0;
	private DelayUnits _delayUnits = null;
	private UUID _uuid = UUID.randomUUID();
	private SimulatedEvent _specification;
	private HashMap<String, Object> _params = new HashMap<String,Object>();

	
	protected SimulatedEventInstance(){}
	
	public boolean isSameEventForCancellation(SimulatedEventInstance other)
	{
		boolean isSame = true;
		
		if(!this._name.equals(other._name))
		{
			isSame = false;
		}
		
		if(!this._sender.equals(other._sender))
		{
			isSame = false;
		}
		
		if(!this._destination.equals(other._destination))
		{
			isSame = false;
		}
		
		return isSame;
	}
	
	@Override
	public boolean equals(Object other) {
		if (other instanceof SimulatedEventInstance) {
			if (((SimulatedEventInstance) other)._uuid.toString().equals(this._uuid.toString())) {
				return true;
			}
		}
		return false;
	}

	public SimulatedEventInstance(SimulatedInstanceIdentifier sender, SimulatedInstanceIdentifier destination) {
		initialiseSimulatedEvent(sender, destination);
	}

	public void initialiseSimulatedEvent(SimulatedInstanceIdentifier sender, SimulatedInstanceIdentifier destination) {
		this._goTime = SimulatedEventTimeHelper.getCurrentTimeHelper();
		this._sender = sender;
		this._destination = destination;
	}
	
	private void createParams()
	{
		for(SimulatedEventParam param : this._specification.getParams())
		{
			String name = param.getName();
			Object defaultValue = param.getType().getDefaultValueAsObject();
			this._params.put(name,defaultValue);
		}
	}

	public SimulatedEventInstance(SimulatedEvent eventSpecification, SimulatedInstanceIdentifier sender, SimulatedInstanceIdentifier destination) {
		initialiseSimulatedEvent(sender, destination);
		this._specification = eventSpecification;
		this.createParams();
	}

	public long getGoTime() {
		return this._goTime;
	}

	public boolean isToSelf() {
		// reference comparisson is enough here - I want to know if the sender
		// is the EXACT same in memory object as the destination
		return this._sender == this._destination;
	}

	public void setDelay(int delayQuantity, DelayUnits delayUnit) {

		this._delayQuantity = delayQuantity;
		this._delayUnits = delayUnit;

		long delayOffset = DelayToMillisecondsConverter.convertDelayIntoMilliseconds(delayQuantity, delayUnit);
		long newDelay = this._goTime + delayOffset;
		this._goTime = newDelay;
	}

	public SimulatedInstanceIdentifier getDestination() {
		return this._destination;
	}

	public void setName(String theName) {
		this._name = theName;
	}

	@Override
	public String toString() {
		String toString = this._name;

		if (this._delayUnits != null) {
			toString += ":" + this._delayQuantity + "" + this._delayUnits.toString();
		}

		return toString;
	}

	public SimulatedEvent getSpecification() {
		return this._specification;
	}

	public Object getParam(String paramName) {
		return this._params.get(paramName);
	}

	public void setParam(String paramName, Object paramValue) {
		Object correctValue = this._specification.convertNewAttributeValueToCorrectDatatype(paramName, paramValue);
		this._params.put(paramName, correctValue);
	}

	public void setDestination(SimulatedInstanceIdentifier destination) {
		this._destination = destination;		
	}

	public void setSender(SimulatedInstanceIdentifier sender) {
		this._sender = sender;		
	}

	public SimulatedInstanceIdentifier getSender() {
		return this._sender;
	}

	public boolean hasDelay() {
		boolean isDelayEmpty = this._delayUnits == null && this._delayQuantity == 0;
		return !isDelayEmpty;
	}

	public Collection<String> getParamNames() {
		return this._params.keySet();
	}

	public String describe() {
		String eventName = this.getSpecification().getConcreteEvent().getName();
		String params = "";
		for(String paramName : this.getParamNames())
		{
			String paramValue = this.getParam(paramName).toString();
			try {
				EntityEventParam eventParam = this.getSpecification().getConcreteEvent().getParamWithName(paramName);
				if(eventParam.getType() instanceof StringEntityDatatype)
				{
					paramValue = Text.quote(paramValue);
				}
			} catch (NameNotFoundException e) {
			}
			params += paramName + "=" + paramValue + ", ";
		}
		if(params != "")
		{
			params = params.substring(0, params.length() - 2);
		}
		String output = eventName + '(' + params + ')';
		return output;
	}

	public Node serialise() {
		IIOMetadataNode eventNode = new IIOMetadataNode("event");
		eventNode.setAttribute("name", this.getSpecification().getConcreteEvent().getName());
		eventNode.setAttribute("senderId", this._sender.toString());
		eventNode.setAttribute("toSelf", Boolean.toString(this.isToSelf()));
		
		for(String paramName : this.getParamNames())
		{
			String paramValue = this.getParam(paramName).toString();
			
			IIOMetadataNode param = new IIOMetadataNode("parameter");
			param.setAttribute("name", paramName);
			param.setAttribute("value", paramValue);
			eventNode.appendChild(param);
		}
		
		return eventNode;
	}

}
