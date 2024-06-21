
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import feed.Article;

import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.sql.SparkSession;

import utils.Config;
import utils.FeedsData;
import utils.JSONParser;
import utils.UserInterface;
import utils.NamedEntitiesUtils;
import utils.ArticleListMaker;
import utils.FileMaker;
import utils.HelpPrinter;

public class App { 

    public static void main(String[] args) {
        
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
    }
    

    private static void run(Config config, List<FeedsData> feedsDataArray) {
        // If para imprimir el help
        if (config.getHelp()) {
            HelpPrinter.printHelp(feedsDataArray);
            return;
        }

        if (feedsDataArray == null || feedsDataArray.size() == 0) {
            System.out.println("No feeds data found");
            return;
        }

        // <> archivo para guardar la descripcion de los articulos
        FileMaker.createFile("src/main/java/data/bigdata.txt");

        // <> Esto tambien guarda las descripciones en resources/bigdata.txt
        List<Article> allArticles = ArticleListMaker.makeArticleList(config, feedsDataArray);
        
        
        // Recorremos el array de feeds data para obtener el content xml
        if (config.getPrintFeed()) {
            System.out.println("Printing feed(s) ");
            for (Article article : allArticles) {
                article.prettyPrint();
            }
        }
        /////////////////////////////////////////////////////////////////////////////////////////////////
        
        if (config.getComputeNamedEntities()) {
            SparkSession spark = SparkSession
            .builder()
            .appName("NamedEntitiesComputation")
            .getOrCreate();
            
            JavaRDD<String> lines = spark.read().textFile("src/main/java/data/bigdata.txt").javaRDD();
            System.out.println("Computing named entities using " + config.getHeuristicConfig());
            
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
}

