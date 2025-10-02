/**
 * @aim Class which implements the behaviour at the destination ( the yellow line on the map ) where the robot is meant to drop off the block
 */

import lejos.hardware.Sound;
import lejos.hardware.lcd.LCD;
import lejos.robotics.Color;
import lejos.robotics.navigation.MovePilot;
import lejos.robotics.subsumption.Behavior;
import lejos.utility.Delay;

public class DestinationDetection implements Behavior 
{
	// OBJ VAR
	private MovePilot pilot;
	private ColourSensor sensor;
	private volatile boolean suppressed = false;
	private boolean Destination = false;

	// Constructor
	public DestinationDetection(MovePilot pilot) 
	{
		this.pilot = pilot;
		this.sensor = ColourSensor.getInstance();
	}

	////////////////////////////////////////////////////
	// Setters and Getters

	public void setDestination(boolean Destination)
	{
		this.Destination = Destination;
	}

	public boolean getDestination()
	{
		return this.Destination;
	}

	////////////////////////////////////////////////////

	@Override
	public boolean takeControl() 
	{
		// Takes control when the yellow line is detected with a red or blue block on the robots tray
		return sensor.isYellowDetected() && (sensor.detectBlockColour().equals("Red") || sensor.detectBlockColour().equals("Blue"));
	}

	// Function which implements the collection of the parcel from the robots tray
	public void packageRemoval() 
	{
		suppressed = false;

		// While loop which runs until there is no longer a red nor blue parcel in the tray
		while ((sensor.detectBlockColour().equals("Red") || sensor.detectBlockColour().equals("Blue"))) 
		{
			Delay.msDelay(100); 
			if (suppressed) return;  // Exit if suppressed
		}

		LCD.clear();
		LCD.drawString("Parcel Removed", 0, 1);
		LCD.drawString("Returning...", 0, 2);

		Delay.msDelay(3000); 
		LCD.clear();

		this.Destination = true;
	}

	@Override
	public void action() 
	{
		// Letting the user know that the robot has reached the delivery zone
		LCD.clear();
		LCD.drawString("Delivery Zone Reached!", 0, 1);
		Sound.playTone(1000, 500);  // Play a jingle
		LCD.drawString("Remove Parcel", 0, 2);

		// Wait until the parcel is removed (detects white area)
		packageRemoval();

		if (suppressed) return; 

		// Adjusting the position of the robot to make it more centred on the yellow line ( which allows for easier rotation later on )
		pilot.forward();
		Delay.msDelay(50);

		if (suppressed) return; 
	}

	@Override
	public void suppress() 
	{
		suppressed = true;
		pilot.stop(); 
	}
}// endClass
