package com.dynamic.command.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

@Component
public class ShutdownConfigAsync {
	
	@Autowired
	private ShutdownConfig shutdownConfig;

	@Async
	public void shutdown() {
		shutdownConfig.shutdown();
	}
}
