package org.controller.modules;

import org.controller.codetemplates.LoggerInterface;
import org.controller.codetemplates.Testable;

import java.time.LocalDate;
import java.time.LocalTime;

public class Logger implements Testable, LoggerInterface {

    private static String getDateAndTime() {
        return LocalDate.now() + " " + LocalTime.now().toString().substring(0, 11);
    }

    public void log(boolean ifAddTime, String... args) {
        for (String arg : args) {
            boolean firstLine = true;

            for (String line : arg.split("\n")) {
                if (firstLine && ifAddTime) {
                    System.out.println(getDateAndTime() + " -> " + line);
                    firstLine = false;

                } else {
                    for (char symbol : getDateAndTime().toCharArray()) {
                        System.out.print(' ');
                    }

                    System.out.println(" -> " + line);
                }
            }
        }
    }

    public void log(String... args) {
        log(true, args);
    }

    public void error(String... args) {
        System.out.println("================ [ERROR] ================");
        this.log(true, args);
        System.out.println("=========================================");
    }


    @Override
    public void test() {
        log("One string log");
        log("String for logger 1\nString for logger 2");
        log("Info for log 1", "Info for log 3", "Info for log 3");
    }
}
