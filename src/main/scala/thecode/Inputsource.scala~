package thecode
import chisel3._
import chisel3.util._
import chisel3.iotesters
import chisel3.iotesters.{ChiselFlatSpec, Driver, PeekPokeTester}
import Chisel.iotesters.{SteppedHWIOTester, ChiselFlatSpec}
import scala.language.reflectiveCalls



class Inputsource (val WaitTime: Int,val ChanPar: Int) extends Module 
{
	val io = IO(new Bundle {
		val Data_out = Output(Vec(ChanPar,UInt(8.W)))
		val valid_Out=Output(UInt(1.W))
})


		val Delay = RegInit(0.U(log2Ceil(WaitTime+1).W))//internal state reg
		val limitREG =RegInit(1.U(1.W))
		val ValidREG =RegInit(1.U(1.W))
		val Sum = RegInit(0.U(log2Ceil(256).W))//internal state reg
		when (Delay < ((WaitTime).asUInt(log2Ceil(WaitTime+1).W))) 
		{ 
			Delay:= Delay + 1.U
			limitREG :=0.U
				
		}
		when (Delay === ((WaitTime-1).asUInt(log2Ceil(WaitTime+1).W)))
		{
			limitREG:= 1.U
			
		}

		when (Delay === ((WaitTime).asUInt(log2Ceil(WaitTime+1).W)))
		{
 			Delay:=0.U
					
		}


		when (limitREG === 1.U)
		{
			limitREG :=0.U
 			Sum:=Sum+1.U
			ValidREG:=1.U		
		}
		.otherwise
		{
			ValidREG:=0.U	

		}

		for( i <- 0 until ChanPar) 
		{
			io.Data_out(i):=Sum+i.asUInt

		}
		io.valid_Out:=ValidREG
		
			
}			

