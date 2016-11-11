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

import com.qualcomm.robotcore.eventloop.opmode.Disabled;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.util.ElapsedTime;
import com.qualcomm.robotcore.util.Range;

/**
 * This OpMode uses the common HardwareK9bot class to define the devices on the robot.
 * All device access is managed through the HardwareK9bot class. (See this class for device names)
 * The code is structured as a LinearOpMode
 *
 * This particular OpMode executes a basic Tank Drive Teleop for the K9 bot
 * It raises and lowers the arm using the Gamepad Y and A buttons respectively.
 * It also opens and closes the claw slowly using the X and B buttons.
 *
 * Note: the configuration of the servos is such that
 * as the arm servo approaches 0, the arm position moves up (away from the floor).
 * Also, as the claw servo approaches 0, the claw opens up (drops the game element).
 *
 * Use Android Studios to Copy this Class, and Paste it into your team's code folder with a new name.
 * Remove or comment out the @Disabled line to add this opmode to the Driver Station OpMode list
 */

@TeleOp(name="TeleopTest", group="K9bot")
public class TeleopTest extends LinearOpMode {

    enum servoStatus  {
        ARM_UP(0.45), ARM_DOWN(0.54),
        BALL_LEFT(0.45), BALL_RIGHT(0.45),
        HOLD(0.50);

        public final double value;
        servoStatus(double v){
            this.value = v;
        }
    };

    public enum servoPosition{
        ARM_DOWN, ARM_UP,
        BALL_LEFT, BALL_RIGHT;
    }

    /* Declare OpMode members. */
    HardwareTest    robot               = new HardwareTest();              // Use a K9'shardware
    servoStatus   armStatus           = servoStatus.HOLD;                   // Servo safe position
    servoStatus   bPusherStatus       = servoStatus.HOLD;                  // Servo safe position
    servoPosition   armPosition         = servoPosition.ARM_UP;
    servoPosition   bPusherPosition     = servoPosition.BALL_LEFT;
    ElapsedTime     armTime             = new ElapsedTime(1000); //starting time is high so doesn't mess with timing
    ElapsedTime     bPusherTime         = new ElapsedTime(1000);

    final double MAX_JOYSTICK_VALUE = 1.414;

    @Override
    public void runOpMode(){

        double leftPower;
        double rightPower;

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

            leftPower = gamepad1.right_stick_y - gamepad1.right_stick_x;
            rightPower = gamepad1.right_stick_y + gamepad1.right_stick_x;
            telemetry.addData("nom", String.format("%.2f", leftPower));
            leftPower = (float) scaleInput(leftPower);
            rightPower = (float) scaleInput(rightPower);
            robot.leftMotor.setPower((double) leftPower);
            robot.rightMotor.setPower((double) rightPower);
            telemetry.addData("Powers", "Left: " + String.format("%.2f", leftPower) + " " + "Right: " + String.format("%.2f", rightPower));

            //arm movement
            if (gamepad1.a && armStatus.equals(servoStatus.HOLD)){
                if (armPosition.equals(servoPosition.ARM_DOWN)){
                    armStatus = servoStatus.ARM_UP;
                    armPosition = servoPosition.ARM_UP;
                    robot.arm.setPosition(servoStatus.ARM_UP.value);
                } else {
                    armStatus = servoStatus.ARM_DOWN;
                    armPosition = servoPosition.ARM_DOWN;
                    robot.arm.setPosition(servoStatus.ARM_DOWN.value);
                }
                armTime.reset();
            }
            if ((armStatus.equals(servoStatus.ARM_DOWN)&&armTime.time() > 0.8)  //going down
                    || (armStatus.equals(servoStatus.ARM_UP)&&armTime.time() > 1.3)){ //going up
                armStatus = servoStatus.HOLD;
                robot.arm.setPosition(servoStatus.HOLD.value);
            }

            //ball holder movement
            if (gamepad1.b && bPusherStatus.equals(servoStatus.HOLD)){
                if (bPusherPosition.equals(servoPosition.BALL_LEFT)){
                    bPusherStatus = servoStatus.BALL_RIGHT;
                    bPusherPosition = servoPosition.BALL_RIGHT;
                    robot.beaconPusher.setPosition(servoStatus.BALL_RIGHT.value);
                } else {
                    bPusherStatus = servoStatus.BALL_LEFT;
                    bPusherPosition = servoPosition.BALL_LEFT;
                    robot.beaconPusher.setPosition(servoStatus.BALL_LEFT.value);
                }
                bPusherTime.reset();
            }
            if ((bPusherStatus.equals(servoStatus.BALL_LEFT)&&bPusherTime.time() > 0.8)  //going left
                    || (bPusherStatus.equals(servoStatus.BALL_RIGHT)&&bPusherTime.time() > 2)){ //going right
                bPusherStatus = servoStatus.HOLD;
                robot.beaconPusher.setPosition(servoStatus.HOLD.value);
            }

            // Send telemetry message to signify robot running;
//            telemetry.addData("arm",   "%.2f", armPosition);
//            telemetry.addData("bpusher",   "%.2f", bPusherPosition);
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
