package net.erdfelt.android.apk;

import net.erdfelt.android.apk.xml.Attribute;
import net.erdfelt.android.apk.xml.BinaryXmlListener;

public class BinaryXmlDump implements BinaryXmlListener {
    public void onXmlEntry(String path, String name, Attribute... attrs) {
        System.out.printf("%s -> <%s%n", path, name);
        String indent = path.replaceAll(".", " ");
        for (Attribute attr : attrs) {
            System.out.printf("%s      %s=\"%s\"%n", indent, attr.getName(), attr.getValue());
        }
    }
}
