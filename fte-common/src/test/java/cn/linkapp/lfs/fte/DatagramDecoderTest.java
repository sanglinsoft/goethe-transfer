package cn.linkapp.lfs.fte;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.embedded.EmbeddedChannel;
import org.junit.Test;
import static org.junit.Assert.*;


public class DatagramDecoderTest {

    @Test
    public void testSendDatagram() {
        String data = "KCP TEST";
        int crc = 13141516;
        int type = 1;
        int data_len = data.length();

        Datagram dg = new Datagram(crc, type, Unpooled.wrappedBuffer(data.getBytes()), data_len);
        EmbeddedChannel channel = new EmbeddedChannel(new DatagramEncoder(), new DatagramDecoder());
        channel.writeOutbound(dg);
        ByteBuf buf = channel.readOutbound();
        assertEquals(16, buf.readableBytes());
        channel.writeInbound(buf);
        Datagram decodeDg = channel.readInbound();
        assertEquals(crc, decodeDg.getCrc());
        assertEquals(type, decodeDg.getType());
        assertEquals(data_len, decodeDg.getData_len());
        assertArrayEquals(data.getBytes(), decodeDg.getBytes());
    }
}
