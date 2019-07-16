// See LICENSE.txt for license details.
package thecode

import chisel3._
import chisel3.util._
import chisel3.iotesters.{PeekPokeTester, Driver}


class InputsourceTests(c: Inputsource) extends PeekPokeTester(c)
{
		step(7*256)
}


