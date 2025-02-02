package org.controller;

import com.fazecast.jSerialComm.SerialPort;
import org.controller.modules.ComPortManager;
import org.controller.modules.ArgumentsHandler;
import org.controller.modules.Logger;

import java.io.IOException;
import java.time.LocalTime;


public class Main {
    // program settings
    private static int COMMAND_CHECKER_DELAY = 10; // in millis


    public static String bytesToString(byte[] bytes) {
        String result = "";

        for (byte b : bytes) {
            result += (char) b;
        }

        return result;
    }

    public static void main(String[] args) throws IOException, InterruptedException {
        // loading base components
        Logger logger = new Logger();
        ArgumentsHandler argumentsHandler = new ArgumentsHandler(args);

        if (!argumentsHandler.programArgs.isEmpty()) {
            logger.log("Load program with args: \n" + argumentsHandler.programArgs);
        } else {
            logger.log("Load program without args.");
        }


        ComPortManager comPortManager = new ComPortManager(logger);

        if (argumentsHandler.hasArg("p")) {
            boolean isCorrect = comPortManager.changeSerialPort(
                    argumentsHandler.getArg("p")
            );

            if (!isCorrect) return;
        }

        if (argumentsHandler.hasOption("s")) {
            logger.log("Available serial ports:");

            SerialPort[] serialPorts = comPortManager.getAllSerialPorts();

            for (SerialPort serialPort : serialPorts) {
                logger.log(false, "(" + serialPort.getSystemPortName() + ")     " + serialPort.toString());
            }

            return;
        }

        if (argumentsHandler.hasArg("d")) {
            COMMAND_CHECKER_DELAY = Integer.parseInt(argumentsHandler.getArg("d"));
            logger.log("New delay for commands checker: " + COMMAND_CHECKER_DELAY);
        }


        while (true) {
            String gottenCommand = comPortManager.readFromSerialPort();

            if (!gottenCommand.equals("")) {
                logger.log(
                        "Gotten from port " + comPortManager.getSerialPortName()
                                + ": \"" + gottenCommand + "\""
                );

                if (gottenCommand.equals("do sleep")) {
                    System.out.println(LocalTime.now() + " -> Doing sleep");
                    Process process = Runtime.getRuntime().exec("cmd.exe /c powercfg -hibernate off && rundll32.exe powrprof.dll,SetSuspendState 0,1,0");
                    process.waitFor();
                } else if (gottenCommand.equals("do poweroff")) {
                    System.out.println(LocalTime.now() + " -> Power off");
                    Process process = Runtime.getRuntime().exec("shutdown /s /t 0");
                    process.waitFor();
                }
            }

            Thread.sleep(COMMAND_CHECKER_DELAY);
        }
    }
}