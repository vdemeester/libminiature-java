package com.github.vdemeester.miniature;

import com.github.vdemeester.miniature.model.Todo;
import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.BlockJUnit4ClassRunner;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.web.client.RestTemplate;

import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.method;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withStatus;


@RunWith(BlockJUnit4ClassRunner.class)
public class TodoRepositoryImplTest {

    private MockRestServiceServer mockRestServiceServer;
    private RestTemplate restTemplate;
    private TodoRepository todoRepository;

    @Before
    public void setUpRest() {
        Config config = ConfigFactory.load();
        restTemplate = new RestTemplate();
        mockRestServiceServer = MockRestServiceServer.createServer(restTemplate);
        todoRepository = new TodoRepositoryImpl(restTemplate, config);
    }

    @Test
    public void getWithInvalidIdShouldReturnNull() {
        mockRestServiceServer.expect(requestTo("http://127.0.0.1:8888/todo/-1"))
                .andExpect(method(HttpMethod.GET))
                .andRespond(withStatus(HttpStatus.NOT_FOUND));
        Todo todo = todoRepository.get(-1);
        assertThat(todo, nullValue());
    }

}