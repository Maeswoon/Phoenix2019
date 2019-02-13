package frc.command;

import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj.command.Command;
import frc.robot.Robot;
import frc.robot.subsystems.BoxManipulator;
import frc.util.Constants;

public class Operator extends Command {

    private Robot m_robot;
    private Joystick m_operatorJoystick;
    private BoxManipulator m_manipulator;

    public Operator(Robot robot, Joystick OperatorJoystick, BoxManipulator manipulator) {
        m_robot = robot;
        m_manipulator = manipulator;
        m_operatorJoystick = OperatorJoystick;
    }
    
    protected void initialize(){

    }

    protected void execute(){
        if(m_operatorJoystick.getRawButton(Constants.XBOX_BUTTON_LEFT_BUMPER)){
            m_manipulator.openManipulator();
        }else if(m_operatorJoystick.getRawButton(Constants.XBOX_BUTTON_RIGHT_BUMPER)){
            m_manipulator.closeManipulator();
        }

        //push and pull the intakes based of the button pressed
        if(m_operatorJoystick.getRawButton(Constants.XBOX_BUTTON_A)){
            m_manipulator.pushBox();
        }else if(m_operatorJoystick.getRawButton(Constants.XBOX_BUTTON_B)){
            m_manipulator.pullBox();
        }else{
            m_manipulator.stop();
        }
        
    }

    protected void end(){
        m_manipulator.goToPosition(0);
    }

    protected boolean isFinished(){
        return false;
    }
    
    protected void interrupted(){
        end();
    }
}