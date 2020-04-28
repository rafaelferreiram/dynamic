package com.dynamic.command.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.dynamic.command.kafka.producer.dto.request.TopicsListRequestDTO;
import com.dynamic.command.kafka.service.KafkaServiceAsync;
import com.dynamic.command.mongo.TweetTopicModel;
import com.dynamic.command.mongo.service.MongoService;

@RestController
@RequestMapping("/twitter")
public class TwitterController {

	@Autowired
	private KafkaServiceAsync kafkaServiceAsync;

	@Autowired
	private MongoService mongoService;

	@GetMapping(value = "/status")
	public ResponseEntity<String> isWorking() {
		return ResponseEntity.ok("Is Working...");
	}

	@GetMapping(value = "/tweets/{topic}")
	public ResponseEntity<String> searchTweetsByTopic(@PathVariable(required = true) final String topic) {
		try {
			kafkaServiceAsync.send(topic);
			return ResponseEntity.ok().body("Topic '" + topic + "' sent will be consumed from tweets on real time");
		} catch (Exception e) {
			return ResponseEntity.badRequest().body("Error while sending topic to kafka");
		}
	}

	@PostMapping(value = "/tweets")
	public ResponseEntity<String> searchTweetsByListTopic(@RequestBody(required = true) TopicsListRequestDTO topic) {
		try {
			if (topic.getTopics().isEmpty()) {
				return ResponseEntity.badRequest().body("List of topics cannot be empty.");
			}
			kafkaServiceAsync.send(topic.getTopics());
			return ResponseEntity.ok()
					.body("Topics '" + topic.getTopics() + "' sent will be consumed from tweets on real time");
		} catch (Exception e) {
			return ResponseEntity.badRequest().body("Error while sending topic to kafka");
		}
	}

	@GetMapping(value = "/tweets/deactivate/{topic}")
	public ResponseEntity<String> deactivateTopic(@PathVariable(required = true) final String topic) {
		try {
			boolean deactivate = kafkaServiceAsync.deactivate(topic);
			if (deactivate) {
				kafkaServiceAsync.closeConnectionClient(topic);
				return ResponseEntity.ok()
						.body("Topic '" + topic + "' sent will be deactivade from Tweets Kafka Producer.");
			}
			return ResponseEntity.ok().body("Topic '" + topic + "' isn't active on Kafka Producer.");
		} catch (Exception e) {
			return ResponseEntity.ok().body("Error while deactivating.");
		}
	}

	@SuppressWarnings("rawtypes")
	@GetMapping(value = "/tweets/list")
	public ResponseEntity getTweetTopics() {
		List<TweetTopicModel> allTopics = mongoService.findAllTopics();
		if (allTopics.isEmpty()) {
			return ResponseEntity.badRequest().body("No Tweet Topics found.");
		}
		return ResponseEntity.ok().body(allTopics);
	}

	@SuppressWarnings("rawtypes")
	@GetMapping(value = "/tweets/list/actives")
	public ResponseEntity getActivesTweetTopics() {
		List<TweetTopicModel> activeTopics = mongoService.findActiveTopics();
		if (activeTopics.isEmpty()) {
			return ResponseEntity.badRequest().body("No Active Tweet Topics found.");
		}
		return ResponseEntity.ok().body(activeTopics);
	}

}
