package thecode
import chisel3._
import chisel3.util._
import chisel3.iotesters
import chisel3.iotesters.{ChiselFlatSpec, Driver, PeekPokeTester}
import Chisel.iotesters.{SteppedHWIOTester, ChiselFlatSpec}
import scala.language.reflectiveCalls


class frontend (val rowSize: Int,val filterSize: Int, val stride: Int, val outConv: Int,val KernNum : Int,val KUints : Int,val ChanPar : Int,val ChanBuffer : Int, val delay : Int,val GROUPS :Int,val Shuffle :Int,val pipeline :Int ,val Conv :Int,val poolint :Int,val FcDiv :Int,val ROM :Int,val AVG :Int) extends Module 
{


val io = IO(new Bundle {
		val write_enable   	= Input(Vec(ChanPar,Bool()))//testbench drives
		val write_data  	= Input(Vec(ChanPar,UInt(8.W)))//testbench drives
	    	val output = Output(Vec(KUints,UInt(8.W)))     
    		val valid_out=Output(Vec(KUints,UInt(1.W)))
		val DONE=Output(UInt(1.W))

})


  	val CPE = {Module(new CPE(rowSize,filterSize,stride,outConv, KernNum , KUints , ChanPar , ChanBuffer,GROUPS,Shuffle,pipeline,Conv,poolint,FcDiv,ROM)).io} 
	//val MDAPE = {Module(new MDAPE(ChanPar,KUints, rowSize)).io} 

	val sych = Array.fill(KUints) {Module(new SyncMachine(delay,outConv)).io} 

	io.DONE:=CPE.DONE(0)



		for(i <- 0 until ChanPar)
		{
			CPE.write_enable(i):=io.write_enable(i)// 19 when apes output the must send a valid and a channel number
			CPE.write_data(i):=io.write_data(i)//1a
		}


		if(GROUPS==ChanBuffer*ChanPar)
		{
			
				for( j <- 0 until KUints) 
				{

					sych(j).write_data:=CPE.mac_out(0)(j)
					sych(j).write_enable:=CPE.valid_out
					io.output(j):=sych(j).read_data
					io.valid_out(j):=sych(j).output_valid
			
				}

			
		}
else
{
		val MDAPE = {Module(new MDAPE(1,ChanBuffer,KUints, outConv)).io} 




		val KMAC_OUT =  Wire(Vec(KUints,UInt(8.W)))
	    	var tempWire = 0.U
		//mul:=(in1*in2)+sum //Name changed
		for( l <- 0 until KUints) 
		{
			for( j <- 0 until ChanPar) 
			{
			  tempWire=tempWire+CPE.mac_out(j)(l)
			}
			KMAC_OUT(l):= tempWire
			tempWire=0.U
		}


	
		for( j <- 0 until KUints) 
		{
			
				MDAPE.ip(0)(j):=KMAC_OUT(j)	//1b
				MDAPE.DataValid(0)(j):=CPE.valid_out//1c	
				//for now everything is sycrhonous
				MDAPE.ChannelPointer(0)(j):=CPE.Chan_Ptr
			sych(j).write_data:=MDAPE.op(j)
			sych(j).write_enable:=MDAPE.valid_out(j)
			io.output(j):=sych(j).read_data
			io.valid_out(j):=sych(j).output_valid
				
		}
}

}
