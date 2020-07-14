package com.scratch.tpservice;

import com.scratch.tpservice.controllers.AccountsController;
import com.scratch.tpservice.controllers.TransactionsController;
import com.scratch.tpservice.models.*;
import com.scratch.tpservice.services.AccountsService;
import com.scratch.tpservice.services.TransactionsService;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.util.MultiValueMap;
import org.springframework.util.LinkedMultiValueMap;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@SpringBootTest
class TpserviceApplicationTests {

	@Autowired
	private TransactionsService transactionsService;

	@Autowired
	private AccountsService accountsService;

	@Autowired
	private TransactionsController transactionsController;

	@Autowired
	private AccountsController accountsController;

	@Test
	void contextLoads() {
	}

	@Test
	public void testCreateAccount() {
		String accountId = "ACT1";
		accountsService.createAccount(accountId);
		// check if account is created with the right balance and not frozen.
		assertTrue(accountsService.getAccountsDB().containsKey(accountId));
		assertEquals(accountsService.getBalance(accountId), 0);
		assertFalse(accountsService.isFrozen(accountId));
	}


	@Test
	public void testDeposit() {
		double amount = 100;
		String accountId = "ACT2";
		try {
			accountsService.deposit(accountId, amount);
			assertTrue(accountsService.getAccountsDB().containsKey(accountId));
			assertEquals(accountsService.getBalance(accountId), 100);
			assertFalse(accountsService.isFrozen(accountId));
		} catch (TspException e) {
			e.printStackTrace();
		}
	}

	@Test
	public void testFreezeAccount() {
		String accountId = "ACT3";

		accountsService.freezeAccount(accountId);
		assertTrue(accountsService.isFrozen(accountId));
	}

	@Test
	public void testThawAccount() {
		String accountId = "ACT4";

		accountsService.freezeAccount(accountId);
		assertTrue(accountsService.isFrozen(accountId));
		accountsService.thawAccount(accountId);
		assertFalse(accountsService.isFrozen(accountId));
	}

	@Test
	public void testDepositWithInvalidAmount() {
		double amount = -50;
		String accountId = "ACT5";
		try {
			accountsService.deposit(accountId, amount);
			assertEquals(accountsService.getBalance(accountId), 100);
			assertFalse(accountsService.isFrozen(accountId));
		} catch (TspException e) {
			e.printStackTrace();
		}
	}

	@Test
	public void testDepositWithFrozenAccount() {
		double amount = 50;
		String accountId = "ACT6";
		try {
			accountsService.deposit(accountId, amount);
			accountsService.freezeAccount(accountId);
			accountsService.deposit(accountId, amount);
			assertEquals(accountsService.getBalance(accountId), 50);
		} catch (TspException e) {
			e.printStackTrace();
		}
	}
	@Test
	public void testWithdraw() {
		double amount = 50;
		String accountId = "ACT7";
		try {
			accountsService.deposit(accountId, amount);
			accountsService.withdraw(accountId, amount);
			assertEquals(accountsService.getBalance(accountId), 0);
		} catch (TspException e) {
			e.printStackTrace();
		}
	}

	@Test
	public void testWithdrawWithInvalidAmount() {
		double amount = -50;
		String accountId = "ACT8";
		try {
			accountsService.withdraw(accountId, amount);
			assertEquals(accountsService.getBalance(accountId), 100);
			assertFalse(accountsService.isFrozen(accountId));
		} catch (TspException e) {
			e.printStackTrace();
		}
	}

	@Test
	public void testWithdrawWithFrozenAccount() {
		double amount = 50;
		String accountId = "ACT9";
		try {
			accountsService.deposit(accountId, amount);
			accountsService.freezeAccount(accountId);
			accountsService.withdraw(accountId, amount);
			assertEquals(accountsService.getBalance(accountId), 50);
		} catch (TspException e) {
			e.printStackTrace();
		}
	}

	@Test
	public void testValidXfer(){
		double amount = 50;
		String fromId = "ACT10";
		String toId = "ACT11";

		Xfer xfer = new Xfer();
		xfer.setAmount(amount);
		xfer.setFromId(fromId);
		xfer.setToId(toId);

		List<Transaction> transactions = new ArrayList<>();
		transactions.add(xfer);
		try {
			accountsService.deposit(fromId, amount);
			accountsService.deposit(toId, amount);
			List<Transaction> invalidTransactions = transactionsService.executeTransactions(transactions);
			assertEquals(accountsService.getBalance(fromId), 0);
			assertEquals(accountsService.getBalance(toId), 100);
			assertEquals(0, invalidTransactions.size());
		} catch (TspException e) {
			e.printStackTrace();
		}
	}

	@Test
	public void testInvalidXfer(){
		double depositAmount = 50;
		double xferAmount = 100;
		String fromId = "ACT12";
		String toId = "ACT13";

		Xfer xfer = new Xfer();
		xfer.setAmount(xferAmount);
		xfer.setFromId(fromId);
		xfer.setToId(toId);

		List<Transaction> transactions = new ArrayList<>();
		transactions.add(xfer);
		try {
			accountsService.deposit(fromId, depositAmount);
			List<Transaction> invalidTransactions = transactionsService.executeTransactions(transactions);
			assertEquals(accountsService.getBalance(fromId), 50);
			assertEquals(accountsService.getBalance(toId), 0);
			assertEquals(1, invalidTransactions.size());
		} catch (TspException e) {
			e.printStackTrace();
		}
	}

	@Test
	public void testBatchTransactions(){

		List<Transaction> transactions = new ArrayList<>();

		Deposit deposit1 = new Deposit();
		deposit1.setAmount(100.00);
		deposit1.setAccountId("ACT300");
		transactions.add(deposit1);

		Xfer xfer = new Xfer();
		xfer.setAmount(10.00);
		xfer.setFromId("ACT300");
		xfer.setToId("ACT100");
		transactions.add(xfer);

		Freeze freeze = new Freeze();
		freeze.setAccountId("ACT303");
		transactions.add(freeze);

		Deposit deposit2 = new Deposit();
		deposit2.setAmount(20.00);
		deposit2.setAccountId("ACT303");
		transactions.add(deposit2);

		Withdraw withdraw = new Withdraw();
		withdraw.setAmount(5.00);
		withdraw.setAccountId("ACT100");
		transactions.add(withdraw);

		Thaw thaw = new Thaw();
		thaw.setAccountId("ACT303");
		transactions.add(thaw);

		List<Transaction> invalidTransactions = transactionsService.executeTransactions(transactions);
		assertEquals(accountsService.getBalance("ACT100"), 5.00);
		assertEquals(accountsService.getBalance("ACT300"), 90.00);
		assertEquals(accountsService.getBalance("ACT303"), 0);
		assertEquals(1, invalidTransactions.size());
	}

	@Test
	public void testValidJsonTransactions() {
		Map<String, Object> map = new HashMap<>();
		map.put("cmd", "DEPOSIT");
		map.put("accountId", "ACT801");
		map.put("amount", 100.00);
		assertTrue(transactionsController.isValid(map));

		map = new HashMap<>();
		map.put("cmd", "WITHDRAW");
		map.put("accountId", "ACT801");
		map.put("amount", 100.00);
		assertTrue(transactionsController.isValid(map));

		map = new HashMap<>();
		map.put("cmd", "FREEZE");
		map.put("accountId", "ACT801");
		assertTrue(transactionsController.isValid(map));

		map = new HashMap<>();
		map.put("cmd", "THAW");
		map.put("accountId", "ACT801");
		assertTrue(transactionsController.isValid(map));

		map = new HashMap<>();
		map.put("cmd", "XFER");
		map.put("fromId", "ACT801");
		map.put("toId", "ACT802");
		map.put("amount", 100.00);
		assertTrue(transactionsController.isValid(map));
	}

	@Test
	public void testInvalidJsonTransactions() {
		Map<String, Object> map = new HashMap<>();
		map.put("cmd", "DEPOSIT");
		map.put("accountId", "ACT801");
		assertFalse(transactionsController.isValid(map));

		map = new HashMap<>();
		map.put("cmd", "WITHDRAW");
		map.put("amount", 100.00);
		assertFalse(transactionsController.isValid(map));

		map = new HashMap<>();
		map.put("cmd", "FREEZE");
		assertFalse(transactionsController.isValid(map));

		map = new HashMap<>();
		map.put("cmd", "THAW");
		assertFalse(transactionsController.isValid(map));

		map = new HashMap<>();
		map.put("cmd", "XFER");
		map.put("fromId", "ACT801");
		map.put("toId", "ACT802");
		assertFalse(transactionsController.isValid(map));
	}

	@Test
	public void testGetAccountBalancesFromJson(){
		List<Transaction> transactions = new ArrayList<>();

		Deposit deposit1 = new Deposit();
		deposit1.setAmount(100.00);
		deposit1.setAccountId("ACT900");
		transactions.add(deposit1);
		Deposit deposit2 = new Deposit();
		deposit2.setAmount(20.00);
		deposit2.setAccountId("ACT901");
		transactions.add(deposit2);
		transactionsService.executeTransactions(transactions);

		MultiValueMap<String,String> accountMap = new LinkedMultiValueMap<>();
		List<String> accountIds = new ArrayList<>();
		accountIds.add("ACT900");
		accountIds.add("ACT901");
		accountMap.put("accountId",accountIds);

		List<Map<String, Object>> accounts = accountsController.getAccountBalances(accountMap);
		Map<String, Object> account = accounts.get(0);
		assertEquals(account.get("accountId"), "ACT900");
		assertEquals(account.get("balance"), 100.00);
		account = accounts.get(1);
		assertEquals(account.get("accountId"), "ACT901");
		assertEquals(account.get("balance"), 20.00);

	}

	@Test
	public void testGetUnknownAccountBalancesFromJson(){

		MultiValueMap<String,String> accountMap = new LinkedMultiValueMap<>();
		List<String> accountIds = new ArrayList<>();
		accountIds.add("ACT902");
		accountMap.put("accountId",accountIds);

		List<Map<String, Object>> accounts = accountsController.getAccountBalances(accountMap);
		Map<String, Object> account = accounts.get(0);
		assertEquals(account.get("accountId"), "ACT902");
		assertEquals(account.get("balance"), 0.00);

	}



}
