package cn.linkapp.lfs.fte;

public class FileInfo {
    private int crc;
    private String path;
    private long size;
    private String storage;

    public FileInfo(int crc, String path, long size, String storage) {
        this.crc = crc;
        this.path = path;
        this.size = size;
        this.storage = storage;
    }

    public int getCrc() {
        return crc;
    }

    public String getPath() {
        return path;
    }

    public long getSize() {
        return size;
    }

    public String getStorage() {
        return storage;
    }
}