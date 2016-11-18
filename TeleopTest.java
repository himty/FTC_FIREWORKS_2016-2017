/*
Copyright (c) 2016 Robert Atkinson
All rights reserved.
Redistribution and use in source and binary forms, with or without modification,
are permitted (subject to the limitations in the disclaimer below) provided that
the following conditions are met:
Redistributions of source code must retain the above copyright notice, this list
of conditions and the following disclaimer.
Redistributions in binary form must reproduce the above copyright notice, this
list of conditions and the following disclaimer in the documentation and/or
other materials provided with the distribution.
Neither the name of Robert Atkinson nor the names of his contributors may be used to
endorse or promote products derived from this software without specific prior
written permission.
NO EXPRESS OR IMPLIED LICENSES TO ANY PARTY'S PATENT RIGHTS ARE GRANTED BY THIS
LICENSE. THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
"AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO,
THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESSFOR A PARTICULAR PURPOSE
ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE
FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL
DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER
CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR
TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF
THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
*/
package org.firstinspires.ftc.teamcode;

import android.graphics.Path;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.robotcore.util.ElapsedTime;

@TeleOp(name="TeleopTest", group="FIREWORKS")
public class TeleopTest extends LinearOpMode {
    /* Declare OpMode members. */
    HardwareTest    robot               = new HardwareTest();              // Use a K9'shardware
    ElapsedTime     armTime             = new ElapsedTime(1000); //starting time is high so doesn't mess with timing
    ElapsedTime     bPusherTime         = new ElapsedTime(1000);
    ElapsedTime     ballHolderTime         = new ElapsedTime(1000);

    final double HOLD_SERVO = 0.5;
    final double ARM_UP_SERVO = 0.54;
    final double ARM_DOWN_SERVO = 0.45;
    final double BALL_LEFT_SERVO = 0.45;
    final double BALL_RIGHT_SERVO = 0.54;

    final double MAX_JOYSTICK_VALUE = 1;

    @Override
    public void runOpMode(){

        double leftPower;
        double rightPower;
        double linearPower;

        /* Initialize the hardware variables.
         * The init() method of the hardware class does all the work here
         */
        robot.init(hardwareMap);

        // Send telemetry message to signify robot waiting;
        telemetry.addData("Say", "Hello Driver");    //
        telemetry.update();


        // Wait for the game to start (driver presses PLAY)
        waitForStart();

        // run until the end of the match (driver presses STOP)
        while (opModeIsActive()) {

            //drive train
            leftPower = (float) scaleInput(gamepad1.right_stick_y - gamepad1.right_stick_x);
            rightPower = (float) scaleInput(gamepad1.right_stick_y + gamepad1.right_stick_x);

            if (gamepad1.right_bumper){
                leftPower /= 5;
                rightPower /= 5;
            }
            robot.leftMotor.setPower((double) -1*leftPower);
            robot.rightMotor.setPower((double) -1 * rightPower);
            telemetry.addData("Powers", "Left: " + String.format("%.2f", leftPower) + " " + "Right: " + String.format("%.2f", rightPower));

            //linear slide
            linearPower = (float) scaleInput(-1*gamepad2.left_stick_y);
            robot.linearSlide.setPower((double)linearPower);

            //arm movement
            if (gamepad1.a && robot.arm.getPosition() == HOLD_SERVO){
                if (robot.arm.getPosition() == ARM_DOWN_SERVO){
                    robot.arm.setPosition(ARM_UP_SERVO);
                } else {
                    robot.arm.setPosition(ARM_DOWN_SERVO);
                }
                armTime.reset();
            }
            if ((robot.arm.getPosition()==ARM_DOWN_SERVO&&armTime.time() > 0.8)  //going down
                    || (robot.arm.getPosition()==ARM_UP_SERVO&&armTime.time() > 2)){ //going up
                robot.arm.setPosition(HOLD_SERVO);
            }

            //beacon pusher movement
            if (gamepad1.b && robot.beaconPusher.getPosition() == HOLD_SERVO){
                if (robot.beaconPusher.getPosition() == BALL_LEFT_SERVO){
                    robot.beaconPusher.setPosition(BALL_RIGHT_SERVO);
                } else {
                    robot.beaconPusher.setPosition(BALL_LEFT_SERVO);
                }
                bPusherTime.reset();
            }
            if ((robot.beaconPusher.getPosition()==BALL_LEFT_SERVO&&bPusherTime.time() > 1*0.6-0.06)  //going left
                    || (robot.beaconPusher.getPosition()==BALL_RIGHT_SERVO&&bPusherTime.time() > 2*0.6)){ //going right
                robot.beaconPusher.setPosition(HOLD_SERVO);
            }

            //ball holder movement
            robot.ballHolder.setPower(gamepad2.left_stick_y);
//
            // Send telemetry message to signify robot running;
            telemetry.addData("Ball Holder Power",   "%.2f", gamepad2.left_stick_y);
            telemetry.addData("left",  "%.2f", leftPower);
            telemetry.addData("right", "%.2f", rightPower);
            telemetry.update();

            // Pause for metronome tick.  40 mS each cycle = update 25 times a second.
            robot.waitForTick(40);
        }
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
