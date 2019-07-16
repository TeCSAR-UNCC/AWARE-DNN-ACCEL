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
object ooc extends App 
{

val numlayer=22
var layername = Seq[String]()
var filename = Seq[String]()
var script = Seq[String]()

for (j <- 0 until numlayer )  
{  		
 layername =layername:+ ("l_"+(j).toString)

 filename =filename:+ (layername(j)+".v ")
	
}

//is projecrt name and director a factor in the naming scheme
//alwas have a top-director and make the project name
// do this after adding the top layer 
val proName= "SMnet"
val proDIR="/home/justin/Desktop/chisel-v3/journel/SMnet/"
val VSRC = "/home/justin/Desktop/chisel-v3/journel/SM-src/"
val newSRC =proDIR+proName+".srcs/"

for (j <- 0 until numlayer )  
{  		

	script =script:+ ("import_files -norecurse "+VSRC+filename(j))

	script =script:+ ("update_compile_order -fileset sources_1")
	script =script:+ ("create_fileset -blockset -define_from "+layername(j)+" "+layername(j))
	script=script:+ ("file mkdir "+proDIR+proName+".srcs/"+layername(j)+"/new")

script=script:+ ("close [ open "+proDIR+proName+".srcs/"+layername(j)+"/new/"+layername(j)+"_ooc.xdc w ]")


script=script:+ ("add_files -fileset "+layername(j)+" "+newSRC+layername(j)+"/new/"+layername(j)+"_ooc.xdc")
script=script:+ ("set data {# nothing}")

script=script:+ ("set filename \""+newSRC+layername(j)+"/new/"+layername(j)+"_ooc.xdc\"")
script=script:+ ("set fileId [open $filename \"w\"]")
script=script:+ ("puts -nonewline $fileId $data")
script=script:+ ("close $fileId")
script=script:+ ("set_property USED_IN {out_of_context synthesis implementation}  [get_files  "+newSRC+layername(j)+"/new/"+layername(j)+"_ooc.xdc]")
	script =script:+ ("\n")
script =script:+ ("\n")
}






	for (i <- 0 until script.length )  
	{  		
	 println(script(i))
	}
	







}
