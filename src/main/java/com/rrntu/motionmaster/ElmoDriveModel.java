/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.rrntu.motionmaster;

import gnu.io.CommPortIdentifier;
import java.text.DecimalFormat;
import java.util.HashMap;

/**
 *
 * @author kasunprabash.ka
 */
public class ElmoDriveModel {
    
    
    public ElmoDriveModel(){
        
    }
    
    public ElmoDriveModel(Sequencer s,String strComPort, HashMap portmap, boolean isMaster){
//        this.strComPort = strComPort;
//        this.portmap = portmap;
//        cmdDecoder = new CommandDecoder(this);
//        comPort = new ComManager(s,portmap, isMaster);
//        currentWindow = new double[50];
//        iqCount = syncDelay;
//        pidController = new MiniPID(0.5,0.0,0.25);
//        pidController.setOutputLimits(10.0);
        //pidController.setDirection(false);
        
    }
    public ElmoDriveModel(StateMachine s,String strComPort, HashMap portmap, boolean isMaster){
        this.strComPort = strComPort;
        this.portmap = portmap;
        cmdDecoder = new CommandDecoder(this);
        comPort = new ComManager(s,portmap, isMaster,this);
        currentWindow = new double[1000];
        iqCount = syncDelay;
        pidController = new MiniPID(0.4,0.0,0.4);
        pidController.setOutputLimits(9.0);
        //pidController.setDirection(false);
    }
    
    

    //Declare the drive contants here for easy access.
    
    public double getCurrentReal() {
        return currentReal;
    }

    public void setCurrentReal(double currentReal) {
        this.currentReal = currentReal;
    }

    public double getCurrentImaginary() {
        return currentImaginary;
    }

    public void setCurrentImaginary(double currentImaginary) {
        this.currentImaginary = currentImaginary;
    }

    public double getSpeed() {
        return speed;
    }

    public void setSpeed(double speed) {
        this.speed = speed;
    }

    public boolean isIsConnected() {
        return isConnected;
    }

    public void setIsConnected(boolean isConnected) {
        this.isConnected = isConnected;
    }

    public boolean isIsHome() {
        return isHome;
    }

    public void setIsHome(boolean isHome) {
        this.isHome = isHome;
    }

    public double getMotorStatus() {
        return motorStatus;
    }

    public void setMotorStatus(double motorStatus) {
        this.motorStatus = motorStatus;
    }

    public long getStatus() {
        return status;
    }

    public void setStatus(long status) {
        this.status = status;
    }

    public int getPosition() {
        return position;
    }

    //private data area
    public void setPosition(int position) {
        this.position = position;
    }
    
    public int getTargetPosition() {
        return targetPosition;
    }

    //private data area
    public void setTargetPosition(int targetPosition) {
        this.targetPosition = targetPosition;
    }
    
    public double getCurrent() {
        return current;
    }

    public void setCurrent() {
        
        this.current = Math.sqrt(currentReal*currentReal + currentImaginary*currentImaginary );
    }
    public void setDriveMode(String s) {
        this.driveMode = s;
    }

    public String getDriveMode() {
        return this.driveMode;
    }
    
    public ComManager getComManager() {
        return this.comPort;
    }
    private double currentReal = 0.0;
    private double currentImaginary;
    private double speed;
    private boolean isConnected;    
    private boolean isHome;
    private double motorStatus;
    private long status;
    private int position;
    private int targetPosition;
    private double current;
    private String driveMode;
    private double maxSpeed;
    private String serialNumber;
    private int baudrate;
    private ComManager comPort;

    private HashMap portmap;
    private String strComPort;
    private boolean isMaster = false;
    private DriveStatus driveStatus;
    private CommandDecoder cmdDecoder;
    private boolean isForwardMotion = true;
    
    private int windowIndex = 0;
    private boolean isAveraging;
    //Transfer from Command Decoder
    String curCommand = "";
    private int syncDelay = 15;
    private int iqCount;
    private MiniPID pidController;
    private double prevCurrent = 0.0;
    private double slaveCurrent = 0.0;
    private double prevSlaveCurrent = 0.0;
    private double currentSetPoint = 0.0;
    public String logFile ="";
    
    //Averaging filter
    private double currentWindow[];
    private int samples = 50;
    private int index = 0;

    public int getIndex() {
        return index;
    }
    
    public int getSamples() {
        return samples;
    }

    public void setSamples(int samples) {
        this.samples = samples;
    }
    
    
    //String status = "";
    
    public int getSyncDelay(){
        return syncDelay;
    }
    
    public void setSyncDelay(int syncDelay){
        this.syncDelay = syncDelay;
    }
    public boolean isAveraging(){
        return this.isAveraging;
    }
    
    public void setIsAveraging(boolean b){
        this.isAveraging = b;
    }
    public CommandDecoder getDecoder(){
        return this.cmdDecoder;
    }
    
    
    public boolean isIsForwardMotion() {
        return isForwardMotion;
    }

    public void setIsForwardMotion(boolean isForwardMotion) {
        this.isForwardMotion = isForwardMotion;
    }
    public static final double MAX_CURRENT = 15;
    
    public DriveStatus getDriveStatus(){
        return driveStatus;
    }
    
    public void setDriveStatus(DriveStatus ds){
        this.driveStatus = ds;
    }
   
    public String getTorqueCommand(){
        
        String valStr = "";
        StringBuilder sb = new StringBuilder();
        sb.append("TC=");
        
        if(currentReal <= MAX_CURRENT){
            
//            //IQ value + previous TC sum divided by 2 can be take as the target
//            //currentSetPoint = (currentReal + slaveCurrent) / 2;
//            currentSetPoint = (currentReal + slaveCurrent) *0.05;
//            pidController.setSetpoint(currentSetPoint);
//            //new Slave current calculated through Controller, we need the set point and IQ values to be 
//            //come to same value at some point
//            //slaveCurrent = pidController.getOutput(currentReal,currentSetPoint);
//            slaveCurrent = pidController.getOutput(currentReal,currentSetPoint);

//LP Average
            slaveCurrent = this.getMeanCurrent();
            //new DecimalFormat("#.######").format(slaveCurrent);
            slaveCurrent = Math.floor(slaveCurrent * 1000000) / 1000000;
            
// //PID    
//            currentSetPoint = (currentReal + slaveCurrent) / 2;
//            //Set point is recent iq, 
//            slaveCurrent = pidController.getOutput(currentReal,currentSetPoint);
            
            try{                
                valStr = Double.toString(slaveCurrent);
                sb.append(valStr);
                sb.append("\r");
            }catch(Exception e){
                return "TC=0\r";
            }
            
        }else{
            return "TC=0\r";
        }
        return sb.toString();
    }
//    public String getTorqueCommand(){
//        
//        String valStr = "";
//        StringBuilder sb = new StringBuilder();
//        sb.append("TC=");
//        
//        if(currentReal <= MAX_CURRENT){
//            
//            try{
//                //valStr = Double.toString(currentReal/2);
//                
//                //If position is about to reach remove the slave suport
//                int positionError = 0;
//                
//                positionError = targetPosition - position;
//                //System.out.println("Target:"+String.valueOf(targetPosition)+"Position:"+String.valueOf(position) + "PE:"+String.valueOf(positionError));
//                if((positionError < 5 && positionError >=0) || (positionError > -5 && positionError <=0)){
//                    //return "TC=0\r";
//                }
//                //Averaging will onle be effected when user commad to AVG mode.
//                if(isAveraging){
//                    currentReal = getMovingAverage();
//                    System.out.println("Moving Avg:"+String.valueOf(currentReal));
//                }
//                //If moving forward 
//                if(isForwardMotion && currentReal < 0){
//                    //System.out.println("FWD-");
//                    return "TC=0\r";
//                    
//                    
//                }else if(!isForwardMotion && currentReal > 0){
//                    //System.out.println("RVD+");
//                    return "TC=0\r";
//                }
//                valStr = Double.toString(currentReal*0.8);
//                sb.append(valStr);
//                sb.append("\r");
//            }catch(Exception e){
//                return "TC=0\r";
//            }
//            
//        }
//        return sb.toString();
//    }
    
    public void updateMovingAverage(double current){
        
        currentWindow[windowIndex%50] = current;
        windowIndex++;
    }
    
    public void enqueCurrentBuffer(double current, int syncDelay){
        
        currentWindow[(windowIndex++)%syncDelay] = current;
        //windowIndex++;
    }
    
    public double getCurrentBuffer(int syncDelay){
        
        //Skip number of samples and get the one after delay amount.
        return currentWindow[syncDelay];
        
    }
    
    public void updateCurrentWindow(double iq){
        
        //Roll over the values to the buffer
        currentWindow[(index%samples)] = iq;
        //increment at each update to move to the next location
        index++;
        
    }
    
    public double getMeanCurrent(){
        
        double mean = 0.0;
        double sum = 0.0;
        //calculate the average
        for(int i =0; i< samples;i++){
            sum = sum + currentWindow[i];
        }
        mean  = sum/samples;
        return mean;
        
    }
    
    public double getMovingAverage(){
        
        double sum = 0.0;
        
        for(int i =0; i< 20;i++){
            sum = sum + currentWindow[i];
        }
        return sum/20;
    }
}
