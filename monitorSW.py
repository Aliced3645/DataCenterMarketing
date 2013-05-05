#!/usr/bin/python
import sys
import time
from time import gmtime, strftime, sleep
from subprocess import call

if len(sys.argv) <2:
	print 'usage: python monitorSW.py [interval_in_milisecond] '
#clean
call("rm -rf diffs temp temp2;mkdir diffs;touch temp", shell=True)
count =0

print 'checking  switches every ', sys.argv[1], 'milliseconds'
while True :
	try:	
		res1=call("curl http://localhost:8080/wm/core/controller/switches/json |python -mjson.tool |cat >temp ", shell=True)
		res2=call("diff temp temp2 ", shell=True)
		res3=call("cp temp temp2 ", shell=True)
		if res2 !=0:
			ver =strftime("%m%dT%H_%M_%S", gmtime())
			res4=call("cp temp2 ./diffs/"+str(count)+"_"+ver, shell=True)
			count=count+1
	except OSError as e:
		print e
	 
	time.sleep( float(sys.argv[1])/1000)
