//*********************************************************************************************************************************************************************************************
//This is a APE of the CNN accelerator.		
//Authors: Adarsh Sawant 
//Editor: Justin Sanchez
//Current Version:1.4
//Module Instatiated: None
//Version1.0 change log
// This module would  aggregate the output of the channnels into single output.
//This is a dynamic state machine which would wait for the validinit(n) signal when it is in the nth state. 
//The number of the states are dependent on the number of channels 
//This block is specially designed for the tensor flow buffer as the output from the tensor flow buffer comes in the serial fashion :one by one channel each giving a valid signal.
//This code would Instatiate Channels number of the  adders and aggregate the output of each channel.
//Once the state of the block equalls to number of the channel then we would get the valid out signal . 
//Version 1.2
//The version 1.2 of the code changes the way we do the addition of the channels beacuse of the way the tensor buffer outputs the data
// This version would  store the each row of the channel in an array of memory where each unit in location corrsponds to the accumulator for the the pixel over the channnels 
//after the first row is store in the memory location we for next subsequent rows we add the data from the first row(stored for first channel )to the next channel row data
//once we reach to the final row of the channel we output the data on the output line and valid signal would be high for one clock duration.
//latency of module:We need to wait till the first window gets filled up after which this module works in the streaming fashion. 
// pixlcounter is used to iterate over the pixels in the window(rowsize)
// state is used to iterate over the channels:
//
//		___Input_________________________________________________
//      |__0___1___2___3___4_____________Channel 0______________|	
//      |__0___1___2___3___4_____________Channel 1______________|
//		|__0___1___2___3___4_____________Channel 2______________|                     		accumlateRegs
//                                                                   _______________________________________________
//																	|__0__|__1___|__2___|__3___|__4___|__5___| n___|
//
//
//
////Version 1.3
//The internal changes to the APU where the following:
//
//1. The accumalation does not increase bit width, if it did the every layer would require a doubling of bitwidth
//		or an overflow/ ownsampling mechanism would be implemented (instead dynamic fixed point is used)
//
//2. The input for a APU is a single port that gets TotalChannel/ChannelParrallelism # of channels switching
//
// 3. The APU had a problem with representing odd # of channels, this was do to not having explicit control for reset of pixel counter 
//
//4. The valids were registered and the inputs were not, leaving a offset delay, to fix we registered the input port
//	this decison was mad to allow the deign to achieve a better max frequency

// VERSION 1.4 : Adarsh Sawant
// This version changes the way we recive the channel valids A.K.A chppointer from the current version:
// In this version the channel valids are recived as  a chnannel poninter of 10 bits rahter than an indivisula bits of channel valids as in the earlier version
// WE have also explicitly reseted the the output counter as this was creating pronblem while we were outputing the data
//This Version also add a datavalid signal which would indicate when the chpointer  and the data on the data line is valid .
//state is replaced by currentChptr to describe the actual meaning
//Valid is replaced by Chpointer to describe the correct meaning



// VERSION 1.5 : Justin S.
// Due to timing issues between high frequency producer layers and low frequency consumer layers 
//we need to adjust how long each output stays out, (accirding to the ratio of the frequecies)
//this should also be done in consideration if the time window the MDAPE has to output 


// THE APU EXPECTS THAT THE DATA INPUT IS STABLE FOR ATLEAST ONE CLOCK CYCLE BEFORE THE ARRIVAL OF CHANNEL POINTER AFTER THE ARRIVAL ON THE DATA LINE. THERE IS NO SUCH DEPENDENCY BETWEEN THE VALID SIGNAL AND AND CHANNEL POINTER.THIS IS 
// DONE TO MAKE SURE THAT IN LATTER STAGES OF DESIGN WHERE WE WOULD IMPLEMENT BIG NETWORKS WE SHOULDNT HAVE DIFFICULTY IN TIMING ANALYSIS. 

package thecode
import chisel3._
import chisel3.util._
import chisel3.iotesters
import chisel3.iotesters.{ChiselFlatSpec, Driver, PeekPokeTester}
import Chisel.iotesters.{SteppedHWIOTester, ChiselFlatSpec}
import scala.language.reflectiveCalls
import chisel3.experimental._

class APU(val Channels: Int,val Rowsize:Int) extends Module
{	 
	  val rowcntlength = log2Ceil(Rowsize+1)
	  //val statelength=log2Ceil(Channels+1)
	  val statelength=10

val io = IO(new Bundle {   
    val ip = Input(UInt(8.W)) // each input from the chanels
  	//val valid=Input(Vec(Channels,UInt(1.W)))
  	val datavalid=Input(UInt(1.W)) // added in this version each tensorbuffer would give out single valid signal to indicate whether the channel pointer is valid or not.
    //val valid=Input(UInt(10.W))
    val chpointer=Input(UInt(10.W)) // actual channel pointer indicating from whoich channel we are gettting the data.
    val op = Output(UInt(8.W))	
	val valid_out=Output(UInt(1.W))
    })

  	 val temp = RegInit(0.U(8.W))// we dont account for overflow-c1
	val tempval=RegInit(0.U(1.W))
	val input=RegInit(0.U(8.W))
	val currentChptr = RegInit(0.U(statelength.W))
	//val validinit=Reg(Vec(10,Bool()))// if you register the valid you must register the input
	tempval:=0.U
	temp:=0.U
	//validinit:=io.valid.toBools
	val accumlateRegs = Reg(Vec(Rowsize,UInt(8.W)))
	val pixlcounter=RegInit(0.U(rowcntlength.W))
	val outcounter=RegInit(0.U(rowcntlength.W))
		input:=io.ip
	when(io.datavalid===true.B)
	{

	when (currentChptr<(Channels.asUInt()))//good
	{	
		when(pixlcounter!=((Rowsize).asUInt))//should be pixlcounter!=((Rowsize).asUInt)
         	{
				pixlcounter:=pixlcounter+1.U
				when(currentChptr.asUInt===0.U)// the first input just places
				{			
					accumlateRegs(pixlcounter) :=input//change to single port-c1
					
				}
				.otherwise// the rest of the inputs sum
				{	
					accumlateRegs(pixlcounter) :=accumlateRegs(pixlcounter)+input
				

							
				}
				when(currentChptr.asUInt()===Channels.asUInt()-1.U)//When at last Channel:		
				{
						//what I will do here is add a counter to before this is done
					tempval:=1.U//asigh the

					//instead of this just set a send state, wait for 
					temp:=accumlateRegs(pixlcounter)
				}
				.otherwise
				{
					temp:=0.U
				}
				
				when(pixlcounter===(Rowsize.asUInt())-1.U)
				{
					currentChptr :=currentChptr+1.U
					pixlcounter:=0.U//explicitly reset pixlcounter

				}
			}

		  }	
		
	}
	when(currentChptr===Channels.asUInt())
	{
		currentChptr:=0.U
	}

	when (tempval===1.U)// add that counter to this as well
	{			
		io.op := accumlateRegs(outcounter)
		outcounter:=outcounter+1.U
		io.valid_out:=1.U
		
	}
	.otherwise
	{
		io.op:=0.U
		io.valid_out:=0.U
	}
	when(outcounter===Rowsize.asUInt)// make sure this doesnt mess with the timing
	{
		outcounter:=0.U
	}

	

}
