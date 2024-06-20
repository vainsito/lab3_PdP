package utils;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.io.Serializable;

import org.apache.spark.api.java.JavaRDD;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.core.type.TypeReference;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.Set;
import java.util.stream.Collectors;
import java.util.HashSet;
import java.util.Iterator;

import feed.Article;
import namedEntities.Category;
import namedEntities.NamedEntity;
import namedEntities.Topics;
import namedEntities.heuristics.Heuristic;
import namedEntities.heuristics.makeHeuristic;

// Clase que se encarga de ordenar las entidades y de imprimir las estadistica
public class NamedEntitiesUtils implements Serializable{
    private Map<String, NamedEntity> namedEntities;

    // Set para almacenar las categorias existentes
    private Set<String> categories;

    // Set para almacenar los tópicos existentes
    private Set<String> topics;

    // Constructor
    public NamedEntitiesUtils() {
        this.namedEntities = new HashMap<>();
        this.categories = new HashSet<>();
        this.topics = new HashSet<>();
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
            List<Map<String, Object>> jsonArray = mapper.readValue(content, new TypeReference<List<Map<String, Object>>>(){});
    
            JavaRDD<NamedEntity> namedEntitiesRDD = candidatos.map(candidate -> {
                NamedEntity namedEntity = null;
                for (Map<String, Object> jsonObject : jsonArray) {
                    if (jsonObject.containsKey("keywords")) {
                        List<String> keywords = (List<String>) jsonObject.get("keywords");
                        for (String keyword : keywords) {
                            if (keyword.equalsIgnoreCase(candidate)) {
                                synchronized (namedEntities) {
                                    boolean isNewEntity = false;
                                    if (namedEntities.containsKey(candidate)) {
                                        namedEntity = namedEntities.get(candidate);
                                        namedEntity.incrementRepetitions();
                                    } else {
                                        Category category_entity = new Category((String) jsonObject.get("Category"));
                                        namedEntity = new NamedEntity(category_entity, (String) jsonObject.get("label"));
                                        namedEntities.put(candidate, namedEntity);
                                        categories.add(category_entity.getName());
                                        isNewEntity = true;
                                    }
    
                                    if (jsonObject.containsKey("Topics") && isNewEntity) {
                                        List<String> topics_entity = (List<String>) jsonObject.get("Topics");
                                        for (String topic : topics_entity) {
                                            Topics topico = new Topics(topic);
                                            namedEntity.addTopic(topico);
                                            topics.add(topico.getName());
                                        }
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
                    this.categories.add("OTHER");
                    this.topics.add("OTHER");
                }
                return namedEntity;
            });
    
            // Recoger los resultados y agregarlos a namedEntities
            List<NamedEntity> namedEntitiesList = namedEntitiesRDD.collect();
            for (NamedEntity namedEntity : namedEntitiesList) {
                namedEntities.put(namedEntity.getName(), namedEntity);
            }
    
            // Imprimir las entidades nombradas
            for (NamedEntity namedEntity : this.namedEntities.values()) {
                namedEntity.namedEntityPrint();
            }
    
        } catch (IOException e) {
            e.printStackTrace();
            System.exit(1);
        }
    }
    /* 
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
            List<Map<String, Object>> jsonArray = mapper.readValue(content, new TypeReference<List<Map<String, Object>>>(){});
            candidatos.foreach(candidate -> {
                boolean found = false;
                for (Map<String, Object> jsonObject : jsonArray) {
                    found = false;
                    if (jsonObject.containsKey("keywords")) {
                        List<String> keywords = (List<String>) jsonObject.get("keywords");
                        for (String keyword : keywords) {
                            if (keyword.equalsIgnoreCase(candidate)) {
                                    NamedEntity namedEntity;
                                    boolean isNewEntity = false;
                                    if (namedEntities.containsKey(candidate)) {
                                        namedEntity = namedEntities.get(candidate);
                                        namedEntity.incrementRepetitions();
                                    } else {
                                        Category category_entity = new Category((String) jsonObject.get("Category"));
                                        namedEntity = new NamedEntity(category_entity, (String) jsonObject.get("label"));
                                        namedEntities.put(candidate, namedEntity);
                                        categories.add(category_entity.getName());
                                        isNewEntity = true;
                                    }
    
                                    if (jsonObject.containsKey("Topics") && isNewEntity) {
                                        List<String> topics_entity = (List<String>) jsonObject.get("Topics");
                                        for (String topic : topics_entity) {
                                            Topics topico = new Topics(topic);
                                            namedEntity.addTopic(topico);
                                            topics.add(topico.getName());
                                        }
                                    }
                                found = true;
                                break;
                            }
                        }
                    }
                }
                if (!found && !this.namedEntities.containsKey(candidate)) {
                    NamedEntity namedEntity = new NamedEntity(new Category("OTHER"), candidate);
                    namedEntity.addTopic(new Topics("OTHER"));
                    this.namedEntities.put(candidate, namedEntity);
                    this.categories.add("OTHER");
                    this.topics.add("OTHER");
                }
            });
    
            // Imprimir las entidades nombradas
            for (NamedEntity namedEntity : this.namedEntities.values()) {
                namedEntity.namedEntityPrint();
            }
    
        } catch (IOException e) {
            e.printStackTrace();
            System.exit(1);
        }
    }
    */

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
            for (String category : this.categories) {
                System.out.println("Category: " + category);
                for (NamedEntity namedEntity : this.namedEntities.values()) {
                    if (namedEntity.getCategory().getName().equals(category)) {
                        System.out.println(namedEntity.getName() + ": " + namedEntity.getRepetitions());
                    }
                }
            }
        } else if (statsSelected.equals("top")) {
            for (String topic : this.topics) {
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