package main.java.avii.editor.contracts.metamodel.entities;


@SuppressWarnings("serial")
public class CannotCreateAttributeCalledStateException extends NullPointerException {

	@Override
	public String toString() {
		return "All classes have an attribute called 'state'. You cannot make another.";
	}

}
