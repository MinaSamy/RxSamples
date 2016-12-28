package net.minasamy.helloworld2;

import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.codec.http.DefaultFullHttpResponse;
import io.netty.handler.codec.http.HttpRequest;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.netty.handler.codec.http.HttpVersion;
import sun.nio.cs.UTF_32;

import java.nio.charset.Charset;

/**
 * Created by Mina.Samy on 11/30/2016.
 */

@ChannelHandler.Sharable
public class HttpHandler extends ChannelInboundHandlerAdapter {

    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
        super.channelReadComplete(ctx);
        ctx.flush();
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        super.channelRead(ctx, msg);
        if(msg instanceof HttpRequest){
            sendResponse(ctx);
        }
    }

    private void sendResponse(ChannelHandlerContext ctx){
        final DefaultFullHttpResponse response=new DefaultFullHttpResponse(HttpVersion.HTTP_1_1
        , HttpResponseStatus.OK, Unpooled.wrappedBuffer("OK".getBytes(Charset.forName("UTF-8"))));
        response.headers().add("Content-length","2");
        ctx.writeAndFlush(response);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        super.exceptionCaught(ctx, cause);
        System.out.println("Httphandler "+cause.toString());
    }
}
