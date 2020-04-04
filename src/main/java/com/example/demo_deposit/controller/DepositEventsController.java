package com.example.demo_deposit.controller;


import javax.validation.Valid;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo_deposit.domain.Transaction;
import com.example.demo_deposit.producer.DepositEventProducer;
import com.example.demo_deposit.service.ITransactionService;
import com.fasterxml.jackson.core.JsonProcessingException;

@RestController
public class DepositEventsController {
	
	private Logger log = LoggerFactory.getLogger(DepositEventsController.class);
	
	@Autowired
	DepositEventProducer depositEventProducer;
	
	@Autowired
	private ITransactionService transactionService;  
		
	
	@PostMapping("/v1/depositEvent")
	public ResponseEntity<Transaction> postLibraryEvent(@RequestBody  Transaction libraryEvent) throws JsonProcessingException{
		
		Transaction tranSql = transactionService.save(libraryEvent);
		
		log.info("antes sendDepositEvent_Approach3 ");
		
		depositEventProducer.sendDepositEvent_Approach3(libraryEvent);
		
		
		log.info("despues sendDepositEvent_Approach3 ");	
		
		
		
		return ResponseEntity.status(HttpStatus.CREATED).body(libraryEvent);
	}
	
	

}
