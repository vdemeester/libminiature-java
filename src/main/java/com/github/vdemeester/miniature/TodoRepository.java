package com.github.vdemeester.miniature;

import com.github.vdemeester.miniature.model.Todo;

import java.util.List;

public interface TodoRepository {

    List<Todo> findAll();

    List<Todo> findAll(boolean includeCompleted);

    Todo get(Integer id);


}
