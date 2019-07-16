// See LICENSE.txt for license details.
package thecode

import chisel3._
import chisel3.util._
import chisel3.iotesters.{PeekPokeTester, Driver}

// import Counter._

// Problem:
//
// Counter should be incremented by the 'amt'
// every clock if 'en' is asserted
//This is free running counter.
//This counter counts  the specified number of clock cylcles as mentioned in limit
//After the limit is reached the overflow signal of the counter will go low for two clock cylces.
//During this period valid signal is deasserted by the counter and  the counter count is not valid
//After the two wait states where valid and overflow are low the counter will roll back and start counting again.
class counterout(limit : Int) extends Bundle 
{
  val valid       = Output(Bool())
  val r      = Input(Bool())
  val Overflow  = Output(Bool())
  val out = Output(UInt(log2Ceil(limit+1).W))

   override def cloneType: this.type = counterout(limit).asInstanceOf[this.type]
   
}
  object counterout {
    def apply(limit: Int): counterout = new counterout(limit)
    }


class jcounter(limit : Int) extends Module 
{

  	val io = IO(new Bundle 
	{
		
    	val count = Input(Bool())
	val output= new counterout(limit) 
		
  	})

if (limit != 1)
{
	val newlimit=limit-1

	val x = RegInit(0.U(log2Ceil(limit+2).W))//internal state reg
	val limitREG =RegInit(1.U(1.W))
	val   valid=RegInit(1.U(1.W))
	when (io.count) 
	{ 	
		valid:=1.U
		limitREG:= 1.U
		when (x < ((newlimit).asUInt(log2Ceil(limit+2).W))) 
		{ 
			
			x:= x + 1.U
			limitREG :=1.U
			valid:=1.U	
		}
		when (x === ((newlimit-1).asUInt(log2Ceil(limit+2).W)))
		{
			//the reset signal is an input signal
			limitREG:= 0.U// is this synchronized?NO its a race condition, decoupled helps prevent race conditions
			valid:=0.U	
					
		}

		when (x === ((newlimit).asUInt(log2Ceil(limit+2).W)))
		{
 			x :=0.U
					
		}		
			
	}

	.otherwise
	{	
		valid:=0.U
	}	
		io.output.out := x
		io.output.Overflow:= !limitREG
		io.output.valid:=valid
	when(io.output.r)
	{
		x :=0.U
		valid:=0.U
		limitREG:= 1.U
	}	
}
else
{
		io.output.out := 1.U
		io.output.Overflow:= 1.U
		io.output.valid:=1.U


}



}
object jcounter
{

	 def apply(count: Bool,r: Bool , limit : Int) = 
	{

		
	    val Thecounter = Module(new jcounter(limit))
	    Thecounter.io.count := count
		Thecounter.io.output.r:=r

	    Thecounter.io.output
	  }

}

