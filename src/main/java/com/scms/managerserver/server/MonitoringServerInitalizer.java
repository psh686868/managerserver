package com.scms.managerserver.server;

import io.netty.channel.Channel;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.group.ChannelGroup;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpServerCodec;
import io.netty.handler.codec.http.websocketx.WebSocketServerProtocolHandler;
import io.netty.handler.stream.ChunkedWriteHandler;


/**
 * Create by psh
 * Date: 2017/12/22
 */
public class MonitoringServerInitalizer extends ChannelInitializer<Channel>{
    private final ChannelGroup channelGroup;

    public MonitoringServerInitalizer(ChannelGroup channels) {
        this.channelGroup = channels;
    }

    @Override
    protected void initChannel(Channel ch) throws Exception {
        ChannelPipeline pipeline = ch.pipeline();

        //http

        //将请求和应答消息编码或者解码为HTTP消息
        pipeline.addLast(new HttpServerCodec());
        //A ChannelHandler that adds support for writing a large data stream asynchronously neither spending a lot of memory nor getting OutOfMemoryError
        pipeline.addLast(new ChunkedWriteHandler());
        //它的作用是讲多个消息转换为单一的FullHttpRequest或FullHttpResponse,原因是Http解码器在每个消息中会生成多个消息对象。
        pipeline.addLast(new HttpObjectAggregator(Integer.MAX_VALUE));
        pipeline.addLast(new HttpRequestHandler());

        //wsc
        pipeline.addLast(new WebSocketServerProtocolHandler("/ws"));
        pipeline.addLast(new TextWebSocketFrameHandler(channelGroup));


    }


}
