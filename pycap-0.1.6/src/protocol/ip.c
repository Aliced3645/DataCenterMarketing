#include "Python.h"
#include "structmember.h"
#define PROTOCOL_MODULE
#include "protocolmodule.h"

extern PyObject *ErrorObject;

PyProtocol_newIPObjectFromPacket_RETURN PyProtocol_newIPObjectFromPacket PyProtocol_newIPObjectFromPacket_PROTO;
PyProtocol_injectIP_RETURN PyProtocol_injectIP PyProtocol_injectIP_PROTO;
PyProtocol_IPCheck_RETURN PyProtocol_IPCheck PyProtocol_IPCheck_PROTO;

int PyProtocol_IPCheck(PyObject *o)
{
    return IPObject_Check(o);
}

/**************
 *     IP     *
 **************/

IPObject *
PyProtocol_newIPObjectFromPacket(const struct pcap_pkthdr *pkthdr, const ip_t *ip, int *parsed_length)
{
    int length = pkthdr->len;
    int len;
    u_int hlen, version, offset;
    IPObject *self;
    self = PyObject_New(IPObject, &IP_Type);
    
    if (self == NULL)
        return NULL;
    self->ip_src = NULL;
    self->ip_dst = NULL;
    self->data = NULL;

    if (length < sizeof(ip_t))
    {
        Py_DECREF(self);
        PyErr_SetString(ErrorObject, "Incomplete IP header");
        return NULL;
    }
    
    len = ntohs(ip->ip_len);
    hlen = ip->ip_hl;
    version = ip->ip_v;

    if (version != 4)
    {
        Py_DECREF(self);
        PyErr_SetString(ErrorObject, "Invalid IP version");
        return NULL;
    }
    if (hlen < 5)
    {
        Py_DECREF(self);
        PyErr_SetString(ErrorObject, "Invalid header length");
        return NULL;
    }
    if (length < len)
    {
        Py_DECREF(self);
        PyErr_SetString(ErrorObject, "Incomplete IP header");
        return NULL;
    }
    offset = ntohs(ip->ip_off);

    self->ip_v = version;
    self->ip_hl = hlen;
    self->ip_len = ntohs(ip->ip_len);
    self->ip_id = ntohs(ip->ip_id);
    self->ip_off = offset;
    self->ip_ttl = ip->ip_ttl;
    self->ip_p = ip->ip_p;
    self->ip_sum = ntohs(ip->ip_sum);
    self->ip_src = PyString_FromString(inet_ntoa(ip->ip_src));
    self->ip_dst = PyString_FromString(inet_ntoa(ip->ip_dst));
    self->data = PyString_FromStringAndSize((char *) ip, (ip->ip_hl * 4));
    *parsed_length += (ip->ip_hl * 4);
    return self;
}

int PyProtocol_injectIP(PyObject *ip_py, libnet_t *context)
{
    IPObject *self = (IPObject *) ip_py;
    libnet_ptag_t r = libnet_build_ipv4(self->ip_len,
                                        self->ip_tos,
                                        self->ip_id,
                                        self->ip_off,
                                        self->ip_ttl,
                                        self->ip_p,
                                        0,
                                        inet_addr(PyString_AsString(self->ip_src)),
                                        inet_addr(PyString_AsString(self->ip_dst)),
                                        NULL,
                                        0,
                                        context,
                                        0);
    if (r == -1)
    {
        PyErr_SetString(ErrorObject, libnet_geterror(context));
        return 0;
    }
    return 1;
}


static PyObject *
IPObject_new(PyTypeObject *type, PyObject *args, PyObject *kwds)
{
    int ip_v;
    int ip_len;
    int ip_id;
    int ip_p;
    int ip_off;
    int ip_ttl;
    int ip_sum;
    char *ip_src, *ip_dst;
    IPObject *self;
    static char *kwlist[] = {"version", "length", "id", "offset", "ttl", "protocol",
        "checksum", "source", "destination", NULL};
    if (!PyArg_ParseTupleAndKeywords(args, kwds, "iiiiiiiss", kwlist, &ip_v, &ip_len, &ip_id, &ip_off, &ip_ttl, &ip_p, &ip_sum, &ip_src, &ip_dst))
        return NULL;
    self = PyObject_New(IPObject, &IP_Type);
    if (self == NULL)
        return NULL;

    self->ip_v = ip_v;
    self->ip_hl = LIBNET_IPV4_H;
    self->ip_tos = 0;
    self->ip_p = ip_p;
    self->ip_len = ip_len;
    self->ip_id = ip_id;
    self->ip_off = ip_off;
    self->ip_ttl = ip_ttl;
    self->ip_sum = ip_sum;
    self->ip_src = PyString_FromString(ip_src);
    self->ip_dst = PyString_FromString(ip_dst);
    self->data = NULL;
    return (PyObject *) self;
}

static void
IPObject_dealloc(IPObject *self)
{
    Py_XDECREF(self->ip_src);
    Py_XDECREF(self->ip_dst);
    Py_XDECREF(self->data);
    PyObject_Del(self);
}

static PyObject *
IPObject_str(IPObject * self)
{
    PyObject *result = PyString_FromFormat("IP(proto=0x%04x, %s -> %s)", self->ip_p,
                                           PyString_AsString(self->ip_src), PyString_AsString(self->ip_dst));
    return result;
}

static PyMethodDef IPObject_methods[] =
{
    {NULL, NULL}
};

static PyMemberDef IPObject_members[] =
{
    {"version", T_UBYTE, offsetof(IPObject, ip_v), 0, "IP version"},
    {"headerlength", T_UBYTE, offsetof(IPObject, ip_hl), 0, "IP header length"},
    {"typeofservice", T_UBYTE, offsetof(IPObject, ip_tos), 0, "IP type of service"},
    {"length", T_USHORT, offsetof(IPObject, ip_len), 0, "IP length"},
    {"id", T_USHORT, offsetof(IPObject, ip_id), 0, "IP identification"},
    {"offset", T_USHORT, offsetof(IPObject, ip_off), 0, "IP offset"},
    {"timetolive", T_UBYTE, offsetof(IPObject, ip_ttl), 0, "IP time to live"},
    {"protocol", T_UBYTE, offsetof(IPObject, ip_p), 0, "IP protocol"},
    {"checksum", T_USHORT, offsetof(IPObject, ip_sum), 0, "IP checksum"},
    {"source", T_OBJECT, offsetof(IPObject, ip_src), 0, "IP source"},
    {"destination", T_OBJECT, offsetof(IPObject, ip_dst), 0, "IP destination"},

    {"packet", T_OBJECT, offsetof(IPObject, data), READONLY, "Raw packet data."},
    { NULL }
};


PyTypeObject IP_Type =
{
    /* The ob_type field must be initialized in the module init function
    * to be portable to Windows without using C++. */
    PyObject_HEAD_INIT(NULL)
    0,                        /*ob_size*/
    "pycap.protocol.ip",                /*tp_name*/
    sizeof(IPObject),         /*tp_basicsize*/
    0,                        /*tp_itemsize*/
    /* methods */
    (destructor)IPObject_dealloc, /*tp_dealloc*/
    0,			      /*tp_print*/
    0,                        /*tp_getattr*/
    0,                        /*tp_setattr*/
    0,                        /*tp_compare*/
    (reprfunc)IPObject_str,   /*tp_repr*/
    0,                        /*tp_as_number*/
    0,                        /*tp_as_sequence*/
    0,                        /*tp_as_mapping*/
    0,                        /*tp_hash*/
    0,                        /*tp_call*/
    (reprfunc)IPObject_str,   /*tp_str*/
    0,                        /*tp_getattro*/
    0,                        /*tp_setattro*/
    0,                        /*tp_as_buffer*/
    Py_TPFLAGS_DEFAULT | Py_TPFLAGS_BASETYPE, /*tp_flags*/
    "IP packet",              /*tp_doc*/
    0,                        /*tp_traverse*/
    0,                        /*tp_clear*/
    0,                        /*tp_richcompare*/
    0,                        /*tp_weaklistoffset*/
    0,                        /*tp_iter*/
    0,                        /*tp_iternext*/
    IPObject_methods,         /*tp_methods*/
    IPObject_members,         /*tp_members*/
    0,                        /*tp_getset*/
    0,                        /*tp_base*/
    0,                        /*tp_dict*/
    0,                        /*tp_descr_get*/
    0,                        /*tp_descr_set*/
    0,                        /*tp_dictoffset*/
    0,                        /*tp_init*/
    0,                        /*tp_alloc*/
    IPObject_new,             /*tp_new*/
    0,                        /*tp_free*/
    0,                        /*tp_is_gc*/
};


int initIPType(PyObject *module_dict)
{
    if (PyType_Ready(&IP_Type) < 0)
        return 1;

    PyDict_SetItemString(module_dict, "ip", (PyObject *) &IP_Type);
    return 0;
}