package thecode
import chisel3._
import chisel3.util._
import chisel3.iotesters
import chisel3.iotesters.{ChiselFlatSpec, Driver, PeekPokeTester}
import Chisel.iotesters.{SteppedHWIOTester, ChiselFlatSpec}
import scala.language.reflectiveCalls



class layer (val rowSize: Int,val filterSize: Int, val stride: Int, val Out_size: Int,val KernNum : Int,val KUints : Int,val ChanPar : Int,val ChanBuffer : Int,val poolOut: String, val delay : Int,val GROUPS :Int,val Shuffle :Int,val pipeline :Int,val Conv :Int, val poolint :Int,val FcDiv :Int,val ROM :Int,val AVG :Int) extends Module 
{

val io = IO(new Bundle {
		val write_enable   	= Input(Vec(ChanPar,Bool()))//testbench drives
		val write_data  	= Input(Vec(ChanPar,UInt(8.W)))//testbench drives
	    	val output = Output(Vec(KUints,UInt(8.W)))     
    		val valid_out=Output(Vec(KUints,UInt(1.W)))
})

//TODO:make dynamic sych machine, make dynamic AVG layer (A1,A2?)

	val frontend = Module(new frontend(rowSize,filterSize,stride,Out_size, KernNum , KUints , ChanPar , ChanBuffer,delay,GROUPS,Shuffle,pipeline,Conv,poolint,FcDiv,ROM,AVG))
	val backends = Array.fill(KUints){ Module(new backend(Out_size,poolOut,KernNum)).io } 


	for(i <- 0 until ChanPar)
		{
			frontend.io.write_enable(i):=io.write_enable(i)// when apes output the must send a valid and a channel number
			frontend.io.write_data(i):=io.write_data(i)
		}



	
			for( j <- 0 until KUints) 
			{
				
					backends(j).input:=frontend.io.output(j)
					backends(j).input_valid:=frontend.io.valid_out(j)

						io.output(j):=backends(j).output.asUInt
						io.valid_out(j):=backends(j).output_valid
			}
		



}


		

