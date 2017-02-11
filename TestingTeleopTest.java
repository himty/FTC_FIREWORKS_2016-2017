package org.firstinspires.ftc.teamcode;

import android.os.Environment;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.CompassSensor;
import com.qualcomm.robotcore.util.ElapsedTime;

import java.io.File;
import java.io.FileWriter;
import java.io.PrintWriter;

@TeleOp(name="TestingTeleopTest", group="FIREWORKS")
public class TestingTeleopTest extends LinearOpMode {
    /* Declare OpMode members. */
    HardwareTest    robot               = new HardwareTest();              // Use a K9'shardware
    ElapsedTime     ballDropperTime     = new ElapsedTime(1000); //starting time is high so doesn't mess with timing
    ElapsedTime     bPusherTime         = new ElapsedTime(1000);
    ElapsedTime     recordingTime       = new ElapsedTime(1000);

    final double HOLD_SERVO = 0.5;
    final double BALL_DROPPER_UP_SERVO = 0.1;
    final double BALL_DROPPER_DOWN_SERVO = 0.9;
    final double BALL_LEFT_SERVO = 0.45;
    final double BALL_RIGHT_SERVO = 0.54;

    final double MAX_JOYSTICK_VALUE = 1;

    double targetLeftPower;
    double targetRightPower;

    //'Y' if the driver is starting a new move
    //' ' if not
    //ex: Y = not turning anymore; now going straight
    char isSwitching = ' ';

    PrintWriter pw = null;

    @Override
    public void runOpMode(){

        double leftPower;
        double rightPower;
        double linearPower;

        /* Initialize the hardware variables.
         * The init() method of the hardware class does all the work here
         */
        robot.init(hardwareMap);

        //init files
        File directory = new File(Environment.getExternalStorageDirectory().getAbsolutePath()+"/LearningData");
        if(!directory.exists()){
            directory.mkdir();
        }
        int count = 1;
        File learningFile;
        while (true){
            learningFile = new File(directory.getAbsolutePath() + "/learning_data"+count+".txt");
            if(!learningFile.exists()) {
                break;
            }
            count++;
        }

        try{
            pw = new PrintWriter(new FileWriter(learningFile, true)); //true = append to file
        } catch (Exception e) {
            e.printStackTrace();
            telemetry.addData("PrintWriter Intialize Error", e.getStackTrace());
            telemetry.update();
            try {
                wait(10000000);
            }
            catch (Exception ex) {
                //meh
            }

        }

        //calibrate compass sensor
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
        final double COMP_INIT_DIRECTION = robot.compSensor.getDirection();

        // Send telemetry message to signify robot waiting;
        telemetry.addData("Say", "Hello Driver");    //
        telemetry.update();

        // Wait for the game to start (driver presses PLAY)
        waitForStart();

//        robot.ballDropper.setPosition(0.1);
        // run until the end of the match (driver presses STOP)
        while (opModeIsActive()) {

            //drive train
            targetLeftPower = (gamepad1.right_stick_y - 2*gamepad1.right_stick_x);
            targetRightPower = (gamepad1.right_stick_y + 2 * gamepad1.right_stick_x);

            if (targetLeftPower < 0 && targetRightPower > 0) {
                targetRightPower = targetRightPower * 0.5;
            }

            if (gamepad1.right_bumper){
                targetLeftPower /= 5;
                targetRightPower /= 5;
            }

//            targetLeftPower = scaleInput(Range.clip(targetLeftPower, -1, 1));
//            targetRightPower = scaleInput(Range.clip(targetLeftPower, -1, 1));
            robot.leftMotor.setPower(targetLeftPower);
            robot.leftMotor2.setPower(targetLeftPower);
            robot.rightMotor.setPower(targetRightPower);
            robot.rightMotor2.setPower(targetRightPower);
//            telemetry.addData("Powers", "Left: " + String.format("%.2f", leftPower) + " " + "Right: " + String.format("%.2f", rightPower));

            //linear slide
            linearPower = (float) scaleInput(-1*gamepad2.right_stick_y);
            robot.linearSlide.setPower((double)linearPower);

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
//
            // Send telemetry message to signify robot running;
//            telemetry.addData("Ball Dropper Position",   "%.2f", robot.ballDropper.getPosition());
//            telemetry.addData("true/false", Double.isNaN(robot.ballDropper.getPosition()));
//            telemetry.addData("left", "%.2f", leftPower);
//            telemetry.addData("right", "%.2f", rightPower);


            isSwitching = ' ';
            if (gamepad1.x) {
                isSwitching = 'Y';
            }

            telemetry.addData("Compass", "%.2f", COMP_INIT_DIRECTION - robot.compSensor.getDirection());
            telemetry.update();

            //Writing files
            if (recordingTime.milliseconds() >= 200) {
                pw.printf("/"+"%.2f"+"\t"+"%.2f"+"\t"+"%d"+"\t"+"%d"+"\t"+"%.2f"+"\t"+"%c"+ "%n",
                        robot.leftMotor.getPower(), robot.rightMotor.getPower(),
                        robot.leftMotor.getCurrentPosition(), robot.rightMotor.getCurrentPosition(),
                        robot.compSensor.getDirection()-COMP_INIT_DIRECTION, isSwitching);
                recordingTime.reset();
            }

            // Pause for metronome tick.  40 mS each cycle = update 25 times a second.
            robot.waitForTick(40);
        }

        //close
        pw.close();
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
