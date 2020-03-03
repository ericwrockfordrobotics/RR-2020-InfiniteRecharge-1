/*----------------------------------------------------------------------------*/
/* Copyright (c) 2017-2018 FIRST. All Rights Reserved.                        */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/

package frc.robot;

import edu.wpi.first.wpilibj.TimedRobot;
import edu.wpi.first.wpilibj.drive.DifferentialDrive;
import edu.wpi.first.wpilibj.smartdashboard.SendableChooser;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj.SpeedControllerGroup;

import com.revrobotics.CANSparkMax;
import com.revrobotics.CANSparkMaxLowLevel.MotorType;

import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj.AnalogInput;
import edu.wpi.first.wpilibj.Timer;

import frc.robot.intakeSubsystem;
import frc.robot.hopperSubsystem;
import frc.robot.aimSubsystem;
import frc.robot.Button;
import frc.robot.hopperState;


/**
 * The VM is configured to automatically run this class, and to call the
 * functions corresponding to each mode, as described in the TimedRobot
 * documentation. If you change the name of this class or the package after
 * creating this project, you must also update the build.gradle file in the
 * project.
 */
public class Robot extends TimedRobot {
  private static final String kDefaultAuto = "Default";
  private static final String kCustomAuto = "My Auto";
  private String m_autoSelected;
  private final SendableChooser<String> m_chooser = new SendableChooser<>();

  private CANSparkMax leftFrontMotor;
  private CANSparkMax leftBackMotor;

  private CANSparkMax rightFrontMotor;
  private CANSparkMax rightBackMotor;
  private final Joystick driveStick = new Joystick(0);
  private final Joystick operatorJoy = new Joystick(1);
  DifferentialDrive driveTrain;
  private final Button button3 = new Button();
  private final Button button6 = new Button();
  private final Button button2 = new Button();
  Turret turret = new Turret(1);
  boolean someBoolean = false;
  private hopperState state = hopperState.INIT;
  private AnalogInput sensorIntake = new AnalogInput(0);
  private AnalogInput sensorOuttake = new AnalogInput(1);
  private boolean sensorIntakeBool = false;
  private boolean sensorIntakeShadow = sensorIntakeBool;
  private boolean sensorOuttakeBool = false;
  private boolean sensorOuttakeShadow = sensorOuttakeBool;
  private int ballCount = 0;
  private Timer timer = new Timer();
  private boolean shoot = false;

  /**
   * This function is run when the robot is first started up and should be used
   * for any initialization code.
   */
  @Override
  public void robotInit() {
    m_chooser.setDefaultOption("Default Auto", kDefaultAuto);
    m_chooser.addOption("My Auto", kCustomAuto);
    SmartDashboard.putData("Auto choices", m_chooser);

    leftFrontMotor = new CANSparkMax(1, MotorType.kBrushless);
    leftBackMotor = new CANSparkMax(2, MotorType.kBrushless);

    rightFrontMotor = new CANSparkMax(3, MotorType.kBrushless);
    rightBackMotor = new CANSparkMax(4, MotorType.kBrushless);

    SpeedControllerGroup leftDriveTrainGroup = new SpeedControllerGroup(leftFrontMotor, leftBackMotor);
    SpeedControllerGroup rightDriveTrainGroup = new SpeedControllerGroup(rightFrontMotor, rightBackMotor);

    driveTrain = new DifferentialDrive(leftDriveTrainGroup, rightDriveTrainGroup);
  }

  /**
   * This function is called every robot packet, no matter the mode. Use this for
   * items like diagnostics that you want ran during disabled, autonomous,
   * teleoperated and test.
   *
   * <p>
   * This runs after the mode specific periodic functions, but before LiveWindow
   * and SmartDashboard integrated updating.
   */
  @Override
  public void robotPeriodic() {
  }

  /**
   * This autonomous (along with the chooser code above) shows how to select
   * between different autonomous modes using the dashboard. The sendable chooser
   * code works with the Java SmartDashboard. If you prefer the LabVIEW Dashboard,
   * remove all of the chooser code and uncomment the getString line to get the
   * auto name from the text box below the Gyro
   *
   * <p>
   * You can add additional auto modes by adding additional comparisons to the
   * switch structure below with additional strings. If using the SendableChooser
   * make sure to add them to the chooser code above as well.
   */
  @Override
  public void autonomousInit() {
    m_autoSelected = m_chooser.getSelected();
    // m_autoSelected = SmartDashboard.getString("Auto Selector", kDefaultAuto);
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

  /**
   * This function is called periodically during operator control.
   */
  @Override
  public void teleopPeriodic() {
    if (sensorIntake.getAverageVoltage() > 0.8) {
      sensorIntakeBool = true;
    }
    else {
      sensorIntakeBool = false;
    }
    if (sensorOuttake.getAverageVoltage() > 0.8) {
      sensorOuttakeBool = true;
    }
    else {
      sensorOuttakeBool = false;
    }
    
    driveTrain.arcadeDrive(driveStick.getRawAxis(1), driveStick.getRawAxis(0));

    //Replace these Button Stubs with real code if needed
    if (operatorJoy.getRawButtonPressed(3)) {
      button3.state = !button3.state;
    }
    if (operatorJoy.getRawButtonPressed(6)) {
      button6.state = !button6.state;
    }
    if (operatorJoy.getRawButtonPressed(2)) {
      button2.state = !button2.state;
    }
    if (button3.state){
      intakeSubsystem.intakeOff();
      intakeSubsystem.intakeRetract();
      SmartDashboard.putBoolean("IntakeON", false);
      SmartDashboard.putBoolean("IntakeExtended", false);
    }
    else if (!button3.state){
      intakeSubsystem.intakeOn();
      intakeSubsystem.intakeExtend();
      SmartDashboard.putBoolean("IntakeON", true);
      SmartDashboard.putBoolean("IntakeExtended", true);
    }
    if (button2.state) {
      hopperSubsystem.hopperOn();
      SmartDashboard.putBoolean("HopperON", true);
    }
    else if (!button2.state) {
      hopperSubsystem.hopperOff();
      SmartDashboard.putBoolean("HopperON", false);
    }
    if (button6.state) {
      aimSubsystem.autoAimOn();
      SmartDashboard.putBoolean("AutoAimON", true);
    }
    else if (!button6.state) {
      aimSubsystem.autoAimOff();
      SmartDashboard.putBoolean("AutoAimON", false);
      turret.rotateByJoystick(operatorJoy.getRawAxis(0));
      if (operatorJoy.getRawButtonPressed(11)) {
        turret.raise();
        SmartDashboard.putString("Turret", "Raised");
      }
      else if(operatorJoy.getRawButtonPressed(10)) {
        turret.lower();
        SmartDashboard.putString("Turret", "Lowered");
      }
    }

    /* State Machine Logic Hopper System */
    if (sensorIntakeShadow != sensorIntakeBool) {
      if (sensorIntakeBool) {
        ballCount++;
      }
      sensorIntakeShadow = sensorIntakeBool;
    }
    if (sensorOuttakeShadow != sensorOuttakeBool) {
      if (sensorOuttakeBool) {
      }
      if (!sensorOuttakeBool) {
        ballCount--;
      }
      sensorOuttakeShadow = sensorOuttakeBool;
    }

    if (!shoot) {
      if (ballCount == 0) {
        state = hopperState.INIT;
      }
      else if (!sensorOuttakeBool) {
        state = hopperState.HOT;
      }
      else { 
        state = hopperState.ARMED;
        if (driveStick.getRawButtonPressed(Button.START)) { 
          state = hopperState.SHOOT;
          shoot = true;
          timer.start(); 
      } 
    }
         

    }

    if (state == hopperState.INIT) {
      SmartDashboard.putString("State", "Init");
      hopperSubsystem.hopperOff();
      intakeSubsystem.intakeOn();
    }
    else if (state == hopperState.HOT) {
      SmartDashboard.putString("State", "Hot");
      hopperSubsystem.hopperOn();
      intakeSubsystem.intakeOn();
    }
    else if (state == hopperState.ARMED) {
      SmartDashboard.putString("State", "Armed");
      if (ballCount < 4) {
        hopperSubsystem.hopperOn();
        intakeSubsystem.intakeOn();
      } 
      else {
        hopperSubsystem.hopperOff();
        intakeSubsystem.intakeOff();
      }
    }
    else if (state == hopperState.SHOOT) {
      SmartDashboard.putString("State", "Shoot");
      hopperSubsystem.hopperOff();
      intakeSubsystem.intakeOff();
      if (timer.get() >= 0.5) {
        shoot = false;
        timer.stop();
        timer.reset();
        state = hopperState.INIT;
      }
        // shoot motor run
    }
    SmartDashboard.putNumber("Ball Count", ballCount);
    turret.shooterSpeed(operatorJoy.getRawAxis(2));
  }

  
  /**
   * This function is called periodically during test mode.
   */
  @Override
  public void testPeriodic() {
  }

  public void rotateByJoystick(double input) {
    ;
}
}
