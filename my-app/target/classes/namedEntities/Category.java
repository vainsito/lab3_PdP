package namedEntities;

import java.util.HashMap;

// Clase madre
public class Category {
    private String name_categorie;
    private HashMap<String, String> attributes;

    public Category(String categorie_name) {
        this.attributes = new HashMap<>();
        this.name_categorie = categorie_name;
    }

    // settters
    public void addAttribute(String key, String value) {
        this.attributes.put(key, value);
    }

    // Getters
    public String getName() {
        return name_categorie;
    }

    public HashMap<String, String> getAttributes() {
        return attributes;
    }

    // Metodo para imprimir cosas
    public void categoriePrint() {
        System.out.println("Categorie name: " + this.name_categorie);
        for (String key : this.attributes.keySet()) {
            System.out.println(key + ": " + this.attributes.get(key));
        }
    }
}
