/**
 * 
 */
package com.loan.aggregator.model;

import java.io.Serializable;
import java.math.BigInteger;
import java.util.Date;
import java.util.Set;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;

/**
 * 
 */
@Entity
@Table(name="appinfolkp")
public class AppInfoLkp implements Serializable{
	
	private static final long serialVersionUID = 1L;
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name="app_info_lkp_id",unique=true,nullable=false)
	private BigInteger appInfoLkpId;
	
	@Column(name="app_id", nullable=false, length=30)
	private String appId;
	
	@Column(name="registrations")
	private byte registrations;
	
	@OneToMany(mappedBy="appinfolkp", fetch=FetchType.LAZY)
	private Set<Consumer> consumers;
	
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name="created_date", nullable=false)
	private Date createdDate;
	
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name="modified_date", nullable=false)
	private Date modifiedDate;

	/**
	 * @return the appInfoLkpId
	 */
	public BigInteger getAppInfoLkpId() {
		return appInfoLkpId;
	}

	/**
	 * @param appInfoLkpId the appInfoLkpId to set
	 */
	public void setAppInfoLkpId(BigInteger appInfoLkpId) {
		this.appInfoLkpId = appInfoLkpId;
	}

	/**
	 * @return the appId
	 */
	public String getAppId() {
		return appId;
	}

	/**
	 * @param appId the appId to set
	 */
	public void setAppId(String appId) {
		this.appId = appId;
	}

	/**
	 * @return the registrations
	 */
	public byte isRegistrations() {
		return registrations;
	}

	/**
	 * @param registrations the registrations to set
	 */
	public void setRegistrations(byte registrations) {
		this.registrations = registrations;
	}

	/**
	 * @return the consumers
	 */
	public Set<Consumer> getConsumers() {
		return consumers;
	}

	/**
	 * @param consumers the consumers to set
	 */
	public void setConsumers(Set<Consumer> consumers) {
		this.consumers = consumers;
	}

	/**
	 * @return the createdDate
	 */
	public Date getCreatedDate() {
		return createdDate;
	}

	/**
	 * @param createdDate the createdDate to set
	 */
	public void setCreatedDate(Date createdDate) {
		this.createdDate = createdDate;
	}

	/**
	 * @return the modifiedDate
	 */
	public Date getModifiedDate() {
		return modifiedDate;
	}

	/**
	 * @param modifiedDate the modifiedDate to set
	 */
	public void setModifiedDate(Date modifiedDate) {
		this.modifiedDate = modifiedDate;
	}
	
	
	

}
