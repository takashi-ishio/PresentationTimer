/*
 * Copyright (C) 2010 Takashi Ishio All Rights Reserved.
 * Licensed under the MIT license: 
 * http://www.opensource.org/licenses/mit-license.php
 */
package presentimer;

public class TimeUtil {

	/**
	 * Create a string "M:SS" from an integer.
	 * For example, "100 seconds" is converted to "1:40".
	 * "200 seconds" is converted to "3:20".
	 */
	public static String secondsToMinutes(int seconds) { 
		int min = seconds / 60;
		int sec = seconds % 60;
		StringBuilder builder = new StringBuilder(10);
		if (min < 10) builder.append("0");
		builder.append(min);
		builder.append(":");
		if (sec < 10) builder.append("0");
		builder.append(sec);
		return builder.toString();
	}

}
