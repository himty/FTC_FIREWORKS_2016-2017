package org.firstinspires.ftc.teamcode;

import android.graphics.Path;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.CompassSensor;
import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.robotcore.util.ElapsedTime;

@TeleOp(name="TeleopTest", group="FIREWORKS")
public class TeleopTest extends LinearOpMode {
    /* Declare OpMode members. */
    HardwareTest    robot               = new HardwareTest();              // Use a K9'shardware
    ElapsedTime     ballDropperTime     = new ElapsedTime(1000); //starting time is high so doesn't mess with timing
    ElapsedTime     bPusherTime         = new ElapsedTime(1000);

    final double HOLD_SERVO = 0.5;
    final double BALL_DROPPER_UP_SERVO = 0.1;
    final double BALL_DROPPER_DOWN_SERVO = 0.9;
    final double BALL_LEFT_SERVO = 0.45;
    final double BALL_RIGHT_SERVO = 0.54;
    final double MAX_JOYSTICK_VALUE = 1;

    double DELTA_DRIVE_POWER;
    double targetLeftPower;
    double targetRightPower;
    double linearPower;

//    double[] scaleArray = { 0, 10, 40, 90, 160, 250, 360, 490, 640, 810, 1000, 1210,
//        1440, 1690, 1960, 2250, 2560, 2890, 3240, 3610, 4000};
    double[] scaleArray = { -1.00, -0.12, 0.0, 0.12, 1.00 };
    int currIndexR = 3;
    int currIndexL = 3;

    @Override
    public void runOpMode(){
        /* Initialize the hardware variables.
         * The init() method of the hardware class does all the work here
         */
        robot.init(hardwareMap);

        // Send telemetry message to signify robot waiting;
        telemetry.addData("Say", "Hello Driver");    //
        telemetry.update();

        DELTA_DRIVE_POWER = robot.leftMotor.getMaxSpeed() / 20;

//        //calibrate compass sensor
//        robot.compSensor.setMode(CompassSensor.CompassMode.CALIBRATION_MODE);
//        bPusherTime.reset();
//        while (bPusherTime.seconds() < 4){
//            ;
//        }
//        if (robot.compSensor.calibrationFailed()){
//            telemetry.addData("Say", "Compass Calibration Failed");    //
//            telemetry.update();
//        }
        robot.compSensor.setMode(CompassSensor.CompassMode.MEASUREMENT_MODE);

        // Wait for the game to start (driver presses PLAY)
        waitForStart();

//        robot.ballDropper.setPosition(0.1);
        // run until the end of the match (driver presses STOP)
        while (opModeIsActive()) {

            //drive train
            targetLeftPower = (gamepad1.right_stick_y - 2*gamepad1.right_stick_x);
            targetRightPower = (gamepad1.right_stick_y + 2 * gamepad1.right_stick_x);

            if (gamepad1.right_bumper){
                targetLeftPower /= 5;
                targetRightPower /= 5;
            }

            if (scaleArray[currIndexL] > targetLeftPower) {
                currIndexL--;
                if (currIndexL < 0) currIndexL = 0;
            }
            if (scaleArray[currIndexL] < targetLeftPower) {
                currIndexL++;
                if (currIndexL > 6) currIndexL = 6;
            }

            if (scaleArray[currIndexR] > targetLeftPower) {
                currIndexR--;
                if (currIndexR < 0) currIndexR = 0;
            }
            if (scaleArray[currIndexR] < targetLeftPower) {
                currIndexR++;
                if (currIndexR > 6) currIndexR = 6;
            }

            robot.leftMotor.setPower(scaleArray[currIndexL]);
            robot.rightMotor.setPower(scaleArray[currIndexR]);

            //linear slide
            robot.linearSlide.setPower(gamepad2.right_stick_y);

            //ball dropper movement
//            if (gamepad2.y && ballDropperTime.seconds() > 0.5){
//                if (robot.ballDropper.getPosition() == BALL_DROPPER_DOWN_SERVO){
//                    robot.ballDropper.setPosition(BALL_DROPPER_UP_SERVO);
//                    ballDropperTime.reset();
//                } else {
//                    robot.ballDropper.setPosition(BALL_DROPPER_DOWN_SERVO);
//                    ballDropperTime.reset();
//                }
//            }
//
//            //beacon pusher movement
//            if (gamepad1.b && robot.beaconPusher.getPosition() == HOLD_SERVO){
//                if (robot.beaconPusher.getPosition() == BALL_LEFT_SERVO){
//                    robot.beaconPusher.setPosition(BALL_RIGHT_SERVO);
//                } else {
//                    robot.beaconPusher.setPosition(BALL_LEFT_SERVO);
//                }
//                bPusherTime.reset();
//            }
//            if ((robot.beaconPusher.getPosition()==BALL_LEFT_SERVO&&bPusherTime.time() > 1*0.6-0.06)  //going left
//                    || (robot.beaconPusher.getPosition()==BALL_RIGHT_SERVO&&bPusherTime.time() > 2*0.6)){ //going right
//                robot.beaconPusher.setPosition(HOLD_SERVO);
//            }

            //ball holder movement
            robot.ballHolder.setPower(gamepad2.left_stick_y);

            //beacon pusher movement
            if (gamepad2.dpad_up){
                robot.beaconPusher.setPower(0.5);
            } else if (gamepad2.dpad_down) {
                robot.beaconPusher.setPower(-0.5);
            } else {
                robot.beaconPusher.setPower(0);
            }
//
            // Send telemetry message to signify robot running;
//            telemetry.addData("Ball Dropper Position",   "%.2f", robot.ballDropper.getPosition());
//            telemetry.addData("true/false", Double.isNaN(robot.ballDropper.getPosition()));
            telemetry.addData("left",  "%.2f", robot.leftMotor.getPower());
            telemetry.addData("right", "%.2f", robot.rightMotor.getPower());

            telemetry.addData("Light", "%.2f %.2f %.2f", robot.lightSensor.getRawLightDetected(), robot.lightSensor.getLightDetected(), robot.lightSensor.getRawLightDetectedMax());
//            telemetry.addData("Max Drive", "%d", robot.rightMotor.getMaxSpeed());
            telemetry.update();

            // Pause for metronome tick.  40 mS each cycle = update 25 times a second.
            robot.waitForTick(40);
        }
    }

    double scaleInput(double dVal)  {
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
