package com.example.commonmodule.repositories;

import com.example.commonmodule.models.TitleBody;
import org.springframework.data.repository.NoRepositoryBean;

@NoRepositoryBean
public interface TitleBodyRepository<M extends TitleBody> extends ManyToOneUserRepository<M> {
}
