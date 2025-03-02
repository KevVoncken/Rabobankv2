package com.voncken.lostandfound.Contracts;

import com.voncken.lostandfound.Repositories.States.UserClaimState;

// based on the uploading-files guide from spring.io
public interface IUserClaimService {
    Iterable<UserClaimState> GetAll();
    void init();
    void Add(UserClaimState userClaimState);
}
