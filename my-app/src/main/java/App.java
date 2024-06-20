
/*import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.io.File;

import feed.Article;
import feed.FeedParser;
import namedEntities.heuristics.Heuristic;
import namedEntities.heuristics.makeHeuristic;

import org.apache.spark.api.java.JavaPairRDD;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.sql.SparkSession;

import utils.Config;
import utils.FeedsData;
import utils.JSONParser;
import utils.UserInterface;
import utils.NamedEntitiesUtils;
import utils.ArticleListMaker;*/

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.IOException;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.io.File;

import feed.Article;
import feed.FeedParser;
import namedEntities.heuristics.Heuristic;
import namedEntities.heuristics.makeHeuristic;

import org.apache.spark.api.java.JavaPairRDD;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.sql.SparkSession;

import utils.Config;
import utils.FeedsData;
import utils.JSONParser;
import utils.UserInterface;
import utils.NamedEntitiesUtils;
import utils.ArticleListMaker;



public class App { 

    /*public static void main(String[] args) {
        
        List<FeedsData> feedsDataArray = new ArrayList<>();
        try {
            feedsDataArray = JSONParser.parseJsonFeedsData("target/classes/data/feeds.json");

        } catch (IOException e) {
            e.printStackTrace();
            System.exit(1);
        }

        UserInterface ui = new UserInterface();
        Config config = ui.handleInput(args);

        run(config, feedsDataArray);
    }*/

    public static void main(String[] args) {
        
        List<FeedsData> feedsDataArray = new ArrayList<>();
        
        try {
            InputStream is = App.class.getResourceAsStream("/data/feeds.json");
            if (is == null) {
                throw new FileNotFoundException("Cannot find resource: data/feeds.json");
            }
            BufferedReader reader = new BufferedReader(new InputStreamReader(is));
            StringBuilder sb = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                sb.append(line);
            }
            String jsonContent = sb.toString();
            feedsDataArray = JSONParser.parseJsonFeedsDataFromJsonString(jsonContent);
        } catch (IOException e) {
            e.printStackTrace();
            System.exit(1);
        }

        UserInterface ui = new UserInterface();
        Config config = ui.handleInput(args);

        run(config, feedsDataArray);
    }

    // TODO: Change the signature of this function if needed
    private static void run(Config config, List<FeedsData> feedsDataArray) {
        // If para imprimir el help
        if (config.getHelp()) {
            printHelp(feedsDataArray);
            return;
        }

        if (feedsDataArray == null || feedsDataArray.size() == 0) {
            System.out.println("No feeds data found");
            return;
        }

        // <> archivo para guardar los articulos
        File archivo = new File("src/data/bigdata.txt");
        try{
            if (!archivo.exists()) {
                archivo.createNewFile();
            }
        } catch (IOException e) {
            System.err.println("An error while creating the file");
        }
        List<Article> allArticles = ArticleListMaker.makeArticleList(config, feedsDataArray);
        // <> Esto tambien guarda las descripciones en resources/bigdata.txt
        
        // Recorremos el array de feeds data para obtener el content xml
        // TODO: Populate allArticles with articles from corresponding feeds
        if (config.getPrintFeed()) {
            System.out.println("Printing feed(s) ");
            // TODO: Print the fetched feed
            for (Article article : allArticles) {
                article.prettyPrint();
            }
        }
        /////////////////////////////////////////////////////////////////////////////////////////////////
        
        if (config.getComputeNamedEntities()) {
            SparkSession spark = SparkSession
            .builder()
            .appName("NamedEntitiesTHING")
            .getOrCreate();
            
            JavaRDD<String> lines = spark.read().textFile("src/data/bigdata.txt").javaRDD();
  
            System.out.println("Computing named entities using " + config.getHeuristicConfig());
       
            Heuristic heuristic = null;
            
            NamedEntitiesUtils entities_sorted = new NamedEntitiesUtils();
            entities_sorted.sortEntities(lines, config.getHeuristicConfig());
            

            System.out.println("\nStats: ");
            System.out.println("-".repeat(80));
        
            try {
                entities_sorted.printStats(config.getStatSelected());
            } catch (Exception e) {
                System.out.println("Error!: Stat not found, please check the stat name and try again.");
                System.exit(1);
            }
  
            spark.stop();
        }

    }

    // TODO: Maybe relocate this function where it makes more sense
    private static void printHelp(List<FeedsData> feedsDataArray) {
        System.out.println("Usage: make run ARGS=\"[OPTION]\"");
        System.out.println("Options:");
        System.out.println("  -h, --help: Show this help message and exit");
        System.out.println("  -f, --feed <feedKey>:                Fetch and process the feed with");
        System.out.println("                                       the specified key");
        System.out.println("                                       Available feed keys are: ");
        for (FeedsData feedData : feedsDataArray) {
            System.out.println("                                       " + feedData.getLabel());
        }
        System.out.println("  -ne, --named-entity <heuristicName>: Use the specified heuristic to extract");
        System.out.println("                                       named entities");
        System.out.println("                                       Available heuristic names are:");
        System.out.println("                                           - acronym: Acronym-based heuristic");
        System.out.println("                                           - preceded: Preceded word heuristic");
        System.out.println("                                           - capitalized: Capitalized word heuristic");
        // TODO: Print the available heuristics with the following format
        System.out.println("  -pf, --print-feed:                   Print the fetched feed");
        System.out.println("  -sf, --stats-format <format>:        Print the stats in the specified format");
        System.out.println("                                       Available formats are: ");
        System.out.println("                                       cat: Category-wise stats");
        System.out.println("                                       topic: Topic-wise stats");
    }

}

