/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.rrntu.motionmaster;
import gnu.io.*;
import java.awt.Color;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.TooManyListenersException;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 *
 * @author kasunprabash.ka
 */
public class CommManager {
  //  public class CommManager implements SerialPortEventListener {
    
//    //for containing the ports that will be found
//    private Enumeration ports = null;
//    //map the port names to CommPortIdentifiers
//    private HashMap portMap = new HashMap();
//    
//    //this is the object that contains the opened port
//    private CommPortIdentifier selectedPortIdentifier = null;
//    private SerialPort serialPort = null;
//    
//    //input and output streams for sending and receiving data
//    private InputStream input = null;
//    private OutputStream output = null;
//   
//    //just a boolean flag that i use for enabling
//    //and disabling buttons depending on whether the program
//    //is connected to a serial port or not
//    private boolean isConnected = false;
//    
//     //the timeout value for connecting with the port
//    final static int TIMEOUT = 2000;
//
//    //some ascii values for for certain things
//    final static int SPACE_ASCII = 32;
//    final static int DASH_ASCII = 45;
//    final static int NEW_LINE_ASCII = 10;
//
//    //a string for recording what goes on in the program
//    //this string is written to the GUI
//    String logText = "";
//    StringBuilder result = new StringBuilder();
//    StringBuilder logLine = new StringBuilder(); 
//    StringBuilder logMore = new StringBuilder(); 
//    long index = 0;
//    BufferedWriter bWriter;
//    FileWriter fWriter;
//    String fileName = "NONE";
//    
//    private MotionMaster mc;
//    private CommandDecoder cmdDecoder = new CommandDecoder();
//    
//    private boolean isRespAvailable = false;
//    private String response;
//    
//    
//    public String getResponse() {
//        return response;
//    }
//
//    public void setResponse(String response) {
//        this.response = response;
//    }
//    
//    public boolean isRespAvailable() {
//        return isRespAvailable;
//    }
//
//    public void setIsRespAvailable(boolean isRespAvailable) {
//        this.isRespAvailable = isRespAvailable;
//        result.delete(0, result.length());
//    }
//    
//    public CommManager(){
//        
//        //this.mc = mc;
//    }
//    
//    public void searchPorts(){
//        
//        ports = CommPortIdentifier.getPortIdentifiers();
//        
//        while(ports.hasMoreElements()){
//            
//            //Get the next element
//            CommPortIdentifier curPort = (CommPortIdentifier)ports.nextElement();
//
//            //get only serial ports
//            if (curPort.getPortType() == CommPortIdentifier.PORT_SERIAL)
//            {
//                portMap.put(curPort.getName(), curPort);
//                System.out.println(curPort.getName());
//            }
//        }
//       
//        
//    }
//    
//    public void connect(String sp)
//    {
//        //String selectedPort = (String)window.cboxPorts.getSelectedItem();
//        selectedPortIdentifier = (CommPortIdentifier)portMap.get(sp);
//
//        CommPort commPort = null;
//
//        try
//        {
//            //the method below returns an object of type CommPort
//            commPort = selectedPortIdentifier.open("TigerControlPanel", TIMEOUT);
//            //the CommPort object can be casted to a SerialPort object
//            serialPort = (SerialPort)commPort;
//
//            //logging
//            logText = sp + " opened successfully.\\n";          
//            
//        }
//        catch (PortInUseException e)
//        {
//            logText = sp + " is in use. (" + e.toString() + ")\\n";
//                        
//        }
//        catch (Exception e)
//        {
//            logText = "Failed to open " + sp + "(" + e.toString() + ")\\n";
//            
//        }
//        System.out.println(logText);
//    }
//    
//    public void disconnect()
//    {
//        //close the serial port
//        try
//        {
//            //writeData(0, 0);
//
//            serialPort.removeEventListener();
//            serialPort.close();
//            input.close();
//            output.close();
//            setConnected(false);
//
//
//            logText = "Disconnected.";
//            
//        }
//        catch (Exception e)
//        {
//            logText = "Failed to close " + serialPort.getName() + "(" + e.toString() + ")";
//
//        }
//        System.out.println(logText);
//    }
//    
//    //Open the IO streams from an open port
//    public boolean initIOStream()
//    {
//        //return value for whather opening the streams is successful or not
//        boolean isSuccess = false;
//
//        try {
//            //
//            input = serialPort.getInputStream();
//            output = serialPort.getOutputStream();
//                        
//            isSuccess = true;
//            System.out.println(logText);
//            return isSuccess;
//        }
//        catch (IOException e) {
//            logText = "I/O Streams failed to open. (" + e.toString() + ")";
//            System.out.println(logText);
//            return isSuccess;
//        }
//        
//    }
//    
//    final public boolean getConnected()
//    {
//        return isConnected;
//    }
//    
//    public void setConnected(boolean bConnected)
//    {
//        this.isConnected = isConnected;
//    }
//    
//    public void writeData(byte[] b)
//    {
//        try
//        {
//            output.write(b);
//            output.flush();
//
//        }
//        catch (Exception e)
//        {
//            logText = "Failed to write data. (" + e.toString() + ")";
//
//        }
//        //System.out.println(logText);
//    }
//    
//        public void writeData(int b)
//    {
//        try
//        {
//            output.write(b);
//            output.flush();
//
//        }
//        catch (Exception e)
//        {
//            logText = "Failed to write data. (" + e.toString() + ")";
//
//        }
//        //System.out.println(logText);
//    }
//    
//    public void initListener()
//    {
//        try
//        {
//            serialPort.addEventListener(this);
//            serialPort.notifyOnDataAvailable(true);
//            //serialPort.setLowLatency();
//        }
//        catch (TooManyListenersException e)
//        {
//            logText = "Too many listeners. (" + e.toString() + ")";
//
//        }
//    }
//    
//    public void manRead(){
//        
//        try
//            {
//                byte singleData = (byte)input.read();
//                if(singleData != 0){
//                //System.out.println(singleData);
//                char[] c = new char[1];
//                c[0] = (char)singleData;
//                String s = new String(c);
//                System.out.print(s);
//                
//                }
//            }catch (Exception e)
//            {
//                logText = "Failed to read data. (" + e.toString() + ")";                
//            }
//    }
//    public void manualRead(){
//        
//        
//    }
//
//    
//    public void processData(){
//        
//       
//    }
//    @Override
//    public void serialEvent(SerialPortEvent evt) {
//        
//        processData();
////        if (evt.getEventType() == SerialPortEvent.DATA_AVAILABLE)
////        {
////            try
////            {
////                byte singleData = (byte)input.read();
////                //System.out.println(singleData);
////                char[] c = new char[1];
////                c[0] = (char)singleData;
////                String s = new String(c);
////                result.append(s);
////                System.out.println(result.toString());
////                //input.read(b);
////                
////                logText = String.valueOf("");
////                
//////                if(result.length() > 5 && result.indexOf(".") >= 0){
//////                    
//////                    int x = result.length() - result.indexOf(".");
//////                    
//////                    if(x >= 3){
//////                        
//////                        
//////                        index++;
//////                        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss");
//////                        LocalDateTime now = LocalDateTime.now();
//////                        
//////                        logLine.append(String.valueOf(index));
//////                        logLine.append(",");
//////                        logLine.append(dtf.format(now));
//////                        logLine.append(",");
//////                        logLine.append(result);
//////                        logLine.append("\r\n");
//////
//////                        response = result.toString();
//////                        result.delete(0, result.length());
//////                        //lets save to the file for every 10 reads
//////                        logMore.append(logLine);
//////                        if(index%10 == 0){
//////                            
//////                            try {
//////                                  
//////                                if(fileName.equals("NONE")){
//////                                    dtf = DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss"); 
//////                                    fileName = "C:\\Users\\KASUNPRABASH.KA\\Logs\\" + dtf.format(now) +".txt";
//////                                    
//////                                }
//////                                 fWriter = new FileWriter(fileName,true);
//////                                 bWriter = new BufferedWriter(fWriter);
//////                                 bWriter.write(logMore.toString());
//////                                 bWriter.close();
//////                                logMore.delete(0, logMore.length());
//////                            }  catch (IOException e) {
////// 
//////                                System.out.print(e.getMessage());
//////                            }
//////                        }
//////                        System.out.println(logLine.toString());
//////                        logLine.delete(0, logLine.length());
//////                    }
//////                }
////
////            }
////            catch (Exception e)
////            {
////                logText = "Failed to read data. (" + e.toString() + ")";                
////            }
////        }
////        //System.out.println(logText);
//    }
//    
//    public void read(){
//        
//        try
//            {
//                byte singleData = (byte)input.read();
//                System.out.println(singleData);
//                //input.read(b);
//                char[] c = new char[1];
//                c[0] = (char)singleData;
//                String s = new String(c);
//                if (singleData != NEW_LINE_ASCII)
//                {
//                    //logText = new String(new byte[] {singleData});    
//                    logText = s;
//                }
//                else
//                {
//                   logText = s;
//                }
//            }
//            catch (Exception e)
//            {
//                logText = "Failed to read data. (" + e.toString() + ")";                
//            }
//        
//    }
}
