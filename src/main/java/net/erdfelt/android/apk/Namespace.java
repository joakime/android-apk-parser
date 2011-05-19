package net.erdfelt.android.apk;

public class Namespace {
    private String prefix;
    private String uri;

    public Namespace(String prefix, String uri) {
        super();
        this.prefix = prefix;
        this.uri = uri;
    }

    public String getPrefix() {
        return prefix;
    }

    public String getUri() {
        return uri;
    }
}
