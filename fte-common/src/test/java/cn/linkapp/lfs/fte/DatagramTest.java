package cn.linkapp.lfs.fte;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

public class DatagramTest {
    private Datagram defaultDg;

    @Before
    public void createDefaultDatagram() {
        String data = "KCP TEST";
        int crc = 13141516;
        int type = 1;
        int data_len = data.length();

        defaultDg = new Datagram(crc, type, Unpooled.wrappedBuffer(data.getBytes()), data_len);
    }

    @Test
    public void testNewDatagram() {
        assertEquals(13141516, defaultDg.getCrc());
        assertEquals(1, defaultDg.getType());
        assertEquals(8, defaultDg.getData_len());
        assertArrayEquals("KCP TEST".getBytes(), defaultDg.getData().array());
    }

    @Test
    public void testSet() {
        defaultDg.setCrc(1111);
        assertEquals(1111, defaultDg.getCrc());

        defaultDg.setType(0);
        assertEquals(0, defaultDg.getType());
    }
}
