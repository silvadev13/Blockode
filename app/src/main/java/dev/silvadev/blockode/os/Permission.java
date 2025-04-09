package dev.silvadev.blockode.os;

public interface Permission {
  void request();

  PermissionStatus check();
}
