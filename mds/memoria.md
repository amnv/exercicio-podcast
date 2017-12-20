# Memória

Para testar a memória foi inicialmente utilizado o Memory Profiler. Foi possível observar momentos específicos onde ocorriam aumento do uso da memória e utilização do Garbage Colletion. 

### Início da execução

Ao começar a execução foi possível observar um aumento no uso da memória, esse aumento parece ser natural devido a necessidade de carregar informações do app para a memória.

![img1]()

### Interagindo com as telas

Uma das primeiras ações do app é verificar se tem coneção com a internet. Caso a resposta seja positiva baixar e inserir no banco o podcasts (caso tenha novos podcasts em relação ao que continha no banco) e caso seja negativa ele tenta carregar as informações que já estavam contidas no banco. Dado a essa consulta ao banco, é possível observar que a um aumento significativo no uso da memoria neste momento.

Logo em seguida foi realizado uma série de ações de navegação pelo app. Primeiramente foi observado a tela EpisodeDetailActivity que também faz consulta ao banco. é possível observar que a novos picos de consumo de bateria neste ponto. Ao voltar a tela principal matando tela sitada é possível observar que ocorreu garbage colletion, provavelmente para descartas as informações carregadas ma quela tela que não seriam mais utilizadas.

Após isso foi interagido com a tela de Settings onde foi utilizado as SharedPrefferences para alterar o campo de busca dos arquivos xml. É possível observar que houve momentos de picos seguidos de decidas consideráveis no uso da memória.

Também foi possível observar que enquanto se interagia com outro app ele não acinou o GC e nem ocorreu redução no uso de memória.

![img2]()

### Percorrendo ListView

Foi possível observar que ao percorrer há um aumento suave no uso de memoria.

![img3]()

### Rotação de tela

É possível observar a ação do GC realmente ao fazer a rotação de tela. Isso se deve provavelmente ao LiveCycle do app que destroi a activity quando a uma rotação de tela.

![img4]()
