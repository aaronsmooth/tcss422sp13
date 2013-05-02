/*
 * Quan Le
 * Tyler Powers
 * Aaron Nelson
 * Seth Kramer
 * Team Robbins Egg Blue Dolphins
 * 05/02/2013
 */

/**
 * SlaveInterger class is used to synchronize an integer.
 * 
 * @author Quan Le
 * @author Tyler Powers
 * @author Aaron Nelson
 * @author Seth Kramer
 * @version 1.0
 */
public class SlaveInteger {
	/**
	 * Private field for integer.
	 */
	private int myInt;
	
	/**
	 * Constructor for SlaveInteger.
	 * @param i an integer
	 */
	public SlaveInteger(int i) {
		myInt = i;
	}
	
	/**
	 * Decrements the integer.
	 */
	public void decrement() {
		myInt--;
	}
	
	/**
	 * Increment the integer.
	 */
	public void increment() {
		myInt++;
	}
	
	/**
	 * Sets value of the integer.
	 * @param i the integer value
	 */
	public void setVal(int i) {
		myInt = i;
	}
	
	/**
	 * Gets value of the integer.
	 * @return value of the integer
	 */
	public int getVal() {
		return myInt;
	}
}
