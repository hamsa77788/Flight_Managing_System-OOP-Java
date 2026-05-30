import javax.swing.*;
import java.awt.*;

public class TicketGUI extends JFrame {

    public TicketGUI(Booking booking) {
        setTitle("✈ Flight Ticket - " + booking.getBookingReference());
        setSize(550, 600);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);

        JTextPane ticketPane = new JTextPane();
        ticketPane.setEditable(false);
        ticketPane.setContentType("text/html"); // HTML formatting

        // تحديد لون الخلفية حسب نوع الكرسي
        String classType = booking.getSeatSelections().get(0).getClassName().toUpperCase();
        String bgColor;
        switch (classType) {
            case "ECONOMY":
                bgColor = "#e6ffe6"; // أخضر فاتح
                break;
            case "BUSINESS":
                bgColor = "#e6f0ff"; // أزرق فاتح
                break;
            case "INTERNATIONAL":
                bgColor = "#fff7e6"; // بيج فاتح
                break;
            default:
                bgColor = "#f2f2f2";
        }

        // بناء محتوى التذكرة باستخدام HTML
        StringBuilder html = new StringBuilder();
        html.append("<html><body style='background-color:").append(bgColor)
                .append("; font-family:Segoe UI, sans-serif; padding:20px;'>");

        html.append("<h2 style='text-align:center;'>🎫 Flight Ticket</h2><hr>");

        html.append("<p><b>Booking Reference:</b> ").append(booking.getBookingReference()).append("</p>");
        html.append("<p><b>Customer Name:</b> ").append(booking.getCustomer().getName()).append("</p>");

        html.append("<p><b>Flight Number:</b> ").append(booking.getFlight().getFlightNumber()).append("</p>");
        html.append("<p><b>From:</b> ").append(booking.getFlight().getOrigin())
                .append(" → <b>To:</b> ").append(booking.getFlight().getDestination()).append("</p>");
        html.append("<p><b>Departure:</b> ").append(booking.getFlight().getDepartureTime()).append("</p>");
        html.append("<p><b>Arrival:</b> ").append(booking.getFlight().getArrivalTime()).append("</p>");
        html.append("<p><b>Seat Class:</b> ").append(classType).append("</p>");

        html.append("<p><b>Passengers:</b></p><ul>");
        for (Passenger p : booking.getPassengers()) {
            html.append("<li><b>").append(p.getName()).append("</b> (Passport: ").append(p.getPassportNumber()).append(")");
            if (!p.getSpecialRequests().equalsIgnoreCase("none")) {
                html.append("<br><i>Special Request:</i> ").append(p.getSpecialRequests());
            }
            html.append("</li>");
        }
        html.append("</ul>");

        html.append("<p><b>Total Price:</b> ").append(booking.calculateTotalPrice()).append(" EGP</p>");
        html.append("<p><b>Status:</b> ").append(booking.getStatus()).append("</p>");

        html.append("<hr><div style='text-align:center; color:#888;'>Thank you for choosing our airline ✈</div>");

        html.append("</body></html>");

        ticketPane.setText(html.toString());
        ticketPane.setCaretPosition(0);

        JScrollPane scrollPane = new JScrollPane(ticketPane);
        add(scrollPane);
    }

    public static void showTicket(Booking booking) {
        SwingUtilities.invokeLater(() -> new TicketGUI(booking).setVisible(true));
    }
}
