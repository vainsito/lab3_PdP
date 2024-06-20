package utils;

import java.util.ArrayList;
import java.util.List;

import feed.Article;
import feed.FeedParser;
import utils.Config;


public class ArticleListMaker {
    public static List<Article> makeArticleList(Config config, List<FeedsData> feedsDataArray){
        List<Article> res = new ArrayList<>();
        boolean use_feed = config.getFeedProvided() && config.getFeedKey() != null;
        
        for (FeedsData feedData : feedsDataArray) {
            if (use_feed && config.getFeedKey().equals(feedData.getLabel())) {
                try {
                    String contenido = FeedParser.fetchFeed(feedData.getUrl());
                    res = FeedParser.parseXML(contenido);
                } catch (Exception e) {
                    e.printStackTrace();
                    System.exit(1);
                }
            } else if (use_feed && !config.getFeedKey().equals(feedData.getLabel())) {
                System.out.println("Feed key is not: " + feedData.getLabel() + " Skipping..");
            } else {
                try {
                    String contenido = FeedParser.fetchFeed(feedData.getUrl());
                    List<Article> articles = FeedParser.parseXML(contenido);
                    res.addAll(articles);
                } catch (Exception e) {
                    System.out.println("Error fetching feed: " + feedData.getLabel());
                    e.printStackTrace();
                }
            }
        }
        return res;
    }
}
