ABORDAGEM PARA DISTRIBUIÇÃO DE VÍDEO BASEADA EM REDES DEFINIDAS POR SOFTWARE
NADYAN SURIEL PSCHEIDT - UDESC

  A transmissão de vídeos através de uma rede é um serviço que vem sendo cada vez mais requisitado.
Empresas fornecedoras de conteúdo em vídeo que disponibilizam esse serviço precisam fornecer o
conteúdo ao consumidor de forma satisfatória, sem problemas de lentidão ou insdisponibilizade do
vídeo escolhido pelo usuário. Além da agilidade na transmissão, esses serviços precisam suportar
diversos usuários simultaneamente, quantidade que aumenta de forma considerável ano após ano.
  O crescimento da quantidade de usuários solicitando os serviços de transmissão de vídeo afetou
também a forma como as redes são gerenciadas e organizadas, tornando essa tarefa mais compelxa do
que costumava ser. Para auxiliar na gerência e desempenho da rede para diversas aplicações, incluindo
transmissão de vídeos, surgiram as chamadas Redes Definidas por Software (SDN), que diferente das Redes
Convencionais, disssociam o plano de dados do plano de controle, que comunicam-se através do protocolo
OpenFlow. Com essa abordagem, é possível modificar, da forma desejada, os tabelas de fluxos do switch
que está sendo configurado, explicitando como cada pacote deve ser roteado através da topologia da rede,
auxiliando assim na entrega do conteúdo para usuários simultâneos e na otimização do uso dos recursos
da rede.
  O objetivo do trabalho consiste, de forma geral, em especificar uma abordagem para transmissão e 
distribuição de vídeos em uma rede na qual existem clientes requisitantes e servidores fornecendo o
conteúdo, de forma a utilizar conceitos e abordagens de Redes Definidas por Software. O propósito é de
diminuir o consumo de recursos de rede, como a largura de banda e a carga no enlace de comunicação,
entre o servidor e o consumidor do conteúdo, possibilitando escalar a quantidade de usuários simultâneos
recebendo tal conteúdo.
  A proposta abrange a transmissão de vídeo com uma quanitdade n de servidores de conteúdo, na qual cada
servidor pode disponibilizar uma quantidade indefinida de conteúdos diferenciados, Entre os servidores
de conteúdo e o cliente final existe a rede gerenciada, que por sua vez é composta de uma quantidade x
de switches OpenFlow controlados por um controlador que se comunica via OpenFlow e que é responsável pela
gerência e políticas de encaminhamento dos conteúdos dentro dessa rede. A gerência abrange também a agregação
dos fluxos semelhantes, ou seja, caso exista uma transmissão de um conteúdo sendo executada e um usuário
requisita o mesmo conteúdo, tais fluxos são agregados e transmitidos como um único fluxo a partir do servidor
até o switch mais próximo dos usuários que realizaram a transmissão do conteúdo.
  Com a abordagem proposta, espera-se uma maior escalabilidade do sistema no quesito quantidade de usuários
simultâneos, justamente pela otimização dos fluxos evitando redundâncias de conteúdo no enlace de comunicação
entre servidor e switch. Com isso, na abordagem proposta mais usuários conseguirão assistir conteúdos diferentes
de forma simultânea.
  Assim como a escalabilidade, espera-se uma melhora na qualidade da transmissãom nos quesitos de pacotes perdidos
ou latência. Quando comparado com uma transmissão em uma Rede Convencional com a mesma quantidade de usuários
simultâneos, a qualidade da transmissão em tais quesitos deve ser superior na abordagem proposta, pela otimização
do uso da larguda de banda e eliminação dos fluxos redundantes.
