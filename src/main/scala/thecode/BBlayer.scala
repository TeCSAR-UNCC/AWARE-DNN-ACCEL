package thecode
import chisel3._
import chisel3.util._
import chisel3.iotesters
import chisel3.iotesters.{ChiselFlatSpec, Driver, PeekPokeTester}
import Chisel.iotesters.{SteppedHWIOTester, ChiselFlatSpec}
import scala.language.reflectiveCalls

//turn this into a function
//allow this to go both directions?
class convert(val vectorLenght: Int, val bitW : Int ) extends Module {
  val io = IO(new Bundle {
    val in = Input(Vec(vectorLenght,UInt(bitW.W)))

    val out = Output(UInt((vectorLenght*bitW).W))
  })


  io.out := io.in.asUInt
}

class MyBundle extends Bundle {
  val a = UInt(2.W)
  val b = UInt(4.W)
  val c = UInt(3.W)
}


class BBlayer (val rowSize: Int,val filterSize: Int, val stride: Int, val Out_size: Int,val KernNum : Int,val KUints : Int,val ChanPar : Int,val ChanBuffer : Int,val poolOut: String, val delay : Int,val GROUPS :Int,val Shuffle :Int,val pipeline :Int,val Conv :Int, val poolint :Int,val FcDiv :Int,val layerNum :Int,val ROM:Int,val AVG :Int) extends Module 
{

val io = IO(new Bundle {
		val write_enable   	= Input(UInt(ChanPar.W))//testbench drives
		val write_data  	= Input(UInt((ChanPar*8).W))//testbench drives
	    	val output = Output(UInt((KUints*8).W))     
    		val valid_out=Output(UInt(KUints.W))
		val DONE=Output(UInt(1.W))
})


val lnum= "l_"+layerNum.toString

override def desiredName = lnum



//TODO:make dynamic sych machine, make dynamic AVG layer (A1,A2?)

	val layer = Module(new layer
	( rowSize, filterSize, stride, Out_size, KernNum , KUints , ChanPar ,ChanBuffer ,poolOut ,  delay , GROUPS ,Shuffle , pipeline , Conv , poolint , FcDiv,ROM,AVG))

//val INe = Module(new convert (ChanPar,1   )).io 
//val INd = Module(new convert (ChanPar,8   )).io 
//val OUTe = Module(new convert (KUints,1   )).io 
//val OUTd = Module(new convert (KUints,8   )).io 

		val INe_b = Wire(Vec(ChanPar,Bool()))
		INe_b:=io.write_enable.toBools
		for(i <- 0 until ChanPar)
		{
			layer.io.write_enable(i):=INe_b(i)
		}
		

		
		//val INd_b =Wire(Vec((ChanPar),UInt(8.W))).fromBits(io.write_data.toBits)
		
		 val INd_b=io.write_data.asTypeOf( Vec(ChanPar,UInt(8.W)))
		 //val bools = VecInit()
		// layer.io.write_data:=io.write_data.toBits
		 var temp =0.U
		/*	
		for (j<- 0 until ChanPar)
		{
			for(i <- 0 until 8)
			{
			temp:=INd_b(i+j*8).asUInt
			}
			
			
		}
		//temp=null
		*/
		layer.io.write_data:=INd_b


	io.valid_out:=(layer.io.valid_out.asUInt)
	io.output:=(layer.io.output.asUInt)
	
	 io.DONE:=layer.io.DONE
/*

	val OUTe_b = Wire(Vec(ChanPar,Bool()))
		OUTe_b:=io.valid_out.toBools
		for(i <- 0 until ChanPar)
		{
			layer.io.valid_out(i):=OUTe_b(i)
		}
		

		//val INd_b = Wire(Vec(ChanPar,UInt(8.W)))
		val OUTd_b =Wire(Vec((KUints*8),UInt(1.W)))
		OUTd_b:=(io.output.toBools)
		for (j<- 0 until KUints)
		{
			for(i <- 0 until 8)
			{
				layer.io.output(j):=OUTd_b(i+j*8)
			}
		}

*/


/*



	INe.in:=layer.
	INd.in:=layer.io.write_data
	OUTe.in:=layer.io.valid_out
	OUTd.in:=layer.io.output
	io.write_enable:=INe.out



		//val INe_vec= VecInit((layer.io.write_enable.asUInt).toBools)
		//val INd_vec= VecInit((layer.io.write_data.asUInt).toBools)
		//val OUTe_vec=VecInit((layer.io.valid_out.asUInt).toBools)
		//val OUTd_vec=VecInit((layer.io.output.asUInt).toBools)

		//



		val INe = Wire(UInt((ChanPar).W))
		val INe_b = Wire(Vec(ChanPar,Bool()))
		INe:=INe_b.asUInt

		val INd = Wire(UInt((ChanPar*8).W))
		val OUTe = Wire(UInt((KUints).W))
		val OUTd = Wire(UInt((KUints*8).W))
		


		for(i <- 0 until ChanPar)
		{
			INe_b(i):=layer.io.write_enable(i)
			for(j <- 0 until 8)
			{
				
			}	
		}
		io.write_enable:=INe
		




INd:=layer.io.write_data.asUInt




		OUTe:=layer.io.valid_out.asUInt
		OUTd:=layer.io.output.asUInt

		
			//INe:=io.write_enable
			io.valid_out:=OUTe
			io.output:=OUTd
			INd:=io.write_data


	
	
		for( m <- 0 until KUints) 
		{

		
			
			for(n <- 0 until 8)
			{
				
			}	
			
		}
		
*/




}

