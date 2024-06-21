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
                    article.writeDescriptionToFile("src/main/java/data/bigdata.txt");
                }
            }

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
