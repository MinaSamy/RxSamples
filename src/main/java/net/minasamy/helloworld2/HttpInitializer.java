package net.minasamy.helloworld2;

import io.netty.channel.ChannelInitializer;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.http.HttpServerCodec;

/**
 * Created by Mina.Samy on 11/30/2016.
 */
public class HttpInitializer extends ChannelInitializer<SocketChannel> {

    private  final HttpHandler httpHandler=new HttpHandler();

    @Override
    protected void initChannel(SocketChannel ch) throws Exception {
        ch.pipeline().addLast(new HttpServerCodec())
                .addLast(httpHandler);
    }
}
