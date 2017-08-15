package com.dk.csvdiff;

import static org.junit.Assert.assertTrue;

import java.io.IOException;

import org.junit.Test;

public class DiffTest 
{
	@Test
    public void testMatchingMissing() throws IOException
    {
		final StringBuilder sbOut = new StringBuilder();
		final StringBuilder sbErr = new StringBuilder();

		Diff diff = new Diff(
    			TestData.idColumn,
    			TestData.nameA,
    			TestData.nameB,
    			TestData.inA,
    			TestData.inB,
    			TestData.output,
    			TestData.out,
                false, // zero matches null
                true, // display matching data
                true, // display missing rows
                sbOut::append,
                sbErr::append);
    	
    	diff.makeDiff();
    	String testData = TestData.dataOut;
    	if (!TestData.dataOut.endsWith("\n"))
    		testData = TestData.dataOut + "\n";
    	assertTrue(TestData.out.toString().equals(testData));
    	
    	assertTrue(sbOut.toString().equals(TestData.stdOut));
    	assertTrue(sbErr.toString().isEmpty());
    }
}
