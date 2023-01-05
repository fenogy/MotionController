/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.rrntu.motionmaster;


import java.time.Clock;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.InputMismatchException;
import java.util.Scanner;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 *
 * @author kasunprabash.ka
 */
public class StateMachine extends Thread {
    
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
    ArrayList<String> userCommandListMaster = new ArrayList<>();
    ArrayList<String> userCommandListSlave = new ArrayList<>();
    String userCommandMaster = "";
    String userCommandSlave = "";
    
    //state machine variables
    volatile int userState;
    volatile int motionState;
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
    boolean masterPoll = true;
    boolean isPolling = false;
    boolean isForwardMotion = true;
    
    
    volatile boolean isInitialized = false;
    volatile boolean isStatusReceived = false;
    
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
    Clock clock = Clock.systemUTC();  
    long previousTime = 0;
    //Thread management
    public volatile boolean isDriveAlive = false;
    @Override
    public void run() {
        
        while(true){
   
            //System.out.println("In While"); 
            //Run MotionState machine
            if(userState == WAIT_USER_CMD && connect ){
                motionStateMachine();
                //if(!(motionState == INITIALIZED || motionState == IN_MOTION))
                    readDriveOutput();
            }
            
            //Command Sequencing
            if(isDriveAlive){
            
                //System.out.println("In Drive Alive"); 
                if(motionState == DRIVES_CONNECTED ){
                    
                    
                        long timeElapsed = 0;                        
                        timeElapsed = System.nanoTime() - previousTime;
                        //System.out.println("In Drivinits"); 
                        //Relate this with DELAY COMMAND
                        if(timeElapsed >100000000){
                           
                            initializeDrives();
                            previousTime = System.nanoTime();
                            driveM.getComManager().setIsRespAvailable(false);
                        }

                }
                if(motionState == INITIALIZED || motionState == IN_MOTION){
                    
                    long timeElapsed = 0;                        
                        timeElapsed = System.nanoTime() - previousTime;
                        //System.out.println("TimeElapsed:" + String.valueOf(timeElapsed) + " Prev Time:" +  String.valueOf(previousTime) + " Time:" + String.valueOf(System.nanoTime()) );
                        
                        //Will Sync Slave and Master positions 
                        //driveM.setPosition(driveS.getPosition());
                        //driveS.setTargetPosition(driveM.getTargetPosition());
                        
                        //Relate this with DELAY COMMAND
                        //if(timeElapsed > 7500000){
                            //if(timeElapsed > 2000000){ //2ms
                                if(timeElapsed > 2000000){
                           // if(timeElapsed > 500000){   //fastest//too fast
                        //if(timeElapsed > 500000){   
                            if(userCommandMaster.length() > 2){
                                
                                sendCommand(userCommandMaster,true);
                                userCommandMaster = "";
                                
                            }else if(userCommandSlave.length() > 2 && isSlaveEnabled){
                                
                                sendCommand(userCommandSlave,false);
                                userCommandSlave = "";
                                
                            }else{
                                
                              if(isPolling){
                                pollDrives(); 
                                //System.out.println("Position: " + String.valueOf(driveM.getPosition()) + " Target: " +  String.valueOf(driveM.getTargetPosition()) + " Time:" + String.valueOf(System.nanoTime()) );
                                //System.out.println("Inside: TimeElapsed:" + String.valueOf(timeElapsed) + " Prev Time:" +  String.valueOf(previousTime) + " Time:" + String.valueOf(System.nanoTime()) );
                                
                              }
                              
                            }
                            
                            //driveM.getComManager().setIsRespAvailable(false);
                            previousTime = System.nanoTime();
                        }
                    
                }
            }
                
        }
        
    }
    
    

    
     public void on(ComManager comMgr){
        
        masterInitList.add("SR\r");
        //masterInitList.add("MO=0\r");
        masterInitList.add("LD\r"); //Load from Flash or clear everything and set the params
        masterInitList.add("UM=5\r");
        masterInitList.add("SR\r");
        masterInitList.add("LD\r"); 
        masterInitList.add("VE\r");
        //masterInitList.add("MO=0\r");
        masterInitList.add("UM\r");
        //masterInitList.add("PX=0\r");
        masterInitList.add("AC=3000\r");
        masterInitList.add("DC=3000\r");
        masterInitList.add("SP\r");
        masterInitList.add("SP=20000\r");
        masterInitList.add("SP\r");
        masterInitList.add("MO=1\r");
        masterInitList.add("MO=1\r");
        masterInitList.add("MO=1\r");

        //masterInitList.add("PR=-80000\r");
        //masterInitList.add("BG\r");

        
        
        slaveInitList.add("SR\r");
        slaveInitList.add("SR\r");
        slaveInitList.add("LD\r"); //Load from Flash or clear everything and set the params
        slaveInitList.add("SR\r");
        slaveInitList.add("SR\r");
        //slaveInitList.add("MO=0\r");
        //slaveInitList.add("MO=0\r");
        //slaveInitList.add("PX=0\r");
        slaveInitList.add("AG[1]=0\r"); //Analog gain to reduce torque on noise
        slaveInitList.add("UM=1\r");
        slaveInitList.add("UM=1\r");
        slaveInitList.add("SR\r");
        slaveInitList.add("VE\r");
        slaveInitList.add("MO=1\r");
        slaveInitList.add("UM\r");
        slaveInitList.add("MO=1\r");
        slaveInitList.add("MO=1\r");

//        //Slave on position mode
//        slaveInitList.add("SR\r");
//        slaveInitList.add("MO=0\r");
//        slaveInitList.add("LD\r"); //Load from Flash or clear everything and set the params
//        slaveInitList.add("UM=5\r");
//        slaveInitList.add("SR\r");
//        slaveInitList.add("LD\r"); 
//        slaveInitList.add("VE\r");
//        slaveInitList.add("MO=0\r");
//        slaveInitList.add("UM\r");
//        slaveInitList.add("PX=0\r");
//        slaveInitList.add("AC=2000\r");
//        slaveInitList.add("DC=2000\r");
//        slaveInitList.add("SP\r");
//        slaveInitList.add("SP=7000\r");
//        slaveInitList.add("SP\r");
//        slaveInitList.add("MO=1\r");
//        slaveInitList.add("MO=1\r");
//        slaveInitList.add("MO=1\r");


        
        for(int i = 0; i < 50; i++){
            
            if(i != 49 && i != 48){
              masterPollList.add("IQ\r");
              masterPollList.add("PX\r");
              //masterPollList.add("PX\r");
              //slavePollList.add("PX\r"); 
            }else if(i == 48) {
              //masterPollList.add("PX\r");
              slavePollList.add("PX\r"); 
            }else if(i == 49){
              //masterPollList.add("SR\r");
              slavePollList.add("SR\r"); 
            }
        }
       
        
        userState = LIST_COM_PORTS;
        
        this.comMgr = comMgr;
        
        while(true){
            
            userStateMachine();
            
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
                                processUserCommands(waitForUserInput());
                                isDriveAlive = true;
//                                try{
//                                    Thread.sleep(50);
//                                    processUserCommands(waitForUserInput());
//                                }catch(Exception e){
//                                    
//                                }
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
                        if(tmp>=1 && tmp <=10000){
                            System.out.println("OK, We are sending commands with a gap of " + strVal + "ms");
                            interCmdDelay = tmp;                            
                            driveM.setSyncDelay(interCmdDelay);
                            if(isSlaveEnabled)
                                driveS.setSyncDelay(interCmdDelay);
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
                //isPolling = false;
            }else if(readIn.contains("SYNC")){
                System.out.println("OK, Enabling the Slave..");
                isSlaveEnabled = true;
                //isPolling = true;
            }else if(readIn.contains("NOTRACK")){
                isTracking = false;
                System.out.println("OK");
            }else if(readIn.contains("NOPOLL")){
                System.out.println("OK, Disabling Polling Drives..");
                isPolling = false;
            }else if(readIn.contains("POLL")){
                System.out.println("OK, Enabling Polling Drives..");
                isPolling = true;
            }else if(readIn.contains("TRACK")){
                isTracking = true;
                System.out.println("OK");
            }else if(readIn.contains("NOAVG")){
                this.driveM.setIsAveraging(false);
                System.out.println("Current will be averaged..");
            }else if(readIn.contains("AVG")){
                this.driveM.setIsAveraging(true);
                System.out.println("Current will be averaged..");
            }else if(readIn.contains("HELP")){
                System.out.println("Use only the below commands \r\nMOVA=XXX\r\nMOVR=XXX\r\nSTOP\r\nENA\r\nDIS\r\nDELAY=XXX\r\nNOSYNC\r\nSYNC\r\nSEND=COMMAND\r\nHELP");
            }else if(readIn.contains("M,")){
                System.out.println("Sending the command to Master.. " + readIn.substring(readIn.indexOf(',')+1,readIn.length()) );
                //userCommandListMaster.add(readIn.substring(readIn.indexOf(',')+1,readIn.length())+ "\r");
                userCommandMaster = readIn.substring(readIn.indexOf(',')+1,readIn.length())+ "\r";
                if(userCommandMaster.contains("BG")){
                    isPolling = true;
                }
                if(userCommandMaster.contains("PR") && userCommandMaster.contains("-") ){
                    driveM.setIsForwardMotion(false);
                    System.out.println("Commanded to Retract..Start with BG");
                }else if(userCommandMaster.contains("PR")){
                    driveM.setIsForwardMotion(true);
                    System.out.println("Commanded to Extend..Start with BG");
                }
            }else if(readIn.contains("S,")){
                
                System.out.println("Sending the command to Slave.. " + readIn.substring(readIn.indexOf(',')+1,readIn.length()) );
                //userCommandListSlave.add(readIn.substring(readIn.indexOf(',')+1,readIn.length())+ "\r");
                userCommandSlave = readIn.substring(readIn.indexOf(',')+1,readIn.length())+ "\r";
                //isTracking = true;
            }else if(readIn.contains("D,")){
                
                System.out.println("Sending the command to both drives.. " + readIn.substring(readIn.indexOf(',')+1,readIn.length()) );
                //userCommandListSlave.add(readIn.substring(readIn.indexOf(',')+1,readIn.length())+ "\r");
                userCommandMaster = readIn.substring(readIn.indexOf(',')+1,readIn.length())+ "\r";
                userCommandSlave = readIn.substring(readIn.indexOf(',')+1,readIn.length())+ "\r";
                //isTracking = true;
                if(userCommandMaster.contains("BG")){
                    isPolling = true;
                }
                if(userCommandMaster.contains("PR") && userCommandMaster.contains("-") ){
                    driveM.setIsForwardMotion(false);
                    System.out.println("Commanded to Retract..Start with BG");
                }else if(userCommandMaster.contains("PR")){
                    driveM.setIsForwardMotion(true);
                    System.out.println("Commanded to Extend..Start with BG");
                }
            }else{
                
                if(isPolling){
                    isPolling = false;
                    System.out.println("OK, Disabling Polling Drives..");
                }else{
                  isPolling = true;  
                  System.out.println("OK, Enabling Polling Drives..");
                }
                
                return;
            }
        }
        
        
    }
    //This will keep up the communication with the two drives simultaneusly.
    public void motionStateMachine(){
        
        switch(motionState){
            
            case DRIVES_NOT_CONNECT:if(connectToDrives())
                                    //connectToDrives();
                                    motionState = DRIVES_CONNECTED;
                                    System.out.println("drives not connected state exited");
                                    System.out.println("M:Sending Init Commands");

                                    break;
            case DRIVES_CONNECTED:if(isInitialized)
                                   motionState = INITIALIZED; 
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
        
        //System.out.println("read");
        driveM.getComManager().manRead();
        if(isSlaveEnabled)
        driveS.getComManager().manRead();
        
        //driveM.getComManager().read();
        //driveS.getComManager().read();
    }
    
    private boolean connectToDrives(){
        
        driveM = new ElmoDriveModel(this,comM,portMap, true);
        driveS = new ElmoDriveModel(this,comS,portMap, false);
        
        if(this.isSlaveEnabled){
            
            if(driveM!= null && driveS!= null){
                
                if(!driveM.getComManager().getConnected()){
                    driveM.getComManager().connect(comM);
                    driveM.getComManager().initIOStream();
                    //driveM.getComManager().initListener();
                }else{
                    return false;
                }
                
                if(!driveS.getComManager().getConnected()){
                    driveS.getComManager().connect(comS);
                    driveS.getComManager().initIOStream();
                    //driveS.getComManager().initListener();
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
                    driveM.getComManager().initIOStream();
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
    public boolean initializeDrives(){
        
        driveM.setSamples(50);
        //driveS.setSamples(50);
        if(isSlaveEnabled){
            if(slaveInitCmd < slaveInitList.size()){
                byte[] b = slaveInitList.get(slaveInitCmd++).getBytes();
                driveS.getComManager().writeData(b);
            //System.out.println("S:Sending Init Commands");
            }
        }
        if(masterInitCmd < masterInitList.size()){
            byte[] b = masterInitList.get(masterInitCmd++).getBytes(); 
            driveM.getComManager().writeData(b);
            //System.out.println("M:Sending Init Commands");
        }
        //If all the commands has been sent we sent the iniliazation to completed.//modfy                        
        if(slaveInitCmd >= slaveInitList.size() || masterInitCmd >= masterInitList.size()){
            isInitialized = true;
            return true;
        }else{
            return false;
        }                              

    }  
    
//    public void pollDrives(){
//        
//        //DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss.SSS");
//        //LocalDateTime now = LocalDateTime.now();
//        if(masterPoll){
//                     
//            if(masterInitCmd < masterPollList.size()){
//            byte[] b = masterPollList.get(masterInitCmd++).getBytes(); 
//            driveM.getComManager().writeData(b);
//                            if(masterInitCmd >= 49)
//                    masterInitCmd = 0;
//            //System.out.println(dtf.format(now));
//            //System.out.println("PollMaster");
//            }
//        
//        }else{
//            
//        if(isSlaveEnabled){
//            
//            byte[] b,c;
//            
//            if(slaveInitCmd < slavePollList.size()){
//                
//                b = slavePollList.get(slaveInitCmd++).getBytes();
//            }
//                c = driveM.getTorqueCommand().getBytes();
//                //driveS.getComManager().writeData(c);
//                //to support both at position mode
//                //driveS.getComManager().writeData(b);
////                if(slaveInitCmd >= 49)
////                    slaveInitCmd = 0;
//                //end of position mode 
//                
//                //If the motor is moving send torque command and measure them sequentially on slave
//                if(slaveInitCmd%2 == 0){                    
//                     //driveS.getComManager().writeData(b);
//                    //driveS.getComManager().writeData(c);
//                    //System.out.println(dtf.format(now));
//                    //System.out.println("Set Slave");
//                }else{
//                    //For Torque Command Uncomment for Slave Torque mode
//                    driveS.getComManager().writeData(c);
//
//                }
//                
//                if(slaveInitCmd >= 49)
//                    slaveInitCmd = 0;
//                
//            
//        }
//        
//        
//           
//        }
//        
//        masterPoll = !masterPoll;
//
//    }
    public void pollDrives(){
        
//        if(masterInitCmd < masterPollList.size()){
//            byte[] b = masterPollList.get(masterInitCmd++).getBytes(); 
//            driveM.getComManager().writeData(b);
//            if(masterInitCmd >= 49)
//                masterInitCmd = 0;
//        }
//       if(masterInitCmd < masterPollList.size()){
//            byte[] b = masterPollList.get(masterInitCmd++).getBytes(); 
//            driveM.getComManager().writeData(b);
//                            if(masterInitCmd >= 49)
//                    masterInitCmd = 0;
//            //System.out.println(dtf.format(now));
//            //System.out.println("PollMaster");
//            }

       
        byte[] b = masterPollList.get(0).getBytes(); 
            driveM.getComManager().writeData(b);
           
        if(isSlaveEnabled){
            
            byte[] c;
            
//            if(slaveInitCmd < slavePollList.size()){
//                
//                b = slavePollList.get(slaveInitCmd++).getBytes();
//            }
                c = driveM.getTorqueCommand().getBytes();
                driveS.getComManager().writeData(c);

        }
        

    }
    
    public void sendCommand(String s, boolean isMaster){
        
        byte[] b = s.getBytes(); 
            
        if(isMaster){
            
            driveM.getComManager().writeData(b);
        }else{
            
            if(isSlaveEnabled){
                
                driveS.getComManager().writeData(b);
            }
        }
    }
    
 
//    private boolean initializeDrives(){
//        
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
//        
//        return true;
//
//    }
        
    
    
}
