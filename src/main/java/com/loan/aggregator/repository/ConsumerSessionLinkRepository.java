package com.loan.aggregator.repository;

import java.io.Serializable;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.loan.aggregator.model.Consumer;
import com.loan.aggregator.model.ConsumerSessionLink;
@Repository
public interface ConsumerSessionLinkRepository extends JpaRepository<ConsumerSessionLink,Serializable>{

	ConsumerSessionLink findByConsumerAndTypeAndDeleteFlag(Consumer consumer, String type, byte deleteFlag);

  ConsumerSessionLink findBySecretTokenAndTypeAndConsumer(String token, String string, Consumer consumer);
}
