package frc.robot;

import frc.util.Constants;

import edu.wpi.first.wpilibj.Compressor;
import edu.wpi.first.wpilibj.Solenoid;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

public class PCMHandler {
	Compressor compressor; 
	Solenoid highgearSol;
	Solenoid lowgearSol; 
	Solenoid clawSolRight;
	Solenoid clawSolLeft;

	Solenoid clawSolRight2;
	Solenoid clawSolLeft2;
	
    
	public PCMHandler(int port) {
		
		compressor = new Compressor(port);
		compressor.setClosedLoopControl(true);
		
		highgearSol = new Solenoid(Constants.PCM_CAN_ID, Constants.PCM_SLOT_HIGHGEAR);
		lowgearSol = new Solenoid(Constants.PCM_CAN_ID, Constants.PCM_SLOT_LOWGEAR);
		clawSolRight = new Solenoid(Constants.PCM_CAN_ID,Constants.PCM_BOX_MANIPULATOR_RIGHT);
		clawSolLeft = new Solenoid(Constants.PCM_CAN_ID,Constants.PCM_BOX_MANIPULATOR_LEFT);

		clawSolRight2 = new Solenoid(Constants.PCM_CAN_ID,1);
		clawSolLeft2 = new Solenoid(Constants.PCM_CAN_ID,0);

	}
	
	public void turnOn(){
		compressor.setClosedLoopControl(true);
	}
	
	public void turnOff(){
		compressor.setClosedLoopControl(false);
	}
	
	public void setLowGear(boolean value) {
		lowgearSol.set(value);
		
	}
	public void setHighGear(boolean value) {
		highgearSol.set(value);
	}
	
	
	public double getCurrent (){
		return compressor.getCompressorCurrent();
	}

	public void openManipulator() {
		clawSolLeft.set(true);
		clawSolRight.set(false);
		clawSolLeft2.set(false);
		clawSolRight2.set(true);
	}

	public void closeManipulator() {
		clawSolLeft.set(false);
		clawSolRight.set(true);
		clawSolLeft2.set(true);
		clawSolRight2.set(false);
	}


	
	


	
}