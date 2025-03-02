package com.voncken.lostandfound.Contracts;

import com.voncken.lostandfound.Repositories.States.LostItemState;

// based on the uploading-files guide from spring.io
public interface ILostItemService {
    void init();
    Iterable<LostItemState> GetAll();
    void Add(LostItemState lostItemState);
}
