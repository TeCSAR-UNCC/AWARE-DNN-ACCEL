package thecode
 
import chisel3.iotesters.{Driver, TesterOptionsManager}
import utils.TutorialRunner
 
// test:runMain thecode.Launcher layer --backend-name verilator


object Launcher {
  val tests = Map(
 
      "buffer" -> { (manager: TesterOptionsManager) =>

        Driver.execute(() => new buffer(
	//rowSize,filterSize,stride,outConv,KernNum,(ChanBuffer/pipeline),GROUPS,Shuffle,Conv					
	   9,       1,        1,      9,      2,             2,              1,    1,     3
								), manager) {
          (c) => new bufferTests(c)
        }
      },
/*
	//rowSize,filterSize, stride,outConv, KernNum, KUints ,ChanPar, channel buffed
	//rowSize, filterSize , stride , outConv ,KernNum , KUints , ChanPar  , ChanBuffer
	   "frontend" -> { (manager: TesterOptionsManager) =>
        Driver.execute(() => new frontend(9,3,2,4,1,1,1,2,1,5,1,1,1,1,0,1), manager) {
          (c) => new frontendTests(c)
        }
      },

*/
	//rowSize , filterSize, stride, Out_size ,KernNum  ,KUints , ChanPar , ChanBuffer ,poolOut )//work on
	   "layer" -> { (manager: TesterOptionsManager) =>
        Driver.execute(() => new 
	  layer(
	//rowSize,filterSize,  stride,  Out_size , KernNum[10]   , KUInts   ,  ChanPar    ,  ChanBuffer[10]    ,  poolOut
	   56	   ,3,		1,	  54,		1,	1,		1,		3,	"nopool",
	                 //   delay    ,  GROUPS   ,  Shuffle   ,  pipeline   ,  Conv   ,   pool   ,FcDiv,ROM,AVG   
			    20		,1		,1	   ,1		,1	   ,0	  ,1,1,0
		), manager) {
          (c) => new layerTests(c)
        }
      },

//old layer        Driver.execute(() => new layer(56,3,1,56,1,1,1,3,"nopool",1,5,1,1,1,1,1,1), manager) {


	   "CPE" -> { (manager: TesterOptionsManager) =>
        Driver.execute(() => 
	new CPE(
		//rowSize,filterSize ,stride, outConv , KernNum ,KUints ,ChanPar , ChanBuffer,
		  9		,3,	1,	7	,10,	1	,2	,	10,
		//GROUPS ,Shuffle , pipeline , Conv, pool,FcDiv  ,ROM 
		   1	  ,1		,1	,1	,0  ,1    ,0

		), manager) {
          (c) => new CPETests(c)
        }
      },

/*
      "MDAPE" -> { (manager: TesterOptionsManager) =>
      Driver.execute(() => new MDAPE(4,1,2,1), manager) {
        (c) => new MDAPETests(c)} },
*/

     
 "MAC" -> { (manager: TesterOptionsManager) =>
      Driver.execute(() => new MAC(1), manager) {
        (c) => new MACTests(c)
      }
    },
   "Counter" -> { (manager: TesterOptionsManager) =>
      Driver.execute(() => new Counter(), manager) {
        (c) => new CounterTest(c)
      }
    },

    "jcounter" -> { (manager: TesterOptionsManager) =>
      Driver.execute(() => new jcounter(1), manager) {
        (c) => new jcounterTests(c)
      }
    },

    "Dcounter" -> { (manager: TesterOptionsManager) =>
      Driver.execute(() => new Dcounter(1), manager) {
        (c) => new DcounterTests(c)
      }
    },

/*
	   "NetACC" -> { (manager: TesterOptionsManager) =>
        Driver.execute(() => new NetACC(), manager) {
          (c) => new NetACCTests(c)
        }
      },
*/
		//Fs,kn,kern-par,ch-par, chan-buff, test	
	   "memory" -> { (manager: TesterOptionsManager) =>
        Driver.execute(() => new memory(3,2,2,2,2,1,0,1,1), manager) {
          (c) => new memoryTests(c)
        }
      },

	//Fs,kn,kern-par,ch-par, chan-buff, test	
	   "Inputsource" -> { (manager: TesterOptionsManager) =>
        Driver.execute(() => new Inputsource(5,3), manager) {
          (c) => new InputsourceTests(c)
        }
      },

	//Fs,kn,kern-par,ch-par, chan-buff, test	
	   "synch" -> { (manager: TesterOptionsManager) =>
        Driver.execute(() => new SyncMachine(4,8), manager) {
          (c) => new SynchTests(c)
        }
      },

	//rowSize , filterSize, stride, Out_size ,KernNum  ,KUints , ChanPar , ChanBuffer ,poolOut )//work on
	   "BBlayer" -> { (manager: TesterOptionsManager) =>
        Driver.execute(() => new 
	  BBlayer(
	//rowSize,filterSize,  stride,  Out_size , KernNum[10]   , KUInts   ,  ChanPar    ,  ChanBuffer[10]    ,  poolOut
	   56	   ,3,		1,	  56,		1,	3,		2,		3,	"nopool"
	//,  testing    ,   delay    ,  GROUPS   ,  Shuffle   ,  pipeline   ,  Conv   ,   pool   ,FcDiv ,layernum,ROM,AVG  
			    ,20		,1		,1	   ,1		,1	   ,0	  ,1     ,1,1,0
		), manager) {
          (c) => new BBlayerTests(c)
        }
      },

		//Fs,kn,kern-par,ch-par, chan-buff, test	
	   "WBanks" -> { (manager: TesterOptionsManager) =>
        Driver.execute(() => new WBanks(3,2,2,2,2,1,1,1), manager) {
          (c) => new WBanksTests(c)
        }
      },

	//Fs,kn,kern-par,ch-par, chan-buff, test	
	   "network" -> { (manager: TesterOptionsManager) =>
        Driver.execute(() => new network(), manager) {
          (c) => new networkTests(c)
        }
      },
	//Fs,kn,kern-par,ch-par, chan-buff, test	
	   "simnet" -> { (manager: TesterOptionsManager) =>
        Driver.execute(() => new simnet(), manager) {
          (c) => new simnetTests(c)
        }
      }



  )
  def main(args: Array[String]): Unit = {
    TutorialRunner("thecode", tests, args)
  }
}
