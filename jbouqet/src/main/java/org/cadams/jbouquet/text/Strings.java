package org.cadams.jbouquet.text;

/**
 * Utility class which provides a bunch of string helper methods.
 * 
 * @author cta
 *
 */
public class Strings {

	/**
	 * Checks if a string is null or it's length is zero.
	 * 
	 * @param text
	 * @return true if null or zero length. false otherwise.
	 */
	public static boolean isEmpty(String text) {
		return !((text != null) && (text.length() > 0));
	}
}
