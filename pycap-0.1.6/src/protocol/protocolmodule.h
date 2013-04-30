#ifndef __protocolmodule_h__
#define __protocolmodule_h__
#ifdef __cplusplus
extern "C" {
#endif
    
#include "arp.h"
#include "ethernet.h"
#include "icmp.h"
#include "ip.h"
#include "tcp.h"
#include "udp.h"
#undef _GNU_SOURCE
#include <libnet.h>

    /* Header file for protocol */

    /* C API functions */
#define PyProtocol_newARPObjectFromPacket_NUM 0
#define PyProtocol_newARPObjectFromPacket_RETURN ARPObject *
#define PyProtocol_newARPObjectFromPacket_PROTO (arp_t *arp, int *parsed_length)

#define PyProtocol_injectARP_NUM 1
#define PyProtocol_injectARP_RETURN int
#define PyProtocol_injectARP_PROTO (PyObject *arp, libnet_t *context)

#define PyProtocol_ARPCheck_NUM 2
#define PyProtocol_ARPCheck_RETURN int
#define PyProtocol_ARPCheck_PROTO (PyObject *o)
    

#define PyProtocol_newEthernetObjectFromPacket_NUM 3
#define PyProtocol_newEthernetObjectFromPacket_RETURN ethernetObject *
#define PyProtocol_newEthernetObjectFromPacket_PROTO (ethernet_t *ether, int *parsed_length)

#define PyProtocol_injectEthernet_NUM 4
#define PyProtocol_injectEthernet_RETURN int
#define PyProtocol_injectEthernet_PROTO (PyObject *ethernet, libnet_t *context)
    
#define PyProtocol_EthernetCheck_NUM 5
#define PyProtocol_EthernetCheck_RETURN int
#define PyProtocol_EthernetCheck_PROTO (PyObject *o)
    

#define PyProtocol_newICMPObjectFromPacket_NUM 6
#define PyProtocol_newICMPObjectFromPacket_RETURN ICMPObject *
#define PyProtocol_newICMPObjectFromPacket_PROTO (icmp_t *icmp_hdr, int *parsed_length)

#define PyProtocol_injectICMP_NUM 7
#define PyProtocol_injectICMP_RETURN int
#define PyProtocol_injectICMP_PROTO (PyObject *icmp, PyObject *payload, libnet_t *context)

#define PyProtocol_ICMPCheck_NUM 8
#define PyProtocol_ICMPCheck_RETURN int
#define PyProtocol_ICMPCheck_PROTO (PyObject *o)    
    

#define PyProtocol_newIPObjectFromPacket_NUM 9
#define PyProtocol_newIPObjectFromPacket_RETURN IPObject *
#define PyProtocol_newIPObjectFromPacket_PROTO (const struct pcap_pkthdr *pkthdr, const ip_t *ip, int *parsed_length)

#define PyProtocol_injectIP_NUM 10
#define PyProtocol_injectIP_RETURN int
#define PyProtocol_injectIP_PROTO (PyObject *ip, libnet_t *context)

#define PyProtocol_IPCheck_NUM 11
#define PyProtocol_IPCheck_RETURN int
#define PyProtocol_IPCheck_PROTO (PyObject *o)
    
    
#define PyProtocol_newTCPObjectFromPacket_NUM 12
#define PyProtocol_newTCPObjectFromPacket_RETURN TCPObject *
#define PyProtocol_newTCPObjectFromPacket_PROTO (tcp_t *tcp, int *parsed_length)

#define PyProtocol_injectTCP_NUM 13
#define PyProtocol_injectTCP_RETURN int
#define PyProtocol_injectTCP_PROTO (PyObject *tcp, PyObject *payload, libnet_t *context)

#define PyProtocol_TCPCheck_NUM 14
#define PyProtocol_TCPCheck_RETURN int
#define PyProtocol_TCPCheck_PROTO (PyObject *o)
    

#define PyProtocol_newUDPObjectFromPacket_NUM 15
#define PyProtocol_newUDPObjectFromPacket_RETURN UDPObject *
#define PyProtocol_newUDPObjectFromPacket_PROTO (udp_t *udp, int *parsed_length)

#define PyProtocol_injectUDP_NUM 16
#define PyProtocol_injectUDP_RETURN int
#define PyProtocol_injectUDP_PROTO (PyObject *udp, PyObject *payload, libnet_t *context)

#define PyProtocol_UDPCheck_NUM 17
#define PyProtocol_UDPCheck_RETURN int
#define PyProtocol_UDPCheck_PROTO (PyObject *o)
    
    
    
    
    /* Total number of C API pointers */
#define PyProtocol_API_pointers 18


#ifdef PROTOCOL_MODULE
    /* This section is used when compiling protocolmodule.c */
    extern PyObject *ErrorObject;

    extern PyProtocol_newEthernetObjectFromPacket_RETURN PyProtocol_newEthernetObjectFromPacket PyProtocol_newEthernetObjectFromPacket_PROTO;
    extern PyProtocol_injectEthernet_RETURN PyProtocol_injectEthernet PyProtocol_injectEthernet_PROTO;
    extern PyProtocol_EthernetCheck_RETURN PyProtocol_EthernetCheck PyProtocol_EthernetCheck_PROTO;
    
    extern PyProtocol_newARPObjectFromPacket_RETURN PyProtocol_newARPObjectFromPacket PyProtocol_newARPObjectFromPacket_PROTO;
    extern PyProtocol_injectARP_RETURN PyProtocol_injectARP PyProtocol_injectARP_PROTO;
    extern PyProtocol_ARPCheck_RETURN PyProtocol_ARPCheck PyProtocol_ARPCheck_PROTO;

    extern PyProtocol_newIPObjectFromPacket_RETURN PyProtocol_newIPObjectFromPacket PyProtocol_newIPObjectFromPacket_PROTO;
    extern PyProtocol_injectIP_RETURN PyProtocol_injectIP PyProtocol_injectIP_PROTO;
    extern PyProtocol_IPCheck_RETURN PyProtocol_IPCheck PyProtocol_IPCheck_PROTO;
    
    extern PyProtocol_newICMPObjectFromPacket_RETURN PyProtocol_newICMPObjectFromPacket PyProtocol_newICMPObjectFromPacket_PROTO;
    extern PyProtocol_injectICMP_RETURN PyProtocol_injectICMP PyProtocol_injectICMP_PROTO;
    extern PyProtocol_ICMPCheck_RETURN PyProtocol_ICMPCheck PyProtocol_ICMPCheck_PROTO;
    
    extern PyProtocol_newTCPObjectFromPacket_RETURN PyProtocol_newTCPObjectFromPacket PyProtocol_newTCPObjectFromPacket_PROTO;
    extern PyProtocol_injectTCP_RETURN PyProtocol_injectTCP PyProtocol_injectTCP_PROTO;
    extern PyProtocol_TCPCheck_RETURN PyProtocol_TCPCheck PyProtocol_TCPCheck_PROTO;

    extern PyProtocol_newUDPObjectFromPacket_RETURN PyProtocol_newUDPObjectFromPacket PyProtocol_newUDPObjectFromPacket_PROTO;
    extern PyProtocol_injectUDP_RETURN PyProtocol_injectUDP PyProtocol_injectUDP_PROTO;
    extern PyProtocol_UDPCheck_RETURN PyProtocol_UDPCheck PyProtocol_UDPCheck_PROTO;



#else
    /* This section is used in modules that use protocolmodule's API */

    static void **PyProtocol_API;

#define PyProtocol_newARPObjectFromPacket \
    (*(PyProtocol_newARPObjectFromPacket_RETURN (*)PyProtocol_newARPObjectFromPacket_PROTO) PyProtocol_API[PyProtocol_newARPObjectFromPacket_NUM])
#define PyProtocol_injectARP \
        (*(PyProtocol_injectARP_RETURN (*)PyProtocol_injectARP_PROTO) PyProtocol_API[PyProtocol_injectARP_NUM])
#define PyProtocol_ARPCheck \
        (*(PyProtocol_ARPCheck_RETURN (*)PyProtocol_ARPCheck_PROTO) PyProtocol_API[PyProtocol_ARPCheck_NUM])

        
#define PyProtocol_newEthernetObjectFromPacket \
        (*(PyProtocol_newEthernetObjectFromPacket_RETURN (*)PyProtocol_newEthernetObjectFromPacket_PROTO) PyProtocol_API[PyProtocol_newEthernetObjectFromPacket_NUM])
#define PyProtocol_injectEthernet \
        (*(PyProtocol_injectEthernet_RETURN (*)PyProtocol_injectEthernet_PROTO) PyProtocol_API[PyProtocol_injectEthernet_NUM])
#define PyProtocol_EthernetCheck \
        (*(PyProtocol_EthernetCheck_RETURN (*)PyProtocol_EthernetCheck_PROTO) PyProtocol_API[PyProtocol_EthernetCheck_NUM])


#define PyProtocol_newICMPObjectFromPacket \
        (*(PyProtocol_newICMPObjectFromPacket_RETURN (*)PyProtocol_newICMPObjectFromPacket_PROTO) PyProtocol_API[PyProtocol_newICMPObjectFromPacket_NUM])
#define PyProtocol_injectICMP \
        (*(PyProtocol_injectICMP_RETURN (*)PyProtocol_injectICMP_PROTO) PyProtocol_API[PyProtocol_injectICMP_NUM])
#define PyProtocol_ICMPCheck \
        (*(PyProtocol_ICMPCheck_RETURN (*)PyProtocol_ICMPCheck_PROTO) PyProtocol_API[PyProtocol_ICMPCheck_NUM])


#define PyProtocol_newIPObjectFromPacket \
        (*(PyProtocol_newIPObjectFromPacket_RETURN (*)PyProtocol_newIPObjectFromPacket_PROTO) PyProtocol_API[PyProtocol_newIPObjectFromPacket_NUM])
#define PyProtocol_injectIP \
        (*(PyProtocol_injectIP_RETURN (*)PyProtocol_injectIP_PROTO) PyProtocol_API[PyProtocol_injectIP_NUM])
#define PyProtocol_IPCheck \
        (*(PyProtocol_IPCheck_RETURN (*)PyProtocol_IPCheck_PROTO) PyProtocol_API[PyProtocol_IPCheck_NUM])

        
#define PyProtocol_newTCPObjectFromPacket \
        (*(PyProtocol_newTCPObjectFromPacket_RETURN (*)PyProtocol_newTCPObjectFromPacket_PROTO) PyProtocol_API[PyProtocol_newTCPObjectFromPacket_NUM])
#define PyProtocol_injectTCP \
        (*(PyProtocol_injectTCP_RETURN (*)PyProtocol_injectTCP_PROTO) PyProtocol_API[PyProtocol_injectTCP_NUM])
#define PyProtocol_TCPCheck \
        (*(PyProtocol_TCPCheck_RETURN (*)PyProtocol_TCPCheck_PROTO) PyProtocol_API[PyProtocol_TCPCheck_NUM])


#define PyProtocol_newUDPObjectFromPacket \
        (*(PyProtocol_newUDPObjectFromPacket_RETURN (*)PyProtocol_newUDPObjectFromPacket_PROTO) PyProtocol_API[PyProtocol_newUDPObjectFromPacket_NUM])
#define PyProtocol_injectUDP \
        (*(PyProtocol_injectUDP_RETURN (*)PyProtocol_injectUDP_PROTO) PyProtocol_API[PyProtocol_injectUDP_NUM])
#define PyProtocol_UDPCheck \
        (*(PyProtocol_UDPCheck_RETURN (*)PyProtocol_UDPCheck_PROTO) PyProtocol_API[PyProtocol_UDPCheck_NUM])


        /* Return -1 and set exception on error, 0 on success. */
        static int
        import_protocol(void)
    {
            PyObject *module = PyImport_ImportModule("pycap.protocol");
            
            if (module != NULL) {
                PyObject *c_api_object = PyObject_GetAttrString(module, "_C_API");
                if (c_api_object == NULL)
                    return -1;
                if (PyCObject_Check(c_api_object))
                    PyProtocol_API = (void **)PyCObject_AsVoidPtr(c_api_object);
                Py_DECREF(c_api_object);
            }
            return 0;
    }

#endif

#ifdef __cplusplus
}
#endif

#endif /* !defined(__protocolmodule_h__) */