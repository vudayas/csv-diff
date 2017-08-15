package com.dk.csvdiff.csv;

import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

import com.dk.csvdiff.i18n.Messages;
import com.opencsv.CSVReader;
import com.opencsv.CSVWriter;
import com.opencsv.RFC4180Parser;

/**
 * Represents a CSV file with an id column
 * or the differences between two CSV files.
 *
 * @author darrenkennedy
 */
public class Sheet {
    private CSVReader reader;
    private Map<String, Row> rows;
    private Set<String> cols;
    private String idColumn;

    // Default constuctor
    private Sheet() {
        this.rows = new LinkedHashMap<>();
        this.cols = new LinkedHashSet<>();
    }
    
    /**
     * Create a sheet with the specified id column
     * @param idColumn The id column name
     */
    public Sheet(String idColumn) {
    	this();
        this.idColumn = idColumn;
    }

    /**
     * Parse data from the Reader to build the Sheet data
     * @param in The Sheet data as CSV
     * @throws IOException
     */
    public void parse(Reader in) throws IOException {
        open(in);
        try {
            cols.addAll(Arrays.asList(reader.readNext()));

            if (!cols.contains(idColumn)) {
                throw new IllegalArgumentException(Messages.getMessage("IdColNotFound", idColumn));
            }

            reader
                .readAll()
                .stream()
                .forEach(r -> {
                    Row r2 = new Row(r, cols.toArray(new String[0]), idColumn);
                    rows.put(r2.getId(), r2);
                });
        } finally {
            reader.close();
        }
    }

    /**
     * Write the Sheet data as CSV
     * @param out The output writer
     * @throws IOException
     */
    public void write(Writer out) throws IOException {
        try (CSVWriter writer = new CSVWriter(out, ',')) {
            writer.writeNext(cols.toArray(new String[0]), false);
            rows
                .values()
                .stream()
                .forEach(r -> writer.writeNext(r.toArray(new String[0]), false));
        } finally {
        	out.close();
        }
    }

    private void open(Reader in) throws IOException {
        reader = new CSVReader(in, 0, new RFC4180Parser());
    }

    /**
     * Get the set of headers
     * @return The set of header
     */
    public Collection<String> getHeaders() {
        return this.cols;
    }

    /**
     * Get the set of Rows
     * @return The set of Rows
     */
    public Collection<Row> getRows() {
        return this.rows.values();
    }

    /**
     * Get the row with the specified id
     * or null if no such row exists
     * @param id The id for the row to fetch
     * @return The matching row or null
     */
    public Row getRow(String id) {
        return this.rows.get(id);
    }

    /**
     * Checks to see if a row with the specified id exists
     * @param id The id to check for
     * @return True if a row with the id exists
     */
    public boolean contains(String id) {
        return this.rows.containsKey(id);
    }

    /**
     * Get the id column name
     * @return The id column name
     */
    public String getIdColumn() {
        return this.idColumn;
    }

    /**
     * Add a Row to this Sheet
     * @param row The Row to add
     */
    public void addRow(Row row) {
        cols.addAll(row.getHeaders());
        rows.put(row.getId(), row);
    }
}
