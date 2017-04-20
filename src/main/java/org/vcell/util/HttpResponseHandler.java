/*
 * Copyright (C) 1999-2011 University of Connecticut Health Center
 *
 * Licensed under the MIT License (the "License").
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at:
 *
 *  http://www.opensource.org/licenses/mit-license.php
 */


package org.vcell.util;

import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.channel.ExceptionEvent;
import org.jboss.netty.channel.MessageEvent;
import org.jboss.netty.channel.SimpleChannelUpstreamHandler;
import org.jboss.netty.handler.codec.http.HttpChunk;
import org.jboss.netty.handler.codec.http.HttpResponse;
import org.jboss.netty.util.CharsetUtil;
import org.vcell.util.NumberUtils;


public class HttpResponseHandler extends SimpleChannelUpstreamHandler {

	private boolean readingChunks;
	private ClientTaskStatusSupport clientTaskStatusSupport;
	private int contentLength = 0;
	private StringBuffer responseContent = new StringBuffer();
	private String serverHost = null;
	

	public StringBuffer getResponseContent() {
		return responseContent;
	}
	
	public HttpResponseHandler(ClientTaskStatusSupport clientTaskStatusSupport, String serverHost) {
		this.clientTaskStatusSupport = clientTaskStatusSupport;
		this.serverHost = serverHost;
	}

	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, ExceptionEvent e) throws Exception {
		System.out.println(e.toString());
		e.getCause().printStackTrace();
	}

	@Override
	public void messageReceived(ChannelHandlerContext ctx, MessageEvent e)
			throws Exception {
		if (clientTaskStatusSupport!=null && clientTaskStatusSupport.isInterrupted()){
			ctx.getChannel().close();
			return;
		}
		if (!readingChunks) {
			HttpResponse response = (HttpResponse) e.getMessage();
			if (!response.getHeaderNames().isEmpty()) {
				if (clientTaskStatusSupport!=null){
					clientTaskStatusSupport.setMessage("downloading "+NumberUtils.formatNumBytes(contentLength)+" from "+serverHost);
					clientTaskStatusSupport.setProgress(0);
				}else{
					System.out.println("downloading "+contentLength+" bytes from "+serverHost);
				}
			}

			if (response.isChunked()) {
				readingChunks = true;
			} else {
				ChannelBuffer content = response.getContent();
				if (content.readable()) {
					responseContent.append(content.toString(CharsetUtil.UTF_8));
					clientTaskStatusSupport.setProgress(100);
				}
			}
		} else {
			HttpChunk chunk = (HttpChunk) e.getMessage();
			if (chunk.isLast()) {
				readingChunks = false;
			} else {
				responseContent.append(chunk.getContent().toString(CharsetUtil.UTF_8));
				if (clientTaskStatusSupport!=null){
					clientTaskStatusSupport.setMessage("downloaded "+NumberUtils.formatNumBytes(responseContent.length()) + " from "+serverHost);
				}else{
					System.out.println("downloaded "+responseContent.length()+" of "+NumberUtils.formatNumBytes(responseContent.length())+" from " + serverHost);
				}
			}
		}
	}
}
