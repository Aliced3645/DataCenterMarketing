#include "Python.h"
#include "structmember.h"
#define PROTOCOL_MODULE
#include "protocolmodule.h"

PyProtocol_newARPObjectFromPacket_RETURN PyProtocol_newARPObjectFromPacket PyProtocol_newARPObjectFromPacket_PROTO;
PyProtocol_injectARP_RETURN PyProtocol_injectARP PyProtocol_injectARP_PROTO;
PyProtocol_ARPCheck_RETURN PyProtocol_ARPCheck PyProtocol_ARPCheck_PROTO;

int PyProtocol_ARPCheck(PyObject *o)
{
    return ARPObject_Check(o);
}


/**************
*    ARP     *
**************/
ARPObject *
PyProtocol_newARPObjectFromPacket(arp_t *arp, int *parsed_length)
{
    ARPObject *self;
    char *packet = (char *) (arp + 1);
    struct in_addr ia;
    self = PyObject_New(ARPObject, &ARP_Type);
    if (self == NULL)
        return NULL;
    self->ar_hrd = ntohs(arp->ar_hrd);
    self->ar_pro = ntohs(arp->ar_pro);
    self->ar_hln = arp->ar_hln;
    self->ar_pln = arp->ar_pln;
    self->ar_op = ntohs(arp->ar_op);
    self->ar_spa = NULL;
    self->ar_tpa = NULL;
    self->ar_sha = NULL;
    self->ar_tha = NULL;
    self->data = NULL;

    if (self->ar_pro == ETHERTYPE_IP)
    {
        memcpy(&ia, packet + self->ar_hln, sizeof(ia));
        self->ar_spa = PyString_FromString(inet_ntoa(ia));
        memcpy(&ia, packet + (2 * self->ar_hln) + self->ar_pln, sizeof(ia));
        self->ar_tpa = PyString_FromString(inet_ntoa(ia));
    }
    else
    {
        PyErr_SetString(PyExc_NotImplementedError, "Only support decoding IPv4 ARP packets");
        return NULL;
    }
    
    if (self->ar_hrd == ARPHRD_ETHER)
    {
        self->ar_sha = MACasString(packet);
        self->ar_tha = MACasString(packet + self->ar_hln + self->ar_pln);
    }
    else
    {
        Py_XDECREF(self->ar_spa);
        Py_XDECREF(self->ar_tpa);
        PyErr_SetString(PyExc_NotImplementedError, "Only support decoding Ethernet ARP packets");
        return NULL;
    }

    self->data = PyString_FromStringAndSize((char *) arp, (sizeof(arp_t) + (2 * self->ar_hln) + (2 * self->ar_pln)));

    *parsed_length += (sizeof(arp_t) + (2 * self->ar_hln) + (2 * self->ar_pln));
    return self;
}

static PyObject *
ARPObject_new(PyTypeObject *type, PyObject *args, PyObject *kwds)
{
    static char *kwargs[] = {"sourcehardware", "targethardware",
        "sourceprotocol", "targetprotocol",
        "operation", NULL };
    PyObject *sourcehardware, *targethardware;
    PyObject *sourceprotocol, *targetprotocol;
    int operation;
    ARPObject *self;
    char *temp;
    
    if (!PyArg_ParseTupleAndKeywords(args, kwds, "SSSSi", kwargs, &sourcehardware, &targethardware,
                                     &sourceprotocol, &targetprotocol, &operation))
        return NULL;
    self = PyObject_New(ARPObject, &ARP_Type);
    if (! self)
        return NULL;

    temp = decodeMAC(sourcehardware);
    if (! temp)
    {
        PyErr_SetString(PyExc_TypeError, "Invalid format for source MAC address");
        return NULL;
    }
    else
        free(temp);

    temp = decodeMAC(targethardware);
    if (! temp)
    {
        PyErr_SetString(PyExc_TypeError, "Invalid format for destination MAC address");
        return NULL;
    }
    else
        free(temp);
    

    self->ar_hrd = ARPHRD_ETHER;
    self->ar_pro = ETHERTYPE_IP;
    self->ar_hln = ETHER_ADDR_LEN;
    self->ar_pln = 4;
    self->ar_op = operation;
    self->data = NULL;

    Py_INCREF(sourcehardware);
    Py_INCREF(targethardware);
    Py_INCREF(sourceprotocol);
    Py_INCREF(targetprotocol);
    self->ar_sha = sourcehardware;
    self->ar_tha = targethardware;
    self->ar_spa = sourceprotocol;
    self->ar_tpa = targetprotocol;
    
    return (PyObject *) self;
}

int
PyProtocol_injectARP(PyObject *arp_py, libnet_t *context)
{
    ARPObject *self = (ARPObject *) arp_py;
    char *sha = decodeMAC(self->ar_sha);
    char *tha = decodeMAC(self->ar_tha);
    struct in_addr spa, tpa;
    libnet_ptag_t r;
    inet_aton(PyString_AsString(self->ar_spa), &spa);
    inet_aton(PyString_AsString(self->ar_tpa), &tpa);
    r = libnet_build_arp(self->ar_hrd,
                         self->ar_pro,
                         self->ar_hln,
                         self->ar_pln,
                         self->ar_op,
                         sha,           // Source hardware address
                         (char *) &spa, // Target hardware address
                         tha,           // Source protocol address
                         (char *) &tpa, // Target protocol address
                         NULL,
                         0,
                         context,
                         0);
    free(sha);
    free(tha);

    if (r == -1)
    {
        PyErr_SetString(ErrorObject, libnet_geterror(context));
        return 0;
    }
    return 1;
}

static void
ARPObject_dealloc(ARPObject *self)
{
    Py_XDECREF(self->ar_sha);
    Py_XDECREF(self->ar_tha);
    Py_XDECREF(self->ar_spa);
    Py_XDECREF(self->ar_tpa);
    Py_XDECREF(self->data);
    PyObject_Del(self);
}

static PyObject *
ARPObject_str(ARPObject * self)
{
    PyObject *result = PyString_FromFormat("ARP(op=0x%04x, protocol=0x%04x, %s (%s) -> %s (%s))",
                                           self->ar_op, self->ar_pro,
                                           PyString_AsString(self->ar_sha),
                                           PyString_AsString(self->ar_spa),
                                           PyString_AsString(self->ar_tha),
                                           PyString_AsString(self->ar_tpa));
    return result;
}



static PyMethodDef ARPObject_methods[] =
{
    {NULL, NULL}
};

static PyMemberDef ARPObject_members[] =
{
    {"protocol", T_USHORT, offsetof(ARPObject, ar_pro), 0, "ARP requested protocol"},
    {"operation", T_USHORT, offsetof(ARPObject, ar_op), 0, "ARP operation"},
    {"hardwarelength", T_USHORT, offsetof(ARPObject, ar_hln), 0, "ARP hardware address length"},
    {"protocollength", T_USHORT, offsetof(ARPObject, ar_pln), 0, "ARP protocol address length"},
    {"hardwareformat", T_USHORT, offsetof(ARPObject, ar_hrd), 0, "ARP hardware type"},
    {"sourcehardware", T_OBJECT, offsetof(ARPObject, ar_sha), 0, "ARP source hardware address"},
    {"targethardware", T_OBJECT, offsetof(ARPObject, ar_tha), 0, "ARP target hardware address"},
    {"sourceprotocol", T_OBJECT, offsetof(ARPObject, ar_spa), 0, "ARP source protocol address"},
    {"targetprotocol", T_OBJECT, offsetof(ARPObject, ar_tpa), 0, "ARP target protocol address"},

    {"packet", T_OBJECT, offsetof(ARPObject, data), READONLY, "Raw packet data"},
    { NULL }
};


PyTypeObject ARP_Type =
{
    /* The ob_type field must be initialized in the module init function
    * to be portable to Windows without using C++. */
    PyObject_HEAD_INIT(NULL)
    0,                        /*ob_size*/
    "pycap.protocol.arp",               /*tp_name*/
    sizeof(ARPObject),        /*tp_basicsize*/
    0,                        /*tp_itemsize*/
    /* methods */
    (destructor)ARPObject_dealloc, /*tp_dealloc*/
    0,			      /*tp_print*/
    0,                        /*tp_getattr*/
    0,                        /*tp_setattr*/
    0,                        /*tp_compare*/
    (reprfunc)ARPObject_str,  /*tp_repr*/
    0,                        /*tp_as_number*/
    0,                        /*tp_as_sequence*/
    0,                        /*tp_as_mapping*/
    0,                        /*tp_hash*/
    0,                        /*tp_call*/
    (reprfunc)ARPObject_str,  /*tp_str*/
    0,                        /*tp_getattro*/
    0,                        /*tp_setattro*/
    0,                        /*tp_as_buffer*/
    Py_TPFLAGS_DEFAULT,       /*tp_flags*/
    "ARP packet",             /*tp_doc*/
    0,                        /*tp_traverse*/
    0,                        /*tp_clear*/
    0,                        /*tp_richcompare*/
    0,                        /*tp_weaklistoffset*/
    0,                        /*tp_iter*/
    0,                        /*tp_iternext*/
    ARPObject_methods,        /*tp_methods*/
    ARPObject_members,        /*tp_members*/
    0,                        /*tp_getset*/
    0,                        /*tp_base*/
    0,                        /*tp_dict*/
    0,                        /*tp_descr_get*/
    0,                        /*tp_descr_set*/
    0,                        /*tp_dictoffset*/
    0,                        /*tp_init*/
    0,                        /*tp_alloc*/
    ARPObject_new,            /*tp_new*/
    0,                        /*tp_free*/
    0,                        /*tp_is_gc*/
};

int initARPType(PyObject *module_dict)
{
    if (PyType_Ready(&ARP_Type) < 0)
        return 1;

    PyDict_SetItemString(module_dict, "arp", (PyObject *) &ARP_Type);
    return 0;
}