package net.erdfelt.android.apk;

public interface BinaryXmlListener {
    void onXmlEntry(String path, String name, Attribute... attrs);
}