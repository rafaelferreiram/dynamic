package com.dynamic.command.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.dynamic.command.kafka.producer.dto.request.TopicsListRequestDTO;
import com.dynamic.command.kafka.producer.dto.response.TopicErrorResponseDTO;
import com.dynamic.command.kafka.producer.dto.response.TopicResponseDTO;
import com.dynamic.command.kafka.service.KafkaServiceAsync;
import com.dynamic.command.mongo.TweetTopicModel;
import com.dynamic.command.mongo.service.MongoService;

@RestController
@RequestMapping("/twitter")
@CrossOrigin(origins = "*")
public class TwitterController {

	@Autowired
	private KafkaServiceAsync kafkaServiceAsync;

	@Autowired
	private MongoService mongoService;
	
	@Value("${twitter.topic.active}")
	private String active;

	@GetMapping(value = "/status")
	public ResponseEntity<String> isWorking() {
		return ResponseEntity.ok("Is Working...");
	}

	@SuppressWarnings("rawtypes")
	@GetMapping(value = "/tweets/{topic}")
	public ResponseEntity searchTweetsByTopic(@PathVariable(required = true) final String topic) {
		try {
			kafkaServiceAsync.send(topic);
			String msg = "Topic '" + topic + "' sent will be consumed from tweets on real time";
			return ResponseEntity.ok().body(new TopicResponseDTO(topic,active,msg));
		} catch (Exception e) {
			String errorMsg = "Error while sending topic to kafka";
			return ResponseEntity.badRequest().body(new TopicErrorResponseDTO(topic,errorMsg));
		}
	}

	@SuppressWarnings("rawtypes")
	@PostMapping(value = "/tweets")
	public ResponseEntity searchTweetsByListTopic(@RequestBody(required = true) TopicsListRequestDTO topic) {
		try {
			if (topic.getTopics().isEmpty()) {
				String emptyListMsg = "List of topics cannot be empty.";
				return ResponseEntity.badRequest().body(new TopicErrorResponseDTO(emptyListMsg));
			}
			kafkaServiceAsync.send(topic.getTopics());
			return ResponseEntity.ok()
					.body("Topics '" + topic.getTopics() + "' sent will be consumed from tweets on real time");
		} catch (Exception e) {
			String errorMsg = "Error while sending topic to kafka";
			return ResponseEntity.badRequest().body(new TopicErrorResponseDTO(errorMsg));
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
