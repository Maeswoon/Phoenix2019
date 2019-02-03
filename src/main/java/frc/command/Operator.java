package frc.command;

import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj.command.Command;

public class Operator extends Command {

    public Operator(Joystick OperatorJoystick) {
        
    }
    
    protected void initialize(){

    }

    protected void execute(){

    }

    protected void end(){

    }

    protected boolean isFinished(){
        return false;
    }
    
    protected void interrupted(){
        end();
    }
}