package models;

import java.util.ArrayList;

public class JsonClassDiagram {

	public ArrayList<JsonEntityClass> _classes = new ArrayList<JsonEntityClass>();
	public ArrayList<JsonEntityRelation> _relations = new ArrayList<JsonEntityRelation>();
	public void add(JsonEntityClass jsonEntityClass) {
		this._classes.add(jsonEntityClass);
	}
	public void add(JsonEntityRelation jsonEntityRelation) {
		this._relations.add(jsonEntityRelation);
	}
	
}
