#include "Python.h"
#include "structmember.h"
#define PROTOCOL_MODULE
#include "protocolmodule.h"

extern PyObject *ErrorObject;

PyProtocol_newUDPObjectFromPacket_RETURN PyProtocol_newUDPObjectFromPacket PyProtocol_newUDPObjectFromPacket_PROTO;
PyProtocol_injectUDP_RETURN PyProtocol_injectUDP PyProtocol_injectUDP_PROTO;
PyProtocol_UDPCheck_RETURN PyProtocol_UDPCheck PyProtocol_UDPCheck_PROTO;

int PyProtocol_UDPCheck(PyObject *o)
{
    return UDPObject_Check(o);
}

/**************
*    UDP     *
**************/
UDPObject *
PyProtocol_newUDPObjectFromPacket(udp_t *udp, int *parsed_length)
{
    UDPObject *self;
    self = PyObject_New(UDPObject, &UDP_Type);
    if (self == NULL)
        return NULL;
    self->uh_sport = ntohs(udp->uh_sport);
    self->uh_dport = ntohs(udp->uh_dport);
    self->uh_ulen = ntohs(udp->uh_ulen);
    self->uh_sum = ntohs(udp->uh_sum);
    self->data = NULL;

    self->data = PyString_FromStringAndSize((char *) udp, sizeof(udp_t));
    if (! self->data)
    {
        Py_DECREF(self);
        return NULL;
    }
    *parsed_length += sizeof(udp_t);
    return self;
}

int PyProtocol_injectUDP(PyObject *udp_py, PyObject *payload_py, libnet_t *context)
{
    UDPObject *self = (UDPObject *) udp_py;
    char *payload = NULL;
    int payload_len = NULL;
    libnet_ptag_t r;
    PyString_AsStringAndSize(payload_py, &payload, &payload_len);
    r = libnet_build_udp(self->uh_sport,
                         self->uh_dport,
                         self->uh_ulen,
                         0,
                         payload,
                         payload_len,
                         context,
                         0);
    if (r == -1)
    {
        PyErr_SetString(ErrorObject, libnet_geterror(context));
        return 0;
    }
    return 1;
}

static void
UDPObject_dealloc(UDPObject *self)
{
    Py_XDECREF(self->data);
    PyObject_Del(self);
}

static PyObject *
UDPObject_str(UDPObject * self)
{
    PyObject *result = PyString_FromFormat("UDP(%d -> %d)", self->uh_sport, self->uh_dport);
    return result;
}

static PyObject *
UDPObject_new(PyTypeObject *type, PyObject *args, PyObject *kwds)
{
    int sourceport, destinationport, length;
    static char *kwlist[] = {"sourceport", "destinationport", "length", NULL};
    UDPObject *self;
    if (!PyArg_ParseTupleAndKeywords(args, kwds, "iii:new", kwlist,
                                     &sourceport, &destinationport,
                                     &length))
        return NULL;

    self = PyObject_New(UDPObject, &UDP_Type);
    if (self == NULL)
        return NULL;
    self->uh_sport = sourceport;
    self->uh_dport = destinationport;
    self->uh_ulen = length;
    self->uh_sum = 0;
    self->data = NULL;    

    return (PyObject *) self;
}


static PyMethodDef UDPObject_methods[] =
{
    {NULL, NULL}
};

static PyMemberDef UDPObject_members[] =
{
    {"sourceport", T_USHORT, offsetof(UDPObject, uh_sport), 0, "UDP source port"},
    {"destinationport", T_USHORT, offsetof(UDPObject, uh_dport), 0, "UDP destination port"},
    {"length", T_USHORT, offsetof(UDPObject, uh_ulen), 0, "UDP length"},
    {"checksum", T_USHORT, offsetof(UDPObject, uh_sum), 0, "UDP checksum"},
    {"packet", T_OBJECT_EX, offsetof(UDPObject, data), READONLY, "Raw packet data"},
    { NULL }
};


PyTypeObject UDP_Type =
{
    /* The ob_type field must be initialized in the module init function
    * to be portable to Windows without using C++. */
    PyObject_HEAD_INIT(NULL)
    0,                        /*ob_size*/
    "pycap.protocol.udp",               /*tp_name*/
    sizeof(UDPObject),        /*tp_basicsize*/
    0,                        /*tp_itemsize*/
    /* methods */
    (destructor)UDPObject_dealloc, /*tp_dealloc*/
    0,			      /*tp_print*/
    0,                        /*tp_getattr*/
    0,                        /*tp_setattr*/
    0,                        /*tp_compare*/
    (reprfunc)UDPObject_str,  /*tp_repr*/
    0,                        /*tp_as_number*/
    0,                        /*tp_as_sequence*/
    0,                        /*tp_as_mapping*/
    0,                        /*tp_hash*/
    0,                        /*tp_call*/
    (reprfunc)UDPObject_str,  /*tp_str*/
    0,                        /*tp_getattro*/
    0,                        /*tp_setattro*/
    0,                        /*tp_as_buffer*/
    Py_TPFLAGS_DEFAULT,       /*tp_flags*/
    "UDP packet",             /*tp_doc*/
    0,                        /*tp_traverse*/
    0,                        /*tp_clear*/
    0,                        /*tp_richcompare*/
    0,                        /*tp_weaklistoffset*/
    0,                        /*tp_iter*/
    0,                        /*tp_iternext*/
    UDPObject_methods,        /*tp_methods*/
    UDPObject_members,        /*tp_members*/
    0,                        /*tp_getset*/
    0,                        /*tp_base*/
    0,                        /*tp_dict*/
    0,                        /*tp_descr_get*/
    0,                        /*tp_descr_set*/
    0,                        /*tp_dictoffset*/
    0,                        /*tp_init*/
    0,                        /*tp_alloc*/
    UDPObject_new,            /*tp_new*/
    0,                        /*tp_free*/
    0,                        /*tp_is_gc*/
};

int initUDPType(PyObject *module_dict)
{
    if (PyType_Ready(&UDP_Type) < 0)
        return 1;

    PyDict_SetItemString(module_dict, "udp", (PyObject *) &UDP_Type);
    return 0;
}