#ifndef __arp_h__
#define __arp_h__

#include "Python.h"
#undef _GNU_SOURCE
#include <libnet.h>
#include <stdlib.h>

#if defined(WIN32)
#include <winsock2.h>
#else
#include <sys/socket.h>
#include <netinet/in.h>
#include <arpa/inet.h>
#endif

typedef struct libnet_arp_hdr arp_t;

typedef struct {
    PyObject_HEAD
    unsigned short ar_hrd;
    unsigned short ar_pro;
    unsigned short ar_hln;
    unsigned short ar_pln;
    unsigned short ar_op;

    PyObject *ar_sha, *ar_spa, *ar_tha, *ar_tpa;

    PyObject *data;
} ARPObject;

extern PyTypeObject ARP_Type;
#define ARPObject_Check(v)      ((v)->ob_type == &ARP_Type)
int initARPType(PyObject *module_dict);

#endif
