/**
 * @aim To exit the program if the battery level is low
 */

import lejos.hardware.Battery;
import lejos.hardware.lcd.LCD;
import lejos.robotics.subsumption.Behavior;
import lejos.utility.Delay;

public class BatteryLevel implements Behavior 
{
	// OBJ VAR
	private boolean suppressed = false;

	@Override
	public boolean takeControl() 
	{
		// Take control if battery voltage is too low
		return Battery.getVoltageMilliVolt() < 1000;
	}

	@Override
	public void action() 
	{
		suppressed = false;
		LCD.clear();
		LCD.drawString("Low Battery!", 0, 1);
		LCD.drawString("Shutting Down...", 0, 2);

		// Give time to display the message
		Delay.msDelay(1000);

		if (!suppressed) 
		{
			System.exit(0);      
		}
	}

	@Override
	public void suppress()
	{
		suppressed = true;
	}
}// endClass
