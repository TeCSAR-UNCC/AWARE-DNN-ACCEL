/*
Buffer module

Function: This code is the module that relizes all the 2D buffers.
It is paramatizable enough to create a tensor buffer.
Tensor buffers are simply 2D buffers that also allocat for the dimension
of channels.

Authors: Justin Sanchez 
Editor: Adarsh Sawant
last changed date:10/26/18 
Current Version:1.0

V1-Changelog:
Currently in the process of verfication and commenting out for future use.

Notes to current editor/author:

hey this is partially comented out, but i understand it may not help, so if at any part you feel a comment is lacking
just add a line like:

//Adarsh:What doese this register function as
I will do the same as well and in the final version we can delete these.
*/

//the tensor buffer has an output delay of 1 cycyle from the start of conv, so conv start is signaled 1 cycle later you get data
//furthermore the first diit is displayed 1 cycle longer than nesesary longer than nessasary
//there are two garbage cycles at the end, so you stop reading the buffer early
//number of cycles total: Filtersize^2*convwindow


//two garbage cycles from conv start
//this does not apply in the middle betwen channels
//two garbage cycles at the end
//window done is signaled every time it changes

package thecode
import chisel3._
import chisel3.util._
import chisel3.iotesters
import chisel3.iotesters.{ChiselFlatSpec, Driver, PeekPokeTester}
import Chisel.iotesters.{SteppedHWIOTester, ChiselFlatSpec}
import scala.language.reflectiveCalls

//test:run-main thecode.Launcher buffer --backend-name verilator

//kernel number respons to how many times we must loop around this buffer. The more kern paralism the less loops
//b channels is the number of buffer channels in a single tensor-buffer. mor chan paralism less buffered channels
//the rest is standard
//window done is triggered 2 cycles early
//row counter is wo cycles early
//clumn pointer is two cyces early
//assicly all the control logic is two cycles behind the actuall output


//declaring Io
class buffer (val rowSize: Int,val filterSize: Int, val stride: Int,  val outConv: Int,KernNum : Int ,val bchannels :Int,val GROUPS :Int,val Shuffle :Int,val Conv :Int ) extends Module 
{
	val io = IO(new Bundle {
		val CHGO = Input(Bool())
		val write_data  	= Input(UInt(8.W))
		val write_enable   	= Input(Bool())

		val convState  	= Output(Bool())//mac control
		val KernelNum = Output(UInt(log2Ceil(KernNum+1).W))//mac control
		val rowPtr = Output(UInt(log2Ceil(filterSize+1).W))
		val colPtr = Output(UInt(log2Ceil(filterSize+1).W))
		val ChannelPTR = Output(UInt(10.W))//mac control, what channel im on
		val KD = Output(Bool())//mac control, kernel travker
		val WD = Output(Bool())//mac control, sliding window tracker
		val read_data 		= Output( Vec(Conv,UInt(8.W)) )//make vec
		val DONE  	= Output(UInt(1.W))//mac control
		//val V_out_window  	= Output(UInt(20.W))//mac control
		//val V_out_channel  	= Output(UInt(20.W))//mac control
		//val V_out_kernel  	= Output(UInt(20.W))//mac control

		
})
//CHisel hints = is a declare, := as a wire
//declaring main componets and initial states
//label regs
			var diffVAr=1
			if(stride==1)
			{
			 diffVAr= rowSize-filterSize
			}
			else
			{
			diffVAr= rowSize-filterSize
			 //diffVAr= rowSize-(filterSize+stride)
			}
			if(filterSize==rowSize)
			{
			 diffVAr= 1
			}




	val DONEFM	= RegInit(0.U(1.W))
	//dontTouch(DONEFM)
	io.DONE:=DONEFM

	val dataOut = RegInit(Vec(Seq.fill(Conv)(0.U(8.W))))// output registers for tensor buffer

	val OutputValid	= RegInit(0.U(1.W))// valid  register so valid and output are the same cycle

	val addresize = (rowSize*(filterSize+stride)*bchannels)
	val mem = {Module(new memgen(addresize,Conv)).io} 
			
	val windowPtr = RegInit(0.U(8.W))
	val chanPtr = RegInit(0.U(10.W))
		io.ChannelPTR:=chanPtr	
	val convSTATE = RegInit(0.U(1.W))// make vector on bchannels

		
	val KD = RegInit(false.B)//kernel tracker

	val WD = RegInit(false.B)//window tracker
	val offset = RegInit(0.U(log2Ceil((filterSize+stride)).W))// column offset
	val coffset = RegInit(0.U(log2Ceil((bchannels+1)).W))// channel offest
	val currcoffset = RegInit(0.U( log2Ceil((bchannels+1)).W))//offset for reading current channel
	val Foffset = RegInit(0.U(log2Ceil((filterSize+stride)).W))//column offset for reading
	val rowCounter = RegInit(0.U(8.W))//for writing
	val currentrowCounter = RegInit(0.U(8.W))//for reading
	val InPtr =RegInit(0.U(log2Ceil((rowSize+1)).W))//writing ptr
	val Kcount= Wire(new counterout(KernNum))//internal kernel count
	Kcount:=Dcounter(KD.toBool,Kcount.Overflow,(KernNum))
	io.KernelNum:=Kcount.out
	io.KD:=KD.toBool
	
	io.convState:=convSTATE

	//the counters arent parametized for 1x1, so i added +1 to filter size, and it simply offseted
	mem.WE:=io.write_enable
	mem.RE:=convSTATE
	mem.Data_in:=io.write_data

		// the vector output of the memory is coming out

		
		val rowPtr = RegInit(0.U(log2Ceil((filterSize+1)).W))
		val rowPtrEN= Wire(Bool())
		val rowPtrLimit= Wire(Bool())
		rowPtrLimit :=false.B
		rowPtrEN :=false.B


		val colPtr = RegInit(0.U(log2Ceil((filterSize+1)).W))
		val colPtrEN= Wire(Bool())
		colPtrEN :=false.B
		val colPtrLimit= Wire(Bool())
		colPtrLimit :=false.B


		when(rowPtrEN===true.B)
		{
			rowPtr:=rowPtr+1.U			
		}

		when(rowPtr===(filterSize-1).asUInt)
		{
			rowPtr:=0.U
			rowPtrLimit:=true.B
			colPtrEN:=true.B			
		}


		when(colPtrEN===true.B)
		{
			colPtr:=colPtr+1.U			
		}
		when((colPtr===(filterSize-1).asUInt)&(rowPtrLimit))
		{
			colPtr:=0.U
			colPtrLimit :=true.B			
		}

		val CWINDOW= Wire(UInt(1.W))//internal window count 
		CWINDOW:=colPtrLimit&rowPtrLimit

		if(filterSize==1)
		{
			CWINDOW:=((colPtr===(filterSize-1).asUInt)&(rowPtrLimit))

		}
		else
		{
			CWINDOW:=((colPtr===(filterSize-1).asUInt)&(rowPtrLimit))

		}

		
		val Doneconv = RegInit(0.U(1.W))
		val WinDoneconv = RegInit(0.U(1.W))

		if(filterSize==1)
		{
			Doneconv:=((convSTATE.toBool)&(!(KD.toBool))).asUInt

		}
		else
		{
			Doneconv:=((colPtr===(filterSize-1).asUInt)&(rowPtrLimit))
		}


		
		WinDoneconv:=Doneconv
		io.rowPtr:=rowPtr
		io.colPtr:=colPtr
		io.WD:=WinDoneconv&convSTATE

val JUSTINREADPTR= RegInit(0.U(log2Ceil(rowSize+(filterSize+stride)*rowSize+(1024)*(filterSize+stride)*rowSize).W))
	if(Conv==1)
	{
	JUSTINREADPTR:= (windowPtr+colPtr+ (((Foffset+rowPtr)%(filterSize+stride).asUInt)*(rowSize).asUInt)+chanPtr*((rowSize)*(filterSize+stride)).asUInt  )

		if(filterSize==1)
		{
			JUSTINREADPTR:= ((windowPtr)+chanPtr*((rowSize)*(filterSize+stride)).asUInt  )

		}
		else
		{
			JUSTINREADPTR:= (windowPtr+colPtr+ (((Foffset+rowPtr)%(filterSize+stride).asUInt)*(rowSize).asUInt)+chanPtr*((rowSize)*(filterSize+stride)).asUInt  )

		}
	}
	else
	{
	JUSTINREADPTR:= (windowPtr+ (((Foffset+rowPtr)%(filterSize+stride).asUInt)*(rowSize).asUInt)+chanPtr*((rowSize)*(filterSize+stride)).asUInt  )

	}


mem.RPTR:=JUSTINREADPTR
/*
so the logic works like this the smallest granularity is the inner loop
-we check if windows of the buffer are done

*/
//convSTATE.toBool





//test logic

	val valid_count_Kernel = RegInit(0.U(20.W))
	val valid_count_Window = RegInit(0.U(20.W))
	//io.V_out_channel:=chanPtr
	//io.V_out_window:=valid_count_Window
	//io.V_out_kernel:=valid_count_Kernel

//test logic done



val JUSTINWRTITEPTR= RegInit(0.U(log2Ceil(rowSize+(filterSize+stride)*rowSize+(bchannels)*(filterSize+stride)*rowSize).W))

JUSTINWRTITEPTR:= (InPtr+(offset*rowSize.asUInt) + ( coffset*((filterSize+stride)*rowSize).asUInt ))
mem.WPTR:=JUSTINWRTITEPTR

when (io.write_enable)//when I have been given a valid write signal
{	
	currcoffset:=coffset//UPDATE CHANNEL OFFSET AT THE BEGINING OF EVERY CYCLE

		
	when (InPtr<(rowSize.asUInt(log2Ceil(rowSize+1).W))-1.U)//rowsize= #of cols
	{
		InPtr:=InPtr+1.U//increass element counter					
	}
	.otherwise//move channel, after you write a Single row
	{	
        // so the cycle, that you'r inptr= row (starting from zero) we set it back to zero, and.. 
		InPtr:=0.U
		when(coffset<((bchannels-1)).asUInt)//continiously check if channels are lower tan limit
		{
			coffset:=coffset+Shuffle.asUInt//if coffset is not the last then continue as always, with shuffle index, this chek usese alot of energy pro
		}
		.otherwise//if it is...
		{
			coffset:=0.U//next cycle reset
			offset:=offset+1.U//shift the rows for next cycle
			rowCounter:=rowCounter+1.U//add row after all channels
						
			when(offset!=(filterSize+stride).asUInt) //The mask is not at limit
			{
				offset:=offset+1.asUInt			
						
			}
			.otherwise//if it is start from the begining
			{
				offset:=0.U
			}	
		}
	}
						
					
}//WRITE LOOP

//compute begin loop/read starting loop
when ((rowCounter===(filterSize).asUInt)&(currentrowCounter!=rowCounter))//when the rows are 3 and its the first time
{
	when(currcoffset!=(bchannels).asUInt)
	{				
			KD:=0.U
			Foffset:=0.U
			currcoffset:=currcoffset+1.U			
	}
	.otherwise
	{			
		convSTATE:=1.U
		currentrowCounter:=rowCounter
		currcoffset:=0.U	
	}				
			
}//VERTICAL FIRST CONV
.otherwise//its not the first conv
{
	when (rowCounter>(filterSize).asUInt)//and row counter> filter (maybe unessary)
	{
		when ( (0.U===(rowCounter-filterSize.asUInt)%stride.asUInt)&(currentrowCounter!=rowCounter) )
		{		//check if a new tile based on stride is in
			when(currcoffset!=(bchannels).asUInt)
			{//if so start compute
				convSTATE:=1.U			
				KD:=0.U
				Foffset:=Foffset+stride.asUInt
				currcoffset:=currcoffset+1.U
			}

			.otherwise
			{
				currentrowCounter:=rowCounter
				currcoffset:=0.U
			}							
		}
	}
}//ALL OTHER CONV

//potentally extra control, ignore for now:
DONEFM:=0.U
	
when ((currentrowCounter===( rowSize ).asUInt))
{
	DONEFM:=1.U
	rowCounter:=0.U
	currentrowCounter:=0.U
	offset:=0.U
	Foffset:=0.U
	InPtr:=0.U

}//LAST ROW, and done with convolution CLEAR ALL



/*
val valid_count_Kernel = RegInit(0.U(20.W))
val valid_count_Channel = RegInit(0.U(20.W))
val valid_count_Window = RegInit(0.U(20.W))
*/


//readlogic

//test registers increment

		
		valid_count_Window:=windowPtr
		
			
		when(KD===true.B)
		{
			valid_count_Kernel:=valid_count_Kernel+1.U
		}

//test regiters increment DONE
//the hang up is in the last window and its relaton to the output row size
when(convSTATE.toBool&io.CHGO) //we are in a sending state, read never changes row state
{		
	KD:=0.U//kernel start
	rowPtrEN :=1.U// when convStart s on next cycle row pointer increments

	when(Kcount.out<=(KernNum-1).asUInt)// as long as we are still finishing the kernel
	{
		when(chanPtr<=(bchannels-1).asUInt)// and still in the channel
		{	




		//HERE:

			when(windowPtr<(diffVAr).asUInt)//you are trying to switch befor window ptr actuall reaches the targer
			{//this switch is defined very dynamically
				
				when(CWINDOW===1.U)
				{
					
					windowPtr:=windowPtr+(stride).asUInt
//window ptr wont increas for the last one

				}//COL AND ROW LOOP
			
			}//WINDOW LOOP
			.otherwise //when done with 2d line 	
			{	
				when(CWINDOW===1.U)
				{windowPtr:=0.U
				 chanPtr:=chanPtr+1.U+(KernNum%GROUPS).asUInt//TODO:check if at channel limit already then proced
				}
				
			}//DONE WITH WINDOW
		
		}//chanel loop
		.otherwise
		{	
			KD:=1.U//kernel done
			chanPtr:=0.U
			
		}
	
	}//kernel
	.otherwise
	{
		convSTATE:=0.U
		windowPtr:=0.U
		rowPtrEN :=0.U
		valid_count_Kernel:=0.U
	}		

}
	for(i <- 0 until Conv){io.read_data(i):=mem.Data_out(i)}

	

}



