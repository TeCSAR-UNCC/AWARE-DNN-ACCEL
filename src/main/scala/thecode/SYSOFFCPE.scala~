package thecode
import chisel3._
import chisel3.util._
import chisel3.iotesters
import chisel3.iotesters.{ChiselFlatSpec, Driver, PeekPokeTester}
import Chisel.iotesters.{SteppedHWIOTester, ChiselFlatSpec}
import scala.language.reflectiveCalls


class SYSOFFCPE (val rowSize: Int,val filterSize: Int, val stride: Int, val outConv: Int,val KernNum : Int,val KUints : Int,val ChanPar : Int,val ChanBuffer : Int,val GROUPS :Int,val Shuffle :Int, val pipeline :Int ,val Conv :Int,val pool :Int,val FcDiv :Int  ) extends Module 
{
	val io = IO(new Bundle {
		val write_enable   	= Input(Vec(ChanPar,Bool()))//testbench drives
		val write_data  	= Input(Vec(ChanPar,UInt(8.W)))//testbench drives
		val mac_out 		= Output(Vec(ChanPar,Vec(KUints,UInt(8.W))))//mac data
		val Chan_Ptr  	= Output(UInt(10.W))//testbench drives
	 val valid_out=Output(UInt(1.W))
	val WRequest=Output(UInt(1.W))
	//val WData_in=Input(Vec(Conv, Vec(ChanPar,Vec(KUints,UInt(8.W))) )    )
	val WData_in=Input(  Vec(KUints,UInt((ChanPar*Conv*8).W))  )
	//val offset=Output(Vec(KUints,UInt(8.W)))
})
	

		val ChanPTR= RegInit(0.U(10.W))
		val ChanPTRDelay= RegInit(0.U(10.W))
		val ChanPTRDelayFINAL= RegInit(0.U(10.W))
		val MACDELAY = Reg(Vec(ChanPar  ,Vec(KUints,UInt(8.W))) )//we explicitaly delay the mac, try moving this into the mac
		val MultiBuffer = Array.fill(ChanPar){ Module(new buffer(rowSize,filterSize,stride,outConv,KernNum,(ChanBuffer/pipeline),GROUPS,Shuffle,Conv)).io }

























		val CORE = Array.fill(ChanPar){ Module(new macE(KUints,Conv)).io }
val WeightPtr= RegInit(0.U( log2Ceil(  (filterSize*filterSize*KernNum*ChanBuffer+1) ).W )   )
	if(Conv==1)
	{
		WeightPtr := (MultiBuffer(0).rowPtr)+ (MultiBuffer(0).colPtr)*filterSize.asUInt+ (MultiBuffer(0).ChannelPTR)*(filterSize*filterSize).asUInt+(MultiBuffer(0).KernelNum)*(filterSize*filterSize*ChanBuffer).asUInt
	}
	else
	{
	WeightPtr := (MultiBuffer(0).rowPtr)+ (MultiBuffer(0).ChannelPTR)*(filterSize*filterSize).asUInt+(MultiBuffer(0).KernelNum)*(filterSize*filterSize*ChanBuffer).asUInt
	}
	val BufferValid = RegInit(false.B)
	BufferValid := MultiBuffer(0).convState 
		//io.offset:=WeightPtr//RMVED WBANKS, no more offset
		io.WRequest:=BufferValid//RMVED WBANKS
/*		when (WinValid==1.U)
		{
			for (k <- 0 until KUints)
			{			
				for (j <- 0 until ChanPar)
				{
					for (r <- 0 until filterSize)
					{
						for (c <- 0 until filterSize)
						{
							//FilterRegs(k)(j)(r)(c):=io.WData_in(k)(j)(r)(c)
								:=io.WData_in(k)(j)(r)(c)
						}
					}
				}
			}
		}
*/
		for(i <- 0 until ChanPar)
		{
			MultiBuffer(i).write_enable:=io.write_enable(i)// when apes output the must send a valid and a channel number
			MultiBuffer(i).write_data:=io.write_data(i)
			MultiBuffer(i).CHGO:=1.U

		}
		//Each buffer is paired with each mac core (each mac core has KUnits number of macs in them)
		for (j <- 0 until ChanPar)
		{
			for (i <- 0 until KUints)
			{
				for (l <- 0 until Conv)
				{
					CORE(j).input_data(l) := MultiBuffer(j).read_data(l)
					CORE(j).k_data(i)(l) := 0.U	
				}
				CORE(j).WD := MultiBuffer(0).WD
				CORE(j).KD := MultiBuffer(0).KD
				CORE(j).convState := MultiBuffer(0).convState 
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
		when(CORE(0).MACSTART)//when one channel is ready so is another
		{
			for (i <- 0 until KUints)
			{			
				for (j <- 0 until ChanPar)
				{
					for (l <- 0 until Conv)
					{
						//CORE(j).k_data(i)(l) := WBANKS.Data_out(l)(j)(i)
						CORE(j).k_data(i)(l) := io.WData_in(i)(j*l)
						//okay so, i will recieve data kernel (chan*conv, fashion)
						// i also need to select the bits for each j and I
						
						//TODO:invert this so the data in is in the fashion kernel, conv, chan
					}
				}
			}		
		}
					for (j <- 0 until ChanPar)
					{
				
						for (i <- 0 until KUints)
						{
					
								io.mac_out(j)(i):=MACDELAY(j)(i)
						}

					}


}
//do something for system pool designs
