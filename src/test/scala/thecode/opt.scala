package thecode
 
import chisel3._
import chisel3.util._
import chisel3.iotesters
import chisel3.iotesters.{ChiselFlatSpec, Driver, PeekPokeTester}
import Chisel.iotesters.{SteppedHWIOTester, ChiselFlatSpec}
import scala.language.reflectiveCalls
import scala.sys.process._
 
// test:runMain thecode.Launcher layer --backend-name verilator

/*
object generator {

  def main(args: Array[String]): Unit = {
    TutorialRunner("thecode", tests, args)
  }
}
*/
object opt extends App 
{

val p=  new netParam()
val mainDIR= Process("pwd").!!



var startCMDs = Seq[String]()
startCMDs=Seq("All-Save.py","MAC-KEEP.py","cleaner.py","weight-KEEP.py","Save-mem.py")





var dspMACs = Seq[String]()
var poolBrams = Seq[String]()
var sychBrams = Seq[String]()
var BuffBrams = Seq[String]()
var WeightBrams = Seq[String]()
dspMACs=Seq("MAC-DSP.py","none.py")
poolBrams=Seq("POOL-BRAM.py","POOL-LUTS.py")
sychBrams=Seq("SYCH-BRAM.py","SYCH-LUTS.py")
BuffBrams=Seq("BUFFER-BRAM.py","BUFFER-LUTS.py")
WeightBrams=Seq("WEIGHTS-BRAM.py","WEIGHTS-LUTS.py")


	var outputs = Seq[String]()
	var path =""
	var program = Seq[String]()
	if (p.connected == 1)
	{

	path = mainDIR.dropRight(1)+"/src/main/resources/"
	

	}
	else
	{
	  path = mainDIR.dropRight(1)+"/verilog/"

	}
	 
	

for (j <- 0 until p.numlayers )  
{  		

	for (i <- 0 until startCMDs.length)  
	{ 

	val  script = mainDIR.dropRight(1)+"/python/"+startCMDs(i)+" "+path+"l_"+(j).toString+".v"

	//println(program)
	Process("python "+script).!!

	}
}

for (j <- 0 until p.numlayers )  
{  		


	val WICMDS = mainDIR.dropRight(1)+"/python/WEIGHT_INITIAL.py "+path+"l_"+(j).toString+".v"

	//println(program)
	Process("python "+WICMDS).!!

	
}


for (j <- 0 until p.numlayers )  
{  		


	val WI2CMDS = mainDIR.dropRight(1)+"/python/WI2.py "+path+"l_"+(j).toString+".v"

	//println(program)
	Process("python "+WI2CMDS).!!

	
}



for (j <- 0 until p.numlayers )  
{  		
 program =program:+ (mainDIR.dropRight(1)+"/python/"+dspMACs(p.dspMACs(j))+" "+path+"l_"+(j).toString+".v ")
 program =program:+ (mainDIR.dropRight(1)+"/python/"+poolBrams(p.poolBrams(j))+" "+path+"l_"+(j).toString+".v ")
 program =program:+ (mainDIR.dropRight(1)+"/python/"+sychBrams(p.sychBrams(j))+" "+path+"l_"+(j).toString+".v ")
 program =program:+ (mainDIR.dropRight(1)+"/python/"+BuffBrams(p.BuffBrams(j))+" "+path+"l_"+(j).toString+".v ")
 program =program:+ (mainDIR.dropRight(1)+"/python/"+WeightBrams(p.WeightBrams(j))+" "+path+"l_"+(j).toString+".v ")	
}


for (i <- 0 until program.length)  
{  			
	//println(program(i))
	outputs = outputs:+ (("python " +program(i))).!! // Captures the output
	//println(outputs(i))

}
 
 


}

	
