package com.workshop.controller;

import com.workshop.entity.Account;
import com.workshop.exception.AccountNotFoundException;
import com.workshop.repository.AccountRepository;
import org.springframework.hateoas.Resource;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;

import static org.springframework.hateoas.mvc.ControllerLinkBuilder.linkTo;
import static org.springframework.hateoas.mvc.ControllerLinkBuilder.methodOn;

@RestController
public class AccountController {
    private final AccountRepository accountRepository;

    public AccountController(AccountRepository accountRepository) {
        this.accountRepository = accountRepository;
    }

    @RequestMapping(value = "/me", method = RequestMethod.GET)
    public Resource<Account> getMe(Principal principal) {
        Account account = accountRepository.findByUsername(principal.getName())
                .orElseThrow(() -> {
                    String msg = String.format("Account with username %s not found", principal.getName());
                    return new AccountNotFoundException(msg);
                });

        return getAccountResource(account, principal);
    }

    private Resource<Account> getAccountResource(Account account, Principal principal) {
        Resource<Account> accountResource = new Resource<>(account);
        accountResource.add(linkTo(methodOn(AccountController.class).getMe(principal)).withSelfRel());
        accountResource.add(linkTo(methodOn(AccountController.class).getMe(principal)).withRel("account"));

        return accountResource;
    }
}
