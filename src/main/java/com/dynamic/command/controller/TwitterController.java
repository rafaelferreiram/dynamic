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
import com.dynamic.command.kafka.producer.dto.response.TopicsListResponseDTO;
import com.dynamic.command.kafka.producer.dto.response.TweetTopicResponse;
import com.dynamic.command.kafka.service.KafkaServiceAsync;
import com.dynamic.command.mongo.service.MongoService;

@RestController
@RequestMapping("/twitter")
@CrossOrigin(origins = "*")
public class TwitterController {

	@Autowired
	private KafkaServiceAsync kafkaServiceAsync;

	@Autowired
	private MongoService mongoService;

	@Value("${twitter.topic.deactive}")
	private String inactive;

	@Value("${twitter.topic.active}")
	private String active;

	@GetMapping(value = "/status")
	public ResponseEntity<String> isWorking() {
		return ResponseEntity.ok("Is Working...");
	}

	@GetMapping(value = "/tweets/{topic}")
	public ResponseEntity<Object> searchTweetsByTopic(@PathVariable(required = true) final String topic) {
		try {
			kafkaServiceAsync.send(topic);
			String msg = "Topic '" + topic + "' sent will be consumed from tweets on real time";
			return ResponseEntity.ok().body(new TopicResponseDTO(topic, active, msg));
		} catch (Exception e) {
			String errorMsg = "Error while sending topic to kafka";
			return ResponseEntity.badRequest().body(new TopicErrorResponseDTO(topic, errorMsg));
		}
	}

	@PostMapping(value = "/tweets")
	public ResponseEntity<Object> searchTweetsByListTopic(@RequestBody(required = true) TopicsListRequestDTO topic) {
		try {
			if (topic.getTopics().isEmpty()) {
				return ResponseEntity.badRequest().body(new TopicErrorResponseDTO("List of topics cannot be empty."));
			}
			kafkaServiceAsync.send(topic.getTopics());
			String msg = "Topics '" + topic.getTopics() + "' sent will be consumed from tweets on real time";
			return ResponseEntity.ok().body(new TopicsListResponseDTO(topic.getTopics(), active, msg));
		} catch (Exception e) {
			return ResponseEntity.badRequest().body(new TopicErrorResponseDTO("Error while sending topic to kafka"));
		}
	}

	@GetMapping(value = "/tweets/deactivate/{topic}")
	public ResponseEntity<Object> deactivateTopic(@PathVariable(required = true) final String topic) {
		try {
			boolean deactivate = kafkaServiceAsync.deactivate(topic);
			String msg;
			if (deactivate) {
				kafkaServiceAsync.closeConnectionClient(topic);
				msg = "Topic '" + topic + "' sent will be deactivade from Tweets Kafka Producer.";
				return ResponseEntity.ok().body(new TopicResponseDTO(topic, inactive, msg));
			}
			msg = "Topic '" + topic + "' isn't active on Kafka Producer.";
			return ResponseEntity.ok().body(new TopicResponseDTO(topic, inactive, msg));
		} catch (Exception e) {
			return ResponseEntity.badRequest().body(new TopicErrorResponseDTO("Error while deactivating."));
		}
	}

	@GetMapping(value = "/tweets/list")
	public ResponseEntity<Object> getTweetTopics() {
		List<TweetTopicResponse> allTopics = mongoService.findAllTopics();
		if (allTopics.isEmpty()) {
			return ResponseEntity.badRequest().body(new TopicErrorResponseDTO("No Tweet Topics found."));
		}
		return ResponseEntity.ok().body(allTopics);
	}

	@GetMapping(value = "/tweets/list/actives")
	public ResponseEntity<Object> getActivesTweetTopics() {
		List<TweetTopicResponse> activeTopics = mongoService.findActiveTopics();
		if (activeTopics.isEmpty()) {
			return ResponseEntity.ok().body(new TopicErrorResponseDTO("No Active Tweet Topics found."));
		}
		return ResponseEntity.ok().body(activeTopics);
	}

}
