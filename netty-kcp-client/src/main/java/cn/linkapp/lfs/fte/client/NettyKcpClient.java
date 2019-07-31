package cn.linkapp.lfs.fte.client;

import cn.linkapp.lfs.fte.DatagramEncoder;
import cn.linkapp.lfs.fte.FileChunkEncoder;
import io.jpower.kcp.netty.ChannelOptionHelper;
import io.jpower.kcp.netty.UkcpChannel;
import io.jpower.kcp.netty.UkcpChannelOption;
import io.jpower.kcp.netty.UkcpClientChannel;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;

import cn.linkapp.lfs.fte.Datagram;

public class NettyKcpClient {
    private final String host;
    private final int port;
    private final String testFile;

    public NettyKcpClient(String host, int port, String testFile) {
        this.host = host;
        this.port = port;
        this.testFile = testFile;
    }

    public void run() throws Exception {
        System.out.println("LFS FTE Client connect to " + this.host + ":" + this.port + "...");

        NettyKcpClientHandler handler = new NettyKcpClientHandler(this.testFile);
        EventLoopGroup group = new NioEventLoopGroup();

        try {
            Bootstrap bootstrap = new Bootstrap();
            bootstrap.group(group)
                    .channel(UkcpClientChannel.class)
                    .handler(new ChannelInitializer<UkcpChannel>() {
                @Override
                protected void initChannel(UkcpChannel channel) throws Exception {
                    channel.pipeline().addLast(new FileChunkEncoder(), handler);
                }
            });

            ChannelOptionHelper.nodelay(bootstrap, true, 20, 2, true)
                    .option(UkcpChannelOption.UKCP_MTU, Datagram.SIZE + 24);

            ChannelFuture future = bootstrap.connect(this.host, this.port).sync();
            future.channel().closeFuture().sync();
        } finally {
            group.shutdownGracefully();
        }
    }

    public static void main(String[] args) throws Exception {
        if (args.length != 3) {
            System.err.println(
                    "Usage: " + NettyKcpClient.class.getSimpleName() + " <host> <port> <file>");
        }

        String host = args[0];
        int port = Integer.parseInt(args[1]);
        String testFile = args[2];
        new NettyKcpClient(host, port, testFile).run();
    }
}
