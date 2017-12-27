package com.scms.managerserver.server;

import io.netty.channel.*;
import io.netty.handler.codec.http.*;
import io.netty.handler.ssl.SslHandler;
import io.netty.handler.stream.ChunkedNioFile;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.RandomAccessFile;

/**
 * Create by psh
 * Date: 2017/12/22
 */
@Service
@Scope("prototype")
@ChannelHandler.Sharable
public class HttpRequestHandler extends SimpleChannelInboundHandler <FullHttpRequest>{
    private static final String wsUrl;
    private static final File index;

    static {
        wsUrl = "/ws";
         String path = HttpRequestHandler.class.getClassLoader().getResource("index.html").getPath();
         index = new File(path);
    }



    @Override
    public void channelRead0(ChannelHandlerContext ctx, FullHttpRequest request) throws Exception {
        // 看是否要webscoket协议
        String uri = request.uri();

        if (wsUrl.equalsIgnoreCase(uri)) {
            //balabala 升级为webscoket协议 则增加引用计数（调用 retain()方法），并将它传递给下一 个 ChannelInboundHandler
            ctx.fireChannelRead(request.retain());
            return;
        }

        //处理 100 Continue 请求以符合 HTTP 1.1 规范
        if (HttpUtil.is100ContinueExpected(request)) {
            send100Contiue (ctx);
        }

        //处理 http请求 实现服务端支持 keep-alive 协议
        boolean isKeepAlive = HttpUtil.isKeepAlive(request);
        HttpResponse response = new DefaultHttpResponse(request.protocolVersion(),
                HttpResponseStatus.OK);

//        HttpResponse response = new DefaultHttpResponse(
//                request.protocolVersion(), HttpResponseStatus.OK);

        response.headers().set(HttpHeaderNames.CONTENT_TYPE, "text/html; charset=UTF-8");

        if (isKeepAlive) {
            HttpUtil.setContentLength(response,index.length());
            HttpUtil.setKeepAlive(response, isKeepAlive);
        }

        //将HttpResponse写回客户端 这不是一个FullHttpResponse，因为它只是响应的第一个部分，将不调用writeAndFlush
        // 等结束的时候调用， ctx.writeAndFlush(response)可以使试试效果
        ctx.write(response);

        // 将index.html写回到客户端
        RandomAccessFile file = new RandomAccessFile(index, "r");//r是只读
        //判断是否使用了加密
        System.out.println(file.length());
        if (ctx.pipeline().get(SslHandler.class) == null) {
            ctx.write(new DefaultFileRegion(file.getChannel(),0,file.length()));
        } else {
            ctx.write(new ChunkedNioFile(file.getChannel()));
        }

        //写latHttpContent并冲涮到客户端
        ChannelFuture future = ctx.writeAndFlush(LastHttpContent.EMPTY_LAST_CONTENT);

        //如果没有请求keep-alive，则在写操作完成后关闭 Channel
        future.addListener(ChannelFutureListener.CLOSE);
        if (!isKeepAlive) {
            future.addListener(ChannelFutureListener.CLOSE);
        }
    }

    //默认支持http的100 Continue规范
    private void send100Contiue(ChannelHandlerContext ctx) {
        ctx.writeAndFlush(new DefaultFullHttpResponse(HttpVersion.HTTP_1_1 , HttpResponseStatus.CONTINUE));
    }

    @Override
    public void exceptionCaught (ChannelHandlerContext ctx, Throwable e) {
        e.printStackTrace();
        ctx.close();
    }
}
