package frc.command;

import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj.command.Command;
import frc.robot.subsystems.BoxManipulator;
import frc.util.Constants;

public class Operator extends Command {

    private Joystick m_operatorJoystick;
    private BoxManipulator m_manipulator;

    public Operator(Joystick OperatorJoystick, BoxManipulator manipulator) {
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

        if(m_operatorJoystick.getRawButton(Constants.XBOX_BUTTON_A)){
            m_manipulator.goToPosition(500);
        }else{
            m_manipulator.goToPosition(0);
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