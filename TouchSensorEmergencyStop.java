/**
 * @aim This class implements an emergency stop behaviour, which stops the robot from moving, and doesn't restart the movement until the button is pressed again
 * Note : This behaviour does not end the program, but rather halts it until the touch sensor is pressed again
 */
import lejos.hardware.sensor.EV3TouchSensor;
import lejos.hardware.port.SensorPort;
import lejos.hardware.lcd.LCD;
import lejos.robotics.subsumption.Behavior;
import lejos.robotics.navigation.MovePilot;
import lejos.utility.Delay;

public class TouchSensorEmergencyStop implements Behavior 
{
   // OBJ VAR
	private MovePilot pilot;
    private EV3TouchSensor touchSensor;
    private boolean suppressed = false;
    
    // Constructor
    public TouchSensorEmergencyStop(MovePilot p) 
    {
        this.pilot = p;
        this.touchSensor = new EV3TouchSensor(SensorPort.S3);
    }

    @Override
    public boolean takeControl() 
    {
        // This behavior takes control if there is at least any input on the touch sensor
        return isPressed(); 
    }

    @Override
    public void action() 
    {
        // Letting the user know that there was an emergencty stop
    	suppressed = false;
        pilot.stop();
        LCD.clear();
        LCD.drawString("Emergency Stop!", 0, 5);

        // Loop which stops ever other behaviour until the touch sensor is pressed again
        while (!suppressed && isPressed()) 
        {
            Delay.msDelay(100);
        }

        // Wait for the button to be pressed again to resume
        while (!suppressed && !isPressed()) 
        {
            Delay.msDelay(100);
        }

        LCD.clear();
        LCD.drawString("Resuming...", 0, 5);
    }

    @Override
    public void suppress() 
    {
        suppressed = true;
    }

    // Function which checks if there is any input on the touch sensor ( aka whether its being pressed or not )
    private boolean isPressed() 
    {
        float[] sample = new float[touchSensor.sampleSize()];
        touchSensor.fetchSample(sample, 0);
        return sample[0] == 1;
    }
}// endClass
