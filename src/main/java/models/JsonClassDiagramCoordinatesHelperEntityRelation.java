package main.java.models;

import java.util.Map;

import org.simpleframework.xml.Attribute;
import org.simpleframework.xml.Element;

@Element
public class JsonClassDiagramCoordinatesHelperEntityRelation {

	@Attribute
	public String uuid = "";
	@Attribute
	public int endAIndex = 0;
	@Attribute
	public int endBIndex = 0;
	@Attribute
	public int verbAOffsetX = 0;
	@Attribute
	public int verbAOffsetY = 0;
	@Attribute
	public int verbBOffsetX = 0;
	@Attribute
	public int verbBOffsetY = 0;
	
	public JsonClassDiagramCoordinatesHelperEntityRelation(@Attribute(name="uuid")String uuid, @Attribute(name="endAIndex") int endAIndex, @Attribute(name="endBIndex")int endBIndex, @Attribute(name="verbAOffsetX")int verbAOffsetX, @Attribute(name="verbAOffsetY")int verbAOffsetY, @Attribute(name="verbBOffsetX")int verbBOffsetX, @Attribute(name="verbBOffsetY")int verbBOffsetY){
		this.uuid = uuid;
		this.endAIndex = endAIndex;
		this.endBIndex = endBIndex;
		this.verbAOffsetX = verbAOffsetX;
		this.verbAOffsetY = verbAOffsetY;
		this.verbBOffsetX = verbBOffsetX;
		this.verbBOffsetY = verbBOffsetY;
	}
	
	public JsonClassDiagramCoordinatesHelperEntityRelation(Map<String, Object> map) {
		for(String key : map.keySet()){
			Object value = map.get(key); 
			
			if(key.equals("uuid"))
			{
				this.uuid = value.toString();
			}
			if(key.equals("endAIndex"))
			{
				this.endAIndex = Integer.parseInt(value.toString());
			}
			if(key.equals("endBIndex"))
			{
				this.endBIndex = Integer.parseInt(value.toString());
			}
			if(key.equals("verbAOffsetX"))
			{
				this.verbAOffsetX = Integer.parseInt(value.toString());
			}
			if(key.equals("verbAOffsetY"))
			{
				this.verbAOffsetY = Integer.parseInt(value.toString());
			}
			if(key.equals("verbBOffsetX"))
			{
				this.verbBOffsetX = Integer.parseInt(value.toString());
			}
			if(key.equals("verbBOffsetY"))
			{
				this.verbBOffsetY = Integer.parseInt(value.toString());
			}
		}
	}

}
