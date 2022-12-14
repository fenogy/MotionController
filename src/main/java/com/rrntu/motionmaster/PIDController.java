/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.rrntu.motionmaster;

/**
 *
 * @author kasunprabash.ka
 */
public class PIDController {
    
    //Gains
    private double Kp, Ki, Kd;
    
    //Variables
    private double prevMeasurement, prevError, integrator,differentiator,output;
    
    //Timing
    private double T,tau;
    
    //Limits
    private double limMin, limMax;
    
    public void PIDController(){
        
        prevMeasurement = 0.0;
        prevError = 0.0;
        integrator = 0.0;
        differentiator = 0.0;
        output = 0.0;

        
        
    }
    public double UpdateDT(double setpoint, double measurement){
        
        
        //Error
        double error = setpoint - measurement;
        
        //Proportional 
        double proportional = this.Kp*error;
        
        //Integral
        integrator = integrator + 0.5*Ki*T*(error + prevError);
        
        
        //Compute integrator limits in anti windup
        
        
        //Differential
        differentiator = (2.0*Kd*(measurement - prevMeasurement) + (2.0*tau - T)*differentiator )/((2.0*tau + T)*differentiator);
        return output;
    }
    
    public double UpdateCT(double setpoint, double measurement){
        
        
        //Error
        double error = setpoint - measurement;
        
        //Proportional 
        double proportional = this.Kp*error;
        
        //Integral
        integrator = integrator + 0.5*Ki*T*(error + prevError);
        
        
        //Compute integrator limits in anti windup
        
        
        //Differential
        differentiator = (2.0*Kd*(measurement - prevMeasurement) + (2.0*tau - T)*differentiator )/((2.0*tau + T)*differentiator);
        return output;
    }
    
}
