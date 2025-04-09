package dev.silvadev.blockode.filetree.utils;

import dev.silvadev.blockode.filetree.Node;
import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class FileTreeUtils {

    public static List<Node<File>> toNodes(File dir) {
        File[] fileArray = dir.listFiles();
        if (fileArray == null || fileArray.length == 0) return new ArrayList<>();
        
        List<File> dirs = new ArrayList<>();
        List<File> files = new ArrayList<>();
        
        for(File file : fileArray) {
        	if(file.isDirectory()) {
        		dirs.add(file);
        	} else {
        		files.add(file);
        	}
        }
        
        List<File> allSorted = new ArrayList<>(dirs);
        allSorted.addAll(files);
        
        List<Node<File>> nodes = new ArrayList<>();
        for(File file : allSorted) {
        	nodes.add(new Node<>(file));
        }
        
    	return nodes;
    }

    public static <T> void addChildren(Node<T> parent, List<Node<T>> children) {
        if (children == null || children.isEmpty()) return;

        parent.isExpanded = true;
        parent.children.addAll(children);
        for (Node<T> child : children) {
            child.parent = parent;
            child.depth = parent.depth + 1;
        }
    }

    public static <T> void removeChildren(Node<T> parent) {
        if (parent.children.isEmpty()) return;

        parent.isExpanded = false;
        for (Node<T> child : parent.children) {
            child.parent = null;
            child.depth = 0;
            if (child.isExpanded) {
                child.isExpanded = false;
                removeChildren(child);
            }
        }
        parent.children.clear();
    }

    public static <T> List<Node<T>> getDescendants(Node<T> parent) {
        List<Node<T>> descendants = new ArrayList<>();
        getDescendantsRecursive(parent, descendants);
        return descendants;
    }

    private static <T> void getDescendantsRecursive(Node<T> parent, List<Node<T>> descendants) {
        descendants.addAll(parent.children);
        for (Node<T> child : parent.children) {
            if (child.isExpanded) {
                getDescendantsRecursive(child, descendants);
            }
        }
    }
}