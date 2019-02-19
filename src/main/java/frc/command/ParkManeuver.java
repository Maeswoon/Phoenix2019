/*----------------------------------------------------------------------------*/
/* Copyright (c) 2018 FIRST. All Rights Reserved.                             */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/

package frc.command;

import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj.command.CommandGroup;
import edu.wpi.first.wpilibj.command.WaitCommand;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import frc.robot.Robot;
import frc.robot.subsystems.TankDrive;

public class ParkManeuver extends CommandGroup {
  public ParkManeuver(Robot robot, Joystick stick, TankDrive tankDrive) {
    // Use requires() here to declare subsystem dependencies
    // eg. requires(chassis);
    addSequential(new DriveVoltageTime(robot, tankDrive, 1));
    //addSequential(new DriveVoltageTimeOneSide(tankDrive, 1, "left"));
    //addSequential(new DriveVoltageTimeOneSide(tankDrive, 1, "right"));
    addSequential(new GetVisionData(robot));
    double multiplier = SmartDashboard.getNumber("DB/Slider 0", 0);
    //System.out.println("TARGET: " + multiplier * robot.targetCenterX);
    addSequential(new DriveGyroOneSide(robot, tankDrive, multiplier, "right", true));
    //addSequential(new WaitCommand(0.5));
    addSequential(new DriveGyroOneSide(robot, tankDrive, multiplier, "left", false));
    //addSequential(new DriveVoltageTime(tankDrive, Constants.TARGET_DISTANCE_MUL * robot.targetDistance));
  }
}
