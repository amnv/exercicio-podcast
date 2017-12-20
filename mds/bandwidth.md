# Consumo de rede

A análise do consumo da rede não pode ser feito em sua totalidade visto que o android studio apresentou um erro.

![img1]()

Mas mesmo assim, foi possível tirar algumas conclusões dos resultados obtidos.

### Início da execução

No começo da execução é feito o download das informações contidas no arquivo xml do diretório http://leopoldomt.com/if710/fronteirasdaciencia.xml. Visto que se trata de arquivos de texto o consumo de rede não é muito alto. Porém, visto que toda vez que a MainActivity é executada ela faz esse download essa ação é muito costusa do ponto de vista de consumo de rede e bateria.

![img2]()

### Download de áudio

Ao iniciar o download de um áudio é perceptível o aumento de consumo da rede. Isso se da devido ao grande volume de dados que está sendo baixado. Essa é a única tela que se faz acesso a internet e, além disso, não há upload de dados.

### Melhorias

Uma das principais melhorias que poderia ser feito é o uso de JobScheduler para evitar a frequente e desnecessária utilização de rede para verificar atualização nos dados do podcast. Visto que o podcast em específico não é atualizado com frequência diaria é possível reduzir essa consulta a apenas uma vez ao dia ou menos, reduzindo substanciamente o consumo de rede e bateria.
