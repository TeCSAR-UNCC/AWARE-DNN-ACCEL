package thecode
import scala.language.reflectiveCalls


class netParam ( ) 
{
//use a bash script to initilize thes variables


//rowsize,filtersize,stride
//outputsize, kernelNum,KUNITS
//channelPar, CHanBuffer
//2x2maxpool,testing,delay
//group,shuffle,pipeline
//convPar,tensorPool,FCDIV,layer num



val numlayers=4
val connected=1
val sys=1
val waitmult=1

//HERE
val rowsize=   Seq(11,9,7,3)
val filterSize=Seq(3,3,3,3)
val stride=    Seq(1,1,2,3)
val Out_size= Seq(9,7,3,1)
val KernNum= Seq(4,1,4,4)
val KUints= Seq(1,4,1,1)
val ChanPar= Seq(3,1,4,1)
val ChanBuffer= Seq(1,4,1,4)
val delay= Seq(3,3,3,3)
val pipeline= Seq(1,1,1,1)
val Conv= Seq(1,1,1,1)
val Group= Seq(3,1,1,1)
val shuff= Seq(1,1,1,1)
val WeightInitial= Seq(36,36,36,144)
val WaitALL= Seq(52,10,40,11)

val waitTime = WaitALL(0)
var sysoff= Seq[Boolean]()
var dspMACs = Seq[Int]()
//var on_off_chip = Seq[Int]()
var poolBrams = Seq[Int]()
var sychBrams = Seq[Int]()
var AVG = Seq[Int]()
var BuffBrams = Seq[Int]()
var WeightBrams = Seq[Int]()
var ROM = Seq[Int]()
var poolint = Seq[Int]()
var poolOut= Seq("nopool")


var FCDIV= Seq(1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1)
var mystuff = Array(Array("-tn","l_0","-td","verilog"))//only thing broken
for( i <- 1 until numlayers)
{ 
	mystuff= mystuff :+  Array("-tn","l_"+(i).toString,"-td","verilog")
	poolOut=poolOut:+"nopool"
}
for( i <- 0 until numlayers)
{ 
	poolint=poolint:+0
	AVG=AVG:+0
	sysoff=sysoff:+false
	//to be designed
	dspMACs=dspMACs:+0
	poolBrams=poolBrams:+0
	sychBrams=sychBrams:+0
	BuffBrams=BuffBrams:+0 //0= BRAMS, 1= LUTS
	ROM=ROM:+0
	WeightBrams=WeightBrams:+0
}

//early delay set to luts
//for( i <- 0 until 5)
//{ 

	//sychBrams=sychBrams.updated((i),1)//luts
//}
/*
for( i <- 0 until numlayers)
{

WeightBrams=WeightBrams.updated((1),1)//LUT Weights
ROM=ROM.updated((1),1)//LUT Weights

}
*/

//set defauls and include a matching progrma,export from csv

//set pool and avg layers
//set dw- distributed luts players

//system
//sysoff=sysoff.updated((8),true)
//sysoff=sysoff.updated((10),true)
//sysoff=sysoff.updated((12),true)
//sysoff=sysoff.updated((13),true)
//sysoff=sysoff.updated((14),true)
//poolint=poolint.updated((1),1)
//poolint=poolint.updated((3),1)
//poolint=poolint.updated((5),1)
//poolint=poolint.updated((7),1)
//poolint=poolint.updated((9),1)
//poolint=poolint.updated((11),1)



}
