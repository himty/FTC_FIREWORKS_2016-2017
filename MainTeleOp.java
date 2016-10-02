package com.qualcomm.ftcrobotcontroller.opmodes;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.robotcore.util.Range;

/**
 * Created by appx on 10/23/15.
 */
public class TestTeleOp extends OpMode {


    DcMotor motorRight, motorLeft;
    DcMotor motorSpoolHeight, motorSpoolLength, motorDropper;
    Servo servoZiplineRed, servoZiplineBlue;

    float leftPower, rightPower;
    float spoolHPower, spoolLPower;
    final double MAX_JOYSTICK_VALUE = 1.414;

    //    final double MAX_ZIPLINE_VALUE = ;
    final double WAIT_TIME = 1.0;
    double runDropperTime = 0, ziplineWait;

    boolean isDropperDown = false;

    double servoPosition = 0.75, servoPosition2 = 0.75;

    public TestTeleOp() {

    }

    public void init() {
        motorRight = hardwareMap.dcMotor.get("motor_2");
        motorLeft = hardwareMap.dcMotor.get("motor_1");
        motorRight.setDirection(DcMotor.Direction.REVERSE);


        motorSpoolHeight = hardwareMap.dcMotor.get("motor_angle");
        motorSpoolLength = hardwareMap.dcMotor.get("motor_spool");
        motorSpoolHeight.setDirection(DcMotor.Direction.REVERSE);
        motorSpoolLength.setDirection(DcMotor.Direction.REVERSE);

//        servoDropper = hardwareMap.servo.get("servo_dropper");
        servoZiplineBlue = hardwareMap.servo.get("servo_ziplineBlue");
        servoZiplineRed = hardwareMap.servo.get("servo_ziplineRed");
        servoZiplineBlue.setDirection(Servo.Direction.REVERSE);
    }

    public void loop() {
        //movement
        if (gamepad2.left_bumper == true) {
            leftPower = gamepad2.left_stick_y - gamepad2.left_stick_x / 5;
            rightPower = gamepad2.left_stick_y + gamepad2.left_stick_x / 5;
        } else {
            leftPower = gamepad2.left_stick_y - gamepad2.left_stick_x;
            rightPower = gamepad2.left_stick_y + gamepad2.left_stick_x;
        }
        
        //might have caused the <100 power thing last year
//         leftPower = Range.clip(leftPower, -1, 1);
//         rightPower = Range.clip(rightPower, -1, 1);
        motorLeft.setPower((double) leftPower);
        motorRight.setPower((double) rightPower);
        telemetry.addData("Drive", "Left: " + String.format("%.2f", leftPower) + " " + "Right: " + String.format("%.2f", rightPower));

        //spool
        if (gamepad1.right_stick_y > 0.3 || gamepad1.right_stick_y < -0.3) {
            if (gamepad1.right_bumper == true) {
                spoolHPower = gamepad1.right_stick_y;
            } else {
                spoolHPower = gamepad1.right_stick_y / 3;
            }
        } else {
            spoolHPower = 0;
        }
        spoolLPower = gamepad1.left_stick_y / 5;
        spoolHPower = Range.clip(spoolHPower, -1, 1);
        spoolLPower = Range.clip(spoolLPower, -1, 1);
        motorSpoolHeight.setPower((double) spoolHPower);
        motorSpoolLength.setPower((double) spoolLPower);

        //window wipers/zipline :3
        if (gamepad1.x == true && ziplineWait < time) {
            servoZiplineBlue.setPosition(0.125);
            servoZiplineBlue.setDirection(Servo.Direction.FORWARD);
            servoZiplineRed.setDirection(Servo.Direction.REVERSE);
            servoZiplineRed.setPosition(0.125);
            ziplineWait = time + WAIT_TIME;
        }
        if (gamepad1.b == true && ziplineWait < time) {
            servoZiplineBlue.setPosition(0.75);
            servoZiplineBlue.setDirection(Servo.Direction.REVERSE);
//            servoZiplineRed.setDirection(Servo.Direction.FORWARD);
            servoZiplineRed.setPosition(0.75);
            ziplineWait = time + WAIT_TIME;
        }

        //dropper
        if (gamepad1.dpad_down == true) { //make dropper go down
            motorDropper.setPower(0.5);
        }
        if (gamepad1.dpad_up == true) {
            motorDropper.setPower(-0.5);
        }
    }

    public void stop() {

    }
}
