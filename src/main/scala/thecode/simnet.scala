
package thecode
import chisel3._
import chisel3.util._
import chisel3.iotesters
import chisel3.iotesters.{ChiselFlatSpec, Driver, PeekPokeTester}
import Chisel.iotesters.{SteppedHWIOTester, ChiselFlatSpec}
import scala.language.reflectiveCalls
import chisel3.experimental.withClock

class simnet () extends Module 
{
	//val clock = Input(Clock())
	//val reset = Input(Bool())
	val p=  new netParam()

	val io = IO(new Bundle {


	//set output
	//set valid
	val output 		= Output(UInt(((p.KUints(p.numlayers-1))*8).W))//mac data
	val valid   		= Output(UInt(((p.KUints(p.numlayers-1))*1).W))//make multi dim
	val DONE   		= Output(Vec(p.numlayers,UInt(1.W)))//make multi dim
	//val chanptr             = Output(UInt(10.W))
})


	//val clockgen = Module(new ingen(5,(1),8 ) )
	val cDiv = RegInit(true.B) // start with falling edge to simplify clock relationship assert
	cDiv := !cDiv//250
	val clock1 = (cDiv).asClock

	withClock(clock1)
	{
		val cDiv2 = RegInit(true.B)
		cDiv2 := !cDiv2
		val clockT = (cDiv2).asClock

		withClock(clockT)
		{
		val net = Module(new network())
		io.output:=net.io.output	
		io.valid:=net.io.valid
		for( i <- 0 until p.numlayers) 
		{ 
			io.DONE:=net.io.DONE
		}
		}


	}//125
	





	val testDiv = RegInit(true.B)


	testDiv := !testDiv//125
//	val clockT = (clockgen.io.valid_Out.toBool).asClock
	
 
}




