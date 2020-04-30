package com.dynamic.command.controller;

import static org.junit.Assert.assertEquals;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import com.dynamic.command.kafka.producer.dto.response.TopicResponseDTO;
import com.dynamic.command.kafka.service.KafkaServiceAsync;
import com.dynamic.command.mongo.service.MongoService;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.google.gson.Gson;

@RunWith(SpringRunner.class)
public class TwitterControllerTest {

	@Mock
	private KafkaServiceAsync kafkaServiceAsync;

	@Mock
	private MongoService mongoService;

	private MockMvc mockMvc;

	Gson gson;

	private static final int BAD_REQUEST = 400;

	private static final int STATUS_OK = 200;

	private static final String TOPIC = "trump";

	@Before
	public void onInit() {
		MockitoAnnotations.initMocks(this);
		TwitterController controller = new TwitterController(kafkaServiceAsync);
		controller.active = "yes";
		controller.inactive = "no";
		mockMvc = MockMvcBuilders.standaloneSetup(controller).build();
		gson = new Gson();
	}

	@Test
	public void sendSingleTopicToKafka() throws Exception {
		TopicResponseDTO expectedReturn = populateResponse();

		ObjectMapper mapper = new ObjectMapper();
		mapper.configure(SerializationFeature.WRAP_ROOT_VALUE, false);

		MvcResult response = this.mockMvc.perform(get("/twitter/tweets/" + TOPIC).contentType(APPLICATION_JSON)).andDo(print())
				.andExpect(status().isOk()).andReturn();

		assertEquals(STATUS_OK, response.getResponse().getStatus());
	}

	private TopicResponseDTO populateResponse() {
		String msg = "Topic '" + TOPIC + "' sent will be consumed from tweets on real time";
		return new TopicResponseDTO(TOPIC, "yes", msg);
	}
}
