# AWARE-CNN-ACCEL {WORK-IN-PROGRESS}
============================================================================================
Project for Automatic Workflow for Application-aware Real-time Edge CNN accelerators
## What this is?
This repository is a chisel project directory. Specifically, this is an architecture compiler for application specific DNN designs. Precisely tailored architectures can be made by exploiting the reconfigurable fabric of FPGAs. The algorithm benefits by allowing resources to be dedicated based on necessity, and allowing energy savings based on a customized memory hierarchy. Further benefits are gained by allowing users to specialize the designs with respect to their unique sets of constraints such as latency, throughput, power, and resource utilization. This specialization is achieved through dedicated architecture knobs and allows for increased coverage of the (incredibly diverse) Edge-DNN domain.

## How to get started
1. (WIP) Use the semi-automated Design Space Explorer to generate the parameters for your targeted constraints and network.
    *(currently implementing further automation, including linear optimizer and better resource estimation)*
Make sure to install Chisel first, by following these steps ( directly from the Chisel Repo https://github.com/freechipsproject/chisel3 )
      1. Install java
         ```sudo apt-get install default-jdk```
      1. Install sbt
          ```
             echo "deb https://dl.bintray.com/sbt/debian /" | sudo tee -a /etc/apt/sources.list.d/sbt.list	
             sudo apt-key adv --keyserver hkp://keyserver.ubuntu.com:80 --recv 642AC823	
             sudo apt-get update	
             sudo apt-get install sbt
           ```
      1.Install Verilator
      ```
      sudo apt-get install git make autoconf g++ flex bison	
      git clone http://git.veripool.org/git/verilator	
      git pull	
      git checkout verilator_4_016	
      unset VERILATOR_ROOT # For bash, unsetenv for csh	
      autoconf # Create ./configure script	
      ./configure	
      make	
      sudo make install	
      ```
1. The first step is to clone this repository.

1. Next, edit */src/main/scala/thecode/netParam.scala* to include the parameters for your specific architecture design.
The parameters are currently set to a toy design to prove our analytical model for latency.

After you have the parameters for your design, you can generate the verilator simulation of our toy example with the following commands:
```
sbt
test:runMain thecode.Launcher simnet --backend-name verilator
```
The simulation is a vcd file located: */test_run_dir/thecode/simnet/simnet.vcd*.

This file can be viewed in GTKWave, which can be installed with:
```
sudo apt-get update
sudo apt-get install gtkwave
```
It should be noted that even with a toy example, the VCD file is a few hundred MBs.
After verifying a design through simulation, only three SBT commands are required to generate the verilog.
```
sbt
test:runMain thecode.generator
test:runMain thecode.opt
test:runMain thecode.connector
```
These commands should be entered sequentially
1. *test:runMain thecode.generator* is used to generate each layer step by step. The outputs are located in */src/main/resources/*

1. *test:runMain thecode.opt* is used to optimize each layer with synthesis attributes for vivado.
This script works in the directory: */src/main/resources/*

1. Lastly, *test:runMain thecode.connector* connects everything together.
Before running this command, the files in: */src/main/resources/* must be moved to the */verilog/* directory.
It is advised to delete old files in there first. The final outputs can be found in the */verilog/* directory.

The very last step is to generate the bitstream from the machine generated verilog.
*A Vivado TCL script can be used, and will be proved in the form of a script generator*



