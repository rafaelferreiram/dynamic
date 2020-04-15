package com.dynamic.command.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.dynamic.command.kafka.service.KafkaService;

@RestController
@RequestMapping("/twitter")
public class TwitterController {
	
	@Autowired
	private KafkaService kafkaService;

	@GetMapping(value = "/status")
	public ResponseEntity<String> isWorking() {
		return ResponseEntity.ok("Is Working");
	}
	
	@GetMapping(value = "/tweets/{topic}")
	public ResponseEntity<String> searchTweetsByTopic(@PathVariable("topic") String topic){
		kafkaService.send(topic);
		return ResponseEntity.ok().body("Topic '"+topic+"' sent will be consumed from tweets on real time");
	}

}
