package thecode

import chisel3.iotesters.{ChiselFlatSpec, Driver, PeekPokeTester}

class networkTests(c: network) extends PeekPokeTester(c)
{


step(100)

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

