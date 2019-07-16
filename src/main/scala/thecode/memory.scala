package thecode
import chisel3._
import chisel3.util._
import chisel3.iotesters
import chisel3.iotesters.{ChiselFlatSpec, Driver, PeekPokeTester}
import Chisel.iotesters.{SteppedHWIOTester, ChiselFlatSpec}
import scala.language.reflectiveCalls
import chisel3.util.experimental.loadMemoryFromFile
import chisel3.experimental.chiselName
//this can be simplified
@chiselName
class memory (val filterSize: Int,val KernNum : Int,val KUints : Int,val ChanPar : Int,val ChanBuffer : Int,val ID : Int,val Conv :Int,val FcDiv :Int, val ROM :Int) extends Module 
{
	val io = IO(new Bundle {
		val Valid_IN   	= Input(UInt(1.W))//testbench drives
		val PTR  	= Input(UInt(log2Ceil(  (filterSize*filterSize*KernNum*ChanBuffer+1) ).W))//testbench drives
		val Data_out = Output(Vec(Conv,UInt(8.W)))
		val valid_Out=Output(UInt(1.W))
})
//load memory is next

	var datainit= Seq(0.U(8.W))
	for(i <- 1 until ( (filterSize*filterSize*KernNum*ChanBuffer))/FcDiv )
	{
		datainit = datainit++Seq((i%255).asUInt(8.W))

	}
	if(ROM==1)
	{

		val Wmemory = Mem( ( (filterSize*filterSize*KernNum*ChanBuffer))/FcDiv , UInt((8*Conv).W)  )
		val ptrREG=RegInit(0.U( log2Ceil(  (filterSize*filterSize*KernNum*ChanBuffer+1) ).W )   )
		val WRITEPtr = RegInit(0.U(log2Ceil((((filterSize*filterSize*KernNum*ChanBuffer+1)))).W))//writing ptr
		ptrREG:=io.PTR
		val DOUT_A = Wire(UInt((8*Conv).W))
		val DOUT_B=DOUT_A.asTypeOf( Vec(Conv,UInt(8.W)))
		DOUT_A:=Wmemory.read(ptrREG)
		for(i <- 0 until Conv)
		{
			io.Data_out(i):=DOUT_B(i)

		}


	}
	else
	{			//SyncReadMem
		val Wmemory = SyncReadMem( ( (filterSize*filterSize*KernNum*ChanBuffer))/FcDiv , UInt((Conv*8).W)  )
		val ptrREG=RegInit(0.U( log2Ceil(  (filterSize*filterSize*KernNum*ChanBuffer+1) ).W )   )
		val WRITEPtr = RegInit(0.U(log2Ceil((((filterSize*filterSize*KernNum*ChanBuffer+1)))).W))//writing ptr
		ptrREG:=io.PTR
		val DOUT_A = Wire(UInt((8*Conv).W))
		val DOUT_B=DOUT_A.asTypeOf( Vec(Conv,UInt(8.W)))
		DOUT_A:=Wmemory.read(ptrREG)
		for(i <- 0 until Conv)
		{
			io.Data_out(i):=DOUT_B(i)

		}
		//loadMemoryFromFile(memory, "/workspace/workdir/mem1.txt")
	}

	
	
	
	val validInREG=RegInit(0.U(1.W))
	val validInREGDelayed=RegInit(0.U(1.W))

	//logaritmic memory adresses
	val dataREG=RegInit(0.U(8.W))
	val validOutREG=RegInit(0.U(1.W))
	
	validInREG:=io.Valid_IN
	validInREGDelayed:=validInREG
	

	io.valid_Out:=validInREGDelayed


	
}

		/*
		//val kernmem = Mem( ( (filterSize*filterSize*KernNum*ChanBuffer))/FcDiv , UInt(8.W)  )
		val kernmem =VecInit(datainit)
		for(i <- 0 until Conv)
		{
			io.Data_out(i):=kernmem(ptrREG+i.asUInt)
		}
		*/

		/*when(WRITEPtr<(((filterSize*filterSize*KernNum*ChanBuffer+1)).asUInt-1.U))
		{	
			

			Wmemory.write(WRITEPtr,WRITEPtr) 
				
			WRITEPtr:=WRITEPtr+1.U
			//kernmem(ptrREG.asUInt)
			//Wmemory.write(ptrREG, kernmem(ptrREG.asUInt)) //kernmem(ptrREG.asUInt)
		}*/


