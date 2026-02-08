package com.sbms.sbms_payment_service.service;

import com.itextpdf.text.*;
import com.itextpdf.text.pdf.*;
import com.itextpdf.text.pdf.draw.LineSeparator;

import com.sbms.sbms_payment_service.client.BoardingClient;
import com.sbms.sbms_payment_service.client.UserClient;
import com.sbms.sbms_payment_service.dto.BoardingInfo;
import com.sbms.sbms_payment_service.dto.UserBasicInfo;
import com.sbms.sbms_payment_service.entity.Payment;
import com.sbms.sbms_payment_service.entity.PaymentIntent;
import com.sbms.sbms_payment_service.repository.PaymentIntentRepository;

import lombok.RequiredArgsConstructor;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.time.format.DateTimeFormatter;

@Service
@RequiredArgsConstructor
public class PaymentReceiptPdfService {

    private final PaymentIntentRepository intentRepository;
    private final UserClient userClient;
    private final BoardingClient boardingClient;

    public byte[] generate(Payment payment) {

        try {
            // ---------------- LOAD PAYMENT INTENT ----------------

            PaymentIntent intent = intentRepository
                    .findById(payment.getPaymentIntentId())
                    .orElseThrow(() ->
                            new IllegalStateException("PaymentIntent not found")
                    );

            // ---------------- FETCH REMOTE DATA ----------------

            UserBasicInfo student =
                    userClient.getUser(intent.getStudentId());

            UserBasicInfo owner =
                    userClient.getUser(intent.getOwnerId());

            BoardingInfo boarding =
                    boardingClient.getBoarding(intent.getBoardingId());

            // ---------------- PDF GENERATION ----------------

            Document doc = new Document(PageSize.A4, 36, 36, 36, 36);
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            PdfWriter.getInstance(doc, out);
            doc.open();

            BaseColor PRIMARY = new BaseColor(37, 99, 235);
            BaseColor DARK = new BaseColor(15, 23, 42);
            BaseColor GRAY = new BaseColor(100, 116, 139);
            BaseColor SUCCESS = new BaseColor(34, 197, 94);

            Font brand = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 22, PRIMARY);
            Font slogan = FontFactory.getFont(FontFactory.HELVETICA, 11, GRAY);
            Font section = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 14, DARK);
            Font label = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 10, GRAY);
            Font value = FontFactory.getFont(FontFactory.HELVETICA, 11, DARK);
            Font success = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 14, SUCCESS);

            // -------- HEADER --------

            doc.add(new Paragraph("SMART BOARD", brand));
            doc.add(new Paragraph("Live Smarter. Manage Better.", slogan));
            doc.add(Chunk.NEWLINE);

            try {
                InputStream sealStream =
                        new ClassPathResource("pdf/paid-seal.png").getInputStream();
                Image seal = Image.getInstance(sealStream.readAllBytes());
                seal.scaleAbsolute(120, 120);
                seal.setAbsolutePosition(420, 700);
                doc.add(seal);
            } catch (Exception ignored) {}

            doc.add(new Paragraph("PAYMENT RECEIPT", section));
            doc.add(new LineSeparator());
            doc.add(Chunk.NEWLINE);

            // -------- META --------

            PdfPTable meta = new PdfPTable(2);
            meta.setWidthPercentage(100);

            addRow(meta, "Receipt No",
                    payment.getGatewayReference(), label, value);

            addRow(meta, "Date",
                    payment.getCompletedAt()
                            .format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")),
                    label, value);

            addRow(meta, "Payment Method",
                    payment.getGateway(), label, value);

            addRow(meta, "Status", "PAID", label, success);

            doc.add(meta);
            doc.add(Chunk.NEWLINE);

            // -------- PARTIES --------

            PdfPTable parties = new PdfPTable(2);
            parties.setWidthPercentage(100);

            addBlock(parties, "STUDENT",
                    student != null ? student.fullName() : "N/A");

            addBlock(parties, "OWNER",
                    owner != null ? owner.fullName() : "N/A");

            doc.add(parties);
            doc.add(Chunk.NEWLINE);

            // -------- BOARDING --------

            doc.add(new Paragraph(
                    "Boarding: " +
                    (boarding != null ? boarding.title() : "N/A"),
                    value
            ));
            doc.add(Chunk.NEWLINE);

            // -------- AMOUNTS --------

            PdfPTable amounts = new PdfPTable(2);
            amounts.setWidthPercentage(100);

            addMoney(amounts, "Gross Amount", payment.getAmount());

            // Fees can be calculated or fetched later
            addMoney(amounts, "Platform Fee (2%)", "—");
            addMoney(amounts, "Gateway Fee", "—");

            doc.add(amounts);

            // -------- FOOTER --------

            doc.add(Chunk.NEWLINE);
            doc.add(new LineSeparator());
            doc.add(new Paragraph(
                    "This is a system-generated receipt.\n" +
                    "Powered by Smart Boarding Management System",
                    FontFactory.getFont(FontFactory.HELVETICA, 9, GRAY)
            ));

            doc.close();
            return out.toByteArray();

        } catch (Exception e) {
            throw new RuntimeException("PDF generation failed", e);
        }
    }

    /* ---------- HELPERS ---------- */

    private void addRow(PdfPTable t, String l, String v, Font lf, Font vf) {
        t.addCell(cell(l, lf));
        t.addCell(cell(v, vf));
    }

    private void addBlock(PdfPTable t, String title, String name) {
        PdfPCell c = new PdfPCell();
        c.setPadding(10);
        c.setBorder(Rectangle.BOX);
        c.addElement(new Paragraph(title,
                FontFactory.getFont(FontFactory.HELVETICA_BOLD, 11)));
        c.addElement(new Paragraph(name,
                FontFactory.getFont(FontFactory.HELVETICA, 12)));
        t.addCell(c);
    }

    private void addMoney(PdfPTable t, String label, Object value) {
        t.addCell(cell(label,
                FontFactory.getFont(FontFactory.HELVETICA, 11)));
        PdfPCell c = cell("LKR " + value,
                FontFactory.getFont(FontFactory.HELVETICA, 11));
        c.setHorizontalAlignment(Element.ALIGN_RIGHT);
        t.addCell(c);
    }

    private PdfPCell cell(String text, Font f) {
        PdfPCell c = new PdfPCell(new Phrase(text, f));
        c.setPadding(8);
        c.setBorder(Rectangle.NO_BORDER);
        return c;
    }
}
