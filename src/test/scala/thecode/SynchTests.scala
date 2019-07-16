package thecode
import chisel3.iotesters.{PeekPokeTester, Driver}
import chisel3._
import chisel3.util._

class SynchTests(c: SyncMachine) extends PeekPokeTester(c)
{
	for(i <- 0 until 15)//input 3 channels
		{
			poke(c.io.write_enable,true.B)
			poke(c.io.write_data,(i+1).asUInt)	
			step(1)
	

		}

}
