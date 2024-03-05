/**
 * 
 */
package com.loan.aggregator.model;



import java.io.Serializable;
import java.math.BigInteger;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;

/**
 * 
 */
@Entity
@Table(name="consumer")
public class Consumer implements Serializable{
	private static final long serialVersionUID = 1L;
	
	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	@Column(name="consumer_id", unique=true, nullable=false)
	private BigInteger consumerId;
	
	@Column(name="email",nullable=false)
	private String email;
	
	@Column(name="first_name",nullable=false)
	private String firstName;
	
	@Column(name="last_name",nullable=true)
	private String lastName;
	
	@Column(name="phone_number",nullable=false)
	private String phoneNumer;
	
	@Column(name="email_verified",nullable=false)
	private byte emailVerified;
	
	@Column(name="is_active",nullable=false)
	private byte isActive;
	
	@Column(name="is_logged_in",nullable=false)
	private byte isLoggedIn;
	
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name="created_date", nullable=false)
	private Date createdDate;
	
	@ManyToOne(fetch=FetchType.EAGER)
	@JoinColumn(name="app_info_lkp_id",nullable=false)
	private AppInfoLkp appinfolkp;
	
	@OneToMany(mappedBy="consumer",fetch=FetchType.LAZY,cascade=CascadeType.ALL)
	private Set<ConsumerSessionInfo> consumerSessionInfo;
	
	@OneToMany(mappedBy="consumer",fetch=FetchType.LAZY,cascade=CascadeType.ALL)
	private Set<ConsumerSessionLink> consumerSessionLinks = new HashSet<>();
	
	
	/**
	 * @return the consumerId
	 */
	public BigInteger getConsumerId() {
		return consumerId;
	}

	/**
	 * @param consumerId the consumerId to set
	 */
	public void setConsumerId(BigInteger consumerId) {
		this.consumerId = consumerId;
	}

	/**
	 * @return the email
	 */
	public String getEmail() {
		return email;
	}

	/**
	 * @param email the email to set
	 */
	public void setEmail(String email) {
		this.email = email;
	}

	/**
	 * @return the firstName
	 */
	public String getFirstName() {
		return firstName;
	}

	/**
	 * @param firstName the firstName to set
	 */
	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	/**
	 * @return the lastName
	 */
	public String getLastName() {
		return lastName;
	}

	/**
	 * @param lastName the lastName to set
	 */
	public void setLastName(String lastName) {
		this.lastName = lastName;
	}

	/**
	 * @return the phoneNumer
	 */
	public String getPhoneNumer() {
		return phoneNumer;
	}

	/**
	 * @param phoneNumer the phoneNumer to set
	 */
	public void setPhoneNumer(String phoneNumer) {
		this.phoneNumer = phoneNumer;
	}

	/**
	 * @return the emailVerified
	 */
	public byte getEmailVerified() {
		return emailVerified;
	}

	/**
	 * @param emailVerified the emailVerified to set
	 */
	public void setEmailVerified(byte emailVerified) {
		this.emailVerified = emailVerified;
	}

	/**
	 * @return the isActive
	 */
	public byte getIsActive() {
		return isActive;
	}

	/**
	 * @param isActive the isActive to set
	 */
	public void setIsActive(byte isActive) {
		this.isActive = isActive;
	}

	/**
	 * @return the isLoggedIn
	 */
	public byte getIsLoggedIn() {
		return isLoggedIn;
	}

	/**
	 * @param isLoggedIn the isLoggedIn to set
	 */
	public void setIsLoggedIn(byte isLoggedIn) {
		this.isLoggedIn = isLoggedIn;
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

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name="modified_date", nullable=false)
	private Date modifiedDate;



	/**
	 * @return the appinfolkp
	 */
	public AppInfoLkp getAppinfolkp() {
		return appinfolkp;
	}

	/**
	 * @param appinfolkp the appinfolkp to set
	 */
	public void setAppinfolkp(AppInfoLkp appinfolkp) {
		this.appinfolkp = appinfolkp;
	}

	/**
	 * @return the consumerSessionInfo
	 */
	public Set<ConsumerSessionInfo> getConsumerSessionInfo() {
		return consumerSessionInfo;
	}

	/**
	 * @param consumerSessionInfo the consumerSessionInfo to set
	 */
	public void setConsumerSessionInfo(Set<ConsumerSessionInfo> consumerSessionInfo) {
		this.consumerSessionInfo = consumerSessionInfo;
	}

	/**
	 * @return the consumerSessionLinks
	 */
	public Set<ConsumerSessionLink> getConsumerSessionLinks() {
		return consumerSessionLinks;
	}

	/**
	 * @param consumerSessionLinks the consumerSessionLinks to set
	 */
	public void setConsumerSessionLinks(Set<ConsumerSessionLink> consumerSessionLinks) {
		this.consumerSessionLinks = consumerSessionLinks;
	}

	
	
}
