# Architecture Components


### Código antes da refatoração
A tabela que foi refatorada foi a **episodes**, estava dentro da classe PodcastDBHelper e tinha as seguintes colunas:

 -_id
 -title
 -pubDate
 -link
 -description
 -downloadLink
 -downloadUri

A classe que fazia a comunicação entre o banco e o resto do sistema era a **PodcastProvider**. Nela se tinha metodos que permitiam fazer diversas consultas ao banco. 

E havia uma classe chamada **PodcastProviderContract** que continha assinaturas úteis para o desenvolvimento.


### Implementação do Architecture Components
Toda a implementação do banco anterior bem como chamada a métodos de consulta no banco não foram utilizadas no código final. Porém, as implementações foram preservadas para análise comparativa.

A classe básica criada, PodcastRoom, mantinha todos os valores anteriores da tabela, porém sem a necessidade de se escrever código Sql. 

Foi criada a classe PodcastDao que continha várias consultas que anteriormente eram feitas em parte do PodcastProvider e em parte no código que chamava está classe.
Expecificamente se referindo aos códigos de consulta foram implementado 4 métodos diferentes de consulta para que ficasse mais fácil nas classes que usam informações do banco de conseguir a informação desejada. 

```Java
    @Query("select * from episodes")
    List<PodcastRoom> getAll();

    @Query("select downloadLink from episodes where title= :title")
    List<String> getTitle(String title);

    @Query("select downloadUri from episodes where title = :title")
    List<String> getDownloadUri(String title);

    @Query("Select * from episodes where title = :title")
    List<PodcastRoom> getPodcastRoom(String title);
```

Além disso foi criada a classe **PodcastDatabase** que faz essa ligação com o banco e permite que seja acessa pelas classes que utilizam informações do banco.

Outra modificação que foi necessário foi feito foi adicionar na classe MainActivity uma AsyncTask para que fosse recuperado dados do banco.

Nas classes que chamam o PodcastDatabase o código ficou muito mais simples e menor conhecimento de consulta em banco.

Foi feita uma adaptação para que não fosse descartada a classe ItemFeed pois isso geraria a necessidade de modificar muito código. É feito uma conversão do PodcastRoom para o ItemFeed quando necessário.
