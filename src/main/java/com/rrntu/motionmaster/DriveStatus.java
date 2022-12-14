/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.rrntu.motionmaster;

/**
 *
 * @author kasunprabash.ka
 */
public class DriveStatus {
    
    //Bit 1-3
    public static final int DRIVE_OK = 0;
    public static final int DRIVE_UNDER_VOLTAGE = 0x2;
    public static final int DRIVE_OVER_VOLTAGE = 0x4;
    public static final int DRIVE_SHORT_CIRCUIT = 0xA;
    public static final int DRIVE_OVER_TEMPERATURE = 0xC;

    //All other bits
    public static final int DRIVE_STATUS = 0x1; //0
    public static final int SERVO_STAUS = 0xE; //1-3
    public static final int MOTOR_ON = 0x10; //4
    public static final int REF_MODE = 0x20;  //5
    public static final int MF_LATCHED = 0x40; //6
    public static final int UNIT_MODE = 0x380; //7-9
    public static final int GAIN_SCHEDULING_ON = 0x400; //10
    public static final int HOMING_ON = 0x800; //11
    public static final int PROGRAM_RUNNING = 0x1000;  //12
    public static final int CURRENT_LIMIT_ON = 0x2000;  //13
    public static final int MS_REFLETION = 0xC000; //14-15
    public static final int RECORDER_STATUS = 0x30000; //16-17
    public static final int HALL_SENSORS = 0x7000000; //24-26
    public static final int CPU_STATUS = 0x8000000; //27
    public static final int STOPPED_BY_LIMIT= 0x10000000; //28
    public static final int USER_PROGRAM_ERROR = 0x20000000; //29
    
    private boolean driveOK = false;
    private boolean driveUV = false;
    private boolean driveOV = false;
    private boolean driveSC = false;
    private boolean driveOT = false;
    
    private int driveStatus = 0;
    private int servoStatus = 0;
    private boolean motorON = false;
    private boolean refMode = false;
    private boolean mfLatched = false;
    private int unitMode = 0;
    private boolean gainON = false;
    private boolean homingON = false;
    private boolean programRunning = false;
    private boolean currentLimitON = false;
    private boolean msReflection = false;
    private int recorderStatus = 0;
    private int hallSensors = 0;
    private boolean cpuStatus = false;
    private boolean stoppedByLimit = false;
    private boolean userProgramError = false;

    public boolean isDriveOK() {
        return driveOK;
    }

    public void setDriveOK(boolean driveOK) {
        this.driveOK = driveOK;
    }

    public boolean isDriveUV() {
        return driveUV;
    }

    public void setDriveUV(boolean driveUV) {
        this.driveUV = driveUV;
    }

    public boolean isDriveOV() {
        return driveOV;
    }

    public void setDriveOV(boolean driveOV) {
        this.driveOV = driveOV;
    }

    public boolean isDriveSC() {
        return driveSC;
    }

    public void setDriveSC(boolean driveSC) {
        this.driveSC = driveSC;
    }

    public boolean isDriveOT() {
        return driveOT;
    }

    public void setDriveOT(boolean driveOT) {
        this.driveOT = driveOT;
    }

    public int getDriveStatus() {
        return driveStatus;
    }

    public void setDriveStatus(int driveStatus) {
        this.driveStatus = driveStatus;
    }

    public int getServoStatus() {
        return servoStatus;
    }

    public void setServoStatus(int servoStatus) {
        this.servoStatus = servoStatus;
    }

    public boolean isMotorON() {
        return motorON;
    }

    public void setMotorON(boolean motorON) {
        this.motorON = motorON;
    }

    public boolean isRefMode() {
        return refMode;
    }

    public void setRefMode(boolean refMode) {
        this.refMode = refMode;
    }

    public boolean isMfLatched() {
        return mfLatched;
    }

    public void setMfLatched(boolean mfLatched) {
        this.mfLatched = mfLatched;
    }

    public int getUnitMode() {
        return unitMode;
    }

    public void setUnitMode(int unitMode) {
        this.unitMode = unitMode;
    }

    public boolean isGainON() {
        return gainON;
    }

    public void setGainON(boolean gainON) {
        this.gainON = gainON;
    }

    public boolean isHomingON() {
        return homingON;
    }

    public void setHomingON(boolean homingON) {
        this.homingON = homingON;
    }

    public boolean isProgramRunning() {
        return programRunning;
    }

    public void setProgramRunning(boolean programRunning) {
        this.programRunning = programRunning;
    }

    public boolean isCurrentLimitON() {
        return currentLimitON;
    }

    public void setCurrentLimitON(boolean currentLimitON) {
        this.currentLimitON = currentLimitON;
    }

    public boolean isMsReflection() {
        return msReflection;
    }

    public void setMsReflection(boolean msReflection) {
        this.msReflection = msReflection;
    }

    public int getRecorderStatus() {
        return recorderStatus;
    }

    public void setRecorderStatus(int recorderStatus) {
        this.recorderStatus = recorderStatus;
    }

    public int getHallSensors() {
        return hallSensors;
    }

    public void setHallSensors(int hallSensors) {
        this.hallSensors = hallSensors;
    }

    public boolean isCpuStatus() {
        return cpuStatus;
    }

    public void setCpuStatus(boolean cpuStatus) {
        this.cpuStatus = cpuStatus;
    }

    public boolean isStoppedByLimit() {
        return stoppedByLimit;
    }

    public void setStoppedByLimit(boolean stoppedByLimit) {
        this.stoppedByLimit = stoppedByLimit;
    }

    public boolean isUserProgramError() {
        return userProgramError;
    }

    public void setUserProgramError(boolean userProgramError) {
        this.userProgramError = userProgramError;
    }
    

    
}
