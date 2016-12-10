package com.workshop.controller;

import com.workshop.entity.Account;
import com.workshop.service.AccountService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.hateoas.Resource;
import org.springframework.hateoas.Resources;
import org.springframework.hateoas.mvc.ControllerLinkBuilder;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;

import static org.springframework.hateoas.mvc.ControllerLinkBuilder.linkTo;
import static org.springframework.hateoas.mvc.ControllerLinkBuilder.methodOn;

@RestController
@RequestMapping("/accounts")
public class AccountController {
    private final AccountService accountService;

    public AccountController(AccountService accountRepository) {
        this.accountService = accountRepository;
    }

    @RequestMapping(value = "/me", method = RequestMethod.GET)
    public Resource<Account> getMe(Principal principal) {
        Account account = accountService.findByUsername(principal.getName());

        return accountService.convertToResource(account);
    }

    @RequestMapping(method = RequestMethod.GET)
    public Resources<Resource<Account>> getAccounts(
            @RequestParam(value = "page", required = false, defaultValue = "0") int page,
            @RequestParam(value = "size", required = false, defaultValue = "15") int size,
            @RequestParam(value = "sort", required = false, defaultValue = "username") String[] sort) {
        PageRequest pageRequest = new PageRequest(page, size, Sort.Direction.ASC, sort);
        Page<Account> accounts = accountService.findAll(pageRequest);

        Resources<Resource<Account>> resources = accountService.convertToResource(accounts, page);
        resources.add(linkTo(methodOn(AccountController.class).getAccounts(page, size, sort))
                .withSelfRel());
        resources.add(linkTo(methodOn(AccountController.class).getAccounts(page, size, sort))
                .withRel("accounts"));

        return resources;
    }

    @PreAuthorize("permitAll")
    @RequestMapping(method = RequestMethod.POST)
    public Resource<Account> createAccount(@RequestBody Account account) {
        Account savedAccount = accountService.save(account);

        return accountService.convertToResource(savedAccount);
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.GET)
    public Resource<Account> getAccountById(@PathVariable String id) {
        Account account = accountService.findOne(id);

        return accountService.convertToResource(account);
    }
}
