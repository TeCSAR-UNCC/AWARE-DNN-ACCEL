//all the package and import lines need to be included
package thecode
import chisel3._
import chisel3.util._
import chisel3.experimental._ // To enable experimental features
import chisel3.iotesters.{PeekPokeTester, Driver}
import chisel3.iotesters.{ChiselFlatSpec, Driver, PeekPokeTester}
import Chisel.iotesters.{SteppedHWIOTester, ChiselFlatSpec}
import chisel3.iotesters.{Driver, TesterOptionsManager}
import scala.language.reflectiveCalls
import scala.collection.mutable.ArrayBuffer

//encapsulates the layer so it is easier to blackbox

class lBB (val KernPar : Int, val ChanPar : Int, val BW : Int, val layerNum : Int) extends BlackBox with HasBlackBoxResource
{

val io = IO(new Bundle {
val clock = Input(Clock())
val reset = Input(Bool())
val io_output= Output(UInt((BW*KernPar).W))
val io_valid_out= Output(UInt((KernPar).W))
val io_write_enable= Input(UInt((ChanPar).W))
val io_write_data= Input(UInt((BW*ChanPar).W))
})

 val lnum= "l_"+layerNum.toString

override def desiredName = lnum


setResource("/"+lnum+".v")

}
