#include "Python.h"
#include "structmember.h"
#include "../protocol/protocolmodule.h"

PyObject *ErrorObject;

typedef struct {
    PyObject_HEAD
    char *device;
    char *filename;
    libnet_t *handle;
} injectObject;


static PyTypeObject inject_Type;

#define injectObject_Check(v) ((v)->ob_type == &inject_Type)

static int injectObject_inject(injectObject *self, PyObject *packet)
{
    PyObject *item = NULL;
    PyObject *payload = NULL;
    int current = PySequence_Size(packet) - 1;

    while (current >= 0)
    {
        item = PySequence_GetItem(packet, current);
        if (PyProtocol_EthernetCheck(item))
        {
            PyProtocol_injectEthernet(item, self->handle);
        }
        else if (PyProtocol_ARPCheck(item))
        {
            PyProtocol_injectARP(item, self->handle);
        }
        else if (PyProtocol_ICMPCheck(item))
        {
            payload = PySequence_GetItem(packet, current + 1);
            if (! PyProtocol_injectICMP(item, payload, self->handle))
                goto error;
            
            Py_DECREF(payload);
            payload = NULL;
        }
        else if (PyProtocol_IPCheck(item))
        {
            PyProtocol_injectIP(item, self->handle);
        }
        else if (PyProtocol_TCPCheck(item))
        {
            payload = PySequence_GetItem(packet, current + 1);
            PyProtocol_injectTCP(item, payload, self->handle);
            Py_DECREF(payload);
            payload = NULL;
        }
        else if (PyProtocol_UDPCheck(item))
        {
            payload = PySequence_GetItem(packet, current + 1);
            PyProtocol_injectUDP(item, payload, self->handle);
            Py_DECREF(payload);
            payload = NULL;
        }
        else
        {
            Py_DECREF(item);
            if (current > 0)
            {
                item = PySequence_GetItem(packet, current - 1);
                if (PyProtocol_ICMPCheck(item) ||
                    PyProtocol_TCPCheck(item) ||
                    PyProtocol_UDPCheck(item))
                {
                    goto payload;
                }
            }
            libnet_clear_packet(self->handle);
            PyErr_SetString(ErrorObject, "Unknown packet type");
            return NULL;
        }
payload: ;
        Py_DECREF(item);
        current--;
    }
    libnet_write(self->handle);
    return 1;

error: ;
    Py_XDECREF(item);
    Py_XDECREF(payload);
    return NULL;
}

static injectObject *
injectObject_new(char *device)
{
    injectObject *self;
    char errbuf[LIBNET_ERRBUF_SIZE];

    self = PyObject_New(injectObject, &inject_Type);
    if (self == NULL)
        return NULL;

    self->handle = libnet_init(LIBNET_LINK, device, errbuf);
    if (self->handle == NULL)
    {
        PyErr_SetString(ErrorObject, errbuf);
        return NULL;
    }

    return self;
}

/* pcap methods */

static void
injectObject_dealloc(injectObject *self)
{
    if (self->handle != NULL)
        libnet_destroy(self->handle);
    PyObject_Del(self);
}

static PyObject *
PyInject_InjectPacket(PyObject *self, PyObject *args)
{
    PyObject *packet;

    if (!PyArg_ParseTuple(args, "O!", &PyTuple_Type, &packet))
        return NULL;

    if (! injectObject_inject((injectObject *) self, packet))
        return NULL;

    return Py_BuildValue("");
}

static PyMethodDef injectObject_methods[] =
{
    {"inject", (PyCFunction) PyInject_InjectPacket, METH_VARARGS, "Inject a packet onto the network."},
    {NULL, NULL}
};

static PyMemberDef injectObject_members[] =
{
    { NULL }
};

static PyObject *
PyInject_New(PyTypeObject *type, PyObject *args, PyObject *kwds)
{
    injectObject *rv;
    char *device = NULL;

    static char *kwlist[] = {"device", NULL};

    if (!PyArg_ParseTupleAndKeywords(args, kwds, "|s:new", kwlist, &device))
        return NULL;

    rv = injectObject_new(device);
    if (rv == NULL)
        return NULL;
    return (PyObject *)rv;
}


static PyTypeObject inject_Type =
{
    /* The ob_type field must be initialized in the module init function
    * to be portable to Windows without using C++. */
    PyObject_HEAD_INIT(NULL)
    0,                        /*ob_size*/
    "pycap.inject.inject",            /*tp_name*/
    sizeof(injectObject),      /*tp_basicsize*/
    0,                        /*tp_itemsize*/
    /* methods */
    (destructor)injectObject_dealloc, /*tp_dealloc*/
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
    "Inject packets onto a network interface",    /*tp_doc*/
    0,                        /*tp_traverse*/
    0,                        /*tp_clear*/
    0,                        /*tp_richcompare*/
    0,                        /*tp_weaklistoffset*/
    0,                        /*tp_iter*/
    0,                        /*tp_iternext*/
    injectObject_methods,       /*tp_methods*/
    injectObject_members,       /*tp_members*/
    0,                        /*tp_getset*/
    0,                        /*tp_base*/
    0,                        /*tp_dict*/
    0,                        /*tp_descr_get*/
    0,                        /*tp_descr_set*/
    0,                        /*tp_dictoffset*/
    0,                        /*tp_init*/
    0,                        /*tp_alloc*/
    PyInject_New,           /*tp_new*/
    0,                        /*tp_free*/
    0,                        /*tp_is_gc*/
};

/* List of functions defined in the module */

static PyMethodDef inject_methods[] = {
    {NULL, NULL}		/* sentinel */
};

static char module_doc[] = "Injects packets onto a network interface";

/* Initialization function for the module (*must* be called initpycap) */
#ifndef PyMODINIT_FUNC
#       if defined(__cplusplus)
#               define PyMODINIT_FUNC extern "C" void
#       else /* __cplusplus */
#               define PyMODINIT_FUNC void
#       endif /* __cplusplus */
#endif

PyMODINIT_FUNC initinject(void)
{
    PyObject *m;
    PyObject *dict;

    /* Finalize the type object including setting type of the new type
        * object; doing it here is required for portability to Windows
        * without requiring C++. */
    if (PyType_Ready(&inject_Type) < 0)
        return;

    /* Create the module and add the functions */
    m = Py_InitModule3("inject", inject_methods, module_doc);

    if (import_protocol() < 0)
        return;

    /* Add some symbolic constants to the module */
    if (ErrorObject == NULL) {
        ErrorObject = PyErr_NewException("pycap.inject.error", NULL, NULL);
        if (ErrorObject == NULL)
            return;
    }
    
    Py_INCREF(ErrorObject);
    PyModule_AddObject(m, "error", ErrorObject);
    dict = PyModule_GetDict(m);
    PyDict_SetItemString(dict, "inject", (PyObject *) &inject_Type);
}
