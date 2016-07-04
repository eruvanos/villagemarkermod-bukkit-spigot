package de.siemering.plugin.villagemarker;

import java.io.IOException;
import java.io.OutputStream;
import java.util.zip.GZIPOutputStream;

final class VillageGZIPOutputStream extends GZIPOutputStream {
 
    public VillageGZIPOutputStream(final OutputStream out) throws IOException {
        super(out);
    }
 
    public void setLevel(int level) {
        def.setLevel(level);
    }
}
