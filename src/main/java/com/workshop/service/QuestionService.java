package com.workshop.service;

import com.workshop.controller.QuestionController;
import com.workshop.entity.Account;
import com.workshop.entity.Answer;
import com.workshop.entity.Question;
import com.workshop.exception.QuestionNotFoundException;
import com.workshop.repository.AnswerRepository;
import com.workshop.repository.QuestionRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.hateoas.PagedResources;
import org.springframework.hateoas.Resource;
import org.springframework.hateoas.Resources;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.*;
import java.util.stream.Collectors;

import static org.springframework.hateoas.mvc.ControllerLinkBuilder.linkTo;
import static org.springframework.hateoas.mvc.ControllerLinkBuilder.methodOn;

@Service
public class QuestionService {
    private final QuestionRepository questionRepository;
    private final AnswerRepository answerRepository;

    public QuestionService(QuestionRepository questionRepository,
                           AnswerRepository answerRepository) {
        this.questionRepository = questionRepository;
        this.answerRepository = answerRepository;
    }

    public Page<Question> findAll(Pageable pageable) {
        return questionRepository.findAll(pageable);
    }

    public Question findOne(String id) {
        return Optional.ofNullable(questionRepository.findOne(id))
                .orElseThrow(() -> {
                    String msg = String.format("Question with id %s not found", id);
                    return new QuestionNotFoundException(msg);
                });
    }

    @Transactional
    public Question save(Question question, Account account) {
        question.setCreatedBy(account);
        question.setModifiedBy(account);
        Question savedQuestion = questionRepository.save(question);

        Set<Answer> answers = question.getAnswers().stream()
                .map(answer -> {
                    answer.setCreatedBy(account);
                    answer.setModifiedBy(account);
                    answer.setQuestionId(savedQuestion.getId());

                    return answer;
                })
                .collect(Collectors.toSet());

        savedQuestion.setAnswers(new HashSet<>(answerRepository.save(answers)));

        return savedQuestion;
    }

    public Resource<Question> convertToResource(Question question) {
        Resource<Question> resource = new Resource<>(question);
        resource.add(linkTo(methodOn(QuestionController.class).getOneById(question.getId()))
                .withSelfRel());
        resource.add(linkTo(methodOn(QuestionController.class).getOneById(question.getId()))
                .withRel("question"));

        return resource;
    }

    public Resources<Resource<Question>> convertToResource(Page<Question> questions, int page) {
        List<Resource<Question>> resources = questions.getContent().stream()
                .map(this::convertToResource)
                .collect(Collectors.toList());

        PagedResources.PageMetadata pageMetadata = new PagedResources.PageMetadata(
                questions.getSize(), page, questions.getTotalElements(), questions.getTotalPages());

        return new PagedResources<>(resources, pageMetadata);
    }
}
