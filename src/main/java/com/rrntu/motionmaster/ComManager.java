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
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 *
 * @author kasunprabash.ka
 */
public class ComManager implements SerialPortEventListener {
    
    //for containing the ports that will be found
    private Enumeration ports = null;
    //map the port names to CommPortIdentifiers
    private HashMap portMap = new HashMap();
    
    //this is the object that contains the opened port
    private CommPortIdentifier selectedPortIdentifier = null;
    private SerialPort serialPort = null;
    
    //input and output streams for sending and receiving data
    private InputStream input = null;
    private OutputStream output = null;
   
    //just a boolean flag that i use for enabling
    //and disabling buttons depending on whether the program
    //is connected to a serial port or not
    private boolean isConnected = false;
    
     //the timeout value for connecting with the port
    final static int TIMEOUT = 2000;

    //some ascii values for for certain things
    final static int SPACE_ASCII = 32;
    final static int DASH_ASCII = 45;
    final static int NEW_LINE_ASCII = 10;

    //a string for recording what goes on in the program
    //this string is written to the GUI
    String logText = "";
    StringBuilder result = new StringBuilder();
    StringBuilder logLine = new StringBuilder(); 
    StringBuilder logMore = new StringBuilder(); 
    long index = 0;
    BufferedWriter bWriter;
    FileWriter fWriter;
    String fileName = "NONE";
    
    private boolean isMaster = false;
    private MotionMaster mc;
    private Sequencer sq;
    private StateMachine sm;
    //private CommandDecoder cmdDecoder = new CommandDecoder();
    private CommandDecoder cmdDecoder;
    
    private boolean isRespAvailable = false;
    private String response;
    
    public CommandDecoder getCommandDecoder() {
        return cmdDecoder;
    }
    
    public String getResponse() {
        return response;
    }

    public void setResponse(String response) {
        this.response = response;
    }
    
    public boolean isRespAvailable() {
        return isRespAvailable;
    }

    public void setIsRespAvailable(boolean isRespAvailable) {
        this.isRespAvailable = isRespAvailable;
        result.delete(0, result.length());
    }
    
    public ComManager(Sequencer sq, HashMap portmap, boolean isMaster){
        
        this.sq = sq;
        this.portMap = portmap;
        this.isMaster = isMaster;
    }
    public ComManager(MotionMaster ms){
        
        this.mc = ms;
    }
    public ComManager(StateMachine sm, HashMap portmap, boolean isMaster,ElmoDriveModel drive){
        
        this.sm = sm;
        this.portMap = portmap;
        this.isMaster = isMaster;
        //cmdDecoder = new CommandDecoder(drive); //Changed on 20221012
        cmdDecoder = drive.getDecoder();
    }
    public HashMap searchPorts(){
        
        ports = CommPortIdentifier.getPortIdentifiers();
        
        while(ports.hasMoreElements()){
            
            //Get the next element
            CommPortIdentifier curPort = (CommPortIdentifier)ports.nextElement();

            //get only serial ports
            if (curPort.getPortType() == CommPortIdentifier.PORT_SERIAL)
            {
                portMap.put(curPort.getName(), curPort);
                System.out.println(curPort.getName());
            }
        }
        return portMap;
        
    }
    
    public void connect(String sp)
    {
        //String selectedPort = (String)window.cboxPorts.getSelectedItem();
        selectedPortIdentifier = (CommPortIdentifier)portMap.get(sp);

        CommPort commPort = null;

        try
        {
            //the method below returns an object of type CommPort
            commPort = selectedPortIdentifier.open("TigerControlPanel", TIMEOUT);
            //the CommPort object can be casted to a SerialPort object
            serialPort = (SerialPort)commPort;
            serialPort.setSerialPortParams(57600,
                SerialPort.DATABITS_8, SerialPort.STOPBITS_1,
                SerialPort.PARITY_NONE);

            //logging
            logText = sp + " opened successfully./\n";          
            
        }
        catch (PortInUseException e)
        {
            logText = sp + " is in use. (" + e.toString() + ")\\n";
                        
        }
        catch (Exception e)
        {
            logText = "Failed to open " + sp + "(" + e.toString() + ")\\n";
            
        }
        System.out.println(logText);
    }
    
    public void disconnect()
    {
        //close the serial port
        try
        {
            //writeData(0, 0);

            serialPort.removeEventListener();
            serialPort.close();
            input.close();
            output.close();
            setConnected(false);


            logText = "Disconnected.";
            
        }
        catch (Exception e)
        {
            logText = "Failed to close " + serialPort.getName() + "(" + e.toString() + ")";

        }
        System.out.println(logText);
    }
    
    //Open the IO streams from an open port
    public boolean initIOStream()
    {
        //return value for whather opening the streams is successful or not
        boolean isSuccess = false;

        try {
            //
            input = serialPort.getInputStream();
            output = serialPort.getOutputStream();
                        
            isSuccess = true;
            System.out.println(logText);
            return isSuccess;
        }
        catch (IOException e) {
            logText = "I/O Streams failed to open. (" + e.toString() + ")";
            System.out.println(logText);
            return isSuccess;
        }
        
    }
    
    final public boolean getConnected()
    {
        return isConnected;
    }
    
    public void setConnected(boolean bConnected)
    {
        this.isConnected = isConnected;
    }
    
    public void writeData(byte[] b)
    {
        //System.out.println(new String(b, StandardCharsets.UTF_8)+ " to " + serialPort.getName());
        try
        {
            output.write(b);
            output.flush();
            

        }
        catch (Exception e)
        {
            logText = "Failed to write data. (" + e.toString() + ")";

        }
        //System.out.println(logText);
    }
    
        public void writeData(int b)
    {
        try
        {
            output.write(b);
            output.flush();

        }
        catch (Exception e)
        {
            logText = "Failed to write data. (" + e.toString() + ")";

        }
        //System.out.println(logText);
    }
    
    public void initListener()
    {
        try
        {
            serialPort.addEventListener(this);
            serialPort.notifyOnDataAvailable(true);
            //serialPort.setLowLatency();
        }
        catch (TooManyListenersException e)
        {
            logText = "Too many listeners. (" + e.toString() + ")";

        }
    }
    
    public void manRead(){
        //Changed on 20221010 for reduce the delay of 250ms read timeout
        try{
            if(input.available() > 0){
                processData();
            }
        }catch(IOException e){
                
            }
        
        
    }


    private void clearResponse(){
        
        result.delete(0, result.length());
    }
    
    private void clearLogResponse(){
        logLine.delete(0, logLine.length());
    }
    
    private void clearLogCollector(){
        logMore.delete(0, logMore.length());
    }
    public void processData(){
        
        

       try
            {
        byte singleData = (byte)input.read();
//                if(singleData == 13 && singleData == 10 && singleData <= 127 && singleData == 11 && singleData == 12 && singleData == 14
//                        && singleData == 15&& singleData == 16&& singleData == 17&& singleData == 18 && singleData == 19){
                if(singleData >=10 && singleData <= 94){
                //System.out.println(singleData);
                char[] c = new char[1];
                c[0] = (char)singleData;
                String s = new String(c);
                result.append(s);

                
                logText = String.valueOf("");
                
                //The minimum feasible response is 4 character long
                //The response should include CR and ;
                //Index of ; must be larger than CR
                //When this condition is satisfied, it will flag on the response is ready and clear the buffer
//                if(result.length() > 4 && 
//                   result.indexOf("\r") >= 0 &&
//                   result.indexOf(";") >= 0 && 
//                   (result.indexOf(";") - result.indexOf("\r")) >= 1 &&
//                       !isRespAvailable ) {

                    if(result.length() > 22){
                        clearResponse();
                    }
                    if(result.indexOf(";") >= 0 && !isRespAvailable ){
                        
                        //To prevent junk and will keep runnign when ; is present
                        if(result.indexOf(";") >= 0 && result.length()< 3){
                            clearResponse();
                            return;
                        }
                        if(result.indexOf("M?") >= 0 ){
                            clearResponse();
                            return;
                        }
                        //M,2022/10/10 14:03:13.810,-0.2562673;M,2022/10/10 14:03:13.810,-0.2562673;IM,2022/10/10 14:03:13.810,-0.2562673;IQM,2022/10/10 14:03:13.810,-0.2562673;IQ
                        if(result.indexOf(";") > result.length() || (result.charAt(0) < 65 ||result.charAt(0) > 90)){
                            clearResponse();
                            return;
                        }
                        index++;
                        
                        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss.SSS");
                        DateTimeFormatter dtf2 = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss.SSS");
                        LocalDateTime now = LocalDateTime.now();
                
                        if(isMaster){
                            logLine.append("M,");
                        }else{
                            logLine.append("S,");
                        }
                        logLine.append(dtf.format(now));
                        logLine.append(",");
//                        logLine.append(String.valueOf(index));
//                        logLine.append(",");
                        //StringBuilder tmp = new StringBuilder();
                        //tmp.append(result.toString());
                        //tmp.deleteCharAt(result.indexOf("\r"));
                        logLine.append(result);
                        //Debug
                        //logLine.append(",");
                        //logLine.append(String.valueOf(result.length()));
                        

                        logLine.deleteCharAt(logLine.indexOf("\r"));
                        logLine.append("\r\n");
                        
                        //delete the result
                        //result.delete(0, result.length());
                        //lets save to the file for every 10 reads
                        logMore.append(logLine);
//                        
                 //Loggin related activies.. mihgt not needed during the first phase of testings   
                        if(index%200 == 0 ){
                            
                            try {
                                  
                                if(fileName.equals("NONE")){
                                    
                                    File file = new File(System.getProperty("user.dir") + "\\Logs\\");
                                    if(!file.exists()){
                                        
                                        boolean dirCreated = file.mkdir();
                                    }
                                    
                                    dtf = DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss"); 
                                    dtf2 = DateTimeFormatter.ofPattern("yyyyMMdd_HHmm"); 
                                    //fileName = "C:\\Users\\KASUNPRABASH.KA\\Logs\\" + dtf.format(now) +".txt";
                                    fileName = System.getProperty("user.dir") + "\\Logs\\" + "ForceLog_" + dtf2.format(now) +".txt";
                                    
                                }
                                 fWriter = new FileWriter(fileName,true);
                                 bWriter = new BufferedWriter(fWriter);
                                 bWriter.write(logMore.toString());
                                 bWriter.close();
                                logMore.delete(0, logMore.length());
                            }  catch (IOException e) {
 
                                System.out.print(e.getMessage());
                            }
                        }
                        //removing any leading characters from the response
                        //result.
                        String tmp = result.toString();
                        //tmp.indexOf("\r");
                        //String cmdStr = result.substring((tmp.indexOf("\r")-2), tmp.indexOf(";"));
                        cmdDecoder.decodeResp(result.toString());
                        //cmdDecoder.decodeResp(result.substring(result.indexOf("\r") -2, result.indexOf(";")));
                        setResponse(result.toString());
                        clearResponse();
                        
                        
                        System.out.println(logLine.toString());
                        logLine.delete(0, logLine.length());
                        //setIsRespAvailable(true);
                    }
                }

                
            }
            catch (Exception e)
            {
                logText = "Failed to read data. (" + e.toString() + ")";                
            }
    }
    @Override
    public void serialEvent(SerialPortEvent evt) {
        
       processData();
 
    }
    
    public String read(){
        
        //System.out.print(".");
        try
            {
                byte singleData = (byte)input.read();
                
                //input.read(b);
                char[] c = new char[1];
                c[0] = (char)singleData;
                String s = new String(c);
                result.append(s);
                System.out.print(s);
                if(result.indexOf(";")>=0){
                    
                        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss.SSS");
                        LocalDateTime now = LocalDateTime.now();
                        
                        result.append(dtf.format(now));
                        result.append("\r\n");
//                        logLine.append(String.valueOf(index));
//                        logLine.append(",");
                        //result.append(result);
                        //result.append("\r\n");
                        System.out.print(result.toString());
                        
                        String s1 = result.toString();
                        clearResponse();
                    return s1;
                }
            }
            catch (Exception e)
            {
                logText = "Failed to read data. (" + e.toString() + ")"; 
                return "Error";
            }
        return null;
    }
    
    
    
    
    
}
