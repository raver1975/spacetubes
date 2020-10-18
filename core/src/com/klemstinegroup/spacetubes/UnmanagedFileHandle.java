package com.klemstinegroup.spacetubes;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.utils.ByteArray;

import java.io.File;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.io.Writer;

/**
 * Managed file handle. Uses an instance of StorageManager to persist file data.
 */
public class UnmanagedFileHandle extends FileHandle {

    private final ByteArray byteArray = new ByteArray();

    /**
     * Storage manager persists FileHandle data.
     */

    public UnmanagedFileHandle() {
        file = new File(".");
    }

    @Override
    public InputStream read() {
        return new InputStream() {
            private int pos = 0;

            @Override
            public int read() {
                if (pos < byteArray.size) {
                    return byteArray.get(pos++);
                } else {
                    return -1;
                }
            }
        };
    }

    @Override
    public byte[] readBytes() {
        return byteArray.toArray();
    }

    @Override
    public void writeBytes(byte[] bytes, boolean append) {
        if (!append) {
            byteArray.clear();
        }
        byteArray.addAll(bytes);
    }

    @Override
    public OutputStream write(boolean append) {
        if (!append) {
            byteArray.clear();
        }
        return new OutputStream() {
            @Override
            public void write(int b) {
                byteArray.add((byte) b);
            }
        };
    }

    @Override
    public Writer writer(boolean append, String charset) {
        try {
            return new OutputStreamWriter(write(append), charset);
        } catch (UnsupportedEncodingException e) {
            Gdx.app.error("UnmanagedFileHandle", e.getMessage());

        }
        return null;
    }


}
