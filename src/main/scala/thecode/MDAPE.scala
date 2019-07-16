//*********************************************************************************************************************************************************************************************
//This is a Multidimensional APE for the Neural network accelerator.   
//Authors: Adarsh Sawant
//Editor: Justin S. 
//Current Version 1.3
//Version:1.0
// I/O
//ip: input to the module(channel data )this depeneds on the channel parallelism we are dealing with which is the artifact of  the keranal parallelism of last layer 
// valid: input valid interface for each of the channel data
//op: agregated output for each kernel over the channels
// valid_out : valid signal for the next module
//Parameters: 
//Channels : number of the channesl input to the APE layer
//KUnits : Number of the kernels the APE is operating
//Rowsize:Size of the window.  
//Module Instatiated: None
//agrtemp: is a vector which is equall to size of the kernel parallelism
//agrtemp: is accumulator for the kernel value over channels 
//Working :We first register the output from each APEs  (the number of which depends on the channel parallelism) into temp register 
// then we agregate the values  for each pixel over different kernels (line 55-68) 
//The version 1.1:Adash Sawant
//the only changes where to the APU, and the input signals through out all modules. Each APU only recieves 1 input, not channel # of inputs 
// vERSION:1.2 :Adarsh Sawant
// This version takes the channel pointers in terms of 1o bit uint rather than indivisual bits as in previous version. This is done to reduce the tesnor buffer decoding logic which would add to the resouce utiltization.
// Even thoug there might be wastage of resource in terms of use of unit for smaller value of unit but we feel that the synthesis tool will take of this during route phase and remove the unessarcy wires.
// We have also added a new signal for validating tensor buffer channel pointer . this signal is one signal per tensor buffer.
//Names of the siganls have been changed to accomodate their meanings.

//Version:1.3
//first small change is making the channelptr 10 Bits through all modules, this can latter be reduced.

//THE MDAPE EXPECTS THE INPUT DATA FROM THE TENSOR BUFFER TO BE ON THE DATA LINE FOR ATLEAST ONE CLOCK CYCLE . THERE IS NO SUCH DEPENDANCY BETWEEN THE BUFFER  VALID AND THE CHANNEL CHANNEL POINTERS.
//THE DATA IS OUTPUT FROM THE MDAPE AFTER THE LAST CHANNNEL DATA OF THE KERNEL IS PASSED. WHEN THE LAST CHANNEL DATA IS PUT ON THE DATA LINE FOR EACH PIXEL THE AGREGATED OUTPUT IS EXPECTED AFTER TWO CLOCK CYCLES.

package thecode
import chisel3.iotesters.{PeekPokeTester, Driver}
import chisel3._
import chisel3.iotesters                                                                                                                        
import chisel3.iotesters.{ChiselFlatSpec, Driver, PeekPokeTester}
import Chisel.iotesters.{SteppedHWIOTester, ChiselFlatSpec}
import scala.language.reflectiveCalls
class MDAPE(val ChanlPar:Int,val Channels: Int,val KUints:Int,val Rowsize:Int) extends Module
{
    val io = IO(new Bundle {   
    val ip = Input(Vec(ChanlPar,Vec(KUints,UInt(8.W)))) // each input from the channels
    val ChannelPointer=Input(Vec(ChanlPar,Vec(KUints,UInt(10.W)))) 
    val DataValid=Input(Vec(ChanlPar,Vec(KUints,UInt(1.W))))
    val op = Output(Vec(KUints,UInt(8.W)))      // 'kernel' num of o/p
    
    val valid_out=Output(Vec(KUints,UInt(1.W)))

    })
 
    val APEs =  Array.fill(ChanlPar){ Module(new APE(Channels,KUints,Rowsize)).io}
    val temp=Reg(Vec(ChanlPar,Vec(KUints,UInt(8.W))))
    val tempval=Reg(Vec(ChanlPar,Vec(KUints,UInt(1.W))))
    val tempvaldelayed=RegNext(tempval)
    val agrttemp=Reg(Vec(KUints,UInt(8.W)))
    val Agval=Reg(Vec(KUints,UInt(1.W)))
    var chanelscount=0
    val KUintsNo=RegInit(0.U(8.W))
    val ChanlParNo=RegInit(0.U(8.W))
    var agrtemp = 0.U

    for (i<-0 until ChanlPar)
    {
        	APEs(i).ip <> io.ip(i)
        	APEs(i).Kernchpointer <> io.ChannelPointer(i)
        	temp(i)<>APEs(i).op 
          tempval(i)<>APEs(i).valid_out
          APEs(i).Indatavalid<>io.DataValid(i)
     }
  for (p<-0 until KUints )
    {
      for(k<- 0 until ChanlPar)
        {
           when(tempval(0)(0)===1.U)
          {
            
            agrtemp=agrtemp+temp(k)(p)
          }    

        }
        agrttemp(p):= agrtemp 
        agrtemp=0.U
  }
  for(h<-0 until KUints)
  {
    io.op(h):=agrttemp(h)//we shouldnt sum kernel paralism?
    io.valid_out(h):=tempvaldelayed(0)(h) 
  }
} 
