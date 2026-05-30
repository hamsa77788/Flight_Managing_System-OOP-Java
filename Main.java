import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.time.LocalDate;

public class Main {
    public static void main(String[] args) throws IOException {
        BookingSystem system = new BookingSystem();
        Scanner scanner = new Scanner(System.in);

        // ====== Create Demo Data ======
        List<User> users = new ArrayList<>();
        Customer customer = new Customer("C001", "ali99", "pass123", "Ali Hassan", "ali@gmail.com", "01111111111", "C001", "Cairo", new ArrayList<>(), system);
        Agent agent = new Agent("A001", "agent007", "secret", "Khaled Amin", "khaled@agent.com", "01555555555", "A001", "Sales", 1000.0, system);
        Administrator admin = new Administrator("AD001", "admin1", "adminpass", "Laila Mohamed", "laila@admin.com", "01333333333", "AD001", "High");
        users.add(customer);
        users.add(agent);
        users.add(admin);
        system.setUsers(users);

        // ====== Add Multiple Flights ======
        Flight flight1 = new Flight("FL001", "EgyptAir", "Cairo", "Paris", LocalDateTime.of(2025, 6, 10, 8, 0), LocalDateTime.of(2025, 6, 10, 12, 30));
        flight1.addSeatClass("Economy", 20, 3000);
        flight1.addSeatClass("Business", 5, 6000);

        Flight flight2 = new Flight("FL002", "Emirates", "Dubai", "London", LocalDateTime.of(2025, 6, 11, 9, 15), LocalDateTime.of(2025, 6, 11, 14, 45));
        flight2.addSeatClass("Economy", 30, 3500);
        flight2.addSeatClass("Business", 10, 7000);

        Flight flight3 = new Flight("FL003", "Qatar Airways", "Doha", "New York", LocalDateTime.of(2025, 6, 12, 2, 0), LocalDateTime.of(2025, 6, 12, 11, 30));
        flight3.addSeatClass("Economy", 25, 3200);
        flight3.addSeatClass("Business", 8, 6500);

        system.setFlights(List.of(flight1, flight2, flight3));



        // ==== Passengers ====
        List<Passenger> passengers = new ArrayList<>();
        passengers.add(new Passenger(1, "Ahmed Zaki", "P001", LocalDateTime.of(1985, 3, 15, 0, 0), "Wheelchair"));
        passengers.add(new Passenger(2, "Lamia Fouad", "P002", LocalDateTime.of(1992, 8, 20, 0, 0), "Vegetarian meal"));
        passengers.add(new Passenger(3, "Youssef Ali", "P003", LocalDateTime.of(2000, 1, 5, 0, 0), "Window seat"));
        passengers.add(new Passenger(4, "Nour El-Din", "P004", LocalDateTime.of(1998, 11, 11, 0, 0), ""));
        passengers.add(new Passenger(5, "Farah Ibrahim", "P005", LocalDateTime.of(1995, 6, 30, 0, 0), "Extra legroom"));

//  أضف هذا السطر بعد ما تنشئ الركاب
        system.setPassengers(passengers);


        // ====== Main Loop ======
        while (true) {
            System.out.println("\n=== Main Menu ===");
            System.out.println("1. Login as Customer");
            System.out.println("2. Login as Agent");
            System.out.println("3. Login as Administrator");
            System.out.println("4. Exit");
            System.out.print("Choose: ");

            int choice = getInt(scanner);


            if (choice == 1) {
                User u = login(users, scanner, "Customer");

                if (u instanceof Customer) handleCustomerMenu((Customer) u, system, scanner);

            } else if (choice == 2) {
                User u = login(users, scanner, "Agent");
                if (u instanceof Agent) handleAgentMenu((Agent) u, system, scanner);

            } else if (choice == 3) {
                User u = login(users, scanner, "Administrator");
                if (u instanceof Administrator) handleAdminMenu((Administrator) u, users, system, scanner);

            }  else if (choice == 4) {
                System.exit(0);  // خروج طبيعي بدون أي error
                break;
            }
        }
    }

    static User login(List<User> users, Scanner sc, String type) {
        System.out.print("Username: ");
        String uname = sc.nextLine();
        System.out.print("Password: ");
        String pass = sc.nextLine();
        for (User u : users) {
            if (u.getUsername().equals(uname) && u.getClass().getSimpleName().equals(type) && u.login(uname, pass)) {
                System.out.println("✔ Login successful");
                return u;
            }
        }
        System.out.println("✘ Invalid credentials");
        return null;
    }

    static void handleCustomerMenu(Customer cust, BookingSystem system, Scanner sc) {
        while (true) {
            System.out.println("\n--- Customer Menu ---");
            System.out.println("1. View Flights");
            System.out.println("2. Create Booking");
            System.out.println("3. View My Bookings");
            System.out.println("4. Cancel Booking");
            System.out.println("5. Logout");
            System.out.print("Choose: ");
            int ch = getInt(sc);

            if (ch == 1) {
                // عرض جميع الرحلات المتاحة
                System.out.println("\nAvailable Flights:");
                for (Flight f : system.getFlights()) {
                    printFlight(f);
                }

            } else if (ch == 2) {
                try {
                    System.out.print("Flight No: ");
                    String fno = sc.nextLine().trim();
                    Flight f = system.getFlightByNumber(fno);
                    if (f == null) {
                        System.out.println("Invalid flight number");
                        continue;
                    }

                    // تحقق من فئة المقعد
                    String cls = "";
                    while (true) {
                        System.out.print("Class (Economy/Business): ");
                        cls = sc.nextLine().trim();
                        if (cls.equalsIgnoreCase("Economy") || cls.equalsIgnoreCase("Business")) {
                            break;  // فئة المقعد صحيحة
                        } else {
                            System.out.println("Invalid class! Please choose either 'Economy' or 'Business'.");
                        }
                    }

                    // التحقق من المقاعد المتاحة
                    SeatClass selectedClass = null;
                    for (SeatClass s : f.getSeatClasses()) {
                        if (s.getClassName().equalsIgnoreCase(cls)) {
                            selectedClass = s;
                            break;
                        }
                    }

                    if (selectedClass == null) {
                        System.out.println("❌ Class not found in this flight");
                        continue;
                    }

                    int availableSeats = selectedClass.getAvailableSeats();
                    String maxBookingsPerUser = FileManager.getSystemSetting("MaxBookingsPerUser");
                    int maxBookings = maxBookingsPerUser != null ? Integer.parseInt(maxBookingsPerUser) : Integer.MAX_VALUE;

                    System.out.println("Max bookings per user: " + maxBookings);
                    int q = 0;
                    while (q <= 0) {
                        System.out.print("No. of passengers: ");
                        q = getInt(sc);
                        if (q <= 0) {
                            System.out.println("❌ The number of passengers must be greater than zero.");
                        }
                    }

                    if (q > maxBookings) {
                        System.out.println("❌ You cannot book more than the maximum allowed bookings: " + maxBookings);
                        continue;
                    }

                    if (q > availableSeats) {
                        System.out.println("❌ Not enough seats available in " + cls + " class. Max available: " + availableSeats);
                        continue;
                    }

                    List<Passenger> list = new ArrayList<>();
                    for (int i = 0; i < q; i++) {
                        System.out.println("Enter details for passenger #" + (i + 1));

                        String name = "";
                        while (name.isEmpty()) {
                            System.out.print("Name: ");
                            name = sc.nextLine().trim();
                            if (name.isEmpty()) {
                                System.out.println("❌ Name cannot be empty.");
                            }
                        }

                        String pass = "";
                        while (pass.isEmpty()) {
                            System.out.print("Passport: ");
                            pass = sc.nextLine().trim();
                            if (pass.isEmpty()) {
                                System.out.println("❌ Passport cannot be empty.");
                            }
                        }

                        LocalDateTime dobDate = null;
                        while (dobDate == null) {
                            System.out.print("DOB (yyyy-MM-dd): ");
                            String dob = sc.nextLine().trim();

                            // تحقق من أن التاريخ مش فاضي
                            if (dob.isEmpty()) {
                                System.out.println("❌ Date of birth cannot be empty.");
                                continue;
                            }

                            try {
                                dobDate = LocalDate.parse(dob).atStartOfDay(); // تصحيح هنا
                            } catch (DateTimeParseException e) {
                                System.out.println("❌ Invalid date format. Please use yyyy-MM-dd.");
                            }
                        }

                        // إضافة السؤال عن الـ Special Request
                        String specialRequest = "";
                        while (specialRequest.isEmpty()) {
                            System.out.print("Special Request (Wheel chair , vegan meal ,window seat, others or none): ");
                            specialRequest = sc.nextLine().trim();
                            if (specialRequest.isEmpty()) {
                                System.out.println("❌ Special request cannot be empty.");
                            }
                        }

                        list.add(new Passenger(i + 1, name, pass, dobDate, specialRequest));
                    }

                    // حفظ الركاب في ملف الـ passengers.txt
                    FileManager.savePassengersToFile(list);  // إضافة هذه السطر لحفظ الركاب في الفايل

                    // محاولة الحجز
                    try {
                        Booking booking = cust.createBooking(f, cls, list);
                        System.out.println("Booking Reference: " + booking.getBookingReference());
                        System.out.println("Total: " + booking.calculateTotalPrice());
                        System.out.println("Choose payment method: 1. Card  2. Bank");
                        int pm = getInt(sc);

                        PaymentMethod method = (pm == 1) ?
                                new CreditCardPayment("1234567812345678", "Ali", "12/26", "123") :
                                new BankTranseferPayment("1234567890", "CIB", "SWIFT001");

                        Payment p = new Payment("P" + System.currentTimeMillis(), booking, "EGP", method);
                        system.processPayment(booking, p);
                        booking.printItinerary();
                    } catch (Exception e) {
                        System.out.println("✘ Error occurred during booking: " + e.getMessage());
                        e.printStackTrace();
                    }

                } catch (Exception e) {
                    System.out.println("✘ Error occurred during booking process: " + e.getMessage());
                    e.printStackTrace();
                }
            }

            else if (ch == 3) {
                List<Booking> list = cust.viewBookings();
                if (list.isEmpty()) System.out.println("No bookings found.");
                else list.forEach(Booking::printItinerary);

            } else if (ch == 4) {
                //  التحقق أولاً من وجود حجوزات
                List<Booking> bookings = cust.viewBookings();
                if (bookings.isEmpty()) {
                    System.out.println("No bookings found. You haven't made any bookings yet.");
                    return;  // الخروج من هذا الاختيار
                }

                System.out.print("Enter Booking Ref: ");
                String ref = sc.nextLine();

                // إلغاء الحجز
                boolean canceled = cust.cancelBooking(ref);
                if (canceled) {
                    System.out.println("✔ Booking cancelled successfully.");
                } else {
                    System.out.println("✘ Could not cancel booking. Make sure the reference is correct.");
                }

            } else if (ch == 5) {
                System.out.println("Logging out...");
                break;

            } else {
                System.out.println("Invalid choice.");
            }
        }
    }

    static void handleAgentMenu(Agent agent, BookingSystem system, Scanner sc) throws IOException {
        while (true) {
            System.out.println("\n--- Agent Menu ---");
            System.out.println("1. Manage Flights");
            System.out.println("2. Create Booking for Customer");
            System.out.println("3. Modify Booking");
            System.out.println("4. Generate Reports");
            System.out.println("5. Logout");
            System.out.print("Choose: ");
            int ch = getInt(sc); sc.nextLine();

            if (ch == 1) {
                System.out.print("Flight No: ");
                String fno = sc.nextLine();
                System.out.print("Airline: ");
                String airline = sc.nextLine();
                System.out.print("Origin: ");
                String origin = sc.nextLine();
                System.out.print("Destination: ");
                String destination = sc.nextLine();
                System.out.print("Departure (yyyy-MM-ddTHH:mm): ");
                String depStr = sc.nextLine();
                System.out.print("Arrival (yyyy-MM-ddTHH:mm): ");
                String arrStr = sc.nextLine();
                System.out.print("Action (add/update/remove): ");
                String action = sc.nextLine();

                try {
                    LocalDateTime dep = LocalDateTime.parse(depStr);
                    LocalDateTime arr = LocalDateTime.parse(arrStr);
                    Flight flight = new Flight(fno, airline, origin, destination, dep, arr);
                    agent.manageFlights(flight, action);
                } catch (DateTimeParseException e) {
                    System.out.println("Invalid date format.");
                }

            } else if (ch == 2) {
                System.out.print("Customer username: ");
                String username = sc.nextLine();
                User u = system.findUserByUsername(username);
                if (u instanceof Customer) {
                    Customer customer = (Customer) u;
                    System.out.print("Flight No: ");
                    String fno = sc.nextLine();
                    Flight flight = system.getFlightByNumber(fno);
                    if (flight == null) {
                        System.out.println("Flight not found.");
                        continue;
                    }

                    // تحقق من فئة المقعد
                    String seatClass = "";
                    while (true) {
                        System.out.print("Seat class (Economy or Business): ");
                        seatClass = sc.nextLine().trim();
                        if (seatClass.equalsIgnoreCase("Economy") || seatClass.equalsIgnoreCase("Business")) {
                            break;
                        } else {
                            System.out.println("❌ Invalid seat class. Please choose either 'Economy' or 'Business'.");
                        }
                    }

                    // التحقق من المقاعد المتاحة
                    SeatClass selectedClass = null;
                    for (SeatClass s : flight.getSeatClasses()) {
                        if (s.getClassName().equalsIgnoreCase(seatClass)) {
                            selectedClass = s;
                            break;
                        }
                    }

                    if (selectedClass == null) {
                        System.out.println("❌ Class not found in this flight.");
                        continue;
                    }

                    int availableSeats = selectedClass.getAvailableSeats();
                    String maxBookingsPerUser = FileManager.getSystemSetting("MaxBookingsPerUser");
                    int maxBookings = maxBookingsPerUser != null ? Integer.parseInt(maxBookingsPerUser) : Integer.MAX_VALUE;

                    System.out.println("Max bookings per user: " + maxBookings);
                    System.out.print("Number of passengers: ");
                    int num = getInt(sc);

                    // تحقق من الحد الأقصى للركاب
                    if (num > maxBookings) {
                        System.out.println("❌ You cannot book more than the maximum allowed bookings: " + maxBookings);
                        continue;
                    }

                    if (num > availableSeats) {
                        System.out.println("❌ Not enough seats available in " + seatClass + " class. Max available: " + availableSeats);
                        continue;
                    }

                    List<Passenger> passengers = new ArrayList<>();
                    for (int i = 0; i < num; i++) {
                        System.out.println("Enter details for passenger #" + (i + 1));

                        // إضافة السؤال عن الـ Special Request
                        String specialRequest = "";
                        while (specialRequest.isEmpty()) {
                            System.out.print("Special Request (if any): ");
                            specialRequest = sc.nextLine().trim();
                            if (specialRequest.isEmpty()) {
                                System.out.println("❌ Special request cannot be empty.");
                            }
                        }

                        System.out.print("Name: ");
                        String name = sc.nextLine();
                        System.out.print("Passport: ");
                        String passport = sc.nextLine();
                        System.out.print("DOB yyyy-MM-dd: ");
                        String dob = sc.nextLine();
                        LocalDateTime dobDate = LocalDateTime.parse(dob + "T00:00");

                        // إضافة الراكب مع الـ Special Request
                        passengers.add(new Passenger(i + 1, name, passport, dobDate, specialRequest));
                    }

                    // حفظ الركاب في ملف الـ passengers.txt
                    FileManager.savePassengersToFile(passengers);  // إضافة هذه السطر لحفظ الركاب في الفايل

                    try {
                        Booking booking = agent.createBookingForCustomer(customer, flight, seatClass, passengers);
                        System.out.println("✅ Booking created successfully. Reference: " + booking.getBookingReference());
                        System.out.println("Total: " + booking.calculateTotalPrice());
                        System.out.println("Choose payment method: 1. Card  2. Bank");
                        int pm = getInt(sc);

                        PaymentMethod method = (pm == 1) ? new CreditCardPayment("1234567812345678", "Ali", "12/26", "123")
                                : new BankTranseferPayment("1234567890", "CIB", "SWIFT001");
                        Payment payment = new Payment("P" + System.currentTimeMillis(), booking, "EGP", method);
                        system.processPayment(booking, payment);
                        booking.printItinerary();
                    } catch (Exception e) {
                        System.out.println("❌ " + e.getMessage());
                    }
                } else {
                    System.out.println("❌ Customer not found.");
                }
            }
            else if (ch == 3) {
                System.out.print("Booking Reference: ");
                String ref = sc.nextLine();

                Booking booking = system.findBookingByReference(ref);
                if (booking == null) {
                    System.out.println("❌ Booking not found.");
                    return;
                }

                if (booking.getStatus().equalsIgnoreCase("CANCELLED")) {
                    System.out.println("❌ Cannot modify a cancelled booking.");
                    return;
                }

                if (!booking.getCustomer().isActive()) {
                    System.out.println("❌ Customer account is not active, cannot modify booking.");
                    return;
                }

                System.out.print("Number of new passengers: ");
                int num = getInt(sc); sc.nextLine();

                SeatClass seatClass = booking.getFlight().getSeatClasses().stream()
                        .filter(s -> s.getClassName().equalsIgnoreCase(booking.getSeatSelections().get(0).getClassName()))
                        .findFirst()
                        .orElse(null);

                if (seatClass == null || num > seatClass.getAvailableSeats()) {
                    System.out.println("❌ Not enough available seats in the selected class.");
                    return;
                }

                List<Passenger> passengers = new ArrayList<>();
                for (int i = 0; i < num; i++) {
                    System.out.print("Name: ");
                    String name = sc.nextLine();
                    System.out.print("Passport: ");
                    String passport = sc.nextLine();
                    System.out.print("DOB yyyy-MM-dd: ");
                    String dob = sc.nextLine();
                    try {
                        LocalDateTime dobDate = LocalDateTime.parse(dob + "T00:00");
                        passengers.add(new Passenger(i + 1, name, passport, dobDate, ""));
                    } catch (Exception e) {
                        System.out.println("❌ Invalid date format for passenger " + (i + 1) + ". Skipping this passenger.");
                    }
                }

                if (passengers.isEmpty()) {
                    System.out.println("❌ No valid passengers added. Aborting modification.");
                    return;
                }

                try {
                    agent.modifyBooking(ref, passengers);
                    System.out.println("✅ Booking modified successfully.");
                } catch (Exception e) {
                    System.out.println("❌ Error modifying booking: " + e.getMessage());
                }

            } else if (ch == 4) {
                agent.generateReports();

            } else if (ch == 5) {
                System.out.println("Logging out...");
                break;

            } else {
                System.out.println("Invalid choice.");
            }
        }
    }

    static void handleAdminMenu(Administrator admin, List<User> users, BookingSystem system, Scanner sc) {
        while (true) {
            System.out.println("\n--- Admin Menu ---");
            System.out.println("1. View System logs (Users logged in system)");
            System.out.println("2. Toggle User Access");
            System.out.println("3. Create New User");
            System.out.println("4. Modify System Settings");
            System.out.println("5. Logout");
            System.out.print("Choose: ");
            int ch = getInt(sc);


            if (ch == 1) {
                System.out.println("=== Registered Users ===");
                for (User u : users) {
                    System.out.println(u.getUsername() + " (" + u.getClass().getSimpleName() + ") - Active: " + u.isActive());
                }

            } else if (ch == 2) {
                System.out.print("Enter username to toggle access: ");
                String uname = sc.nextLine();
                boolean found = false;
                for (User u : users) {
                    if (u.getUsername().equals(uname)) {
                        admin.manageUserAccess(u);
                        found = true;
                        break;
                    }
                }
                if (!found) System.out.println("User not found.");
                try {
                    FileManager.saveUsersToFile(users);
                } catch (IOException e) {
                    System.out.println("Error saving users: " + e.getMessage());
                }

            } else if (ch == 3) {
                System.out.print("Enter user type (Customer/Agent): ");
                String type = sc.nextLine().trim();

                System.out.print("Username: ");
                String username = sc.nextLine();

                // تحقق من الباسورد
                String password = "";
                while (true) {
                    System.out.print("Password: ");
                    password = sc.nextLine();
                    if (password == null || password.trim().isEmpty()) {
                        System.out.println("❌ Password cannot be empty. Please try again.");
                    } else if (password.length() < 6) {
                        System.out.println("❌ Password too short. Minimum 6 characters required.");
                    } else {
                        break; // باسورد صالح
                    }
                }

                System.out.print("Name: ");
                String name = sc.nextLine();
                System.out.print("Email: ");
                String email = sc.nextLine();
                System.out.print("Contact Info: ");
                String contact = sc.nextLine();
                System.out.print("User ID: ");
                String userId = sc.nextLine();

                User newUser = null;
                if (type.equalsIgnoreCase("Customer")) {
                    newUser = new Customer(userId, username, password, name, email, contact,
                            userId, "Default Address", new ArrayList<>(), system);
                } else if (type.equalsIgnoreCase("Agent")) {
                    newUser = new Agent(userId, username, password, name, email, contact,
                            userId, "DefaultDept", 0.0, system);
                } else {
                    System.out.println("Invalid user type.");
                    continue;
                }

                admin.createUser(newUser, users);
                try {
                    FileManager.saveUsersToFile(users);
                } catch (IOException e) {
                    System.out.println("Error saving users: " + e.getMessage());
                }
            }
            else if (ch == 4) {
                // Modify Max Bookings Per User setting
                Integer currentValue = FileManager.getSystemSettingAsInteger("MaxBookingsPerUser",5);
                System.out.println("\n*** Modify System Setting: Max Bookings Per User ***");

                if (currentValue != null) {
                    System.out.println("Current maximum bookings per user: " + currentValue);
                } else {
                    System.out.println("Current maximum bookings per user: Not Set");
                }

                System.out.print("Enter new maximum number of bookings per user: ");
                String newValue = sc.nextLine();

                // Validate the input to ensure it's a valid number and not empty
                if (newValue != null && !newValue.trim().isEmpty()) {
                    try {
                        int newMaxBookings = Integer.parseInt(newValue);
                        if (newMaxBookings > 0) {
                            // Valid input, proceed to modify the setting
                            admin.modifySystemSettings("MaxBookingsPerUser", String.valueOf(newMaxBookings));
                            System.out.println("Setting updated successfully.");
                        } else {
                            System.out.println("Error: The number of bookings per user must be a positive number.");
                        }
                    } catch (NumberFormatException e) {
                        System.out.println("Error: Please enter a valid number for max bookings per user.");
                    }
                } else {
                    System.out.println("Error: Input cannot be empty.");
                }
            } else if (ch == 5) {
                System.out.println("Logging out...");
                break;
            }

        }
    }

    static int getInt(Scanner sc) {
        int choice;
        while (true) {
            try {
                choice = Integer.parseInt(sc.nextLine());
                break;
            } catch (NumberFormatException e) {
                System.out.print("Please enter a valid number: ");
            }
        }
        return choice;
    }

    static void printFlight(Flight flight) {
        System.out.println("Flight No: " + flight.getFlightNumber());
        System.out.println("From: " + flight.getOrigin() + " To: " + flight.getDestination());
        System.out.println("Departure: " + flight.getDepartureTime() + " Arrival: " + flight.getArrivalTime());
        for (SeatClass sc : flight.getSeatClasses()) {
            System.out.println("Class: " + sc.getClassName() + " Available Seats: " + sc.getAvailableSeats() + " Price: " + sc.getPricePerSeat());
        }
    }
}