/**
 * @aim This class handles the behaviour at the start of the program and when the robot returns to its starting point after a successful delivery
 */

import lejos.hardware.lcd.LCD;
import lejos.hardware.Button;
import lejos.robotics.navigation.MovePilot;
import lejos.robotics.subsumption.Behavior;
import lejos.utility.Delay;

public class Home implements Behavior 
{
	// OBJ VAR
	private MovePilot pilot;
	private ColourSensor sensor;
	private boolean suppressed = false;
	private boolean HomeStart = true;
	private String parcelColour = "";

	// Constructor
	public Home(MovePilot pilot, ColourSensor sensor) 
	{
		this.pilot = pilot;
		this.sensor = sensor;
	}

	////////////////////////////////////////////////////
	// Setters and Getters
	public boolean getHome()
	{
		return this.HomeStart;
	}

	public void setHome(boolean Home)
	{
		this.HomeStart = Home;
	}

	public String getParcelColour()
	{
		return this.parcelColour;
	}

	public void setParcelColour(String parcelColour)
	{
		this.parcelColour = parcelColour;
	}

	////////////////////////////////////////////////////

	@Override
	public boolean takeControl() 
	{
		// This behaviour takes control if purple is detected ( when the robot is at the home base )
		return sensor.isPurpleDetected();
	}

	@Override
	public void action() 
	{
		suppressed = false;

		// If the robot is at the designated parcel pick up point then the program lets
		// ..the user put a parcel in, after which it finds the correct line to follow to 
		// ..the drop off point
		if(this.getHome() == true)
		{

			while(!Button.ENTER.isDown() && !suppressed)
			{
				LCD.clear();
				LCD.drawString("Place Parcel", 0, 1);
			}

			// If suppressed during waiting, exit early
			if (suppressed) return;

			LCD.clear();
			LCD.drawString("Moving Forward", 0, 1);

			pilot.forward();

			while(sensor.isPurpleDetected())
			{
				Delay.msDelay(50);
			}

			Delay.msDelay(50);
			pilot.stop();

			// Check the block colour and determine direction
			String blockColor = sensor.detectBlockColour();

			// Turn left for red
			if (blockColor.equals("Red")) 
			{
				pilot.rotate(-90); 
				this.setParcelColour("Red");
			} 
			// Turn right for blue
			else if (blockColor.equals("Blue")) 
			{
				pilot.rotate(90); 
				this.setParcelColour("Blue");
			}
			// Makes the robot turn around if no block was placed
			else
			{
				pilot.rotate(180);
				pilot.forward();
				Delay.msDelay(1000);
				pilot.rotate(180);
			}

			pilot.forward();


			// Continue until yellow line is reached
			while (!suppressed && !sensor.isRedDetected() && !sensor.isBlueDetected());

			pilot.stop();

			// Transition to GoHome behavior
			LCD.clear();
			Delay.msDelay(500);
			this.setHome(false);
		}
		// Else if the robot has just finished returning home from the parcel
		// ..destination via the green line, it moves forward until it is at the designated 
		// ..parcel drop-off point, after which it rotates 180 degrees
		// .. so that it has the correct rotation to start moving towards the coloured lines again
		else
		{
			LCD.clear();
			LCD.drawString("Reached Home", 0, 1);
			pilot.forward();
			Delay.msDelay(1500);
			pilot.rotate(180);
			// Setting the home boolean var to be true so that now the program can detect that the robot has returned home ( used in the other functions )
			this.setHome(true);

			LCD.clear();
			LCD.drawString("Operation Completed", 0, 1);
			Delay.msDelay(1000);
			LCD.clear();

		}
	}

	@Override
	public void suppress() 
	{
		suppressed = true;
		pilot.stop();
	}
}// endClass