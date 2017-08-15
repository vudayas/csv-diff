package com.dk.csvdiff;

import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.io.StringWriter;
import java.io.Writer;

import org.junit.Test;

public class DiffTest 
{
	@Test
    public void testDisplayMatchingDisplayMissing() throws IOException
    {
        Reader inA = new StringReader(TestData.dataA);
        Reader inB = new StringReader(TestData.dataB);
        Writer out = new StringWriter();

		final StringBuilder sbOut = new StringBuilder();
		final StringBuilder sbErr = new StringBuilder();

		Diff diff = new Diff(
    			TestData.idColumn,
    			TestData.nameA,
    			TestData.nameB,
    			inA,
    			inB,
    			TestData.output,
    			out,
                false, // zero matches null
                true, // display matching data
                true, // display missing rows
                sbOut::append,
                sbErr::append);
    	
    	diff.makeDiff();
    	String testData = TestData.dataOutDisplayMatchingDisplayMissing;
    	if (!TestData.dataOutDisplayMatchingDisplayMissing.endsWith("\n"))
    		testData = TestData.dataOutDisplayMatchingDisplayMissing + "\n";
    	assertTrue(out.toString().equals(testData));
    	
    	assertTrue(sbOut.toString().equals(TestData.stdOut));
    	assertTrue(sbErr.toString().isEmpty());
    }

	@Test
    public void testZeroMatchingNull() throws IOException
    {
        Reader inA = new StringReader(TestData.dataA);
        Reader inB = new StringReader(TestData.dataB);
        Writer out = new StringWriter();

        final StringBuilder sbOut = new StringBuilder();
		final StringBuilder sbErr = new StringBuilder();

		Diff diff = new Diff(
    			TestData.idColumn,
    			TestData.nameA,
    			TestData.nameB,
    			inA,
    			inB,
    			TestData.output,
    			out,
                true, // zero matches null
                false, // display matching data
                false, // display missing rows
                sbOut::append,
                sbErr::append);
    	
    	diff.makeDiff();
    	String testData = TestData.dataOutZeroMatchingNull;
    	if (!TestData.dataOutZeroMatchingNull.endsWith("\n"))
    		testData = TestData.dataOutZeroMatchingNull + "\n";
    	assertTrue(out.toString().equals(testData));
    	
    	assertTrue(sbOut.toString().equals(TestData.stdOut));
    	assertTrue(sbErr.toString().isEmpty());
    }

	@Test
    public void testZeroNotMatchingNullNoDisplay() throws IOException
    {
        Reader inA = new StringReader(TestData.dataA);
        Reader inB = new StringReader(TestData.dataB);
        Writer out = new StringWriter();

		final StringBuilder sbOut = new StringBuilder();
		final StringBuilder sbErr = new StringBuilder();

		Diff diff = new Diff(
    			TestData.idColumn,
    			TestData.nameA,
    			TestData.nameB,
    			inA,
    			inB,
    			TestData.output,
    			out,
                false, // zero matches null
                false, // display matching data
                false, // display missing rows
                sbOut::append,
                sbErr::append);
    	
    	diff.makeDiff();
    	String testData = TestData.dataOutZeroNotMatchingNullNoDisplay;
    	if (!TestData.dataOutZeroNotMatchingNullNoDisplay.endsWith("\n"))
    		testData = TestData.dataOutZeroNotMatchingNullNoDisplay + "\n";
    	assertTrue(out.toString().equals(testData));
    	
    	assertTrue(sbOut.toString().equals(TestData.stdOut));
    	assertTrue(sbErr.toString().isEmpty());
    }
}
