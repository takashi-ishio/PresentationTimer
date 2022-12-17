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
