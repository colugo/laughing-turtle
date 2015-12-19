package models;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;

import org.codehaus.jackson.map.ObjectMapper;

import main.java.models.JsonClassDiagramCoordinatesHelper;
import main.java.models.JsonClassDiagramCoordinatesHelperEntityClass;
import main.java.models.JsonClassDiagramCoordinatesHelperEntityRelation;

public class JsonCoordinateHelper {

	@SuppressWarnings("unchecked")
	public static void help(String jsonString, JsonClassDiagramCoordinatesHelper coordinatesHelper){
		try {
			ObjectMapper coordMapper = new ObjectMapper();
			Map<String, ArrayList<Map<String, Object>>> rootCoordAsMap;
			rootCoordAsMap = coordMapper.readValue(jsonString, Map.class);
			ArrayList<Map<String,Object>> classes = rootCoordAsMap.get("classes");
			for(Map<String,Object> classMap : classes)
			{
				coordinatesHelper.classes.add(new JsonClassDiagramCoordinatesHelperEntityClass(classMap));
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@SuppressWarnings("unchecked")
	public static void helpEntityClasses(LinkedHashMap<String, Object> map,	JsonClassDiagramCoordinatesHelper coordinatesHelper) {
		Collection<Map<String, Object>> classes = (Collection<Map<String, Object>>) map.get("classes");
		for(Map<String,Object> classMap : classes)
		{
			coordinatesHelper.classes.add(new JsonClassDiagramCoordinatesHelperEntityClass(classMap));
		}
		
		Collection<Map<String, Object>> relations = (Collection<Map<String, Object>>) map.get("relations");
		for(Map<String,Object> relationMap : relations)
		{
			coordinatesHelper.relations.add(new JsonClassDiagramCoordinatesHelperEntityRelation(relationMap));
		}
	}
	
}
