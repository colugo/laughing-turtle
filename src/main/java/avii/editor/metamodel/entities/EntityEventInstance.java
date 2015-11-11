/**
 * @author Alastair Parker
 * Copyright Alastair Parker 2011
 */
package main.java.avii.editor.metamodel.entities;

public class EntityEventInstance {

	private EntityState _fromState;
	private EntityState _toState;
	private EntityEventSpecification _specification;
	public int fromIndex=0;
	public int toIndex=0;
	public int fromDragX=0;
	public int fromDragY=0;
	public int toDragX=0;
	public int toDragY=0;
	private String _id;

	
	public EntityEventInstance(EntityEventSpecification spec, EntityState fromState, EntityState toState) {
		this._fromState = fromState;
		this._toState = toState;
		this._specification = spec;

		this._specification.addEventInstance(this);
	}
	
	@Override
	public String toString()
	{
		String str = this._fromState.getName() + "[" + this._specification.getName() + "]" + this._toState.getName();
		return str;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		EntityEventInstance other = (EntityEventInstance) obj;
		if (!getFromState().equals(other.getFromState()))
			return false;
		if (!getToState().equals(other.getToState()))
			return false;
		if (!(_specification.equals(other._specification))) {
			return false;
		}

		return true;
	}

	public EntityState getToState() {
		return _toState;
	}

	public EntityState getFromState() {
		return _fromState;
	}

	public EntityEventSpecification getSpecification() {
		return _specification;
	}

	public void setId(String _instanceId) {
		this._id = _instanceId;
	}
	
	public String getId(){
		return this._id;
	}

	public void setSpec(EntityEventSpecification theSpec) {
		this._specification = theSpec;
	}

}
