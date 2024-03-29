package frc.robot;

import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

public class ComponentsControl {
    //Variable Defintions
    private final double shooterHighSpeed = 1.0;
    private final double shooterLowSpeed = 0.3;
    private final double intakeInSpeed = 1.0;
    private final double baseClimbSpeed = 0.45;
    private final double climbUpSpeed = 0.25;
    private final double climbLevelTolerance = 5.0;
    private final double climbConstant = 0.05;
    
    //Run the components
    public void runComponents(Components components, ControlInputs controlInputs, SensorInputs sensorInputs) {
        //Variable Defintions
        double shooterSpeed = 0.0;
        double intakeSpeed = 0.0;
        double climbLeftSpeed = 0.0;
        double climbRightSpeed = 0.0;

        if (controlInputs.shootHigh) {
            shooterSpeed = shooterHighSpeed;
            intakeSpeed = -intakeInSpeed;
        } else if (controlInputs.shootLow) {
            shooterSpeed = shooterLowSpeed;
            intakeSpeed = -intakeInSpeed;
        }

        if (controlInputs.intakeIn) {
            if (!controlInputs.intakeSensorOff) {
                if (!sensorInputs.intakeLimitHome) {
                    intakeSpeed = intakeInSpeed;
                }
            } else {
                intakeSpeed = intakeInSpeed;
            }
        }

        if (controlInputs.climbDown) {
            climbLeftSpeed = -baseClimbSpeed;
            climbRightSpeed = -baseClimbSpeed;

            if (sensorInputs.currentRollDegrees > climbLevelTolerance) { //Right side too low
                double rightPowerAddition = climbConstant * Math.abs(sensorInputs.currentRollDegrees);
                climbRightSpeed -= rightPowerAddition;
            } else if (sensorInputs.currentRollDegrees < -climbLevelTolerance) { //Left side too low
                double leftPowerAddition = climbConstant * Math.abs(sensorInputs.currentRollDegrees);
                climbLeftSpeed -= leftPowerAddition;
            }
        } else if (controlInputs.climbUp) {
            climbLeftSpeed = climbUpSpeed;
            climbRightSpeed = climbUpSpeed;
        }

        SmartDashboard.putNumber("Shooter Speed", shooterSpeed);
        SmartDashboard.putNumber("Intake Speed", intakeSpeed);
        SmartDashboard.putNumber("Left Climb Speed", climbLeftSpeed);
        SmartDashboard.putNumber("Right Climb Speed", climbRightSpeed);

        components.leftLowerShooter.set(shooterSpeed);
        components.leftUpperShooter.set(shooterSpeed);
        components.intakeBigBar.set(intakeSpeed);
        components.climbLeft.set(powerClamp(climbLeftSpeed));
        components.climbRight.set(powerClamp(climbRightSpeed));
    }

    private double powerClamp(double power) {
        if (power > 1.0) {
            power = 1.0;
        } else if (power < -1) {
            power = -1.0;
        }
        return power;
    }
}