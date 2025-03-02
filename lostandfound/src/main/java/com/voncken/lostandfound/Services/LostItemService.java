package com.voncken.lostandfound.Services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.voncken.lostandfound.Contracts.ILostItemService;
import com.voncken.lostandfound.Repositories.LostItemRepository;
import com.voncken.lostandfound.Repositories.States.LostItemState;

@Service
public class LostItemService implements ILostItemService {

    private final LostItemRepository repository;

    @Autowired
    public LostItemService(LostItemRepository repository) {
        this.repository = repository;
    }

    @Override
    public Iterable<LostItemState> GetAll() {
        return repository.GetAll();
    }

    @Override
    public void Add(LostItemState lostItemState) {
        repository.Add(lostItemState);
    }

    @Override
    public void init() {
    }
}
