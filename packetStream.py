import json
import urllib2
import sys

controller = 'localhost'
host = '00:00:00:00:00:02'

url = 'http://%s:8080/wm/core/packettrace/json' % controller
filter = {'mac':host, 'direction':'both', 'period':1000}
post_data = json.dumps(filter)
request = urllib2.Request(url, post_data, {'Content-Type':'application/json'})
response_text = None

try:
    response = urllib2.urlopen(request)
    response_text = response.read()
except Exception, e:
    # Floodlight may not be running, but we don't want that to be a fatal
    # error, so we just ignore the exception in that case.
    print "Exception:", e
    exit

if not response_text:
    print "Failed to start a packet trace session"
    sys.exit()

response_text = json.loads(response_text)

sessionId = None
if "sessionId" in response_text:
    sessionId = response_text["sessionId"]




def terminateTrace(sid):
    global controller

    filter = {SESSIONID:sid, 'period':-1}
    post_data = json.dumps(filter)
    url = 'http://%s:8080/wm/core/packettrace/json' % controller
    request = urllib2.Request(url, post_data, {'Content-Type':'application/json'})
    try:
        response = urllib2.urlopen(request)
        response_text = response.read()
    except Exception, e:
        # Floodlight may not be running, but we don't want that to be a fatal
        # error, so we just ignore the exception in that case.
        print "Exception:", e



try:
    # Make socket
    transport = TSocket.TSocket('localhost', 9090)
    # Buffering is critical. Raw sockets are very slow
    transport = TTransport.TFramedTransport(transport)
    # Wrap in a protocol
    protocol = TBinaryProtocol.TBinaryProtocol(transport)
    # Create a client to use the protocol encoder
    client = PacketStreamer.Client(protocol)
    # Connect!
    transport.open()

    while 1:
        packets = client.getPackets(sessionId)
        for packet in packets:
            print "Packet: %s"% packet
            if "FilterTimeout" in packet:
                sys.exit()

except Exception, e:
    print '%s' % (e.message)
    terminateTrace(sessionId)

