/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.rrntu.motionmaster;

/**
 *
 * @author kasunprabash.ka
 */
public class LPF {
    
    private double prevValue;
    private double prevOutput;
    private double gain;
    private double swfreq;
    private double stepSize;
    
    public LPF(double frequency, double cutoff){
        
        swfreq = frequency;
        stepSize =  1.0/(2.0*swfreq);
    }
    public double getLowPassFilteredOutput(double value){
        
        double output =0.0;
        
        
        return output;
        
    }
    
}
