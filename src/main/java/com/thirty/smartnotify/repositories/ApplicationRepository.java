package com.thirty.smartnotify.repositories;

import com.thirty.smartnotify.domain.Application;
import com.thirty.smartnotify.model.StatusEnum;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;


@Repository
public interface ApplicationRepository extends CrudRepository<Application, Long> {
    List<Application> findApplicationBySenderEmail(String email);
    @Transactional
    @Modifying
    @Query("UPDATE Application a SET a.status = :newStatus WHERE a.senderEmail = :email")
    int updateApplicationBySenderEmail(@Param("email") String email, @Param("newStatus") StatusEnum newStatus);
}
