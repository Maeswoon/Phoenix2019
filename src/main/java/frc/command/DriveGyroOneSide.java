/*----------------------------------------------------------------------------*/
/* Copyright (c) 2018 FIRST. All Rights Reserved.                             */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/

package frc.command;

import edu.wpi.first.wpilibj.command.Command;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import frc.robot.Gyro;
import frc.robot.subsystems.TankDrive;
import frc.util.Constants;
import frc.util.PIDLoop;
import frc.robot.Robot;

public class DriveGyroOneSide extends Command {

  private double angle;
  private boolean first;
  private TankDrive m_tankDrive;

  private String m_side;

  private PIDLoop pidLoop;

  private boolean reached;
  private long reachedTime;

  private Robot robot;

  private boolean back;

  public DriveGyroOneSide(Robot robot, TankDrive tankDrive, double angle, String side, boolean first) {
    m_tankDrive = tankDrive;
    this.angle = angle;
    m_side = side;
    reached = false;
    this.robot = robot;
    this.first = first;
  }

  // Called just before this Command runs the first time
  @Override
  protected void initialize() {
    if((!first && robot.targetCenterX < -Constants.NEAR_TARGET) || (first && robot.targetCenterX > Constants.NEAR_TARGET))
      m_side = "right";
    else if((first && robot.targetCenterX < -Constants.NEAR_TARGET) || (!first && robot.targetCenterX > Constants.NEAR_TARGET))
      m_side = "left";
    else
      m_side = "kill me";

    if(robot.targetCenterX < -Constants.NEAR_TARGET)
      angle = -angle;

    if(m_side.equals("right"))
      this.angle = -angle;
    System.out.println("target xcent " + robot.targetCenterX);
    m_tankDrive.zeroEncoders();
    if(m_side.equals("left"))
      pidLoop = new PIDLoop(16, 0.02, 0, 20, angle * robot.targetCenterX);
    else if(m_side.equals("right"))
      pidLoop = new PIDLoop(16, 0.05, 0, 20, angle * robot.targetCenterX);
    Gyro.reset();
    robot.pidControl = true;
  }

  // Called repeatedly when this Command is scheduled to run
  @Override
  protected void execute() {
    if(m_side.equals("right")) {
      m_tankDrive.setPercentage(-pidLoop.get(m_side), 0);
    } else if(m_side.equals("left")) {
      m_tankDrive.setPercentage(0, -pidLoop.get(m_side));
    }

    if(Math.abs(Gyro.angle() + angle) < 5 && !reached) {
      reached = true;
      reachedTime = System.currentTimeMillis();
    }
  }

  // Make this return true when this Command no longer needs to run execute()
  @Override
  protected boolean isFinished() {
    return (m_side.equals("kill me")) || (reached && System.currentTimeMillis() - reachedTime > 2000);
  }

  // Called once after isFinished returns true
  @Override
  protected void end() {
    m_tankDrive.setPercentage(0, 0);
    robot.pidControl = false;
  }

  // Called when another command which requires one or more of the same
  // subsystems is scheduled to run
  @Override
  protected void interrupted() {
  }
}
