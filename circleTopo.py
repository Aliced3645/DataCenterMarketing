from mininet.topo import Topo

class MyTopo( Topo ):
    "Circle topology"
    
    def __init__(self):

        Topo.__init__(self)

        h1 = self.addHost('h1');
        h2 = self.addHost('h2');
        h3 = self.addHost('h3');
        h4 = self.addHost('h4');
        h5 = self.addHost('h5');
    
        s1 = self.addSwitch('s1');
        s2 = self.addSwitch('s2');
        s3 = self.addSwitch('s3');
        s4 = self.addSwitch('s4');
        s5 = self.addSwitch('s5');

        
        self.addLink(s1,s5);
        self.addLink(s1,s2);
        self.addLink(s2,s3);
        self.addLink(s3,s4);
        self.addLink(s4,s5);
        self.addLink(s1,h1);
        self.addLink(s2,h2);
        self.addLink(s3,h3);
        self.addLink(s4,h4);
        self.addLink(s5,h5);

topos = { 'circletopo': ( lambda: MyTopo() ) }
