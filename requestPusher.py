#! /usr/bin/python


import os
import sys
import subprocess
import json
import argparse
import io
import time

"""
Marketing bidders could use this script to push bid requests to the Auctioneer
for this round
Example Original Request REST command:
    Send Request:
        curl -d '{"Bidder":"Shu", "Value":100, "SID":1, "DID":3, "MinRate":150,"MaxRate":200"Data":data} 		http://localhost:8080/marketing/request/shu
    Query Request:
        curl -s http://localhost:8080/marketing/result/shu | python -mjson.tool

Usage:
    requestPusher --controller {IP:REST_PORT} --
"""
parser = argparse.ArgumentParser(description='Request Pusher')
parser.add_argument('-controller', dest='controllerRestIp', action='store', default='localhost:8080', help='controller IP:RESTport, e.g., localhost:8080 or A.B.C.D:8080')
# Six must-be-filled fields
parser.add_argument('-id', dest='bidderID', action='store',help='ID for the bidder, e.g., mike')
parser.add_argument('-src', dest='srcID', action='store', help='source host ID')
parser.add_argument('-dst', dest='destID', action='store', help='source host ID')
parser.add_argument('-value', dest = 'value', action='store', help='value of the bid')
parser.add_argument('-min', dest='minRate', action='store', help='minimum required rate')
parser.add_argument('-max', dest='maxRate', action='store', help='maximum required rate')
parser.add_argument('-data', dest='data', action='store', help='data to transmit')
parser.add_argument('-bid', dest='action', action='store_const', const='bid', default='bid', help='action: bid, query')
parser.add_argument('-query', dest='action', action='store_const', const='query', default='bid', help='action: bid, query')

args = parser.parse_args()

if args.action == 'bid':
    bidderID = args.bidderID
    srcID = args.srcID
    destID = args.destID
    value = args.value
    minRate = args.minRate
    maxRate = args.maxRate
    data = args.data

    if bidderID is None:
        print 'BidderID not filled'
        sys.exit()
    if srcID is None:
        print 'Source Host ID not filled'
        sys.exit()
    if destID is None:
        print 'Destination Host ID not filled'
        sys.exit()
    if value is None:
        print 'Bid Value is not filled'
        sys.exit()
    if minRate is None:
        print 'MinRate is not filled'
        sys.exit()
    if data is None:
        print 'Data size is not filled'
        sys.exit()

    # all fields are submitted, construct the REST qurey string
    json = " '{\"Bidder\":\"%s\", \"Value\":%s, \"SID\":%s, \"DID\":%s, \"MinRate\":%s, \"Data\":%s" %(bidderID, value, srcID, destID, minRate, data)

    if maxRate is not None:
        json += " \"MaxRate\":%s " %(maxRate)
    json += "}'"
    #print json
    uri = "http://%s/marketing/request/%s" %(args.controllerRestIp, bidderID)
    command = "curl -d" + json + " " + uri
    #print command
    result = os.popen(command).read()
    sys.exit()

elif args.action == 'query':
    bidderID = args.bidderID
    if bidderID is None:
        print 'BidderID not filled'
        sys.exit()
    uri = "http://%s/marketing/request/%s" %(args.controllerRestIp, bidderID)
    command = "curl -s " + uri + " | python -mjson.tool"
    result = os.popen(command).read()
    print result
    sys.exit()


