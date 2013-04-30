#include "Python.h"
#include "structmember.h"
#define PROTOCOL_MODULE
#include "protocolmodule.h"

PyProtocol_newTCPObjectFromPacket_RETURN PyProtocol_newTCPObjectFromPacket PyProtocol_newTCPObjectFromPacket_PROTO;
PyProtocol_injectTCP_RETURN PyProtocol_injectTCP PyProtocol_injectTCP_PROTO;
PyProtocol_TCPCheck_RETURN PyProtocol_TCPCheck PyProtocol_TCPCheck_PROTO;

int PyProtocol_TCPCheck(PyObject *o)
{
    return TCPObject_Check(o);
}


/**************
*    TCP     *
**************/
TCPObject *
PyProtocol_newTCPObjectFromPacket(tcp_t *tcp, int *parsed_length)
{
    TCPObject *self;
    self = PyObject_New(TCPObject, &TCP_Type);
    if (self == NULL)
        return NULL;
    self->th_sport = ntohs(tcp->th_sport);
    self->th_dport = ntohs(tcp->th_dport);
    self->th_seq = ntohl(tcp->th_seq);
    self->th_ack = ntohl(tcp->th_ack);
    self->th_off = tcp->th_off;
    self->th_flags = tcp->th_flags;
    self->th_win = ntohs(tcp->th_win);
    self->th_sum = ntohs(tcp->th_sum);
    self->th_urp = ntohs(tcp->th_urp);
    self->data = NULL;

    self->data = PyString_FromStringAndSize((char *) tcp, (tcp->th_off * 4));
    if (! self->data)
    {
        Py_DECREF(self);
        return NULL;
    }
    *parsed_length += (tcp->th_off * 4);
    return self;
}

int PyProtocol_injectTCP(PyObject *tcp_py, PyObject *payload_py, libnet_t *context)
{
    TCPObject *self = (TCPObject *) tcp_py;
    char *payload = NULL;
    int payload_len = NULL;
    libnet_ptag_t r;
    PyString_AsStringAndSize(payload_py, &payload, &payload_len);
    r = libnet_build_tcp(self->th_sport,
                         self->th_dport,
                         self->th_seq,
                         self->th_ack,
                         self->th_flags,
                         self->th_win,
                         0,
                         self->th_urp,
                         LIBNET_TCP_H + payload_len,
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

static PyObject *
TCPObject_new(PyTypeObject *type, PyObject *args, PyObject *kwds)
{
    int th_sport;               /* source port */
    int th_dport;               /* destination port */
    long th_seq;                  /* sequence number */
    long th_ack;                  /* acknowledgement number */
//    unsigned char  th_x2;
    char th_flags;

    int th_win;                 /* window */
    int th_urp;                 /* urgent pointer */

    static char *kwlist[] = {"sourceport", "destinationport", "sequence", "acknowledge", "flags",
        "window", "urgent", NULL};
    TCPObject *self;

    if (!PyArg_ParseTupleAndKeywords(args, kwds, "iillbii", kwlist, &th_sport, &th_dport, &th_seq,
                                     &th_ack, &th_flags, &th_win, &th_urp))
        return NULL;
    self = PyObject_New(TCPObject, &TCP_Type);
    if (self == NULL)
        return NULL;

    self->th_sport = th_sport;
    self->th_dport = th_dport;
    self->th_seq = th_seq;
    self->th_ack = th_ack;
    self->th_flags = th_flags;
    self->th_win = th_win;
    self->th_urp = th_urp;
    self->th_x2 = 0;
    self->th_off = 0;
    self->th_sum = 0;
    self->data = NULL;

    return (PyObject *) self;
}

static void
TCPObject_dealloc(TCPObject *self)
{
    Py_XDECREF(self->data);
    PyObject_Del(self);
}

static PyObject *
TCPObject_str(TCPObject * self)
{
    char flags[255] = "";
    char buffer[1024] = "";
    unsigned char f = self->th_flags;

    if (f & TH_FIN)
        strcat(flags, "fin, ");
    if (f & TH_SYN)
        strcat(flags, "syn, ");
    if (f & TH_RST)
        strcat(flags, "rst, ");
    if (f & TH_PUSH)
        strcat(flags, "push, ");
    if (f & TH_ACK)
        strcat(flags, "ack, ");
    if (f & TH_URG)
        strcat(flags, "urg, ");

    if (f & (TH_FIN|TH_SYN|TH_RST|TH_ACK|TH_URG))
        flags[strlen(flags) - 2] = 0;
    else
        strcpy(flags, "none");

    sprintf(buffer, "TCP(%d -> %d, seq=0x%04lx, ack=0x%04lx, flags=(%s))",
            self->th_sport, self->th_dport, self->th_seq, self->th_ack,
            flags);
    return Py_BuildValue("s", buffer);
}



static PyMethodDef TCPObject_methods[] =
{
    {NULL, NULL}
};

static PyMemberDef TCPObject_members[] =
{
    {"sourceport", T_USHORT, offsetof(TCPObject, th_sport), 0, "TCP source port"},
    {"destinationport", T_USHORT, offsetof(TCPObject, th_dport), 0, "TCP destination port"},
    {"sequence", T_ULONG, offsetof(TCPObject, th_seq), 0, "TCP sequence number"},
    {"acknowledge", T_ULONG, offsetof(TCPObject, th_ack), 0, "TCP acknowledgement number"},
//    {"offset", T_UBYTE, offsetof(TCPObject, th_off), 0, "TCP data offset"},
    {"flags", T_UBYTE, offsetof(TCPObject, th_flags), 0, "TCP flags"},
    {"window", T_USHORT, offsetof(TCPObject, th_win), 0, "TCP window size"},
    {"checksum", T_USHORT, offsetof(TCPObject, th_sum), 0, "TCP checksum"},
    {"urgent", T_USHORT, offsetof(TCPObject, th_urp), 0, "TCP urgent pointer"},

    {"packet", T_OBJECT, offsetof(TCPObject, data), READONLY, "Raw packet data"},
    { NULL }
};


PyTypeObject TCP_Type =
{
    /* The ob_type field must be initialized in the module init function
    * to be portable to Windows without using C++. */
    PyObject_HEAD_INIT(NULL)
    0,                        /*ob_size*/
    "pycap.protocol.tcp",               /*tp_name*/
    sizeof(TCPObject),        /*tp_basicsize*/
    0,                        /*tp_itemsize*/
    /* methods */
    (destructor)TCPObject_dealloc, /*tp_dealloc*/
    0,			      /*tp_print*/
    0,                        /*tp_getattr*/
    0,                        /*tp_setattr*/
    0,                        /*tp_compare*/
    (reprfunc)TCPObject_str,  /*tp_repr*/
    0,                        /*tp_as_number*/
    0,                        /*tp_as_sequence*/
    0,                        /*tp_as_mapping*/
    0,                        /*tp_hash*/
    0,                        /*tp_call*/
    (reprfunc)TCPObject_str,  /*tp_str*/
    0,                        /*tp_getattro*/
    0,                        /*tp_setattro*/
    0,                        /*tp_as_buffer*/
    Py_TPFLAGS_DEFAULT,       /*tp_flags*/
    "TCP packet",             /*tp_doc*/
    0,                        /*tp_traverse*/
    0,                        /*tp_clear*/
    0,                        /*tp_richcompare*/
    0,                        /*tp_weaklistoffset*/
    0,                        /*tp_iter*/
    0,                        /*tp_iternext*/
    TCPObject_methods,        /*tp_methods*/
    TCPObject_members,        /*tp_members*/
    0,                        /*tp_getset*/
    0,                        /*tp_base*/
    0,                        /*tp_dict*/
    0,                        /*tp_descr_get*/
    0,                        /*tp_descr_set*/
    0,                        /*tp_dictoffset*/
    0,                        /*tp_init*/
    0,                        /*tp_alloc*/
    TCPObject_new,            /*tp_new*/
    0,                        /*tp_free*/
    0,                        /*tp_is_gc*/
};

int initTCPType(PyObject *module_dict)
{
    if (PyType_Ready(&TCP_Type) < 0)
        return 1;
    PyDict_SetItemString(module_dict, "tcp", (PyObject *) &TCP_Type);
    return 0;
}