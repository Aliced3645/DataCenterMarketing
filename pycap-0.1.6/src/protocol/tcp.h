#ifndef __tcp_h__
#define __tcp___
#include "Python.h"
#undef _GNU_SOURCE
#include <libnet.h>
#include <stdlib.h>
#include <sys/socket.h>
#include <netinet/in.h>
#include <arpa/inet.h>

typedef struct libnet_tcp_hdr tcp_t;

typedef struct {
    PyObject_HEAD
    unsigned short th_sport;               /* source port */
    unsigned short th_dport;               /* destination port */
    unsigned long th_seq;                  /* sequence number */
    unsigned long th_ack;                  /* acknowledgement number */
    unsigned char  th_x2;
    unsigned char th_off;                  /* data offset */
    unsigned char  th_flags;

    unsigned short th_win;                 /* window */
    unsigned short th_sum;                 /* checksum */
    unsigned short th_urp;                 /* urgent pointer */

    PyObject *data;

} TCPObject;

extern PyTypeObject TCP_Type;
#define TCPObject_Check(v)      ((v)->ob_type == &TCP_Type)

int initTCPType(PyObject *module_dict);
#endif