package com.dk.csvdiff.csv;

import static org.junit.Assert.*;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.dk.csvdiff.TestData;

import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class SheetTest {

    @Before
    public void setUp() throws Exception {}

    @After
    public void tearDown() throws Exception {}

    @Test
    public void testAddRows() {
        String[] headers1 = new String[]{"a","b","c"};
        String[] headers2 = new String[]{"a","x","c","d"};
        String[] values1 = new String[]{"1","2","3"};
        String[] values2 = new String[]{"2","100","101","102"};
        String[] values3 = new String[]{"3","103","104","105"};
        String idColumn = "c";

        Row r1 = new Row(values1, headers1, idColumn);
        Row r2 = new Row(values2, headers2, idColumn);
        Row r3 = new Row(values3, headers2, idColumn);
        Sheet s = new Sheet("c");
        s.addRow(r1);
        s.addRow(r2);
        s.addRow(r3);

        assertEquals("id column", idColumn, s.getIdColumn());
        Collection<String> headers = s.getHeaders();
        assertEquals(5, headers.size());
        assertTrue(headers.containsAll(Arrays.asList(new String[]{"a","b","c","d","x"})));
        Collection<Row> rows = s.getRows();
        assertEquals(3, rows.size());
        Iterator<Row> iterRow = rows.iterator();
        RowTest.checkRow(iterRow.next(), headers1, values1, idColumn, "3", "Row 1");
        RowTest.checkRow(iterRow.next(), headers2, values2, idColumn, "101", "Row 2");
        RowTest.checkRow(iterRow.next(), headers2, values3, idColumn, "104", "Row 3");
    }

    @Test
    public void testGetHeaders() {
        String[] headers1 = new String[]{"a","b","c"};
        String[] headers2 = new String[]{"a","x","c","d"};
        String[] values1 = new String[]{"1","2","3"};
        String[] values2 = new String[]{"2","100","101","102"};
        String[] values3 = new String[]{"3","103","104","105"};
        String idColumn = "c";

        Row r1 = new Row(values1, headers1, idColumn);
        Row r2 = new Row(values2, headers2, idColumn);
        Row r3 = new Row(values3, headers2, idColumn);
        Sheet s = new Sheet("c");
        s.addRow(r1);
        s.addRow(r2);
        s.addRow(r3);

        assertEquals(5, s.getHeaders().size());
        assertTrue(s.getHeaders().containsAll(Arrays.asList("a","b","c","d","x")));
    }

    @Test
    public void testGetRows() {
        String[] headers1 = new String[]{"a","b","c"};
        String[] headers2 = new String[]{"a","x","c","d"};
        String[] values1 = new String[]{"1","2","3"};
        String[] values2 = new String[]{"2","100","101","102"};
        String[] values3 = new String[]{"3","103","104","105"};
        String idColumn = "c";

        Row r1 = new Row(values1, headers1, idColumn);
        Row r2 = new Row(values2, headers2, idColumn);
        Row r3 = new Row(values3, headers2, idColumn);
        Sheet s = new Sheet("c");
        s.addRow(r1);
        s.addRow(r2);
        s.addRow(r3);

        assertEquals(3, s.getRows().size());
        for (Row r: s.getRows()) {
            switch (r.getId()) {
            case "3":
                RowTest.checkRow(r, headers1, values1, idColumn, r.getId(), "Row 1");
                break;
            case "101":
                RowTest.checkRow(r, headers2, values2, idColumn, r.getId(), "Row 2");
                break;
            case "104":
                RowTest.checkRow(r, headers2, values3, idColumn, r.getId(), "Row 3");
                break;
            default:
                fail("Unexpected id");
            }
        }
    }

    @Test
    public void testGetRow() {
        String[] headers1 = new String[]{"a","b","c"};
        String[] headers2 = new String[]{"a","x","c","d"};
        String[] values1 = new String[]{"1","2","3"};
        String[] values2 = new String[]{"2","100","101","102"};
        String[] values3 = new String[]{"3","103","104","105"};
        String idColumn = "c";

        Row r1 = new Row(values1, headers1, idColumn);
        Row r2 = new Row(values2, headers2, idColumn);
        Row r3 = new Row(values3, headers2, idColumn);
        Sheet s = new Sheet("c");
        s.addRow(r1);
        s.addRow(r2);
        s.addRow(r3);

        Row r = s.getRow("3");
        RowTest.checkRow(r, headers1, values1, idColumn, r.getId(), "Row 1");
        r = s.getRow("101");
        RowTest.checkRow(r, headers2, values2, idColumn, r.getId(), "Row 2");
        r = s.getRow("104");
        RowTest.checkRow(r, headers2, values3, idColumn, r.getId(), "Row 3");

        assertNull(s.getRow("no such id"));
    }

    @Test
    public void testContains() {
        String[] headers1 = new String[]{"a","b","c"};
        String[] headers2 = new String[]{"a","x","c","d"};
        String[] values1 = new String[]{"1","2","3"};
        String[] values2 = new String[]{"2","100","101","102"};
        String[] values3 = new String[]{"3","103","104","105"};
        String idColumn = "c";

        Row r1 = new Row(values1, headers1, idColumn);
        Row r2 = new Row(values2, headers2, idColumn);
        Row r3 = new Row(values3, headers2, idColumn);
        Sheet s = new Sheet("c");
        s.addRow(r1);
        s.addRow(r2);
        s.addRow(r3);

        assertTrue(s.contains("3"));
        assertTrue(s.contains("101"));
        assertTrue(s.contains("104"));

        assertFalse(s.contains("no such id"));
    }

    @Test
    public void testGetIdColumn() {
        String[] headers1 = new String[]{"a","b","c"};
        String[] headers2 = new String[]{"a","x","c","d"};
        String[] values1 = new String[]{"1","2","3"};
        String[] values2 = new String[]{"2","100","101","102"};
        String[] values3 = new String[]{"3","103","104","105"};
        String idColumn = "c";

        Row r1 = new Row(values1, headers1, idColumn);
        Row r2 = new Row(values2, headers2, idColumn);
        Row r3 = new Row(values3, headers2, idColumn);
        Sheet s = new Sheet("c");
        s.addRow(r1);
        assertEquals("c", s.getIdColumn());
        s.addRow(r2);
        assertEquals("c", s.getIdColumn());
        s.addRow(r3);
        assertEquals("c", s.getIdColumn());
    }

    @Test
    public void testParseAndWrite() throws IOException {
        parseAndWrite(new Sheet(TestData.idColumn), TestData.dataA);
        parseAndWrite(new Sheet(TestData.idColumn), TestData.dataB);
    }
    
    private void parseAndWrite(Sheet s, String testData) throws IOException {
        s.parse(new StringReader(testData));
        StringWriter writer = new StringWriter();
        s.write(writer);

        if (!testData.endsWith("\n"))
    		testData = testData + "\n";
    	assertEquals(testData, writer.toString());
    }
}
