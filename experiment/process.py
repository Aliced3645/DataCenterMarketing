import os
import subprocess
from subprocess import call
try:	
	command =" rm -rf *.t1 *.t2 *.t3  *.t4  *.t5 *.csv  *~"
	print command		
	res1=call(command, shell=True)
except OSError as e:
	print e


proc = subprocess.Popen("ls", stdout =subprocess.PIPE)
output=proc.stdout.read()
for w in output.split():
	try:	
		command =" cat "+w+" |grep Round >"+w+".t1;  sed \'s/ThisRoundUtilization/ /\' "+w+".t1  > "+w+".t2 ;  sed \'s/INFO: Bidding Round/ /\' "+w+".t2  > "+w+".t3 ;   sed \'s/thisRoundIncome/ /\' "+w+".t3  > "+w+".t4 ;   sed \'s/ total income / /\' "+w+".t4  > "+w+".t5;   sed \':a;N;$!ba;s/.\\n/\\n /g\' "+w+".t5 |tr -s \' \'|cut -d\' \' -f3-  > "+w+".csv; rm -rf *.t1 *.t2 *.t3  *.t4 *.t5    *~"
		print command		
		res1=call(command, shell=True)
	except OSError as e:
		print e
