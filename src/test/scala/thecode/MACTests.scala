package thecode

import chisel3.iotesters.{ChiselFlatSpec, Driver, PeekPokeTester}

class MACTests(c: MAC) extends PeekPokeTester(c)
{
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

