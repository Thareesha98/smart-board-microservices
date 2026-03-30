package com.sbms.chatbot.service;

import com.sbms.chatbot.client.AppointmentClient;
import com.sbms.chatbot.client.BillingClient;
import com.sbms.chatbot.client.MaintenanceClient;
import com.sbms.chatbot.client.UserClient;
import com.sbms.chatbot.dto.ChatResponse;
import com.sbms.chatbot.dto.DynamicData;
import com.sbms.chatbot.dto.IntentResponse;
import com.sbms.chatbot.dto.client.UserMinimalDTO;
import com.sbms.chatbot.rule.IntentOverrideMatrix;
import com.sbms.chatbot.service.ContextManager.ChatContext;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ChatbotService {

    private final IntentClient intentClient;
    private final ResponseGenerator responseGenerator;
    private final ContextManager contextManager;
    private final UnknownIntentLogger unknownIntentLogger;
    private final IntentOverrideMatrix overrideMatrix;

    // 🔥 MICROSERVICE CLIENTS
    private final UserClient userClient;
    private final BillingClient billingClient;
    private final MaintenanceClient maintenanceClient;
    private final AppointmentClient appointmentClient;

    public ChatResponse chat(String message, String sessionId, Long userId) {

        // -----------------------------
        // CONTEXT
        // -----------------------------
        ChatContext previousContext = contextManager.getContext(sessionId);

        // -----------------------------
        // INTENT DETECTION
        // -----------------------------
        IntentResponse intentResponse = intentClient.predictIntent(message);

        String intent = intentResponse.getIntent();
        double confidence = intentResponse.getConfidence();
        String explanation;

        // -----------------------------
        // CONTEXT + OVERRIDE LOGIC
        // -----------------------------
        if (previousContext != null && confidence < 0.75) {

            Optional<String> override =
                    overrideMatrix.resolveOverride(
                            previousContext.lastIntent(),
                            message
                    );

            if (override.isPresent()) {
                intent = override.get();
                explanation = "I connected this with your previous question to better help you 😊";
            } else {
                explanation = confidence >= 0.5
                        ? "I think I understood your question 👍"
                        : "Hmm... I’m not fully sure, but I’ll still try to help.";
            }

        } else {
            explanation = confidence >= 0.75
                    ? "I clearly understand your question ✅"
                    : "I think this is what you’re asking 👇";
        }

        // -----------------------------
        // UNKNOWN INTENT LOGGING
        // -----------------------------
        if ("UNKNOWN".equals(intent)) {
            unknownIntentLogger.log(sessionId, message, confidence);
        }

        // -----------------------------
        // FETCH USER NAME (SAFE)
        // -----------------------------
        String userName = "there";

        try {
            UserMinimalDTO user = userClient.getById(userId);

            if (user != null && user.getFullName() != null) {
                userName = user.getFullName();
            }

        } catch (Exception e) {
            // silent fallback
        }

        // -----------------------------
        // PREPARE DYNAMIC DATA
        // -----------------------------
        DynamicData data = DynamicData.builder()
                .userName(userName)
                .build();

        // -----------------------------
        // FETCH DATA BASED ON INTENT
        // -----------------------------
        try {

            switch (intent) {

                // 💳 BILLING
                case "PAYMENT_HISTORY", "BILL_CALCULATION" -> {
                    data.setBills(billingClient.getStudentBills(userId));
                }

                // 🛠️ MAINTENANCE
                case "MAINTENANCE" -> {
                    data.setMaintenanceList(
                            maintenanceClient.getMyMaintenance(userId)
                    );
                }

                // 🗓️ APPOINTMENTS
                case "APPOINTMENT_HELP" -> {
                    data.setAppointments(
                            appointmentClient.getStudentAppointments(userId)
                    );
                }
            }

        } catch (Exception e) {
            // IMPORTANT: never break chatbot
        }

        // -----------------------------
        // GENERATE RESPONSE
        // -----------------------------
        String reply = responseGenerator.generateReply(intent, data);

        // -----------------------------
        // SUGGESTIONS
        // -----------------------------
        List<String> suggestions = responseGenerator.generateSuggestions(intent);

        // -----------------------------
        // UPDATE CONTEXT
        // -----------------------------
        contextManager.updateContext(sessionId, intent);

        // -----------------------------
        // RETURN FINAL RESPONSE
        // -----------------------------
        return new ChatResponse(
                intent,
                reply,
                confidence,
                explanation,
                suggestions
        );
    }
}