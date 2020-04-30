package com.dynamic.command.controller;

import static org.junit.Assert.assertEquals;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import com.dynamic.command.kafka.producer.dto.response.TopicErrorResponseDTO;
import com.dynamic.command.kafka.producer.dto.response.TopicResponseDTO;
import com.dynamic.command.kafka.producer.dto.response.TweetTopicResponse;
import com.dynamic.command.kafka.service.KafkaService;
import com.dynamic.command.kafka.service.KafkaServiceAsync;
import com.dynamic.command.mongo.service.MongoService;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

@RunWith(SpringRunner.class)
public class TwitterControllerTest {

	@Mock
	private KafkaServiceAsync kafkaServiceAsync;
	
	@Mock
	private KafkaService kafkaService;

	@Mock
	private MongoService mongoService;

	private MockMvc mockMvc;

	private static final int BAD_REQUEST = 400;
	
	private static final int STATUS_OK = 200;
	
	private static final String TOPIC = "trump";
	
	Gson gson;

	@Before
	public void onInit() {
		MockitoAnnotations.initMocks(this);
		TwitterController controller = new TwitterController(kafkaServiceAsync,kafkaService,mongoService);
		controller.active = "yes";
		controller.inactive = "no";
		mockMvc = MockMvcBuilders.standaloneSetup(controller).build();
		gson = new Gson();
	}

	@Test
	public void sendSingleTopicToKafka() throws Exception {
		TopicResponseDTO expectedReturn = populateResponse();
		String expectedResponseJson = gson.toJson(expectedReturn);
		
		Mockito.when(kafkaService.isKafkaIsOn()).thenReturn(true);
		
		MvcResult response = this.mockMvc.perform(get("/twitter/tweets/" + TOPIC).contentType(APPLICATION_JSON))
				.andDo(print()).andExpect(status().isOk()).andReturn();

		assertEquals(STATUS_OK, response.getResponse().getStatus());
		assertEquals(expectedResponseJson, response.getResponse().getContentAsString());
	}

	@Test
	public void sendSingleTopicToKafkaWhenOffline() throws Exception {
		TopicErrorResponseDTO expectedReturn = populateErroResponse();
		String expectedResponseJson = gson.toJson(expectedReturn);
		
		Mockito.when(kafkaService.isKafkaIsOn()).thenReturn(false);

		MvcResult response = this.mockMvc.perform(get("/twitter/tweets/" + TOPIC).contentType(APPLICATION_JSON))
				.andDo(print()).andExpect(status().isBadRequest()).andReturn();

		assertEquals(BAD_REQUEST, response.getResponse().getStatus());
		assertEquals(expectedResponseJson, response.getResponse().getContentAsString());

	}
	
	@Test
	public void shouldDeactivateTopicSuccessfully() throws Exception {
		TopicResponseDTO expectedReturn = populateResponse();
		expectedReturn.setMsg("Topic " + TOPIC.toUpperCase() + " sent will be deactivade from Tweets Kafka Producer.");
		expectedReturn.setActive("no");
		String expectedResponseJson = gson.toJson(expectedReturn);
		
		Mockito.when(kafkaServiceAsync.deactivate(TOPIC)).thenReturn(true);

		MvcResult response = this.mockMvc.perform(get("/twitter/tweets/deactivate/" + TOPIC).contentType(APPLICATION_JSON))
				.andDo(print()).andExpect(status().isOk()).andReturn();

		assertEquals(STATUS_OK, response.getResponse().getStatus());
		assertEquals(expectedResponseJson, response.getResponse().getContentAsString());

	}
	
	@Test
	public void shouldReturnaMsgForTopicAlreadyDeactivated() throws Exception {
		TopicResponseDTO expectedReturn = populateResponse();
		expectedReturn.setMsg("Topic " + TOPIC.toUpperCase() + " is not active on Kafka Producer.");
		expectedReturn.setActive("no");
		String expectedResponseJson = gson.toJson(expectedReturn);
		
		Mockito.when(kafkaServiceAsync.deactivate(TOPIC)).thenReturn(false);

		MvcResult response = this.mockMvc.perform(get("/twitter/tweets/deactivate/" + TOPIC).contentType(APPLICATION_JSON))
				.andDo(print()).andExpect(status().isBadRequest()).andReturn();

		assertEquals(BAD_REQUEST, response.getResponse().getStatus());
		assertEquals(expectedResponseJson, response.getResponse().getContentAsString());

	}
	
	
	@Test
	public void shouldReturnListOfTopics() throws Exception {
		List<TweetTopicResponse> expectedResponse =  populateListResponse();
		String expectedResponseJson = gson.toJson(expectedResponse);
		Mockito.when(mongoService.findAllTopics()).thenReturn(expectedResponse);
		
		MvcResult response = this.mockMvc.perform(get("/twitter/tweets/list").contentType(APPLICATION_JSON))
				.andDo(print()).andExpect(status().isOk()).andReturn();

		assertEquals(STATUS_OK, response.getResponse().getStatus());
		assertEquals(expectedResponseJson, response.getResponse().getContentAsString());

	}
	
	@Test
	public void shouldReturnEmptyListOfTopics() throws Exception {
		GsonBuilder gsonBuilder = new GsonBuilder();  
		gsonBuilder.serializeNulls();  
		gson = gsonBuilder.create();
		
		TopicErrorResponseDTO expectedResponse = new TopicErrorResponseDTO(null,"No Tweet Topics found.");
		String expectedResponseJson = gson.toJson(expectedResponse);
		
		Mockito.when(mongoService.findAllTopics()).thenReturn(new ArrayList<TweetTopicResponse>());
		
		MvcResult response = this.mockMvc.perform(get("/twitter/tweets/list").contentType(APPLICATION_JSON))
				.andDo(print()).andExpect(status().isBadRequest()).andReturn();

		assertEquals(BAD_REQUEST, response.getResponse().getStatus());
		assertEquals(expectedResponseJson, response.getResponse().getContentAsString());

	}
	
	@Test
	public void shouldReturnListOfActives() throws Exception {
		List<TweetTopicResponse> expectedResponse =  populateListResponse();
		String expectedResponseJson = gson.toJson(expectedResponse);
		Mockito.when(mongoService.findActiveTopics()).thenReturn(expectedResponse);
		
		MvcResult response = this.mockMvc.perform(get("/twitter/tweets/list/actives").contentType(APPLICATION_JSON))
				.andDo(print()).andExpect(status().isOk()).andReturn();

		assertEquals(STATUS_OK, response.getResponse().getStatus());
		assertEquals(expectedResponseJson, response.getResponse().getContentAsString());

	}
	
	@Test
	public void shouldReturnNoTopicsActives() throws Exception {
		GsonBuilder gsonBuilder = new GsonBuilder();  
		gsonBuilder.serializeNulls();  
		gson = gsonBuilder.create();
		
		TopicErrorResponseDTO expectedResponse = new TopicErrorResponseDTO("No Active Tweet Topics found.");
		String expectedResponseJson = gson.toJson(expectedResponse);
		
		Mockito.when(mongoService.findActiveTopics()).thenReturn(new ArrayList<TweetTopicResponse>());
		
		MvcResult response = this.mockMvc.perform(get("/twitter/tweets/list/actives").contentType(APPLICATION_JSON))
				.andDo(print()).andExpect(status().isBadRequest()).andReturn();

		assertEquals(BAD_REQUEST, response.getResponse().getStatus());
		assertEquals(expectedResponseJson, response.getResponse().getContentAsString());

	}
	
	private List<TweetTopicResponse> populateListResponse() {
		List<TweetTopicResponse> returnList = new ArrayList<TweetTopicResponse>();
		TweetTopicResponse responseOne =  new TweetTopicResponse("1","netflix","20/04/2020","yes");
		TweetTopicResponse responseTwo =  new TweetTopicResponse("2","ps5","21/04/2020","no");
		TweetTopicResponse responseThree =  new TweetTopicResponse("3","iphone","22/04/2020","yes");
		returnList.add(responseOne);
		returnList.add(responseTwo);
		returnList.add(responseThree);
		return returnList;
	}

	private TopicErrorResponseDTO populateErroResponse() {
		String errorMsg = "Kafka server is OFFLINE";
		return new TopicErrorResponseDTO(TOPIC, errorMsg);
	}

	private TopicResponseDTO populateResponse() {
		String msg = "Topic " + TOPIC.toUpperCase() + " sent will be consumed from tweets on real time";
		return new TopicResponseDTO(TOPIC, "yes", msg);
	}
}
