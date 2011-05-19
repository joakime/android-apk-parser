package net.erdfelt.android.apk.xml;


public interface BinaryXmlListener {
    void onXmlEntry(String path, String name, Attribute... attrs);
}