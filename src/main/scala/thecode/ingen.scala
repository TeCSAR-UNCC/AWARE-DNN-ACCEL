package thecode
import chisel3._
import chisel3.util._
import chisel3.iotesters
import chisel3.iotesters.{ChiselFlatSpec, Driver, PeekPokeTester}
import Chisel.iotesters.{SteppedHWIOTester, ChiselFlatSpec}
import scala.language.reflectiveCalls



class ingen (val WaitTime: Int,val ChanPar: Int,val BW: Int) extends Module 
{
	val io = IO(new Bundle {
		val Data_out = Output(UInt((ChanPar*BW).W))
		val valid_Out=Output(UInt(ChanPar.W))
})


		val Delay = RegInit(0.U(log2Ceil(WaitTime+1).W))//internal state reg
		val limitREG =RegInit(1.U(1.W))
		val ValidREG =RegInit(1.U(1.W))
		val Sum = RegInit(0.U((BW+1).W))//internal state reg
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
//output

		val Out_vec = Wire(Vec(ChanPar,UInt((BW).W)))
		val Valid_vec = Wire(Vec(ChanPar,Bool()))

		for( i <- 0 until ChanPar) 
		{
			Out_vec(i):=Sum+i.asUInt
			Valid_vec(i):=ValidREG

		}

		io.Data_out:=Out_vec.asUInt

		io.valid_Out:=Valid_vec.asUInt
		
			
}			

