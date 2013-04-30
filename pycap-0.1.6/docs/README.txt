=============================================
 Python Packet Capture and Injection Library
=============================================

:Author: Mark Rowe
:Contact: bdash@gmx.net
:Web site: http://pycap.sourceforge.net
:Project page: http://sourceforge.net/projects/pycap/

This package provides the ability to capture packets from network
interfaces.  It dissects commonly found structures in network packets
such as Ethernet, IP_, ARP_, TCP_, UDP_, and ICMP_ headers.

Supported Platforms
-------------------

``pycap`` should work on any platform that supports libpcap_, libnet_ and
Python_.  It currently requires Python 2.3.  It will be backported to
Python 2.2 if enough interest is shown. If you find any bugs please
report them on the `Sourceforge project page`_.

To Do 
-----

Packet injection is currently a work in progress.  It is known to be
broken, and should hopefully be working soon.  A full list of things
left to complete is available on a `separate page`_.

Installation
------------

``pycap`` is available from CVS, or from its `Sourceforge project
page`_.  Installing ``pycap`` from the released tarball requires
extracting the file then executing the following commands.

::

	% python setup.py build
	% sudo python setup.py install


To obtain and install ``pycap`` from CVS follow these steps,
adjusting for your platform as necessary.  Note that the CVS password
is empty.

::

	% cvs -d:pserver:anonymous@cvs.sourceforge.net:/cvsroot/pycap login 
	(Logging in to anonymous@cvs.sourceforge.net)
	CVS password:
	% cvs -z3 -d:pserver:anonymous@cvs.sourceforge.net:/cvsroot/pycap co pycap
	% cd pycap/
	% python setup.py build
	% sudo python setup.py install

Details
-------
Note that libpcap_ and libnet_ may require superuser access to capture packets.

Example
-------

>>> import pycap.capture
>>> p = pycap.capture.capture()
>>> packet = p.next()
>>> packet
(Ethernet(type=0x800, 00:03:93:44:a9:92 -> 00:50:ba:8f:c4:5f), IP(proto=0x6, 192.168.0.235 -> 64.12.24.129),
 TCP(57579 -> 5190, seq=0xc1600e16, ack=0xf481e20e, flags=(push, ack)), '*\x05\x01\xff\x00\x00', 
 1046153559.33903)
>>> packet[0]
Ethernet(type=0x800, 00:03:93:44:a9:92 -> 00:50:ba:8f:c4:5f)
>>> dir(packet[0])
['__class__', '__delattr__', '__doc__', '__getattribute__', '__hash__', '__init__', '__new__', '__reduce__',
 '__reduce_ex__', '__repr__', '__setattr__', '__str__', 'destination', 'packet', 'source', 'type']
>>> packet[0].source
'00:03:93:44:a9:92'
>>> dir(packet[1])
['__class__', '__delattr__', '__doc__', '__getattribute__', '__hash__', '__init__', '__new__', '__reduce__',
 '__reduce_ex__', '__repr__', '__setattr__', '__str__', 'checksum', 'destination', 'headerlength', 'id',
 'length', 'offset', 'packet', 'protocol', 'source', 'timetolive', 'typeofservice', 'version']
>>> packet[1].version
4
>>> 


Reference
---------

`Reference documentation`_ is severely lacking.  If there is anything
that you would like an explantion on feel free to email me or file a
bug report on it.


.. _Python: http://www.python.org
.. _libpcap: http://www.tcpdump.org
.. _IP: http://www.faqs.org/rfcs/rfc791.html
.. _TCP: http://www.faqs.org/rfcs/rfc793.html
.. _UDP: http://www.faqs.org/rfcs/rfc768.html
.. _ICMP: http://www.faqs.org/rfcs/rfc792.html
.. _ARP: http://www.faqs.org/rfcs/rfc826.html
.. _Sourceforge project page: https://sourceforge.net/projects/pycap/
.. _separate page: http://pycap.sourceforge.net/todo.html
.. _provides: https://sourceforge.net/cvs/?group_id=73606
.. _Reference documentation: http://pycap.sourceforge.net/docs.html
.. _libnet: http://www.packetfactory.net/libnet/