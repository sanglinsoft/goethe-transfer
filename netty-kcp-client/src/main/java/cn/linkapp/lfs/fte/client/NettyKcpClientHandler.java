package cn.linkapp.lfs.fte.client;

import cn.linkapp.lfs.fte.FileChunk;
import io.jpower.kcp.netty.UkcpChannel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

import cn.linkapp.lfs.fte.Datagram;

import java.io.File;
import java.io.FileInputStream;
import java.io.RandomAccessFile;
import java.nio.channels.FileChannel;

public class NettyKcpClientHandler extends ChannelInboundHandlerAdapter {
    private final String testFile;

    public NettyKcpClientHandler(String testFile) {
        this.testFile = testFile;
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        UkcpChannel channel = (UkcpChannel) ctx.channel();
        channel.conv(Datagram.CONV);

        RandomAccessFile destFile = new RandomAccessFile(this.testFile, "r");
        FileChannel fc = destFile.getChannel();
        int defaultLen = 8;
        long pos = 0;
        for (; ; ) {
            long size = fc.size();
            long unread = size - pos;

            if (pos >= size) {
                break;
            }

            int data_len = (unread < defaultLen) ? (int) unread : defaultLen;
            FileChunk chunk = new FileChunk(1314, 1, pos, data_len, fc);
            ctx.write(chunk);
            pos += data_len;
        }

        FileChunk endChunk = new FileChunk(1314, 2, pos, 0, null);
        ctx.writeAndFlush(endChunk).addListener(new ChannelFutureListener() {
            @Override
            public void operationComplete(ChannelFuture future) throws Exception {
                if (!future.isSuccess()) {
                    Throwable cause = future.cause();
                    cause.printStackTrace();
                }
                fc.close();
                destFile.close();
                ctx.close();
            }
        });
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        System.out.println("channelRead: " + msg.toString());
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
}
