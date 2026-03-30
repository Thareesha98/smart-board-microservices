package com.sbms.chatbot.service;

import org.springframework.stereotype.Component;

import com.sbms.chatbot.dto.DynamicData;
import com.sbms.chatbot.dto.client.MaintenanceResponseDTO;
import com.sbms.chatbot.dto.client.MonthlyBillResponseDTO;

import java.util.List;

@Component
public class ResponseGenerator {

    public String generateReply(String intent, DynamicData data) {

    	 String name = data.getUserName() != null ? data.getUserName() : "there";
        return switch (intent) {
        
        case "PAYMENT_HISTORY", "BILL_CALCULATION" -> {
            List<MonthlyBillResponseDTO> bills = data.getBills();
            
            

            if (bills != null && !bills.isEmpty()) {
                
                bills.sort((a, b) -> b.getMonth().compareTo(a.getMonth()));
                MonthlyBillResponseDTO latest = bills.get(0);

                yield String.format(
                    "Hi %s 👋,\n\nHere’s a quick summary of your latest bill:\n" +
                    "🏠 Boarding: %s\n" +
                    "💡 Electricity: Rs. %.2f\n" +
                    "🚿 Water: Rs. %.2f\n" +
                    "💰 Total: Rs. %.2f\n\n" +
                    "📅 Due Date: %s (%d days remaining)\n" +
                    "⚠️ Status: %s\n\n" +
                    "Please make sure to pay on time to avoid penalties 😊",
                    name,
                    latest.getBoardingTitle(),
                    latest.getElectricityFee(),
                    latest.getWaterFee(),
                    latest.getTotalAmount(),
                    latest.getDueDate(),
                    latest.getDueInDays(),
                    latest.getDueStatus()
                );
            }

            yield "Hi " + name + ", I couldn’t find any billing information right now.";
        }
        
        
        
        
        
        
        case "MAINTENANCE" -> {
            List<MaintenanceResponseDTO> list = data.getMaintenanceList();

            if (list != null && !list.isEmpty()) {
                MaintenanceResponseDTO m = list.get(0);

                yield String.format(
                    "Hi %s 🛠️,\n\nYou currently have %d maintenance request(s).\n\n" +
                    "📌 Latest Issue: %s\n" +
                    "🏠 Boarding: %s\n" +
                    "🏠 Address: %s\n" +
                    "⚡ Urgency: %s\n" +
                    "📊 Status: %s\n\n" +
                    "👨‍🔧 Assigned Technician: %s\n" +
                    " Description: %s\n\n" +
                    "We recommend following up if it's taking too long.",
                    name,
                    list.size(),
                    m.getTitle(),
                    m.getBoardingAddress(),
                    m.getBoardingTitle(),
                    m.getMaintenanceUrgency(),
                    m.getStatus(),
                    m.getTechnicianName(),
                    m.getDescription()
                );
            }

            yield "Hi " + name + ", you currently have no active maintenance requests 🎉";
        }
        
        
        
        
        
        case "APPOINTMENT_HELP" -> {
        	

            var list = data.getAppointments();

            if (list != null && !list.isEmpty()) {

                // Sort latest
                list.sort((a, b) ->
                    b.getCreatedAt().compareTo(a.getCreatedAt())
                );

                var a = list.get(0);

                yield String.format(
                    "Hi %s 🗓️\n\n" +
                    "Here’s your latest appointment:\n\n" +
                    "🏠 Boarding: %s\n" +
                    "📍 Address: %s\n" +
                    "👤 Owner: %s (%s)\n\n" +
                    "📅 Requested Time: %s → %s\n" +
                    "📅 Owner Scheduled: %s → %s\n\n" +
                    "📊 Status: %s\n\n" +
                    "%s",
                    data.getUserName(),
                    safe(a.getBoardingTitle()),
                    safe(a.getBoardingAddress()),
                    safe(a.getOwnerName()),
                    safe(a.getOwnerContact()),
                    safeTime(a.getRequestedStartTime()),
                    safeTime(a.getRequestedEndTime()),
                    safeTime(a.getOwnerStartTime()),
                    safeTime(a.getOwnerEndTime()),
                    a.getStatus(),
                    getStatusMessage(a.getStatus().toString())
                );
            }

            yield "Hi " + data.getUserName() +
                    " 😊\n\nYou don’t have any appointments yet.\n" +
                    "You can easily book a visit from the Appointments section.";
        }
        
        
        
        
        
        
        case "PAYMENT_HELP" ->
        "Hi " + name + " 💳\n\n" +
        "You can easily pay your boarding and utility bills from the *Payments* section in your dashboard.\n\n" +
        "⚡ It’s quick, secure, and keeps your records updated.\n" +
        "Let me know if you need help with anything else! 😊";


    case "PAYMENT_FAILED" ->
        "Hi " + name + " ⚠️\n\n" +
        "It looks like your payment didn’t go through.\n\n" +
        "💡 If money was deducted, don’t worry — refunds usually take a few days.\n" +
        "📄 You can check the full details in your *Payment History*.\n\n" +
        "If the issue continues, I recommend contacting support.";


    case "LATE_PAYMENT_RULES" ->
        "Hi " + name + " ⏰\n\n" +
        "Late payments may result in penalties depending on your boarding rules.\n\n" +
        "📅 Always try to pay before the due date shown in your dashboard.\n" +
        "This helps you avoid extra charges and keeps everything smooth 👍";


    case "REGISTRATION_PROCESS" ->
        "Hi " + name + " 📝\n\n" +
        "Getting a boarding is simple!\n\n" +
        "1️⃣ Choose your preferred boarding\n" +
        "2️⃣ Submit a registration request\n" +
        "3️⃣ Wait for owner approval\n\n" +
        "You can track everything directly from your dashboard 😊";


    case "REGISTRATION_STATUS" ->
        "Hi " + name + " 📊\n\n" +
        "You can check your boarding registration status anytime from your dashboard.\n\n" +
        "📌 It will show whether your request is:\n" +
        "• Pending ⏳\n" +
        "• Approved ✅\n" +
        "• Rejected ❌\n\n" +
        "Let me know if you need help understanding it!";


    case "REGISTRATION_REJECTION" ->
        "Hi " + name + " ❌\n\n" +
        "Your registration request was not approved.\n\n" +
        "📌 Don’t worry — you can:\n" +
        "• Review the reason for rejection\n" +
        "• Fix any issues\n" +
        "• Apply again anytime\n\n" +
        "I can guide you if you want 👍";


    case "UTILITIES_HELP" ->
        "Hi " + name + " ⚡🚿\n\n" +
        "Your utility charges are calculated based on actual usage.\n\n" +
        "💡 Electricity → based on units consumed\n" +
        "🚿 Water → based on monthly usage\n\n" +
        "These are added to your monthly bill automatically.\n" +
        "Let me know if you want a breakdown!";


    case "REPORT_ISSUES_HELP" ->
        "Hi " + name + " 🚨\n\n" +
        "If you’re facing any issues with a boarding or owner, you can report it easily.\n\n" +
        "📌 Go to the *Reports* section and submit your concern.\n" +
        "👨‍💼 Our admin team will review and take action.\n\n" +
        "Your safety and comfort always come first 👍";


    default ->
        "Hi " + name + " 😊\n\n" +
        "I’m not fully sure I understood that.\n\n" +
        "Could you try asking in a different way?\n" +
        "I’ll do my best to help you 💡";
        };
    }

    // -----------------------------
    // FOLLOW-UP SUGGESTIONS (NEW)
    // -----------------------------
    public List<String> generateSuggestions(String intent) {

        return switch (intent) {

            case "PAYMENT_HELP" -> List.of(
                    "What happens if I pay late?",
                    "Where can I see my payment history?"
            );

            case "PAYMENT_HISTORY" -> List.of(
                    "How can I download a receipt?",
                    "What if a payment is missing?"
            );

            case "PAYMENT_FAILED" -> List.of(
                    "Money was deducted, what should I do?",
                    "How long does refund take?"
            );

            case "REGISTRATION_PROCESS" -> List.of(
                    "How long does approval take?",
                    "How can I check my registration status?"
            );

            case "REGISTRATION_STATUS" -> List.of(
                    "Why is my registration pending?",
                    "What if my registration is rejected?"
            );

            case "MAINTENANCE" -> List.of(
                    "How can I check maintenance status?",
                    "What if the owner does not respond?"
            );

            case "LATE_PAYMENT_RULES" -> List.of(
                    "Is there a grace period?",
                    "Can penalties be removed?"
            );

            case "UTILITIES_HELP" -> List.of(
                    "Why is my electricity bill high?",
                    "How are water charges calculated?"
            );

            default -> List.of(); // No suggestions
        };
    }
    
 // -----------------------------
 // SAFE NULL HANDLING
 // -----------------------------
 private String safe(String value) {
     return value != null ? value : "N/A";
 }

 // -----------------------------
 // SAFE TIME FORMAT
 // -----------------------------
 private String safeTime(java.time.LocalDateTime time) {
     return time != null ? time.toString() : "Not scheduled yet";
 }

 // -----------------------------
 // STATUS MESSAGE (SMART UX)
 // -----------------------------
 private String getStatusMessage(String status) {

     if (status == null) return "";

     return switch (status) {
         case "PENDING" -> "⏳ Your request is waiting for owner approval.";
         case "APPROVED" -> "✅ Your appointment has been confirmed!";
         case "REJECTED" -> "❌ Unfortunately, the owner declined this appointment.";
         case "COMPLETED" -> "🎉 This appointment has already been completed.";
         case "CANCELLED" -> "⚠️ This appointment was cancelled.";
         default -> "";
     };
 }
}
