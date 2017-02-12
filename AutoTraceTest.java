package org.firstinspires.ftc.teamcode;

import android.os.Environment;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.util.ElapsedTime;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.Reader;
import java.util.Scanner;
import java.util.regex.Pattern;

//import org.firstinspires.ftc.robotcontroller.external.samples.HardwareTest;

@Autonomous
public class AutoTraceTest extends LinearOpMode{
    HardwareTest robot = new HardwareTest();
    private ElapsedTime runPeriod = new ElapsedTime(1000);


    static final double     FORWARD_SPEED = 1;
    static final double     TURN_SPEED    = -1;
    public void runOpMode() throws InterruptedException {

        /*
         * Initialize the drive system variables.
         * The init() method of the hardware class does all the work here
         */
        robot.init(hardwareMap);

        robot.leftMotor.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        robot.rightMotor.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        robot.leftMotor2.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        robot.rightMotor2.setMode(DcMotor.RunMode.RUN_TO_POSITION);

        File directory = new File(Environment.getExternalStorageDirectory().getAbsolutePath()+"/LearningData/" +
                "1.txt");

        // Send telemetry message to signify robot waiting;
        telemetry.addData("Status", "Ready to run");    //
        telemetry.update();

        // Wait for the game to start (driver presses PLAY)
        waitForStart();

        // Start
        int numThingsRead = 0;
        double leftPower = 0, rightPower = 0;
        String[] scannedThing;
        try {
            Scanner scan = new Scanner(directory);

            while (scan.hasNext()) {
                while (runPeriod.milliseconds() < 200) {
                    ;
                }
                
                scannedThing = scan.nextLine().split("\\s+");
                robot.leftMotor.setPower(Double.parseDouble(scannedThing[0]));
                robot.leftMotor2.setPower(Double.parseDouble(scannedThing[0]));
                robot.rightMotor.setPower(Double.parseDouble(scannedThing[1]));
                robot.rightMotor2.setPower(Double.parseDouble(scannedThing[1]));
            }
        }
        catch (Exception e) {
            telemetry.addData("Error", "Rippu...");
            telemetry.update();
        }

        // Step 4:  Stop and close the claw.
        robot.leftMotor.setPower(0);
        robot.leftMotor2.setPower(0);
        robot.rightMotor.setPower(0);
        robot.rightMotor2.setPower(0);

        telemetry.addData("Path", "Complete");
        telemetry.update();
        sleep(1000);
        idle();
    }

    void move(int left, int right, double power) {
        robot.leftMotor.setPower(power);
        robot.leftMotor2.setPower(power);
        robot.rightMotor.setPower(power);
        robot.rightMotor2.setPower(power);

        robot.leftMotor.setTargetPosition(left);
        robot.leftMotor2.setTargetPosition(left);
        robot.rightMotor.setTargetPosition(right);
        robot.rightMotor2.setTargetPosition(right);
    }
}
