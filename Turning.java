/**
 * @aim Turning Class which implements a turning behaviour if the robot meets an edge ( or comes off the coloured lines )
 */

import lejos.hardware.lcd.LCD;
import lejos.robotics.navigation.MovePilot;
import lejos.robotics.subsumption.Behavior;
import lejos.utility.Delay;

public class Turning implements Behavior 
{
    // OBJ VAR
	private MovePilot pilot;
    private ColourSensor colourSensor;
    private volatile boolean suppressed = false;

    // Constructor
    public Turning(MovePilot p) 
    {
        this.pilot = p;
        this.colourSensor = ColourSensor.getInstance();
    }

    @Override
    public boolean takeControl() 
    {
    	// Makes this behaviour take control if the robot comes onto the white map ( aka it is off the coloured lines )
    	return colourSensor.isWhiteDetected(); 
    }

    @Override
    public void action() 
    {
        suppressed = false;
        pilot.stop();
        LCD.drawString("Adjusting...", 0, 5);

        // Progressive small-angle corrections
        if (!scanForLine(10) && !scanForLine(-20) && 
            !scanForLine(30) && !scanForLine(-40))
        {
            // If all small corrections fail, try a larger correction
            pilot.rotate(50);
            Delay.msDelay(500);
            
            // If the larger correction fails, the robot goes the complete other way
            if (!isOnTargetColor()) 
            {
                pilot.rotate(-100); 
                Delay.msDelay(500);
            }
        }

        pilot.forward(); 
    }

    @Override
    public void suppress() 
    {
        suppressed = true;
        pilot.stop();
    }

    // Function which implements the turning angles given in action()
    private boolean scanForLine(int angle) 
    {
        pilot.rotate(angle);
        Delay.msDelay(100); 
        return isOnTargetColor();
    }

    // Function to simplify the checking behaviour for the turning. It checks whether the robot is on any of the lines on the map
    private boolean isOnTargetColor()
    {
        return colourSensor.isRedDetected() || colourSensor.isYellowDetected() ||
               colourSensor.isBlueDetected() || colourSensor.isGreenDetected();
    }
}
