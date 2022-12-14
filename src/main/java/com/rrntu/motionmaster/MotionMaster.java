/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Project/Maven2/JavaApp/src/main/java/${packagePath}/${mainClassName}.java to edit this template
 */

package com.rrntu.motionmaster;

import java.util.InputMismatchException;
import java.util.Scanner;

import java.util.InputMismatchException;
import java.util.Scanner;
import java.time.LocalDateTime;  
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter; 
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

/**
 *
 * @author kasunprabash.ka
 */
public class MotionMaster extends TimerTask{
    
    private ComManager comMgr = new ComManager(this);
    //private CommandDecoder respDcdr = new CommandDecoder();
    private ElmoDriveModel edm1 = new ElmoDriveModel();
    private ElmoDriveModel edm2 = new ElmoDriveModel();
    private Sequencer sequencer;
    
    @Override
    public void run() {
        //System.out.println("Timer task started at:"+new Date());
        //completeTask();
        //System.out.println("Timer task finished at:"+new Date());
    }

    private void completeTask() {
                   byte[] data1 = new byte[3];
        data1[0] = (byte) 'M';     
        data1[1] = (byte) 'O';
        data1[2] = (byte) '\r'; 

                this.comMgr.writeData(data1);

    }
    
    public MotionMaster(){
        
        //sequencer = new Sequencer(this);
    }
    
    public static String cleanResponse(String resp){
        
        //In all responses the follwing condition should be tested
        //1. either only two characters before '='
        //2. only two characters before '\r' or five when square brackets present
        //we test this here and trim from this index and index of ';'
        
        int equalIndex = resp.indexOf("=");
        int enterIndex = resp.indexOf("\r");
        int bracketIndex = resp.indexOf("[");
        int commaIndex = resp.indexOf(";");
        int startIndex;
        
        if(equalIndex > 0 || bracketIndex > 0){
            
            if(equalIndex > 0 && bracketIndex == -1){
                
                int start = equalIndex - 2;
                resp = resp.substring(start, commaIndex);
                return resp;
                
            }
            
            if(equalIndex > 0 && bracketIndex > 0){
                
                int start = bracketIndex - 2;
                resp = resp.substring(start, commaIndex);
                return resp;
                
            }
            
            if(bracketIndex > 0){
                
                int start = bracketIndex - 2;
                resp = resp.substring(start, commaIndex);
                return resp;
                
            }
            
            
            
        }else{
            
            if(enterIndex >= 2){
                
                int start = enterIndex - 2;
                resp = resp.substring(start, commaIndex);
                return resp;
            }
        }
        return resp;
    }
    
    public static void main(String[] args) {
        
        //testings
        Double val = Double.valueOf("-0.04881281");
        String sval = String.valueOf(val);
        
        String str = cleanResponse("?SR\r17000;");
        str = cleanResponse("?DV[1]\r17000;");
        str = cleanResponse("?TC=0.0\r;");
        str = cleanResponse("??DV[1]=0.0\r;");
        //ComManager comMgr = new ComManager();
        MotionMaster mCont = new MotionMaster();
        Sequencer sequencer = new Sequencer();
        
        StateMachine sm = new StateMachine();
        sm.start();
        sm.on(mCont.comMgr);
        //sequencer.start();
        //sequencer.on(mCont.comMgr);
        
        //mCont.startMotionController();
        
        //DateTimeFormatter fmt = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS");
        // convert the timestamp to a zoneddatetime
        //java.time.Clock.systemUTC().instant()
        //ZonedDateTime z = toInstant().atZone(ZoneId.of("Asia/Calcutta"));
        
        //StringBuilder sbTimeStamp = new StringBuilder(java.time.Clock.systemUTC().instant().toString());
        //sbTimeStamp.replace(0, 0, str)
//        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss.SSS");
//        LocalDateTime now = LocalDateTime.now();
//        System.out.println(dtf.format(now));   
//        try{
//                
//                Thread.sleep(100);
//            }catch(Exception e){
//                
//            }
//        
//
//        //System.out.println(java.time.Clock.systemUTC().instant());
//        now = LocalDateTime.now();
//        System.out.println(dtf.format(now));  
        //System.out.println("List all avaialable ports");
        //mCont.comMgr.searchPorts();
        
        //System.out.println("Select the COM port connected..");
        //String comDrive1 = mCont.getInput();
        //System.out.println(comDrive1);
        
        //System.out.println("Select the COM port for drive2");
        //String comDrive2 = mSync.getInput();
        //System.out.println(comDrive2);
        
        //mCont.comMgr.connect(comDrive1);
        
        
        //mCont.comMgr.initIOStream();
        //mCont.comMgr.initListener();
        //mSync.comMgr.initListener();


        //System.out.println("Writing simple data..");
        //mSync.comMgr.writeData(data);
        //System.out.println("Reading data from SAUTER FH100 @ ~50Hz..");
//        byte[] data1 = new byte[3];
//        data1[0] = (byte) 'S';     
//        data1[1] = (byte) 'N';
//        data1[2] = (byte) '\r';
        
        //Timer timer = new Timer(true);
        //timer.scheduleAtFixedRate(mCont, 10,10 );
        
        //System.out.println("TimerTask started");
        while(true){
                
          
                //data1[0] = (byte)(data1[0] +1)%80; 
            //mCont.comMgr.writeData(data1);
            //mCont.comMgr.
            //mCont.comMgr.manualRead();
            //mCont.comMgr.manRead();
            
            
//            if(mCont.comMgr.isRespAvailable()){
//                
//               mCont.respDcdr.decodeResp(mCont.comMgr.getResponse());
//               mCont.comMgr.setIsRespAvailable(false);
//                
//            }
//            try{
//                
//                Thread.sleep(100);
//                mCont.comMgr.writeData(data1);
//            }catch(Exception e){
//                
//            }
//            //mSync.comMgr.read();
//            //try{
//            //Thread.sleep(100);
//
//            //}catch(Exception ex){
//                
//            //}
        }
        //comMgr.searchPorts();
    }
    
//    public void startMotionController(){
//        
//        System.out.println("List all avaialable ports");
//        mCont.comMgr.searchPorts();
//        
//        System.out.println("Select the COM port connected..");
//        String comDrive1 = mCont.getInput();
//        System.out.println(comDrive1);
//        
//        //System.out.println("Select the COM port for drive2");
//        //String comDrive2 = mSync.getInput();
//        //System.out.println(comDrive2);
//        
//        mCont.comMgr.connect(comDrive1);
//        //mSync.comMgr.connect(comDrive2);
//        
//        mCont.comMgr.initIOStream();
//        //mSync.comMgr.initListener();
//
//
//        //System.out.println("Writing simple data..");
//        //mSync.comMgr.writeData(data);
//        System.out.println("Reading data from SAUTER FH100 @ ~50Hz..");
//        byte[] data1 = new byte[3];
//        data1[0] = (byte) 'S';
//        data1[1] = (byte) 'R';
//        data1[2] = (byte) '\r';
//        while(true){
//          
//
//            mCont.comMgr.writeData(data1);
//            mCont.comMgr.manualRead();
//            
//            if(mCont.comMgr.isRespAvailable()){
//                
//                
//            }
//        
//    }
    public String getInput(){
        
        Scanner scan = new Scanner(System.in);
        boolean validData = false;
        String readIn = "";

        do{
            
            try{
                readIn = scan.nextLine();//tries to get data. Goes to catch if invalid data
                validData = true;//if gets data successfully, sets boolean to true
            }catch(InputMismatchException e){
                //executes when this exception occurs
                System.out.println("Input has to be a number. ");
           }
        }while(validData==false);//loops until validData is true
        
        return readIn;
    }
}
