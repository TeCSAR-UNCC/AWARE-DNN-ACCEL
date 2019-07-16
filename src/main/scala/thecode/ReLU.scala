// See LICENSE.txt for license details.
package thecode
import chisel3.iotesters.{PeekPokeTester, Driver}
import chisel3._
import chisel3.util._

class ReLU extends Module 
{
  	val io = IO(new Bundle 		
	{
		val ReLU_input  = Input(SInt(8.W))
    		val ReLU_output	= Output(SInt(8.W))
  	})



	when (io.ReLU_input < 0.S) 
	{
		io.ReLU_output := 1.S//did this bcause test data generated too many zeros, not a fair compparision
	}
	.otherwise
	{	
		io.ReLU_output := io.ReLU_input
	}
	

}

/*
class testbench(taker: ReLU) extends PeekPokeTester(taker)
{
	poke(taker.io.ReLU_input, 53)
	expect(taker.io.ReLU_output, 53)	
	step(1)

	poke(taker.io.ReLU_input, -80)
	expect(taker.io.ReLU_output,0)	
	step(1)

	poke(taker.io.ReLU_input, -128)
	expect(taker.io.ReLU_output,0)	
	step(1)

	poke(taker.io.ReLU_input, 127)
	expect(taker.io.ReLU_output,127)	
	step(1)

	poke(taker.io.ReLU_input, 0)
	expect(taker.io.ReLU_output,0)	
	step(1)
}

// can only declare one same object 

//test
object ReLU
{
	def main(args: Array[String]): Unit = {
	  if(!Driver(()=> new ReLU())(taker=>new testbench(taker))) System.exit(1)}

}
*/
