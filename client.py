import threading
from scapy.all import *
import os
import sys
import subprocess
import json
import argparse
import random

parser = argparse.ArgumentParser(description='Client')
parser.add_argument('-interface', dest='interface', action='store', help='Network card Interface connected to swtich')
parser.add_argument('-id', dest='bidderID', action='store', help='ID for the bidder, e.g., mike')
parser.add_argument('-host', dest='host', action='store', help='source host ID')
parser.add_argument('-all', dest='allhosts', action='store', help='total number of hosts')
args = parser.parse_args()
interface = args.interface
bidderID = args.bidderID
host = args.host
allhosts = args.allhosts

if interface is None:
    interface = "h1-eth0"
if bidderID is None:
    bidderID = "shu"
if host is None:
    host = 0
if allhosts is None:
    allhosts = 7
    
def constructBidString(value, destID, minRate, data, start, end, latency):
    json = "{\"Bidder\":\"%s\", \"Value\":%s, \"SID\":%s, \"DID\":%s, \"MinRate\":%s, \"Data\":%s,\"Start\":%s, \"End\":%s, \"Latency\":%s}" % (bidderID, value, host, destID, minRate, data, start, end, latency)
    return json

def randomRequestGenerator():
    #random items to generate:
    value = random.randint(0,1000)
    destID = random.randint(0,allhosts - 1)
    minRate = random.randint(0, 5000)
    data = random.randint(0,50000)
    start = random.randint(0,1000)
    end = random.randint(start, 2000)
    latency = random.randint(100000, 1000000)
    
    randomJson = constructBidString(value, destID, minRate, data, start, end, latency)
    return randomJson

#the second thread sniffing coming packets
#def replyListener():


if __name__ == "__main__":
    print randomRequestGenerator()
    #send scapy packet ...
    #p = threading.Thread(target = replyListener)
    #p.daemon = True
    #p.start()
    packet = Ether() / IP(dst="10.0.0.10") / randomRequestGenerator()
    # send
    sendp(packet, iface=interface)
    # listen for receiving packets
    #while True:
    #    incoming = sniff(count=1, iface=interface)
    #    print incoming
    #    break
    

        
