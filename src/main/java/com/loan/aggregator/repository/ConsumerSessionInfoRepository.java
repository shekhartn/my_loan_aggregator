package com.loan.aggregator.repository;

import java.io.Serializable;
import java.math.BigInteger;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.loan.aggregator.model.AppInfoLkp;
import com.loan.aggregator.model.ConsumerSessionInfo;

import jakarta.transaction.Transactional;
@Repository
public interface ConsumerSessionInfoRepository extends JpaRepository<ConsumerSessionInfo,Serializable>{

	ConsumerSessionInfo findBySessionTokenAndDeleteFlag(String sessionToken, byte deleteFlag);
}
