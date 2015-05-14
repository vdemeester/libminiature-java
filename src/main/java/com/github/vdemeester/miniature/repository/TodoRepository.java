package com.github.vdemeester.miniature.repository;

import com.github.vdemeester.miniature.model.Todo;
import org.springframework.stereotype.Repository;

import java.util.List;

public interface TodoRepository {

    List<Todo> findAll();

    List<Todo> findAll(boolean includeCompleted);

    Todo get(Integer id);

}
