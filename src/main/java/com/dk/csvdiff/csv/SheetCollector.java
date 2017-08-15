package com.dk.csvdiff.csv;

import java.util.HashSet;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.function.BinaryOperator;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collector;

/**
 * Custom collector to add Rows to a Sheet
 * 
 * @author darrenkennedy
 */
public class SheetCollector implements Collector<Row, Sheet, Sheet> {
    private String idColumn;

    public SheetCollector(String idColumn) {
        this.idColumn = idColumn;
    }

    @Override
    public Supplier<Sheet> supplier() {
        return () -> new Sheet(idColumn);
    }

    @Override
    public BiConsumer<Sheet, Row> accumulator() {
        return (s, r) -> s.addRow(r);
    }

    @Override
    public BinaryOperator<Sheet> combiner() {
        return (s1, s2) -> {
            s2.getRows()
                .stream()
                .forEach(r -> s1.addRow(r));
            return s1;
        };
    }

    @Override
    public Function<Sheet, Sheet> finisher() {
        return Function.identity();
    }

    @Override
    public Set<java.util.stream.Collector.Characteristics> characteristics() {
        Set<java.util.stream.Collector.Characteristics> c = new HashSet<>();
        c.add(Characteristics.IDENTITY_FINISH);
        c.add(Characteristics.UNORDERED);
        return c;
    }

}
