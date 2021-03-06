package frc.command;

import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj.command.CommandGroup;
import frc.robot.subsystems.BoxManipulator;
import frc.robot.subsystems.TankDrive;
import frc.command.Driver;
import frc.command.Operator;


public class Teleop extends CommandGroup {

    public Teleop(TankDrive tankDrive, BoxManipulator manipulator, Joystick driverJoystick, Joystick operatorJoyStick) {
        addSequential(new Driver(tankDrive,driverJoystick));
        addSequential(new Operator(operatorJoyStick, manipulator));
    }
}