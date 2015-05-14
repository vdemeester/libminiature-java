package com.github.vdemeester.miniature.repository;

import com.github.vdemeester.miniature.model.Todo;
import com.google.common.collect.ImmutableMap;
import com.typesafe.config.Config;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Repository;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.stream.Collectors;

@Repository
public class TodoRepositoryImpl implements TodoRepository {

    public static final ParameterizedTypeReference<List<Todo>> TODO_PARAMETERIZED_TYPE_REFERENCE = new ParameterizedTypeReference<List<Todo>>() {
    };

    private final RestTemplate restTemplate;
    private final Config config;

    private final String baseURI;

    @Autowired
    public TodoRepositoryImpl(RestTemplate restTemplate, Config config) {
        this.restTemplate = restTemplate;
        this.config = config;
        baseURI = config.getString("restBaseUri");
    }


    @Override
    public List<Todo> findAll() {
        ResponseEntity<List<Todo>> response = restTemplate.exchange(urlFor("todos/"), HttpMethod.GET, null, TODO_PARAMETERIZED_TYPE_REFERENCE);
        return response.getBody();
    }

    @Override
    public List<Todo> findAll(boolean includeCompleted) {
        List<Todo> todos = findAll();
        return todos.stream()
                .filter(todo -> includeCompleted || !todo.isCompleted())
                .collect(Collectors.toList());
    }

    @Override
    public Todo get(Integer id) {
        try {
            return restTemplate.getForObject(urlFor("todos/{id}"), Todo.class, ImmutableMap.of("id", id));
        } catch (HttpClientErrorException e) {
            return null;
        }
    }

    private String urlFor(String path) {
        return String.format("%s/%s", baseURI, path);
    }
}
