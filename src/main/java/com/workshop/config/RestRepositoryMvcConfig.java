package com.workshop.config;

import com.workshop.entity.Answer;
import com.workshop.entity.Question;
import com.workshop.entity.Session;
import com.workshop.entity.SessionItem;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.rest.core.config.RepositoryRestConfiguration;
import org.springframework.data.rest.webmvc.config.RepositoryRestConfigurerAdapter;

@Configuration
public class RestRepositoryMvcConfig extends RepositoryRestConfigurerAdapter {
    @Override
    public void configureRepositoryRestConfiguration(RepositoryRestConfiguration config) {
        config.exposeIdsFor(Session.class, SessionItem.class, Question.class, Answer.class);
    }
}
