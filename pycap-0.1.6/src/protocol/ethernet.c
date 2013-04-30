#include "Python.h"
#include "structmember.h"
#define PROTOCOL_MODULE
#include "protocolmodule.h"

PyProtocol_newEthernetObjectFromPacket_RETURN PyProtocol_newEthernetObjectFromPacket PyProtocol_newEthernetObjectFromPacket_PROTO;
PyProtocol_injectEthernet_RETURN PyProtocol_injectEthernet PyProtocol_injectEthernet_PROTO;
PyProtocol_EthernetCheck_RETURN PyProtocol_EthernetCheck PyProtocol_EthernetCheck_PROTO;

int PyProtocol_EthernetCheck(PyObject *o)
{
    return ethernetObject_Check(o);
}

char *
decodeMAC(PyObject *str);

/**************/
/*  ETHERNET  */
/**************/

ethernetObject *
PyProtocol_newEthernetObjectFromPacket(ethernet_t *ether, int *parsed_length)
{
    ethernetObject *self;
    self = PyObject_New(ethernetObject, &ethernet_Type);
    if (self == NULL)
        return NULL;
    self->ether_dhost = NULL;
    self->ether_shost = NULL;
    self->data = NULL;
    
    self->ether_dhost = MACasString(ether->ether_dhost);
    if (! self->ether_dhost)
        goto error;
    self->ether_shost = MACasString(ether->ether_shost);
    if (! self->ether_shost)
        goto error;
    self->ether_type = ether->ether_type;

    self->data = PyString_FromStringAndSize((char *) ether, sizeof(ethernet_t));
    if (! self->data)
        goto error;
    *parsed_length += sizeof(ethernet_t);
    return self;

error: ;
    Py_DECREF(self);
    return NULL;
}

int PyProtocol_injectEthernet(PyObject *ethernet_py, libnet_t *context)
{
    ethernetObject *self = (ethernetObject *) ethernet_py;
    char *shost = decodeMAC(self->ether_shost);
    char *dhost = decodeMAC(self->ether_dhost);
    libnet_ptag_t r = libnet_build_ethernet(dhost,
                                            shost,
                                            self->ether_type,
                                            NULL,
                                            0,
                                            context,
                                            0);
    free(shost);
    free(dhost);
    if (r == -1)
    {
        PyErr_SetString(ErrorObject, libnet_geterror(context));
        return 0;
    }
    return 1;
}


static PyObject *
ethernetObject_new(PyTypeObject *type, PyObject *args, PyObject *kwds)
{
    PyObject *ether_dhost;
    PyObject *ether_shost;
    char *temp = NULL;
    int ether_type;
    static char *kwargs[] = {"type", "source", "destination"};
    ethernetObject *self;
    if (!PyArg_ParseTupleAndKeywords(args, kwds, "iSS", kwargs, &ether_type, &ether_shost, &ether_dhost))
        return NULL;

    self = PyObject_New(ethernetObject, &ethernet_Type);
    if (self == NULL)
        return NULL;

    temp = decodeMAC(ether_shost);
    if (! temp)
    {
        PyErr_SetString(PyExc_TypeError, "Invalid format for source MAC address");
        return NULL;
    }
    else
        free(temp);

    temp = decodeMAC(ether_dhost);
    if (! temp)
    {
        PyErr_SetString(PyExc_TypeError, "Invalid format for destination MAC address");
        return NULL;
    }
    else
        free(temp);


    Py_INCREF(ether_dhost);
    Py_INCREF(ether_shost);
    self->ether_dhost = ether_dhost;
    self->ether_shost = ether_shost;
    self->ether_type = ether_type;
    self->data = NULL;
    return (PyObject *) self;
}

static void
ethernetObject_dealloc(ethernetObject *self)
{
    Py_XDECREF(self->ether_dhost);
    Py_XDECREF(self->ether_shost);
    Py_XDECREF(self->data);
    PyObject_Del(self);
}

PyObject *
MACasString(char *s)
{
    char buffer[255] = "";
    int i;

    for (i = 0; i < ETHER_ADDR_LEN; i++)
    {
        sprintf(buffer, "%s%02x:", buffer, (unsigned char) s[i]);
    }
    buffer[strlen(buffer) - 1] = 0;
    return PyString_FromString(buffer);
}

char *
decodeMAC(PyObject *str)
{
    int i, num;
    unsigned int bufbig[6];
    char *buffer = (char *) calloc(ETHER_ADDR_LEN, sizeof(unsigned int));
    char *source = PyString_AsString(str);
    if (strlen(source) != 17)
        return NULL;
    num = sscanf(source, "%02x:%02x:%02x:%02x:%02x:%02x",
                 &bufbig[0], &bufbig[1], &bufbig[2],
                 &bufbig[3], &bufbig[4], &bufbig[5]);
    if (num != 6)
    {
        return NULL;
    }
    for (i = 0; i < 6; i++)
        buffer[i] = (unsigned char) bufbig[i];
    return buffer;
}

static PyObject *
ethernetObject_str(ethernetObject * self)
{
    PyObject *result = PyString_FromFormat("Ethernet(type=0x%08x, %s -> %s)", self->ether_type,
                                           PyString_AsString(self->ether_shost),
                                           PyString_AsString(self->ether_dhost));
    return result;
}

static PyMethodDef ethernetObject_methods[] =
{
    {NULL, NULL}
};

static PyMemberDef ethernetObject_members[] =
{
    {"destination", T_OBJECT, offsetof(ethernetObject, ether_dhost), 0, "The destination of the ethernet packet."},
    {"source", T_OBJECT, offsetof(ethernetObject, ether_shost), 0, "The source of the ethernet packet."},
    {"type", T_USHORT, offsetof(ethernetObject, ether_type), 0, "The type of the ethernet packet."},
    {"packet", T_OBJECT, offsetof(ethernetObject, data), READONLY, "Raw packet data."},
    { NULL }
};


PyTypeObject ethernet_Type =
{
    /* The ob_type field must be initialized in the module init function
    * to be portable to Windows without using C++. */
    PyObject_HEAD_INIT(NULL)
    0,                        /*ob_size*/
    "pycap.protocol.ethernet",          /*tp_name*/
    sizeof(ethernetObject),   /*tp_basicsize*/
    0,                        /*tp_itemsize*/
    /* methods */
    (destructor)ethernetObject_dealloc, /*tp_dealloc*/
    0,			      /*tp_print*/
    0,                        /*tp_getattr*/
    0,                        /*tp_setattr*/
    0,                        /*tp_compare*/
    (reprfunc)ethernetObject_str,       /*tp_repr*/
    0,                        /*tp_as_number*/
    0,                        /*tp_as_sequence*/
    0,                        /*tp_as_mapping*/
    0,                        /*tp_hash*/
    0,                        /*tp_call*/
    (reprfunc)ethernetObject_str,       /*tp_str*/
    0,                        /*tp_getattro*/
    0,                        /*tp_setattro*/
    0,                        /*tp_as_buffer*/
    Py_TPFLAGS_DEFAULT,       /*tp_flags*/
    "Ethernet packet",        /*tp_doc*/
    0,                        /*tp_traverse*/
    0,                        /*tp_clear*/
    0,                        /*tp_richcompare*/
    0,                        /*tp_weaklistoffset*/
    0,                        /*tp_iter*/
    0,                        /*tp_iternext*/
    ethernetObject_methods,   /*tp_methods*/
    ethernetObject_members,   /*tp_members*/
    0,                        /*tp_getset*/
    0,                        /*tp_base*/
    0,                        /*tp_dict*/
    0,                        /*tp_descr_get*/
    0,                        /*tp_descr_set*/
    0,                        /*tp_dictoffset*/
    0,                        /*tp_init*/
    0,                        /*tp_alloc*/
    ethernetObject_new,       /*tp_new*/
    0,                        /*tp_free*/
    0,                        /*tp_is_gc*/
};

int initEthernetType(PyObject *module_dict)
{
    if (PyType_Ready(&ethernet_Type) < 0)
        return 1;

    PyDict_SetItemString(module_dict, "ethernet", (PyObject *) &ethernet_Type);
    return 0;
}