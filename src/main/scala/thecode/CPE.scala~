package thecode
import chisel3._
import chisel3.util._
import chisel3.iotesters
import chisel3.iotesters.{ChiselFlatSpec, Driver, PeekPokeTester}
import Chisel.iotesters.{SteppedHWIOTester, ChiselFlatSpec}
import scala.language.reflectiveCalls


class CPE (val rowSize: Int,val filterSize: Int, val stride: Int, val outConv: Int,val KernNum : Int,val KUints : Int,val ChanPar : Int,val ChanBuffer : Int,val GROUPS :Int,val Shuffle :Int, val pipeline :Int ,val Conv :Int,val pool :Int,val FcDiv :Int,val ROM :Int  ) extends Module 
{


		var newPar=0
		if(GROUPS==ChanBuffer*ChanPar)
		{
			newPar=1

		}
		else
		{
			newPar=ChanPar
			//in this case its the original value
		}



	val io = IO(new Bundle {
		val write_enable   	= Input(Vec(ChanPar,Bool()))//testbench drives
		val write_data  	= Input(Vec(ChanPar,UInt(8.W)))//testbench drives
		val mac_out 		= Output(Vec(newPar,Vec(KUints,UInt(8.W))))//mac data
		val Chan_Ptr  	= Output(UInt(10.W))//testbench drives
		val valid_out=Output(UInt(1.W))
})

		//make an array of memory with dataflow
		//val values = (1 to 7).toArray


		
if(pool==0)
{
		val ChanPTR= RegInit(0.U(10.W))
		val ChanPTRDelay= RegInit(0.U(10.W))
		val ChanPTRDelayFINAL= RegInit(0.U(10.W))
		val MultiBuffer = Array.fill(ChanPar){ Module(new buffer(rowSize,filterSize,stride,outConv,KernNum,(ChanBuffer/pipeline),GROUPS,Shuffle,Conv)).io }//channel pipelining tells the CPE to hold less channels
		val MACDELAY = Reg(Vec(ChanPar  ,Vec(KUints,UInt(8.W))) )//we explicitaly delay the mac, try moving this into the mac
		
		//make this dependent on depthwise
		
		val Mpattern = 0x1.U((ChanPar).W)//state register
		val active_CH_tile = RegInit(Vec(Mpattern.toBools))//for memory read
		
		val chSwitch =Wire(Bool())
		chSwitch:=0.U
		for (i <- 0 until ChanPar)
		{
			when( MultiBuffer(i).KD)
			{
				chSwitch:=1.U

			}
			MultiBuffer(i).CHGO:=active_CH_tile(i)
		}
			
		when( chSwitch)
		{	
			for (i <- 0 until ChanPar-1)
			{				
				active_CH_tile(i+1):=active_CH_tile(i)
			}
			active_CH_tile(0):=active_CH_tile(ChanPar-1)
			

		}
		var channelSize=0
		var chP=0
		if(GROUPS==ChanBuffer*ChanPar)
		{
			channelSize=1
			chP=1
			//if you have depthwise convolution and channel parlism, im just replicating the same channel over and over, with regular group conv, this becomes difficult
			//rathar than duplicating the memory you can also simply make more ports

		}
		else
		{

			channelSize=(ChanBuffer/GROUPS)
			chP=ChanPar
			//in this case its the original value
		}

		val buffIN =  Wire(Vec(Conv,UInt(8.W)))
		val default_IN =Vec(Seq.fill((Conv))(1.U(8.W)))
		buffIN:=default_IN
	
		//buffIN<>MultiBuffer(0).read_data	
		for(i <- 0 until ChanPar)//careful of multiple drivers
		{
			when(active_CH_tile(i))
			{
				buffIN:=MultiBuffer(i).read_data				
					
			}
			
		}

		val FchP=chP
		val FchannelSize=channelSize
	        val CORE = Array.fill(FchP){ Module(new macE(KUints,Conv)).io }
		channelSize=0
		val WBANKS= Module(new WBanks(filterSize,KernNum,KUints,ChanPar,FchannelSize,Conv,FcDiv,ROM)).io

//val filterSize: Int,val KernNum : Int,val KUints : Int,val ChanPar : Int,val ChanBuffer : Int,val testing : Int, val Conv :Int,val FcDiv :Int
//chan-par*chanbuff not nessary, need to re think Wbanks and channel parlism with respect to group


	//can be redone to simple counter
	val WeightPtr= RegInit(0.U( log2Ceil(  (filterSize*filterSize*KernNum*ChanBuffer+1) ).W )   )

	if(Conv==1)
	{
		WeightPtr := (MultiBuffer(0).rowPtr)+ (MultiBuffer(0).colPtr)*filterSize.asUInt+ (MultiBuffer(0).ChannelPTR)*(filterSize*filterSize).asUInt+(MultiBuffer(0).KernelNum)*(filterSize*filterSize*ChanBuffer).asUInt

	}
	else
	{
	WeightPtr := (MultiBuffer(0).rowPtr)+ (MultiBuffer(0).ChannelPTR)*(filterSize*filterSize).asUInt+(MultiBuffer(0).KernelNum)*(filterSize*filterSize*ChanBuffer).asUInt

	}

	val BufferValid = RegInit(false.B)
	 BufferValid := MultiBuffer(0).convState
	   
		WBANKS.PTR:=WeightPtr	
		WBANKS.Valid_IN:=BufferValid
		
		//validinit=Reg(Vec(Channels, UInt(1.W)))

		for(i <- 0 until ChanPar)
		{
			MultiBuffer(i).write_enable:=io.write_enable(i)// when apes output the must send a valid and a channel number
			MultiBuffer(i).write_data:=io.write_data(i)
		}

		//Each buffer is paired with each mac core (each mac core has KUnits number of macs in them)


	

		
		for (j <- 0 until FchP)
		{
			for (i <- 0 until KUints)
			{
			
				for (l <- 0 until Conv)
				{	//needs to be routed depending on active channel tile

					if(GROUPS==ChanBuffer*ChanPar)
					{
						CORE(j).input_data(l) := buffIN(l)

					}
					else
					{

						CORE(j).input_data(l):=MultiBuffer(j).read_data(l)
					}
					CORE(j).k_data(i)(l) := 0.U	
				}
				CORE(j).WD := MultiBuffer(0).WD
				CORE(j).KD := MultiBuffer(0).KD
				
				CORE(j).convState := MultiBuffer(0).convState 
				//io.mac_out(i+j*ChanPar):=0.U
				io.mac_out(j)(i):=0.U
			}
		}
			ChanPTR:=MultiBuffer(0).ChannelPTR//grab a channel ptr from one of the buffers
			//the following 2 lines delay the channel ptr by 2 cycles
			ChanPTRDelay:=ChanPTR
			ChanPTRDelayFINAL:=ChanPTRDelay
			io.Chan_Ptr:=ChanPTRDelayFINAL
 		 	//io.valid_out(j)(i)(k):= := (io.sel & io.in1) | (~io.sel & io.in0)
					
			for (j <- 0 until FchP)
			{
					when( (CORE(0).valid) ) 		
					{	io.valid_out:=1.U
						for (i <- 0 until KUints)
						{
						//io.mac_out(i+j*ChanPar):=CORE(j).mac_out(i)
							MACDELAY(j)(i):=CORE(j).mac_out(i)
						}
					}
					.otherwise
					{
						io.valid_out:=0.U
					}
				

			}  	


		when(CORE(0).MACSTART)//when one channel is ready so is another
		{
			for (i <- 0 until KUints)
			{			
				for (j <- 0 until FchP)
				{
					for (l <- 0 until Conv)
					{
						CORE(j).k_data(i)(l) := WBANKS.Data_out(l)(j)(i)
					}
				}
			}		
		}
		for (j <- 0 until FchP)
		{
	
			for (i <- 0 until KUints)
			{
		
					io.mac_out(j)(i):=MACDELAY(j)(i)
			}

		}

}
else
{
		val ChanPTR= RegInit(0.U(10.W))
		val ChanPTRDelay= RegInit(0.U(10.W))
		val ChanPTRDelayFINAL= RegInit(0.U(10.W))


				val MultiBuffer = Array.fill(ChanPar){ Module(new buffer(rowSize,filterSize,stride,outConv,KernNum,(ChanBuffer/pipeline),GROUPS,Shuffle,Conv)).io }


	var channelSize=0
		var chP=0
		if(GROUPS==ChanBuffer*ChanPar)
		{
			channelSize=ChanBuffer*ChanPar
			chP=1
			//if you have depthwise convolution and channel parlism, im just replicating the same channel over and over, with regular group conv, this becomes difficult
			//rathar than duplicating the memory you can also simply make more ports

		}
		else
		{

			channelSize=(ChanBuffer/GROUPS)
			chP=ChanPar
			//in this case its the original value
		}



		val FchP=chP
		val FchannelSize=channelSize


	
		val Mpattern = 0x1.U((ChanPar).W)//state register
		val active_CH_tile = RegInit(Vec(Mpattern.toBools))//for memory read
		
		val chSwitch =Wire(Bool())
		chSwitch:=0.U
		for (i <- 0 until ChanPar)
		{
			when( MultiBuffer(i).KD)
			{
				chSwitch:=1.U

			}
			MultiBuffer(i).CHGO:=active_CH_tile(i)
		}
			
		when( chSwitch)
		{	
			for (i <- 0 until ChanPar-1)
			{				
				active_CH_tile(i+1):=active_CH_tile(i)
			}
			active_CH_tile(0):=active_CH_tile(ChanPar-1)
			

		}
	

		val buffIN =  Wire(Vec(Conv,UInt(8.W)))
		val default_IN =Vec(Seq.fill((Conv))(1.U(8.W)))
		buffIN:=default_IN
	
		//buffIN<>MultiBuffer(0).read_data	
		for(i <- 0 until ChanPar)//careful of multiple drivers
		{
			when(active_CH_tile(i))
			{
				buffIN:=MultiBuffer(i).read_data				
					
			}
			
		}





		val CORE = Array.fill(FchP){ Module(new poolE(Conv)).io }
		val MACDELAY = Reg(Vec(FchP,UInt(8.W))) //we explicitaly delay the mac, try moving this into the mac
		val BufferValid = RegInit(false.B)
	BufferValid := MultiBuffer(0).convState 








		for(i <- 0 until ChanPar)
		{
			MultiBuffer(i).write_enable:=io.write_enable(i)// when apes output the must send a valid and a channel number
			MultiBuffer(i).write_data:=io.write_data(i)
		}
		for (j <- 0 until FchP)
		{
			//route
			for (l <- 0 until Conv)
			{
				CORE(j).input_data(l) := buffIN(l)
			}	
				
			CORE(j).WD := MultiBuffer(0).WD
			CORE(j).KD := MultiBuffer(0).KD
			CORE(j).convState := MultiBuffer(0).convState 
				//io.mac_out(i+j*ChanPar):=0.U
				io.mac_out(j)(0):=0.U
			

		}	
		ChanPTR:=MultiBuffer(0).ChannelPTR//grab a channel ptr from one of the buffers
			//the following 2 lines delay the channel ptr by 2 cycles
		ChanPTRDelay:=ChanPTR
		ChanPTRDelayFINAL:=ChanPTRDelay
		io.Chan_Ptr:=ChanPTRDelayFINAL
		for (j <- 0 until FchP)
		{
					when( (CORE(0).valid) ) 		
					{	io.valid_out:=1.U
						
						//io.mac_out(i+j*ChanPar):=CORE(j).mac_out(i)
							MACDELAY(j):=CORE(j).mac_out
						
					}
					.otherwise
					{
						io.valid_out:=0.U
					}
		}  	

		for (j <- 0 until FchP)
		{
	
			for (i <- 0 until KUints)
			{
		
					io.mac_out(j)(i):=MACDELAY(j)(i)
			}

		}

}


}

