# -*- coding: utf-8 -*-
import string
import os
import argparse

#f1 = open('l3.scala', 'r')
#f2 = open('L3.scala', 'w')
#for line in f1:
#    f2.write(line.replace('_0', ' '))
#f1.close()
#f2.close()



parser = argparse.ArgumentParser()
parser.add_argument("filename")
args = parser.parse_args()

fileedit=args.filename
f = open(fileedit,'r')
filedata = f.read()
f.close()

newdata = filedata.replace("module memory","(* DONT_TOUCH = “yes” *)  module memory")
newdata = filedata.replace("module WBanks","(* keep_hierarchy = \"yes\" *) module WBanks")
#(* S = \"yes\" *)
#(* dont_touch = "true" *) 
#module WBanks
# XDC constraint where you can specify the instance name on which you want to apply DONT_TOUCH constraint.

f = open(fileedit,'w')
f.write(newdata)
f.close()
