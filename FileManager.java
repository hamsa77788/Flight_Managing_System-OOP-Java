import java.io.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class FileManager {
    private static final String USERS_FILE = "users.txt";
    private static final String FLIGHTS_FILE = "flights.txt";
    private static final String BOOKINGS_FILE = "bookings.txt";
    private static final String PASSENGERS_FILE = "passengers.txt";
    private static final String SYSTEM_SETTINGS_FILE = "systemSettings.txt";


    private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

    // Save methods
    public static void saveUsersToFile(List<User> users) throws IOException {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(USERS_FILE))) {
            for (User user : users) {
                writer.write(String.format("%s,%s,%s,%s,%s,%s,%s,%s",
                        user.getUserId(), user.getUsername(), user.getPassword(), user.getName(),
                        user.getEmail(), user.getContactInfo(), user.isActive(), user.getClass().getSimpleName()));
                writer.newLine();
            }
        }
    }

    public static void saveFlightsToFile(List<Flight> flights) throws IOException {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(FLIGHTS_FILE))) {
            for (Flight flight : flights) {
                writer.write(String.format("%s,%s,%s,%s,%s,%s",
                        flight.getFlightNumber(), flight.getAirline(), flight.getOrigin(),
                        flight.getDestination(), flight.getDepartureTime().format(DATE_TIME_FORMATTER),
                        flight.getArrivalTime().format(DATE_TIME_FORMATTER)));
                writer.newLine();
            }
        }
    }

    public static void saveBookingsToFile(List<Booking> bookings) throws IOException {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(BOOKINGS_FILE))) {
            for (Booking booking : bookings) {
                writer.write(String.format("%s,%s,%s,%s",
                        booking.getBookingReference(), booking.getCustomer().getUserId(),
                        booking.getFlight().getFlightNumber(), booking.getStatus()));
                writer.newLine();
                for (Passenger passenger : booking.getPassengers()) {
                    writer.write(String.format("    %d,%s,%s,%s,%s",
                            passenger.getPassengerId(), passenger.getName(), passenger.getPassportNumber(),
                            passenger.getDateOfBirth().format(DATE_TIME_FORMATTER), passenger.getSpecialRequests()));
                    writer.newLine();
                }
            }
        }
    }

    public static void savePassengersToFile(List<Passenger> passengers) throws IOException {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(PASSENGERS_FILE))) {
            for (Passenger passenger : passengers) {
                writer.write(String.format("%d,%s,%s,%s,%s",
                        passenger.getPassengerId(), passenger.getName(), passenger.getPassportNumber(),
                        passenger.getDateOfBirth().format(DATE_TIME_FORMATTER), passenger.getSpecialRequests()));
                writer.newLine();
            }
        }
    }

    // Modified method to handle MaxBookingsPerUser only
    public static void saveSystemSettingsToFile(String settingName, String value) throws IOException {
        File file = new File(SYSTEM_SETTINGS_FILE);
        List<String> lines = new ArrayList<>();
        boolean found = false;

        if (file.exists()) {
            try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    if (line.startsWith(settingName + "=")) {
                        lines.add(settingName + "=" + value); // عدل القيمة
                        found = true;
                    } else {
                        lines.add(line); // احتفظ بأي إعدادات تانية
                    }
                }
            }
        }

        // لو المفتاح مش موجود، ضيفه
        if (!found) {
            lines.add(settingName + "=" + value);
        }

        // اكتب الملف من جديد
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(file))) {
            for (String line : lines) {
                writer.write(line);
                writer.newLine();
            }
        }
    }


    // Load methods
    public static List<User> loadUsersFromFile(BookingSystem bookingSystem) throws IOException {
        List<User> users = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(USERS_FILE))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts.length == 8) {
                    User user;
                    switch (parts[7]) {
                        case "Customer":
                            user = new Customer(parts[0], parts[1], parts[2], parts[3], parts[4], parts[5],
                                    parts[0], "Default Address", new ArrayList<>(), bookingSystem);
                            break;
                        case "Administrator":
                            user = new Administrator(parts[0], parts[1], parts[2], parts[3], parts[4], parts[5],
                                    parts[0], "Default Security Level");
                            break;
                        case "Agent":
                            user = new Agent(parts[0], parts[1], parts[2], parts[3], parts[4], parts[5],
                                    parts[0], "Default Department", 0.0, bookingSystem);
                            break;
                        default:
                            continue;
                    }
                    user.setActive(Boolean.parseBoolean(parts[6]));
                    users.add(user);
                }
            }
        }
        return users;
    }

    public static List<Flight> loadFlightsFromFile() throws IOException {
        List<Flight> flights = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(FLIGHTS_FILE))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts.length == 6) {
                    Flight flight = new Flight(parts[0], parts[1], parts[2], parts[3],
                            LocalDateTime.parse(parts[4], DATE_TIME_FORMATTER),
                            LocalDateTime.parse(parts[5], DATE_TIME_FORMATTER));
                    flights.add(flight);
                }
            }
        }
        return flights;
    }

    public static List<Booking> loadBookingsFromFile(BookingSystem bookingSystem) throws IOException {
        List<Booking> bookings = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(BOOKINGS_FILE))) {
            String line;
            Booking currentBooking = null;
            while ((line = reader.readLine()) != null) {
                if (!line.startsWith("    ")) {
                    String[] parts = line.split(",");
                    if (parts.length == 4) {
                        Customer customer = (Customer) bookingSystem.findUserById(parts[1]);
                        Flight flight = bookingSystem.getFlightByNumber(parts[2]);
                        if (customer != null && flight != null) {
                            currentBooking = new Booking(parts[0], customer, flight);
                            currentBooking.setStatus(parts[3]);
                            bookings.add(currentBooking);
                        }
                    }
                } else if (currentBooking != null) {
                    String[] passengerParts = line.trim().split(",");
                    if (passengerParts.length == 5) {
                        Passenger passenger = new Passenger(
                                Integer.parseInt(passengerParts[0]),
                                passengerParts[1],
                                passengerParts[2],
                                LocalDateTime.parse(passengerParts[3], DATE_TIME_FORMATTER),
                                passengerParts[4]
                        );
                        currentBooking.addPassenger(passenger);
                    }
                }
            }
        }
        return bookings;
    }

    public static List<Passenger> loadPassengersFromFile() throws IOException {
        List<Passenger> passengers = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(PASSENGERS_FILE))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts.length == 5) {
                    Passenger passenger = new Passenger(
                            Integer.parseInt(parts[0]),
                            parts[1],
                            parts[2],
                            LocalDateTime.parse(parts[3], DATE_TIME_FORMATTER),
                            parts[4]
                    );
                    passengers.add(passenger);
                }
            }
        }
        return passengers;
    }

    // Method to read system setting by key
    public static String getSystemSetting(String settingName) {
        File file = new File(SYSTEM_SETTINGS_FILE);
        if (!file.exists()) return null;
        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split("=", 2);
                if (parts.length == 2 && parts[0].trim().equals(settingName)) {
                    return parts[1].trim();
                }
            }
        } catch (IOException e) {
            System.out.println("Error reading system settings: " + e.getMessage());
        }
        return null;
    }







    public static int getSystemSettingAsInteger(String settingName, int defaultValue) {
        String value = getSystemSetting(settingName);
        if (value != null) {
            try {
                return Integer.parseInt(value.trim());
            } catch (NumberFormatException e) {
                System.out.println("Invalid number format for setting '" + settingName + "': " + value);
            }
        }
        return defaultValue;
    }

}