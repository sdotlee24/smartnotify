package com.thirty.smartnotify.repositories;

import com.thirty.smartnotify.domain.Application;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ApplicationRepository extends CrudRepository<Application, Long> {
    Optional<Application> findApplicationBySenderEmail(String email);
}
