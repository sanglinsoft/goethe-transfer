package cn.linkapp.lfs.fte;

import io.netty.buffer.ByteBuf;

/**
 * Datagram表示一数据包
 */
public class Datagram {
    // 会话编号
    public static final int CONV = 10;
    public static final int SIZE = 520;

    private int crc;
    private int type;
    private int data_len;
    private ByteBuf data;

    public class Info {
        private int crc;
        private int type;
        private int data_len;

        private Info(int crc, int type, int data_len) {
            this.crc = crc;
            this.type = type;
            this.data_len = data_len;
        }

        public int getCrc() {
            return crc;
        }

        public int getType() {
            return type;
        }

        public int getData_len() {
            return data_len;
        }
    }

    public Datagram(int crc, int type, ByteBuf data, int data_len) {
        this.crc = crc;
        this.type = type;
        this.data = data;
        this.data_len = data_len;
    }

    public int getCrc() {
        return crc;
    }

    public void setCrc(int crc) {
        this.crc = crc;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public int getData_len() {
        return data_len;
    }

    public ByteBuf getData() {
        return data;
    }

    public byte[] getBytes() {
        if (this.data_len <= 0) {
            return null;
        }

        byte[] bytes = new byte[this.data_len];
        this.data.getBytes(this.data.readerIndex(), bytes);
        return bytes;
    }

    public Info getInfo() {
        return new Info(this.crc, this.type, this.data_len);
    }
}
