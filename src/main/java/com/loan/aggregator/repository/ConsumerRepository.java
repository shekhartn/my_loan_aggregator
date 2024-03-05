package com.loan.aggregator.repository;

import java.io.Serializable;
import java.math.BigInteger;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.loan.aggregator.model.AppInfoLkp;
import com.loan.aggregator.model.Consumer;

@Repository
public interface ConsumerRepository extends JpaRepository<Consumer,Serializable>{

	Consumer findByEmailAndIsActiveAndAppinfolkp(String email, byte isActive, AppInfoLkp appInfoLkp);

	Consumer findByConsumerIdAndIsActive(BigInteger consumerId, byte isActive);

}	
