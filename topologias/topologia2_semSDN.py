#   TCC - Abordagem para Distribuicao de Video Baseada em Redes Definidas por Software
#   Nadyan Suriel Pscheidt
#   Script de criacao de topologia Mininet
#   Topologia 2 - Sem OpenFlow

#   $ sudo mn --custom topologia2_semSDN.py --topo e2_noOF

from mininet.topo import Topo

class Experimento2( Topo ):

    def build( self ):
        # Create two hosts.
        server1 = self.addHost( 's1' )
        user1 = self.addHost( 'u1' )
        user2 = self.addHost( 'u2' )
        user3 = self.addHost( 'u3' )

        # Create a switch
        switch1 = self.addSwitch( 'sw1' )

        # Add links between the switch and each host
        self.addLink( switch1, server1 )
        self.addLink( switch1, user1 )
        self.addLink( switch1, user2 )
        self.addLink( switch1, user3 )

topos = {
    'e2_noOF': Experimento2
}
