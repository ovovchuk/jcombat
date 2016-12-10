package com.workshop.controller;

import com.workshop.entity.Account;
import com.workshop.entity.Question;
import com.workshop.service.AccountService;
import com.workshop.service.QuestionService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.hateoas.Resource;
import org.springframework.hateoas.Resources;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;

import static org.springframework.hateoas.mvc.ControllerLinkBuilder.linkTo;
import static org.springframework.hateoas.mvc.ControllerLinkBuilder.methodOn;

@RestController
@RequestMapping("/questions")
public class QuestionController {
    private final QuestionService questionService;
    private final AccountService accountService;

    public QuestionController(QuestionService questionService,
                              AccountService accountService) {
        this.questionService = questionService;
        this.accountService = accountService;
    }

    @RequestMapping(method = RequestMethod.GET)
    public Resources<Resource<Question>> findAll(
            @RequestParam(value = "page", required = false, defaultValue = "0") int page,
            @RequestParam(value = "size", required = false, defaultValue = "15") int size,
            @RequestParam(value = "sort", required = false, defaultValue = "dateCreated") String[] sort) {
        PageRequest pageRequest = new PageRequest(page, size, Sort.Direction.ASC, sort);
        Page<Question> questions = questionService.findAll(pageRequest);

        Resources<Resource<Question>> resources = questionService.convertToResource(questions, page);
        resources.add(linkTo(methodOn(QuestionController.class).findAll(page, size, sort)).withSelfRel());
        resources.add(linkTo(methodOn(QuestionController.class).findAll(page, size, sort)).withRel("questions"));

        return resources;
    }

    @RequestMapping(method = RequestMethod.POST)
    public Resource<Question> save(@RequestBody Question question, Principal principal) {
        Account account = accountService.findByUsername(principal.getName());
        Question savedQuestion = questionService.save(question, account);

        return questionService.convertToResource(savedQuestion);
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.GET)
    public Resource<Question> getOneById(@PathVariable String id) {
        Question question = questionService.findOne(id);

        return questionService.convertToResource(question);
    }
}
