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
parser.add_argument('-start', dest='start', action='store', help='start time')
parser.add_argument('-end', dest='end', action='store', help='end time')
parser.add_argument('-bid', dest='action', action='store_const', const='bid',
        default='bid', help='action: bid, queray, result')
parser.add_argument('-ping', dest='action', action='store_const', const='ping',
        default='ping', help='action: pibg, bid, queray, result')
parser.add_argument('-query', dest='action', action='store_const',
        const='query', default='bid', help='action: bid, query,result,allresults, time')
parser.add_argument('-result', dest='action', action='store_const',
        const='result', default='bid', help='action: bid, query,result, allresults, time')
parser.add_argument('-allresults', dest='action', action='store_const',
        const='allresults', default='bid', help='action: bid, query, result,allresults, time')
parser.add_argument('-time', dest='action', action='store_const', const='time', default='bid', help='action: bid,query, result,allresults, time')
args = parser.parse_args()

if args.action == 'bid':
    bidderID = args.bidderID
    srcID = args.srcID
    destID = args.destID
    value = args.value
    minRate = args.minRate
    maxRate = args.maxRate
    data = args.data
    start = args.start
    end = args.end

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
    if start is None:
        print 'Start time is not filled'
        sys.exit()
    if end is None:
        print'End time is not filled'
        sys.exit()

    # all fields are submitted, construct the REST qurey string
    json = " '{\"Bidder\":\"%s\", \"Value\":%s, \"SID\":%s, \"DID\":%s, \"MinRate\":%s, \"Data\":%s,\"Start\":%s, \"End\":%s" %(bidderID, value, srcID, destID, minRate, data, start, end)

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
    print '\n' + result
    sys.exit()

elif args.action == 'ping':
    srcID = args.srcID
    destID = args.destID
    if srcID is None:
        print 'Source Host ID not filled'
        sys.exit()
    if destID is None:
        print 'Destination Host ID not filled'
        sys.exit()
    json = " '{\"SID\":%s, \"DID\":%s" %(srcID, destID)
    json += "}'"
    uri = "http://%s/marketing/latency/" %(args.controllerRestIp)
    command = "curl -d" + json + " " + uri
    result = os.popen(command).read()
    print '\n' + result
    sys.exit()

elif args.action == 'result':
    bidderID = args.bidderID
    if bidderID is None:
        print 'BidderID not filled'
        sys.exit()
    uri = "http://%s/marketing/result/%s" %(args.controllerRestIp, bidderID)
    command = "curl -s " + uri + " | python -mjson.tool"
    result = os.popen(command).read()
    print '\n' + result
    sys.exit()

elif args.action == 'allresults':
    uri = "http://%s/marketing/result/" %(args.controllerRestIp)
    command = "curl -s " + uri + " | python -mjson.tool"
    result = os.popen(command).read()
    print '\n' + result
    sys.exit()

elif args.action == 'time':
    uri = "http://%s/marketing/time/" %(args.controllerRestIp)
    command = "curl -s " + uri + " | python -mjson.tool"
    result = os.popen(command).read()
    print '\n' + result
    sys.exit()

