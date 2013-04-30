#ifndef __udp_h__
#define __udp_h__
#include "Python.h"
#undef _GNU_SOURCE
#include <libnet.h>
#include <stdlib.h>
#include <sys/socket.h>
#include <netinet/in.h>
#include <arpa/inet.h>

typedef struct libnet_udp_hdr udp_t;

typedef struct {
    PyObject_HEAD
    unsigned short uh_sport;
    unsigned short uh_dport;
    unsigned short uh_ulen;
    unsigned short uh_sum;

    PyObject *data;
} UDPObject;

extern PyTypeObject UDP_Type;
#define UDPObject_Check(v)      ((v)->ob_type == &UDP_Type)
int initUDPType(PyObject *module_dict);
#endif
