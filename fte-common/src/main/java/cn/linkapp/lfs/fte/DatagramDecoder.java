package cn.linkapp.lfs.fte;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;

import java.util.List;

public class DatagramDecoder extends ByteToMessageDecoder {
    @Override
    protected void decode(ChannelHandlerContext channelHandlerContext,
                          ByteBuf byteBuf, List<Object> list) throws Exception {
        int crc = byteBuf.readInt();
        int type = byteBuf.readShort();
        int data_len = byteBuf.readShort();

        Datagram dg = null;

        if (data_len > 0) {
            dg = new Datagram(crc, type, Unpooled.copiedBuffer(byteBuf), data_len);
            byteBuf.readerIndex(byteBuf.readerIndex()+data_len);
        } else {
            dg = new Datagram(crc, type, null, data_len);
        }

        list.add(dg);
    }
}
