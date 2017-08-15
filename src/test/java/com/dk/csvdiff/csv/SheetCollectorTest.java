package com.dk.csvdiff.csv;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.stream.Collector;

import org.junit.Test;

public class SheetCollectorTest {

	@Test
	public void test() {
        String[] headers1 = new String[]{"a","b","c"};
        String[] headers2 = new String[]{"a","x","c","d"};
        String[] values1 = new String[]{"1","2","3"};
        String[] values2 = new String[]{"2","100","101","102"};
        String[] values3 = new String[]{"3","103","104","105"};
        String idColumn = "c";

        Collection<Row> rows = new ArrayList<>();
        rows.add(new Row(values1, headers1, idColumn));
        rows.add(new Row(values2, headers2, idColumn));
        rows.add(new Row(values3, headers2, idColumn));
        Collector<Row, Sheet, Sheet> c = new SheetCollector(idColumn);
        Sheet s = rows.stream().collect(c);

        assertEquals("id column", idColumn, s.getIdColumn());
        Collection<String> headers = s.getHeaders();
        assertEquals(5, headers.size());
        assertTrue(headers.containsAll(Arrays.asList(new String[]{"a","b","c","d","x"})));
        rows = s.getRows();
        assertEquals(3, rows.size());
        Iterator<Row> iterRow = rows.iterator();
        RowTest.checkRow(iterRow.next(), headers1, values1, idColumn, "3", "Row 1");
        RowTest.checkRow(iterRow.next(), headers2, values2, idColumn, "101", "Row 2");
        RowTest.checkRow(iterRow.next(), headers2, values3, idColumn, "104", "Row 3");
    }
}
