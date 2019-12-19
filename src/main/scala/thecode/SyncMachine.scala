/*
*****************************************************************Synchrosnisation machine to sync data between two layers*********************************************************
Ver 1.1
Author: Adarsh Sawant(asawant2@uncc.edu)
Description:
This is a Synchronising machine to Synchronise data rates between two layers.
This module would accept the depth and size of the delay , NOte that the delay should be always less than that of the size of depth of the fifo element.
The module would accept the data inside its storage element untill the delay size is reached, once the delay size is reached then it would automatically readout the data on the output port 
We use Readpixelcounter tho reset the SyncAcquired variable 
WE use the WritePxilcounter to reset the Write counter as Read and the write activities are async with each other unlike fifo where they are synchronised if there is data in the buffer 
but in this sync machin we know that  if the sync is acquired then we ncan read data iresspective of write and write the data ireesecptive read.Thus once the sync is acquired this machine would be 
aync wrto Read and write
Known problems:
Currently the delay size cannot be less than 3. 

Justins edits:

much greater simplification
the delay and depth are essentialy tied together.
With the delay being staticly equal to depth-1


*****************************************************************************************************************************

*/



package thecode
import chisel3._
import chisel3.util._
import chisel3.iotesters
import chisel3.iotesters.{ChiselFlatSpec, Driver, PeekPokeTester}
import Chisel.iotesters.{SteppedHWIOTester, ChiselFlatSpec}
import scala.language.reflectiveCalls
import chisel3.experimental._
 
class SyncMachine (val depth: Int,val Rowsize:Int) extends Module 
{
  val io = IO(new Bundle {
    val write_enable    = Input(UInt(1.W))
    val write_data      = Input(UInt(8.W))
    val read_data       = Output(UInt(8.W))
    val output_valid    = Output(UInt(1.W))
    //val fifo_full       = Output(UInt(1.W)) //from older version  
  })
    //read and write ptr with just enough bits
    val ReadPtr         = RegInit(0.U(log2Ceil(depth+1).W)) 
    val WritePtr        = RegInit(0.U(log2Ceil(depth+1).W))    
    val OutputValid     = RegInit(0.U(1.W))
    val ReadState   = RegInit(0.U(1.W))
    val InputCount    = RegInit(0.U(log2Ceil(Rowsize+1).W))
    val OutputCount    = RegInit(0.U(log2Ceil(Rowsize+1).W))
  
		io.output_valid:=io.write_enable
		io.read_data:=io.write_data
		
         /*
    val SYNCmem = SyncReadMem(depth,UInt(8.W)) //the physical layout of memory has been abstracted away

	    when(io.write_enable === 1.U) // Writing the data to the Fifo element
	    {   
	       SYNCmem.write(WritePtr,io.write_data)
		WritePtr:=WritePtr+1.U
		InputCount:=InputCount+1.U
	    }

 	when((WritePtr ===(depth).asUInt))
	{
		ReadState:=1.U
		ReadPtr:=ReadPtr+1.U
		OutputValid  :=1.U
		OutputCount:=OutputCount+1.U
		WritePtr:=0.U

	}
	when (ReadState===1.U)
	{
	    		
			ReadPtr:=ReadPtr+1.U
			OutputValid  :=1.U
			OutputCount:=OutputCount+1.U

	}
	.otherwise
	{
			OutputValid  :=0.U
	}
	
	when (OutputCount===(depth).asUInt)
	{
		
		ReadPtr:=0.U
		ReadState:=0.U
	}

    io.read_data := SYNCmem.read(ReadPtr)+ReadPtr
	io.output_valid:=OutputValid

*/
}
