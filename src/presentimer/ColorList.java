/*
 * Copyright (C) 2010 Takashi Ishio All Rights Reserved.
 * Licensed under the MIT license: 
 * http://www.opensource.org/licenses/mit-license.php
 */
package presentimer;

import java.awt.Color;
import java.util.HashMap;

public class ColorList {

	private String[] colors;
	private int index = 0;
	
	public ColorList(String[] colors) {
		this.colors = colors;
	}
	
	public Color next() {
		if (hasNext()) {
			Color c = getColor(colors[index]);
			index++;
			return c;
		} else {
			return Color.BLACK;
		}
	}
	
	public boolean hasNext() {
		return index < colors.length;
	}
	
	private static HashMap<String, Color> colorMap;
	
	static {
		colorMap = new HashMap<String, Color>();
		colorMap.put("blue", Color.BLUE);
		colorMap.put("black", Color.BLACK);
		colorMap.put("cyan", Color.CYAN);
		colorMap.put("darkgray", Color.DARK_GRAY);
		colorMap.put("gray", Color.GRAY);
		colorMap.put("green", Color.GREEN);
		colorMap.put("lightgray", Color.LIGHT_GRAY);
		colorMap.put("magenta", Color.MAGENTA);
		colorMap.put("orange", Color.ORANGE);
		colorMap.put("pink", Color.PINK);
		colorMap.put("red", Color.RED);
		colorMap.put("white", Color.WHITE);
		colorMap.put("yellow", Color.YELLOW);
	}
	
	private static Color getColor(String name) {
		Color c = colorMap.get(name.toLowerCase());
		if (c != null) {
			return c;
		} else {
			return Color.BLACK;
		}
	}
	

}
