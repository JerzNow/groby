package net.nornick.groby.model;

public enum FileFormat {
    JPG("J"), PNG("P"), BMP("B");
    private String code;

    FileFormat(String code) {
        this.code = code;
    }

    public String getCode() {
        return code;
    }
}
