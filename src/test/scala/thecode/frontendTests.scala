package thecode
import chisel3.iotesters.{PeekPokeTester, Driver}
import chisel3._
import chisel3.util._

class frontendTests(c: frontend) extends PeekPokeTester(c)
{
		//val channel_args = Seq(3,10,16)

	      //  val dataIn =Seq.fill(c.filterSize+c.stride)(0)

	for(k <- 0 until c.outConv)//sub-FM-regions
	{


			for(chan <- 0 until c.filterSize)//input filtersize
			{

				for(i <- 0 until 3)//input 3 channels
				{
					for(j <- 0 until c.rowSize)//input 1 row
					{


						for(chan <- 0 until c.ChanPar)
						{
							poke(c.io.write_enable(chan),true.B)
							poke(c.io.write_data(chan),rnd.nextInt(5).asUInt.asUInt)
						}
						step(1)
						for(chan <- 0 until c.ChanPar)
						{
							poke(c.io.write_enable(chan),false.B)
						}
						step(150)
					}
				}

		}
		

	}


}
