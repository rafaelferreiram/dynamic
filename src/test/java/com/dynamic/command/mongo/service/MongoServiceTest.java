package com.dynamic.command.mongo.service;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.test.context.junit4.SpringRunner;

import com.dynamic.command.kafka.mapper.TweetTopicMapper;
import com.dynamic.command.kafka.producer.dto.response.TweetTopicResponse;
import com.dynamic.command.mongo.TweetTopicModel;
import com.dynamic.command.mongo.repository.TweetLogRepository;
import com.dynamic.command.mongo.repository.TweetTopicRepository;
import com.dynamic.command.mongo.service.impl.MongoServiceImpl;

@RunWith(SpringRunner.class)
public class MongoServiceTest {

	private static final String ACTIVE = "yes";

	@InjectMocks
	private MongoServiceImpl service;
	
	@Mock
	private TweetTopicMapper mapper;

	@Mock
	private TweetTopicRepository repository;

	@Mock
	private TweetLogRepository logRepository;

	@Before
	public void onInit() {
		MockitoAnnotations.initMocks(this);
	}
	
	@Test
	public void shouldReturnPopulatedListOfTopics() {
		List<TweetTopicResponse> expectedResponse = populateResponse();
		List<TweetTopicModel> allTopics = populateRepositoryResponse();
		
		Mockito.when(repository.findAll()).thenReturn(allTopics);
		Mockito.when(mapper.modelToResponse(allTopics.get(0))).thenReturn(expectedResponse.get(0));
		Mockito.when(mapper.modelToResponse(allTopics.get(1))).thenReturn(expectedResponse.get(1));
		Mockito.when(mapper.modelToResponse(allTopics.get(2))).thenReturn(expectedResponse.get(2));
		Mockito.when(mapper.modelToResponse(allTopics.get(3))).thenReturn(expectedResponse.get(3));
		
		List<TweetTopicResponse> topicsResponse = service.findAllTopics();
		
		assertEquals(expectedResponse, topicsResponse);
	}
	
	@Test
	public void shouldReturnEmptyListOfTopics() {
		List<TweetTopicResponse> expectedResponse = new ArrayList<TweetTopicResponse>();
		List<TweetTopicModel> allTopics = new ArrayList<TweetTopicModel>();
		
		Mockito.when(repository.findAll()).thenReturn(allTopics);
		
		List<TweetTopicResponse> topicsResponse = service.findAllTopics();
		
		assertEquals(expectedResponse, topicsResponse);
	}
	
	@Test
	public void shoudReturnActiveTopics() {
		List<TweetTopicResponse> expectedResponse = populateResponse();
		List<TweetTopicModel> activeTopics = populateRepositoryResponse();
		
		service.active = ACTIVE;
		
		Mockito.when(repository.findActiveTopics(ACTIVE)).thenReturn(activeTopics);
		Mockito.when(mapper.modelToResponse(activeTopics.get(0))).thenReturn(expectedResponse.get(0));
		Mockito.when(mapper.modelToResponse(activeTopics.get(1))).thenReturn(expectedResponse.get(1));
		Mockito.when(mapper.modelToResponse(activeTopics.get(2))).thenReturn(expectedResponse.get(2));
		Mockito.when(mapper.modelToResponse(activeTopics.get(3))).thenReturn(expectedResponse.get(3));
		
		List<TweetTopicResponse> activeTopicsResponse = service.findActiveTopics();
		
		assertEquals(expectedResponse, activeTopicsResponse);
		
	}
	
	@Test
	public void shoudReturnEmptyListOfActiveTopics() {
		List<TweetTopicResponse> expectedResponse = new ArrayList<TweetTopicResponse>();
		List<TweetTopicModel> activeTopics = new ArrayList<TweetTopicModel>();
		
		service.active = ACTIVE;
		
		Mockito.when(repository.findActiveTopics(ACTIVE)).thenReturn(activeTopics);
		
		List<TweetTopicResponse> activeTopicsResponse = service.findActiveTopics();
		
		assertEquals(expectedResponse, activeTopicsResponse);
		
	}
	
	@Test
	public void shouldFindTopicByName() {
		TweetTopicModel topic = populateRepositoryResponse().get(0);
		
		Mockito.when(repository.findByTopicName("topicOne")).thenReturn(topic);
		
		TweetTopicModel topicResponse = service.findByTopicName("topicOne");
		
		assertEquals(topic, topicResponse);
	}
	
	@Test
	public void shouldNotReturnTopicByName() {
		
		Mockito.when(repository.findByTopicName("topicOne")).thenReturn(null);
		
		TweetTopicModel topicResponse = service.findByTopicName("topicOne");
		
		assertEquals(null, topicResponse);
	}
	
	private List<TweetTopicModel> populateRepositoryResponse() {
		List<TweetTopicModel> resultList = new ArrayList<TweetTopicModel>();
		TweetTopicModel topicOne = new TweetTopicModel("1","topicOne","21/02/2020",ACTIVE);
		TweetTopicModel topicTwo = new TweetTopicModel("2","topicTwo","21/02/2020",ACTIVE);
		TweetTopicModel topicThree = new TweetTopicModel("3","topicThree","21/02/2020",ACTIVE);
		TweetTopicModel topicFour = new TweetTopicModel("4","topicFour","21/02/2020",ACTIVE);
		resultList.add(topicOne);
		resultList.add(topicTwo);
		resultList.add(topicThree);
		resultList.add(topicFour);
		return resultList;
	}

	private List<TweetTopicResponse> populateResponse() {
		List<TweetTopicResponse> resultList = new ArrayList<TweetTopicResponse>();
		TweetTopicResponse topicOne = new TweetTopicResponse("1","topicOne","21/02/2020",ACTIVE);
		TweetTopicResponse topicTwo = new TweetTopicResponse("2","topicTwo","21/02/2020",ACTIVE);
		TweetTopicResponse topicThree = new TweetTopicResponse("3","topicThree","21/02/2020",ACTIVE);
		TweetTopicResponse topicFour = new TweetTopicResponse("4","topicFour","21/02/2020",ACTIVE);
		resultList.add(topicOne);
		resultList.add(topicTwo);
		resultList.add(topicThree);
		resultList.add(topicFour);
		return resultList;
	}
}
