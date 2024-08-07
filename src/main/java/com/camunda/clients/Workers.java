package com.camunda.clients;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.springframework.stereotype.Component;

import io.camunda.zeebe.client.api.response.ActivatedJob;
import io.camunda.zeebe.client.api.worker.JobClient;
import io.camunda.zeebe.spring.client.annotation.JobWorker;

@Component
public class Workers {

	@JobWorker(type = "checkIfInsuranceCoversClaim")
	public void checkIfInsuranceCoversClaim(final JobClient client, final ActivatedJob job) throws ParseException, ClassCastException {
		System.out.println("Check Insurance Claim Task Started");
		try {
			final Map<String, Object> inputVariables = job.getVariablesAsMap();
			final String reference = (String) inputVariables.get("claimReference");
	        final Double amount = (Double) inputVariables.get("claimAmount");
	        final String claimdate = (String) inputVariables.get("claimDate");
	        
	        SimpleDateFormat formatter = new SimpleDateFormat("ddMMyyyy");
	        Date date = formatter.parse(claimdate);
	        
	        System.out.println("Claim "+ reference+" raised for amount: $"+amount);
			
			
			final Map<String, Object> outputVariables = new HashMap<String, Object>();
	        outputVariables.put("policyCoversClaim", date.before(new Date()));
	        
	        client.newCompleteCommand(job.getKey()).variables(outputVariables).send().join();
		} catch (ParseException e) {
			client.newThrowErrorCommand(job.getKey()).errorCode("dateParseError").send().join();
		} catch (ClassCastException e) {
			client.newThrowErrorCommand(job.getKey()).errorCode("doubleParseError").send().join();
		}
        System.out.println("Check Insurance Claim Task Completed");
	}
	
	@JobWorker(type = "checkCaseHistory")
	public void checkCaseHistory(final JobClient client, final ActivatedJob job) {
		
		System.out.println("Check Case History Task Started");
		final Map<String, Object> inputVariables = job.getVariablesAsMap();
		String claimdate = (String) inputVariables.get("claimDate");
		
		String year = claimdate.substring(4);
		
		final Map<String, Object> outputVariables = new HashMap<String, Object>();
        outputVariables.put("caseAge", (2024 - Integer.parseInt(year)));
        client.newCompleteCommand(job.getKey()).variables(outputVariables).send().join();
		System.out.println("Check Case History Task Completed");
	}
	
	@JobWorker(type = "shouldClaimAccepted")
	public void shouldClaimAccepted(final JobClient client, final ActivatedJob job) {
		System.out.println("Should Claim Accepted - Manual Task Started");
		
		final Map<String, Object> inputVariables = job.getVariablesAsMap();
		String claimdate = (String) inputVariables.get("claimDate");
		
		String year = claimdate.substring(4);
		
		final Map<String, Object> outputVariables = new HashMap<String, Object>();
		boolean value = (2024 - Integer.parseInt(year) <= 18);
		outputVariables.put("claimAccepted", value);
        client.newCompleteCommand(job.getKey()).variables(outputVariables).send().join();
		
		System.out.println("Should Claim Accepted - Manual Task Completed");
	}
	
	@JobWorker(type = "rejectClaim")
	public void rejectClaim(final JobClient client, final ActivatedJob job) {
		System.out.println("Claim Rejected Task Started");
		final Map<String, Object> outputVariables = new HashMap<String, Object>();
		outputVariables.put("Claim Status", "Rejected");
        client.newCompleteCommand(job.getKey()).variables(outputVariables).send().join();
        System.out.println("Claim Rejected Task Completed");
	}
	
	@JobWorker(type = "acceptClaim")
	public void acceptClaim(final JobClient client, final ActivatedJob job) {
		System.out.println("Claim Accepted Task Started");
		final Map<String, Object> outputVariables = new HashMap<String, Object>();
		outputVariables.put("Claim Status", "Accepted");
        client.newCompleteCommand(job.getKey()).variables(outputVariables).send().join();
        System.out.println("Claim Accepted Task Completed");
	}
	
	@JobWorker(type="processPayment")
	public void processPayment(final JobClient client, final ActivatedJob job) {
		System.out.println("Payment process task started");
		System.out.println("Payment processed");
		client.newCompleteCommand(job.getKey()).send().join();
		System.out.println("Payment process task completed");
	}
	
	@JobWorker(type = "correctAmountVariableType")
	public void correctAmountVariableType(final JobClient client, final ActivatedJob job) {
		System.out.println("Correct Amount Data Type - Error Task Started");
		
		final Map<String, Object> inputVariables = job.getVariablesAsMap();
		Integer amount = (Integer) inputVariables.get("claimAmount");
		
		
		final Map<String, Object> outputVariables = new HashMap<String, Object>();
		
		outputVariables.put("claimAmount", Double.valueOf(amount));
        client.newCompleteCommand(job.getKey()).variables(outputVariables).send().join();
		
        System.out.println("Correct Amount Data Type - Error Task Completed");
	}

}
