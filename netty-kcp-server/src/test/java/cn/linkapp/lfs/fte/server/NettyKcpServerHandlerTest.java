package cn.linkapp.lfs.fte.server;

import cn.linkapp.lfs.fte.Datagram;
import io.netty.buffer.Unpooled;
import org.junit.Test;
import static org.junit.Assert.*;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

public class NettyKcpServerHandlerTest {
    private class TestListener implements  PropertyChangeListener {
        private Datagram.Info info;
        private String name;

        @Override
        public void propertyChange(PropertyChangeEvent evt) {
            this.name = evt.getPropertyName();
            this.info = (Datagram.Info)evt.getNewValue();
        }

        private String getEvent() {
            return this.name;
        }

        private Datagram.Info getInfo() {
            return this.info;
        }
    }

    @Test
    public void testEventListener() throws Exception {
        NettyKcpServerHandler handler = new NettyKcpServerHandler("127.0.0.1", 6379);
        TestListener listener = new TestListener();
        handler.addEventListener(NettyKcpServerHandler.Event.FIRST_PACKAGE, listener);
        handler.addEventListener(NettyKcpServerHandler.Event.LAST_PACKAGE, listener);

        byte[] data = new byte[] {0x01, 0x02, 0x03, 0x04};
        Datagram dgStart = new Datagram(1314, 0, Unpooled.wrappedBuffer(data), data.length);
        handler.channelRead(null, dgStart);

        assertEquals("FIRST_PACKAGE", listener.getEvent());
        assertEquals(NettyKcpServerHandler.Event.FIRST_PACKAGE.getName(), listener.getEvent());
        assertEquals(1314, listener.getInfo().getCrc());
        assertEquals(0, listener.getInfo().getType());
        assertEquals(data.length, listener.getInfo().getData_len());

        Datagram dgNormal = new Datagram(1111, 1, null, 0);
        handler.channelRead(null, dgNormal);

        assertEquals("FIRST_PACKAGE", listener.getEvent()); // 未监听NORMAL_PACKAGE，所有状态未变

        Datagram dgLast = new Datagram(2222, 2, null, 0);
        handler.channelRead(null, dgLast);

        assertEquals("LAST_PACKAGE", listener.getEvent());
        assertEquals(2222, listener.getInfo().getCrc());
    }
}
