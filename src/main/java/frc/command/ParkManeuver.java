/*----------------------------------------------------------------------------*/
/* Copyright (c) 2018 FIRST. All Rights Reserved.                             */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/

package frc.command;

import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj.command.CommandGroup;
import frc.robot.Robot;
import frc.robot.subsystems.TankDrive;
import frc.util.Constants;

public class ParkManeuver extends CommandGroup {
  public ParkManeuver(Robot robot, Joystick stick, TankDrive tankDrive) {
    // Use requires() here to declare subsystem dependencies
    // eg. requires(chassis);
    addSequential(new DriveVoltageTime(tankDrive, 1));
    //addSequential(new DriveVoltageTimeOneSide(tankDrive, 1, "left"));
    //addSequential(new DriveVoltageTimeOneSide(tankDrive, 1, "right"));
    // addSequential(new GetVisionData(robot));
    // if(robot.targetCenterX < Constants.NEAR_TARGET) {
    //   addSequential(new DriveVoltageTimeOneSide(tankDrive, Constants.TARGET_CENTERX_MUL * robot.targetCenterX, "left"));
    //   addSequential(new DriveVoltageTimeOneSide(tankDrive, Constants.TARGET_CENTERX_MUL * robot.targetCenterX, "right"));
    //   addSequential(new DriveVoltageTime(tankDrive, robot.targetDistance));
    // } else if(robot.targetCenterX > Constants.NEAR_TARGET) {
    //   addSequential(new DriveVoltageTimeOneSide(tankDrive, Constants.TARGET_CENTERX_MUL * robot.targetCenterX, "right"));
    //   addSequential(new DriveVoltageTimeOneSide(tankDrive, Constants.TARGET_CENTERX_MUL * robot.targetCenterX, "left"));
    //   addSequential(new DriveVoltageTime(tankDrive, Constants.TARGET_DISTANCE_MUL * robot.targetDistance));
    // } else {
    //   addSequential(new DriveVoltageTime(tankDrive, Constants.TARGET_DISTANCE_MUL * robot.targetDistance));
    // }
  }
}
