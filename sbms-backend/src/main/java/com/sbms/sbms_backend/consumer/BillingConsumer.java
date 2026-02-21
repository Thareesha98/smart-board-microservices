package com.sbms.sbms_backend.consumer;

import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;

import com.sbms.sbms_backend.events.PaymentSucceededEvent;
import com.sbms.sbms_backend.model.MonthlyBill;
import com.sbms.sbms_backend.model.enums.MonthlyBillStatus;
import com.sbms.sbms_backend.repository.MonthlyBillRepository;

import jakarta.transaction.Transactional;

public class BillingConsumer {
	
	@Autowired
	private MonthlyBillRepository billRepository;
	
	@RabbitListener(queues = "billing.payment.succeeded.queue")
	@Transactional
	public void handlePaymentSucceeded(PaymentSucceededEvent event) {

	    if (event.getMonthlyBillId() == null) return;

	    MonthlyBill bill = billRepository
	        .findById(event.getMonthlyBillId())
	        .orElseThrow();

	    if (bill.getStatus() == MonthlyBillStatus.PAID) {
	        return; // IDEMPOTENT
	    }

	    bill.setStatus(MonthlyBillStatus.PAID);
	    billRepository.save(bill);
	}

}
