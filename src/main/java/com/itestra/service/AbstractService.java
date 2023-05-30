package com.itestra.service;

import com.itestra.domain.AbstractDomainModel;

public abstract class AbstractService<T extends AbstractDomainModel> {
    public abstract T getById(String tid);
}
