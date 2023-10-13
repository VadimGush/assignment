import database.Database;
import database.DbRecord;

import java.util.*;

/**
 * The service itself which handles incoming queries.
 */
public class Service {

    private final Database database;

    public Service(final Database database) {
        this.database = database;
    }

    private static Weights calculateWeights(final String string) {
        int totalCharacterValue = 0;
        double lexicalWeight = 0;

        for (int i = 0; i < string.length(); i++) {
            // if a = 1, b = 2 and so on. then the value of any given
            // character is: character_value = ASCII value - 96;
            totalCharacterValue += string.charAt(i) - 96;

            // Every character has a weight depending on its position in the string.
            // The closer the position to the beginning of the string, the higher weight will be.

            // NOTE: Please read the note in the getClosestLexically() function!
            lexicalWeight += ((string.charAt(i) - 97) / 26.) / Math.pow(26., i);
        }

        return new Weights(totalCharacterValue, lexicalWeight);
    }


    // TODO: Does this method will be called by multiple threads?
    public Result handle(final String word) {
        final Weights weights = calculateWeights(word);

        // Get the closest string in terms of total character value
        final String closestValue = database.getClosestValue(weights.totalCharacterValue);

        // Get lexically the closest string
        final String closestLexically = database.getClosestLexically(weights.lexicalWeight);

        // Write the word into all dictionaries
        database.insert(new DbRecord(word, weights.totalCharacterValue, weights.lexicalWeight));

        return new Result(closestValue, closestLexically);
    }

    /**
     * Represents the result of a handle() call.
     */
    public static class Result {
        public final String value;
        public final String lexical;

        public Result(final String value, final String lexical) {
            this.value = value;
            this.lexical = lexical;
        }
    }

    /**
     * Represents all weights for a given word.
     */
    private static class Weights {
        private final int totalCharacterValue;
        private final double lexicalWeight;

        public Weights(final int totalCharacterValue, final double lexicalWeight) {
            this.totalCharacterValue = totalCharacterValue;
            this.lexicalWeight = lexicalWeight;
        }
    }

}
