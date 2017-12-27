package com.scms.managerserver.server;

import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.group.ChannelGroup;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import io.netty.handler.codec.http.websocketx.WebSocketServerProtocolHandler;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

/**
 * Create by psh
 * Date: 2017/12/22
 */
@Slf4j
@Service
@Scope("prototype")
@ChannelHandler.Sharable
public class TextWebSocketFrameHandler extends SimpleChannelInboundHandler<TextWebSocketFrame> {
    public final ChannelGroup channelGroup;

    TextWebSocketFrameHandler (ChannelGroup channelGroup) {
        this.channelGroup = channelGroup;
    }

    /**
     * 客户端连接
     * @param ctx 上下文
     */
    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        // 添加
        log.info("客户端与服务端连接成功");
    }

    /**
     * 客户端关闭
     *
     * @param ctx 上下文
     * @throws Exception 异常
     */
    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        // 处理httpHandler和webSocket的Handler
        TextWebSocketFrameHandler textWebSocketFrameHandler = ctx.pipeline().get(TextWebSocketFrameHandler.class);

        if (textWebSocketFrameHandler !=null) {
            ctx.close();
            log.info("客户端与服务端连接关闭成功");
        }

    }

    @Override
    public void userEventTriggered (ChannelHandlerContext ctx, Object event) throws Exception {
        if (event == WebSocketServerProtocolHandler.ServerHandshakeStateEvent.HANDSHAKE_COMPLETE) {
            //已经不使用http协议了，所以去掉http协议的HttpRequestHander
            ctx.pipeline().remove(HttpRequestHandler.class);

            channelGroup.writeAndFlush(new TextWebSocketFrame("Client connet " + channelGroup));
            channelGroup.add(ctx.channel());
        } else {
            super.userEventTriggered(ctx,event);
        }
    }

    @Override
    public void channelRead0(ChannelHandlerContext ctx, TextWebSocketFrame msg) throws Exception {
        // 加消息的引用计数，并将它写到 ChannelGroup 中所有已经连接的客户端
        channelGroup.writeAndFlush(new TextWebSocketFrame(msg.text()));
    }
}
