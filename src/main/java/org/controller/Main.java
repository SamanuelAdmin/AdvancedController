package org.controller;

import com.fazecast.jSerialComm.SerialPort;

import java.io.IOException;
import java.time.LocalTime;
import java.util.Arrays;

//TIP To <b>Run</b> code, press <shortcut actionId="Run"/> or
// click the <icon src="AllIcons.Actions.Execute"/> icon in the gutter.
public class Main {
    public static String bytesToString(byte[] bytes) {
        String result = "";

        for (byte b : bytes) {
            result += (char) b;
        }

        return result;
    }

    public static void main(String[] args) throws IOException, InterruptedException {
        String COMPortName;

        if (args.length < 1) {
            COMPortName = "COM5";
        } else {
            COMPortName = args[0];
        }

        SerialPort serialPort = SerialPort.getCommPort(COMPortName);
        serialPort.openPort();
        serialPort.setComPortParameters(9600, 8, 1, 0);

        if (!serialPort.isOpen()) {
            System.out.println("Serial port not open");
            return;
        }


        while (serialPort.isOpen()) {
            if (serialPort.bytesAvailable() > 0){
                byte[] gottenBytes = new byte[serialPort.bytesAvailable()];
                serialPort.readBytes(gottenBytes, serialPort.bytesAvailable());

                String gottenCommand = bytesToString(gottenBytes);

                if (gottenCommand.length() > 0) {
                    System.out.println("\"" + gottenCommand + "\"");

                    if (gottenCommand.equals("do sleep")) {
                        System.out.println(LocalTime.now() + " -> Doing sleep");
                        Process process = Runtime.getRuntime().exec("powercfg -hibernate off && rundll32.exe powrprof.dll,SetSuspendState 0,1,0");
                        process.waitFor();
                    } else if (gottenCommand.equals("do poweroff")) {
                        System.out.println(LocalTime.now() + " -> Power off");
                        Process process = Runtime.getRuntime().exec("shutdown /s /t 0");
                        process.waitFor();
                    }
                }
            }

        }

        serialPort.closePort();
    }
}