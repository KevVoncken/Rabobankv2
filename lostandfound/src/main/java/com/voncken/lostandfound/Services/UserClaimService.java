package com.voncken.lostandfound.Services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.voncken.lostandfound.Contracts.IUserClaimService;
import com.voncken.lostandfound.Repositories.UserClaimRepository;
import com.voncken.lostandfound.Repositories.States.UserClaimState;

@Service
public class UserClaimService implements IUserClaimService {

    private final UserClaimRepository repository;

    @Autowired
    public UserClaimService(UserClaimRepository repository) {
        this.repository = repository;
    }

    @Override
    public Iterable<UserClaimState> GetAll() {
        return repository.GetAll();
    }

    @Override
    public void Add(UserClaimState userClaimState)
    {
        repository.Add(userClaimState);
    }

    @Override
    public void init() {
    }
}
