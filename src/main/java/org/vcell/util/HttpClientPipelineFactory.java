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

import org.jboss.netty.channel.ChannelPipeline;
import org.jboss.netty.channel.ChannelPipelineFactory;
import org.jboss.netty.channel.Channels;
import org.jboss.netty.handler.codec.http.HttpClientCodec;
import org.jboss.netty.handler.codec.http.HttpContentDecompressor;

public class HttpClientPipelineFactory implements ChannelPipelineFactory {

	private HttpResponseHandler responseHandler = null;

	public HttpClientPipelineFactory(HttpResponseHandler responseHandler) {
		this.responseHandler = responseHandler;
	}

	public ChannelPipeline getPipeline() throws Exception {

		ChannelPipeline pipeline = Channels.pipeline();

		// pipeline.addLast("ssl", new SslHandler(engine));

		pipeline.addLast("codec", new HttpClientCodec());

		pipeline.addLast("inflater", new HttpContentDecompressor());

		// pipeline.addLast("aggregator", new HttpChunkAggregator(1048576));

		pipeline.addLast("handler", responseHandler);

		return pipeline;
	}
}
