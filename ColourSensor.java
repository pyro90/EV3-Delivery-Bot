/**
 * @aim This class handles all the colour sensor data + functions. It has 2 main sensors that it creates : 
 * - The Line sensor ( which handles the colouring of the lines that the robot is meant to follow )
 * - The Block Sensor ( which handles the colour of the blocks that are placed in the robots tray )
 */
import lejos.hardware.Button;
import lejos.hardware.lcd.LCD;
import lejos.hardware.port.SensorPort;
import lejos.hardware.sensor.EV3ColorSensor;
import lejos.hardware.sensor.SensorMode;
import lejos.robotics.Color;

public class ColourSensor 
{
	// OBJ VAR
	private static ColourSensor instance; 
	private EV3ColorSensor lineSensor;    
	private EV3ColorSensor blockSensor;  
	private SensorMode lineColorProvider;
	private SensorMode blockColorProvider;

	private float[] lineSample;
	private float[] blockSample;

	// Class constructor
	private ColourSensor() 
	{
		// Creating and declaring the 2 sensors used by the robot
		lineSensor = new EV3ColorSensor(SensorPort.S1);  
		blockSensor = new EV3ColorSensor(SensorPort.S4);

		lineColorProvider = lineSensor.getRGBMode();
		blockColorProvider = blockSensor.getRGBMode();

		// Creating 2 float arrays which handle all the samples provided by the sensors
		lineSample = new float[lineColorProvider.sampleSize()];
		blockSample = new float[blockColorProvider.sampleSize()];
	}

	// Singleton instance retrieval
	public static ColourSensor getInstance()
	{
		if (instance == null) 
		{
			instance = new ColourSensor();
		}
		return instance;
	}

	// Function to close both sensors
	public void close() 
	{
		lineSensor.close();
		blockSensor.close();
	}

	// Fetch and return the latest RGB sample from the line sensor (S1)
	public float[] getLineRGBSample() 
	{
		lineColorProvider.fetchSample(lineSample, 0);
		return lineSample.clone();
	}

	// Fetch and return the latest RGB sample from the block sensor (S4)
	public float[] getBlockRGBSample() 
	{
		blockColorProvider.fetchSample(blockSample, 0);
		return blockSample.clone();
	}

	// Detect the color of the block based on RGB values
	public String detectBlockColour() 
	{
		float[] sample = getBlockRGBSample();
		float red = sample[0];
		float green = sample[1];
		float blue = sample[2];

		//displayRGB(sample, "Block Sensor");

		// Red colour range for the blocks
		if((red >= 0.19 && red<=0.26) && green <= 0.1  && blue <= 0.1) 
		{
			LCD.drawString("Block : Red ", 0, 5);
			LCD.clear();

			return "Red";
		} 
		// Blue colour range for the blocks
		else if (red <= 0.1 && (green >= 0.08 && green <= 0.12) && (blue >= 0.08 && blue <= 0.12)) 
		{		
			LCD.drawString("Block : Blue ", 0, 5);
			LCD.clear();

			return "Blue";
		} 
		else 
		{
			return "Unknown";
		}
	}
   
	//////////////////////////////////////////////////////////////////////////////////////////////////////
	// Functions to check which colour is being detected by the line sensor based on pre-tested values

	public boolean isWhiteDetected() 
	{
		float[] sample = getLineRGBSample(); 

		float red = sample[0];
		float green = sample[1];
		float blue = sample[2];


		// Check if RGB values match the orange line
		return (red >= 0.1) && (red <= 0.4) && (green >= 0.1) && (green <= 0.4) && (blue >= 0.1) && (blue <= 0.35);
	}

	public boolean isPurpleDetected() 
	{
		float[] sample = getLineRGBSample();
		//displayRGB(sample, "Line Sensor");

		float red = sample[0];
		float green = sample[1];
		float blue = sample[2];

		return (red >= 0.08 && red <= 0.17 ) && (green <= 0.1) && (blue >= 0.1 && blue <= 0.2);
	}
	
	public boolean isGreenDetected() 
	{
		float[] sample = getLineRGBSample(); 
		//	displayRGB(sample, "Line Sensor");

		float red = sample[0];
		float green = sample[1];
		float blue = sample[2];

		return (red >= 0.05 && red <= 0.15) && (green >= 0.2 && green <= 0.3) && (blue <= 0.1);
	}

	public boolean isBlueDetected() 
	{
		float[] sample = getLineRGBSample();
		//displayRGB(sample, "Line Sensor");

		float red = sample[0];
		float green = sample[1];
		float blue = sample[2];

		return (red <= 0.1) && (green >= 0.1 && green <= 0.2) && (blue >= 0.15 && blue <= 0.25);
	}

	public boolean isRedDetected() 
	{
		float[] sample = getLineRGBSample(); 
		//displayRGB(sample, "Line Sensor");

		float red = sample[0];
		float green = sample[1];
		float blue = sample[2];

		return (red >= 0.3 && red <= 0.4) && (green <= 0.1) && (blue <= 0.07);
	}

	public boolean isYellowDetected() 
	{
		float[] sample = getLineRGBSample(); 
		//displayRGB(sample, "Line Sensor");

		float red = sample[0];
		float green = sample[1];
		float blue = sample[2];

		return (red <= 0.4 && red >= 0.3) && (green >= 0.18 && green <= 0.25) && (blue <= 0.07);
	}

	//////////////////////////////////////////////////////////////////////////////////////////////////////
	
	// Function to display RGB values on the EV3 screen
	private void displayRGB(float[] sample, String sensorType) 
	{
		LCD.clear();
		LCD.drawString(sensorType, 0, 0);
		LCD.drawString("Red: " + sample[0], 0, 1);
		LCD.drawString("Green: " + sample[1], 0, 2);
		LCD.drawString("Blue: " + sample[2], 0, 3);
	}


	// Main function - used for testing purposes only
	public static void main(String[] args) 
	{
		ColourSensor colourSensor = ColourSensor.getInstance();

		while (!Button.ESCAPE.isDown()) 
		{
			float[] rgb = colourSensor.getBlockRGBSample(); 

			colourSensor.displayRGB(rgb,"Line");

			lejos.utility.Delay.msDelay(500); // Wait for 500ms before refreshing
		}
		System.exit(0);
	}// endMain
}// endClass
