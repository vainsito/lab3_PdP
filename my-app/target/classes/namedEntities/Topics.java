package namedEntities;

// Posible idea para topic si la hacemos clase y cada topico una instancia de la clase
public class Topics {
    private String name;

    public Topics(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    // Metodo para imprimir
    public void topicPrint() {
        System.out.println("Topic name: " + this.name);
    }
}
