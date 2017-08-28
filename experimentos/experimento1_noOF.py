#   TCC - Abordagem para Distribuicao de Video Baseada em Redes Definidas por Software
#   Nadyan Suriel Pscheidt
#   Script de criacao de topologia Mininet
#   Experimento 1 - Sem OpenFlow

#   $ sudo mn --custom experimento1_noOF.py --topo e1_noOF

from mininet.topo import Topo

class Experimento1( Topo ):

    def build( self ):
        # Create two hosts.
        server1 = self.addHost( 's1' )
        user1 = self.addHost( 'u1' )

        # Create a switch
        switch1 = self.addSwitch( 'sw1' )

        # Add links between the switch and each host
        self.addLink( switch1, server1 )
        self.addLink( switch1, user1 )

topos = {
    'e1_noOF': Experimento1
}
