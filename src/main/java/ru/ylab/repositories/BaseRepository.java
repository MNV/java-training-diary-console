package ru.ylab.repositories;

import ru.ylab.models.BaseModel;


public interface BaseRepository<M extends BaseModel, D> extends BaseImmutableRepository<M, D> {
    void update(M model);
}
