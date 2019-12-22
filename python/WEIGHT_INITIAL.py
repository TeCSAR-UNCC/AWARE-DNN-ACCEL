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
#parser.add_argument("DEPTH")
args = parser.parse_args()

fileedit=args.filename
#depth=args.DEPTH

with open(fileedit, "r") as in_file:
    buf = in_file.readlines()

with open(fileedit, "w") as out_file:
    for line in buf:
        if line.startswith("  reg [7:0] Wmemory ["):
            listline= line.split(':')
            #depth=''.join(list(filter(str.isdigit, listline[2])))
            depth= re.findall('\d+', listline[2] )[0]
            str2 = "\n \tinteger j;\n"
            str3 =" \tinitial begin\n"
            str4 = " \t\tfor(j = 0; j < %s; j = j+1)\n"%(depth)
            str5 = " \t\t\tWmemory[j] = {$random} % 255;\n"
            str6 =" \tend\n"
            strfinal = str2+str3+str4+str5+str6
            line = line + strfinal
        out_file.write(line)





