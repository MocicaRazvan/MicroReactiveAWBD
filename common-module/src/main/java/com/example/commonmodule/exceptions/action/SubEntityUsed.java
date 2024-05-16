package com.example.commonmodule.exceptions.action;

import com.example.commonmodule.exceptions.notFound.IdNameException;
import lombok.Getter;

@Getter
public class SubEntityUsed extends IdNameException {


    public SubEntityUsed(String name, Long id) {
        super(name, id, "Entity " + name + " with id " + id.toString() + " is used!");

    }

}
