package dev.silvadev.blockode.exc.todo;

public class TODO {
  public static void TODO(final String funName) {
    throw new TODOException(funName);
  }
}
