package com.dynamic.command.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.dynamic.command.config.ShutdownConfigAsync;
import com.dynamic.command.kafka.producer.dto.response.ConfigResponseDTO;

@RestController
@RequestMapping("/config")
public class ConfigController {
	
	@Autowired
	private ShutdownConfigAsync shutdownConfig;

	@GetMapping(value = "/shutdown")
	public ResponseEntity<ConfigResponseDTO> shutdown() {
		ConfigResponseDTO configResponse = new ConfigResponseDTO();
		configResponse.setStatus("Shutting Down");
		configResponse.setTopicsStatus("Client Topics Deactivated");
		shutdownConfig.shutdown();
		return ResponseEntity.ok().body(configResponse);
	}
}
