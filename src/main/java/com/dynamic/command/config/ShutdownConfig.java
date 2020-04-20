package com.dynamic.command.config;

import org.springframework.stereotype.Component;

@Component
public class ShutdownConfig {

	public void shutdown() {
		System.exit(0);	
	}
}
