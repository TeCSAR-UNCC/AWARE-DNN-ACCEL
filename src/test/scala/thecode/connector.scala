package thecode
 
import chisel3._
import chisel3.util._
import chisel3.iotesters
import chisel3.iotesters.{ChiselFlatSpec, Driver, PeekPokeTester}
import Chisel.iotesters.{SteppedHWIOTester, ChiselFlatSpec}
import scala.language.reflectiveCalls

 
// test:runMain thecode.Launcher layer --backend-name verilator

/*
object generator {

  def main(args: Array[String]): Unit = {
    TutorialRunner("thecode", tests, args)
  }
}
*/
object connector extends App 
{

val  NETargs=   Array("-tn","network","-td","verilog")



 chisel3.Driver.execute(NETargs, () => new network())

 
 


}

	
