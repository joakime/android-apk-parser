package net.erdfelt.android.apk;

import java.util.logging.Logger;

import net.erdfelt.android.apk.xml.Attribute;
import net.erdfelt.android.apk.xml.BinaryXmlListener;

public class BinaryXmlDump implements BinaryXmlListener {
    private static final Logger LOG = Logger.getLogger(BinaryXmlDump.class.getName());

    public void onXmlEntry(String path, String name, Attribute... attrs) {
        dump("%s -> <%s", path, name);
        String indent = path.replaceAll(".", " ");
        for (Attribute attr : attrs) {
            dump("%s      %s=\"%s\"", indent, attr.getName(), attr.getValue());
        }
    }

    private void dump(String format, Object... args) {
        LOG.info(String.format(format, args));
    }
}
