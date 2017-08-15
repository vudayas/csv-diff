package com.dk.csvdiff.csv;

import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

import com.dk.csvdiff.i18n.Messages;

public class Row implements Comparable<Row> {
    private String id;
    private String idColumn;
    // Map<ColName, value>
    private Map<String, String> colVals;
    private boolean isEmpty;

    // Default constructor
    private Row() {
    	this.colVals = new LinkedHashMap<>();
    	this.isEmpty = true;
    }
    
    /**
     * Create a Row with an array of values and column headers and an idColumn
     * The number of column headers must be the same or larger than the number of values.
     * The idColumn value must be in the headers.
     * @param values
     * @param headers
     * @param idColumn
     */
    public Row(String[] values, String[] headers, String idColumn) {
        this(Arrays.asList(values), headers, idColumn);
    }

    /**
     * Create a Row with a list of values and an array of column headers and an idColumn
     * The number of column headers must be the same or larger than the number of values.
     * The idColumn value must be in the headers.
     * @param values
     * @param headers
     * @param idColumn
     */
    public Row(List<String> values, String[] headers, String idColumn) {
        this(values, Arrays.asList(headers), idColumn);
    }

    /**
     * Create a Row with a list of values and a list of column headers and an idColumn
     * The number of column headers must be the same or larger than the number of values.
     * The idColumn must be in the headers.
     * The corresponding value for idColumn must not be null.
     * @param values
     * @param headers
     * @param idColumn
     * @throws IllegalArgumentException
     */
    public Row(List<String> values, List<String> headers, String idColumn) {
        this();
        
        if (values.size() > headers.size()) {
        	throw new IllegalArgumentException(Messages.getMessage("HeadersValuesMismatch"));
        }
        
        // If we have at least 2 non-empty values then the row is not empty.
        // If we have only 1 then that should be the idColumn value and the row is empty (or invalid).
        int emptyCheck = 2;
        
        for (int i = 0; i < headers.size(); i++) {
        	String value = "";
        	if (i < values.size())
        		value = values.get(i);
        	
        	this.colVals.put(headers.get(i), value);
        	
            if (emptyCheck != 0 && value != null && !value.isEmpty())
            	isEmpty = --emptyCheck != 0;
        }

        this.id = colVals.get(idColumn);
        this.idColumn = idColumn;

        if (!this.colVals.containsKey(idColumn)) {
            throw new IllegalArgumentException(Messages.getMessage("NoIdCol"));
        }

        if (this.id == null) {
            throw new IllegalArgumentException(Messages.getMessage("NullId"));
        }
    }

    /**
     * Create a Row copied from another Row with a new id value
     * @param row
     * @param newId The new id value for the Row
     */
    public Row(Row row, String newId) {
        this();
        this.colVals.putAll(row.colVals);
        this.idColumn = row.idColumn;
        this.isEmpty = row.isEmpty;
        setId(newId);
    }

    /**
     * Get the Row Id
     * @return The Row Id
     */
    public String getId() {
        return this.id;
    }

    /**
     * Set the Row Id
     * @param id New Row Id
     */
    public void setId(String id) {
        this.colVals.put(idColumn, id);
        this.id = id;
    }

    /**
     * Get the Row value for the specified column
     * @param colName The column nmae
     * @return The value for the specified column
     */
    public String getValue(String colName) {
        return this.colVals.get(colName);
    }

    /**
     * Get the set of headers for this Row
     * @return The set of headers for this Row
     */
    public Set<String> getHeaders() {
        return this.colVals.keySet();
    }

    /**
     * Gets an array of the row values in no particular order
     * @param values
     * @return Array of Row values
     */
    public String[] toArray(String[] values) {
        return colVals.values().toArray(values);
    }

    /**
     * A Row is empty if only the id column has a value
     * @return true if the row is empty
     */
    public boolean isEmpty() {
        /* The values are set on object construction so isEmpty can not change
        for (Entry<String, String> e: colVals.entrySet()) {
            if (!e.getValue().isEmpty() && !e.getKey().equals(idColumn)) {
                return false;
            }
        }

        return true;*/
    	
    	return isEmpty;
    }

    @Override
    public int compareTo(Row o) {
        if (this.equals(o))
            return 0;

        return this.id.compareTo(((Row) o).getId());
    }

    /**
     * Equality is based on the id value only
     */
    @Override
    public boolean equals(Object obj)
    {
       if (obj == null) {
          return false;
       }
       
       if (getClass() != obj.getClass()) {
          return false;
       }
       
       final Row other = (Row)obj;
       return Objects.equals(this.id, other.id);
    }

    @Override
    public int hashCode() {
        return this.id.hashCode();
    }
}
