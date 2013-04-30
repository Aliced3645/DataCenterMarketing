#ifndef __icmp_h__
#define __icmp_h__
#include "Python.h"
#undef _GNU_SOURCE
#include <libnet.h>
#include "ip.h"
#include <stdlib.h>
#include <sys/socket.h>
#include <netinet/in.h>
#include <arpa/inet.h>

typedef struct libnet_icmpv4_hdr icmp_t;

typedef struct {
    PyObject_HEAD
    unsigned char icmp_type;              /* type of message, see below */
    unsigned char icmp_code;              /* type sub code */
    unsigned short icmp_cksum;            /* ones complement cksum of struct */

    PyObject *pptr;
    PyObject *gwaddr;
    PyObject *id;
    PyObject *seq;

    PyObject *otime;
    PyObject *rtime;
    PyObject *ttime;

    PyObject *data;
} ICMPObject;

int initICMPType(PyObject *module_dict);
#endif