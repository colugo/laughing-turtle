package main.java.models;

import java.util.ArrayList;

import org.simpleframework.xml.Element;
import org.simpleframework.xml.ElementList;

@Element
public class JsonClassDiagramCoordinatesHelper {

	@ElementList
	public ArrayList<JsonClassDiagramCoordinatesHelperEntityClass> classes = new ArrayList<JsonClassDiagramCoordinatesHelperEntityClass>();
	@ElementList
	public ArrayList<JsonClassDiagramCoordinatesHelperEntityRelation> relations = new ArrayList<JsonClassDiagramCoordinatesHelperEntityRelation>();

}
