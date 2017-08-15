package com.dk.csvdiff.cmdline;


import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;

import com.dk.csvdiff.i18n.Messages;

/**
 * @author darrenkennedy
 *
 * Parses the command line and provides the specified
 * options and arguments as class fields
 */
public class ParsedCommandLine {
    private CommandLine cmd;
    private Options options = new Options();

    // Command line options and parameters
    private String filenameA;       // Input filename
    private String filenameB;       // Input filename
    private String filenameOut;     // Output filename
    private String idColumn;        // Input files' id column name
    private boolean displayMissingRows = false;
    private boolean displayMatchingData = true;
    private boolean zeroMatchesNull = true;  // If true then '0' matches an empty cell (i.e. 0 == "")

    /**
     * Default constructor
     */
    public ParsedCommandLine() {
        constructOptions();
    }

    /**
     * Constructor with all fields as parameters
     * @param filenameA
     * @param filenameB
     * @param filenameOut
     * @param idColumn
     * @param displayMissingRows
     * @param displayMatchingData
     * @param zeroMatchesNull
     */
    public ParsedCommandLine(String filenameA, String filenameB, String filenameOut, String idColumn, boolean displayMissingRows,
                             boolean displayMatchingData, boolean zeroMatchesNull) {
        this.filenameA = filenameA;
        this.filenameB = filenameB;
        this.filenameOut = filenameOut;
        this.idColumn = idColumn;
        this.displayMissingRows = displayMissingRows;
        this.displayMatchingData = displayMatchingData;
        this.zeroMatchesNull = zeroMatchesNull;
    }

    /**
     * Output the help / usage string
     * @param errMsg Optional message to display if the command line args entered were incorrect
     */
    public final void usage(String errMsg)
    {
        HelpFormatter formatter = new HelpFormatter();
        formatter.printHelp("Diff [-d | -diffsOnly] [-m | --showMissing] [-z | --zeroNotNull] <idColumnName> <CSV File A> <CSV File B> <Output CSV file>",
                            "CSV Diff Tool",
                            options, "\n" + errMsg);
    }

    /**
     * Parses the command line
     * @param args Command line arguments
     * @return true if parsing succeeded
     */
    public final boolean parseCommandLine(String[] args)
    {
        if (parseCommandLine(args, options))
            return getOptions();

        return false;
    }

    /*
     * Create the set of options to parse
     */
    private void constructOptions()
    {
        options.addOption("h", "help", false, Messages.getMessage("optHelp"));

        Option optDiffsOnly = Option
                        .builder("d")
                        .required(false)
                        .hasArg(false)
                        .desc(Messages.getMessage("optD"))
                        .longOpt("diffsOnly")
                        .build();
        options.addOption(optDiffsOnly);

        Option optDisplayMissingRows = Option
                        .builder("m")
                        .required(false)
                        .hasArg(false)
                        .desc(Messages.getMessage("optM"))
                        .longOpt("showMissing")
                        .build();
        options.addOption(optDisplayMissingRows);

        Option optZeroNotNull = Option
                        .builder("z")
                        .required(false)
                        .hasArg(false)
                        .argName("domain")
                        .desc(Messages.getMessage("optZ"))
                        .longOpt("zeroNotNull")
                        .build();
        options.addOption(optZeroNotNull);
    }

    /*
     * Set class fields from command line options
     * @return false if we are only displaying the help message
     */
    private boolean getOptions()
    {
        if (cmd.hasOption('h'))
        {
            usage("");
            return false;
        }

        displayMatchingData = !cmd.hasOption('d');
        displayMissingRows = cmd.hasOption('m');
        zeroMatchesNull = !cmd.hasOption('z');

        return true;
    }

    /*
     * Parse the command line and set field values from arguments
     * @return true if parsing succeeded
     */
    private boolean parseCommandLine(String args[], Options options)
    {
        CommandLineParser parser = new DefaultParser();
        try {
            cmd = parser.parse(options, args);
        } catch (ParseException e) {
            usage(Messages.getMessage("FailParse", e.getMessage()));
            return false;
        }

        String[] cmdArgs = cmd.getArgs();
        if (cmdArgs.length < 4) {
            usage(Messages.getMessage("MissingArgs"));
            return false;
        } else if (cmdArgs.length > 4) {
            usage(Messages.getMessage("TooManyArgs"));
            return false;
        }

        idColumn = cmdArgs[0];
        filenameA = cmdArgs[1];
        filenameB = cmdArgs[2];
        filenameOut = cmdArgs[3];
        return true;
    }

    /**
     * @return Input filename A
     */
    public String getFilenameA() {
        return filenameA;
    }

    /**
     * @return Input filename B
     */
    public String getFilenameB() {
        return filenameB;
    }

    /**
     * @return Output filename
     */
    public String getFilenameOut() {
        return filenameOut;
    }

    /**
     * @return The id column name on the input files
     */
    public String getIdColumn() {
        return idColumn;
    }

    /**
     * @return true if missing rows are to be displayed
     */
    public boolean isDisplayMissingRows() {
        return displayMissingRows;
    }

    /**
     * @return true if matching data are to be displayed
     */
    public boolean isDisplayMatchingData() {
        return displayMatchingData;
    }

    /**
     * @return true if '0' matches an empty cell
     */
    public boolean isZeroMatchesNull() {
        return zeroMatchesNull;
    }
}