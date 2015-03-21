/**
 * 		BINDS: 
 * 		
 * 		Joystick 1:
 * 
 * 		Select = Reset elevator encoders
 * 		Start = Cam Manual (in case pot breaks)
 * 		
 * 		Left Thumbstick - Forward/Backward
 * 		Right Thumbstick - Turn Left/Right
 * 
 * 		LB = Arms Toggle
 * 		RB = Cam Manual toggler
 * 
 * 		A = Stinger	
 * 		B = Switch Gears
 * 	
 *		Joystick 2:
 *
 *		Start = Cam manual (forward only)
 *
 *		LB = Arms Toggle
 *
 * 		A = Cam Setpoint Toggle
 * 		X = Elevator Low
 * 		Y = Elevator One Tote
 *		
 *		Triggers = Elevator Manual
 *
 *		Left Stick = Arm Motors In/Out
 *		Right Stick = Arm Motors L/R
 *
 */

//THIS IS COMPETITION BOT CODE

package org.usfirst.frc.team4334.robot;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import edu.wpi.first.wpilibj.AnalogInput;
import edu.wpi.first.wpilibj.CANTalon;
import edu.wpi.first.wpilibj.Compressor;
import edu.wpi.first.wpilibj.CounterBase.EncodingType;
import edu.wpi.first.wpilibj.DigitalInput;
import edu.wpi.first.wpilibj.DoubleSolenoid;
import edu.wpi.first.wpilibj.Encoder;
import edu.wpi.first.wpilibj.IterativeRobot;
import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj.Preferences;
import edu.wpi.first.wpilibj.Talon;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

/**
 * This is the official code for ATA's 2015 robot: "Elevation".
 */

// [COPYRIGHT] Alberta Tech Alliance 2015. All rights reserved.

public class Robot extends IterativeRobot 
{
    
    //This function is run when the robot is first started up and should be
    //used for any initialization code.
     	
    Preferences prefs;
   
	Joystick joy;	//Xbox controllers
	Joystick joy2;
	
	CANTalon canFL; // Talon SRXs
    CANTalon canBL;
    CANTalon canFR;
    CANTalon canBR;
    CANTalon canWinch;
    CANTalon canWinch2;
    Talon talKicker;	//Talon SRs
    Talon talArmLeft;
    Talon talArmRight;
    
    Encoder encoderL; //Encoders
	Encoder encoderR;
	Encoder encoderElevator;
	
	Timer sensorThreadAuto; 	//Threads
	Timer elevatorThreadAuto;
	Timer elevatorThread2Auto;	
	Timer sensorThread;
	Timer elevatorThread;
	Timer elevatorThread2;
	Timer camThreadAuto;
	Timer camThread;
	Timer recording;

	AnalogInput pot1;		//Potentiometer, Compressor and solenoids
	Compressor comp;
    DoubleSolenoid gearShift;
    DoubleSolenoid leftArm;
    DoubleSolenoid rightArm;
    DoubleSolenoid flipper;
   
    DigitalInput limit1;	//Limit Switches
    DigitalInput limit2;
    
    String gearPos, gearPos2; // Strings for the smartdasboard gear positions
    
    double leftThumb2,rightThumb2; 	// Variables where second Xbox thumbstick values are stored
    double leftTrig,rightTrig;	   	// Variables where Xbox trigger values are stored
    double leftTrig2,rightTrig2;	// Variables where second Xbox trigger values are stored
    double degrees, potDegrees;		// Variables where Potentiometer values are stored
	double leftThumb,rightThumb;	// Variables where first Xbox thumbstick values are stored
	double turnRad, speedMultiplier;// Variables for turning radius and overall speed multiplier
	double deadZ, deadZ2;			// Variables that store deadzones
	double camSet1, camSet2;		// Variables that decide that setpoints the cam uses
	
	//emulator defines
	double buttonJ1Ljoy; 
	double buttonJ1Rjoy;
	double buttonJ2Ljoy;
	double buttonJ2Rjoy;
	double buttonJ2Ltrig;
	double buttonJ2Rtrig;
	
    boolean stillPressed;	//Booleans to stop button presses from repeating 20 x per second lol
    boolean stillPressed2;
    boolean stillPressed3;
    boolean stillPressed4;
    boolean stillPressed5;
    boolean stillPressed6;
    boolean stillPressed7;
    boolean stillPressed8;
    boolean stillPressed9;
    boolean stillPressed10;
    boolean elevatorMax;	//Booleans for elevator limit switches
    boolean elevatorMin;
    boolean elevatorManual;	//Boolean to decide whether manual elevator control is allowed
    boolean camSetPoint = false;
	boolean gotoSpot, gotoSpot2, gotoSpot3;
	boolean gotoCam1 = true;
	boolean gotoCam2 = false;
	boolean camChange = false;
	boolean camActivate = false;
	boolean goOnce, teleOpOnce; // Variables to allow auto and certain teleop funtions to run only once
	
	//defineing buttons for emulator
	boolean buttonJ1B1;
	boolean buttonJ1B2;
	boolean buttonJ1B3;
	boolean buttonJ1B4;
	boolean buttonJ1B5;
	boolean buttonJ1B6;
	boolean buttonJ1B7;
	boolean buttonJ1B8;
	boolean buttonJ1B9;
	boolean buttonJ1B10;
	
	boolean buttonJ2B1;
	boolean buttonJ2B2;
	boolean buttonJ2B3;
	boolean buttonJ2B4;
	boolean buttonJ2B5;
	boolean buttonJ2B6;
	boolean buttonJ2B7;
	boolean buttonJ2B8;
	boolean buttonJ2B9;
	boolean buttonJ2B10;
	
	
	int camMode;	// Decide whether cam should use setpoint or manual mode
	int leftR, rightR, elevatorR;	// Variables that store encoder values. "R" means rotations not "right".
	int autoMode;	// Variable that decides which auto to use
	
    public void robotInit()
    {
   	
    	canFL = new CANTalon(1); // Declaring shit
    	canBL = new CANTalon(2);
    	canFR = new CANTalon(5);
    	canBR = new CANTalon(6);
    	canWinch = new CANTalon(3);
    	canWinch2 = new CANTalon(4);
    	talKicker = new Talon(0);
    	talArmLeft = new Talon(1);
    	talArmRight = new Talon(2);
    
    	sensorThread = new Timer();
    	elevatorThread = new Timer();
    	elevatorThread2 = new Timer();
    	sensorThreadAuto = new Timer();
    	elevatorThreadAuto = new Timer();
    	elevatorThread2Auto = new Timer();
    	camThreadAuto = new Timer();
    	camThread = new Timer();
    	recording = new Timer();
    
    	elevatorManual = false;
    	camMode = 1;
   
    	gearPos = "Gear Position:";
    
    	joy = new Joystick(0);
    	joy2 = new Joystick(1);
    
    	comp  = new Compressor(0);
    	comp.setClosedLoopControl(true); // Setting compressor to closed loop control (basically automatic)
    
    	pot1 = new AnalogInput(0);  
    
    	limit1 = new DigitalInput(3);
    	limit2 = new DigitalInput(2);
    
    	rightArm = new DoubleSolenoid(2, 3);
    	rightArm.set(DoubleSolenoid.Value.kForward);
    	leftArm = new DoubleSolenoid(4, 5);
    	leftArm.set(DoubleSolenoid.Value.kForward);
    	gearShift = new DoubleSolenoid(6, 7);
    	gearShift.set(DoubleSolenoid.Value.kForward);
    	flipper = new DoubleSolenoid(0, 1);
    	flipper.set(DoubleSolenoid.Value.kReverse);
    
    	encoderElevator = new Encoder(0, 1, true, EncodingType.k4X);
    	encoderR = new Encoder(8, 9, true, EncodingType.k4X);
    	encoderL = new Encoder(6, 7, true, EncodingType.k4X);
    	encoderL.reset();
    	encoderR.reset();
    	encoderElevator.reset(); 
    	teleOpOnce = true;
    	autoMode = 0;
    	speedMultiplier = 1;
    	turnRad = 0.74;
    	goOnce = true;
    }
    	//controller 1 list defines
        List<Boolean> J1B1 = new ArrayList<Boolean>();
        List<Boolean> J1B2 = new ArrayList<Boolean>();
        List<Boolean> J1B3 = new ArrayList<Boolean>();
        List<Boolean> J1B4 = new ArrayList<Boolean>();
        List<Boolean> J1B5 = new ArrayList<Boolean>();
        List<Boolean> J1B6 = new ArrayList<Boolean>();
        List<Boolean> J1B7 = new ArrayList<Boolean>();
        List<Boolean> J1B8 = new ArrayList<Boolean>();
        List<Boolean> J1B9 = new ArrayList<Boolean>();
        List<Boolean> J1B10 = new ArrayList<Boolean>();
        List<Double> J1Ljoy = new ArrayList<Double>();
        List<Double> J1Rjoy = new ArrayList<Double>();
        
      //controller 2 list defines
        List<Boolean> J2B1 = new ArrayList<Boolean>();
        List<Boolean> J2B2 = new ArrayList<Boolean>();
        List<Boolean> J2B3 = new ArrayList<Boolean>();
        List<Boolean> J2B4 = new ArrayList<Boolean>();
        List<Boolean> J2B5 = new ArrayList<Boolean>();
        List<Boolean> J2B6 = new ArrayList<Boolean>();
        List<Boolean> J2B7 = new ArrayList<Boolean>();
        List<Boolean> J2B8 = new ArrayList<Boolean>();
        List<Boolean> J2B9 = new ArrayList<Boolean>();
        List<Boolean> J2B10 = new ArrayList<Boolean>();
        List<Double> J2Ljoy = new ArrayList<Double>();
        List<Double> J2Rjoy = new ArrayList<Double>();
        List<Double> J2Ltrig = new ArrayList<Double>(); //down
        List<Double> J2Rtrig = new ArrayList<Double>(); //up
        
        int loops = 0;
    
    	//autoMode = prefs.getInt("Auto Mode", 0); // Determining which auto mode should be used from the preferences table on SmartDashboard
    

    
    public void autonomousInit()
    {
    	 
    }
    
    /**
    * This function is called periodically [20 ms] during autonomous
    */
    
    public void autonomousPeriodic()
    {
    	
    	if(goOnce) // Allows Auto to run only once instead of 20x per second
    	{
       		elevatorThread2Auto.schedule(new TimerTask(){public void run(){elevatorLow();}}, 20, 20); //Starting Threads for auto
    		elevatorThreadAuto.schedule(new TimerTask(){public void run(){elevatorOneTote();}}, 20, 20);
    		sensorThread.schedule(new TimerTask(){public void run(){getSensors();}}, 20, 20);
    		camThreadAuto.schedule(new TimerTask(){public void run(){camSetpoint();}}, 20, 20);
    		
    		if(autoMode == 0)
    		{
    			goOnce = false;
    			nothingAuto();
    		}
    		
    		if(autoMode == 1)
    		{
    			goOnce = false;
    			moveToZoneAuto();
    		}
    		
    		if(autoMode == 2)
    		{
    			goOnce = false;
    			oneToteAuto();
    		}
    		
    		if(autoMode == 3)
    		{
    			goOnce = false;
    			oneBinAuto();
    		}
    		
    		if(autoMode == 4)
    		{
    			goOnce = false;
    			Testing();
    		}
    		
    		if(autoMode == 6)
    		{
    			goOnce = false;
    			binJackerAuto();     
    		}
    		
    		if(autoMode == 7)
    		{
    			goOnce = false;
    			threeToteAuto();
    		}
    	}
    }

    
     //This function is called periodically [20 ms] during operator control
    
	public void teleopPeriodic() 
    {
		if(teleOpOnce) // Everything that should only be run once goes in here
		{
			elevatorThread.schedule(new TimerTask(){public void run(){elevatorOneTote();}}, 20, 20); // Starting threads
			elevatorThread2.schedule(new TimerTask(){public void run(){elevatorLow();}}, 20, 20);
			sensorThread.schedule(new TimerTask(){public void run(){getSensors();}}, 20, 20);
			recording.schedule(new TimerTask(){public void run(){record();}}, 20, 20);
			
			teleOpOnce = false; // Ending if statement so it only runs once
		}
		
    	arcadeDrive();
    	
    	armMotors();
    	
    	elevator();
    	
    	buttonToggles();
    	
    	camFullManual();
    	
    	camSetpoint();
    	
    	smartDashboard();
    	
    }

     //This function is called periodically during test mode
     
    public void testPeriodic() 
    {
    	
    }

//----------------------------------------------------------------------------------------------------------------------------------\\
   
    //Teleop methods
    
    public void smartDashboard()
    {
    	//Printing info for the smartdashboard
    	
    	SmartDashboard.putNumber("Elevator Encoder", elevatorR); 
    	SmartDashboard.putNumber("Cam Potentiometer", potDegrees); 
    	SmartDashboard.putBoolean("High Limit Switch", elevatorMax);   
    	SmartDashboard.putBoolean("Low Limit Switch", elevatorMin);   
    	SmartDashboard.putString(gearPos, gearPos2);
    	SmartDashboard.putNumber("Speed Multiplier", speedMultiplier);
    	SmartDashboard.putNumber("Turn Multiplier", turnRad);
    }
    
    public void elevatorLow()
    {
    	if (joy2.getRawButton(3) == false) {stillPressed7 = false;}
    	
    	if (joy2.getRawButton(3) && (stillPressed7 == false))
    	{
    		gotoSpot2 = true;
    		gotoCam1 = false;
			gotoCam2 = true;
    		camActivate = true;
    		stillPressed7 = true;
    	}
    	
    	if (gotoSpot2)
    	{

    		leftArm.set(DoubleSolenoid.Value.kForward);
			rightArm.set(DoubleSolenoid.Value.kForward);
    		
    		if ((elevatorMin) && (elevatorR >= 1100))
    		{
    			canWinch.set(0.4);
    			canWinch2.set(0.4);
    		}
    		
    		else if((elevatorMin) && (elevatorR < 1100))
    		{
    			canWinch.set(0.2);
    			canWinch2.set(0.2);
    		}
    		
    		else 
    		{
    			canWinch.set(0);
    			canWinch2.set(0);
    			gotoSpot2=false;
   			}
    	}
    }
    
    public void elevatorHigh()
    {
    	if (joy2.getRawButton(2) == false) {stillPressed9 = false;}
    	
    	if (joy2.getRawButton(2) && (stillPressed9 == false))
    	{
    		gotoSpot3 = true;
    		gotoCam1 = true;
			gotoCam2 = false;
    		camActivate = true;	
    		stillPressed9 = true;
    	}
    	
    	if (gotoSpot3)
    	{

    		leftArm.set(DoubleSolenoid.Value.kForward);
			rightArm.set(DoubleSolenoid.Value.kForward);
    		
    		if ((elevatorMax) && (elevatorR <= 7900))
    		{
    			canWinch.set(-0.8);
    			canWinch2.set(0.8);
    		}
    		else if((elevatorMax) && (elevatorR > 7900))
    		{
    			canWinch.set(-0.33);
    			canWinch2.set(0.33);
    		}
    		else {
    			gotoSpot2=false;
    		}
    	}
    }
    
    public void elevatorOneTote()
    {
    	if (joy2.getRawButton(4) == false) {stillPressed6 = false;}
    	
    	if (joy2.getRawButton(4) && (stillPressed6 == false))
    	{
    		gotoSpot = true;
    		gotoCam1 = true;
			gotoCam2 = false;
    		camActivate = true;
    		stillPressed6 = true;
    		
    	}
    	
    	if (gotoSpot)
    	{
    
    		leftArm.set(DoubleSolenoid.Value.kForward);
			rightArm.set(DoubleSolenoid.Value.kForward);
    		
    		if (elevatorR < 10768)
    		{
    			canWinch.set(-1);
    			canWinch2.set(-1);
    		}
    		else 
    		{
    			canWinch.set(0);
    			canWinch2.set(0);
    			gotoSpot=false;
    		}
    	}
    }
    
    public void camFullManual()
    {
    	//If cam manual is allowed, use the select button to move it in only one direction
    	
    	if(camMode == 2)
    	{
    		if(joy.getRawButton(8) == false)
    		{
    			talKicker.set(0);
    		}
    	
    		if(joy.getRawButton(8) == true)
    		{
    			talKicker.set(-1);
    		}

    	}
    }
    
    public void buttonToggles()
    
    {
    	//Arcade mode speed switcher
    	
    	if (joy.getRawButton(3) == false) {stillPressed10 = false;}
    	
    	if (joy.getRawButton(3) && (stillPressed10 == false))
    	{
    		if (speedMultiplier == 1)
			{
				speedMultiplier = 0.4;
				turnRad = 1.8;
				stillPressed10 = true;
				
			}
    		else if (speedMultiplier == 0.4)
    		{
    			speedMultiplier = 1;
    			turnRad = 0.74;
    			stillPressed10 = true;
    		}
    		
    	}
    	
    	//Smartdashboard gear position string changer
    	
    	if(gearShift.get() == DoubleSolenoid.Value.kForward)
    	{
    		gearPos2 = "High Gear";
    	}
    	if(gearShift.get() == DoubleSolenoid.Value.kReverse)
    	{
    		gearPos2 = "Low Gear";
    	}
    	
    	//Cam Setpoint Toggle
    	
    	if (joy2.getRawButton(1) == false) {stillPressed5 = false;}
    	
    	if (joy2.getRawButton(1) && (stillPressed5 == false))
    	{
    		if (gotoCam1)
			{
				gotoCam1 = false;
				
			}
    		else if (!gotoCam1)
    		{
    			gotoCam1 = true;
    		}
    		
    		camActivate = true;
    		stillPressed5 = true;
    	}
    
    	//Cam Mode Switching [RB]
    	
    	if (joy2.getRawButton(6) == false) {stillPressed8 = false;}
    	
    	if (joy2.getRawButton(6) && (stillPressed8 == false))
    	{
    		if (camMode == 1)
    			{
    				camMode = 2;
    			}
    		else if(camMode == 2)
    		{
    			camMode = 1;
    		}
    	}
    	
    	//Gear Shifting [Right Thumbstick Button]
    	
    	if (joy.getRawButton(2) == false) {stillPressed = false;}
    	
    	if (joy.getRawButton(2) && (stillPressed == false))
    	{	
    		if (gearShift.get() == DoubleSolenoid.Value.kForward)
    			{
    			gearShift.set(DoubleSolenoid.Value.kReverse);  		
    			stillPressed=true;
    			}
    		else
    		{
    			gearShift.set(DoubleSolenoid.Value.kForward);
    			stillPressed=true;
    		}
    	}
    	
    	//Arms Toggle [LB] Controller 1
    	
    	if (joy.getRawButton(5) == false) {stillPressed2 = false;}
    	
    	if (joy.getRawButton(5) && (stillPressed2 == false))
    	{
    		if ((leftArm.get() == DoubleSolenoid.Value.kForward) && (rightArm.get() == DoubleSolenoid.Value.kForward))
   			{
   				leftArm.set(DoubleSolenoid.Value.kReverse);  
   				rightArm.set(DoubleSolenoid.Value.kReverse);
   				stillPressed2=true;
   			}
    		else 
    		{
    			leftArm.set(DoubleSolenoid.Value.kForward);
    			rightArm.set(DoubleSolenoid.Value.kForward);
    			stillPressed2=true;
    		}
    	}
    	
    	//Arm Toggle [LB] Controller 2
    	if (joy2.getRawButton(5) == false) {stillPressed4 = false;}
		
    	if (joy2.getRawButton(5) && (stillPressed4 == false))
    	{
    		if ((leftArm.get() == DoubleSolenoid.Value.kForward) && (rightArm.get() == DoubleSolenoid.Value.kForward))
   			{
   				leftArm.set(DoubleSolenoid.Value.kReverse);  
   				rightArm.set(DoubleSolenoid.Value.kReverse);
   				stillPressed4=true;
   			}
    		else 
    		{
    			leftArm.set(DoubleSolenoid.Value.kForward);
    			rightArm.set(DoubleSolenoid.Value.kForward);
    			stillPressed4=true;
    		}
    	}
    	
    	//Stinger [RB]
    	
    		if (joy.getRawButton(1) == false) {stillPressed3 = false;}
    		
    		if (joy.getRawButton(1) && (stillPressed3 == false))
    		{
    			if (flipper.get() == DoubleSolenoid.Value.kForward)
    			{
    				flipper.set(DoubleSolenoid.Value.kReverse);  		
    				stillPressed3=true;
    			}
    			else
    			{
    				flipper.set(DoubleSolenoid.Value.kForward);
    				stillPressed3=true;
    			}
    		}	
    }
       
    public void camSetpoint()
    {
    	//If cam is in setpoint mode, switch positions using the pot
    	
    	if ((camActivate) && (camMode == 1))
    	{
    		if(gotoCam1)
    		{

        		if (potDegrees < 2.91)
        		{
        			talKicker.set(-1);
        		}
     
        		else 
        		{
        			talKicker.set(0);
        			camActivate=false;
        		}
    		}
    		
    		if(!gotoCam1)
    		{

    			if (potDegrees > 2.5)
        		{
        			talKicker.set(1);
        		}
        		
        		else 
        		{
        			talKicker.set(0);
        			camActivate=false;
        		}
    		}
    		
    	}
    }
    
    public void arcadeDrive()
    {
    	//Assign the xbox values to variables
    	
    	rightThumb = joy.getRawAxis(4);
    	
    	leftThumb = -(joy.getRawAxis(1));
    	
    	//Define the speed multiplier, deadzones and turning radius multiplier
     	
    	deadZ = 0.25;
    	
    	//If left thumbstick is still
    	
    	if((leftThumb < deadZ) && (leftThumb > -deadZ))
    	{
    		canFL.set(((-(rightThumb * turnRad))) * speedMultiplier);
    		canBL.set(((-(rightThumb * turnRad))) * speedMultiplier);
    		
    		canBR.set(((-(rightThumb * turnRad))) * speedMultiplier);
    		canFR.set(((-(rightThumb * turnRad))) * speedMultiplier);
    	}
    	
    	//If right thumbstick is still
    	
    	if((rightThumb < deadZ) && (rightThumb > -deadZ))
    	{
    		canFL.set(((-leftThumb)) * speedMultiplier);
    		canBL.set(((-leftThumb)) * speedMultiplier);
    		
    		canBR.set(((leftThumb)) * speedMultiplier);
    		canFR.set(((leftThumb)) * speedMultiplier);
    	}
    	
    	//If both thumbsticks are positive
    	
    	if((leftThumb > deadZ) && (rightThumb > deadZ))
    	{
    		canFL.set(((-leftThumb)) * speedMultiplier);
    		canBL.set(((-leftThumb)) * speedMultiplier);
    		
    		canBR.set(((leftThumb - (rightThumb * turnRad))) * speedMultiplier);
    		canFR.set(((leftThumb - (rightThumb * turnRad))) * speedMultiplier);
    	}
    	
    	//If left thumbstick is positive and right thumbstick is negative
    	
    	if((leftThumb > deadZ) && (rightThumb < -deadZ))
    	{
    		canFL.set(((-(leftThumb + (rightThumb * turnRad)))) * speedMultiplier);
    		canBL.set(((-(leftThumb + (rightThumb * turnRad)))) * speedMultiplier);
		
    		canBR.set(((leftThumb)) * speedMultiplier);
    		canFR.set(((leftThumb)) * speedMultiplier);
    	}
    	
    	//If left thumbstick is negative and right thumbstick is positive
    	
    	if((leftThumb < -deadZ) && (rightThumb > deadZ))
    	{
    		canFL.set(((-(leftThumb + (rightThumb * turnRad)))) * speedMultiplier);
    		canBL.set(((-(leftThumb + (rightThumb * turnRad)))) * speedMultiplier);
    		
    		canBR.set(((leftThumb)) * speedMultiplier);
    		canFR.set(((leftThumb)) * speedMultiplier);
    	}
    	
    	//If left thumbstick is negative and right thumbstick is negative
    	
    	if((leftThumb < -deadZ) && (rightThumb < -deadZ))
    	{
    		canFL.set(((-leftThumb)) * speedMultiplier);
    		canBL.set(((-leftThumb)) * speedMultiplier);
    		
    		canBR.set(((leftThumb - (rightThumb * turnRad))) * speedMultiplier);
    		canFR.set(((leftThumb - (rightThumb * turnRad))) * speedMultiplier);
    	}

    }
       
    public void armMotors()
    {
    	//Arm motors
    	
    	//Assign xbox values to variables
    	
		leftThumb2 = (joy2.getRawAxis(1));
    	rightThumb2 = (joy2.getRawAxis(4));
    	
    	deadZ2 = 0.17;
    	
    	//If left thumbstick is still

    	if((leftThumb2>-deadZ2) && (leftThumb2<deadZ2)) 
    	{
    		talArmLeft.set(-(rightThumb2));

    		talArmRight.set(-(rightThumb2));
    	}

    	//If right thumbstick is still

    	if((rightThumb2>-deadZ2) && (rightThumb2<deadZ2)) 
    	{
    		talArmLeft.set(leftThumb2);

    		talArmRight.set(-leftThumb2);
    	}

    	//If left thumbstick is positive and right thumbstick is positive

    	if((leftThumb2>deadZ2) && (rightThumb2>deadZ2)) 
    	{
    		talArmLeft.set(leftThumb2 - (rightThumb2 * 0.9));

    		talArmRight.set(-(leftThumb2));
    	}

    	//If left thumbstick is positive and right thumbstick is negative

    	if((leftThumb2>deadZ2) && (rightThumb2<-deadZ2)) 
    	{
    		talArmLeft.set(leftThumb2);

    		talArmRight.set(-(leftThumb2 + (rightThumb2 * 0.9)));
    	}

    	//If left thumbstick is negative and right thumbstick is positive

    	if((leftThumb2<-deadZ2) && (rightThumb2>deadZ2)) 
    	{
    		talArmLeft.set(leftThumb2 + (rightThumb2 * 0.9));

    		talArmRight.set(-(leftThumb2));
    	}

    	//If left thumbstick is negative and right thumbstick is negative

    	if((leftThumb2<-deadZ2) && (rightThumb2<-deadZ2)) 
    	{
    		talArmLeft.set(leftThumb2);

    		talArmRight.set(-(leftThumb2 - (rightThumb2 * 0.9))); 	
    	}
    }
       
    public void elevator()
    	
    {
    	
    	//Elevator Motors [Y = Up B = Down]
    	
     		if((joy2.getRawAxis(3) > 0) && (joy2.getRawAxis(2) > 0))
        	{
        		canWinch.set(0);
        		canWinch2.set(0);
        	}
     		
     		if((joy2.getRawAxis(3) < 0.01) && (joy2.getRawAxis(2) < 0.01))
        	{
        		canWinch.set(0);
        		canWinch2.set(0);
        	}
     		
        	if((joy2.getRawAxis(3) > 0) && (joy2.getRawAxis(2) < 0.01) && (elevatorMax == true))
        	{
        		elevatorManual = true;
        		gotoSpot=false;
        		canWinch.set(-(joy2.getRawAxis(3)));
        		canWinch2.set(-(joy2.getRawAxis(3)));
        	}
        	
        	if((joy2.getRawAxis(3) > 0) && (joy2.getRawAxis(2) < 0.01) && (!elevatorMax))
        	{
        		canWinch.set(0);
        		canWinch2.set(0);
        	}
        	
        	if((joy2.getRawAxis(3) < 0.001) && (joy2.getRawAxis(2) > 0) && (elevatorMin == true))
        	{
        		elevatorManual = true;
        		gotoSpot=false;
        		canWinch.set(joy2.getRawAxis(2));
        		canWinch2.set(joy2.getRawAxis(2));
        	}
        	
        	if((joy2.getRawAxis(3) < 0.001) && (joy2.getRawAxis(2) > 0) && (!elevatorMin))
        	{
        		canWinch.set(0);
        		canWinch2.set(0);
        	}
        	
        //Use button on the first controller to reset the elevator encoder, useful for not fucking up the threaded rod :P
        	
    	if(joy.getRawButton(7) == true)
    	{
    		encoderElevator.reset();
    	}
    }

//----------------------------------------------------------------------------------------------------------------------------------\\
   
    //Auto Mode methods
    
    public void getSensors()
    {
    	//Gets the absolute value of the drivetrain encoders
    	
    	leftR = Math.abs(encoderR.get());
    	rightR = Math.abs(encoderL.get());
    	
    	//Gets the regular values of everything else
    	
    	elevatorR = (-encoderElevator.get());
    	potDegrees = pot1.getVoltage();
    	elevatorMin = limit2.get();
    	elevatorMax = limit1.get();
    	
    	//Prints them to the smartdashboard
    	
    	SmartDashboard.putNumber("Left Encoder", leftR);
    	SmartDashboard.putNumber("Right Encoder", rightR);
    	SmartDashboard.putNumber("Elevator Encoder", elevatorR);
    }
    
    public void drive(int distance, double power)
    {
    	//Sets the motors to the given power
    	
    	canFL.set(-power);
		canBL.set(-power);
		
		canBR.set(power);
		canFR.set(power);
		
		//Waits for the given time
		
		try {
			Thread.sleep(distance);
		} catch (InterruptedException e) {
			
			Thread.currentThread().interrupt();
		}
		
		//Stops
    	
    	canFL.set(0);
		canBL.set(0);
		
		canBR.set(0);
		canFR.set(0);
	
    }
    
    public void armsClose()
    {
    	//Sets the arms solenoids to open
    	
    	leftArm.set(DoubleSolenoid.Value.kReverse);  
		rightArm.set(DoubleSolenoid.Value.kReverse);
    	
    }
    
    public void armsOpen()
    {
    	//Sets the arms solenoids to closed
    	leftArm.set(DoubleSolenoid.Value.kForward);  
		rightArm.set(DoubleSolenoid.Value.kForward);
    	
    }
    
    public void leftTurn90(double power)
    {
    	//Sets the drivetrain motors to the given power to make a right angle turn
    	
    	while(rightR < 550)
    	{
    		canFL.set(power);
    		canBL.set(power);
    		
    		canBR.set(power);
    		canFR.set(power);
    	}
    	canFL.set(0);
		canBL.set(0);
		
		canBR.set(0);
		canFR.set(0);
		
		encoderL.reset();
    
    }
    
    public void rightTurn90(double power)
    {
    	//Sets the drivetrain motors to the given power to make a right angle turn
    	
    	while(rightR < 550)
    	{
    		canFL.set(-power);
    		canBL.set(-power);
    		
    		canBR.set(-power);
    		canFR.set(-power);
    	}
    	canFL.set(0);
		canBL.set(0);
		
		canBR.set(0);
		canFR.set(0);
		
		encoderL.reset();
    }
    
    public void leftTurn45(double power)
    {
    	//Sets the drivetrain motors to the given power to make a 45 degree turn
    	
    	canFL.set(power);
		canBL.set(power);
		
		canBR.set(power);
		canFR.set(power);
		
		//Waits for the given time
		
		try {
			Thread.sleep(353);
		} catch (InterruptedException e) {
			
			Thread.currentThread().interrupt();
		}
		
		//Stops
    	
    	canFL.set(0);
		canBL.set(0);
		
		canBR.set(0);
		canFR.set(0);
    }
    
    public void rightTurn45(double power)
    {
    	//Sets the drivetrain motors to the given power to make a 45 degree turn
    	
    	canFL.set(-power);
		canBL.set(-power);
		
		canBR.set(-power);
		canFR.set(-power);
		
		//Waits for the given time
		
		try {
			Thread.sleep(353);
		} catch (InterruptedException e) {
			
			Thread.currentThread().interrupt();
		}
		
		//Stops
    	
    	canFL.set(0);
		canBL.set(0);
		
		canBR.set(0);
		canFR.set(0);
    }
    
    public void setTurn(double turnDegrees, double power)
    {
    	//Sets the drivetrain motors to the given power to make a right angle turn
	  
    	while(rightR < (turnDegrees * 6.1111))
    	{
    		canFL.set(power);
    		canBL.set(power);
    		
    		canBR.set(power);
    		canFR.set(power);
    	}
    	
    	canFL.set(0);
		canBL.set(0);
		
		canBR.set(0);
		canFR.set(0);
		
		encoderL.reset();
    } 

    public void moveArms(int time, int power)
    {
    	//Moves arms ar given power
    	
    	talArmLeft.set(-power);
    	talArmRight.set(power);
    		
    	//Waits for the given time
    	
    	try {
    			Thread.sleep(time);
    	} catch (InterruptedException e) {
    		
   			Thread.currentThread().interrupt();
   		}
    	
    	//Stops
    	
    	talArmLeft.set(0);
		talArmRight.set(0);
    }
    
    public void stingerOut()
    {
    	//Sets the stinger solenoid to out
    	
    	flipper.set(DoubleSolenoid.Value.kForward);
    }
    
    public void stingerIn()
    {
    	//Sets the stinger solenoid to in
    	
    	flipper.set(DoubleSolenoid.Value.kReverse);
    }
    
    public void wait(int Milliseconds)
    {
    	//Pauses the thread for the given amount of time
    	
    	try {
			Thread.sleep(Milliseconds);
		} catch (InterruptedException e) {
			
			Thread.currentThread().interrupt();
		}
    }
    
    public void elevatorUp()
    {
    	gotoSpot = true;
		gotoCam1 = true;
		gotoCam2 = false;
		camActivate = true;
	
    }
    
    public void elevatorDown()
    {
    	gotoSpot2 = true;
		gotoCam1 = false;
		gotoCam2 = true;
		camActivate = true;
    }
    
//----------------------------------------------------------------------------------------------------------------------------------\\
    
    //Auto Modes
    
    public void Testing()
    {
    	
    }
    
    public void moveToZoneAuto()
    {
    	drive(750, -0.5);
    	
    	elevatorThreadAuto.cancel();
    	elevatorThread2Auto.cancel();
    	sensorThreadAuto.cancel();
    }
    
    public void oneToteAuto()
    {
    	armsClose();
    	
    	setTurn(90, -1);
    	
    	drive(1400, 0.5);
    	
    	armsOpen();
    	
    	elevatorThreadAuto.cancel();
    	elevatorThread2Auto.cancel();
    	sensorThreadAuto.cancel();
    }
    
    public void oneBinAuto()
    {
    	armsClose();
    	
    	setTurn(90, 1);
    	
    	drive(1400, 0.6);
    	
    	armsOpen();
    	
    	elevatorThreadAuto.cancel();
    	elevatorThread2Auto.cancel();
    	sensorThreadAuto.cancel();
    }
    
    public void nothingAuto()
    {
    	elevatorThreadAuto.cancel();
    	elevatorThread2Auto.cancel();
    	sensorThreadAuto.cancel();
    }
    
    public void threeToteAuto()
    {
    	
    }
  
    public void binJackerAuto()
    {
    	drive(598, -0.7);
		
		wait(900);
		
    	stingerOut();
    	
    	wait(1000);
    	
    	drive(800, 1);
    	
		wait(700);
		
    	stingerIn();
    	
    	elevatorThreadAuto.cancel();
    	elevatorThread2Auto.cancel();
    	sensorThreadAuto.cancel();
    }
    public void record(){
    	//controller 1 adding value to list
        J1B1.add(joy.getRawButton(1));
        J1B2.add(joy.getRawButton(2));
        J1B3.add(joy.getRawButton(3));
        J1B4.add(joy.getRawButton(4));
        J1B5.add(joy.getRawButton(5));
        J1B6.add(joy.getRawButton(6));
        J1B7.add(joy.getRawButton(7));
        J1B8.add(joy.getRawButton(8));
        J1B9.add(joy.getRawButton(9));
        J1B10.add(joy.getRawButton(10));
        J1Rjoy.add(joy.getRawAxis(4));
        J1Ljoy.add(joy.getRawAxis(1));
        
        //controller 2 adding value to list
        J2B1.add(joy.getRawButton(1));
        J2B2.add(joy.getRawButton(2));
        J2B3.add(joy.getRawButton(3));
        J2B4.add(joy.getRawButton(4));
        J2B5.add(joy.getRawButton(5));
        J2B6.add(joy.getRawButton(6));
        J2B7.add(joy.getRawButton(7));
        J2B8.add(joy.getRawButton(8));
        J2B9.add(joy.getRawButton(9));
        J2B10.add(joy.getRawButton(10));
        J2Rjoy.add(joy2.getRawAxis(4));
        J2Ljoy.add(joy2.getRawAxis(1));
        J2Ltrig.add(joy2.getRawAxis(2));
        J2Rtrig.add(joy2.getRawAxis(3));
        
        
        loops++;
        if(loops > 4){
        	loops = 0;
        	
        	//printing controller 1 lists
        	System.out.println("joy1 button1: "+J1B1);   
        	System.out.println("joy1 button2: "+J1B2);
        	System.out.println("joy1 button3: "+J1B3);
        	System.out.println("joy1 button4: "+J1B4);
        	System.out.println("joy1 button5: "+J1B5);
        	System.out.println("joy1 button6: "+J1B6);
        	System.out.println("joy1 button7: "+J1B7);
        	System.out.println("joy1 button8: "+J1B8);
        	System.out.println("joy1 button9: "+J1B9);
        	System.out.println("joy1 button10: "+J1B10);
        	System.out.println("joy1 left stick: "+J1Ljoy);
        	System.out.println("joy1 right stick: "+J1Rjoy);
        	System.out.println();
        	
        	//printing controller 2 lists
        	System.out.println("joy2 button1: "+J2B1);   
        	System.out.println("joy2 button2: "+J2B2);
        	System.out.println("joy2 button3: "+J2B3);
        	System.out.println("joy2 button4: "+J2B4);
        	System.out.println("joy2 button5: "+J2B5);
        	System.out.println("joy2 button6: "+J2B6);
        	System.out.println("joy2 button7: "+J2B7);
        	System.out.println("joy2 button8: "+J2B8);
        	System.out.println("joy2 button9: "+J2B9);
        	System.out.println("joy2 button10: "+J2B10);
        	System.out.println("joy2 left stick: "+J2Ljoy);
        	System.out.println("joy2 right stick: "+J2Rjoy);
        	System.out.println("joy2 left trigger: "+J2Ltrig);
        	System.out.println("joy2 right trigger: "+J2Rtrig);
        	System.out.println();
        	
        	//clearing lists
        	J1B1.clear();
        	J1B2.clear();
        	J1B3.clear();
        	J1B4.clear();
        	J1B5.clear();
        	J1B6.clear();
        	J1B7.clear();
        	J1B8.clear();
        	J1B9.clear();
        	J1B10.clear();
        	J1Ljoy.clear();
        	J1Rjoy.clear();
        	
        	J2B1.clear();
        	J2B2.clear();
        	J2B3.clear();
        	J2B4.clear();
        	J2B5.clear();
        	J2B6.clear();
        	J2B7.clear();
        	J2B8.clear();
        	J2B9.clear();
        	J2B10.clear();
        	J2Ljoy.clear();
        	J2Rjoy.clear();
        	J2Ltrig.clear();
        	J2Rtrig.clear();
        }
    }
    
    public void playback(){
    	//J1B1.add(joy.getRawButton(1));
    	for (int i = 0; i < J1B1.size(); i++){
    		boolean buttonJ1B1 = J1B1.get(i);
    		boolean buttonJ1B2 = J1B2.get(i);
    		boolean buttonJ1B3 = J1B3.get(i);
    		boolean buttonJ1B4 = J1B4.get(i);
    		boolean buttonJ1B5 = J1B5.get(i);
    		boolean buttonJ1B6 = J1B6.get(i);
    		boolean buttonJ1B7 = J1B7.get(i);
    		boolean buttonJ1B8 = J1B8.get(i);
    		boolean buttonJ1B9 = J1B9.get(i);
    		boolean buttonJ1B10 = J1B10.get(i);
    		double buttonJ1Ljoy = J1Ljoy.get(i);
    		double buttonJ1Rjoy = J1Rjoy.get(i);
    		
    		boolean buttonJ2B1 = J2B1.get(i);
    		boolean buttonJ2B2 = J2B2.get(i);
    		boolean buttonJ2B3 = J2B3.get(i);
    		boolean buttonJ2B4 = J2B4.get(i);
    		boolean buttonJ2B5 = J2B5.get(i);
    		boolean buttonJ2B6 = J2B6.get(i);
    		boolean buttonJ2B7 = J2B7.get(i);
    		boolean buttonJ2B8 = J2B8.get(i);
    		boolean buttonJ2B9 = J2B9.get(i);
    		boolean buttonJ2B10 = J2B10.get(i);
    		double buttonJ2Ljoy = J2Ljoy.get(i);
    		double buttonJ2Rjoy = J2Rjoy.get(i);
    		double buttonJ2Ltrig = J2Ltrig.get(i);
    		double buttonJ2Rtrig = J2Ltrig.get(i);
    		
    		elevatorLowE();
    		elevatorHighE();
    		elevatorOneToteE();
    		camFullManualE();
    		buttonTogglesE();
    		arcadeDriveE();
    		armMotorsE();
    		elevatorE();
    		try {
				Thread.sleep(20);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
    	}
    	
    }
    
//emulator methods DO NOT TOUCH----------------------------------------------------------------------------------------------------------
    
    public void elevatorLowE()
    {
    	if (buttonJ2B3 == false) {stillPressed7 = false;}
    	
    	if (buttonJ2B3 && (stillPressed7 == false))
    	{
    		gotoSpot2 = true;
    		gotoCam1 = false;
			gotoCam2 = true;
    		camActivate = true;
    		stillPressed7 = true;
    	}
    	
    	if (gotoSpot2)
    	{

    		leftArm.set(DoubleSolenoid.Value.kForward);
			rightArm.set(DoubleSolenoid.Value.kForward);
    		
    		if ((elevatorMin) && (elevatorR >= 1100))
    		{
    			canWinch.set(0.4);
    			canWinch2.set(0.4);
    		}
    		
    		else if((elevatorMin) && (elevatorR < 1100))
    		{
    			canWinch.set(0.2);
    			canWinch2.set(0.2);
    		}
    		
    		else 
    		{
    			canWinch.set(0);
    			canWinch2.set(0);
    			gotoSpot2=false;
   			}
    	}
    }
    
    public void elevatorHighE()
    {
    	if (buttonJ2B2 == false) {stillPressed9 = false;}
    	
    	if (buttonJ2B2 && (stillPressed9 == false))
    	{
    		gotoSpot3 = true;
    		gotoCam1 = true;
			gotoCam2 = false;
    		camActivate = true;	
    		stillPressed9 = true;
    	}
    	
    	if (gotoSpot3)
    	{

    		leftArm.set(DoubleSolenoid.Value.kForward);
			rightArm.set(DoubleSolenoid.Value.kForward);
    		
    		if ((elevatorMax) && (elevatorR <= 7900))
    		{
    			canWinch.set(-0.8);
    			canWinch2.set(0.8);
    		}
    		else if((elevatorMax) && (elevatorR > 7900))
    		{
    			canWinch.set(-0.33);
    			canWinch2.set(0.33);
    		}
    		else {
    			gotoSpot2=false;
    		}
    	}
    }
    
    public void elevatorOneToteE()
    {
    	if (buttonJ2B4 == false) {stillPressed6 = false;}
    	
    	if (buttonJ2B4 && (stillPressed6 == false))
    	{
    		gotoSpot = true;
    		gotoCam1 = true;
			gotoCam2 = false;
    		camActivate = true;
    		stillPressed6 = true;
    		
    	}
    	
    	if (gotoSpot)
    	{
    
    		leftArm.set(DoubleSolenoid.Value.kForward);
			rightArm.set(DoubleSolenoid.Value.kForward);
    		
    		if (elevatorR < 10768)
    		{
    			canWinch.set(-1);
    			canWinch2.set(-1);
    		}
    		else 
    		{
    			canWinch.set(0);
    			canWinch2.set(0);
    			gotoSpot=false;
    		}
    	}
    }
    
    public void camFullManualE()
    {
    	//If cam manual is allowed, use the select button to move it in only one direction
    	
    	if(camMode == 2)
    	{
    		if(buttonJ1B8 == false)
    		{
    			talKicker.set(0);
    		}
    	
    		if(buttonJ1B8 == true)
    		{
    			talKicker.set(-1);
    		}

    	}
    }
    
    public void buttonTogglesE()
    
    {
    	//Arcade mode speed switcher
    	
    	if (buttonJ1B3 == false) {stillPressed10 = false;}
    	
    	if (buttonJ1B3 && (stillPressed10 == false))
    	{
    		if (speedMultiplier == 1)
			{
				speedMultiplier = 0.4;
				turnRad = 1.8;
				stillPressed10 = true;
				
			}
    		else if (speedMultiplier == 0.4)
    		{
    			speedMultiplier = 1;
    			turnRad = 0.74;
    			stillPressed10 = true;
    		}
    		
    	}
    	
    	//Smartdashboard gear position string changer
    	
    	if(gearShift.get() == DoubleSolenoid.Value.kForward)
    	{
    		gearPos2 = "High Gear";
    	}
    	if(gearShift.get() == DoubleSolenoid.Value.kReverse)
    	{
    		gearPos2 = "Low Gear";
    	}
    	
    	//Cam Setpoint Toggle
    	
    	if (buttonJ2B1 == false) {stillPressed5 = false;}
    	
    	if (buttonJ2B1 && (stillPressed5 == false))
    	{
    		if (gotoCam1)
			{
				gotoCam1 = false;
				
			}
    		else if (!gotoCam1)
    		{
    			gotoCam1 = true;
    		}
    		
    		camActivate = true;
    		stillPressed5 = true;
    	}
    
    	//Cam Mode Switching [RB]
    	
    	if (buttonJ2B6 == false) {stillPressed8 = false;}
    	
    	if (buttonJ2B6 && (stillPressed8 == false))
    	{
    		if (camMode == 1)
    			{
    				camMode = 2;
    			}
    		else if(camMode == 2)
    		{
    			camMode = 1;
    		}
    	}
    	
    	//Gear Shifting [Right Thumbstick Button]
    	
    	if (buttonJ1B2 == false) {stillPressed = false;}
    	
    	if (buttonJ1B2 && (stillPressed == false))
    	{	
    		if (gearShift.get() == DoubleSolenoid.Value.kForward)
    			{
    			gearShift.set(DoubleSolenoid.Value.kReverse);  		
    			stillPressed=true;
    			}
    		else
    		{
    			gearShift.set(DoubleSolenoid.Value.kForward);
    			stillPressed=true;
    		}
    	}
    	
    	//Arms Toggle [LB] Controller 1
    	
    	if (buttonJ1B5 == false) {stillPressed2 = false;}
    	
    	if (buttonJ1B5 && (stillPressed2 == false))
    	{
    		if ((leftArm.get() == DoubleSolenoid.Value.kForward) && (rightArm.get() == DoubleSolenoid.Value.kForward))
   			{
   				leftArm.set(DoubleSolenoid.Value.kReverse);  
   				rightArm.set(DoubleSolenoid.Value.kReverse);
   				stillPressed2=true;
   			}
    		else 
    		{
    			leftArm.set(DoubleSolenoid.Value.kForward);
    			rightArm.set(DoubleSolenoid.Value.kForward);
    			stillPressed2=true;
    		}
    	}
    	
    	//Arm Toggle [LB] Controller 2
    	if (buttonJ2B5 == false) {stillPressed4 = false;}
		
    	if (buttonJ2B5 && (stillPressed4 == false))
    	{
    		if ((leftArm.get() == DoubleSolenoid.Value.kForward) && (rightArm.get() == DoubleSolenoid.Value.kForward))
   			{
   				leftArm.set(DoubleSolenoid.Value.kReverse);  
   				rightArm.set(DoubleSolenoid.Value.kReverse);
   				stillPressed4=true;
   			}
    		else 
    		{
    			leftArm.set(DoubleSolenoid.Value.kForward);
    			rightArm.set(DoubleSolenoid.Value.kForward);
    			stillPressed4=true;
    		}
    	}
    	
    	//Stinger [RB]
    	
    		if (buttonJ1B1 == false) {stillPressed3 = false;}
    		
    		if (buttonJ1B1 && (stillPressed3 == false))
    		{
    			if (flipper.get() == DoubleSolenoid.Value.kForward)
    			{
    				flipper.set(DoubleSolenoid.Value.kReverse);  		
    				stillPressed3=true;
    			}
    			else
    			{
    				flipper.set(DoubleSolenoid.Value.kForward);
    				stillPressed3=true;
    			}
    		}	
    }
              
    public void arcadeDriveE()
    {
    	//Assign the xbox values to variables
    	
    	rightThumb = buttonJ1Rjoy;
    	
    	leftThumb = -buttonJ1Ljoy;
    	
    	//Define the speed multiplier, deadzones and turning radius multiplier
     	
    	deadZ = 0.25;
    	
    	//If left thumbstick is still
    	
    	if((leftThumb < deadZ) && (leftThumb > -deadZ))
    	{
    		canFL.set(((-(rightThumb * turnRad))) * speedMultiplier);
    		canBL.set(((-(rightThumb * turnRad))) * speedMultiplier);
    		
    		canBR.set(((-(rightThumb * turnRad))) * speedMultiplier);
    		canFR.set(((-(rightThumb * turnRad))) * speedMultiplier);
    	}
    	
    	//If right thumbstick is still
    	
    	if((rightThumb < deadZ) && (rightThumb > -deadZ))
    	{
    		canFL.set(((-leftThumb)) * speedMultiplier);
    		canBL.set(((-leftThumb)) * speedMultiplier);
    		
    		canBR.set(((leftThumb)) * speedMultiplier);
    		canFR.set(((leftThumb)) * speedMultiplier);
    	}
    	
    	//If both thumbsticks are positive
    	
    	if((leftThumb > deadZ) && (rightThumb > deadZ))
    	{
    		canFL.set(((-leftThumb)) * speedMultiplier);
    		canBL.set(((-leftThumb)) * speedMultiplier);
    		
    		canBR.set(((leftThumb - (rightThumb * turnRad))) * speedMultiplier);
    		canFR.set(((leftThumb - (rightThumb * turnRad))) * speedMultiplier);
    	}
    	
    	//If left thumbstick is positive and right thumbstick is negative
    	
    	if((leftThumb > deadZ) && (rightThumb < -deadZ))
    	{
    		canFL.set(((-(leftThumb + (rightThumb * turnRad)))) * speedMultiplier);
    		canBL.set(((-(leftThumb + (rightThumb * turnRad)))) * speedMultiplier);
		
    		canBR.set(((leftThumb)) * speedMultiplier);
    		canFR.set(((leftThumb)) * speedMultiplier);
    	}
    	
    	//If left thumbstick is negative and right thumbstick is positive
    	
    	if((leftThumb < -deadZ) && (rightThumb > deadZ))
    	{
    		canFL.set(((-(leftThumb + (rightThumb * turnRad)))) * speedMultiplier);
    		canBL.set(((-(leftThumb + (rightThumb * turnRad)))) * speedMultiplier);
    		
    		canBR.set(((leftThumb)) * speedMultiplier);
    		canFR.set(((leftThumb)) * speedMultiplier);
    	}
    	
    	//If left thumbstick is negative and right thumbstick is negative
    	
    	if((leftThumb < -deadZ) && (rightThumb < -deadZ))
    	{
    		canFL.set(((-leftThumb)) * speedMultiplier);
    		canBL.set(((-leftThumb)) * speedMultiplier);
    		
    		canBR.set(((leftThumb - (rightThumb * turnRad))) * speedMultiplier);
    		canFR.set(((leftThumb - (rightThumb * turnRad))) * speedMultiplier);
    	}

    }
       
    public void armMotorsE()
    {
    	//Arm motors
    	
    	//Assign xbox values to variables
    	
		leftThumb2 = buttonJ2Ljoy;
    	rightThumb2 = buttonJ2Rjoy;
    	
    	deadZ2 = 0.17;
    	
    	//If left thumbstick is still

    	if((leftThumb2>-deadZ2) && (leftThumb2<deadZ2)) 
    	{
    		talArmLeft.set(-(rightThumb2));

    		talArmRight.set(-(rightThumb2));
    	}

    	//If right thumbstick is still

    	if((rightThumb2>-deadZ2) && (rightThumb2<deadZ2)) 
    	{
    		talArmLeft.set(leftThumb2);

    		talArmRight.set(-leftThumb2);
    	}

    	//If left thumbstick is positive and right thumbstick is positive

    	if((leftThumb2>deadZ2) && (rightThumb2>deadZ2)) 
    	{
    		talArmLeft.set(leftThumb2 - (rightThumb2 * 0.9));

    		talArmRight.set(-(leftThumb2));
    	}

    	//If left thumbstick is positive and right thumbstick is negative

    	if((leftThumb2>deadZ2) && (rightThumb2<-deadZ2)) 
    	{
    		talArmLeft.set(leftThumb2);

    		talArmRight.set(-(leftThumb2 + (rightThumb2 * 0.9)));
    	}

    	//If left thumbstick is negative and right thumbstick is positive

    	if((leftThumb2<-deadZ2) && (rightThumb2>deadZ2)) 
    	{
    		talArmLeft.set(leftThumb2 + (rightThumb2 * 0.9));

    		talArmRight.set(-(leftThumb2));
    	}

    	//If left thumbstick is negative and right thumbstick is negative

    	if((leftThumb2<-deadZ2) && (rightThumb2<-deadZ2)) 
    	{
    		talArmLeft.set(leftThumb2);

    		talArmRight.set(-(leftThumb2 - (rightThumb2 * 0.9))); 	
    	}
    }
       
    public void elevatorE()
    	
    {
    	
    	//Elevator Motors [Y = Up B = Down]
    	
     		if((buttonJ2Rtrig > 0) && (buttonJ2Ltrig > 0))
        	{
        		canWinch.set(0);
        		canWinch2.set(0);
        	}
     		
     		if((buttonJ2Rtrig < 0.01) && (buttonJ2Ltrig < 0.01))
        	{
        		canWinch.set(0);
        		canWinch2.set(0);
        	}
     		
        	if((buttonJ2Rtrig > 0) && (buttonJ2Ltrig < 0.01) && (elevatorMax == true))
        	{
        		elevatorManual = true;
        		gotoSpot=false;
        		canWinch.set(-(joy2.getRawAxis(3)));
        		canWinch2.set(-(joy2.getRawAxis(3)));
        	}
        	
        	if((buttonJ2Rtrig > 0) && (buttonJ2Ltrig < 0.01) && (!elevatorMax))
        	{
        		canWinch.set(0);
        		canWinch2.set(0);
        	}
        	
        	if((buttonJ2Rtrig < 0.001) && (buttonJ2Ltrig > 0) && (elevatorMin == true))
        	{
        		elevatorManual = true;
        		gotoSpot=false;
        		canWinch.set(joy2.getRawAxis(2));
        		canWinch2.set(joy2.getRawAxis(2));
        	}
        	
        	if((buttonJ2Rtrig < 0.001) && (buttonJ2Ltrig > 0) && (!elevatorMin))
        	{
        		canWinch.set(0);
        		canWinch2.set(0);
        	}
        	
        //Use button on the first controller to reset the elevator encoder, useful for not fucking up the threaded rod :P
        	
    	if(buttonJ1B7 == true)
    	{
    		encoderElevator.reset();
    	}
    }
}