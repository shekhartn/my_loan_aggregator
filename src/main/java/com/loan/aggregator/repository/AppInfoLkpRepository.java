package com.loan.aggregator.repository;

import java.io.Serializable;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.loan.aggregator.model.AppInfoLkp;

@Repository
public interface AppInfoLkpRepository extends JpaRepository<AppInfoLkp,Serializable>{

	AppInfoLkp findByAppId(String appId);	
}
