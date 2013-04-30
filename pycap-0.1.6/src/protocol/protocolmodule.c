#include "Python.h"
#define PROTOCOL_MODULE
#include "protocolmodule.h"

PyObject *ErrorObject;

/* List of functions defined in the module */

static PyMethodDef pycap_protocol_methods[] = {
{NULL, NULL}                /* sentinel */
};

static char module_doc[] = "Network protocol types.";

/* Initialization function for the module (*must* be called initprotocol) */
#ifndef PyMODINIT_FUNC
#       if defined(__cplusplus)
#               define PyMODINIT_FUNC extern "C" void
#       else /* __cplusplus */
#               define PyMODINIT_FUNC void
#       endif /* __cplusplus */
#endif

PyMODINIT_FUNC initprotocol(void)
{
    PyObject *m;
    PyObject *dict;
    static void *PyProtocol_API[PyProtocol_API_pointers];
    PyObject *c_api_object;
    
    /* Create the module and add the functions */
    m = Py_InitModule3("protocol", pycap_protocol_methods, module_doc);

    /* Initialize the C API */
    PyProtocol_API[PyProtocol_newARPObjectFromPacket_NUM] = (void *)PyProtocol_newARPObjectFromPacket;
    PyProtocol_API[PyProtocol_injectARP_NUM] = (void *)PyProtocol_injectARP;
    PyProtocol_API[PyProtocol_ARPCheck_NUM] = (void *)PyProtocol_ARPCheck;
    
    PyProtocol_API[PyProtocol_newEthernetObjectFromPacket_NUM] = (void *)PyProtocol_newEthernetObjectFromPacket;
    PyProtocol_API[PyProtocol_injectEthernet_NUM] = (void *)PyProtocol_injectEthernet;
    PyProtocol_API[PyProtocol_EthernetCheck_NUM] = (void *)PyProtocol_EthernetCheck;

    PyProtocol_API[PyProtocol_newICMPObjectFromPacket_NUM] = (void *)PyProtocol_newICMPObjectFromPacket;
    PyProtocol_API[PyProtocol_injectICMP_NUM] = (void *)PyProtocol_injectICMP;
    PyProtocol_API[PyProtocol_ICMPCheck_NUM] = (void *)PyProtocol_ICMPCheck;

    PyProtocol_API[PyProtocol_newIPObjectFromPacket_NUM] = (void *)PyProtocol_newIPObjectFromPacket;
    PyProtocol_API[PyProtocol_injectIP_NUM] = (void *)PyProtocol_injectIP;
    PyProtocol_API[PyProtocol_IPCheck_NUM] = (void *)PyProtocol_IPCheck;

    PyProtocol_API[PyProtocol_newTCPObjectFromPacket_NUM] = (void *)PyProtocol_newTCPObjectFromPacket;
    PyProtocol_API[PyProtocol_injectTCP_NUM] = (void *)PyProtocol_injectTCP;
    PyProtocol_API[PyProtocol_TCPCheck_NUM] = (void *)PyProtocol_TCPCheck;

    PyProtocol_API[PyProtocol_newUDPObjectFromPacket_NUM] = (void *)PyProtocol_newUDPObjectFromPacket;
    PyProtocol_API[PyProtocol_injectUDP_NUM] = (void *)PyProtocol_injectUDP;
    PyProtocol_API[PyProtocol_UDPCheck_NUM] = (void *)PyProtocol_UDPCheck;

    /* Create a CObject containing the API pointer array's address */
    c_api_object = PyCObject_FromVoidPtr((void *)PyProtocol_API, NULL);

    if (c_api_object != NULL)
        PyModule_AddObject(m, "_C_API", c_api_object);

    /* Add some symbolic constants to the module */
    if (ErrorObject == NULL) {
        ErrorObject = PyErr_NewException("pycap.protocol.error", NULL, NULL);
        if (ErrorObject == NULL)
            return;
    }
    Py_INCREF(ErrorObject);
    PyModule_AddObject(m, "error", ErrorObject);
    dict = PyModule_GetDict(m);
    if (initEthernetType(dict))
        return;
    if (initIPType(dict))
        return;
    if (initICMPType(dict))
        return;
    if (initTCPType(dict))
        return;
    if (initUDPType(dict))
        return;
    if (initARPType(dict))
        return;
}