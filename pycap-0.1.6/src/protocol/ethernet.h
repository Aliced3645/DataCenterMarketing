#ifndef __ethernet_h__
#define __ethernet_h__
#include "Python.h"
#undef _GNU_SOURCE
#include <libnet.h>
#include <sys/socket.h>
#include <netinet/in.h>
#include <arpa/inet.h>


typedef struct libnet_ethernet_hdr ethernet_t;

typedef struct
{
    PyObject_HEAD
    PyObject *ether_dhost;
    PyObject *ether_shost;
    u_short ether_type;

    PyObject *data;
} ethernetObject;

extern PyTypeObject ethernet_Type;
#define ethernetObject_Check(v) ((v)->ob_type == &ethernet_Type)

int initEthernetType(PyObject *module_dict);

PyObject *MACasString(char *);
char *decodeMAC(PyObject *str);
#endif