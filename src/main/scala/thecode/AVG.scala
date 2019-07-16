package thecode
import chisel3._
import chisel3.util._
import chisel3.iotesters
import chisel3.iotesters.{ChiselFlatSpec, Driver, PeekPokeTester}
import Chisel.iotesters.{SteppedHWIOTester, ChiselFlatSpec}
import scala.language.reflectiveCalls


class AVG (val rowSize: Int,val filterSize: Int, val stride: Int, val outConv: Int,val KernNum : Int,val KUints : Int,val ChanPar : Int,val ChanBuffer : Int,val testing : Int,val pipelining : Int) extends Module 
{
	val io = IO(new Bundle {
		val write_enable   	= Input(Vec(ChanPar,Bool()))//testbench drives
		val write_data  	= Input(Vec(ChanPar,UInt(8.W)))//testbench drives
		
		val mac_out 		= Output(Vec(ChanPar,Vec(KUints,UInt(8.W))))//mac data
		val Chan_Ptr  	= Output(UInt(10.W))//testbench drives
		 val valid_out=Output(UInt(1.W))
})

		//make an array of memory with dataflow
		//val values = (1 to 7).toArray

val WRITEPtr =RegInit(0.U(log2Ceil(((filterSize*filterSize+1))).W))//writing ptr
val THEWEIGHTs = SyncReadMem( (filterSize*filterSize) , UInt(8.W)  )
val OutPtr =RegInit(0.U(log2Ceil(((filterSize*filterSize+1))).W))//reading ptr

		val kernmem =VecInit(Array(1.U, 2.U,3.U ,4.U,5.U,6.U,7.U))//
		val ChanPTR= RegInit(0.U(10.W))
		
		val MACDELAY = Reg(Vec(ChanPar  ,Vec(KUints,UInt(8.W))) )//we explicitaly delay the mac, try moving this into the mac engine
		val ChanPTRDelay= RegInit(0.U(10.W))
		val ChanPTRDelayFINAL= RegInit(0.U(10.W))

		//kernmem((InputBuffer.io.KernelNum)%KUints.asUInt)
		    //val m = VecInit(Array(1.U, 2.U, 4.U, 8.U))
		//make an array of 
		//2D-Buffer
	
		val MultiBuffer = Array.fill(ChanPar){ Module(new buffer(rowSize,filterSize,stride,outConv,KernNum,ChanBuffer/pipelining,1,1,1)).io }
		val CORE = Array.fill(ChanPar){ Module(new macE(KUints,1)).io }
		//validinit=Reg(Vec(Channels, UInt(1.W)))

		for(i <- 0 until ChanPar)
		{
			MultiBuffer(i).write_enable:=io.write_enable(i)// when apes output the must send a valid and a channel number
			MultiBuffer(i).write_data:=io.write_data(i)
		}

		//Each buffer is paired with each mac core (each mac core has KUnits number of macs in them)
		
		for (j <- 0 until ChanPar)
		{
			for (i <- 0 until KUints)
			{
			
				CORE(j).input_data(0) := MultiBuffer(j).read_data(0) 
				CORE(j).k_data(i)(0) := 0.U	
				CORE(j).WD := MultiBuffer(j).WD
				CORE(j).KD := MultiBuffer(j).KD
				
				CORE(j).convState := MultiBuffer(j).convState 
				//io.mac_out(i+j*ChanPar):=0.U
				io.mac_out(j)(i):=0.U
				
			}

					
					
				
				
	
		}	
			ChanPTR:=MultiBuffer(0).ChannelPTR//grab a channel ptr from one of the buffers
			//the following 2 lines delay the channel ptr by 2 cycles
			ChanPTRDelay:=ChanPTR
			ChanPTRDelayFINAL:=ChanPTRDelay

			io.Chan_Ptr:=ChanPTRDelayFINAL

 		 	//io.valid_out(j)(i)(k):= := (io.sel & io.in1) | (~io.sel & io.in0)
					
			for (j <- 0 until ChanPar)
			{
				
					
					when( (CORE(0).valid) ) 		
					{	io.valid_out:=1.U
						for (i <- 0 until KUints)
						{
						//io.mac_out(i+j*ChanPar):=CORE(j).mac_out(i)
							MACDELAY(j)(i):=CORE(j).mac_out(i)
						}
					}
					.otherwise
					{
						io.valid_out:=0.U
					}
				

			}  	

		//the macs are fairly automated with their own control
		//we just need to hook up some weights to the macs
	//((filterSize*filterSize*KernNum*KUints*ChanPar*ChanBuffer+KernNum*KUints).asUInt)
//		// (InPtr<(rowSize.asUInt(log2Ceil(rowSize).W))-1.U)
//(filterSize*filterSize*KernNum*KUints*ChanPar*ChanBuffer+KernNum*KUints)
		
//

			if(testing ==1)
			{
				when(WRITEPtr<(((filterSize*filterSize).asUInt-1.U)))
				{
					WRITEPtr:=WRITEPtr+1.U
					THEWEIGHTs.write(WRITEPtr, kernmem(WRITEPtr.asUInt)) 
				}
			}
			else
			{


			}
	
		when(CORE(0).MACSTART)//when one channel is ready so is another
		{
			for (i <- 0 until KUints)
			{		
				for (j <- 0 until ChanPar)
				{
					CORE(j).k_data(i)(0) := THEWEIGHTs.read(OutPtr)
					OutPtr:=OutPtr+1.U
				}
			}
					
		}
				for (j <- 0 until ChanPar)
					{
				
						for (i <- 0 until KUints)
						{
						//io.mac_out(i+j*ChanPar):=CORE(j).mac_out(i)
								io.mac_out(j)(i):=MACDELAY(j)(i)
						}

					}

		
}
