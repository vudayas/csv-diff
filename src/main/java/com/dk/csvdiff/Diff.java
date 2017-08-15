package com.dk.csvdiff;

import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import java.util.stream.Collector;
import java.util.stream.Collectors;

import com.dk.csvdiff.cmdline.ParsedCommandLine;
import com.dk.csvdiff.csv.Row;
import com.dk.csvdiff.csv.Sheet;
import com.dk.csvdiff.csv.SheetCollector;
import com.dk.csvdiff.i18n.Messages;

/**
 * @author darrenkennedy
 *
 * Main class
 * Reads and parses command line
 * Reads input files
 * Writes output file
 **/
public class Diff {
	public static final String DIFF_MARKER_A = "<<";
    public static final String DIFF_MARKER_B = ">>";
    public static final String RECORD_DIFF_MARKER_A = "<<<<<<";
    public static final String RECORD_DIFF_MARKER_B = ">>>>>>";

    // User input stuff
    private String idColumn;
    private boolean zeroMatchesNull;
    private boolean displayMissingRows;
    private boolean displayMatchingData;
    private String inputA;
    private String inputB;
    private String output;
    private Reader readerA;
    private Reader readerB;
    
    // For IO
    private Writer writer;
    private Consumer<String> stdOut;
    private Consumer<String> stdErr;

    // Internal data models
    private Sheet sheetA;
    private Sheet sheetB;
    private List<String> headers;
    private List<String> headersMissingA;
    private List<String> headersMissingB;
    private boolean hasDiff;

    // A consumer to write a string and append a newline
    private class LineWriter implements Consumer<String> {
    	private Consumer<String> c;
    	
    	public LineWriter(Consumer<String> c) {
    		this.c = c;
    	}
    	
		@Override
		public void accept(String t) {
			c.accept(t);
			c.accept("\n");
		}
    	
    };
    
    /**
     * Main entry point
     * @throws IOException
     */
    public static void main(String[] args) throws IOException {
        ParsedCommandLine cmd = new ParsedCommandLine();
        if (cmd.parseCommandLine(args)) {
            new Diff(cmd).makeDiff();
        }
    }
    
    // Default constructor
    private Diff() {
    	this.hasDiff = false;
    }

    /**
     * Constructor for command line operation
     * @param cmd The command line arguments as class fields
     * @throws IOException
     */
    public Diff(ParsedCommandLine cmd) throws IOException {
        this(cmd.getIdColumn(),
             cmd.getFilenameA(),
             cmd.getFilenameB(),
             new FileReader(cmd.getFilenameA()),
             new FileReader(cmd.getFilenameB()),
             cmd.getFilenameOut(),
             new FileWriter(cmd.getFilenameOut()),
             cmd.isZeroMatchesNull(),
             cmd.isDisplayMatchingData(),
             cmd.isDisplayMissingRows(),
             System.out::println,
             System.err::println);
    }

    /**
     * Constructor with all user-input and IO parameters
     * @param cmd The command line arguments as class fields
     * @throws IOException
     *
     * @param idColumn Column name for the id column
     * @param inputA name for CSV data set A
     * @param inputB name for CSV data set B
     * @param readerA reader for CSV data set A
     * @param readerB reader for CSV data set B
     * @param output name for CSV output data set
     * @param writer writer for CSV output data set
     * @param zeroMatchesNull true if '0' matches an empty cell
     * @param displayMatchingData show data for matching cells
     * @param stdOut output stream for stdOut
     * @param stdErr output stream for stdErr
     */
    public Diff(String idColumn,
                String inputA,
                String inputB,
                Reader readerA,
                Reader readerB,
                String output,
                Writer writer,
                boolean zeroMatchesNull,
                boolean displayMatchingData,
                boolean displayMissingRows,
                Consumer<String> stdOut,
                Consumer<String> stdErr) throws IOException {
    	this();
        this.idColumn = idColumn;
        this.inputA = inputA;
        this.inputB = inputB;
        this.readerA = readerA;
        this.readerB = readerB;
        this.output = output;
        this.writer = writer;
        this.zeroMatchesNull = zeroMatchesNull;
        this.displayMatchingData = displayMatchingData;
        this.displayMissingRows = displayMissingRows;
        this.stdOut = new LineWriter(stdOut);
        this.stdErr = new LineWriter(stdErr);
    }

    /**
     * Reads the input files,
     * finds the differences and
     * writes the output file.
     * @throws IOException
     */
    public void makeDiff() throws IOException {
        sheetA = new Sheet(idColumn);
        sheetB = new Sheet(idColumn);

        try {
            sheetA.parse(readerA);
        } catch (Exception e) {
        	Messages.write(stdErr, "ParseCSVFail", inputA);
            throw e;
        }
        try {
            sheetB.parse(readerB);
        } catch (Exception e) {
        	Messages.write(stdErr, "ParseCSVFail", inputB);
            throw e;
        }

        if (!getHeaders()) { // No common headers
            return;
        }
        
        printMissingColumns(inputA, headersMissingA);
        printMissingColumns(inputB, headersMissingB);

        // Find all records from A that have records (by key) in B
        // Map the CSVRecord to get the diff string if cells in the record don't match,
        // or an empty string if we are not displaying matching values,
        // or the matching values if we are displaying them.
        // Add rows to a new sheet
        final Collector<Row, Sheet, Sheet> c = new SheetCollector(idColumn);
        Sheet sheetDiff = sheetA
                        .getRows()
                        .stream()
                        .filter(r -> sheetB.contains(r.getId()))
                        .map(r -> getDiff(r, sheetB.getRow(r.getId()), displayMatchingData))
                        .filter(r -> displayMatchingData || !r.isEmpty())
                        .collect(c);

        if (sheetDiff.getRows().size() == 0) {
        	Messages.write(stdOut, "NoMatchingRecords");
            return;
        }

        List<Row> recordsMissingA = getMissingRecords(sheetA, sheetB);
        List<Row> recordsMissingB = getMissingRecords(sheetB, sheetA);

        printMissingRecords(inputA, recordsMissingA);
        printMissingRecords(inputB, recordsMissingB);

        if (!hasDiff) {
        	Messages.write(stdOut, "CommonRecordsIdentical");
            if (recordsMissingA.isEmpty() && recordsMissingB.isEmpty()) {
            	Messages.write(stdOut, "SameRecords");
                if (headersMissingA.isEmpty() && headersMissingB.isEmpty()) {
                	Messages.write(stdOut, "SameHeaders");
                	Messages.write(stdOut, "IdenticalFile");
                    return;
                }
            }
        }

        if (displayMissingRows) {
            addMissingRecords(sheetDiff, recordsMissingA, recordsMissingB);
        }

        sheetDiff.write(writer);
        Messages.write(stdOut, "WroteDiffs", output);
    }

    /*
     * Gets headers common to both input sets and
     * the headers missing between each set.
     */
    private boolean getHeaders() {
        headers = sheetA
                        .getHeaders()
                        .stream()
                        .filter(h -> sheetB.getHeaders().contains(h))
                        .sorted((h1, h2) -> h1.compareTo(h2))
                        .collect(Collectors.toList());

        headersMissingB = sheetA
                        .getHeaders()
                        .stream()
                        .filter(h -> !headers.contains(h))
                        .collect(Collectors.toList());

        headersMissingA = sheetB
                        .getHeaders()
                        .stream()
                        .filter(h -> !headers.contains(h))
                        .collect(Collectors.toList());

        if (headers.isEmpty()) {
        	Messages.write(stdOut, "NoMatchingCols");
            return false;
        }

        return true;
    }

    /*
     * Get a list of the headers missing between A and B with markers in the text
     */
    private List<String> getDiffHeaders() {
        final ArrayList<String> diffHeaders = new ArrayList<String>(headers);

        headersMissingA
            .stream()
            .forEach(h -> diffHeaders.add(DIFF_MARKER_A + h));

        headersMissingB
            .stream()
            .forEach(h -> diffHeaders.add(DIFF_MARKER_B + h));
        
        return diffHeaders;
    }

    private void printMissingColumns(String input, final List<String> headersMissing) {
        stdOut.accept(Messages.getMessage("ColumnsMissing", input));
        headersMissing
            .stream()
            .forEach(stdOut::accept);
        stdOut.accept("<end>");
    }

    private List<Row> getMissingRecords(final Sheet missingFrom, Sheet checkAgainst) {
        return checkAgainst
            .getRows()
            .stream()
            .filter(r -> !missingFrom.contains(r.getId()))
            .sorted((r1, r2) -> r1.getId().compareTo(r2.getId()))
            .map(r -> getDiff(r, r, true))
            .collect(Collectors.toList());
    }

    private void printMissingRecords(String input, List<Row> recordsMissing) {
        stdOut.accept(Messages.getMessage("RecordsNotFound", input));
        recordsMissing
            .stream()
            .sorted()
            .map(Row::getId)
            .forEach(stdOut::accept);
        stdOut.accept("<end>");
    }

    private void addMissingRecords(final Sheet sheetDiff, List<Row> recordsMissingA, List<Row> recordsMissingB) {
        if (!recordsMissingA.isEmpty()) {
            sheetDiff.addRow(new Row(new String[]{RECORD_DIFF_MARKER_A}, new String[]{idColumn}, idColumn));
            recordsMissingA
                     .stream()
                     .forEach(r -> sheetDiff.addRow(new Row(r, DIFF_MARKER_A + r.getId())));
        }

        if (!recordsMissingB.isEmpty()) {
            sheetDiff.addRow(new Row(new String[]{RECORD_DIFF_MARKER_B}, new String[]{idColumn}, idColumn));
            recordsMissingB
                     .stream()
                     .forEach(r -> sheetDiff.addRow(new Row(r, DIFF_MARKER_B + r.getId())));
        }
    }

    private Row getDiff(final Row r1, final Row r2, final boolean showIdentical) {
    	// Create a new Row with the diff values, headers and idColumn
        return new Row(headers
                .stream()
                .map(h -> {
                    if (h.equals(idColumn)) // Id column must match
                        return r1.getId();

                    String h1 = r1.getValue(h);
                    String h2 = r2.getValue(h);
                    
                    // Handle identical case
                    if (h1.equals(h2))
                        return showIdentical ? h1 : "";
                    else if (zeroMatchesNull && "0".equals(h1 + h2))
                        return showIdentical ? "0" : "";

                    // Cells differ
                    String diff = "";
                    if (!h1.isEmpty())
                        diff = r1.getValue(h) + DIFF_MARKER_A;
                    if (!h2.isEmpty())
                        diff = diff.concat(DIFF_MARKER_B + r2.getValue(h));
                    if (!diff.isEmpty())
                        hasDiff = true;
                    return diff;
                })
                // To correlate with headers we must 
                // collect all cells, including empty "" ones
                .collect(Collectors.toList()),
            headers,
            idColumn);
    }

}
