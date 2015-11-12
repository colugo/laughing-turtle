package implementation.storage;

@SuppressWarnings("serial")
public class StorageException extends Exception {
	private String _message;
	public StorageException(String message){
		this._message = message;
	}
	public String toString(){
		return this._message;
	}
}
