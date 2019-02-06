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
    addSequential(new DriveVoltageTime(tankDrive, 2));
    addSequential(new GetVisionData(robot));
    if(robot.targetCenterX < Constants.NEAR_TARGET) {
      addSequential(new DriveVoltageTimeOneSide(tankDrive, robot.targetCenterX, "left"));
      addSequential(new DriveVoltageTimeOneSide(tankDrive, robot.targetCenterX, "right"));
      addSequential(new DriveVoltageTime(tankDrive, robot.targetDistance));
    } else if(robot.targetCenterX > Constants.NEAR_TARGET) {
      addSequential(new DriveVoltageTimeOneSide(tankDrive, robot.targetCenterX, "right"));
      addSequential(new DriveVoltageTimeOneSide(tankDrive, robot.targetCenterX, "left"));
      addSequential(new DriveVoltageTime(tankDrive, robot.targetDistance));
    } else {
      addSequential(new DriveVoltageTime(tankDrive, robot.targetDistance));
    }
  }

  // Called just before this Command runs the first time
  @Override
  protected void initialize() {
  }

  // Called repeatedly when this Command is scheduled to run
  @Override
  protected void execute() {
  }

  // Make this return true when this Command no longer needs to run execute()
  @Override
  protected boolean isFinished() {
    return false;
  }

  // Called once after isFinished returns true
  @Override
  protected void end() {
  }

  // Called when another command which requires one or more of the same
  // subsystems is scheduled to run
  @Override
  protected void interrupted() {
  }
}
