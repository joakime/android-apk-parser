package net.erdfelt.android.apk.xml;

public class Namespace {
    public static final Namespace EMPTY = new Namespace(null, null);

    private String                prefix;
    private String                uri;

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

    @Override
    public String toString() {
        if (uri == null) {
            return "";
        }
        return String.format("%s:", prefix);
    }
}
