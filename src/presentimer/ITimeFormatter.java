/*
 * Copyright (C) 2010 Takashi Ishio All Rights Reserved.
 * Licensed under the MIT license: 
 * http://www.opensource.org/licenses/mit-license.php
 */
package presentimer;

import java.awt.Color;

public interface ITimeFormatter {

	public boolean isActive(int second);
	
	/**
	 * @return a string "MM:SS" for a label. 
	 */
	public String toString(int second);
	
	/**
	 * @return a color to draw a string.
	 */
	public Color getColor();
}
