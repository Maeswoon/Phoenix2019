package frc.command;

import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj.command.Command;
import frc.robot.Robot;

public class Operator extends Command {

    private Robot m_robot;

    public Operator(Robot robot, Joystick OperatorJoystick) {
        m_robot = robot;
    }
    
    protected void initialize(){

    }

    protected void execute(){
        if(!m_robot.getTeleopOpControl()) return;
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