package thecode
import chisel3._
import chisel3.util._
import chisel3.iotesters
import chisel3.iotesters.{ChiselFlatSpec, Driver, PeekPokeTester}
import Chisel.iotesters.{SteppedHWIOTester, ChiselFlatSpec}
import scala.language.reflectiveCalls


class poolE (val Conv : Int ) extends Module 
{
	val io = IO(new Bundle {
		
		val input_data  	= Input(Vec(Conv,UInt(8.W)))//testbench drives
		val KD = Input(Bool())//mac control
		val WD = Input(Bool())//mac control
		
		val convState  	= Input(Bool())//mac control

		val mac_out 		= Output(UInt(8.W))//mac data
		val valid   	= Output(Bool())
		val MACSTART   	= Output(Bool())
})


		//Mac engine
		val Engine = { Module(new POOLUNIT(Conv)).io }

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

		Engine.a <> io.input_data		
		Engine.r := false.B
			
		

		delay:=io.convState
		pastKD:=io.KD
		pastWD:=io.WD
		WDDelay:=pastWD
		KDDelay:=pastKD
		Start:=delay
			io.MACSTART:=Start

		Engine.enable := 0.U
		io.mac_out := Engine.out
		

	//and a counter that determins if the min engine fires
	// for 1x1 only on first 3 cycles ()
	//then next 6
	//after that skip 3 cycles
	//then 
		//ctrl logic	
		when(Start)
		{
		Engine.enable := 1.U//enable on			
		}
		//kernelDONE Phase
	when((KDDelay)&(!pastKD))
	{

					
		Engine.r:=1.U//how to do chisel resets?
		Engine.enable := 0.U
		
	}
		//winDONE Phase
	when((WDDelay)&(!pastWD))
	{
			Engine.r:=1.U
	}
	when(Start)
	{
		io.valid:=(WDDelay)&(!pastWD)^KDDelay
	}
	.otherwise
	{
		io.valid:=0.U

	}

}

