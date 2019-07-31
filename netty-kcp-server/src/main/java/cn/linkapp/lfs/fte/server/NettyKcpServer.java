package cn.linkapp.lfs.fte.server;

import cn.linkapp.lfs.fte.Datagram;
import cn.linkapp.lfs.fte.DatagramDecoder;
import io.jpower.kcp.netty.ChannelOptionHelper;
import io.jpower.kcp.netty.UkcpChannel;
import io.jpower.kcp.netty.UkcpChannelOption;
import io.jpower.kcp.netty.UkcpServerChannel;
import io.netty.bootstrap.UkcpServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;

public class NettyKcpServer {
    private final int port;
    private final String redisHost;
    private final int redisPort;

    public NettyKcpServer(int port, String redisHost, int redisPort) {
        this.port = port;
        this.redisHost = redisHost;
        this.redisPort = redisPort;
    }

    public void run() throws Exception {
        System.out.println("LFS FTE Server is Running，on " + this.port + "...");

        NettyKcpServerHandler handler = new NettyKcpServerHandler(
                this.redisHost, this.redisPort);
        handler.addEventListener(); // 加载缺省的listener

        EventLoopGroup group = new NioEventLoopGroup();

        try {
            UkcpServerBootstrap bootstrap = new UkcpServerBootstrap();
            bootstrap.group(group)
                    .channel(UkcpServerChannel.class)
                    .childHandler(new ChannelInitializer<UkcpChannel>() {
                        @Override
                        protected void initChannel(UkcpChannel channel) throws Exception {
                            channel.pipeline().addLast(new DatagramDecoder(), handler);
                        }
                    });

            ChannelOptionHelper.nodelay(bootstrap, true, 20, 2, true)
                    .childOption(UkcpChannelOption.UKCP_MTU, Datagram.SIZE + 24);

            ChannelFuture future = bootstrap.bind(this.port).sync();
            future.channel().closeFuture().sync();
        } finally {
            group.shutdownGracefully().sync();
        }
    }

    public static void main(String[] args) throws Exception {
        if (args.length != 3) {
            System.err.println(
                    "Usage: " + NettyKcpServer.class.getSimpleName() + " <port>");
        }

        int port = Integer.parseInt(args[0]);
        String redisHost = args[1];
        int redisPort = Integer.parseInt(args[2]);
        new NettyKcpServer(port, redisHost, redisPort).run();
    }
}
