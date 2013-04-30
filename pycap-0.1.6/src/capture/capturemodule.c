/* pcap objects */

#include "Python.h"
#include "structmember.h"
#include <pcap.h>
#include "../protocol/protocolmodule.h"

PyObject *ErrorObject;

typedef struct {
    PyObject_HEAD
    char *device;
    char *filename;
    pcap_t *pcap;
} captureObject;


static PyTypeObject capture_Type;

#define captureObject_Check(v)	((v)->ob_type == &capture_Type)

int parseIP(PyObject *result, struct pcap_pkthdr header,
            unsigned char *packet, int *parsed_length);
int parseARP(PyObject *result, struct pcap_pkthdr header,
             unsigned char *packet, int *parsed_length);


static captureObject *
newcaptureObjectDevice(char *device, int snaplen, int promisc, int timeout)
{
    char errbuf[PCAP_ERRBUF_SIZE];
    captureObject *self;

    self = PyObject_New(captureObject, &capture_Type);
    if (self == NULL)
        return NULL;

    if (! device)
    {
        device = pcap_lookupdev(errbuf);
        if (! device)
        {
            PyErr_SetString(ErrorObject, errbuf);
            return NULL;
        }
    }
    self->device = PyMem_New(char, strlen(device));
    strcpy(self->device, device);
    self->filename = NULL;
    self->pcap = pcap_open_live(device, snaplen, promisc, timeout, errbuf);        

    if (! self->pcap)
    {
        PyMem_Free(self->device);
        PyErr_SetString(ErrorObject, errbuf);
        return NULL;
    }
    return self;
}

static captureObject *
newcaptureObjectFile(char *filename)
{
    char errbuf[PCAP_ERRBUF_SIZE];
    captureObject *self;

    self = PyObject_New(captureObject, &capture_Type);
    if (self == NULL)
        return NULL;

    self->device = NULL;
    self->filename = PyMem_New(char, strlen(filename));
    strcpy(self->filename, filename);
    self->pcap = pcap_open_offline(filename, errbuf);

    if (! self->pcap)
    {
        PyMem_Free(self->device);
        PyErr_SetString(ErrorObject, errbuf);
        return NULL;
    }
    return self;
}


/* pcap methods */

static void
captureObject_dealloc(captureObject *self)
{
    pcap_close(self->pcap);
    PyMem_Free(self->device);
    if (self->filename)
        PyMem_Free(self->filename);
    PyObject_Del(self);
}

PyObject *
captureObject_next(captureObject *self)
{
    u_char *packet = NULL;
    struct pcap_pkthdr header;
    PyObject *ethernet = NULL;
    PyObject *result = NULL;
    PyObject *resultTuple = NULL;
    ethernet_t *eth_header = NULL;
    char buffer[255];
    double packetTime;
    int packet_offset = 0;
    PyObject *remaining = NULL;
    int offset[5] = {
        0, 0, 0, 0, 0
    };
    

    packet = (u_char *) pcap_next(self->pcap, &header);
    if (! packet)
    {
        Py_INCREF(Py_None);
        return Py_None;

        //        PyErr_SetString(ErrorObject, "No data available before timeout");
        //        return NULL;
    }
    sprintf(buffer, "%ld.%ld", (long) header.ts.tv_sec, (long) header.ts.tv_usec);
    packetTime = strtod(buffer, NULL);
    result = PyList_New(0);
    switch (pcap_datalink(self->pcap))
    {
        case DLT_EN10MB:
        {
            eth_header = (ethernet_t *) packet;
            ethernet = (PyObject *) PyProtocol_newEthernetObjectFromPacket(eth_header, &packet_offset);
            offset[0] = packet_offset;
            PyList_Append(result, ethernet);
            Py_DECREF(ethernet);

            switch (ntohs(eth_header->ether_type))
            {
                case ETHERTYPE_IP:
                {
                    if (! parseIP(result, header, packet, &packet_offset))
                    {
                        Py_DECREF(result);
                        return NULL;
                    }
                    offset[1] = packet_offset;
                    break;
                }
                case ETHERTYPE_ARP:
                    if (! parseARP(result, header, packet, &packet_offset))
                    {
                        Py_DECREF(result);
                        return NULL;
                    }
                    offset[2] = packet_offset;
                    break;
                default:
                {
                    
                }
            }
            break;
        }
        case DLT_NULL:
        {
            packet_offset = 4;
            if (! parseIP(result, header, packet, &packet_offset))
            {
                Py_DECREF(result);
                return NULL;
            }
            offset[3] = packet_offset;
            break;
        }
    }
    if ((int) (header.len) - packet_offset < 0)
    {
        Py_DECREF(result);
        PyErr_Format(ErrorObject, "Parsed parsed end of packet (%d %d %d %d)",
                        offset[0], offset[1], offset[2], offset[3]);
        return NULL;
    }
    remaining = PyString_FromStringAndSize((char *) (packet + packet_offset),
                                                     header.len - packet_offset);
    PyList_Append(result, remaining);
    Py_DECREF(remaining);
    
    
    PyList_Append(result, PyFloat_FromDouble(packetTime));
    resultTuple = PyList_AsTuple(result);
    Py_DECREF(result);
    return resultTuple;
}

PyObject *
captureObject_stats(captureObject *self, PyObject *args)
{
    struct pcap_stat stats;   
    
    if (! PyArg_ParseTuple(args, ":stats"))
        return NULL;

    if (pcap_stats(self->pcap, &stats))
    {
        PyErr_SetString(ErrorObject, pcap_geterr(self->pcap));
        return NULL;
    }

    return Py_BuildValue("iii", stats.ps_recv, stats.ps_drop, stats.ps_ifdrop);
}

PyObject *
captureObject_datalink(captureObject *self, PyObject *args)
{
    if (! PyArg_ParseTuple(args, ":datalink"))
        return NULL;

    return Py_BuildValue("i", pcap_datalink(self->pcap));
}

PyObject *
captureObject_filter(captureObject *self, PyObject *args)
{
    char *filter = NULL;
    int optimize = 0;
    unsigned int netmask = 0xffffffff;
    struct bpf_program prog;
    
    if (! PyArg_ParseTuple(args, "s|i:filter", &filter, &optimize))
        return NULL;

    if (pcap_compile(self->pcap, &prog, filter, optimize, netmask))
    {
        PyErr_SetString(ErrorObject, pcap_geterr(self->pcap));
        return NULL;
    }
    if (pcap_setfilter(self->pcap, &prog))
    {
        pcap_freecode(&prog);
        PyErr_SetString(ErrorObject, pcap_geterr(self->pcap));
        return NULL;
    }
    pcap_freecode(&prog);
    return Py_BuildValue("");
}

static PyObject *
captureObject_fromFile(PyTypeObject *class, PyObject *args)
{
    captureObject *rv;
    char *filename = NULL;

    if (!PyArg_ParseTuple(args, "s:fromFile", &filename))
        return NULL;

    rv = newcaptureObjectFile(filename);
    
    if (rv == NULL)
        return NULL;
    return (PyObject *)rv;
}

static PyMethodDef captureObject_methods[] =
{
    {"next", (PyCFunction) captureObject_next, METH_NOARGS, "Return next packet"},
    {"stats", (PyCFunction) captureObject_stats, METH_VARARGS, "Return statistics"},
    {"datalink", (PyCFunction) captureObject_datalink, METH_VARARGS, "Return the link type"},
    {"filter", (PyCFunction) captureObject_filter, METH_VARARGS, "Filter out certain packets"},
    {"fromFile", (PyCFunction) captureObject_fromFile, METH_VARARGS | METH_CLASS, "Dump packets from file as opposed to network interface."},
    {NULL, NULL}
};

static PyMemberDef captureObject_members[] =
{
    {"device", T_STRING, offsetof(captureObject, device), READONLY, "The device that libpcap should use."},
    {"filename", T_STRING, offsetof(captureObject, filename), READONLY, "The filename that libpcap is dumping from."},
    { NULL }
};

static PyObject *
captureObject_new(PyTypeObject *type, PyObject *args, PyObject *kwds)
{
    captureObject *rv;
    char *device = NULL;
    unsigned int snaplen = 65535;
    int promisc = 0;
    int timeout = 0x000000ff;
    static char *kwlist[] = {"device", "snaplen", "promisc", "timeout", NULL};

    if (!PyArg_ParseTupleAndKeywords(args, kwds, "|siii:new", kwlist,
                                     &device, &snaplen, &promisc, &timeout))
        return NULL;

    rv = newcaptureObjectDevice(device, snaplen, promisc, timeout);
    if (rv == NULL)
        return NULL;
    return (PyObject *)rv;
}


static PyTypeObject capture_Type =
{
    /* The ob_type field must be initialized in the module init function
    * to be portable to Windows without using C++. */
    PyObject_HEAD_INIT(NULL)
    0,                        /*ob_size*/
    "pycap.capture.capture",            /*tp_name*/
    sizeof(captureObject),      /*tp_basicsize*/
    0,                        /*tp_itemsize*/
    /* methods */
    (destructor)captureObject_dealloc, /*tp_dealloc*/
    0,			      /*tp_print*/
    0,                        /*tp_getattr*/
    0,                        /*tp_setattr*/
    0,                        /*tp_compare*/
    0,                        /*tp_repr*/
    0,                        /*tp_as_number*/
    0,                        /*tp_as_sequence*/
    0,                        /*tp_as_mapping*/
    0,                        /*tp_hash*/
    0,                        /*tp_call*/
    0,                        /*tp_str*/
    0,                        /*tp_getattro*/
    0,                        /*tp_setattro*/
    0,                        /*tp_as_buffer*/
    Py_TPFLAGS_DEFAULT,       /*tp_flags*/
    "Capture packets from a network interface",    /*tp_doc*/
    0,                        /*tp_traverse*/
    0,                        /*tp_clear*/
    0,                        /*tp_richcompare*/
    0,                        /*tp_weaklistoffset*/
    PyObject_SelfIter,        /*tp_iter*/
    (iternextfunc) captureObject_next, /*tp_iternext*/
    captureObject_methods,       /*tp_methods*/
    captureObject_members,       /*tp_members*/
    0,                        /*tp_getset*/
    0,                        /*tp_base*/
    0,                        /*tp_dict*/
    0,                        /*tp_descr_get*/
    0,                        /*tp_descr_set*/
    0,                        /*tp_dictoffset*/
    0,                        /*tp_init*/
    0,                        /*tp_alloc*/
    captureObject_new,           /*tp_new*/
    0,                        /*tp_free*/
    0,                        /*tp_is_gc*/
};


int parseIP(PyObject *result, struct pcap_pkthdr header,
            unsigned char *packet, int *parsed_length)
{
    PyObject *py_ip = NULL;
    ip_t *ip_header = (ip_t *) (packet + *parsed_length);
    py_ip = (PyObject *) PyProtocol_newIPObjectFromPacket(&header, ip_header, parsed_length);
    if (! py_ip)
        return 0;
    PyList_Append(result, py_ip);
    Py_DECREF(py_ip);
    switch (ip_header->ip_p)
    {
        case IPPROTO_TCP:
        {
            PyObject *tcp = NULL;
            tcp_t *tcp_header = (tcp_t *) (packet + *parsed_length);
            tcp = (PyObject *) PyProtocol_newTCPObjectFromPacket(tcp_header, parsed_length);
            if (! tcp)
                return 0;
            PyList_Append(result, tcp);
            Py_DECREF(tcp);
            break;
        }
        case IPPROTO_UDP:
        {
            PyObject *udp = NULL;
            udp_t *udp_header = (udp_t *) (packet + *parsed_length);
            udp = (PyObject *) PyProtocol_newUDPObjectFromPacket(udp_header, parsed_length);
            if (! udp)
                return 0;
            PyList_Append(result, udp);
            Py_DECREF(udp);
            break;
        }
        case IPPROTO_ICMP:
        {
            PyObject *py_icmp = NULL;
            icmp_t *icmp_header = (icmp_t *) (packet + *parsed_length);
            py_icmp = (PyObject *) PyProtocol_newICMPObjectFromPacket(icmp_header, parsed_length);
            if (! py_icmp)
                return 0;
            PyList_Append(result, py_icmp);
            Py_DECREF(py_icmp);
            break;
        }
        default:
        {
        }
    }
    return 1;
}

int parseARP(PyObject *result, struct pcap_pkthdr header,
            unsigned char *packet, int *parsed_length)
{
    PyObject *arp = NULL;
    arp_t *arp_header = (arp_t *) (packet + *parsed_length);
    arp = (PyObject *) PyProtocol_newARPObjectFromPacket(arp_header, parsed_length);
    if (! arp)
        return 0;
    PyList_Append(result, arp);
    Py_DECREF(arp);
    return 1;
}

/* List of functions defined in the module */

static PyMethodDef capture_methods[] = {
    {NULL, NULL}		/* sentinel */
};

static char module_doc[] = "Captures packets from a network interface and disects common structures in them.";

/* Initialization function for the module (*must* be called initpycap) */
#ifndef PyMODINIT_FUNC
#       if defined(__cplusplus)
#               define PyMODINIT_FUNC extern "C" void
#       else /* __cplusplus */
#               define PyMODINIT_FUNC void
#       endif /* __cplusplus */
#endif

PyMODINIT_FUNC initcapture(void)
{
    PyObject *m;
    PyObject *dict;

    /* Finalize the type object including setting type of the new type
    * object; doing it here is required for portability to Windows
    * without requiring C++. */
    if (PyType_Ready(&capture_Type) < 0)
        return;

    /* Create the module and add the functions */
    m = Py_InitModule3("capture", capture_methods, module_doc);

    if (import_protocol() < 0)
        return;

    /* Add some symbolic constants to the module */
    if (ErrorObject == NULL) {
        ErrorObject = PyErr_NewException("pycap.capture.error", NULL, NULL);
        if (ErrorObject == NULL)
            return;
    }
    Py_INCREF(ErrorObject);
    PyModule_AddObject(m, "error", ErrorObject);
    dict = PyModule_GetDict(m);
    PyDict_SetItemString(dict, "capture", (PyObject *) &capture_Type);
}
