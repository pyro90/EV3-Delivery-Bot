/**
 * @aim This Class handles the behaviour to find and move the robot to the required line to go back to base after a successful delivery
 */

import lejos.hardware.lcd.LCD;
import lejos.robotics.Color;
import lejos.robotics.navigation.MovePilot;
import lejos.robotics.subsumption.Behavior;
import lejos.utility.Delay;

public class GoHome implements Behavior 
{
	// OBJ VAR
	private MovePilot pilot;
	private ColourSensor sensor;
	private DestinationDetection destinationDetection;
	private Home home;
	private boolean suppressed = false;
	private boolean goHome = false;

	// Constructor
	public GoHome(MovePilot pilot, ColourSensor sensor, DestinationDetection destinationDetection, Home home) 
	{
		this.pilot = pilot;
		this.sensor = sensor;
		this.destinationDetection = destinationDetection;
		this.home = home;
	}

	@Override
	public boolean takeControl() 
	{
		// Take control only if Destination is reached and the line sensor detects green
		return destinationDetection.getDestination() && sensor.isGreenDetected();
	}

	@Override
	public void action() 
	{
		suppressed = false;
		LCD.clear();
		LCD.drawString("Following Green", 0, 1);

		// Moving the robot to the right position to perform a turn later on
		pilot.forward();
		Delay.msDelay(600);

		pilot.stop();

		// Rotates the robot right 90 degrees if it came from the Red line 
		if(home.getParcelColour().equals("Red"))
		{
			pilot.rotate(90);
		}
		// Rotates the robot left 90 degrees if it came from the Blue line 
		else if(home.getParcelColour().equals("Blue"))
		{
			pilot.rotate(-90);
		}

		Delay.msDelay(500);
		LCD.clear();
		pilot.forward();

		// Setting the value of the Destination boolean variable to be false, since the robot 
		// ..is no longer at the destination to drop off parcels
		destinationDetection.setDestination(false);

		// Making the robot follow the green line until it reaches the purple line 
		// ..( green no longer detected at that point )
		while (!suppressed && sensor.isGreenDetected()) 
		{
			LCD.clear();
			LCD.drawString("On the Green path", 0, 1);
			Delay.msDelay(50);
		}

		pilot.stop();
	}

	@Override
	public void suppress() 
	{
		suppressed = true;
		pilot.stop();
	}
}
