import unittest
import pycap.protocol, pycap.constants

class ARPTest(unittest.TestCase):
    def testCreate(self):
        shw = '00:03:93:44:a9:92'
        sp = '192.168.0.2'
        thw = '00:50:ba:8f:c4:5f'
        tp = '192.168.0.1'
        
        arp = pycap.protocol.arp(shw, thw, sp, tp, pycap.constants.arp.ARPOP_REQUEST)

        self.assertEqual(arp.sourcehardware, shw)
        self.assertEqual(arp.targethardware, thw)
        self.assertEqual(arp.sourceprotocol, sp)
        self.assertEqual(arp.targetprotocol, tp)
        self.assertEqual(arp.operation, pycap.constants.arp.ARPOP_REQUEST)

if __name__ == "__main__":
    unittest.main()
