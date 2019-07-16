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



val numlayers=22
val connected=1
val sys=1
val waitmult=1

val waitTime =66


val rowsize=   Seq(224,224,112,112,56,56,56,56,56,28,28,28,28,28,14,14,14,14,14,14,14,14)
val filterSize=Seq(3,2,3,2,1,3,1,3,2,1,2,1,3,2,1,3,1,3,1,1,1,1)
val stride=    Seq(1,2,1,2,1,1,1,1,2,1,1,1,1,2,1,1,1,1,1,1,1,1)
val Out_size= Seq(224,112,112,56,56,56,56,56,28,28,28,28,28,14,14,14,14,14,14,14,1,1)
val KernNum= Seq(4,16,4,32,16,32,16,32,128,32,64,32,64,256,32,128,64,128,64,200,1000,1000)
val KUints= Seq(4,1,8,1,1,4,1,4,1,1,4,1,4,1,2,4,1,4,2,5,1,1)
val ChanPar= Seq(3,4,1,8,1,1,4,1,4,1,1,4,1,4,1,2,4,1,4,2,5,1)
val ChanBuffer= Seq(1,4,16,4,32,16,32,16,32,128,32,64,32,64,256,32,128,64,128,64,200,1000)
val delay= Seq(1142,1142,440,1202,4137,190,15119,70,674,7650,511,18929,194,794,6683,4090,42133,1161,4221,33,184,3)
val pipeline= Seq(1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1)
val Conv= Seq(1,1,1,1,1,3,1,3,1,1,1,1,3,1,1,3,1,3,1,1,1,1)
val Group= Seq(1,16,1,32,1,1,1,1,128,1,1,1,1,256,1,1,1,1,1,1,1000,1000)
val shuff= Seq(1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1)
val WeightInitial= Seq(36,16,576,16,512,4608,512,4608,128,4096,8192,2048,18432,256,8192,36864,8192,73728,8192,12800,200,1000)
val WaitALL= Seq(66,24,45,179,90,106,58,99,52,100,401,201,233,117,231,733,534,880,441,203,65,185)

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

//early DElay set to luts
for( i <- 0 until 10)
{ 

	sychBrams=sychBrams.updated((i),1)//luts
}
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
sysoff=sysoff.updated((12),true)
sysoff=sysoff.updated((15),true)
sysoff=sysoff.updated((17),true)
sysoff=sysoff.updated((19),true)
//WeightBrams=WeightBrams.updated((19),1), maybe small 3x3's are suitable?

//early 1x1 laters
WeightBrams=WeightBrams.updated((0),1)//LUT Weights
WeightBrams=WeightBrams.updated((2),1)//LUT Weights
ROM=ROM.updated((0),1)//LUT Weights
ROM=ROM.updated((2),1)//LUT Weights

WeightBrams=WeightBrams.updated((4),1)//LUT Weights
ROM=ROM.updated((4),1)//LUT Weights

WeightBrams=WeightBrams.updated((6),1)//LUT Weights
ROM=ROM.updated((6),1)//LUT Weights


//pooling layers
poolint=poolint.updated((1),1)
poolint=poolint.updated((3),1)
poolint=poolint.updated((8),1)
poolint=poolint.updated((13),1)

//avg layers
FCDIV=FCDIV.updated((20),(KernNum(20)))
FCDIV=FCDIV.updated((21),(KernNum(21)))
AVG=AVG.updated((0),0)

}
