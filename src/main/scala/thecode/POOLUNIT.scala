
package thecode
import chisel3.iotesters.{PeekPokeTester, Driver}
import chisel3._
//please! make enable a register
class POOLUNIT ( val Conv : Int)  extends Module 
{
    val io = IO(new Bundle {
    val enable = Input(Bool())
    val r = Input(Bool())
    val a = Input(Vec(Conv,UInt(8.W)))
    val out = Output(UInt(16.W))
    })

	val Accumulation =RegInit(0.U(16.W))
 	val Final_Accumulation = RegInit(0.U(16.W))
	val in1 =  Wire(UInt(8.W))
	val PartialAccum = RegInit(0.U(16.W))

	when(in1>PartialAccum)
	{
		PartialAccum:=in1
	}
	when (io.enable) 
	{
		in1:=io.a(0)
		when(PartialAccum<Accumulation)
		{
			Accumulation := PartialAccum
		}
		
	}
	.otherwise
	{	
			in1:=0.U  
	}
	when(io.r)
	{
		io.out :=Final_Accumulation
		Accumulation:=0.U
	}
	.otherwise
	{
		Final_Accumulation:=PartialAccum
		io.out := 0.U
	}
	
}


