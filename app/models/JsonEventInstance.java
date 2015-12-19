package models;

import main.java.avii.editor.metamodel.entities.EntityEventInstance;

public class JsonEventInstance {
	public String _fromUUID;
	public String _toUUID;
	public int _fromIndex;
	public int _toIndex;
	public int _fromDragX;
	public int _fromDragY;
	public int _toDragX;
	public int _toDragY;
	public String _uuid;
	
	public JsonEventInstance(EntityEventInstance instance) {
		this._uuid = instance.getId();
		this._fromUUID = instance.getFromState().getId();
		this._toUUID = instance.getToState().getId();
		this._fromIndex = instance.fromIndex;
		this._toIndex = instance.toIndex;
		this._fromDragX = instance.fromDragX;
		this._fromDragY = instance.fromDragY;
		this._toDragX = instance.toDragX;
		this._toDragY = instance.toDragY;
	}

	
}
