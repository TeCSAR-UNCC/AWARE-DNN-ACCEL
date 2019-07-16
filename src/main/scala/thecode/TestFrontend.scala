package thecode
import chisel3._
import chisel3.util._
import chisel3.iotesters
import chisel3.iotesters.{ChiselFlatSpec, Driver, PeekPokeTester}
import Chisel.iotesters.{SteppedHWIOTester, ChiselFlatSpec}
import scala.language.reflectiveCalls


class TestFrontend (val rowSize: Int,val filterSize: Int, val stride: Int, val outConv: Int,val KernNum : Int,val KUints : Int,val ChanPar : Int,val ChanBuffer : Int, val delay : Int,val GROUPS :Int,val Shuffle :Int,val pipeline :Int ,val Conv :Int,val poolint :Int,val FcDiv :Int,val WaitTime :Int, val waitmult: Int, val sysoff :Boolean,val ROM :Int,val AVG :Int) extends Module 
{

val io = IO(new Bundle {


//MDAPE test, cahnnel reduction

var newPar=0
		if(GROUPS==ChanBuffer*ChanPar)
		{
			newPar=1

		}
		else
		{
			newPar=ChanPar
			//in this case its the original value
		}

		
	    	val output = Output(Vec(KUints,UInt(8.W)))     
    		val valid_out=Output(Vec(KUints,UInt(1.W)))
		val WData_in=  if (sysoff) Some( Input(  Vec(KUints,UInt((newPar*Conv*8).W))  ) ) else None 
		val request   	=  if (sysoff) Some( Output(UInt(1.W)) ) else None

})



  val CPE = {Module(new TestCPE(rowSize,filterSize,stride,outConv, KernNum , KUints , ChanPar , ChanBuffer,GROUPS,Shuffle,pipeline,Conv,poolint,FcDiv,WaitTime,waitmult,sysoff,ROM)).io} 
	//val MDAPE = {Module(new MDAPE(ChanPar,KUints, rowSize)).io} 
//this should be conditional, add a minimum delay?
val sych = Array.fill(KUints) {Module(new SyncMachine(delay,outConv)).io} 

if(GROUPS==ChanBuffer*ChanPar)
{

		for( l <- 0 until 1) 
		{
			for( j <- 0 until KUints) 
			{
	
				sych(j).write_data:=CPE.mac_out(0)(j)
				sych(j).write_enable:=CPE.valid_Out
				io.output(j):=sych(j).read_data
				io.valid_out(j):=sych(j).output_valid
					
			}

		}



}
else
{					    //used to be channel paralism, we've reduced it now
		val MDAPE = {Module(new MDAPE(1,ChanBuffer,KUints, outConv)).io} 
		//val PartialAccum = Wire(Vec((Conv,UInt(8.W)))
		//val PartialAccum = Wire(UInt(16.W))
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
				MDAPE.DataValid(0)(j):=CPE.valid_Out//1c	
				//for now everything is sycrhonous
				MDAPE.ChannelPointer(0)(j):=CPE.Chan_Ptr
			sych(j).write_data:=MDAPE.op(j)
			sych(j).write_enable:=MDAPE.valid_out(j)
			io.output(j):=sych(j).read_data
			io.valid_out(j):=sych(j).output_valid
				
		}


}


	

		if (sysoff)
		{

		for(i <- 0 until KUints)
		{
			CPE.WData_in.get(i):=io.WData_in.get(i)//1a	
		}
		io.request.get:=CPE.request.get
		}
		else
		{


		}
		//work out the 2d wiring
		//insure fair connections

		

}
