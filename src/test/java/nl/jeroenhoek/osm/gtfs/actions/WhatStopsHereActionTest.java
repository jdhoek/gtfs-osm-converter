package nl.jeroenhoek.osm.gtfs.actions;

import org.junit.Test;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.junit.Assert.*;

public class WhatStopsHereActionTest {
    @Test
    public void test() {
        Set<List<String>> set = new HashSet<>();
        List<String> one = Arrays.asList("a", "b");
        List<String> two = Arrays.asList("a", "b");
        set.add(one);
        set.add(two);

        System.out.println(set.size());
    }
}