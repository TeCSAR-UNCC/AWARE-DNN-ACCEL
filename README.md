# AWARE-DNN-ACCEL {WORK-IN-PROGRESS}
============================================================================================
Project for Automatic Workflow for Application-aware Real-time Edge DNN accelerators
## What it is?
This repository is a chisel project directory. Specifically, this is an architecture compiler for application specific DNN designs. 
## How to get started
1. (WIP) Use the semi-automated Design space explorer to generate the parameters for your targeted constraints and network.
    *(currently implementing further automation, including linear optimizer and better resource estimation)*
1. Simply clone this repository and run SBT inside it to get started.

1. Edit */src/main/scala/thecode/netParam.scala* to include the parameters for your specific Architecture design.

After you have the parameters for your Design its three SBT commands to generate the verilog.

1. Run *test:runMain thecode.generator* to generate each layer step by step.

1. Run *test:runMain thecode.opt* to optimize each layer with synthesis attributes for vivado.

1. Lastly, run *test:runMain thecode.connector* to connect everything together

The very last step is to generate the bitstream from the machine generated verilog.
*A Vivado TCL script can be used.*
