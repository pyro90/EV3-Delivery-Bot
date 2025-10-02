/**
 * @aim This class implements the exit behaviour. It allows the user to exit the program at any point in time via the press of the ESC key
 */
import lejos.hardware.Button;
import lejos.robotics.subsumption.Behavior;

public class ExitBehaviour implements Behavior 
{
    @Override
    public boolean takeControl() 
    {
        // Take control when the Escape button is pressed
        return Button.ESCAPE.isDown();
    }

    @Override
    public void action() 
    {
    	 // Terminate the program completely
        System.out.println("Exiting Program...");
        System.exit(0);
    }

    @Override
    public void suppress() 
    {
        // No suppression needed, since this is a final action
    }
}// endClass
