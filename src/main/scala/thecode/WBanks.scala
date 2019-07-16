package thecode
import chisel3._
import chisel3.util._
import chisel3.iotesters
import chisel3.iotesters.{ChiselFlatSpec, Driver, PeekPokeTester}
import Chisel.iotesters.{SteppedHWIOTester, ChiselFlatSpec}
import scala.language.reflectiveCalls



class WBanks (val filterSize: Int,val KernNum : Int,val KUints : Int,val ChanPar : Int,val ChanBuffer : Int, val Conv :Int,val FcDiv :Int,val ROM :Int) extends Module 
{
	val io = IO(new Bundle {
		val Valid_IN   	= Input(UInt(1.W))//testbench drives
		val PTR  	= Input(UInt(log2Ceil(  (filterSize*filterSize*KernNum*ChanBuffer+1) ).W))//testbench drives
		val Data_out = Output(Vec(Conv, Vec(ChanPar,Vec(KUints,UInt(8.W))) )    )
		val valid_Out=Output(UInt(1.W))
})
	//each channel hs the same row and col, channel, and kern , but diffrent kern and chanel parlism
		//ie: in sych




val THEBANKS = for (i <- 0 until ChanPar*KUints) yield 
	{  							//to create multiple layers
	       val BANK = Module(new memory(filterSize,KernNum,KUints,ChanPar,ChanBuffer,i,Conv,FcDiv,ROM ))
	  
	       BANK
	 }

		//validinit=Reg(Vec(Channels, UInt(1.W)))

		for(i <- 0 until KUints)
		{
			for(j <- 0 until ChanPar)
			{
				THEBANKS(j+i*ChanPar).io.Valid_IN:=io.Valid_IN
				THEBANKS(j+i*ChanPar).io.PTR:=io.PTR
			}
		}


io.valid_Out:=THEBANKS(0).io.valid_Out

for(i <- 0 until KUints)
		{
			for(j <- 0 until ChanPar)
			{
				for(l <- 0 until Conv)
				{
					io.Data_out(l)(j)(i):=THEBANKS(j+i*ChanPar).io.Data_out(l)
				}
			}
		}










}
