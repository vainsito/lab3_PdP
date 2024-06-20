package utils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class UserInterface {

    private HashMap<String, String> optionDict;

    private List<Option> options;

    public UserInterface() {
        options = new ArrayList<Option>();
        options.add(new Option("-h", "--help", 0));
        options.add(new Option("-f", "--feed", 1));
        options.add(new Option("-ne", "--named-entity", 1));
        options.add(new Option("-pf", "--print-feed", 0));
        options.add(new Option("-sf", "--stats-format", 0));
        // Agregamos esta opcion para que el usuario pueda determinar la heuristica a
        // emplear
        optionDict = new HashMap<String, String>();
    }

    public Config handleInput(String[] args) {

        for (Integer i = 0; i < args.length; i++) {
            for (Option option : options) {
                if (option.getName().equals(args[i]) || option.getLongName().equals(args[i])) {
                    if (option.getnumValues() == 0) {
                        optionDict.put(option.getName(), null);
                    } else {
                        if (i + 1 < args.length && !args[i + 1].startsWith("-")) {
                            optionDict.put(option.getName(), args[i + 1]);
                            i++;
                        } else {
                            System.out.println("Invalid inputs");
                            System.exit(1);
                        }
                    }
                }
            }
        }

        Boolean printFeed = optionDict.containsKey("-pf");
        Boolean computeNamedEntities = optionDict.containsKey("-ne");
        Boolean statsFormat = optionDict.containsKey("-sf");
        Boolean help = optionDict.containsKey("-h");
        Boolean feed_provided = optionDict.containsKey("-f");
        // TODO: use value for heuristic config
        String heuristicConfig = optionDict.get("-ne"); // Obtengo el valor de heuristic pasada como parametro
        String feedKey = optionDict.get("-f");
        String statSelected = optionDict.get("-sf");
        if (statSelected == null) {
            statSelected = "cat";
        }

        // Creo el objeto config, y agrego la heuristic
        return new Config(help, feed_provided, printFeed, computeNamedEntities, statsFormat, feedKey, heuristicConfig,
                statSelected);
    }
}
