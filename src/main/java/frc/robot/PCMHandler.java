package frc.robot;

import frc.util.Constants;

import edu.wpi.first.wpilibj.Compressor;
import edu.wpi.first.wpilibj.Solenoid;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

public class PCMHandler {
	Compressor compressor; 
	Solenoid highgearSol;
	Solenoid lowgearSol; 
	Solenoid clawSolLeft;
	Solenoid clawSolRight;

	public PCMHandler(int port) {
		
		compressor = new Compressor(port);
		compressor.setClosedLoopControl(true);
		
		highgearSol = new Solenoid(Constants.PCM_CAN_ID, Constants.PCM_SLOT_HIGHGEAR);
		lowgearSol = new Solenoid(Constants.PCM_CAN_ID, Constants.PCM_SLOT_LOWGEAR);
		clawSolLeft = new Solenoid(Constants.PCM_CAN_ID, Constants.PCM_BOX_MANIPULATOR_LEFT);
		clawSolRight = new Solenoid(Constants.PCM_CAN_ID, Constants.PCM_BOX_MANIPULATOR_RIGHT);
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
		clawSolRight.set(false);
		clawSolLeft.set(false);
	}
	
	public void closeManipulator() {
		clawSolLeft.set(true);
		clawSolRight.set(true);
	}
	
}