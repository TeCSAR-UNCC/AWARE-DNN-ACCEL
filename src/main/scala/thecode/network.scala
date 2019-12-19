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
	val DONE   		= Output(Vec(p.numlayers,UInt(1.W)))//make multi dim
	//val chanptr             = Output(UInt(10.W))
})



//set ingen
val ingen = Module(new ingen(p.waitTime,(p.ChanPar(0)),8 ) )


//val rowSize: Int,val filterSize: Int, val stride: Int, val Out_size: Int,val KernNum : Int,val KUints : Int,val ChanPar : Int,val ChanBuffer : Int,val poolOut: String, val delay : Int,val GROUPS :Int,val Shuffle :Int,val pipeline :Int,val Conv :Int, val poolint :Int,val FcDiv :Int,val layerNum :Int,val ROM:Int,val AVG :Int

	val l = for (i <- 0 until p.numlayers) yield 
	{  							//to create multiple layers
	       val templayer = Module(new BBlayer(		//
		p.rowsize(i),p.filterSize(i),p.stride(i),p.Out_size(i),p.KernNum(i),p.KUints(i),p.ChanPar(i),p.ChanBuffer(i),
		//pool ,  testing    ,   delay    ,  GROUPS   ,  Shuffle   ,  pipeline   ,  Conv   ,   poolINT   ,FcDiv ,layernum
		"nopool",p.delay(i),p.Group(i),1,p.pipeline(0), p.Conv(i), p.poolint(i),p.FCDIV(i),(i),p.ROM(i),p.AVG(i)) )
	  
	       templayer
	}				

	//l(0).io.clock:=clock
	//l(0).io.reset:=reset
	l(0).io.write_enable:=ingen.io.valid_Out
	l(0).io.write_data:=ingen.io.Data_out
	for( i <- 0 until p.numlayers-1) 
	{  					
		l(i+1).io.write_data:=l(i).io.output
		l(i+1).io.write_enable:=l(i).io.valid_out
		io.DONE(i):=l(i).io.DONE
		//l(i+1).io.clock:=clock
		//l(i+1).io.reset:=reset					
	}

	io.output:=l(p.numlayers-1).io.output	
	io.valid:=l(p.numlayers-1).io.valid_out
	io.DONE(p.numlayers-1):=l(p.numlayers-1).io.DONE

	

}


