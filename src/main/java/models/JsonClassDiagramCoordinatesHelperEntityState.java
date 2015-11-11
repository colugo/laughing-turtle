package main.java.models;

import java.util.Map;

import org.simpleframework.xml.Attribute;
import org.simpleframework.xml.Element;

@Element
public class JsonClassDiagramCoordinatesHelperEntityState {

	@Attribute
	public String uuid = "";
	@Attribute
	public double x = 0.0;
	@Attribute
	public double y = 0.0;

	
	public JsonClassDiagramCoordinatesHelperEntityState(@Attribute(name="uuid")String uuid, @Attribute(name="x")double x, @Attribute(name="y")double y){
		this.uuid = uuid;
		this.x = x;
		this.y = y;
	}
	
	public JsonClassDiagramCoordinatesHelperEntityState(Map<String, Object> map) {
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
		}
	}

}
