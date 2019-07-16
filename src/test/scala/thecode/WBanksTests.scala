package thecode
import chisel3.iotesters.{PeekPokeTester, Driver}
import chisel3._
import chisel3.util._

class WBanksTests(c: WBanks) extends PeekPokeTester(c)
{
		//val channel_args = Seq(3,10,16)

	      //  val dataIn =Seq.fill(c.filterSize+c.stride)(0)
	step(4)
	poke(c.io.Valid_IN,1.U)
	poke(c.io.PTR,1.U)
	step(1)
	poke(c.io.Valid_IN,0.U)
	poke(c.io.PTR,9.U)
	step(1)
	poke(c.io.Valid_IN,1.U)
	poke(c.io.PTR,2.U)
	step(1)
	poke(c.io.PTR,3.U)
	step(1)
	poke(c.io.PTR,4.U)
	step(1)
	poke(c.io.PTR,5.U)
	step(1)
	poke(c.io.Valid_IN,0.U)
	step(5)
	}
