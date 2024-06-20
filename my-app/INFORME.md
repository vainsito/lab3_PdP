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