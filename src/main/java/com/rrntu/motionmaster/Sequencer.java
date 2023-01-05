/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.rrntu.motionmaster;

import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.InputMismatchException;
import java.util.Scanner;


/**
 *
 * @author kasunprabash.ka
 */
public class Sequencer extends Thread {
    
    ElmoDriveModel driveM, driveS;
    ComManager comMgr;
    MotionMaster ms;
    String comM, comS;
    ArrayList<String> comPortList = new ArrayList<String>();
    private HashMap portMap = new HashMap();
    Enumeration allPorts = null;
    
    //Timer timer;
        
    ArrayList<String> masterInitList = new ArrayList<>();
    ArrayList<String> slaveInitList = new ArrayList<>();
    ArrayList<String> masterPollList= new ArrayList<>();
    ArrayList<String> slavePollList = new ArrayList<>();
    
    //state machine variables
    int userState;
    int motionState;
    double absPosition;
    double relPosition;
    double setAbsPosition;
    double setRelPosition;
    int interCmdDelay;
    boolean isSync;
    boolean isRunninng;
    boolean isEnabled;
    boolean isSlaveEnabled;
    boolean isTracking;
    boolean connect;
    
    
    boolean isInitialized = false;
    boolean isStatusReceived = false;
    
    int masterInitCmd = 0;
    int slaveInitCmd = 0;
    int masterPollCmd = 0;
    int slavePollCmd = 0;
    
    //Set of states for user command line interaction
    public final static int GET_MASTER_PORT = 1;
    public final static int GET_SLAVE_PORT = 2;
    public final static int LIST_COM_PORTS  = 0;
    public final static int WAIT_USER_CMD = 3;
    
    //Set of motion states
    public final static int DRIVES_NOT_CONNECT = 0;
    public final static int DRIVES_CONNECTED = 1;
    public final static int INITIALIZED  = 2;
    public final static int IN_MOTION = 3;
    public final static int ERROR = 9;
    
    //Motion commands supported
    public final static int MOVA = 5;
    public final static int MOVR = 6;
    public final static int STOP = 7;
    public final static int ENA = 8;
    public final static int DIS = 9;
    public final static int VER = 10;
    
    //Thread management
    public volatile boolean isDriveAlive = false;
    
  
    @Override
    public void run() {

        while(true){
            
            if(isDriveAlive)
                completeTask();
        }

    }

    private void completeTask() {

       //
       
       System.out.println("running the Motion System");
       //try { this.sleep(2000); } catch(InterruptedException ie) {}
//       if(isSlaveEnabled){
//           
//           if(masterInitCmd < masterInitList.size())
//                this.driveM.getComManager().writeData(masterInitList.get(masterInitCmd++).getBytes());
//           if(slaveInitCmd < slaveInitList.size())
//                this.driveM.getComManager().writeData(slaveInitList.get(slaveInitCmd++).getBytes());
//           
//           
//       }
//        String strCmd = "MO\r";
//        Charset charset = Charset.forName("ASCII");
//
//        byte[] data1 = strCmd.getBytes(charset);
//        this.comMgr.writeData(data1);


    }
    public void on(ComManager comMgr){
        
        masterInitList.add("SR\r");
        masterInitList.add("VE\r");
        masterInitList.add("MO\r");
        masterInitList.add("UM\r");
        
        slaveInitList.add("SR\r");
        slaveInitList.add("VE\r");
        slaveInitList.add("MO\r");
        slaveInitList.add("UM\r");
        
        masterPollList.add("SR\r");
        masterPollList.add("ID\r");
        masterPollList.add("IQ\r");
        masterPollList.add("MO\r"); 
        
        slavePollList.add("SR\r");
        slavePollList.add("ID\r");
        slavePollList.add("IQ\r");
        
        userState = LIST_COM_PORTS;
        
        this.comMgr = comMgr;
        
        while(true){
            
            userStateMachine();
            
            if(userState == WAIT_USER_CMD && connect ){
             motionStateMachine();   
            }
            
        }
    }
    
    //will let the user to command the motion , intelocks and 
    //other safety features
    public void userStateMachine(){
        
        switch(userState){
            
            case LIST_COM_PORTS: portMap = searchPorts();
                                 validatePorts();
                                 userState = GET_MASTER_PORT;
                                 break;
            case GET_MASTER_PORT: selectMasterPort();
                                 userState = GET_SLAVE_PORT;
                                 break;
            case GET_SLAVE_PORT: selectSlavePort();
                                userState = WAIT_USER_CMD;
                                
                                break;
            case WAIT_USER_CMD: //System.out.println("Waiting for Actuator control commands..");
                                System.out.print(">");
                                isDriveAlive = true;
                                processUserCommands(waitForUserInput());
                                userState = WAIT_USER_CMD;
                                break; //Need to do this on a separate thread with motion StateMachine
            default: break;
        }
    }
     
    private void processUserCommands(String readIn){
        
    //If the state is for waiting user command, we will separate the command and args
        if(userState == WAIT_USER_CMD){
            
            if(readIn.contains("MOVA")){
                
                if(readIn.length() > 6 && readIn.indexOf("=") >= 4){
                    
                    String strVal = readIn.substring(readIn.indexOf("=") + 1, readIn.length());
                    System.out.println("Attempt to move Absolute " + strVal);
                    
                    try{
                        setAbsPosition = Double.parseDouble(strVal);
                        //Need to test this is between a range. WTD
                    }catch(Exception e){
                        
                    }
                }
            }else if(readIn.contains("MOVR")){
                if(readIn.length() > 6 && readIn.indexOf("=") >= 4){
                    
                    String strVal = readIn.substring(readIn.indexOf("=") + 1, readIn.length());
                    System.out.println("Attempt to move Relative " + strVal);
                    
                    try{
                        setRelPosition = Double.parseDouble(strVal);
                        //Need to test this is between a range. WTD
                    }catch(Exception e){
                        
                    }
                }
            }else if(readIn.contains("STOP")){
                System.out.println("OK, Drives are stopping");
                isRunninng = false;
            }else if(readIn.contains("ENA")){
                System.out.println("OK, Drives are enabled");
                isEnabled = true;
            }else if(readIn.contains("DIS")){
                System.out.println("OK, Drives are disabled");
                isEnabled = false;
            }else if(readIn.contains("CON")){
                System.out.println("OK, Drives are connecting via RS232");
                connect = true;
            }else if(readIn.contains("DELAY")){
                
                if(readIn.length() > 6 && readIn.indexOf("=") >= 5){
                    
                    String strVal = readIn.substring(readIn.indexOf("=") + 1, readIn.length());
                    int tmp = interCmdDelay;
                    
                    try{
                        tmp = Integer.parseInt(strVal);
                        if(tmp>=10 && tmp <=10000){
                            System.out.println("OK, We are sending commands with a gap of " + strVal + "ms");
                            interCmdDelay = tmp;
                        }else{
                            System.out.println("Sorry the delay value of " + strVal + "ms is not in range.");   
                            
                        }
                        //Need to test this is between a range. WTD
                    }catch(Exception e){
                         System.out.println("Sorry the delay value of " + strVal + "ms is not in range."); 
                         
                    }
                }else{
                    System.out.println("Check the command syntax.."); 
                }
            }else if(readIn.contains("NOSYNC")){
                System.out.println("OK, Disabling the Slave..");
                isSlaveEnabled = false;
            }else if(readIn.contains("SYNC")){
                System.out.println("OK, Enabling the Slave..");
                isSlaveEnabled = true;
            }else if(readIn.contains("NOTRACK")){
                isTracking = false;
                System.out.println("OK");
            }else if(readIn.contains("TRACK")){
                isTracking = true;
                System.out.println("OK");
            }else if(readIn.contains("HELP")){
                System.out.println("Use only the below commands \r\nMOVA=XXX\r\nMOVR=XXX\r\nSTOP\r\nENA\r\nDIS\r\nDELAY=XXX\r\nNOSYNC\r\nSYNC\r\nSEND=COMMAND\r\nHELP");
            }else{
                return;
            }
        }
        
        
    }
    //This will keep up the communication with the two drives simultaneusly.
    public void motionStateMachine(){
        
        switch(motionState){
            
            case DRIVES_NOT_CONNECT://if(connectToDrives())
                                        connectToDrives();
                                        motionState = DRIVES_CONNECTED;
                                    System.out.println("drives not connected state exited");
                                    break;
            case DRIVES_CONNECTED://initializeDrives();
                System.out.println("drives connected state");
                                  //readDriveOutput();
                                  break;
            case INITIALIZED:break;
            case IN_MOTION:break;
            case ERROR:break;
            default:break;
            
        }
        
    }
    
    
    //we will wait till we get user command.
    //This is better be run with a timer task with timeout as well.
    //will not implemet advanced functions in the first phase
    private String waitForUserInput(){
        
        Scanner scan = new Scanner(System.in);
        boolean validData = false;
        String readIn = "";

        do{
            
            try{
                readIn = scan.nextLine();//tries to get data. Goes to catch if invalid data
                validData = true;//if gets data successfully, sets boolean to true
            }catch(InputMismatchException e){
                //executes when this exception occurs
                System.out.println("Use only the below commands \r\nMOVA=XXX\r\nMOVR=XXX\r\nSTOP\r\nENA\r\nDIS\r\nDELAY=XXX\r\nNOSYNC\r\nSYNC\r\nSEND=COMMAND\r\nHELP");
           }
        }while(validData==false);//loops until validData is true
        
        
        return readIn;
    }
    
    private HashMap searchPorts(){
        
        System.out.println("Following COM ports are found..");
        portMap = comMgr.searchPorts();
        return portMap;
    }
    
    private void selectMasterPort(){
        System.out.println("Select COM port connected to the Master Drive..");
        comM = waitForUserInput();
    }
    
    private void selectSlavePort(){
        System.out.println("Select COM port connected to the Slave Drive..");
        comS = waitForUserInput();
    }
    
    private void validatePorts(){
        
        //portMap.entrySet().
    }
    
    private void readDriveOutput(){
        
        driveM.getComManager().manRead();
        driveS.getComManager().manRead();
        
    }
    
    private boolean connectToDrives(){
        
        driveM = new ElmoDriveModel(this,comM,portMap, true);
        driveS = new ElmoDriveModel(this,comS,portMap, false);
        
        if(this.isSlaveEnabled){
            
            if(driveM!= null && driveS!= null){
                
                if(!driveM.getComManager().getConnected()){
                    driveM.getComManager().connect(comM);
                }else{
                    return false;
                }
                
                if(!driveS.getComManager().getConnected()){
                    driveS.getComManager().connect(comS);
                }else{
                    return false;
                }
            }else{
                return false;
            }
            
        }else{
                
             if(driveM!= null){
                 
                 if(!driveM.getComManager().getConnected()){
                    driveM.getComManager().connect(comM);
                    return true;
                 }else{
                     return false;
                 }
             }else{
                 return false;
             }   
        }
        return true;
    }
    
    private boolean initializeDrives(){
        
//        if(!isInitialized && timer==null){
//            //timer = new Timer(true);
//           // timer.scheduleAtFixedRate(this, 10,50 );
//            
//        }else if(!isInitialized && timer!=null){
//            //timer.scheduleAtFixedRate(this, 10,50 );
//            
//        }else{
//            return false;
//        }
        
        return true;

    }
    
    
}
