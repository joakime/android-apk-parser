package net.erdfelt.android.apk;

import org.apache.commons.lang.StringUtils;

public class BinaryXmlDump implements BinaryXmlListener {
    public void onXmlEntry(String path, String name, Attribute... attrs) {
        System.out.printf("%s -> %s%n", path, name);
        String indent = StringUtils.leftPad("", path.length());
        for (Attribute attr : attrs) {
            System.out.printf("%s   %s=%s%n", indent, attr.getName(), attr.getValue());
        }
    }
}
