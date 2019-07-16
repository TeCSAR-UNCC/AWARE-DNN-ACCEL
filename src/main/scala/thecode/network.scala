package thecode
import chisel3._
import chisel3.util._
import chisel3.iotesters
import chisel3.iotesters.{ChiselFlatSpec, Driver, PeekPokeTester}
import Chisel.iotesters.{SteppedHWIOTester, ChiselFlatSpec}
import scala.language.reflectiveCalls


class network () extends Module 
{
	//val clock = Input(Clock())
	//val reset = Input(Bool())
	val p=  new netParam()

	val io = IO(new Bundle {


	//set output
	//set valid
	val output 		= Output(UInt(((p.KUints(p.numlayers-1))*8).W))//mac data
	val valid   		= Output(UInt(((p.KUints(p.numlayers-1))*1).W))//make multi dim
	//val chanptr             = Output(UInt(10.W))
})



//set ingen
val ingen = Module(new ingen(p.waitTime,(p.ChanPar(0)),8 ) )




	val l = for (i <- 0 until p.numlayers) yield 
	{  							//to create multiple layers
	       val templayer = Module(new lBB(p.KUints(i),p.ChanPar(i),8,(i)))
	  
	       templayer
	}				

	l(0).io.clock:=clock
	l(0).io.reset:=reset
	l(0).io.io_write_enable:=ingen.io.valid_Out
	l(0).io.io_write_data:=ingen.io.Data_out
	for( i <- 0 until p.numlayers-1) 
	{  					
		l(i+1).io.io_write_data:=l(i).io.io_output
		l(i+1).io.io_write_enable:=l(i).io.io_valid_out
		l(i+1).io.clock:=clock
		l(i+1).io.reset:=reset					
	}

	io.output:=l(p.numlayers-1).io.io_output	
	io.valid:=l(p.numlayers-1).io.io_valid_out

	

}


