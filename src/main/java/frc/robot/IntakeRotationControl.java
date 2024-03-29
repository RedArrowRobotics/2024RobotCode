package frc.robot;

import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

public class IntakeRotationControl {
    //Variable Defintions
    private boolean emergencyStopped = false;
    private final double rotationMaxSpeed = 1.0; //Must be a + value
    private final double rotationMinSpeed = 0.4; //Must be a + value
    private final double motionScalerConstant = 0.06;
    private boolean homed = false;

    //Run the components
    public final void runRotation(Components components, ControlInputs controlInputs, SensorInputs sensorInputs) {
        //Variable Defintions
        int encoderPosition = sensorInputs.intakeEncoder.get(); //Assume this value is + towards out
        int target = encoderPosition;
        SmartDashboard.putNumber("Intake Rotation Count", encoderPosition);

        //EStop Controls
        if (controlInputs.intakeEStop) emergencyStopped = true;
        SmartDashboard.putBoolean("Intake E Stopped", emergencyStopped);
        if (emergencyStopped) {
            return;
        }

        //Intake Target Controls
        if (controlInputs.intakeOut) {
            target = 972;
        } else if (sensorInputs.intakeLimitHome == false) {
            target = 0;
        }
        //Otherwise target will equal the current encoder position

        //Prevent target from being less then zero (for saftey)
        if (target < 0) {
            target = 0;
        }
        
        double intakePower = 0.0;
        if (homed) {
            intakePower = rotationMath(target, encoderPosition);
        } else {
            intakePower = rotationHome(sensorInputs);
        }
        
        components.intakeRotation.set(intakePower);
        SmartDashboard.putBoolean("Intake Unlocked", homed);
        SmartDashboard.putNumber("Intake Power", intakePower);
    }

    private final double rotationMath(int target, int encoderPosition) {
        int intakeDistRemainTravel = (target - encoderPosition);
        
        double intakeMath = 0.0;
        if (target != encoderPosition) {
            intakeMath = Math.sqrt(Math.abs(intakeDistRemainTravel));
            intakeMath *= motionScalerConstant;
            if (intakeDistRemainTravel < 0) {
                intakeMath = Math.min(-rotationMinSpeed, -intakeMath);
                SmartDashboard.putString("Intake Movement", "Moving In");
            } else if (intakeDistRemainTravel > 0) {
                intakeMath = Math.max(rotationMinSpeed, intakeMath);
                SmartDashboard.putString("Intake Movement", "Moving Out");
            }
        } else {
            intakeMath = 0.0;
            SmartDashboard.putString("Intake Movement", "Stopped");
        }
        SmartDashboard.putNumber("Intake Math", intakeMath);
        SmartDashboard.putNumber("Intake Dist Travel", intakeDistRemainTravel);

        double intakeMathClamped = Math.max(-rotationMaxSpeed, Math.min(rotationMaxSpeed, intakeMath));
        SmartDashboard.putNumber("Intake Math Clamped", intakeMathClamped);
        return intakeMathClamped;
    }

    private final double rotationHome(SensorInputs sensorInputs) {
        if (sensorInputs.intakeLimitHome == false) {
            SmartDashboard.putString("Intake Movement", "In (Homing)");
            return -rotationMinSpeed;
        } else {
            homed = true;
            sensorInputs.intakeEncoder.reset();
            SmartDashboard.putString("Intake Movement", "Homed");
        }
        return 0.0;
    }
}
