package main.java.avii.scenario;

public class InitialTestVectorProcedure extends TestVectorProcedure {

	public void setVector(TestVector vector) {
		this._vector = vector;
		this._vector.setInitialProcedure(this);
	}

}
