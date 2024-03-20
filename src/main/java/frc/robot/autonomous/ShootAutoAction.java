package frc.robot.autonomous;

import frc.robot.Components;
import frc.robot.DriveTrain;
import frc.robot.SensorInputs;

public class ShootAutoAction extends AutoAction {
    private double timeToShoot;
    private double power;
    private long startTime;
    private final double beltSpeed = 1.0;
    private final double intakeSpeed = 0.5;
    
    public ShootAutoAction(double shootTime, double power) {
        timeToShoot = shootTime * 1000;
        this.power = power;
    }
    
    @Override
    public void init(DriveTrain driveTrain, Components components, SensorInputs sensor) {
        startTime = System.currentTimeMillis();
    }

    @Override
    public boolean execute(DriveTrain driveTrain, Components components, SensorInputs sensor) {
        components.leftShooter.set(power);
        components.rightShooter.set( Math.max(0, power-0.05) );
        components.intakeBars.set(-intakeSpeed);
        components.rightBelt.set(beltSpeed);
        driveTrain.mecanumDrive(0, 0, 0, driveTrain.defaultRotation2d);
        return (System.currentTimeMillis() - startTime) >= timeToShoot;
    }

    @Override
    public void finalize(DriveTrain driveTrain, Components components, SensorInputs sensor) {
        components.leftShooter.set(0.0);
        components.rightShooter.set(0.0);
        components.intakeBars.set(0.0);
        components.rightBelt.set(0.0);
    }

    @Override
    public String toString() {
        return "Auto: Shoot";
    } 
}