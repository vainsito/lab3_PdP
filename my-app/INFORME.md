---
title: Laboratorio de Programación Orientada a Objetos
author: Alexis Joaquin Ortiz, Esteban Ezequiel Marin Reyna y Bruno Lopez Storani.
---

El enunciado del laboratorio se encuentra en [este link](https://docs.google.com/document/d/1wLhuEOjhdLwgZ4rlW0AftgKD4QIPPx37Dzs--P1gIU4/edit#heading=h.xe9t6iq9fo58).

# 1. Tareas
Pueden usar esta checklist para indicar el avance.

## Verificación de que pueden hacer las cosas.
- [x] Java 17 instalado. Deben poder compilar con `make` y correr con `make run` para obtener el mensaje de ayuda del programa.

## 1.1. Interfaz de usuario
- [x] Estructurar opciones
- [x] Construir el objeto de clase `Config`

## 1.2. FeedParser
- [x] `class Article`
    - [x] Atributos
    - [x] Constructor
    - [x] Método `print`
    - [x] _Accessors_
- [x] `parseXML`

## 1.3. Entidades nombradas
- [x] Pensar estructura y validarla con el docente
- [x] Implementarla
- [x] Extracción
    - [x] Implementación de heurísticas
- [x] Clasificación
    - [x] Por tópicos
    - [x] Por categorías
- Estadísticas
    - [x] Por tópicos
    - [x] Por categorías
    - [x] Impresión de estadísticas

## 1.4 Limpieza de código
- [x] Pasar un formateador de código
- [x] Revisar TODOs

# 2. Experiencia
En general a los 3 nos encanto hacer este laboratorio, ya que aprender un lenguaje nuevo que comprende un paradigma de la programacion diferente al que venimos estudiando estos años
es algo que nos entusiasma mucho a cada uno de nosotros.
Si tuvieramos que destacar algo que nos costo de este laboratorio fue explorar en la sintaxis de java, ya que ninguno de nosotros tiene experiencia previa con este lenguaje.

# 3. Preguntas
1. Explicar brevemente la estructura de datos elegida para las entidades nombradas.

    Nosotros empleamos 3 clases:

    - Category : Como lo indica el nombre, es la clase que creamos para las categorias, la cual se compone de dos campos relevantes, que son su nombre y sus atributos, el nombre es un simple string y los atributos son un hashmap donde su key sera el name_categorie (para almacenar atributos de esa categoria especifica).

    - Topics : Donde creamos los topics con el constructor Topics (No hay mucho que explicar).
    
    - NamedEntity : Clase donde vamos a crear una entidad nombrada utilizando objetos de las ya explicadas. Cada entidad poseera su categoria y su lista de topicos, a demas de un campo de repeticiones para implementar luego las estadisticas.

2. Explicar brevemente cómo se implementaron las heurísticas de extracción.
    
    Las euristicas creadas por nosotros son:

    - AcronymWordHeuristic : Nos permite tomar como candidatos acronimos.

    - PrecededWordHeuristic : Tomar como candidato una entidad precedida por palabras que suelen acompañar a estas entidades.

# 4. Extras
Completar si hacen algo.

Changelog:
- App.java mas bonito:
    > nuevo .java para crear el archivo
    > printHelp a otro .java, lo recomendaba el comentario
    > borre un par de cosas que quedaron de versiones anteriores
- bigdata ya no crece infinitamente:
    > si el archivo ya existe borro el contenido

Dato:
- NamedEntitiesUtils ya no usa el bool found por que hace namedEntity = null
  que si sigue null significa que no la encontraron. Muy inteligente el chatGTP.

- Hice que el bigdata.txt se creara dentro de main/java. 
  Funciona, pero al hacer mvn clean install se mete el bigdata.txt en
  target/classes/data, y la verdad no se si cambia algo eso.
  Queda MUY feo que se haga una nueva carpeta en src/data en vez de usar la carpeta src/main/java/data
  es muy ChatGTP en mi opinion. 

PROBLEMA:
- No printea las stats... Me fije y dentro de printStats el argumento statsSelected esta BIEN, lo imprimi
  y es 'cat' ya que no le puse ningun argumento.
  cada NamedEntity tiene sus categories y topics bien.
  importante: Los prints dentro de el foreach del JavaRDD no aparecen en la terminal
  Aparecen en la pagina del master, medio raro de encontrar, anda al CompletedOperation mas recente
  y te fijas en el stdout de alguno de los dos workers. es raro por que algunos no dicen nada.
  EN FIN. En un log que vi solo entro una vez al print "at least i enter here" pero nunca entro ni si quiera al isNewEntity. 
  Esto es raro por que si no entra ahi entonces nunca añade nada al NamedEntities, pero esto si ocurre xD