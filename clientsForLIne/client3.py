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
import time


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

bidRound = 0

if interface is None:
    interface = "h3-eth0"
if bidderID is None:
    bidderID = "gay"
if host is None:
    host = 2
if allhosts is None:
    allhosts = 5
    
def constructBidString(value, destID, minRate, data, start, end, latency):
    global host
    global bidderID
    json = "{\"Bidder\":\"%s\", \"Value\":%s, \"SID\":%s, \"DID\":%s, \"MinRate\":%s, \"Data\":%s,\"Start\":%s, \"End\":%s, \"Latency\":%s}" % (bidderID, value, host, destID, minRate, data, start, end, latency)
    return json

def randomRequestGenerator():
    #random items to generate:
    value = random.randint(1000,10000)
    while True:
        destID = random.randint(0,4)
        if destID != 2:
            break
    minRate = random.randint(0, 10)
    #relative time..
    start = random.randint(10000,100000)
    end = random.randint(start, 200000)
    data = (end - start) / 1000.0 * minRate
    latency = random.randint(1000000, 10000000)
    latencyq = 100000000
    randomJson = constructBidString(value, destID, minRate, data, start, end,latencyq)
    return randomJson

def eth_addr (a) :
    b = "%.2x:%.2x:%.2x:%.2x:%.2x:%.2x" % (ord(a[0]) , ord(a[1]) , ord(a[2]), ord(a[3]), ord(a[4]) , ord(a[5]))
    return b
       
lastFetched = ''

def parse_packet(packet) :
     
    #parse ethernet header
    eth_length = 14
    global lastFetched    
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
            h_size = eth_length + iph_length 
            data = packet[h_size:]
            lastFetched = data[0:]
            return '1.2.3.4'
        

        #Reminder from the server that 
        #the client could start to send packets
        elif str(s_addr) == '1.2.3.5':
            h_size = eth_length + iph_length 
            data = packet[h_size:]
            lastFetched = data[0:]
            return '1.2.3.5'




def sniffing():
    global lastFetched
    global localBiddingRound
    global bidRound
    cap = pcapy.open_live(interface , 65536 , 0 , 0)
    #while bidRound < 5 :
    while True:
        (header, packet) = cap.next()
        #function to parse a packet
        result = parse_packet(packet)
        if result is not None:
            if result == '1.2.3.4':
		bidRound += 1
                print lastFetched
                content = randomRequestGenerator()
                packet = Ether() / IP(dst="10.0.0.255") / content
                sendp(packet, iface=interface, count=1)
'''
            if result == '1.2.3.5':
                #parse JSON first
                parsed_json = json.loads(lastFetched)
                print 'Get a reminder from the server, start transmitting..'
                destIP = parsed_json['destIP']
                bandwidth = parsed_json['bandwidth']
                duration = parsed_json['duration']
                data = parsed_json['data']
                pSend = threading.Thread(target=udpSend, args=(destIP,
                            bandwidth, duration, data))
                pSend.start()
'''

                

datablock = 'cccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccc'


granuality = 1000.0
def udpSend(destIP, bandwidth, duration, data):
    # current method : send until all data ends
    sock = socket.socket(socket.AF_INET, # Internet
                     socket.SOCK_DGRAM) # UDP
    start_time = time.time()
    left = data
    allData = 0
    count = 0
    granulaityData = (float) (bandwidth / granuality)
    print 'Begin transmitting to ' + str(destIP) + ' at the speed of ' + str(bandwidth) + 'MB/s'
    print 'Duration: ' + str(duration) + 'ms, ', 'data: ' + str(data) + 'MB'
    while left > 0 :
        dataThisRound = 0
        startThisRound = time.time()
        while dataThisRound <=  granulaityData:
            allData += 0.001
            dataThisRound += 0.001
            count += 1
            sock.sendto(datablock, (destIP, 9999))
            left = left - 0.001
            if count == 1000:
                rate = allData / (time.time() - start_time) 
                count = 0
        passedTime = time.time() - startThisRound
        if passedTime < 0.001:
            t = time.sleep(0.001 - passedTime)
    
    return

#a thread always receiving data for UDP
def udpListen():
    sock = socket.socket(socket.AF_INET, # Internet
            socket.SOCK_DGRAM) # UDP
    sock.bind(('0.0.0.0', 9999))
    while True:
        data, addr = sock.recvfrom(5000)
    return

    
if __name__ == "__main__":
    
    p = threading.Thread(target=sniffing)
    p.start()
    random.seed(3)
    p2 = threading.Thread(target=udpListen)
    p2.start()

    #make the first bid
    content = randomRequestGenerator()
    print content
    packet = Ether() / IP(dst="10.0.0.255") / content
    # send
    sendp(packet, iface=interface, count=1)
    bidRound += 1
    p.join()

'''
    while True:
        content = randomRequestGenerator()
        print content
        #send scapy packet ...
        #p = threading.Thread(target = replyListener)
        #p.daemon = True
        #p.start()
        p = threading.Thread(target=sniffing)
        p.start()
        packet = Ether() / IP(dst="10.0.0.255") / content
        # send
        sendp(packet, iface=interface, count=1)
        
        #sniffing()
        p.join()
        
        print lastFetched
        
        #parse the result
        if lastFetched[0:3] == 'Yes':
            print 'ready to send UDP flows'
            
'''

