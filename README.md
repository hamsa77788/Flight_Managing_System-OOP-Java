# Flight Booking Management System

## Overview

The Flight Booking Management System is a Java-based application that simulates the operations of a travel agency. The system allows customers to search and book flights, travel agents to manage reservations and flight schedules, and administrators to control system settings and user access.

The project was developed to apply Object-Oriented Programming (OOP) concepts, including inheritance, encapsulation, abstraction, polymorphism, and class relationships.

---

## Features

### User Authentication & Authorization

* Secure login system
* Role-based access control
* User profile management
* Session handling and logout functionality
* Input validation and authentication checks

### Flight Management

* Add and manage flights
* Search flights by origin, destination, and date
* Track seat availability
* Manage flight schedules and pricing

### Booking Management

* Create new bookings
* Modify and cancel reservations
* Manage passenger information
* Generate booking confirmations and itineraries

### Payment & Ticketing

* Simulated payment processing
* Multiple payment methods
* Booking status tracking
* E-ticket generation

---

## Object-Oriented Design

### Inheritance

* Abstract `User` class
* `Customer`, `Agent`, and `Administrator` subclasses

### Encapsulation

* Private attributes with controlled access through getters and setters
* Secure handling of user information

### Polymorphism

* Different implementations for booking, pricing, and payment-related operations

### Abstraction

* Abstract classes and interfaces to simplify system functionality

### Class Relationships

* Customers manage multiple bookings
* Flights contain seat information
* Bookings contain passenger and payment details

---

## Data Storage

The system uses file-based storage for data persistence.

Files include:

* `users.txt`
* `flights.txt`
* `bookings.txt`
* `passengers.txt`

Data is loaded and saved through a dedicated File Manager component.

---

## Technologies Used

* Java
* Object-Oriented Programming (OOP)
* File Handling
* Collections Framework
* UML Design

---

## Main Classes

* User (Abstract Class)
* Customer
* Agent
* Administrator
* Flight
* Booking
* Passenger
* Payment
* BookingSystem
* FileManager

---

## Learning Outcomes

* Applying Object-Oriented Programming principles
* Designing large-scale Java applications
* Authentication and role-based access control
* File handling and data persistence
* System analysis and UML modeling
* Building modular and maintainable software systems

---

## Project Outcome

This project demonstrates the practical application of Java and Object-Oriented Programming concepts through the development of a complete flight booking and management solution that supports multiple user roles, booking workflows, and data management functionalities.
