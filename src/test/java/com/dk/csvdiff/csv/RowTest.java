package com.dk.csvdiff.csv;

import static org.junit.Assert.*;

import com.dk.csvdiff.csv.Row;
import com.dk.csvdiff.i18n.Messages;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import java.util.Arrays;

public class RowTest {
    @Rule
    public ExpectedException exception = ExpectedException.none();

    @Before
    public void setUp() throws Exception {}

    @After
    public void tearDown() throws Exception {}

    @Test
    // This also tests getValue() and getHeaders()
    public void testConstructors() {
        String[] headers = new String[]{"a","b","c"};
        String[] values = new String[]{"1","2","3"};

        Row r1 = new Row(values, headers, "a");
        Row r2 = new Row(Arrays.asList(values), headers, "b");
        Row r3 = new Row(Arrays.asList(values), Arrays.asList(headers), "c");
        Row r4 = new Row(r3, "4");
        Row r5 = new Row(new String[]{"1"}, new String[]{"a"}, "a");

        checkRow(r1, headers, values, "a", "1", "Row 1");
        checkRow(r2, headers, values, "b", "2", "Row 2");
        checkRow(r3, headers, values, "c", "3", "Row 3");
        checkRow(r4, headers, new String[]{"1","2","4"}, "c", "4", "Row 4");
        checkRow(r5, new String[]{"a"}, new String[]{"1"}, "a", "1", "Row 5");
    }

    @Test
    public void testBadLists() {
        exception.expect(IllegalArgumentException.class);
        exception.expectMessage(Messages.getMessage("HeadersValuesMismatch"));
        new Row(new String[]{"1","2"}, new String[]{"a"}, "a");
    }
    
    @Test
    public void testShortValuesList() {
        String[] headers = new String[]{"a","b","c"};
    	String[][] valuesList = new String[][] {
    		{"1","2"},
    		{"1"},
    		{"1","2"}
    	};
    	
    	Row r1 = new Row(valuesList[0], headers, "a");
    	Row r2 = new Row(valuesList[1], headers, "a");
    	Row r3 = new Row(valuesList[2], headers, "b");
    	
        checkRow(r1, headers, valuesList[0], "a", "1", "Row 1");
        checkRow(r2, headers, valuesList[1], "a", "1", "Row 2");
        checkRow(r3, headers, valuesList[2], "b", "2", "Row 3");
    }
    
    @Test
    public void testNullId() {
        exception.expect(IllegalArgumentException.class);
        exception.expectMessage(Messages.getMessage("NullId"));
        new Row(new String[]{null}, new String[]{"a"}, "a");
    }

    @Test
    public void testBadIdColumn() {
        exception.expect(IllegalArgumentException.class);
        exception.expectMessage(Messages.getMessage("NoIdCol"));
        new Row(new String[]{"1"}, new String[]{"a"}, "b");
    }

    @Test
    public void testId() {
        String[] headers = new String[]{"a","b","c"};
        String[] values = new String[]{"1","2","3"};

        Row r1 = new Row(values, headers, "a");

        String newId = "testId";
        r1.setId(newId);
        assertEquals(newId, r1.getId());
    }

    @Test
    public void testToArray() {
        String[] headers = new String[]{"a","b","c"};
        String[] values = new String[]{"1","2","3"};

        Row r1 = new Row(values, headers, "a");
        assertTrue(Arrays.asList(r1.toArray(new String[0])).containsAll(Arrays.asList(values)));
        assertEquals(values.length, r1.toArray(new String[0]).length);
    }

    @Test
    public void testEquals() {
        String[] headers = new String[]{"a","b","c"};
        String[] values = new String[]{"1","2","3"};

        Row r1 = new Row(values, headers, "a");
        Row r2 = new Row(Arrays.asList(values), headers, "a");
        assertEquals(r1,r2);

        Row r3 = new Row(values, headers, "b");
        assertNotEquals(r1, r3);

        r2.setId("4");
        assertNotEquals(r1, r2);

        Row r4 = new Row(new String[]{"1"}, new String[]{"a"}, "a");
        assertEquals(r1, r4);
    }

    @Test
    public void testCompareTo() {
        String[] headers = new String[]{"a","b","c"};
        String[] values = new String[]{"1","2","3"};

        Row r[] = new Row[] { // Id values in lexographical order (not numerical) as they are strings
        		new Row(new String[]{"-1","4","3"}, headers, "a"),
        		new Row(new String[]{Integer.toString(Integer.MIN_VALUE),"5","3"}, headers, "a"),
        		new Row(new String[]{"0","3","3"}, headers, "a"),
        		new Row(new String[]{"1","2","3"}, headers, "a"),
        		new Row(new String[]{Integer.toString(Integer.MAX_VALUE),"a","3"}, headers, "a")};
        
        for (int i = 0; i < r.length; i++) {
        	if (i > 0)
        		assertTrue(String.valueOf(i), r[i].compareTo(r[i-1]) > 0);
        	
        	if (i+1 < r.length)
        		assertTrue(String.valueOf(i), r[i].compareTo(r[i+1]) < 0);
        	
        	assertTrue(String.valueOf(i), r[i].compareTo(r[i]) == 0);
        }
    }
    
    @Test
    public void testIsEmpty() {
    	assertFalse(new Row(new String[]{"0","3","3"}, new String[]{"a","b","c"}, "a").isEmpty());
    	assertTrue(new Row(new String[]{"","3",""}, new String[]{"a","b","c"}, "b").isEmpty());
    	assertTrue(new Row(new String[]{"3"}, new String[]{"a"}, "a").isEmpty());
    }
    
    public static void checkRow(Row r, String[] headers, String[] values, String idColumn, String idValue, String msg) {
        assertEquals(msg + " headers count", headers.length, r.getHeaders().size());
        assertTrue(msg + " headers", r.getHeaders().containsAll(Arrays.asList(headers)));

        for (int i = 0; i < headers.length; i++) {
        	String value = "";
        	if (i < values.length)
        		value = values[i];
            assertEquals(msg + " value " + i, value, r.getValue(headers[i]));
        }

        assertEquals(msg + " id column", idValue, r.getValue(idColumn));
        assertEquals(msg + " id value", idValue, r.getId());
    }

}
