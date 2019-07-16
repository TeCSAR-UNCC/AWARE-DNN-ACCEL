//*********************************************************************************************************************************************************************************************
//This is a APE of the CNN accelerator.		
//Authors: Adarsh Sawant & Justin Sanchez
//Current Version 1.4
//Version:1.0
// I/O
//ip: input to the module(channel data )
// valid: input valid interface for each of the channel data
//op: agregated output for each kernel over the channels
// valid_out : valid signal for the next module
//Parameters: 
//Channels : number of the channesl input to the APE layer
//KUnits : Number of the kernels the APE is operating
//Rowsize:Size of the window.  
//Module Instatiated: None
// This module would  aggregate the output of the channel when each of valid signal is high.when Valid is low it is going to  reset the registers for each  of the kernel.
// we are using seperate registers for storing the kernel values as we are using the kernel parallelisim.
// Know deffects:
// We are currently using a single valid signal to output the data ,assuming that the data would come at same time from all the channels(2D line buffer does garuntee this) but we might run
// into implementation problems in latter stages.
//Version1.2
//This version of the code used in conjuction with the Tensor buffer.
//This module instatiates the APUS , and we will have Kunits of such APUs as we are using kernel parallelisim
// Now we are using the indivisual valid signal from each channel for each of the Kernel units
// We have used the bulk connections for making the connections easy.

//Version:1.3:Adarsh Sawant 
//the only changes where to the APU, and the input signals through out all modules. Each APU only recieves 1 input, not channel # of inputs 


//Ver1.4: Adarsh Sawant
//This changes the way how we recieve the Channel pointers . This chaneg is the by product of the APU change which we made.
//again the valid input from the 1.3 version is changed to Kernchpointer to indicate  the correct meaning  of the variable
//New signal valid_out is being added in oder to implement the chnage in the tensor buffer
//This signal would  indicate when the data and the channel/Kernchpointer is valid.
package thecode
import chisel3.iotesters.{PeekPokeTester, Driver}
import chisel3._
import chisel3.iotesters
import chisel3.iotesters.{ChiselFlatSpec, Driver, PeekPokeTester}
import Chisel.iotesters.{SteppedHWIOTester, ChiselFlatSpec}
import scala.language.reflectiveCalls
class APE(val Channels: Int,val KUints:Int,val Rowsize:Int) extends Module
{
    val io = IO(new Bundle {   
    val ip = Input(Vec(KUints,UInt(8.W))) // each input from the chanels
    //val valid=Input(Vec(KUints,UInt(10.W))) 
    val Kernchpointer=Input(Vec(KUints,UInt(10.W))) 

    val Indatavalid=Input(Vec(KUints,UInt(1.W))) 
    val op = Output(Vec(KUints,UInt(8.W)))      // 'kernel' num of o/p
    val valid_out=Output(Vec(KUints,UInt(1.W)))
    })
 
    val APUs =  Array.fill(KUints){ Module(new APU(Channels,Rowsize)).io}    
    for (i<-0 until KUints)
    {
        	APUs(i).ip <> io.ip(i)
            APUs(i).datavalid <>io.Indatavalid(i)
        	APUs(i).chpointer <> io.Kernchpointer(i)
        	io.op(i) <> APUs(i).op
        	io.valid_out(i) <> APUs(i).valid_out

        
     }
   }
