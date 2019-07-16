package thecode
import chisel3._
import chisel3.util._
import chisel3.iotesters
import chisel3.iotesters.{ChiselFlatSpec, Driver, PeekPokeTester}
import Chisel.iotesters.{SteppedHWIOTester, ChiselFlatSpec}
import scala.language.reflectiveCalls



class memgen (val AddreSize: Int,val Conv: Int) extends Module 
{
	val io = IO(new Bundle {
		val WE   	= Input(UInt(1.W))//testbench drives
		val RE   	= Input(UInt(1.W))//testbench drives
		val WPTR  	= Input(UInt(log2Ceil(  (AddreSize+1) ).W))//testbench drives
		val RPTR  	= Input(UInt(log2Ceil(  (AddreSize+1) ).W))//testbench drives
		val Data_in = Input(UInt(8.W))
		val Data_out = Output(Vec(Conv,UInt(8.W)))
		//val Data_out = Output(UInt(8.W))

})


	val genmemory = SyncReadMem( ( (AddreSize)) , UInt(8.W)  )

	//val WRITEPtr =RegInit(0.U(log2Ceil((( (filterSize*filterSize*KernNum*ChanBuffer)))).W))//writing ptr

	//val RptrREG=RegInit(0.U( log2Ceil(  (filterSize*filterSize*KernNum*ChanBuffer+1) ).W )   )


	//ptrREG:=io.PTR


	when (io.RE===1.U)
	{
		for(i <- 0 until Conv)
		{
			io.Data_out(i):=genmemory.read(io.RPTR+i.asUInt)
		}
	}



	.otherwise
	{
		
		for(i <- 0 until Conv)
		{
			io.Data_out(i):=0.U
		}


	}

	when (io.WE===1.U)
	{
		genmemory.write(io.WPTR,io.Data_in)
	}



}
