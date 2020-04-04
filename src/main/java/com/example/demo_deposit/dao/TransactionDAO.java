package com.example.demo_deposit.dao;

import org.springframework.data.repository.CrudRepository;

import com.example.demo_deposit.domain.Transaction;

public interface TransactionDAO extends CrudRepository<Transaction, Integer>{

	
}