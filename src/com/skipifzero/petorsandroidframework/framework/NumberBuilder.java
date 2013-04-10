package com.skipifzero.petorsandroidframework.framework;

/**
 * NumberBuilder: A simple class for building and updating a String with a number.
 * 
 * @author Peter Hillerström
 * @author Centril<twingoow@gmail.com> / Mazdak Farrokhzad.
 * @since 2013-04-07
 * @version 2.0
 */
public class NumberBuilder {
	/** The current number. */
	private int number;

	/** The internal StringBuilder */
	private final StringBuilder builder;
	
	/** The start index when updating the StringBuilder */
	private final int deleteStart;
	
	/**
	 * Constructs the builder with a specified text and a initial number.
	 * For example: "FPS: " and "30" will give the String "FPS: 30".
	 *
	 * @param initialNumber the initial number to set.
	 */
	public NumberBuilder( String text, int initialNumber ) {
		this.number = initialNumber;
		this.builder = new StringBuilder( text + this.number );
		this.deleteStart = text.length();
	}

	/**
	 * Updates/sets the new current number.
	 * 
	 * @param number the number to set
	 */
	public void update( int number ) {
		if ( this.number != number ) {
			this.number = number;
			this.builder.delete( deleteStart, builder.length() ).append( this.number );
		}
	}

	/**
	 * Returns the current number.
	 *
	 * @return the current number.
	 */
	public int getNumber() {
		return this.number;
	}

	/**
	 * Returns the string.
	 */
	public String toString() {
		return this.builder.toString();
	}
}
