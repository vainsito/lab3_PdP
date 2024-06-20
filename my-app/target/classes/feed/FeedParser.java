package feed;

//import java.io.Serializable;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;
import java.util.ArrayList;

import java.util.stream.Stream; // Importamos Stream para poder usarlo en el metodo parseXML

public class FeedParser {

    public static List<Article> parseXML(String xmlData) {
        // La idea es que tenemos un string con toda la data, y tenemos que ir
        // matcheando cosas
        // Primero tenemos que matchear el tag <item> y luego matchear los tags <title>,
        // <description>, <pubDate>, <link>
        // Usamos un for para recorrer el string y matchear los tags
        // Entonces, tenemos que hacer un split por <item> y luego por cada uno de esos
        // splits, matchear los tags
        List<Article> res = new ArrayList<>();

        try {
            for (String item : xmlData.split("<item>")) {
                boolean contains_label = Stream.of("<title>", "</title>", "<description>",
                        "</description>", "<pubDate>", "</pubDate>",
                        "<link>", "</link>").allMatch(item::contains);
                if (contains_label) {
                    String title = item.split("<title>")[1].split("</title>")[0];
                    String description = item.split("<description>")[1].split("</description>")[0];
                    String pubDate = item.split("<pubDate>")[1].split("</pubDate>")[0];
                    String link = item.split("<link>")[1].split("</link>")[0];
                    Article article = new Article(title, description, pubDate, link);
                    res.add(article);
                    article.writeDescriptionToFile("src/data/bigdata.txt");
                    
                }
            }

            // If por si res no contiene nada
            if (res.size() == 0) {
                System.out.println("No se encontraron articulos");
                System.exit(1);
            }
        } catch (Exception e) {
            System.out.println("Error al parsear el XML");
            System.exit(1);
        }

        
        return res;
    }

    public static String fetchFeed(String feedURL) throws MalformedURLException, IOException, Exception {

        URL url = new URL(feedURL);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();

        connection.setRequestMethod("GET");
        connection.setRequestProperty("Content-Type", "application/json");

        // TODO: Cambiar el user-agent al nombre de su grupo.
        // Si todos los grupos usan el mismo user-agent, el servidor puede bloquear las
        // solicitudes.
        connection.setRequestProperty("user_agent", "Grupo_00_Lab2_2024");
        connection.setConnectTimeout(5000);
        connection.setReadTimeout(5000);

        int status = connection.getResponseCode();
        if (status != 200) {
            throw new Exception("HTTP error code: " + status);
        } else {
            BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            String inputLine;
            StringBuffer content = new StringBuffer();
            while ((inputLine = in.readLine()) != null) {
                content.append(inputLine);
            }
            in.close();
            connection.disconnect();
            return content.toString();
        }
    }
}
