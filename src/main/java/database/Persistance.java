package database;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * This class is responsible for saving data to the hard drive.
 * <p/>
 * Currently, it saves all the data to the log file. Similar to WriteAheadLog in
 * databases. We do that for the sake of write speeds. If we want to save storage
 * we will periodically save the entire state of the DB to the disk and clear the log file.
 * This will give us both storage saving and fast writes. Though, we need to do that in
 * a separate thread.
 */
public class Persistance implements Closeable {

    private final File logs;

    private OutputStream logsOutputStream = null;

    public Persistance(final String filepath) {
        logs = new File(filepath);
    }

    public List<DbRecord> readFromLog() throws IOException {
        if (!Files.exists(logs.toPath())) {
            return Collections.emptyList();
        }

        // NOTE: The content of the file might not fit into memory,
        // so it will be a better idea to return a Stream<DbRecord>
        final List<DbRecord> result = new ArrayList<>();
        final BufferedReader reader = new BufferedReader(new FileReader(logs));
        String line;
        while ((line = reader.readLine()) != null) {
            final String[] parts = line.split(" ");
            if (parts.length == 3) {
                // We can achieve faster reads using binary data instead of text.
                // And we will be able to save a little bit of space on the disk.
                result.add(new DbRecord(
                        parts[0],
                        Integer.parseInt(parts[2]),
                        Double.parseDouble(parts[1])
                ));
            }
        }

        return result;
    }

    private void openLogFile() throws IOException {
        if (!Files.exists(logs.toPath())) {
            Files.createFile(logs.toPath());
        }
        this.logsOutputStream = new BufferedOutputStream(new FileOutputStream(logs, true));
    }

    public void writeToLog(final DbRecord record) throws IOException {
        if (logsOutputStream == null) {
            openLogFile();
        }

        final String stringBuffer = record.word +
                ' ' +
                record.lexicalWeight +
                ' ' +
                record.totalCharacterValue +
                '\n';

        // NOTE: This is blocking IO! I can't find any API for non-blocking operations
        // on files not in POSIX specification, not in Java. Though, on SSD this code barely makes
        // any difference (My benchmarks didn't show anything), and on HDD it only slows down
        // the code by ~25% on my machine.
        //
        // This performance penalty on HDD in theory can be solved by moving FS IO operations
        // to a separate thread. But if that thread will not be able to keep up with
        // writes to our database - well, a lot of things will fail.

        logsOutputStream.write(stringBuffer.getBytes(StandardCharsets.UTF_8));
    }


    @Override
    public void close() throws IOException {
        if (logsOutputStream != null) {
            logsOutputStream.close();
        }
    }
}
