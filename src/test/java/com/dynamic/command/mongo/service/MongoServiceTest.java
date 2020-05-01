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
//@SpringBootTest
public class MongoServiceTest {

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

	private List<TweetTopicModel> populateRepositoryResponse() {
		List<TweetTopicModel> resultList = new ArrayList<TweetTopicModel>();
		TweetTopicModel topicOne = new TweetTopicModel("1","topicOne","21/02/2020","yes");
		TweetTopicModel topicTwo = new TweetTopicModel("2","topicTwo","21/02/2020","yes");
		TweetTopicModel topicThree = new TweetTopicModel("3","topicThree","21/02/2020","yes");
		TweetTopicModel topicFour = new TweetTopicModel("4","topicFour","21/02/2020","yes");
		resultList.add(topicOne);
		resultList.add(topicTwo);
		resultList.add(topicThree);
		resultList.add(topicFour);
		return resultList;
	}

	private List<TweetTopicResponse> populateResponse() {
		List<TweetTopicResponse> resultList = new ArrayList<TweetTopicResponse>();
		TweetTopicResponse topicOne = new TweetTopicResponse("1","topicOne","21/02/2020","yes");
		TweetTopicResponse topicTwo = new TweetTopicResponse("2","topicTwo","21/02/2020","yes");
		TweetTopicResponse topicThree = new TweetTopicResponse("3","topicThree","21/02/2020","yes");
		TweetTopicResponse topicFour = new TweetTopicResponse("4","topicFour","21/02/2020","yes");
		resultList.add(topicOne);
		resultList.add(topicTwo);
		resultList.add(topicThree);
		resultList.add(topicFour);
		return resultList;
	}
}
