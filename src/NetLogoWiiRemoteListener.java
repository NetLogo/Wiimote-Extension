import org.nlogo.api.*;
import wiiremotej.*;
import wiiremotej.event.*;
import wiiremotej.event.WRIREvent.*;
import wiiremotej.event.WiiDeviceDiscoveredEvent.*;
import wiiremotej.event.WiiDeviceDiscoveryListener.*;


public class NetLogoWiiRemoteListener extends WiiRemoteAdapter {

    public int xAccel;
    public int yAccel;
    public int zAccel;
    public int roll;
    public int pitch;
	public double xIR; // IR light variables
	public double yIR;
	private double pos1x = 0;
    private double pos1y = 0;
    private double pos2x = 0;
    private double pos2y = 0;
	public double previousXIR = 0;
	public double previousYIR = 0;

    public boolean buttonA;
    public boolean buttonB;
    public boolean button1;
    public boolean button2;
    public boolean buttonUp;
    public boolean buttonDown;
    public boolean buttonRight;
    public boolean buttonLeft;
    public boolean buttonPlus;
    public boolean buttonMinus;
    public boolean buttonHome;

    public void accelerationInputReceived(WRAccelerationEvent evt)
    {
        xAccel = (int)(evt.getXAcceleration()/5*300);
        yAccel = (int)(evt.getYAcceleration()/5*300);
        zAccel = (int)(evt.getZAcceleration()/5*300);
        pitch = (int)(evt.getPitch()*1000);
        roll = (int)(evt.getRoll()*1000);
    }

    public void buttonInputReceived(WRButtonEvent evt) 
    {
        buttonA = evt.isPressed(WRButtonEvent.A);
        buttonB = evt.isPressed(WRButtonEvent.B);
        button1 = evt.isPressed(WRButtonEvent.ONE);
        button2 = evt.isPressed(WRButtonEvent.TWO);
        buttonUp = evt.isPressed(WRButtonEvent.UP);
        buttonDown = evt.isPressed(WRButtonEvent.DOWN);
        buttonLeft = evt.isPressed(WRButtonEvent.LEFT);
        buttonRight = evt.isPressed(WRButtonEvent.RIGHT);
        buttonHome = evt.isPressed(WRButtonEvent.HOME);
        buttonPlus = evt.isPressed(WRButtonEvent.PLUS);
        buttonMinus = evt.isPressed(WRButtonEvent.MINUS);
    }
	

	public void IRInputReceived(WRIREvent evt) 
	{
		if(evt.getIRLights()[0] != null)
		{
            pos1x = 1 - (evt.getIRLights()[0].getX());
            pos1y = 1 - (evt.getIRLights()[0].getY());
			xIR = (80*pos1x) - 40;
			yIR = (80*pos1y) - 40;
			previousXIR = xIR;
			previousYIR = yIR;
		}
		else if (evt.getIRLights()[1] != null)
		{
			pos2x = 1 - (evt.getIRLights()[1].getX());
            pos2y = 1 - (evt.getIRLights()[1].getY());
			xIR = (80*pos2x) - 40;
			yIR = (80*pos2x) - 40;
			previousXIR = xIR;
			previousYIR = yIR;
		}
		else
		{
			xIR = previousXIR;
			yIR = previousYIR;
		}
	}
}