package main.java.models;

import java.util.Map;

import org.simpleframework.xml.Attribute;
import org.simpleframework.xml.Element;

@Element
public class JsonClassDiagramCoordinatesHelperEventInstance {

	@Attribute
	public String uuid = "";
	@Attribute
	public int fromIndex = 0;
	@Attribute
	public int toIndex = 0;
	@Attribute
	public int fromDragX = 0;
	@Attribute
	public int fromDragY = 0;
	@Attribute
	public int toDragX = 0;
	@Attribute
	public int toDragY = 0;

	
	public JsonClassDiagramCoordinatesHelperEventInstance(@Attribute(name="uuid")String uuid, @Attribute(name="fromIndex") int fromIndex, @Attribute(name="toIndex")int toIndex,@Attribute(name="fromDragX")int fromDragX,@Attribute(name="fromDragY")int fromDragY,@Attribute(name="toDragX")int toDragX,@Attribute(name="toDragY")int toDragY){
		this.uuid = uuid;
		this.fromIndex = fromIndex;
		this.toIndex = toIndex;
		this.fromDragX = fromDragX;
		this.fromDragY = fromDragY;
		this.toDragX = toDragX;
		this.toDragY = toDragY;
	}
	
	public JsonClassDiagramCoordinatesHelperEventInstance(Map<String, Object> map) {
		for(String key : map.keySet()){
			Object value = map.get(key); 
			
			if(key.equals("uuid"))
			{
				this.uuid = value.toString();
			}
			if(key.equals("fromIndex"))
			{
				this.fromIndex = Integer.parseInt(value.toString());
			}
			if(key.equals("toIndex"))
			{
				this.toIndex = Integer.parseInt(value.toString());
			}
			if(key.equals("fromDragX"))
			{
				this.fromDragX = Integer.parseInt(value.toString());
			}
			if(key.equals("fromDragY"))
			{
				this.fromDragY = Integer.parseInt(value.toString());
			}
			if(key.equals("toDragX"))
			{
				this.toDragX = Integer.parseInt(value.toString());
			}
			if(key.equals("toDragY"))
			{
				this.toDragY = Integer.parseInt(value.toString());
			}
			
		}
	}

}
