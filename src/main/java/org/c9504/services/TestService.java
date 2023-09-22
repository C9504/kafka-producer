package org.c9504.services;

import org.c9504.entities.Test;

import javax.enterprise.context.ApplicationScoped;
import javax.transaction.Transactional;

@ApplicationScoped
public class TestService {

    @Transactional
    public void saveTest(Test test) {
        test.persist();
    }

}
