// See LICENSE.txt for license details.
package thecode

import chisel3._
import chisel3.util._
import chisel3.iotesters.{PeekPokeTester, Driver}


class DcounterTests(c: Dcounter) extends PeekPokeTester(c)
{
		poke(c.io.count, 1)
		//expect(c.io.output.out, 0)
		step(1)
		//expect(c.io.output.out, 1)
		poke(c.io.count, 1)
		step(1)
		//expect(c.io.output.out, 2)
		step(1)
		//expect(c.io.output.out, 3)
		step(1)
		//expect(c.io.output.out, 4)
		step(1)
		//expect(c.io.output.out, 5)
		step(1)
		//expect(c.io.output.out, 0)
		poke(c.io.count, 1)
		//expect(c.io.output.out, 0)
		step(1)
		//expect(c.io.output.out, 1)
		poke(c.io.count, 1)
		step(1)
		//expect(c.io.output.out, 2)
		step(1)
		//expect(c.io.output.out, 3)
		step(1)
		//expect(c.io.output.out, 4)
		step(1)
		//expect(c.io.output.out, 5)
}


