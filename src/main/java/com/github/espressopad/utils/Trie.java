package com.github.espressopad.utils;

import java.util.List;
import java.util.TreeSet;
import java.util.stream.Collectors;

public class Trie extends TreeSet<String> {
    public boolean matchPrefix(String prefix) {
        return this.tailSet(prefix).stream().anyMatch(tail -> tail.startsWith(prefix));
    }

    public List<String> findCompletions(String prefix) {
        //else break;
        return this.tailSet(prefix).stream().filter(tail -> tail.startsWith(prefix)).collect(Collectors.toList());
    }
}
