package utils;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.io.Serializable;

import org.apache.spark.api.java.JavaRDD;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.core.type.TypeReference;

import java.util.Set;
import java.util.HashSet;

import namedEntities.Category;
import namedEntities.NamedEntity;
import namedEntities.Topics;
import namedEntities.heuristics.Heuristic;
import namedEntities.heuristics.makeHeuristic;

// Clase que se encarga de ordenar las entidades y de imprimir las estadistica
public class NamedEntitiesUtils implements Serializable{
    // Map para almacenar las named entities recolectadas de los workers
    private Map<String, NamedEntity> namedEntities;

    // Map para almacenar temporalmente las named entities de cada worker
    private Map<String, NamedEntity> namedEntitiesTemp;

    // Set para almacenar las categorias existentes
    private Set<String> categoriesSet; // <> Mejor Nombre

    // Set para almacenar los tópicos existentes
    private Set<String> topicsSet; // <> Mejor Nombre

    // Constructor
    public NamedEntitiesUtils() {
        this.namedEntities = new HashMap<>();
        this.categoriesSet = new HashSet<>();
        this.topicsSet = new HashSet<>();
        this.namedEntitiesTemp = new HashMap<>();
    }

    // Metodos


    public void sortEntities(JavaRDD<String> lines, String heuristic) {

        Heuristic heuristica = new makeHeuristic();
        JavaRDD<String> candidatos = null;
        
    
        try{
            candidatos = heuristica.extractCandidates(lines, heuristic);
        } catch (IllegalArgumentException e) {
            System.exit(1);
        } 
    
        try { 
            String content = new String(Files.readAllBytes(Paths.get("target/classes/data/dictionary.json")),
                    StandardCharsets.UTF_8);
    
            // Usar Jackson para parsear el contenido JSON
            ObjectMapper mapper = new ObjectMapper();
            List<Map<String, Object>> mapEntries = mapper.readValue(content, new TypeReference<List<Map<String, Object>>>(){});
            // <> Mejor Nombre
            JavaRDD<NamedEntity> namedEntitiesRDD = candidatos.map(candidate -> {
                NamedEntity namedEntity = null;
                for (Map<String, Object> entry : mapEntries) { // <> Mejor Nombre
                    if (entry.containsKey("keywords")) {
                        List<String> keywordList = (List<String>) entry.get("keywords");
                        for (String keyword : keywordList) {
                            if (keyword.equalsIgnoreCase(candidate)) {
                                    boolean isNewEntity = false;

                                    if (namedEntitiesTemp.containsKey(candidate)) {
                                        namedEntity = namedEntitiesTemp.get(candidate);
                                        namedEntity.incrementRepetitions();
                                    } else {
                                        Category category_entity = new Category((String) entry.get("Category"));
                                        namedEntity = new NamedEntity(category_entity, (String) entry.get("label"));
                                        namedEntitiesTemp.put(candidate, namedEntity);
                                        isNewEntity = true;
                                    }
    
                                    if (entry.containsKey("Topics") && isNewEntity) {
                                        List<String> topics_entity = (List<String>) entry.get("Topics");
                                        for (String topic : topics_entity) {
                                            Topics topico = new Topics(topic);
                                            namedEntity.addTopic(topico);
                                        }
                                    }
                                break;
                            }
                        }
                    }
                }
                if (namedEntity == null) {
                    namedEntity = new NamedEntity(new Category("OTHER"), candidate);
                    namedEntity.addTopic(new Topics("OTHER"));
                    this.namedEntities.put(candidate, namedEntity);
                    this.categoriesSet.add("OTHER");
                    this.topicsSet.add("OTHER");
                }
                return namedEntity;
            });
    
            // <> Se utiliza el temp para no tener que hacer cambios drasticos en el codigo del lab 2.

            // Recoger los resultados y agregarlos a namedEntities
            // <> Ahora mas elegante
            List<NamedEntity> namedEntitiesList = namedEntitiesRDD.collect();
            namedEntitiesList.forEach(namedEntity -> {
                namedEntities.put(namedEntity.getName(), namedEntity);
                categoriesSet.add(namedEntity.getCategory().getName());
                namedEntity.getTopics().forEach(topic -> topicsSet.add(topic.getName()));
            });

            // <> Ahora usa printNamedEntities en App.java
    
        } catch (IOException e) {
            e.printStackTrace();
            System.exit(1);
        }
    }

    // metodo para imprimir las entidades nombradas
    public void printNamedEntities() {
        for (NamedEntity namedEntity : this.namedEntities.values()) {
            namedEntity.namedEntityPrint();
        }
    }

    // Metodo para imprimir las estadisticas de repetición de las entidades
    // nombradas

    public void printStats(String statsSelected) {
        // Si statsSelected es "cat" se imprimen las repeticiones de las entidades
        // nombradas por categoría
        // Si statsSelected es "top" se imprimen las repeticiones de las entidades
        // nombradas por tópico
        if (statsSelected.equals("cat")) {
            for (String category : this.categoriesSet) {
                System.out.println("Category: " + category);
                for (NamedEntity namedEntity : this.namedEntities.values()) {
                    if (namedEntity.getCategory().getName().equals(category)) {
                        System.out.println(namedEntity.getName() + ": " + namedEntity.getRepetitions());
                    }
                }
            }
        } else if (statsSelected.equals("top")) {
            for (String topic : this.topicsSet) {
                System.out.println("Topic: " + topic);
                for (NamedEntity namedEntity : this.namedEntities.values()) {
                    for (Topics t : namedEntity.getTopics()) {
                        if (t.getName().equals(topic)) {
                            System.out.println(namedEntity.getName() + ": " + namedEntity.getRepetitions());
                            break;
                        }
                    }
                }
            }
        } else {
            System.out.println("Invalid stats option");
        }
    }
}