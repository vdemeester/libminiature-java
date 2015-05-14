package com.github.vdemeester.miniature;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.vdemeester.miniature.model.Todo;
import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;
import org.assertj.core.api.iterable.Extractor;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.BlockJUnit4ClassRunner;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.filter;
import static org.assertj.core.api.Assertions.tuple;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.method;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withStatus;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;


@RunWith(BlockJUnit4ClassRunner.class)
public class TodoRepositoryImplTest {

    private static final LocalDateTime NOW = LocalDateTime.now();
    private static final String JSON_TODO1 = "{\"id\":1,\"name\":\"Write presentation\",\"completed\":true,\"due\":\"\"}";
    private static final String JSON_TODO2 = "{\"id\":2,\"name\":\"Host meetup\",\"completed\":false,\"due\":\"2015-05-14T14:14:00\"}";
    private static final String JSON_TODOS = String.format("[%s,%s]", JSON_TODO1, JSON_TODO2);
    public static final String JSON_EMPTY_LIST = "[]";
    public static final Todo TODO1 = new Todo(1, "Write presentation", true, null);
    public static final Todo TODO2 = new Todo(2, "Host meetup", false, LocalDateTime.of(2015, 05, 14, 14, 14, 00));

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
        mockRestServiceServer.expect(requestTo("http://127.0.0.1:8888/todos/-1"))
                .andExpect(method(HttpMethod.GET))
                .andRespond(withStatus(HttpStatus.NOT_FOUND));
        mockRestServiceServer.expect(requestTo("http://127.0.0.1:8888/todos/100"))
                .andExpect(method(HttpMethod.GET))
                .andRespond(withStatus(HttpStatus.NOT_FOUND));
        Todo todo = todoRepository.get(-1);
        assertThat(todo).isNull();
        todo = todoRepository.get(100);
        assertThat(todo).isNull();
    }

    @Test
    public void getWithValidIdShouldReturnATodo() {
        mockRestServiceServer.expect(requestTo("http://127.0.0.1:8888/todos/1"))
                .andExpect(method(HttpMethod.GET))
                .andRespond(withSuccess(JSON_TODO1, MediaType.APPLICATION_JSON));
        Todo todo = todoRepository.get(1);
        assertThat(todo).isNotNull()
                .isEqualToComparingFieldByField(TODO1);
    }

    @Test
    public void findAllWithEmptyResultShouldReturnEmpty() {
        mockRestServiceServer.expect(requestTo("http://127.0.0.1:8888/todos/"))
                .andExpect(method(HttpMethod.GET))
                .andRespond(withSuccess(JSON_EMPTY_LIST, MediaType.APPLICATION_JSON));
        List<Todo> todos = todoRepository.findAll();
        assertThat(todos).isNotNull()
                .isEmpty();
    }

    @Test
    public void findAll() {
        mockRestServiceServer.expect(requestTo("http://127.0.0.1:8888/todos/"))
                .andExpect(method(HttpMethod.GET))
                .andRespond(withSuccess(JSON_TODOS, MediaType.APPLICATION_JSON));
        List<Todo> todos = todoRepository.findAll();
        assertThat(todos).isNotNull()
                .isNotEmpty()
                .hasSize(2)
                .containsExactly(TODO1, TODO2);
    }

    @Test
    public void findAllWithCompleted() {
        mockRestServiceServer.expect(requestTo("http://127.0.0.1:8888/todos/"))
                .andExpect(method(HttpMethod.GET))
                .andRespond(withSuccess(JSON_TODOS, MediaType.APPLICATION_JSON));
        List<Todo> todos = todoRepository.findAll(true);
        assertThat(todos).isNotNull()
                .isNotEmpty()
                .hasSize(2)
                .containsExactly(TODO1, TODO2);
    }

    @Test
    public void findAllWithoutCompleted() {
        mockRestServiceServer.expect(requestTo("http://127.0.0.1:8888/todos/"))
                .andExpect(method(HttpMethod.GET))
                .andRespond(withSuccess(JSON_TODOS, MediaType.APPLICATION_JSON));
        List<Todo> todos = todoRepository.findAll(false);
        assertThat(todos).isNotNull()
                .isNotEmpty()
                .hasSize(1)
                .containsExactly(TODO2);
    }

}