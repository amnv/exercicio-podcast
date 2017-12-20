# Android Profiler

Ao navegar pelas telas do app pude perceber que haviam alguns momentos de picos.

### IntentService
Por exemplo, a dois momentos de pico quando o IntentService é chamado. Ele é chamado quando é necessários recuperar dados dos banco ou baixa-los da internet.

![CPU1](https://github.com/amnv/exercicio-podcast/blob/master/img/CpuMostrandon_if710_podcast.png)

### Thread mais usada

Parece ter uma relação entre os momentos de menor estresse da cpu e o fato da thread n.if710.podcast está dormindo. Provavelmente está é a thread que se encontra a MainActivity e por fazer uso da UI e tenha um consumo de cpu maior.

![CPU2](https://github.com/amnv/exercicio-podcast/blob/master/img/CpuMostrandon_if710_podcast.png)

### AsyncTask

Outra coisa que é possível observar é que nos momentos em que são chamados as asyncTasks não há uma redução no uso da cpu. Isso mostra que embora as atividades estejam sendo feitas em treads separadas não evitou que houvesse grande uso de cpu. Isso não quer dizer que o uso de threads foi ineficaz.

![CPU3](https://github.com/amnv/exercicio-podcast/blob/master/img/CpuAsyncTasks.png)

### Melhorias

Uma melhoria que poderia ser feita pra reduzir o consumo de cpu seria a utilizam de jobscheduler para que a atulização do banco fosse executada com menor frequência.
