import string
import os
import argparse
import re


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
newdata = re.sub(".*( _RAND).*", "", filedata)
#newdata = filedata.replace("MAC MAC","JUSTINLOOK")

f = open(fileedit,'w')
f.write(newdata)
f.close()
