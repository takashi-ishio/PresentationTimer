/*
 * Copyright (C) 2010 Takashi Ishio All Rights Reserved.
 * Licensed under the MIT license: 
 * http://www.opensource.org/licenses/mit-license.php
 */
package presentimer;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;


/**
 * A presentation timer tool.
 * @author ishio
 */
public class PresentationTimer {

	/**
	 * The main method of this program.
	 * @param args is a list of integers to specify 
	 *  timings when a chime should be played.
	 */
	public static void main(String[] args) {
		if (args.length >= 3) {
			int[] newAlarmTime = new int[3];
			for (int i=0; i<3; ++i) {
				try {
					newAlarmTime[i] = Integer.valueOf(args[i]);
				} catch (NumberFormatException e) {
					newAlarmTime[i] = i * 60;
				}
			}
			ITimeFormatter[] formatters;
			Color backColor = null;
			if (args.length >= 4) {
				String format = args[3];
				String[] colors = new String[0];
				if (args.length >= 5) {
					colors = args[4].toLowerCase().split("/");
				}

				ColorList colorList = new ColorList(colors);
				formatters = FormatterFactory.createFormatter(format, newAlarmTime, colorList);
				if (formatters == null) {
					System.err.println("Unknown Format Option: " + format);
					System.err.println("Valid format option is: 1[du]?2[du]?3[duU]");
					return;
				}
				if (colorList.hasNext()) {
					backColor = colorList.next();
				}
			} else {
				formatters = FormatterFactory.getDefaultFormatter();
			}
			new PresentationTimer(newAlarmTime, formatters, backColor);
		} else {
			new PresentationTimer(new int[] { 10, 20, 30}, FormatterFactory.getDefaultFormatter(), null);
		}
	}

	private Chime chime;
	private int[] alarmTime;
	private JLabel currentTimeLabel;
	private ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
    private ScheduledFuture<?> scheduleHandle = null;
	private int seconds = 0;
	private ITimeFormatter[] timeToStr;
	
	
	public PresentationTimer(int[] alarmTime, ITimeFormatter[] timeToStr, Color back) {
		assert alarmTime.length == 3;
		
		this.alarmTime = alarmTime;
		this.timeToStr = timeToStr;
		
		chime = new Chime();
		if (!chime.isAvailable()) {
			System.err.println("Failed to prepare sound data.");
		}

		final JFrame frame = new JFrame();
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setTitle("Presentation Timer");
        frame.setResizable(true);
        frame.setMinimumSize(new Dimension(320, 200));

        JPanel base = new JPanel();
        base.setLayout(new BorderLayout());
        frame.setContentPane(base);

        JPanel caption = new JPanel();
        caption.add(createTimingLabel());
        base.add(caption, BorderLayout.NORTH);

        JPanel buttons = new JPanel();
        base.add(buttons, BorderLayout.SOUTH);
        buttons.setLayout(new GridLayout(1, 2));
        buttons.add(createStartStopButton());
        buttons.add(createResetButton());
        
        currentTimeLabel = createCurrentTimeLabel();
        base.add(currentTimeLabel, BorderLayout.CENTER);

        if (back != null) {
        	base.setBackground(back);
        }

		updateLabel();
		
        frame.pack();
        frame.setVisible(true);
	}	
	
	private JButton createStartStopButton() {
		final JButton startStopButton = new JButton("Start");
		startStopButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				if (isActive()) {
					stop();
					startStopButton.setText("Start");
				} else {
					start();
					startStopButton.setText("Stop");
				}
			}
		});
		return startStopButton;
	}
	
	private JButton createResetButton() {
		final JButton resetButton = new JButton("Reset");
		resetButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				if (!isActive()) reset();
			}
		});
		return resetButton;
	}


	public JLabel createCurrentTimeLabel() {
		JLabel label = new JLabel();  
		label.setHorizontalAlignment(JLabel.CENTER);
		label.addComponentListener(new ComponentListener() {
			@Override
			public void componentShown(ComponentEvent arg0) {
			}
			
			@Override
			public void componentResized(ComponentEvent arg0) {
				JLabel l = (JLabel)arg0.getComponent();
				Font f = l.getFont();
				int width = l.getWidth();
				// 0.3 and 0.7 are experimentally determined on Windows 7.
				float availableHeight = (float)Math.min(width * 0.3, l.getHeight() * 0.7);
				l.setFont(f.deriveFont(availableHeight));
				l.repaint();
			}
			
			@Override
			public void componentMoved(ComponentEvent arg0) {
			}
			
			@Override
			public void componentHidden(ComponentEvent arg0) {
			}
		});
		return label;
	}
	
	public JLabel createTimingLabel() {
		JLabel timingLabel = new JLabel();
		StringBuilder timeBuf = new StringBuilder(); 
		timeBuf.append("1st bell = ");
		timeBuf.append(TimeUtil.secondsToMinutes(alarmTime[0]));
		timeBuf.append(",  2nd bell = ");
		timeBuf.append(TimeUtil.secondsToMinutes(alarmTime[1]));
		timeBuf.append(",  3rd bell = ");
		timeBuf.append(TimeUtil.secondsToMinutes(alarmTime[2]));
		timingLabel.setText(timeBuf.toString());
		return timingLabel;
	}
	
	/**
	 * Show the current time to the window.
	 */
	private void updateLabel() {
		int visibleSeconds = seconds;
		for (int i=0; i<timeToStr.length; ++i) {
			if (timeToStr[i].isActive(visibleSeconds)) {
				currentTimeLabel.setForeground(timeToStr[i].getColor());
				currentTimeLabel.setText(timeToStr[i].toString(visibleSeconds));
				break;
			}
		}
		currentTimeLabel.repaint();
	}
	
	/**
	 * Reset a timer.
	 */
	private void reset() {
		seconds = 0;
		updateLabel();
	}
	
	/**
	 * Start a timer thread.
	 */
	private void start() {
	    scheduleHandle = scheduler.scheduleAtFixedRate(new Runnable() {
	    	@Override
	    	public void run() {
	    		++seconds;
	    		updateLabel();
	    		
	    		for (int i=0; i<alarmTime.length; ++i) {
	    			if (seconds == alarmTime[i]) chime.playSound(i);
	    		}
	    	}
	    }, 1, 1, TimeUnit.SECONDS);
	}
	
	/**
	 * Stop a timer thread.
	 */
	private void stop() {
		scheduleHandle.cancel(true);
		scheduleHandle = null;
	}
	
	/**
	 * @return true if a timer is running.
	 */
	private boolean isActive() {
		return scheduleHandle != null;
	}
	
	
	
}
