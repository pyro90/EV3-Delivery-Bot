
/**
 * @aim This is the Driver class, which holds the arbitrator. This allows to connect all the behaviours and classes under a single main driver class which runs the entire program.
 */

import lejos.hardware.port.MotorPort;
import lejos.robotics.chassis.Wheel;
import lejos.robotics.chassis.WheeledChassis;
import lejos.robotics.navigation.MovePilot;
import lejos.robotics.subsumption.Arbitrator;
import lejos.robotics.subsumption.Behavior;
import lejos.hardware.Button;
import lejos.hardware.lcd.LCD;
import lejos.hardware.motor.BaseRegulatedMotor;
import lejos.hardware.motor.EV3LargeRegulatedMotor;
import lejos.hardware.port.Port;
import lejos.robotics.chassis.Chassis;

public class Driver 
{	


	public static void main(String[] args) 
	{
		displayWelcomeScreen();

		// Declaring a Robot Pilot which controls the movement of the colour tracker
		MovePilot pilot = getPilot(MotorPort.D, MotorPort.A, 56, 65);
		pilot.setLinearSpeed(100);

		// Create the singleton instance of ColourSensor within Driver ( to call to the other behaviours )
		ColourSensor colourSensor = ColourSensor.getInstance();

		// Creating a home object, in order to call it to the GoHome behaviour in object form
		Home home = new Home(pilot, colourSensor);

		// Create DestinationDetection (needed for GoHome)
		DestinationDetection destination = new DestinationDetection(pilot);

		// Creating the Behaviours for the arbitrator
		Behavior Trundle = new Trundle(pilot, colourSensor, destination);
		Behavior Turning = new Turning(pilot);
		Behavior GoHome = new GoHome(pilot, colourSensor, destination,home); 
		Behavior Destination = destination; // Ensure DestinationDetection is referenced
		Behavior ExitBehaviour = new ExitBehaviour();
		Behavior Home = home;
		Behavior TouchSensorEmergencyStop = new TouchSensorEmergencyStop(pilot);
		Behavior batteryLevel = new BatteryLevel();

		// Arbitrator controls which behaviour takes priority ( priority ranked from right hand side being the highest, to left hand side being the lowest )
		Arbitrator ab = new Arbitrator(new Behavior[] {Trundle, Turning, Destination, GoHome, Home,TouchSensorEmergencyStop, ExitBehaviour, batteryLevel});
		ab.go();
	}

	// Function to display the welcome screen
	private static void displayWelcomeScreen() 
	{
		LCD.clear();
		LCD.drawString("Colour Tracker Program", 2, 1);
		LCD.drawString("Version: 1.0", 2, 2);
		LCD.drawString("Authors:", 2, 4);
		LCD.drawString("KB, CL, YM", 2, 5);
		LCD.drawString("Press any key", 2, 7);

		// Wait for any button press to proceed
		Button.waitForAnyPress();
		LCD.clear();
	}

	// Function which allows to simplify the main function by handling the creation and declaration of the pilot
	private static MovePilot getPilot(Port left, Port right, int diam, int offset)
	{
		BaseRegulatedMotor mL = new EV3LargeRegulatedMotor(left);
		Wheel wL = WheeledChassis.modelWheel(mL, diam).offset(-1 * offset);
		BaseRegulatedMotor mR = new EV3LargeRegulatedMotor(right);
		Wheel wR = WheeledChassis.modelWheel(mR, diam).offset(offset);
		Wheel[] wheels = new Wheel[] {wR, wL};
		Chassis chassis = new WheeledChassis(wheels, WheeledChassis.TYPE_DIFFERENTIAL);

		return new MovePilot(chassis);
	}
}// endClass
