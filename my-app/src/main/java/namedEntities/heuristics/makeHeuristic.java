package namedEntities.heuristics;
import org.apache.spark.api.java.JavaRDD;
import java.text.Normalizer;
import java.util.List;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class makeHeuristic implements Heuristic {

    public JavaRDD<String> extractCandidates(JavaRDD<String> lines, String heuristic) {
        return lines.flatMap(line -> {
            // Remove unwanted characters
            line = line.replaceAll("[-+.^:\",]", "");
            // Normalize the string to remove diacritics
            line = Normalizer.normalize(line, Normalizer.Form.NFD);
            line = line.replaceAll("\\p{M}", "");

            // match choosen heuristic
            String heuristic_result;
            switch (heuristic) {
                case "acronym":
                    heuristic_result = "[A-Z]{3,5}";
                    break;
                case "preceded":
                    heuristic_result = "(?:Sr\\\\.|Sra\\\\.|Dr\\\\.|Dra\\\\.|Lic\\\\.|Ing\\\\.|el|El|la|La|los|Los|las|Las|yo|tu|ella|nosotros|vosotros|ellos|ellas)\\\\s([A-Z][a-z]+(?:\\\\s[A-Z][a-z]+)?)";
                    break;
                case "capitalized":
                    heuristic_result = "[A-Z][a-z]+(?:\\\\s[A-Z][a-z]+)*";
                    break;
                default:
                    throw new IllegalArgumentException("Error!: Heuristic not found, please check the heuristic name and try again.");
            }
            // Pattern to match capitalized words
            Pattern pattern = Pattern.compile(heuristic_result);

            // Find and collect matches
            Matcher matcher = pattern.matcher(line);
            List<String> candidates = new ArrayList<>();
            while (matcher.find()) {
                candidates.add(matcher.group());
            }

            // Convert the list to an iterator
            return candidates.iterator();
        });
    }
}
