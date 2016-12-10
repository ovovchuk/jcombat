package com.workshop.service;

import com.workshop.controller.AccountController;
import com.workshop.entity.Account;
import com.workshop.exception.AccountNotFoundException;
import com.workshop.repository.AccountRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.hateoas.PagedResources;
import org.springframework.hateoas.Resource;
import org.springframework.hateoas.Resources;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static org.springframework.hateoas.mvc.ControllerLinkBuilder.linkTo;
import static org.springframework.hateoas.mvc.ControllerLinkBuilder.methodOn;

@Service
public class AccountService {
    private final AccountRepository accountRepository;

    public AccountService(AccountRepository accountRepository) {
        this.accountRepository = accountRepository;
    }

    public Page<Account> findAll(Pageable pageable) {
        return accountRepository.findAll(pageable);
    }

    public Account findByUsername(String username) {
        return accountRepository.findByUsername(username)
                .orElseThrow(() -> {
                    String msg = String.format("Account with username %s not found", username);
                    return new AccountNotFoundException(msg);
                });
    }

    public Account save(Account account) {
        return accountRepository.save(account);
    }

    public Resource<Account> convertToResource(Account account) {
        Resource<Account> accountResource = new Resource<>(account);
        accountResource.add(linkTo(methodOn(AccountController.class).getAccountById(account.getId()))
                .withSelfRel());
        accountResource.add(linkTo(methodOn(AccountController.class).getAccountById(account.getId()))
                .withRel("account"));

        return accountResource;
    }

    public Resources<Resource<Account>> convertToResource(Page<Account> accounts, int page) {
        List<Resource<Account>> resources = accounts.getContent().stream()
                .map(this::convertToResource)
                .collect(Collectors.toList());

        PagedResources.PageMetadata pageMetadata = new PagedResources.PageMetadata(
                accounts.getSize(), page, accounts.getTotalElements(), accounts.getTotalPages());

        return new PagedResources<>(resources, pageMetadata);
    }

    public Account findOne(String id) {
        return Optional.ofNullable(accountRepository.findOne(id))
                .orElseThrow(() -> {
                    String msg = String.format("Account with id %s not found", id);
                    return new AccountNotFoundException(msg);
                });
    }
}
