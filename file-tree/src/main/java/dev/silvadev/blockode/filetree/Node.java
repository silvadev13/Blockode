package dev.silvadev.blockode.filetree;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Node<T> {
    public T value;
    public Node<T> parent;
    public List<Node<T>> children;
    public boolean isExpanded;
    public int depth;

    public Node(T value) {
        this.value = value;
        this.children = new ArrayList<>();
        this.isExpanded = false;
        this.depth = 0;
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(value);
    }
}