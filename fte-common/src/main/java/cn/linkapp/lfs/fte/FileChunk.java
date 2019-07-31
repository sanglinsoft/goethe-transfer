package cn.linkapp.lfs.fte;

import java.nio.channels.FileChannel;

public class FileChunk {
    private int crc;
    private int type;
    private int len;
    private long pos;
    private FileChannel channel;

    public FileChunk(int crc, int type, long pos, int len, FileChannel channel) {
        this.crc = crc;
        this.type = type;
        this.pos = pos;
        this.len = len;
        this.channel = channel;
    }

    public int getCrc() {
        return crc;
    }

    public int getType() {
        return type;
    }

    public long getPos() {
        return pos;
    }

    public int getLen() {
        return len;
    }

    public FileChannel getChannel() {
        return channel;
    }
}
