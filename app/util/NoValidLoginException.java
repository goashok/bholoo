package util;

public class NoValidLoginException extends RuntimeException {

	public NoValidLoginException(String msg) {
		super(msg);
	}
	
	public NoValidLoginException(String msg, Throwable t) {
		super(msg, t);
	}
}
