package com.klemstinegroup.spacetubes;

import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.utils.ByteArray;

import java.io.*;

public class MemoryFileHandle extends FileHandle {
    private final ByteArray byteArray = new ByteArray();

    public MemoryFileHandle() {
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
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
