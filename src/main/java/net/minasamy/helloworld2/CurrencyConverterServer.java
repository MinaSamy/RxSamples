package net.minasamy.helloworld2;

import io.netty.channel.ChannelPipeline;
import io.netty.handler.codec.LineBasedFrameDecoder;
import io.netty.handler.codec.string.StringDecoder;
import io.reactivex.netty.channel.Connection;
import io.reactivex.netty.protocol.tcp.server.ConnectionHandler;
import io.reactivex.netty.protocol.tcp.server.TcpServer;
import rx.Observable;
import rx.functions.Action1;
import rx.functions.Func1;

import java.math.BigDecimal;
import java.nio.charset.Charset;
import java.util.concurrent.TimeUnit;

/**
 * Created by Mina.Samy on 12/5/2016.
 */
public class CurrencyConverterServer {

    private final static BigDecimal RATE = new BigDecimal("1.5");


    public static void startServer() {
        TcpServer.newServer(8000)
                .<String, String>pipelineConfigurator(new Action1<ChannelPipeline>() {
                    @Override
                    public void call(ChannelPipeline entries) {
                        entries.addLast(new LineBasedFrameDecoder(1024));
                        entries.addLast(new StringDecoder(Charset.forName("UTF-8")));
                    }
                }).start(new ConnectionHandler<String, String>() {
            @Override
            public Observable<Void> handle(Connection<String, String> newConnection) {
                Observable<String> output = newConnection.getInput()
                        .map(new Func1<String, BigDecimal>() {
                            @Override
                            public BigDecimal call(String s) {
                                return new BigDecimal(s);
                            }
                        })
                        .flatMap(new Func1<BigDecimal, Observable<String>>() {
                            @Override
                            public Observable<String> call(BigDecimal bigDecimal) {
                                return convert(bigDecimal);
                            }
                        });
                return newConnection.writeAndFlushOnEach(output);
            }
        }).awaitShutdown();
    }

    static Observable<String> convert(BigDecimal value) {
        return Observable.just(value.multiply(RATE))
                .map(new Func1<BigDecimal, String>() {
                    @Override
                    public String call(BigDecimal bigDecimal) {
                        return value + " euro is " + bigDecimal + " USD";
                    }
                }).delay(1, TimeUnit.SECONDS);
    }
}
