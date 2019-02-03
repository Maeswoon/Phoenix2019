/*----------------------------------------------------------------------------*/
/* Copyright (c) 2018 FIRST. All Rights Reserved.                             */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/

package frc.command;

import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj.command.Command;
import frc.robot.Robot;
import frc.util.Constants;

public class SemiAutoRoutine extends Command {

  private Robot m_robot;
  private Joystick m_stick;

  public SemiAutoRoutine(Robot robot, Joystick stick) {
    m_robot = robot;
    m_stick = stick;
  }

  // Called just before this Command runs the first time
  @Override
  protected void initialize() {
    m_robot.setTeleopOpControl(false);
  }

  // Called repeatedly when this Command is scheduled to run
  @Override
  protected void execute() {

  }

  // Make this return true when this Command no longer needs to run execute()
  @Override
  protected boolean isFinished() {
    return m_stick.getRawButton(Constants.XBOX_BUTTON_THREE_LINES);
  }

  // Called once after isFinished returns true
  @Override
  protected void end() {
    m_robot.setTeleopOpControl(true);
  }

  // Called when another command which requires one or more of the same
  // subsystems is scheduled to run
  @Override
  protected void interrupted() {
  }
}
