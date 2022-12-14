/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.rrntu.motionmaster;

/**
 *
 * @author kasunprabash.ka
 */
public class CommandDecoder {
    
    String curCommand = "";
    String status = "";
    
    private DriveStatus drive1Status = new DriveStatus();
    private DriveStatus drive2Status = new DriveStatus(); 
    
    private ElmoDriveModel drive;
    
    public CommandDecoder(ElmoDriveModel drive){
        this.drive = drive;
    }
    public String cleanResponse(String resp){
        
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
                resp = resp.substring(start, commaIndex+1);
                return resp;
                
            }
            
            if(equalIndex > 0 && bracketIndex > 0){
                
                int start = bracketIndex - 2;
                resp = resp.substring(start, commaIndex+1);
                return resp;
                
            }
            
            if(bracketIndex > 0){
                
                int start = bracketIndex - 2;
                resp = resp.substring(start, commaIndex+1);
                return resp;
                
            }
            
            
            
        }else{
            
            if(enterIndex >= 2){
                
                int start = enterIndex - 2;
                resp = resp.substring(start, commaIndex+1);
                return resp;
            }
        }
        return resp;
    }
    void decodeResp(String resp){
        
        if(resp.indexOf("?")>=0){
            System.out.println("Incorrect mode..");
            return;
        }
        resp = cleanResponse(resp);
        
        if(resp.length() >= 3){
            
            curCommand = resp.substring(0, 2);
            switch (curCommand){
            
                case "SR" :decodeSR(resp.substring(3, resp.indexOf(";")));break;
                case "MO" :decodeMO(resp.substring(3, resp.indexOf(";")));break;
                case "ID" :decodeID(resp.substring(3, resp.indexOf(";")));break;
                case "IQ" :decodeIQ(resp.substring(3, resp.indexOf(";")));break;
                case "SP" :decodeSP(resp.substring(3, resp.indexOf(";")));break;
                case "ST" :decodeST(resp.substring(3, resp.indexOf(";")));break;
                case "UM" :decodeUM(resp.substring(3, resp.indexOf(";")));break;
                case "PR" :if(resp.indexOf("=") >= 0){
                            decodePR(resp.substring(3, resp.indexOf("\r")));break;
                           }else{
                            decodePR(resp.substring(3, resp.indexOf(";")));break;
                             }
                    
                case "PX" :if(resp.indexOf("=") >= 0){
                            decodePX(resp.substring(3, resp.indexOf("\r")));break;
                           }else{
                            decodePX(resp.substring(3, resp.indexOf(";")));break;
                             }
                case "PY" :decodePY(resp.substring(3, resp.indexOf(";")));break;
                case "RM" :decodeRM(resp.substring(3, resp.indexOf(";")));break;
                case "SN" :decodeSN(resp.substring(3, resp.indexOf(";")));break;
                case "TC" :decodeTC(resp.substring(3, resp.indexOf(";")));break;
                case "VR" :decodeVR(resp.substring(3, resp.indexOf(";")));break;
                case "VE" :decodeVR(resp.substring(3, resp.indexOf(";")));break;
                case "BG" :decodeBG(resp.substring(3, resp.indexOf(";")));break;
                default: 
                 //System.out.println("Command " + resp + " is unknown"); 
                    break;
            }
        }else{
            System.out.println("Command " + resp + " is unknown");
        }
    }
    
    void decodeSR(String s){
       
        status = s;
        //System.out.println(s);
        //System.out.println("SR received " + status );
        try{
            Integer hexStatus = Integer.valueOf(status);
            //System.out.println("SR received " + hexStatus );
            if((hexStatus & 0x0001) == DriveStatus.DRIVE_STATUS){
                
            }
            
            
            
            
            
            
        }catch(NumberFormatException e){
           System.out.println("Status value is not decodable..");
        }
        
        
    }
    
    void decodeMO(String s){
        
        status = s;
        //System.out.println(s);
        //System.out.println("SR received " + status );
        try{


            
            
            
            
            
            
        }catch(NumberFormatException e){
           System.out.println("mode value is not decodable..");
        }
    }
    
    void decodeSP(String s){
        
        
    }
    
     void decodeBG(String s){
        
        
    }
    
    void decodeID(String s){
        //System.out.println("ID received..");
    }
    
    void decodeIQ(String s){
        
        try{
            
            Double val = Double.valueOf(s);
            drive.setCurrentReal(val);
            
            if(val < 12.0 && val > -12.0)
                drive.updateCurrentWindow(val);
            
            
            //drive.enqueCurrentBuffer(val, drive.getSyncDelay());
            //drive.updateMovingAverage(val);
            try{
             //System.out.println("Added " + String.valueOf(val*10000/10000) + " at " + String.valueOf(drive.getIndex()) + " average <" + String.valueOf(drive.getMeanCurrent()*100000/100000) +">");
   
            }catch(Exception e){
                
            }
                        
        }catch(Exception e){
            
        }
        
    }
    
    void decodeUM(String s){
        
        
    }
    
    void decodeST(String s){
        
    }
    
    void decodePR(String s){

        try{


            int position = Integer.valueOf(s);
            drive.setTargetPosition(position);
            //System.out.println(s);
        }catch(Exception e){
            System.out.println("PR decode error.. Try again before continuing..");
        }
    }
    
    void decodePX(String s){
        
        try{
            int position = Integer.valueOf(s);
            drive.setPosition(position); //Usualy gets from slave to speed up
            //System.out.println("T:"+String.valueOf(drive.getTargetPosition())+" , "+"C:"+String.valueOf(drive.getPosition()));
        }catch(Exception e){
            System.out.println("PX decode error.. Use previous value..");
        }
         
        
    }
    
    void decodePY(String s){
        
    }
    
    //send this at the begining to ignore respond to noise
    void decodeRM(String s){
        
    }
    
    void decodeSN(String s){
        System.out.println("Drive Serial number is "+ s);
    }
    
    void decodeTC(String s){
        
        try{
            
            Double val = Double.valueOf(s);
            //drive.setCurrent(val);
            
        }catch(Exception e){
            
        }
    }
    
    void decodeVR(String s){
        
    }
}
