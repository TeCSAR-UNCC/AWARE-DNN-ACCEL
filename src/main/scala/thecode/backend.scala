package thecode
import chisel3._
import chisel3.util._
import chisel3.iotesters
import chisel3.iotesters.{ChiselFlatSpec, Driver, PeekPokeTester}
import Chisel.iotesters.{SteppedHWIOTester, ChiselFlatSpec}
import scala.language.reflectiveCalls


class backend(val Out_size: Int, val poolOut: String, val chan: Int) extends Module 
{
  	val io = IO(new Bundle 		
	{
	val input  = Input(UInt(8.W))
    	val output	= Output(SInt(8.W))
    	val input_valid = Input(UInt(1.W))
    	val output_valid = Output(UInt(1.W)) //ready
  	})

  	//val reluin = RegInit(1.S(8.W))			//connects input to ReLU i/p
	val reluin =RegInit(1.S(8.W))
	//val reluout =RegInit(Vec(Seq.fill(Channels)(1.S(8.W))))
  	val reluout = RegInit(1.S(8.W))	
  	val relu = Module(new ReLU()) 
	val pool_in_valid= RegInit(1.U(1.W))
	val valid_Delay= RegInit(1.U(1.W))
	reluin := io.input.asSInt
	relu.io.ReLU_input := reluin	
	
	reluout := relu.io.ReLU_output
	

	if (poolOut == "pool") 
	{
		val pool_out = RegInit(1.S(8.W))		//connects this out to pooling out
	  	val pool_out_valid= RegInit(1.U(1.W))	//connects this o/p valid to pooling o/p valid
	  		//connects this i/p valid to pooling i/p valid
		val pool = Module(new MaxPool(Out_size,chan))
	
		pool_in_valid := io.input_valid	//input valid connections
		pool.io.valid_in := pool_in_valid//input valid connections
		//propagate through input valid register
	
		pool.io.input := reluout.asUInt
		pool_out:= pool.io.output.asSInt 
		io.output := pool_out	


		pool_out_valid := pool.io.valid_out  //top o/p to pooling o/p
		io.output_valid := pool_out_valid
		}
	else 
	{
		io.output:= reluout
		pool_in_valid := io.input_valid	
		valid_Delay:=pool_in_valid
		io.output_valid := valid_Delay
		
	}

		



}
