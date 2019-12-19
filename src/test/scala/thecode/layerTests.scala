package thecode
import chisel3.iotesters.{PeekPokeTester, Driver}
import chisel3._
import chisel3.util._

class layerTests(c: layer) extends PeekPokeTester(c)
{
		//val channel_args = Seq(3,10,16)

	      //  val dataIn =Seq.fill(c.filterSize+c.stride)(0)
//val rowSize: Int,val filterSize: Int, val stride: Int, val Out_size: Int,val KernNum : Int,val KUints : Int,val ChanPar : Int,val ChanBuffer : Int,val poolOut: String)
val row0 = Seq(0,1,2,3,4,5,6,7,8)
val row1 = Seq(9,10,11,12,13,14,15,16,17)
val row2 = Seq(18,19,20,21,22,23,24,25,26)
val row3 = Seq(27,28,29,30,31,32,33,34,35)
val row4 = Seq(36,37,38,39,40,41,42,43,44)
val row5 = Seq(45,46,47,48,49,50,51,52,53)
val row6 = Seq(54,55,56,57,58,59,60,61,62)
val row7 = Seq(63,64,65,66,67,68,69,70,71)
val row8 = Seq(72,73,74,75,76,77,78,79,80)
val IMAGE = row0++row1++row2++row3++row4++row5++row6++row7++row8

	for(l <- 0 until (c.rowSize+2)*c.ChanBuffer)//input 3 channels
		
	{
		for(input <- 0 until c.rowSize)//input 3 channels
		{
			for(chan <- 0 until c.ChanPar)
			{
				poke(c.io.write_enable(chan),true.B)
				poke(c.io.write_data(chan),input.asUInt)
			}
			step(1)
		}
		for(chan <- 0 until c.ChanPar)
		{
			poke(c.io.write_enable(chan),false.B)
		}
	step(56)//1000 was old one
	}

//rnd.nextInt(5).asUInt.asUInt
	/*
	for(i <- 0 until c.ChanBuffer)//input 3 channels
	{
				
		for(input <- 0 until 56*56)//input 3 channels
		{
			for(chan <- 0 until c.ChanPar)
			{
				poke(c.io.write_enable(chan),true.B)
				poke(c.io.write_data(chan),input.asUInt)
			}
			step(1)
			for(chan <- 0 until c.ChanPar)
			{
				poke(c.io.write_enable(chan),false.B)
			}
			step(150)
		}			
	}

	
*/

}
