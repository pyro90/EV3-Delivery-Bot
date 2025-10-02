/**
 * @aim This behaviour is intended to make the robot move forward if it detects a certain line colour ( or a certain line colour with a certain object colour )
 */
import lejos.hardware.lcd.LCD;
import lejos.robotics.navigation.MovePilot;
import lejos.robotics.subsumption.Behavior;
import lejos.utility.Delay;

public class Trundle implements Behavior 
{
	// OBJ VAR
	private MovePilot pilot;
	private ColourSensor colourSensor;
	private DestinationDetection destinationDetection; 
	private volatile boolean suppressed = false;

	// OBJ Constructor
	public Trundle(MovePilot p, ColourSensor cs, DestinationDetection destinationDetection) 
	{
		this.pilot = p;
		this.colourSensor = cs;
		this.destinationDetection = destinationDetection;
	}

	@Override
	public boolean takeControl() 
	{
		// Makes the robot move forward if: 
		// - Blue line is detected with a blue block in the tray
		// - Red line is detected with a red block in the tray
		// - A Yellow line is detected and neither red nor blue blocks are in the tray
		// - A Green line is detected and neither red nor blue blocks are in the tray, and the robot is not at its destination
		
		return (colourSensor.isBlueDetected() && colourSensor.detectBlockColour().equals("Blue")) 
				|| (colourSensor.isRedDetected() && colourSensor.detectBlockColour().equals("Red")) 
				|| (colourSensor.isYellowDetected() && !(colourSensor.detectBlockColour().equals("Red") || colourSensor.detectBlockColour().equals("Blue")))
				|| (colourSensor.isGreenDetected() && !destinationDetection.getDestination() && !(colourSensor.detectBlockColour().equals("Red") || colourSensor.detectBlockColour().equals("Blue")));
	}

	@Override
	public void action() 
	{
		suppressed = false;
		LCD.clear();
		pilot.forward();

		// Whilst the takeControl() conditions are met and the behaviour is not suppressed, the robot moves forward
		while (takeControl() && !suppressed) 
		{

			Delay.msDelay(50);
		}

		LCD.clear();
		pilot.stop();
	}

	@Override
	public void suppress() 
	{
		suppressed = true;
		pilot.stop();
	}
}
