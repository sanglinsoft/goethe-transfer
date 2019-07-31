package cn.linkapp.lfs.fte.server;

import cn.linkapp.lfs.fte.FileManager;
import cn.linkapp.lfs.fte.RedisFileFinder;
import io.jpower.kcp.netty.UkcpChannel;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.util.CharsetUtil;

import cn.linkapp.lfs.fte.Datagram;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.nio.channels.FileChannel;

@ChannelHandler.Sharable
public class NettyKcpServerHandler extends ChannelInboundHandlerAdapter {
    public enum Event {
        FIRST_PACKAGE("FIRST_PACKAGE"),
        NORMAL_PACKAGE("NORMAL_PACKAGE"),
        LAST_PACKAGE("LAST_PACKAGE"),
        ERROR_PACKAGE("ERROR_PACKAGE");

        private String name;

        private Event(String name) {
            this.name = name;
        }

        public String getName() {
            return this.name;
        }
    }

    private Datagram.Info info;
    private final String redisHost;
    private final int redisPort;

    private PropertyChangeSupport changeSupport = new PropertyChangeSupport(this);

    public NettyKcpServerHandler(String redisHost, int redisPort) {
        this.redisHost = redisHost;
        this.redisPort = redisPort;
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        UkcpChannel channel = (UkcpChannel)ctx.channel();
        channel.conv(Datagram.CONV);

        // 指定redis finder
        // FileManager.getInstance().setFileFinder(new RedisFileFinder(redisHost, redisPort));
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        Datagram dg = (Datagram)msg;
        int crc = dg.getCrc();
        int len = dg.getData_len();
        int type = dg.getType();

        if (len > 0) {
            System.out.println("channelRead: " + new String(dg.getBytes(), CharsetUtil.UTF_8));
        }

        // 写入文件
//        FileChannel ch = FileManager.getInstance().findFile(crc);
//        if (ch != null) {
//            ch.write(dg.getData().nioBuffer(dg.getData().readerIndex(), dg.getData_len()));
//        }

        switch (type) {
            case 0:
                this.fireEvent(Event.FIRST_PACKAGE, dg.getInfo());
                break;
            case 1:
                this.fireEvent(Event.NORMAL_PACKAGE, dg.getInfo());
                break;
            case 2:
                this.fireEvent(Event.LAST_PACKAGE, dg.getInfo());
                break;
        }
    }

    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
        ctx.flush();
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        cause.printStackTrace();
        ctx.close();
    }

    public void addEventListener(PropertyChangeListener listener) {
        this.changeSupport.addPropertyChangeListener(listener);
    }

    public void addEventListener(String eventName, PropertyChangeListener listener) {
        this.changeSupport.addPropertyChangeListener(eventName, listener);
    }

    public void addEventListener(Event eventName, PropertyChangeListener listener) {
        this.changeSupport.addPropertyChangeListener(eventName.getName(), listener);
    }

    protected void addEventListener() {
        this.addEventListener(new PropertyChangeListener() {
            @Override
            public void propertyChange(PropertyChangeEvent evt) {
                System.out.println("[LogListener] " + evt.getPropertyName());
            }
        }); // 日志处理，所有数据包事件

        this.addEventListener(
                Event.LAST_PACKAGE,
                new NotifyListener()); // 结束数据包事件
    }

    protected void fireEvent(Event eventName, Datagram.Info info ) {
        Datagram.Info oldInfo = this.info;
        this.info = info;
        this.changeSupport.firePropertyChange(eventName.getName(), oldInfo, info);
    }
}
