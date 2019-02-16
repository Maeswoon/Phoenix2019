package frc.command;

import edu.wpi.first.wpilibj.command.Command;
import frc.robot.subsystems.BoxManipulator;

public class MoveManipulator extends Command{
    private BoxManipulator m_manipulator;
    private double m_targetPosition;

    public MoveManipulator(BoxManipulator manipulator, double position) {
        m_manipulator = manipulator;
        m_targetPosition = position;
    }
    
    protected void initialize(){

    }

    protected void execute(){
        m_manipulator.goToPosition(m_targetPosition);
    }

    protected void end(){
        m_manipulator.goToPosition(0);
    }

    protected boolean isFinished(){
        return Math.abs(m_manipulator.getPosition() - m_targetPosition) < 1000.0;
    }
    
    protected void interrupted(){
        end();
    }
}