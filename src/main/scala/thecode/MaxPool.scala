package thecode
import chisel3.iotesters.{PeekPokeTester, Driver}
import chisel3._
import chisel3.iotesters
import chisel3.util._
import chisel3.iotesters.{ChiselFlatSpec, Driver, PeekPokeTester}
import Chisel.iotesters.{SteppedHWIOTester, ChiselFlatSpec}
import scala.language.reflectiveCalls



/*
on the when recieve the first row it stores the first elment 
then compares to the next one
depending on the result that is the one that goes into the buffer

after one row we switch channels until we reach the last channel

then we enter the sencond row state

this state 
















*/

class MaxPool(val OutRowsize:Int, val chan :Int) extends Module
{
	val bufferBitW = log2Ceil(OutRowsize+1)

	val io = IO(new Bundle 
	{   
		val input = Input(UInt(8.W))  // each input from the chanels
		val valid_in=Input(UInt(1.W)) 
		val output = Output(UInt(8.W))      // 'kernel' num of o/p
		val valid_out=Output(UInt(1.W))
	})
 	//old memory
	//val buffer = Reg(Vec(OutRowsize,UInt(8.W))) //we use this to buffer input pixles
	//make this work with a certain number of channels and bram
	val inputCntr=RegInit(0.U(bufferBitW.W))// this keeps track of inputs
	val CompareCntr=RegInit(0.U(1.W))// this slowly moves the input counter
	val RowCntr=RegInit(0.U(1.W))// this slowly moves the input counter
	val OutputReg=RegInit(0.U(8.W))
	val ValidOutReg=RegInit(0.U(8.W))
	ValidOutReg:=0.U

val WPtr =RegInit(0.U( log2Ceil(OutRowsize*chan+1  ).W )   )
val RE=RegInit(0.U(1.W))
val WE=RegInit(0.U(1.W))
val Rptr=RegInit(0.U( log2Ceil(OutRowsize*chan+1   ).W )   )
val chanPtr=RegInit(0.U( log2Ceil(chan+1   ).W )   )

val temp1=RegInit(0.U(8.W))
val temp2=RegInit(0.U(8.W))
val DATAin = Wire(UInt(8.W))
val DATAOut = Wire(UInt(8.W))
DATAin:=0.U
DATAOut:=0.U
temp2:=DATAOut
	val poolmem = SyncReadMem( ( ((OutRowsize/2)*chan)) , UInt(8.W)  )

	//val WRITEPtr =RegInit(0.U(log2Ceil((( (filterSize*filterSize*KernNum*ChanBuffer)))).W))//writing ptr

	//val RptrREG=RegInit(0.U( log2Ceil(  (filterSize*filterSize*KernNum*ChanBuffer+1) ).W )   )


	//ptrREG:=io.PTR


	when (RE===1.U)
	{
		
		DATAOut:=poolmem.read(Rptr)
		
	}


	when (WE===1.U)
	{
		poolmem.write(WPtr,DATAin)
	}


	when(io.valid_in.toBool)
	{
		
		// temp:= DATAOut
		when(RowCntr===0.U)
		{//when in the first row
			
			when( (CompareCntr)===(0.U))
			{//and first element
				//buffer(inputCntr):= io.input
				temp1:= io.input
				CompareCntr:=CompareCntr+1.U
				//just place it in, and move to 2nd element
			}
			.otherwise
			{//on 2nd element

				//when (io.input > buffer(inputCntr)) 
				when (io.input >temp1) 
				{//compare to 1st element
					//buffer(inputCntr):= io.input
					//and replace if nessesary
					DATAin:=io.input


				}
				.otherwise
				{
					DATAin:=temp1

				}
				WPtr:=inputCntr+chanPtr*OutRowsize.asUInt
				WE:=1.U
				CompareCntr:=CompareCntr+1.U
				inputCntr:=inputCntr+1.U
				//go back to first element
				// and move pointer
			}
		}
		.otherwise
		{//2nd row	
			Rptr:=inputCntr+chanPtr*OutRowsize.asUInt
			
			when( (CompareCntr)===(0.U))
			{	//first element
				
				temp1:= io.input
				
				//when (io.input > buffer(inputCntr))
				/*				
				when (io.input >temp2 )
				{//compare
					
					temp1:= io.input
				}
				.otherwise
				{
					temp1:= temp2
				}
				*/

				RE:=1.U
					
				CompareCntr:=CompareCntr+1.U
				//go to 2nd element
			}
			.otherwise
			{//2nd element
			
				//when (io.input > buffer(inputCntr)) 
				when ((io.input >temp1) &(io.input >temp2))
				{//compare
					//buffer(inputCntr):= io.input
					OutputReg:=io.input
					
					//output input if inpute> buffer
				}
				.otherwise
				{//otherwise output
					//OutputReg:=buffer(inputCntr)
					when(temp1>temp2)
					{
						OutputReg:=temp1

					}
					.otherwise
					{
						OutputReg:=temp2						
					}
		
				}
				ValidOutReg:=1.U
				CompareCntr:=CompareCntr+1.U
				inputCntr:=inputCntr+1.U
			}	
		}


	}


		when( (inputCntr)===((OutRowsize/2).asUInt))
		{
			inputCntr:=0.U
			chanPtr:=chanPtr+1.U
			when( (chanPtr)===((chan).asUInt))
			{

				RowCntr:=RowCntr+1.U
				//Re:=1.U
				//Rptr:=chanPtr+1.U
				chanPtr:=0.U
				//temp2:=DATAOut
			}
		
		}
	
		
		io.output:=OutputReg
		io.valid_out:=ValidOutReg

}
	
