package cn.linkapp.lfs.fte;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;

public class DatagramEncoder extends MessageToByteEncoder<Datagram> {
    public final static int HEAD_LEN = 8;

    @Override
    protected void encode(ChannelHandlerContext channelHandlerContext,
                          Datagram datagram, ByteBuf byteBuf) throws Exception {
        byteBuf.writeInt(datagram.getCrc());
        byteBuf.writeShort(datagram.getType());
        byteBuf.writeShort(datagram.getData_len());
        byteBuf.writeBytes(datagram.getData(), datagram.getData_len());
    }
}
