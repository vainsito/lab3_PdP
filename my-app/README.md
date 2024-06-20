## Lab 2 - Programación orientada a objetos
---
### Dependencias

Necesitan Java 17, tanto el JRE como el JDK. En Ubuntu, pueden instalarlo con:

```bash
apt install openjdk-17-jdk openjdk-17-jre
```
Tambien se requiere Apache Spark!!!
### Creacion del Cluster de Spark (escribir bien)
1) Empezamos con
``
./sbin/start-master.sh
``

2) Abrimos en un navegador
localhost:8080

3) Configuramos conf/spark-env.sh
``
SPARK_WORKER_CORES=2
``
``
SPARK_WORKER_INSTANCES=2
``
``
SPARK_WORKER_MEMORY=2g
``
*(ahora al ejecutar start-worker se hacen dos)*
*(dos cores y 2g memoria para que se repartan entre los dos)*

4) Creamos los workers
``
./start-worker.sh spark://darkaraus-System-Product-Name:7077  -m 1G -c 1
``
### Cambios Respecto al Lab2 (escribir bien)
1) **Modularizacion en App.java:**
- Creacion de lista de articulos ahora se hace con ArticleListMaker.java. Es literalmente un copia y pega.
- Matcheo de Heuristica se hace en makeHeuristic.java. Ahora es solo un archivo para todas las heuristicas. 
2) **Se usa JavaRDD en vez de List para usar Spark:**
- El JavaRDD es repartido entre los workers y laburan paralelamente en el.
- Despues de procesar los datos, los reunimos con .collect().
3) **Los Articles pueden escribir la descripcion en un archivo de texto.**


### Decisiones Particulares
1) Decidimos que los articulos solo escriban la descripcion de el articulo ya que no utilizamos el titulo en ningun lado y complicaria las cosas.

### Dificultades
1) Hacer que funcione Spark es una abominacion. 

### Compilación y ejecución

- Para compilar el código ejecutamos `make`, lo cual crea todos los archivos compilados en el directorio `./bin`

- Para correr el código ejecutamos `make run ARGS="<flags>"` donde <flags> son las flags que corresponden a los args toma la función principal del software. 

- `make clean` borra los archivos `.class` que se generan en la compilación

