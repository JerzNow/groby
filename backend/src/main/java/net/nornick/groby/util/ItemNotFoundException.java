package net.nornick.groby.util;

public class ItemNotFoundException extends RuntimeException {
    public ItemNotFoundException(String path, Long id) {
        super("Could not find: " + path + id);
    }
}