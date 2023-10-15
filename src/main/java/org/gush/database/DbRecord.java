package org.gush.database;

public class DbRecord {

    public final int totalCharacterValue;

    public final double lexicalWeight;

    public final String word;

    public DbRecord(final String word, final int totalCharacterValue, final double lexicalWeight) {
        this.word = word;
        this.totalCharacterValue = totalCharacterValue;
        this.lexicalWeight = lexicalWeight;
    }

}
