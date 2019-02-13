/*----------------------------------------------------------------------------*/
/* Copyright (c) 2017-2018 FIRST. All Rights Reserved.                        */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/

package frc.robot;

import com.ctre.phoenix.motorcontrol.can.WPI_TalonSRX;
import com.ctre.phoenix.motorcontrol.can.WPI_VictorSPX;

import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj.TimedRobot;
import edu.wpi.first.wpilibj.command.Command;
import edu.wpi.first.wpilibj.command.Scheduler;
import edu.wpi.first.wpilibj.smartdashboard.SendableChooser;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import frc.command.DriveVoltageTime;
import frc.command.ParkManeuver;
import frc.command.Teleop;
import frc.robot.subsystems.BoxManipulator;
import frc.robot.subsystems.TankDrive;
import frc.util.Constants;

/**
 * The VM is configured to automatically run this class, and to call the
 * functions corresponding to each mode, as described in the IterativeRobot
 * documentation. If you change the name of this class or the package after
 * creating this project, you must also update the build.gradle file in the
 * project.
 */
public class Robot extends TimedRobot {
  private static final String kDefaultAuto = "Default";
  private static final String kCustomAuto = "My Auto";
  private String m_autoSelected;
  private final SendableChooser<String> m_chooser = new SendableChooser<>();

  Joystick driverJoystick;
  Joystick operatorJoystick;

  WPI_TalonSRX talonFR;
	WPI_TalonSRX talonFL;
	WPI_TalonSRX talonBR;
  WPI_TalonSRX talonBL;
  
  WPI_VictorSPX talonIntakeLeft;
  WPI_VictorSPX talonIntakeRight;
  WPI_TalonSRX talonTip;

  TankDrive tankDrive;

  public double targetCenterX = 11;
  public double targetDistance = 0;
  public boolean targetFound = true;
  
  BoxManipulator manipulator;
  PCMHandler pcm;


  /**
   * This function is run when the robot is first started up and should be
   * used for any initialization code.
   */
  @Override
  public void robotInit() {

    driverJoystick = new Joystick(0);
    operatorJoystick = new Joystick(1);
    pcm = new PCMHandler(11);
    talonFR = new WPI_TalonSRX(Constants.RIGHT_MASTER_TALON_ID);
    talonFL = new WPI_TalonSRX(Constants.LEFT_MASTER_TALON_ID);
		talonBR = new WPI_TalonSRX(Constants.RIGHT_SLAVE_TALON_ID);
    talonBL = new WPI_TalonSRX(Constants.LEFT_SLAVE_TALON_ID);
    
    talonIntakeLeft = new WPI_VictorSPX(Constants.VICTOR_INTAKE_LEFT);
    talonIntakeRight = new WPI_VictorSPX(Constants.VICTOR_INTAKE_RIGHT);
    talonTip = new WPI_TalonSRX(Constants.TALON_TIP);

    tankDrive = new TankDrive(talonFL, talonFR, talonBL, talonBR);
    manipulator = new BoxManipulator(talonIntakeRight, talonIntakeLeft, talonTip, pcm);

    m_chooser.setDefaultOption("Default Auto", kDefaultAuto);
    m_chooser.addOption("My Auto", kCustomAuto);
    SmartDashboard.putData("Auto choices", m_chooser);

    Gyro.init();
  }

  /**
   * This function is called every robot packet, no matter the mode. Use
   * this for items like diagnostics that you want ran during disabled,
   * autonomous, teleoperated and test.
   *
   * <p>This runs after the mode specific periodic functions, but before
   * LiveWindow and SmartDashboard integrated updating.
   */
  @Override
  public void robotPeriodic() {
  }

  /**
   * This autonomous (along with the chooser code above) shows how to select
   * between different autonomous modes using the dashboard. The sendable
   * chooser code works with the Java SmartDashboard. If you prefer the
   * LabVIEW Dashboard, remove all of the chooser code and uncomment the
   * getString line to get the auto name from the text box below the Gyro
   *
   * <p>You can add additional auto modes by adding additional comparisons to
   * the switch structure below with additional strings. If using the
   * SendableChooser make sure to add them to the chooser code above as well.
   */
  @Override
  public void autonomousInit() {
    m_autoSelected = m_chooser.getSelected();
    // autoSelected = SmartDashboard.getString("Auto Selector",
    // defaultAuto);
    System.out.println("Auto selected: " + m_autoSelected);
  }

  /**
   * This function is called periodically during autonomous.
   */
  @Override
  public void autonomousPeriodic() {
    switch (m_autoSelected) {
      case kCustomAuto:
        // Put custom auto code here
        break;
      case kDefaultAuto:
      default:
        // Put default auto code here
        break;
    }
  }


  public void teleopInit() {
    pcm.turnOn();
    tankDrive.teleopConfig();

    Command teleop = new Teleop(this, tankDrive, manipulator, driverJoystick, operatorJoystick);
		Scheduler.getInstance().add(teleop);


  } 
  /**
   * This function is called periodically during operator control.
   */
  @Override
  public void teleopPeriodic() {

    // if(driverJoystick.getRawButton(Constants.XBOX_BUTTON_A)) {
    //   //Scheduler.getInstance().removeAll();
    //   Scheduler.getInstance().add(new ParkManeuver(this, driverJoystick, tankDrive));
    //   //Scheduler.getInstance().add(new Teleop(this, tankDrive, driverJoystick, operatorJoystick));
    // }

    // if(driverJoystick.getRawButton(Constants.XBOX_BUTTON_B)) {
    //   Scheduler.getInstance().add(new DriveVoltageTime(tankDrive, -1));
    // }

    if(driverJoystick.getRawButton(Constants.XBOX_BUTTON_LEFT_BUMPER)) {
        pcm.setLowGear(false);
        pcm.setHighGear(true);
    }

    if(driverJoystick.getRawButton(Constants.XBOX_BUTTON_RIGHT_BUMPER)) {
      pcm.setLowGear(true);
      pcm.setHighGear(false);
    }

    Scheduler.getInstance().run();
    
    System.out.println("Gyro angle: " + Gyro.subsystem());

  }

  /**
   * This function is called periodically during test mode.
   */
  @Override
  public void testPeriodic() {
  }
}
