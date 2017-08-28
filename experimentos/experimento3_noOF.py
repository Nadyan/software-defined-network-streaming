#   TCC - Abordagem para Distribuicao de Video Baseada em Redes Definidas por Software
#   Nadyan Suriel Pscheidt
#   Script de criacao de topologia Mininet
#   Experimento 3 - Sem OpenFlow

#   $ sudo mn --custom experimento3_noOF.py --topo e3_noOF

from mininet.topo import Topo

class Experimento3( Topo ):

    def build( self ):
        server1 = self.addHost( 's1' )
        user1 = self.addHost( 'u1' )
        user2 = self.addHost( 'u2' )
        user3 = self.addHost( 'u3' )

        switch1 = self.addSwitch( 'sw1' )
        switch2 = self.addSwitch( 'sw2' )

        self.addLink( switch1, server1 )
        self.addLink( switch1, user1 )
        self.addLink( switch2, user2 )
        self.addLink( switch2, user3 )
        self.addLink( switch1, switch2 )
        self.addLink( switch2, server1 )

topos = {
    'e3_noOF': Experimento3
}
