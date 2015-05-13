package com.github.vdemeester.miniature;

import com.github.vdemeester.miniature.model.Todo;
import com.google.common.collect.ImmutableMap;
import com.typesafe.config.Config;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.net.URI;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class TodoRepositoryImpl implements TodoRepository {

    private final RestTemplate restTemplate;
    private final Config config;

    private final String baseURI;

    public TodoRepositoryImpl(RestTemplate restTemplate, Config config) {
        this.restTemplate = restTemplate;
        this.config = config;
        baseURI = config.getString("restBaseUri");
    }


    @Override
    public List<Todo> findAll() {
        return restTemplate.getForObject(urlFor("todo/"), List.class);
    }

    @Override
    public List<Todo> findAll(boolean includeCompleted) {
        List<Todo> todos = restTemplate.getForObject(urlFor("todo/"), List.class);
        return todos.stream()
                .filter(Todo::isCompleted)
                .collect(Collectors.toList());
    }

    @Override
    public Todo get(Integer id) {
        return restTemplate.getForObject(urlFor("todo/{id}"), Todo.class, ImmutableMap.of("id", id));
    }

    private String urlFor(String path) {
        return String.format("%s/%s", baseURI, path);
    }
}
