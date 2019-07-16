package thecode
 
import chisel3._
import chisel3.util._
import chisel3.iotesters
import chisel3.iotesters.{ChiselFlatSpec, Driver, PeekPokeTester}
import Chisel.iotesters.{SteppedHWIOTester, ChiselFlatSpec}
import scala.language.reflectiveCalls
import scala.sys.process._


//because this is in the test directory 
// test:runMain thecode.Launcher layer --backend-name verilator



//test:runMain thecode.generator
object generator extends App 
{

val p=  new netParam()

val  mystuff=  Array( Array("-tn","network","-td","verilog"))
val mainDIR= Process("pwd").!!
//p.numlayers
for (i <- 0 until p.numlayers)  
{  			


	//look into:
	//concurency
	//memory leak and class unloading
	//https://github.com/apache/incubator-mxnet/blob/master/scala-package/memory-management.md
	//https://blog.codecentric.de/en/2016/02/lazy-vals-scala-look-hood/
	//http://blog.dmitryleskov.com/programming/scala/stream-hygiene-i-avoiding-memory-leaks/
	
	if(p.sys==0)
	{		
		 chisel3.Driver.execute(p.mystuff(i), () => new BBlayer(

		//
		p.rowsize(i),p.filterSize(i),p.stride(i),p.Out_size(i),p.KernNum(i),p.KUints(i),p.ChanPar(i),p.ChanBuffer(i),
		//pool ,  testing    ,   delay    ,  GROUPS   ,  Shuffle   ,  pipeline   ,  Conv   ,   poolINT   ,FcDiv ,layernum
		"nopool",p.delay(i),p.Group(i),1,p.pipeline(0), p.Conv(i), p.poolint(i),p.FCDIV(i),(i),p.ROM(i),p.AVG(i)) )

	}



//val rowSize: Int,val filterSize: Int, val stride: Int, val Out_size: Int,val KernNum : Int,val KUints : Int,val ChanPar : Int,val ChanBuffer : Int,val poolOut: String, val delay : Int,val GROUPS :Int,val Shuffle :Int,val pipeline :Int,val Conv :Int, val poolint :Int,val FcDiv :Int,val layerNum :Int,val ROM:Int,val AVG :Int)
	else
	{


	 chisel3.Driver.execute(p.mystuff(i), () => new TestLayer(

	//
	p.rowsize(i),p.filterSize(i),p.stride(i),p.Out_size(i),p.KernNum(i),p.KUints(i),p.ChanPar(i),p.ChanBuffer(i),
	//pool ,  testing    ,   delay    ,  GROUPS   ,  Shuffle   ,  pipeline   ,  Conv   ,   poolINT   ,FcDiv ,layernum
	"nopool",p.delay(i),p.Group(i),1,p.pipeline(0), p.Conv(i), p.poolint(i),p.FCDIV(i), p.WaitALL(i), p.waitmult,p.sysoff(i),(i),p.ROM(i),p.AVG(i))
	
	)
	}


	var verSRC = mainDIR.dropRight(1)+"/verilog/l_"+(i).toString+".v"
	var verDEST = mainDIR.dropRight(1)+"/src/main/resources/"
	println(verDEST)
	Process("cp -f "+verSRC+" "+verDEST).!!

}




}

	
