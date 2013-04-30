#ifndef __ip_h__
#define __ip_h__
#include "Python.h"
#include <stdlib.h>
#include <sys/socket.h>
#include <netinet/in.h>
#include <arpa/inet.h>
#include <pcap.h>
#undef _GNU_SOURCE
#include <libnet.h>

typedef struct libnet_ipv4_hdr ip_t;

typedef struct {
    PyObject_HEAD
    unsigned char ip_v;
    unsigned char ip_hl;
    unsigned char ip_tos;
    unsigned short ip_len;
    unsigned short ip_id;
    unsigned short ip_off;
    unsigned char ip_ttl;
    unsigned char ip_p;
    unsigned short ip_sum;
    PyObject *ip_src, *ip_dst;

    PyObject *data;
} IPObject;

extern PyTypeObject IP_Type;
#define IPObject_Check(v)       ((v)->ob_type == &IP_Type)

int initIPType(PyObject *module_dict);

#endif