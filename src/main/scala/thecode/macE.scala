package thecode
import chisel3._
import chisel3.util._
import chisel3.iotesters
import chisel3.iotesters.{ChiselFlatSpec, Driver, PeekPokeTester}
import Chisel.iotesters.{SteppedHWIOTester, ChiselFlatSpec}
import scala.language.reflectiveCalls


class macE ( val KUints : Int, val Conv : Int) extends Module 
{
	val io = IO(new Bundle {
		
		val input_data  	= Input(Vec(Conv,UInt(8.W)))//testbench drives
		val k_data 		= Input(Vec(KUints,Vec(Conv,UInt(8.W))))//mac data
		val KD = Input(Bool())//mac control
		val WD = Input(Bool())//mac control
		
		val convState  	= Input(Bool())//mac control

		
		val mac_out 		= Output(Vec(KUints,UInt(8.W)))//mac data
		


		val valid   	= Output(Bool())
		val MACSTART   	= Output(Bool())
})

		io.valid:=0.U
		//Mac engine
		val Engine = Array.fill(KUints){ Module(new MAC(Conv)).io }

		//Status registers+ delay circuts
		val delay = RegInit(false.B)
		val Start = RegInit(false.B)
		//convstate->delay->start
		val pastKD = RegInit(true.B)
		val KDDelay = RegInit(true.B)
		//KD->pastKD->KDDelay
		
		val pastWD = RegInit(true.B)
		val WDDelay = RegInit(true.B)
		val channelDone = RegInit(false.B)
		//WD->pastWD->WDDelay

		//wiring
		for (j <- 0 until KUints)
		{

				for (i <- 0 until Conv)
				{
					Engine(j).a(i) := io.input_data(i)
					Engine(j).b(i) := 0.U	
				}
				
				Engine(j).r := false.B
			
		}

		delay:=io.convState
		pastKD:=io.KD
		pastWD:=io.WD
		WDDelay:=pastWD
		KDDelay:=pastKD
		Start:=delay
			io.MACSTART:=Start

		for (j <- 0 until KUints)
		{
				
			Engine(j).enable := 0.U
			io.mac_out(j) := Engine(j).out
		}

	//and a counter that determins if the min engine fires
	// for 1x1 only on first 3 cycles ()
	//then next 6
	//after that skip 3 cycles
	//then 
		//ctrl logic	
		when(Start)
		{

			for (j <- 0 until KUints)
			{
				//kern memory acesss

				for (i <- 0 until Conv)
				{
					Engine(j).b(i) := io.k_data(j)(i)
				}

				Engine(j).enable := 1.U//enable on
			}		
		}
		//kernelDONE Phase
		when((KDDelay)&(!pastKD))
		{
			for (j <- 0 until KUints)
			{
					
				Engine(j).r:=1.U//how to do chisel resets?
				Engine(j).enable := 0.U
			}
		}
		//winDONE Phase
		when((WDDelay)&(!pastWD))
		{
			for (j <- 0 until KUints)
			{
				Engine(j).r:=1.U
				
			}
			
		}
	when(Start)
	{
		io.valid:=io.WD&(!pastKD.toBool)
	}
	.otherwise
	{
		io.valid:=0.U

	}



}

