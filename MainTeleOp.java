package com.qualcomm.ftcrobotcontroller.opmodes;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.LightSensor;
import com.qualcomm.robotcore.util.Range;
import com.qualcomm.robotcore.hardware.Servo;

/**
 * Created by appx on 10/23/15.
 */
public class MainTeleOp extends OpMode {

    DcMotor motorRight, motorLeft;

    LightSensor lightSensorLeft, lightSensorRight;

    float leftPower, rightPower;
    final double MAX_JOYSTICK_VALUE = 1.414;

    public MainTeleOp(){

    }

    public void init(){
        motorRight = hardwareMap.dcMotor.get("motor_right");
        motorLeft = hardwareMap.dcMotor.get("motor_left");
        motorLeft.setDirection(DcMotor.Direction.REVERSE);

        lightSensorLeft = hardwareMap.lightSensor.get("light_sensor_left");
        lightSensorRight = hardwareMap.lightSensor.get("light_sensor_right");
    }

    public void loop(){
        //movement
        leftPower = gamepad1.right_stick_y+gamepad1.right_stick_x;
        rightPower = gamepad1.right_stick_y-gamepad1.right_stick_x;
        telemetry.addData("1", "Raw Drive Left: "+String.format("%.2f",leftPower) + "  Raw Drive Right: "+String.format("%.2f",rightPower));

        telemetry.addData("2", "Raw Light Left: "+String.format("%.2f",lightSensorLeft.getLightDetectedRaw())+" Raw Light Right: "+String.format("%.2f", lightSensorRight.getLightDetectedRaw()));

        //This might be the reason why the motor power was not going up to 100%
//      leftPower = Range.clip(leftPower, -1, 1);
//      rightPower = Range.clip(rightPower, -1, 1);

        //movement continued
        leftPower = (float)scaleInput(leftPower);
        rightPower = (float)scaleInput(rightPower);
        motorLeft.setPower((double)leftPower);
        motorRight.setPower((double) rightPower);
        telemetry.addData("Powers", "Left: " + String.format("%.2f", leftPower) + " " + "Right: " + String.format("%.2f", rightPower));

//        armPower = gamepad1.left_stick_y;
//        armPower = Range.clip(armPower, -1, 1);
//        armPower = (float)scaleInput(armPower);
//        motorArm.setPower(armPower);
    }

    public void stop(){

    }

    double scaleInput(double dVal)  {
        double[] scaleArray = { 0.0, 0.05, 0.09, 0.10, 0.12, 0.15, 0.18, 0.24,
                0.30, 0.36, 0.43, 0.50, 0.60, 0.72, 0.85, 1.00, 1.00 };

        // get the corresponding index for the scaleInput array.
        int index = (int) ((dVal/MAX_JOYSTICK_VALUE) * 16.0); //number is now <= 1

        // index should be positive.
        if (index < 0) {
            index = -index;
        }

        // index cannot exceed size of array minus 1.
        if (index > 16) {
            index = 16;
        }

        // get value from the array.
        double dScale = 0.0;
        if (dVal < 0) {
            dScale = -scaleArray[index];
        } else {
            dScale = scaleArray[index];
        }

        // return scaled value.
        return dScale;
    }
}
