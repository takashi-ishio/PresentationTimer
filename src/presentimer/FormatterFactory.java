package presentimer;

import java.awt.Color;

public class FormatterFactory {
	
	public static ITimeFormatter[] getDefaultFormatter() {
		 return new ITimeFormatter[] { new TimeCountUp(0, Integer.MAX_VALUE, Color.BLACK) };	
	}
	
	public static ITimeFormatter[] createFormatter(String formatOption, int[] alarmTime, ColorList colors) {
		if (formatOption.matches("123d")) {
			return new ITimeFormatter[] {
					new TimeCountDown(0, alarmTime[2], colors.next()),
					createLast(formatOption.charAt(3), 0, alarmTime[2], colors.next())
			};
		} else if (formatOption.matches("123[uU]")) {
			return new ITimeFormatter[] {
					new TimeCountUp(0, alarmTime[2], colors.next()),
					createLast(formatOption.charAt(3), 0, alarmTime[2], colors.next())
			};
		} else if (formatOption.matches("12[du]3[duU]")) {
			ITimeFormatter t1 = create(formatOption.charAt(2), 0, alarmTime[1], colors.next());
			ITimeFormatter t2 = create(formatOption.charAt(4), alarmTime[1], alarmTime[2], colors.next());
			ITimeFormatter t3 = createLast(formatOption.charAt(4), alarmTime[1], alarmTime[2], colors.next());
			return new ITimeFormatter[] { t1, t2, t3 };
		} else if (formatOption.matches("1[du]23[duU]")) {
			ITimeFormatter t1 = create(formatOption.charAt(1), 0, alarmTime[0], colors.next());
			ITimeFormatter t2 = create(formatOption.charAt(4), alarmTime[0], alarmTime[2], colors.next());
			ITimeFormatter t3 = createLast(formatOption.charAt(4), alarmTime[0], alarmTime[2], colors.next());
			return new ITimeFormatter[] { t1, t2, t3 };
		} else if (formatOption.matches("1[du]2[du]3[duU]")) {
			ITimeFormatter t1 = create(formatOption.charAt(1), 0, alarmTime[0], colors.next());
			ITimeFormatter t2 = create(formatOption.charAt(3), alarmTime[0], alarmTime[1], colors.next());
			ITimeFormatter t3 = create(formatOption.charAt(5), alarmTime[1], alarmTime[2], colors.next());
			ITimeFormatter t4 = createLast(formatOption.charAt(5), alarmTime[1], alarmTime[2], colors.next());
			return new ITimeFormatter[] { t1, t2, t3, t4 };
		} else {
			return null;
		}
	}
	
	private static ITimeFormatter create(char type, int start, int end, Color c) {
		if (type == 'd') {
			return new TimeCountDown(start, end, c);
		} else if ((type == 'u')||(type == 'U')) {
			return new TimeCountUp(start, end, c);
		} else {
			assert false;
			return null;
		}
	}
	
	
	private static ITimeFormatter createLast(char type, int previousTime, int lastTime, Color c) {
		if (type == 'U') {
			return new TimeCountUp(previousTime, Integer.MAX_VALUE, c);
		} else {
			return new TimeCountUp(lastTime, Integer.MAX_VALUE, c);
		}
	}
	
}
