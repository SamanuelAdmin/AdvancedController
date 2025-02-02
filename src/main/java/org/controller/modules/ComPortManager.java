package org.controller.modules;

import com.fazecast.jSerialComm.SerialPort;
import org.controller.codetemplates.LoggerInterface;

public class ComPortManager implements AutoCloseable {
    LoggerInterface logger;

    private SerialPort serialPort;
    private String serialPortName;
    private int baudRate = 9600;

    // constructor
    public ComPortManager(LoggerInterface logger) {
        this.logger = logger;

        serialPort = SerialPort.getCommPorts()[0];
        serialPort.openPort();
        serialPort.setComPortParameters(this.baudRate, 8, 1, 0);

        serialPortName = serialPort.getSystemPortName();

        this.logger.log("Choose base serial port - " + serialPortName);

        if (!serialPort.isOpen()) {
            this.logger.error("Could not open serial port - " + serialPortName);
        }
    }

    // destructor
    public void close() {
        if(serialPort != null) {
            if (serialPort.isOpen())
                serialPort.closePort();
        }
    }


    public void setBaudRate(int baudRate) {
        this.baudRate = baudRate;
    }


    public boolean changeSerialPort(String newSerialPortName) {
        serialPort = SerialPort.getCommPort(newSerialPortName);
        serialPort.openPort();
        serialPort.setComPortParameters(this.baudRate, 8, 1, 0);

        if (serialPort.isOpen()) {
            serialPortName = newSerialPortName;
            this.logger.log("Serial port changed to " + newSerialPortName);

            return true;
        } else {
            this.logger.error("Cannot change serial port cuz port is not open. Current port - " + serialPortName);

            return false;
        }
    }

    public String getSerialPortName() {
        return serialPortName;
    }

    public SerialPort[] getAllSerialPorts() {
        return SerialPort.getCommPorts();
    }


    public static String bytesToString(byte[] bytes) {
        String result = "";

        for (byte b : bytes) {
            result += (char) b;
        }

        return result;
    }


    public String readFromSerialPort() {
        while (serialPort.isOpen()) {
            if (serialPort.bytesAvailable() > 0) {
                byte[] buffer = new byte[serialPort.bytesAvailable()];
                serialPort.readBytes(buffer, serialPort.bytesAvailable());

                return bytesToString(buffer);
            }
        }

        return null;
    }

}
