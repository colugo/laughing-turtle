package main.java.models;

import java.util.ArrayList;
import java.util.Map;

import org.simpleframework.xml.Attribute;
import org.simpleframework.xml.Element;
import org.simpleframework.xml.ElementList;

@Element
public class JsonClassDiagramCoordinatesHelperEntityClass {

	@Attribute
	public String uuid = "";
	@Attribute
	public double x = 0.0;
	@Attribute
	public double y = 0.0;
	@Attribute
	public int superClassTriangleIndex;
	@ElementList(required=false)
	public ArrayList<JsonClassDiagramCoordinatesHelperEntityState> states = new ArrayList<JsonClassDiagramCoordinatesHelperEntityState>();
	@ElementList(required=false)
	public ArrayList<JsonClassDiagramCoordinatesHelperEventInstance> instances = new ArrayList<JsonClassDiagramCoordinatesHelperEventInstance>();
	
	public JsonClassDiagramCoordinatesHelperEntityClass(@Attribute(name="uuid")String uuid, @Attribute(name="x")double x, @Attribute(name="y")double y, @Attribute(name="superClassTriangleIndex")int superClassTriangleIndex){
		this.uuid = uuid;
		this.x = x;
		this.y = y;
		this.superClassTriangleIndex = superClassTriangleIndex;
	}
	
	public JsonClassDiagramCoordinatesHelperEntityClass(@Attribute(name="uuid")String uuid, @Attribute(name="x")double x, @Attribute(name="y")double y, @Attribute(name="superClassTriangleIndex")int superClassTriangleIndex, @ElementList(name="states")ArrayList<JsonClassDiagramCoordinatesHelperEntityState> states){
		this.uuid = uuid;
		this.x = x;
		this.y = y;
		this.superClassTriangleIndex = superClassTriangleIndex;
		this.states = states;
	}
	
	public JsonClassDiagramCoordinatesHelperEntityClass(@Attribute(name="uuid")String uuid, @Attribute(name="x")double x, @Attribute(name="y")double y, @Attribute(name="superClassTriangleIndex")int superClassTriangleIndex, @ElementList(name="states")ArrayList<JsonClassDiagramCoordinatesHelperEntityState> states, @ElementList(name="instances")ArrayList<JsonClassDiagramCoordinatesHelperEventInstance> instances){
		this.uuid = uuid;
		this.x = x;
		this.y = y;
		this.superClassTriangleIndex = superClassTriangleIndex;
		this.states = states;
		this.instances = instances;
	}
	
	@SuppressWarnings("unchecked")
	public JsonClassDiagramCoordinatesHelperEntityClass(Map<String, Object> map) {
		for(String key : map.keySet()){
			Object value = map.get(key); 
			
			if(key.equals("uuid"))
			{
				this.uuid = value.toString();
			}
			if(key.equals("x"))
			{
				this.x = Double.parseDouble(value.toString());
			}
			if(key.equals("y"))
			{
				this.y = Double.parseDouble(value.toString());
			}
			if(key.equals("superClassTriangleIndex"))
			{
				this.superClassTriangleIndex = Integer.parseInt(value.toString());
			}
			if(key.equals("states")){
				ArrayList<Map<String, Object>> states = (ArrayList<Map<String, Object>>)value;
				for(Map<String, Object> stateMap : states){
					JsonClassDiagramCoordinatesHelperEntityState jsonState = new JsonClassDiagramCoordinatesHelperEntityState(stateMap);
					this.states.add(jsonState);
				}
			}
			if(key.equals("instances")){
				ArrayList<Map<String, Object>> instances = (ArrayList<Map<String, Object>>)value;
				for(Map<String, Object> instanceMap : instances){
					JsonClassDiagramCoordinatesHelperEventInstance jsonInstance = new JsonClassDiagramCoordinatesHelperEventInstance(instanceMap);
					this.instances.add(jsonInstance);
				}
			}
		}
	}

}
