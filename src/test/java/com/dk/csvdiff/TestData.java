package com.dk.csvdiff;

import java.io.FileWriter;
import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.io.StringWriter;
import java.io.Writer;

public class TestData {
    public static String nameA;
    public static String nameB;
    public static Reader inA;
    public static Reader inB;
    public static String output;
    public static Writer out;

    public static String dataA;
    public static String dataB;
    public static String dataOut;
    public static String stdOut;

    public static String idColumn;

    static {
        idColumn = "a";

        dataA = "col X,b,a,c,e\n" +
                "0,yo,7,,0\n" +
                "six,999,6,111,0\n" +
                "one,2,1,3,0\n" +
                "two,k,2,stuff,0\n" +
                "\"three, three\",hi,3,there,0\n" +
                "five,0,5,0,0\n" +
                ",,101,,\n" +
                ",,102,,";

        dataB = "a,c,b,d,col X,\"missing,col\"\n" +
                "2,stuff,k,0,two,x\n" +
                "3,there,hello,0,\"three,three\", \n" +
                "1,3,2,0,one,\",,,\"\n" +
                "4,0,0,0,four,\"\"\"\"\"\"\n" +
                "7,world,,0,,f\n" +
                "6,222,999,0,six,\n" +
                "100,,,,,\"\"\"\"\n" +
                "101,,,,0,\n" + 
                "102,,,,,";

        dataOut = "a,b,c,col X\n" +
                        "7,yo" + Diff.DIFF_MARKER_A + "," + Diff.DIFF_MARKER_B + "world,0" + Diff.DIFF_MARKER_A + "\n" +
                        "6,999,111" + Diff.DIFF_MARKER_A + Diff.DIFF_MARKER_B + "222,six\n" +
                        "1,2,3,one\n" +
                        "2,k,stuff,two\n" +
                        "3,hi" + Diff.DIFF_MARKER_A + Diff.DIFF_MARKER_B + "hello,there,\"three, three" + Diff.DIFF_MARKER_A + Diff.DIFF_MARKER_B + "three,three\"\n" +
                        "101,,," + Diff.DIFF_MARKER_B + "0\n" +
                        "102,,,\n" +
                        Diff.RECORD_DIFF_MARKER_A + "\n" +
                        Diff.DIFF_MARKER_A + "100,,,\n" +
                        Diff.DIFF_MARKER_A + "4,0,0,four\n" +
                        Diff.RECORD_DIFF_MARKER_B + "\n" +
                        Diff.DIFF_MARKER_B + "5,0,0,five\n";

        stdOut = "Columns missing from dataA:\n" + 
        		"d\n" + 
        		"missing,col\n" + 
        		"<end>\n" + 
        		"Columns missing from dataB:\n" + 
        		"e\n" + 
        		"<end>\n" + 
        		"Records not found in dataA:\n" + 
        		"100\n" + 
        		"4\n" + 
        		"<end>\n" + 
        		"Records not found in dataB:\n" + 
        		"5\n" + 
        		"<end>\n" + 
        		"Diffs written to: output data\n";
        
        nameA = "dataA";
        nameB = "dataB";
        inA = new StringReader(dataA);
        inB = new StringReader(dataB);
        output = "output data";
        out = new StringWriter();
    }
    
    /**
     * Write the test data to 3 files.
     * @param args FilenameA, FilenameB, FilenameDiff
     * @throws IOException 
     */
    public static void main(String[] args) throws IOException {
    	if (args.length != 3) {
    		System.err.println("Incorrect command line - provide 3 filenames to write data.");
    		return;
    	}
    	
    	try (Writer w = new FileWriter(args[0])) {
    		w.write(dataA);
    	}
    	try (Writer w = new FileWriter(args[1])) {
    		w.write(dataB);
    	}
    	try (Writer w = new FileWriter(args[2])) {
    		w.write(dataOut);
    	}
    	
    	System.out.println("Done");
    }
}
