package C195.Model;

import C195.C195;
import com.mysql.cj.protocol.Resultset;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import sun.nio.cs.ext.DoubleByte;

import java.sql.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZonedDateTime;

public class DBMethods {
    public static ObservableList<Customer> getCustomers() {
        ObservableList<Customer> customers = FXCollections.observableArrayList();
        try {
            PreparedStatement stmt = C195.dbConnection.prepareStatement(
                "SELECT customer.customerName, customer.customerId, address.phone, "
                    + "address.address, address.address2, address.postalCode, address.addressId, "
                    + "city.city, city.cityId, country.country, country.countryId "
                    + "FROM customer, address, city, country "
                    + "WHERE customer.addressId = address.addressId "
                    + "AND address.cityId = city.cityId AND city.countryId = country.countryId"
            );
            ResultSet rs = stmt.executeQuery();
            while(rs.next()) {
                Customer customer = new Customer();
                customer.setName(rs.getString("customer.customerName"));
                customer.setPhone(rs.getString("address.phone"));
                customer.setCustomerId(rs.getInt("customer.customerId"));
                customer.setAddress(rs.getString("address.address"));
                customer.setAddress2(rs.getString("address.address2"));
                customer.setCity(rs.getString("city.city"));
                customer.setPostalCode(rs.getString("address.postalCode"));
                customer.setCountry(rs.getString("country.country"));
                customer.setAddressId(rs.getInt("address.addressId"));
                customer.setCityId(rs.getInt("city.cityId"));
                customer.setCountryId(rs.getInt("country.countryId"));
                customers.add(customer);
            }
        } catch (SQLException e) {
            System.out.println("Issue with SQL");
            e.printStackTrace();
        }

        return customers;
    }

    public static ObservableList<City> getCities() {
        ObservableList<City> cities = FXCollections.observableArrayList();

        try {
            PreparedStatement stmt = C195.dbConnection.prepareStatement(
                    "SELECT city.city, city.cityId, country.country, country.countryId "
                        + "FROM city, country "
                        + "WHERE city.countryId = country.countryId"
            );

            ResultSet rs = stmt.executeQuery();
            while(rs.next()) {
                City city = new City();
                city.setCity(rs.getString("city.city"));
                city.setCountry(rs.getString("country.country"));
                city.setCityId(rs.getInt("city.cityId"));
                city.setCountryId(rs.getInt("country.countryId"));
                cities.add(city );
            }
        } catch (SQLException e) {
            System.out.println("Issue with SQL");
            e.printStackTrace();
        }

        return cities;
    }

    public static void saveCustomer(Customer customerToSave, String currentUser) {
        // This tells us if this is a modify customer or a new customer
        if(customerToSave.getCustomerId() > 0) {
            // This is a modify so we need to match it to an existing record in the db
            try {
                // Modify address
                PreparedStatement addr = C195.dbConnection.prepareStatement(
                        "UPDATE address, customer, city, country "
                                + "SET address.address = ?, address.address2 = ?, address.cityId = ?, "
                                + "address.postalCode = ?, address.phone = ?, address.lastUpdate = ?, address.lastUpdateBy = ? "
                                + "WHERE customer.customerId = ? AND customer.addressId = address.addressId "
                                + "AND address.cityId = city.cityId AND city.countryId = country.countryId "
                );
                addr.setString(1, customerToSave.getAddress());
                addr.setString(2, customerToSave.getAddress2());
                addr.setInt(3, customerToSave.getCityId());
                addr.setString(4, customerToSave.getPostalCode());
                addr.setString(5, customerToSave.getPhone());
                addr.setTimestamp(6, new java.sql.Timestamp(System.currentTimeMillis()));
                addr.setString(7, currentUser);
                addr.setInt(8, customerToSave.getCustomerId());
                addr.execute();

                // Modify Customer
                PreparedStatement cust = C195.dbConnection.prepareStatement(
                        "UPDATE address, customer "
                                + "SET customer.customerName = ?, customer.lastUpdateBy = ?, customer.lastUpdateBy = ?  "
                                + "WHERE customer.customerId = ? AND customer.addressId = address.addressId "
                );
                cust.setString(1, customerToSave.getName());
                cust.setTimestamp(2, new java.sql.Timestamp(System.currentTimeMillis()));
                cust.setString(3, currentUser);
                cust.setInt(4, customerToSave.getCustomerId());
                cust.execute();

            } catch (SQLException e) {
                e.printStackTrace();
            }
        } else { // This is a new customer
            try {
                // Create new address
                PreparedStatement newAddr = C195.dbConnection.prepareStatement(
                        "INSERT INTO address (address, address2, cityId, postalCode, phone, createDate, createdBy, lastUpdate, lastUpdateBy) "
                                + "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)", Statement.RETURN_GENERATED_KEYS
                );
                newAddr.setString(1, customerToSave.getAddress());
                if (customerToSave.getAddress2() == null) {
                    newAddr.setString(2, "");
                } else {
                    newAddr.setString(2, customerToSave.getAddress2());
                }
                newAddr.setInt(3, customerToSave.getCityId());
                newAddr.setString(4, customerToSave.getPostalCode());
                newAddr.setString(5, customerToSave.getPhone());
                newAddr.setTimestamp(6, new java.sql.Timestamp(System.currentTimeMillis()));
                newAddr.setString(7, currentUser);
                newAddr.setTimestamp(8, new java.sql.Timestamp(System.currentTimeMillis()));
                newAddr.setString(9, currentUser);
                newAddr.execute();

                // Assign address ID to customer
                ResultSet rs = newAddr.getGeneratedKeys();
                if (rs.next()) {
                    customerToSave.setAddressId(rs.getInt(1));
                } else {
                    System.out.println("No generated key returns, customer is flawed.");
                    customerToSave.setAddressId(-1);
                }

                // Create new Customer
                PreparedStatement newCust = C195.dbConnection.prepareStatement(
                        "INSERT INTO customer (customername, addressid, active, createdate, createdby, lastupdate, lastupdateby) "
                                + "VALUES (?, ?, ?, ?, ?, ?, ?) "
                );
                newCust.setString(1, customerToSave.getName());
                newCust.setInt(2, customerToSave.getAddressId());
                newCust.setInt(3, 1);
                newCust.setTimestamp(4, new java.sql.Timestamp(System.currentTimeMillis()));
                newCust.setString(5, currentUser);
                newCust.setTimestamp(6, new java.sql.Timestamp(System.currentTimeMillis()));
                newCust.setString(7, currentUser);
                newCust.execute();

            } catch (SQLException e) {
                System.out.println("Issue with SQL");
                e.printStackTrace();
            }
        }
    }

    public static void deleteCustomer(Customer customerToDelete) {
        try {
            PreparedStatement custD = C195.dbConnection.prepareStatement(
                    "DELETE customer.*, address.* "
                        + "FROM customer, address "
                        + "WHERE customer.customerId = ? AND customer.addressId = address.addressId "
            );
            custD.setInt(1, customerToDelete.getCustomerId());
            custD.execute();
        } catch(SQLException e) {
            System.out.println("Issue with SQL");
            e.printStackTrace();
        }
    }

    public static ObservableList<Appointment> getAppointments() {
        ObservableList<Appointment> appointments = FXCollections.observableArrayList();

        return appointments;
    }

    public static void saveAppointment(Appointment appointmentToSave, String currentUser, int customerId) {
        try {
            PreparedStatement newApt = C195.dbConnection.prepareStatement(
                    "INSERT INTO appointment "
                    + "(customerId, title, description, location, contact, url, start, end, createDate, createdBy, lastUpdate, lastUpdateBy)  "
                    + "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?) "
            );
            newApt.setInt(1, customerId);
            newApt.setString(2, appointmentToSave.getTitle());
            newApt.setString(3, appointmentToSave.getDescription());
            newApt.setString(4, "");
            newApt.setString(5, "");
            newApt.setString(6, "");
            ZonedDateTime startZ = ZonedDateTime.parse(appointmentToSave.getStart());
            LocalDateTime start = startZ.toLocalDateTime();
            Timestamp startT = Timestamp.valueOf(start);
            newApt.setTimestamp(7, startT);
            ZonedDateTime endZ = ZonedDateTime.parse(appointmentToSave.getEnd());
            LocalDateTime end = endZ.toLocalDateTime();
            Timestamp endT = Timestamp.valueOf(end);
            newApt.setTimestamp(8, endT);
            newApt.setTimestamp(9, new java.sql.Timestamp(System.currentTimeMillis()));
            newApt.setString(10, currentUser);
            newApt.setTimestamp(11, new java.sql.Timestamp(System.currentTimeMillis()));
            newApt.setString(12, currentUser);
            newApt.execute();
        } catch (SQLException e) {
            System.out.println("Issue with SQL");
            e.printStackTrace();
        }
    }

}
