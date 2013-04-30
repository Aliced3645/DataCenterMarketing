import threading
from scapy.all import *
import os
import subprocess
import json
import argparse
import random
import socket
from struct import *
import datetime
import pcapy
import sys



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
    latency = random.randint(1000000, 10000000)
    latencyq = 100000000
    randomJson = constructBidString(value, 2, minRate, data, start, end,latencyq)
    return randomJson

def eth_addr (a) :
    b = "%.2x:%.2x:%.2x:%.2x:%.2x:%.2x" % (ord(a[0]) , ord(a[1]) , ord(a[2]), ord(a[3]), ord(a[4]) , ord(a[5]))
    return b
       

def parse_packet(packet) :
     
    #parse ethernet header
    eth_length = 14
     
    eth_header = packet[:eth_length]
    eth = unpack('!6s6sH' , eth_header)
    eth_protocol = socket.ntohs(eth[2])
 
    #Parse IP packets, IP Protocol number = 8
    if eth_protocol == 8 :
        #Parse IP header
        #take first 20 characters for the ip header
        ip_header = packet[eth_length:20+eth_length]
        #now unpack them :)
        iph = unpack('!BBHHHBBH4s4s' , ip_header)
        version_ihl = iph[0]
        ihl = version_ihl & 0xF
 
        iph_length = ihl * 4
        s_addr = socket.inet_ntoa(iph[8]);
         
        #we fetch the response packet
        if str(s_addr) == '1.2.3.4':
            print "\ntarget fetched!!\n"
            h_size = eth_length + iph_length 
            data = packet[h_size:]
            fetchedData = data[0:]
            return fetchedData
        
lastFetched = ''

def sniffing():
    global lastFetched
    global localBiddingRound
    
    cap = pcapy.open_live(interface , 65536 , 1 , 0)
    while(1) :
        (header, packet) = cap.next()
        #function to parse a packet
        result = parse_packet(packet)
        if result is not None:
            lastFetched = result
            thread.exit()

def udpListen():
    
    return

    
if __name__ == "__main__":
    
    while True:
        print randomRequestGenerator()
        #send scapy packet ...
        #p = threading.Thread(target = replyListener)
        #p.daemon = True
        #p.start()
        p = threading.Thread(target=sniffing)
        p.start()
        packet = Ether() / IP(dst="10.0.0.10") / randomRequestGenerator()
        # send
        sendp(packet, iface=interface)
        
        #sniffing()
        p.join()
        
        print lastFetched
        
        #parse the result
        if lastFetched[0:3] == 'Yes':
            print 'ready to send UDP flows'
            
        
