package br.ufrj.cos.lens.odyssey.tools.charon;

/**
 * This class represents an internal exception of Charon process machine
 * @author murta
 */
public class CharonException extends Exception {

	/**
	 * Constructs the exception
	 */
	public CharonException(String message) {
		super(message);
	}
	
	/**
	 * Constructs the exception with a cause
	 */
	public CharonException(String message, Throwable cause) {
		super(message, cause);
	}	
}
