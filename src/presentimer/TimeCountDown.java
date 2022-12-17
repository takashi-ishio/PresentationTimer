/*
 * Copyright (C) 2010 Takashi Ishio All Rights Reserved.
 * Licensed under the MIT license: 
 * http://www.opensource.org/licenses/mit-license.php
 */
package presentimer;

import java.awt.Color;

public class TimeCountDown implements ITimeFormatter {

	private int start;
	private int end;
	private Color color;
	
	public TimeCountDown(int startTime, int endTime, Color c) {
		start = startTime;
		end = endTime;
		this.color = c;
	}
	
	@Override
	public Color getColor() {
		return color;
	}
	
	@Override
	public boolean isActive(int second) {
		return (start <= second) && (second < end);
	}
	
	/**
	 * @return time remaining.
	 */
	@Override
	public String toString(int second) {
		return TimeUtil.secondsToMinutes(end - second);
	}
}
