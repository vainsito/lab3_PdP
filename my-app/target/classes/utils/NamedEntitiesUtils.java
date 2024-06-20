package utils;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.spark.api.java.JavaRDD;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.Set;
import java.util.HashSet;

import feed.Article;
import namedEntities.Category;
import namedEntities.NamedEntity;
import namedEntities.Topics;
import namedEntities.heuristics.Heuristic;
import namedEntities.heuristics.makeHeuristic;

// Clase que se encarga de ordenar las entidades y de imprimir las estadistica
public class NamedEntitiesUtils {
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

        Heuristic heuristica = new makeHeuristic(); //aca
        JavaRDD<String> candidatos = null; // aca

        try{
            candidatos = heuristica.extractCandidates(lines, heuristic);
        } catch (IllegalArgumentException e) {
            System.exit(1);
        } 
        
        // Esto es para que funcione temporalmente lo que sigue
        List<String> candidatosLISTA = candidatos.collect(); // <---

        try { 
            String content = new String(Files.readAllBytes(Paths.get("src/main/resources/dictionary.json")),
                    StandardCharsets.UTF_8);
            JSONArray jsonArray = new JSONArray(content);

            // Mapa para almacenar las entidades nombradas, utilizando namedEntities

            candidatos.foreach(candidate -> {
                for (int pos = 0; pos < jsonArray.length(); pos++) {
                    JSONObject jsonObject = jsonArray.getJSONObject(pos);
                    if (jsonObject.has("keywords")) {
                        JSONArray keywords = jsonObject.getJSONArray("keywords");
                        for (int i = 0; i < keywords.length(); i++) {
                            String keyword = keywords.getString(i);
    
                            if (keyword.equalsIgnoreCase(candidate)) {
                                synchronized (namedEntities) { // el compadre GTP dice que hay que sincronizar esto..
                                    NamedEntity namedEntity;   // ...por que lo modifican varios worker
                                    boolean isNewEntity = false;
                                    if (namedEntities.containsKey(candidate)) { // race condition
                                        namedEntity = namedEntities.get(candidate);
                                        namedEntity.incrementRepetitions();
                                    } else {
                                        Category category_entity = new Category(jsonObject.getString("Category"));
                                        namedEntity = new NamedEntity(category_entity, jsonObject.getString("label"));
                                        namedEntities.put(candidate, namedEntity);
                                        categories.add(category_entity.getName());
                                        isNewEntity = true;
                                    }
    
                                    if (jsonObject.has("Topics") && isNewEntity) { 
                                        JSONArray topics_entity = jsonObject.getJSONArray("Topics");
                                        for (int j = 0; j < topics_entity.length(); j++) {
                                            Topics topico = new Topics(topics_entity.getString(j));
                                            namedEntity.addTopic(topico);
                                            topics.add(topico.getName());
                                        }
                                    }
                                } // la sincronizacion acaba aqui. (es ridiculo sincronizar la verdad, pierde la gracia usar Spark)
                                break;
                            }
                        }
                    }
                }
                if (!this.namedEntities.containsKey(candidate)) {
                    NamedEntity namedEntity = new NamedEntity(new Category("OTHER"), candidate);
                    namedEntity.addTopic(new Topics("OTHER"));
                    this.namedEntities.put(candidate, namedEntity);
                    // Puede cambiar lo d abajo
                    this.categories.add("OTHER");
                    this.topics.add("OTHER");
                }
            }); // Aqui termina el for each 


            // <> no se necesita un collect de nada por que solo utilizamos los datos del RDD y no creamos ni modificamos uno nuevo...
            // YYY ENTONCES el print de los datos los dejamos como estan, con el namedEntities.........................
            // Todo este codigo es adivinar eh xD

            // Imprimir las entidades nombradas
            for (NamedEntity namedEntity : this.namedEntities.values()) {
                namedEntity.namedEntityPrint();
            }

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