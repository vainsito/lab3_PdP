package namedEntities;

import java.util.List;
import java.util.ArrayList;

public class NamedEntity {

    private String entidad_nombrada;
    private Category category;
    private List<Topics> topics;
    private int repetitions;

    // Constructor
    public NamedEntity(Category category, String palabra) {
        this.category = category;
        this.topics = new ArrayList<>();
        this.entidad_nombrada = palabra;
        this.repetitions = 1;
    }

    // Setter
    public void addTopic(Topics topic) {
        this.topics.add(topic);
    }

    public void incrementRepetitions() {
        this.repetitions++;
    }

    // Getters
    public Category getCategory() {
        return category;
    }

    public List<Topics> getTopics() {
        return topics;
    }

    public int getRepetitions() {
        return this.repetitions;
    }

    public String getName() {
        return this.entidad_nombrada;
    }

    // Metodo para imprimir
    public void namedEntityPrint() {
        System.out.println("Named Entity: " + this.entidad_nombrada);
        this.category.categoriePrint();
        System.out.println("Repeticiones : " + this.repetitions);
        for (Topics topic : this.topics) {
            topic.topicPrint();
        }
        System.out.println("-".repeat(80));
    }
}