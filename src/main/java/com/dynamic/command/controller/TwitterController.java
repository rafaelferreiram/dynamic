package com.dynamic.command.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.dynamic.command.kafka.service.KafkaServiceAsync;
import com.dynamic.command.mongo.TweetTopicModel;
import com.dynamic.command.mongo.service.MongoService;

@RestController
@RequestMapping("/twitter")
public class TwitterController {

	@Autowired
	private KafkaServiceAsync kafkaService;
	
	@Autowired
	private MongoService mongoService;

	@GetMapping(value = "/status")
	public ResponseEntity<String> isWorking() {
		return ResponseEntity.ok("Is Working");
	}

	@GetMapping(value = "/tweets/{topic}")
	public ResponseEntity<String> searchTweetsByTopic(@PathVariable(required = true) final String topic) {
		kafkaService.send(topic);
		return ResponseEntity.ok().body("Topic '" + topic + "' sent will be consumed from tweets on real time");
	}
	
	@SuppressWarnings("rawtypes")
	@GetMapping(value = "/tweets/list")
	public ResponseEntity getTweetTopics(){
		List<TweetTopicModel> allTopics = mongoService.findAllTopics();
		if(allTopics.isEmpty()) {
			return ResponseEntity.badRequest().body("No Tweet Topics found.");
		}
		return ResponseEntity.ok().body(allTopics);
	}

}
