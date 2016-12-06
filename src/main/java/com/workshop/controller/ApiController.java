package com.workshop.controller;

import com.workshop.entity.Account;
import com.workshop.repository.AccountRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.hateoas.PagedResources;
import org.springframework.hateoas.Resource;
import org.springframework.hateoas.Resources;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import static org.springframework.hateoas.mvc.ControllerLinkBuilder.linkTo;
import static org.springframework.hateoas.mvc.ControllerLinkBuilder.methodOn;

@RestController
@RequestMapping("/api")
public class ApiController {
    private final AccountRepository accountRepository;

    public ApiController(AccountRepository accountRepository) {
        this.accountRepository = accountRepository;
    }

    @RequestMapping(value = "/accounts", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public Resources<Resource<Account>> getAccounts() {
        Page<Account> accounts = accountRepository.findAll(new PageRequest(0, 20));

        List<Resource<Account>> resourceList = accounts.getContent().stream()
                .map(this::getAccountResource)
                .collect(Collectors.toList());

        PagedResources.PageMetadata pageMetadata = new PagedResources.PageMetadata(accounts.getSize(), accounts.getNumberOfElements(), accounts.getTotalElements(), accounts.getTotalPages());
        return new PagedResources<>(resourceList, pageMetadata, Collections.emptyList());
    }

    @RequestMapping(value = "/accounts/{id}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public Resource<Account> getAccount(@PathVariable String id) {
        Account account = accountRepository.findOne(id);

        return getAccountResource(account);
    }

    private Resource<Account> getAccountResource(Account account) {
        Resource<Account> accountResource = new Resource<>(account);
        accountResource.add(linkTo(methodOn(ApiController.class).getAccount(account.getId())).withSelfRel());

        return accountResource;
    }

}
