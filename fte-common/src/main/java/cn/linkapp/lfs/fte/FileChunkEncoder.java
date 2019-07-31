package cn.linkapp.lfs.fte;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;

public class FileChunkEncoder extends MessageToByteEncoder<FileChunk> {
    public final static int HEAD_LEN = 8;

    @Override
    protected void encode(ChannelHandlerContext channelHandlerContext,
                          FileChunk chunk, ByteBuf byteBuf) throws Exception {
        int type = chunk.getType();
        byteBuf.writeInt(chunk.getCrc());
        byteBuf.writeShort(type);
        byteBuf.writeShort(chunk.getLen());

        if (type != 2) {
            byteBuf.writeBytes(chunk.getChannel(), chunk.getPos(), chunk.getLen());
        }

        System.out.println("byteBuf=" + byteBuf.readableBytes());
    }
}
