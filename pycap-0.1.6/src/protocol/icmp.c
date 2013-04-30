#include "Python.h"
#include "structmember.h"
#define PROTOCOL_MODULE
#include "protocolmodule.h"

extern PyObject *ErrorObject;

PyProtocol_newICMPObjectFromPacket_RETURN PyProtocol_newICMPObjectFromPacket PyProtocol_newICMPObjectFromPacket_PROTO;
PyProtocol_injectICMP_RETURN PyProtocol_injectICMP PyProtocol_injectICMP_PROTO;
PyProtocol_ICMPCheck_RETURN PyProtocol_ICMPCheck PyProtocol_ICMPCheck_PROTO;

typedef ICMPObject ICMPEchoRequestObject;
typedef ICMPObject ICMPEchoReplyObject;
typedef ICMPObject ICMPTimestampRequestObject;
typedef ICMPObject ICMPTimestampReplyObject;
typedef ICMPObject ICMPRedirectObject;
typedef ICMPObject ICMPParameterProblemObject;
typedef ICMPObject ICMPInfoRequestObject;
typedef ICMPObject ICMPInfoReplyObject;


PyTypeObject ICMP_Type;
PyTypeObject ICMPEchoRequest_Type;
PyTypeObject ICMPEchoReply_Type;
PyTypeObject ICMPTimestampRequest_Type;
PyTypeObject ICMPTimestampReply_Type;
PyTypeObject ICMPRedirect_Type;
PyTypeObject ICMPParameterProblem_Type;
PyTypeObject ICMPInfoRequest_Type;
PyTypeObject ICMPInfoReply_Type;

#define ICMPObject_Check(v)     ((v)->ob_type == &ICMP_Type)

int PyProtocol_ICMPCheck(PyObject *o)
{
    return PyObject_IsInstance(o, (PyObject *) &ICMP_Type);
}



/**************
*    ICMP     *
**************/
ICMPObject *
PyProtocol_newICMPObjectFromPacket(icmp_t *icmp_hdr, int *parsed_length)
{
    ICMPObject *self;
    switch (icmp_hdr->icmp_type)
    {
        case ICMP_TSTAMP:
            self = PyObject_New(ICMPTimestampRequestObject, &ICMPTimestampRequest_Type);
            break;
        case ICMP_TSTAMPREPLY:
            self = PyObject_New(ICMPTimestampReplyObject, &ICMPTimestampReply_Type);
            break;
        case ICMP_ECHO:
            self = PyObject_New(ICMPEchoRequestObject, &ICMPEchoRequest_Type);
            break;
        case ICMP_ECHOREPLY:
            self = PyObject_New(ICMPEchoReplyObject, &ICMPEchoReply_Type);
            break;
        case ICMP_REDIRECT:
            self = PyObject_New(ICMPRedirectObject, &ICMPRedirect_Type);
            break;
        case ICMP_PARAMPROB:
            self = PyObject_New(ICMPParameterProblemObject, &ICMPParameterProblem_Type);
            break;
        case ICMP_IREQ:
            self = PyObject_New(ICMPInfoRequestObject, &ICMPInfoRequest_Type);
            break;
        case ICMP_IREQREPLY:
            self = PyObject_New(ICMPInfoReplyObject, &ICMPInfoReply_Type);
            break;
        default:
            self = PyObject_New(ICMPObject, &ICMP_Type);            
    }

    if (self == NULL)
        return NULL;
    
    self->icmp_type = icmp_hdr->icmp_type;
    self->icmp_code = icmp_hdr->icmp_code;
    self->icmp_cksum = ntohs(icmp_hdr->icmp_sum);

    self->pptr = NULL;
    self->gwaddr = NULL;
    self->id = NULL;
    self->seq = NULL;
    self->otime = NULL;
    self->rtime = NULL;
    self->ttime = NULL;

    self->data = NULL;

    switch (icmp_hdr->icmp_type)
    {
        case ICMP_TSTAMP:
        case ICMP_TSTAMPREPLY:
        {
            self->otime = PyInt_FromLong((long) (ntohl(icmp_hdr->icmp_otime)));
            self->rtime = PyInt_FromLong((long) (ntohl(icmp_hdr->icmp_rtime)));
            self->ttime = PyInt_FromLong((long) (ntohl(icmp_hdr->icmp_ttime)));
        }
        case ICMP_IREQ:
        case ICMP_IREQREPLY:
        case ICMP_ECHOREPLY:
        case ICMP_ECHO:
        {
            self->id = PyInt_FromLong((long) (ntohs(icmp_hdr->icmp_id)));
            self->seq = PyInt_FromLong((long) (ntohs(icmp_hdr->icmp_seq)));
            break;
        }
        case ICMP_PARAMPROB:
        {
            // icmp_hdr->icmp_pptr isnt defined in libnet?!
            break;
        }
        case ICMP_REDIRECT:
        {
            self->gwaddr = PyString_FromString(inet_ntoa(* ((struct in_addr *) &icmp_hdr->hun.gateway)));
            break;
        }
    }
    self->data = PyString_FromStringAndSize((char *) icmp_hdr, sizeof(icmp_t));
    *parsed_length += sizeof(icmp_t);
    return self;
}

int
PyProtocol_injectICMP(PyObject *icmp_py, PyObject *payload_py, libnet_t *context)
{
    ICMPObject *self = (ICMPObject *) icmp_py;
    icmp_t *packet = (icmp_t *) PyString_AsString(self->data);
    char *payload = NULL;
    int payload_len = NULL;
    libnet_ptag_t r;
    PyString_AsStringAndSize(payload_py, &payload, &payload_len);
    printf("Injecting ICMP packet\n");
    switch (packet->icmp_type)
    {
        case ICMP_IREQ:
        case ICMP_IREQREPLY:
        case ICMP_ECHOREPLY:
        case ICMP_ECHO:
            r = libnet_build_icmpv4_echo(packet->icmp_type,
                                         packet->icmp_code,
                                         0,
                                         ntohs(packet->icmp_id),
                                         ntohs(packet->icmp_seq),
                                         payload,
                                         payload_len,
                                         context,
                                         0);
            break;
        case ICMP_TSTAMP:
        case ICMP_TSTAMPREPLY:
            r = libnet_build_icmpv4_timestamp(packet->icmp_type,
                                              packet->icmp_code,
                                              0,
                                              packet->icmp_id,
                                              packet->icmp_seq,
                                              ntohl(packet->icmp_otime),
                                              ntohl(packet->icmp_rtime),
                                              ntohl(packet->icmp_ttime),
                                              payload,
                                              payload_len,
                                              context,
                                              0);
            break;
        case ICMP_REDIRECT:
            // TODO:  We don't currently disect the IP header in the
            //        body of the ICMP redirect packet.
        default:
            r = -1;
    }
    if (r == -1)
    {
        PyErr_SetString(ErrorObject, libnet_geterror(context));
        return 0;
    }
    return 1;
}

static PyObject *
ICMPObject_new(PyTypeObject *type, PyObject *args, PyObject *kwds)
{
    PyErr_SetString(PyExc_RuntimeError, "Lazy");
    return NULL;
}

static void
ICMPObject_dealloc(ICMPObject *self)
{
    Py_XDECREF(self->pptr);
    Py_XDECREF(self->gwaddr);
    Py_XDECREF(self->id);
    Py_XDECREF(self->seq);
    Py_XDECREF(self->otime);
    Py_XDECREF(self->rtime);
    Py_XDECREF(self->ttime);
    
    Py_XDECREF(self->data);
    PyObject_Del(self);
}

static PyObject *
ICMPObject_str(ICMPObject * self)
{
    PyObject *result = PyString_FromFormat("ICMP(type=0x%04x, code=0x%04x)", self->icmp_type, self->icmp_code);
    return result;
}



static PyMethodDef ICMPObject_methods[] =
{
    {NULL, NULL}
};

static PyMemberDef ICMPObject_members[] =
{
    {"type", T_UBYTE, offsetof(ICMPObject, icmp_type), 0, "ICMP type"},
    {"code", T_UBYTE, offsetof(ICMPObject, icmp_code), 0, "ICMP code"},
    {"checksum", T_USHORT, offsetof(ICMPObject, icmp_cksum), 0, "ICMP checksum"},
    //    {"problempointer", T_OBJECT_EX, offsetof(ICMPObject, pptr), 0, "ICMP problem pointer"},
    //    {"gateway", T_OBJECT_EX, offsetof(ICMPObject, gwaddr), 0, "ICMP gateway address"},
    //    {"id", T_OBJECT_EX, offsetof(ICMPObject, id), 0, "ICMP identifier"},
    //    {"sequence", T_OBJECT_EX, offsetof(ICMPObject, seq), 0, "ICMP sequence number"},

    /*
     TODO: Path MTU Discovery (RFC1191)

     PyObject *icmp_void;
     PyObject *icmp_pmvoid;
     PyObject *icmp_nextmtu;
     PyObject *icmp_num_addrs;
     PyObject *icmp_wpa;
     PyObject *icmp_lifetime;
     */

    //    {"originatetime", T_OBJECT_EX, offsetof(ICMPObject, otime), 0, "ICMP originate timestamp"},
    //    {"receivetime", T_OBJECT_EX, offsetof(ICMPObject, rtime), 0, "ICMP receive timestamp"},
    //    {"transmittime", T_OBJECT_EX, offsetof(ICMPObject, ttime), 0, "ICMP transmit timestamp"},
    //    {"ip", T_OBJECT_EX, offsetof(ICMPObject, icmp_ip), 0, "ICMP IP header"},

    /*
     TODO: Path MTU Discovery (RFC1191)

     PyObject *icmp_radv;
     PyObject *icmp_mask;
     */
    {"packet", T_OBJECT, offsetof(ICMPObject, data), READONLY, "Raw packet data"},
    { NULL }
};


PyTypeObject ICMP_Type =
{
    /* The ob_type field must be initialized in the module init function
    * to be portable to Windows without using C++. */
    PyObject_HEAD_INIT(NULL)
    0,                        /*ob_size*/
    "pycap.protocol.icmp",              /*tp_name*/
    sizeof(ICMPObject),       /*tp_basicsize*/
    0,                        /*tp_itemsize*/
    /* methods */
    (destructor)ICMPObject_dealloc, /*tp_dealloc*/
    0,			      /*tp_print*/
    0,                        /*tp_getattr*/
    0,                        /*tp_setattr*/
    0,                        /*tp_compare*/
    (reprfunc)ICMPObject_str, /*tp_repr*/
    0,                        /*tp_as_number*/
    0,                        /*tp_as_sequence*/
    0,                        /*tp_as_mapping*/
    0,                        /*tp_hash*/
    0,                        /*tp_call*/
    (reprfunc)ICMPObject_str, /*tp_str*/
    0,                        /*tp_getattro*/
    0,                        /*tp_setattro*/
    0,                        /*tp_as_buffer*/
    Py_TPFLAGS_DEFAULT | Py_TPFLAGS_BASETYPE, /*tp_flags*/
    "ICMP packet",            /*tp_doc*/
    0,                        /*tp_traverse*/
    0,                        /*tp_clear*/
    0,                        /*tp_richcompare*/
    0,                        /*tp_weaklistoffset*/
    0,                        /*tp_iter*/
    0,                        /*tp_iternext*/
    ICMPObject_methods,       /*tp_methods*/
    ICMPObject_members,       /*tp_members*/
    0,                        /*tp_getset*/
    0,                        /*tp_base*/
    0,                        /*tp_dict*/
    0,                        /*tp_descr_get*/
    0,                        /*tp_descr_set*/
    0,                        /*tp_dictoffset*/
    0,                        /*tp_init*/
    0,                        /*tp_alloc*/
    ICMPObject_new,                        /*tp_new*/
    0,                        /*tp_free*/
    0,                        /*tp_is_gc*/
};

static PyMemberDef ICMPEchoObject_members[] =
{
    {"id", T_OBJECT_EX, offsetof(ICMPObject, id), 0, "ICMP identifier"},
    {"sequence", T_OBJECT_EX, offsetof(ICMPObject, seq), 0, "ICMP sequence number"},

    {NULL}
};

PyTypeObject ICMPEchoRequest_Type =
{
    /* The ob_type field must be initialized in the module init function
    * to be portable to Windows without using C++. */
    PyObject_HEAD_INIT(&PyType_Type)
    0,                        /*ob_size*/
    "pycap.protocol.icmpEchoRequest",   /*tp_name*/
    sizeof(ICMPObject),       /*tp_basicsize*/
    0,                        /*tp_itemsize*/
    /* methods */
    0,                        /*tp_dealloc*/
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
    "ICMP Echo Request packet",/*tp_doc*/
    0,                        /*tp_traverse*/
    0,                        /*tp_clear*/
    0,                        /*tp_richcompare*/
    0,                        /*tp_weaklistoffset*/
    0,                        /*tp_iter*/
    0,                        /*tp_iternext*/
    ICMPObject_methods,       /*tp_methods*/
    ICMPEchoObject_members,   /*tp_members*/
    0,                        /*tp_getset*/
    &ICMP_Type,               /*tp_base*/
    0,                        /*tp_dict*/
    0,                        /*tp_descr_get*/
    0,                        /*tp_descr_set*/
    0,                        /*tp_dictoffset*/
    0,                        /*tp_init*/
    0,                        /*tp_alloc*/
    0,                        /*tp_new*/
    0,                        /*tp_free*/
    0,                        /*tp_is_gc*/
};

PyTypeObject ICMPEchoReply_Type =
{
    /* The ob_type field must be initialized in the module init function
    * to be portable to Windows without using C++. */
    PyObject_HEAD_INIT(&PyType_Type)
    0,                        /*ob_size*/
    "pycap.protocol.icmpEchoReply",     /*tp_name*/
    sizeof(ICMPObject),       /*tp_basicsize*/
    0,                        /*tp_itemsize*/
    /* methods */
    0,                        /*tp_dealloc*/
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
    "ICMP Echo Reply packet", /*tp_doc*/
    0,                        /*tp_traverse*/
    0,                        /*tp_clear*/
    0,                        /*tp_richcompare*/
    0,                        /*tp_weaklistoffset*/
    0,                        /*tp_iter*/
    0,                        /*tp_iternext*/
    ICMPObject_methods,       /*tp_methods*/
    ICMPEchoObject_members,   /*tp_members*/
    0,                        /*tp_getset*/
    &ICMP_Type,               /*tp_base*/
    0,                        /*tp_dict*/
    0,                        /*tp_descr_get*/
    0,                        /*tp_descr_set*/
    0,                        /*tp_dictoffset*/
    0,                        /*tp_init*/
    0,                        /*tp_alloc*/
    0,                        /*tp_new*/
    0,                        /*tp_free*/
    0,                        /*tp_is_gc*/
};


static PyMemberDef ICMPTimestampObject_members[] =
{
    {"originatetime", T_OBJECT_EX, offsetof(ICMPObject, otime), 0, "ICMP originate timestamp"},
    {"receivetime", T_OBJECT_EX, offsetof(ICMPObject, rtime), 0, "ICMP receive timestamp"},
    {"transmittime", T_OBJECT_EX, offsetof(ICMPObject, ttime), 0, "ICMP transmit timestamp"},

    {NULL}
};

PyTypeObject ICMPTimestampRequest_Type =
{
    /* The ob_type field must be initialized in the module init function
    * to be portable to Windows without using C++. */
    PyObject_HEAD_INIT(&PyType_Type)
    0,                        /*ob_size*/
    "pycap.protocol.icmpTimestampRequest",/*tp_name*/
    sizeof(ICMPObject),       /*tp_basicsize*/
    0,                        /*tp_itemsize*/
    /* methods */
    0,                        /*tp_dealloc*/
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
    "ICMP Timestamp Request packet",/*tp_doc*/
    0,                        /*tp_traverse*/
    0,                        /*tp_clear*/
    0,                        /*tp_richcompare*/
    0,                        /*tp_weaklistoffset*/
    0,                        /*tp_iter*/
    0,                        /*tp_iternext*/
    ICMPObject_methods,       /*tp_methods*/
    ICMPTimestampObject_members,   /*tp_members*/
    0,                        /*tp_getset*/
    &ICMP_Type,               /*tp_base*/
    0,                        /*tp_dict*/
    0,                        /*tp_descr_get*/
    0,                        /*tp_descr_set*/
    0,                        /*tp_dictoffset*/
    0,                        /*tp_init*/
    0,                        /*tp_alloc*/
    0,                        /*tp_new*/
    0,                        /*tp_free*/
    0,                        /*tp_is_gc*/
};

PyTypeObject ICMPTimestampReply_Type =
{
    /* The ob_type field must be initialized in the module init function
    * to be portable to Windows without using C++. */
    PyObject_HEAD_INIT(&PyType_Type)
    0,                        /*ob_size*/
    "pycap.protocol.icmpTimestampReply",/*tp_name*/
    sizeof(ICMPObject),       /*tp_basicsize*/
    0,                        /*tp_itemsize*/
    /* methods */
    0,                        /*tp_dealloc*/
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
    "ICMP Timestamp Reply packet",/*tp_doc*/
    0,                        /*tp_traverse*/
    0,                        /*tp_clear*/
    0,                        /*tp_richcompare*/
    0,                        /*tp_weaklistoffset*/
    0,                        /*tp_iter*/
    0,                        /*tp_iternext*/
    ICMPObject_methods,       /*tp_methods*/
    ICMPTimestampObject_members,   /*tp_members*/
    0,                        /*tp_getset*/
    &ICMP_Type,               /*tp_base*/
    0,                        /*tp_dict*/
    0,                        /*tp_descr_get*/
    0,                        /*tp_descr_set*/
    0,                        /*tp_dictoffset*/
    0,                        /*tp_init*/
    0,                        /*tp_alloc*/
    0,                        /*tp_new*/
    0,                        /*tp_free*/
    0,                        /*tp_is_gc*/
};


static PyMemberDef ICMPRedirectObject_members[] =
{
    {"gateway", T_OBJECT_EX, offsetof(ICMPObject, gwaddr), 0, "ICMP gateway address"},
    {NULL}
};

PyTypeObject ICMPRedirect_Type =
{
    /* The ob_type field must be initialized in the module init function
    * to be portable to Windows without using C++. */
    PyObject_HEAD_INIT(&PyType_Type)
    0,                        /*ob_size*/
    "pycap.protocol.icmpRedirect",   /*tp_name*/
    sizeof(ICMPObject),       /*tp_basicsize*/
    0,                        /*tp_itemsize*/
    /* methods */
    0,                        /*tp_dealloc*/
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
    "ICMP Redirect packet",   /*tp_doc*/
    0,                        /*tp_traverse*/
    0,                        /*tp_clear*/
    0,                        /*tp_richcompare*/
    0,                        /*tp_weaklistoffset*/
    0,                        /*tp_iter*/
    0,                        /*tp_iternext*/
    ICMPObject_methods,       /*tp_methods*/
    ICMPRedirectObject_members,/*tp_members*/
    0,                        /*tp_getset*/
    &ICMP_Type,               /*tp_base*/
    0,                        /*tp_dict*/
    0,                        /*tp_descr_get*/
    0,                        /*tp_descr_set*/
    0,                        /*tp_dictoffset*/
    0,                        /*tp_init*/
    0,                        /*tp_alloc*/
    0,                        /*tp_new*/
    0,                        /*tp_free*/
    0,                        /*tp_is_gc*/
};

static PyMemberDef ICMPParameterProblemObject_members[] =
{
    {"problempointer", T_OBJECT_EX, offsetof(ICMPObject, pptr), 0, "ICMP problem pointer"},
    {NULL}
};

PyTypeObject ICMPParameterProblem_Type =
{
    /* The ob_type field must be initialized in the module init function
    * to be portable to Windows without using C++. */
    PyObject_HEAD_INIT(&PyType_Type)
    0,                        /*ob_size*/
    "pycap.protocol.icmpParameterProblem", /*tp_name*/
    sizeof(ICMPObject),       /*tp_basicsize*/
    0,                        /*tp_itemsize*/
    /* methods */
    0,                        /*tp_dealloc*/
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
    "ICMP Parameter Problem packet",   /*tp_doc*/
    0,                        /*tp_traverse*/
    0,                        /*tp_clear*/
    0,                        /*tp_richcompare*/
    0,                        /*tp_weaklistoffset*/
    0,                        /*tp_iter*/
    0,                        /*tp_iternext*/
    ICMPObject_methods,       /*tp_methods*/
    ICMPParameterProblemObject_members,/*tp_members*/
    0,                        /*tp_getset*/
    &ICMP_Type,               /*tp_base*/
    0,                        /*tp_dict*/
    0,                        /*tp_descr_get*/
    0,                        /*tp_descr_set*/
    0,                        /*tp_dictoffset*/
    0,                        /*tp_init*/
    0,                        /*tp_alloc*/
    0,                        /*tp_new*/
    0,                        /*tp_free*/
    0,                        /*tp_is_gc*/
};


static PyMemberDef ICMPInfoObject_members[] =
{
    {"id", T_OBJECT_EX, offsetof(ICMPObject, id), 0, "ICMP identifier"},
    {"sequence", T_OBJECT_EX, offsetof(ICMPObject, seq), 0, "ICMP sequence number"},

    {NULL}
};

PyTypeObject ICMPInfoRequest_Type =
{
    /* The ob_type field must be initialized in the module init function
    * to be portable to Windows without using C++. */
    PyObject_HEAD_INIT(&PyType_Type)
    0,                        /*ob_size*/
    "pycap.protocol.icmpInfoRequest",   /*tp_name*/
    sizeof(ICMPObject),       /*tp_basicsize*/
    0,                        /*tp_itemsize*/
    /* methods */
    0,                        /*tp_dealloc*/
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
    "ICMP Info Request packet",/*tp_doc*/
    0,                        /*tp_traverse*/
    0,                        /*tp_clear*/
    0,                        /*tp_richcompare*/
    0,                        /*tp_weaklistoffset*/
    0,                        /*tp_iter*/
    0,                        /*tp_iternext*/
    ICMPObject_methods,       /*tp_methods*/
    ICMPInfoObject_members,   /*tp_members*/
    0,                        /*tp_getset*/
    &ICMP_Type,               /*tp_base*/
    0,                        /*tp_dict*/
    0,                        /*tp_descr_get*/
    0,                        /*tp_descr_set*/
    0,                        /*tp_dictoffset*/
    0,                        /*tp_init*/
    0,                        /*tp_alloc*/
    0,                        /*tp_new*/
    0,                        /*tp_free*/
    0,                        /*tp_is_gc*/
};

PyTypeObject ICMPInfoReply_Type =
{
    /* The ob_type field must be initialized in the module init function
    * to be portable to Windows without using C++. */
    PyObject_HEAD_INIT(&PyType_Type)
    0,                        /*ob_size*/
    "pycap.protocol.icmpInfoReply",     /*tp_name*/
    sizeof(ICMPObject),       /*tp_basicsize*/
    0,                        /*tp_itemsize*/
    /* methods */
    0,                        /*tp_dealloc*/
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
    "ICMP Info Reply packet", /*tp_doc*/
    0,                        /*tp_traverse*/
    0,                        /*tp_clear*/
    0,                        /*tp_richcompare*/
    0,                        /*tp_weaklistoffset*/
    0,                        /*tp_iter*/
    0,                        /*tp_iternext*/
    ICMPObject_methods,       /*tp_methods*/
    ICMPInfoObject_members,   /*tp_members*/
    0,                        /*tp_getset*/
    &ICMP_Type,               /*tp_base*/
    0,                        /*tp_dict*/
    0,                        /*tp_descr_get*/
    0,                        /*tp_descr_set*/
    0,                        /*tp_dictoffset*/
    0,                        /*tp_init*/
    0,                        /*tp_alloc*/
    0,                        /*tp_new*/
    0,                        /*tp_free*/
    0,                        /*tp_is_gc*/
};



int initICMPType(PyObject *module_dict)
{
    if (PyType_Ready(&ICMP_Type) < 0)
        return 1;
    if (PyType_Ready(&ICMPEchoRequest_Type) < 0)
        return 1;
    if (PyType_Ready(&ICMPEchoReply_Type) < 0)
        return 1;
    if (PyType_Ready(&ICMPTimestampRequest_Type) < 0)
        return 1;
    if (PyType_Ready(&ICMPTimestampReply_Type) < 0)
        return 1;
    if (PyType_Ready(&ICMPRedirect_Type) < 0)
        return 1;
    if (PyType_Ready(&ICMPParameterProblem_Type) < 0)
        return 1;
    if (PyType_Ready(&ICMPInfoRequest_Type) < 0)
        return 1;
    if (PyType_Ready(&ICMPInfoReply_Type) < 0)
        return 1;
    

    PyDict_SetItemString(module_dict, "icmp", (PyObject *) &ICMP_Type);
    PyDict_SetItemString(module_dict, "icmpEchoRequest", (PyObject *) &ICMPEchoRequest_Type);
    PyDict_SetItemString(module_dict, "icmpEchoReply", (PyObject *) &ICMPEchoReply_Type);
    PyDict_SetItemString(module_dict, "icmpTimestampRequest", (PyObject *) &ICMPTimestampRequest_Type);
    PyDict_SetItemString(module_dict, "icmpTimestampReply", (PyObject *) &ICMPTimestampReply_Type);
    PyDict_SetItemString(module_dict, "icmpRedirect", (PyObject *) &ICMPRedirect_Type);
    PyDict_SetItemString(module_dict, "icmpParameterProblem", (PyObject *) &ICMPParameterProblem_Type);
    PyDict_SetItemString(module_dict, "icmpInfoRequest", (PyObject *) &ICMPInfoRequest_Type);
    PyDict_SetItemString(module_dict, "icmpInfoReply", (PyObject *) &ICMPInfoReply_Type);

    return 0;
}
