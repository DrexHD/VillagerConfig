package me.drex.villagerconfig.util;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.Multimap;

import java.util.function.Supplier;

public class TradeTableReporter {

    private final Multimap<String, String> errors;
    private final Multimap<String, String> warnings;
    private final Supplier<String> nameFactory;
    private String name;

    public TradeTableReporter() {
        this(HashMultimap.create(), HashMultimap.create(), () -> "");
    }

    public TradeTableReporter(Multimap<String, String> errors, Multimap<String, String> warnings, Supplier<String> nameFactory) {
        this.errors = errors;
        this.warnings = warnings;
        this.nameFactory = nameFactory;
    }

    private String getName() {
        if (this.name == null) {
            this.name = this.nameFactory.get();
        }
        return this.name;
    }

    public void error(String message) {
        this.errors.put(this.getName(), message);
    }

    public void warn(String message) {
        this.errors.put(this.getName(), message);
    }

    public Multimap<String, String> getErrors() {
        return ImmutableMultimap.copyOf(this.errors);
    }

    public Multimap<String, String> getWarnings() {
        return ImmutableMultimap.copyOf(this.warnings);
    }

    public TradeTableReporter makeChild(String name) {
        return new TradeTableReporter(this.errors, this.warnings, () -> this.getName() + name);
    }

    public TradeTableReporter withTable(String name) {
        return new TradeTableReporter(HashMultimap.create(this.errors), HashMultimap.create(this.warnings), () -> this.getName() + name);
    }


}
