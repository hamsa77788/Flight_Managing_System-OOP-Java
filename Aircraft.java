import java.util.ArrayList;
import java.util.List;

// Aircraft class
class Aircraft {
    private String model;
    private int capacity;

    public Aircraft(String model, int capacity) {
        this.model = model;
        this.capacity = capacity;
    }

    public String getModel() { return model; }
    public int getCapacity() { return capacity; }

    @Override
    public String toString() {
        return model + " (Capacity: " + capacity + ")";
    }
}

// Airline class - Aggregates Aircraft
class Airline {
    private String name;
    private List<Aircraft> aircraftList;

    public Airline(String name) {
        this.name = name;
        this.aircraftList = new ArrayList<>();
    }

    // Add aircraft to the airline
    public void addAircraft(Aircraft aircraft) {
        aircraftList.add(aircraft);
    }

    public void displayFleet() {
        System.out.println(" Airline: " + name + " Fleet:");
        for (Aircraft a : aircraftList) {
            System.out.println(" - " + a);
        }
    }
}
