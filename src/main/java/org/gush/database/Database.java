package org.gush.database;

import java.io.Closeable;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

/**
 * Stores all the data and performs data search.
 */
public class Database implements Closeable {

    private final Persistence persistence;

    // Strings sorted by total character values
    // NOTE: Assignment doesn't specify what should happen if 2 strings
    // have the same total character value (like "ab" and "ba"). For now, I will
    // just store the latest inserted string.
    public final TreeMap<Integer, String> valueDictionary = new TreeMap<>();

    // Lexical order is preserved
    public final TreeMap<Double, String> lexicalDictionary = new TreeMap<>();

    public Database(final String logFilepath) throws IOException {
        this.persistence = new Persistence(logFilepath);
        this.initDb();
    }

    private void initDb() throws IOException {
        final List<DbRecord> logs = persistence.readFromLog();
        if (!logs.isEmpty()) {
            System.out.println("=> Loading the database from the disk");
            logs.forEach(record -> {
                lexicalDictionary.put(record.lexicalWeight, record.word);
                valueDictionary.put(record.totalCharacterValue, record.word);
            });
            System.out.println("=> Loading complete! Entries: " + logs.size());
        }
    }

    /**
     * Returns the closest word in terms of the total character value
     */
    public String getClosestValue(final int wordValue) {

        final Map.Entry<Integer, String> greaterOrEqual = valueDictionary.ceilingEntry(wordValue);

        if (greaterOrEqual != null && greaterOrEqual.getKey().equals(wordValue)) {
            // If we found a string in the tree with the same total character value
            // we should return this string
            return greaterOrEqual.getValue();
        }

        final Map.Entry<Integer, String> lower = valueDictionary.lowerEntry(wordValue);

        if (greaterOrEqual == null && lower != null) {
            return lower.getValue();
        }

        if (lower == null) {
            return greaterOrEqual != null ? greaterOrEqual.getValue() : null;
        }

        // Now we have 2 values which are not equal to the "wordValue",
        // so now we need to find the closest one.
        // NOTE: The assignment doesn't specify what should happen if we have 2 strings
        // which are both on the same distance in terms of total character value from the given string.
        // So we will prefer the lowest value always in this case.

        final int greaterValueDistance = Math.abs(wordValue - greaterOrEqual.getKey());
        final int lowerValueDistance = Math.abs(wordValue - lower.getKey());
        return greaterValueDistance < lowerValueDistance ? greaterOrEqual.getValue() : lower.getValue();
    }

    /**
     * Returns the closest word in terms of lexical order.
     */
    public String getClosestLexically(final double lexicalWeight) {

        final Map.Entry<Double, String> greaterOrEqual = lexicalDictionary.ceilingEntry(lexicalWeight);
        final Map.Entry<Double, String> lower = lexicalDictionary.lowerEntry(lexicalWeight);

        if (greaterOrEqual == null && lower != null) {
            return lower.getValue();
        }

        if (lower == null) {
            return greaterOrEqual != null ? greaterOrEqual.getValue() : null;
        }

        // NOTE: I'm a little bit confused by the notion of "lexically closest".
        // So if we have 2 values "a" and "b" and the word from the request is "ab",
        // then "a" should be the closest one to the "ab". On another hand "ax"
        // I will assume will be the closest one to the "b"?? But I might be wrong.

        final double greaterValueDistance = Math.abs(lexicalWeight - greaterOrEqual.getKey());
        final double lowerValueDistance = Math.abs(lexicalWeight - lower.getKey());
        return greaterValueDistance < lowerValueDistance ? greaterOrEqual.getValue() : lower.getValue();
    }

    public void insert(final DbRecord dbRecord) throws IOException {
        // Save to the disk first
        persistence.writeToLog(dbRecord);

        // Then to memory
        lexicalDictionary.put(dbRecord.lexicalWeight, dbRecord.word);
        valueDictionary.put(dbRecord.totalCharacterValue, dbRecord.word);
    }

    @Override
    public void close() throws IOException {
        persistence.close();
    }
}
