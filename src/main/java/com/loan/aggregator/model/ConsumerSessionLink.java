/**
 * 
 */
package com.loan.aggregator.model;



import java.io.Serializable;
import java.math.BigInteger;
import java.util.Date;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;

/**
 * 
 */
@Entity
@Table(name="consumersessionlink")
public class ConsumerSessionLink implements Serializable {
	private static final long serialVersionUID = 1L;
	
	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	@Column(name="consumer_sessionlink_id", unique=true, nullable=false)
	private BigInteger consumerSessionLinkId;
	
	@Column(name="secret_token",nullable=true)
	private String secretToken;
	
	@Column(length=50)
	private String type;
	
	@Column(name="delete_flag",nullable=false)
	private byte deleteFlag;
	
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name="created_date", nullable=false)
	private Date createdDate;
	
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name="modified_date", nullable=false)
	private Date modifiedDate;
	
	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="consumer_id",nullable=false)
	private Consumer consumer;
	
	

	/**
	 * @return the consumerSessionLinkId
	 */
	public BigInteger getConsumerSessionLinkId() {
		return consumerSessionLinkId;
	}

	/**
	 * @param consumerSessionLinkId the consumerSessionLinkId to set
	 */
	public void setConsumerSessionLinkId(BigInteger consumerSessionLinkId) {
		this.consumerSessionLinkId = consumerSessionLinkId;
	}

	/**
	 * @return the secretToken
	 */
	public String getSecretToken() {
		return secretToken;
	}

	/**
	 * @param secretToken the secretToken to set
	 */
	public void setSecretToken(String secretToken) {
		this.secretToken = secretToken;
	}

	/**
	 * @return the type
	 */
	public String getType() {
		return type;
	}

	/**
	 * @param type the type to set
	 */
	public void setType(String type) {
		this.type = type;
	}

	/**
	 * @return the deleteFlag
	 */
	public byte getDeleteFlag() {
		return deleteFlag;
	}

	/**
	 * @param deleteFlag the deleteFlag to set
	 */
	public void setDeleteFlag(byte deleteFlag) {
		this.deleteFlag = deleteFlag;
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

	/**
	 * @return the consumer
	 */
	public Consumer getConsumer() {
		return consumer;
	}

	/**
	 * @param consumer the consumer to set
	 */
	public void setConsumer(Consumer consumer) {
		this.consumer = consumer;
	}

		
}
