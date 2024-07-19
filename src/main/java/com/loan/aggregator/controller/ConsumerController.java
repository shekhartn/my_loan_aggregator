/**
 * 
 */
package com.loan.aggregator.controller;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.ResourceBundle;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;

import com.loan.aggregator.exception.MyLoanAggregatorException;
import com.loan.aggregator.manager.ConsumerManager;
import com.loan.aggregator.model.Consumer;
import com.loan.aggregator.repository.ConsumerRepository;
import com.loan.aggregator.request.ConsumerRequest;
import com.loan.aggregator.request.LoginRequest;
import com.loan.aggregator.request.RequestHeader;
import com.loan.aggregator.response.ConsumerRegistrationResponse;
import com.loan.aggregator.response.Response;
import com.loan.aggregator.util.CommonConstants;
import com.loan.aggregator.util.MessageUtil;

import jakarta.servlet.http.HttpServletRequest;

/**
 * 
 */
@RestController
public class ConsumerController extends BaseController {
	
	List<Consumer> consumers=new ArrayList<Consumer>();
	
	@Autowired
	private ConsumerRepository consumerRepository;
	
	@Autowired
	private ConsumerManager consumerManager;
	
	static final ResourceBundle envResourceBundle = ResourceBundle.getBundle(CommonConstants.ENV);
	
	@GetMapping("/consumers/{consumerId}/emailverification/{token}")
	public ModelAndView getConsumer(@PathVariable BigInteger consumerId,
			@PathVariable String token,
			@RequestParam(value = "appId") String appId,
			HttpServletRequest request,
			ModelMap modelMap)throws MyLoanAggregatorException {
		System.out.println("*****Start of emailVerification*****");
		Consumer consumer=null;
		if(consumerId != null) {
			consumer=consumerRepository.findByConsumerIdAndIsActive(consumerId,(byte)1);
		}
		Optional<ModelAndView> validationView=validateConsumer(consumer,appId);
		if(validationView.isPresent()){
			return validationView.get();
		}
		String message=consumerManager.emailVerified(consumer,consumerId,token,appId,request);
		modelMap.addAttribute(CommonConstants.MESSAGE_PROP, message);
		return new ModelAndView(appId.toLowerCase() + "/success", modelMap);
	}	
	private Optional<ModelAndView> validateConsumer(Consumer consumer, String appId) {
		if(Objects.isNull(consumer)) {
			ModelMap modelMap = new ModelMap();
			ModelAndView view = new ModelAndView();
			//modelMap.addAllAttributes(linkConfig.getModelMap());
			//view.addAllObjects(modelMap);
			view.addObject(CommonConstants.MESSAGE_PROP, MessageUtil.getMessage("consumer.not.found"));
			view.setViewName(appId + "/error");
			return Optional.of(view);
		}
		return Optional.empty();
	}
	@SuppressWarnings("unchecked")
	@PostMapping("/consumers")
	public @ResponseBody ResponseEntity<ConsumerRegistrationResponse> registerConsumer(@RequestBody ConsumerRequest consumerRequest,
			HttpServletRequest request) {
		Response responseData=null;
		RequestHeader requestHeaders=null;
		try {
			requestHeaders=createRequestHeaders(request);
			responseData=consumerManager.registerConsumer(consumerRequest,requestHeaders);
		} catch (MyLoanAggregatorException exception) {
			throw new MyLoanAggregatorException(exception.getStatusCode(),exception.getMessage());
		}
		catch (Exception exception) {
			throwMyLoanAggregatorException(requestHeaders);
		}
		
		System.out.println("Hey Mr/Mrs."+consumerRequest.getFirstName()+" "+consumerRequest.getLastName()+", Your registration is successful!");
		return (ResponseEntity<ConsumerRegistrationResponse>)restResponse(responseData,request.getMethod());
	}

	@GetMapping("/consumers")
	public @ResponseBody List<Consumer> getConsumers(){
		return consumerRepository.findAll();
	}
	@PutMapping("/consumers")
	public @ResponseBody Consumer updateConsumer(@RequestBody Consumer consumer) {
		 consumer=consumerRepository.save(consumer);
		 return consumer;
	}
	
	@PostMapping("/consumers/login")
	public @ResponseBody ResponseEntity<ConsumerRegistrationResponse> loginConsumer(@RequestBody LoginRequest loginRequest,
			HttpServletRequest request) {
		Response responseData=null;
		RequestHeader requestHeaders=null;
		try {
			requestHeaders=createRequestHeaders(request);
			responseData=consumerManager.login(loginRequest,requestHeaders);
		} catch (MyLoanAggregatorException exception) {
			throw new MyLoanAggregatorException(exception.getStatusCode(),exception.getMessage());
		}
		catch (Exception exception) {
			throwMyLoanAggregatorException(requestHeaders);
		}
		
		System.out.println(loginRequest.getEmail()+", Your Login is successful!");
		return (ResponseEntity<ConsumerRegistrationResponse>)restResponse(responseData,request.getMethod());
	}
}
