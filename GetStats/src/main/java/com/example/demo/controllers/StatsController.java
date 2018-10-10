package com.example.demo.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class StatsController {

	@Autowired
	StatsCache cache;

	@RequestMapping(path = "/statistics")
	public @ResponseBody GetStatsResponse getStats() {
		// This returns a JSON with stats

		GetStatsResponse msg = new GetStatsResponse();
		msg.setMin(cache.getMin());
		msg.setCount(cache.getSize());

		msg.setMax(cache.getMax());
		msg.setSum(cache.getSum());

		msg.setAvg(cache.getAvg());

		return msg;
	}

	@PostMapping("/transactions")
	public ResponseEntity<String> addTransactionFromBody(
			@RequestBody Transaction transaction) {

		cache.put(transaction.getTimestamp(), transaction.getAmount());

		return ResponseEntity.ok(transaction.toString());
	}

}
