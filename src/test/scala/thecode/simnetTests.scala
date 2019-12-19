package thecode

import chisel3.iotesters.{ChiselFlatSpec, Driver, PeekPokeTester}

class simnetTests(c: simnet) extends PeekPokeTester(c)
{


step(1000000)//~2*224^2

/*
		poke(c.io.enable, 1)
		poke(c.io.a, 4)
		poke(c.io.b, 4)
		step(1)
		expect(c.io.out, 16)	

		poke(c.io.enable, 1)
		poke(c.io.a, 4)
		poke(c.io.b, 4)
		step(1)
		expect(c.io.out, 32)
		//expect(c.io.adder, 144)
*/

}

