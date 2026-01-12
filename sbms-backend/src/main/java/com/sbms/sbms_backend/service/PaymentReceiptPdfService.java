package com.sbms.sbms_backend.service;


import com.sbms.sbms_backend.model.PaymentTransaction;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;

import com.itextpdf.text.*;
import com.itextpdf.text.pdf.PdfWriter;

@Service
public class PaymentReceiptPdfService {

    public byte[] generateReceipt(PaymentTransaction tx) {

        try {
            Document document = new Document();
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            PdfWriter.getInstance(document, out);

            document.open();

            document.add(new Paragraph("SMART BOARDING MANAGEMENT SYSTEM"));
            document.add(new Paragraph("Payment Receipt"));
            document.add(new Paragraph(" "));

            document.add(new Paragraph("Transaction Ref: " + tx.getTransactionRef()));
            document.add(new Paragraph("Amount: LKR " + tx.getAmount()));
            document.add(new Paragraph("Payment Method: " + tx.getMethod()));
            document.add(new Paragraph("Status: " + tx.getStatus()));
            document.add(new Paragraph("Date: " + tx.getCreatedAt()));

            document.add(new Paragraph(" "));
            document.add(new Paragraph("Thank you for your payment."));

            document.close();
            return out.toByteArray();

        } catch (Exception e) {
            throw new RuntimeException("Failed to generate receipt PDF", e);
        }
    }
}
