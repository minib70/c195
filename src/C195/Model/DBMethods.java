package C195.Model;

import C195.C195;
import com.mysql.cj.protocol.Resultset;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import sun.nio.cs.ext.DoubleByte;

import java.sql.*;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;

public class DBMethods {
    private static final ZoneId zid = ZoneId.systemDefault();
    private static final DateTimeFormatter timeFormatter = DateTimeFormatter.ofLocalizedDateTime(FormatStyle.SHORT);
    private static final DateTimeFormatter dtfInstant = DateTimeFormatter.ofPattern("yyyy-MM-dd h:mm a");

    private static ZonedDateTime localZoneToUTC(ZonedDateTime date) {
        return date.withZoneSameInstant(ZoneId.of("UTC"));
    }

    private static ZonedDateTime utcToLocalZone(ZonedDateTime date) {
        return date.withZoneSameInstant(ZoneId.systemDefault());
    }

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

        try {
            PreparedStatement stmt = C195.dbConnection.prepareStatement( //TODO: Add customer name
                    "SELECT appointment.appointmentId, appointment.customerId, appointment.title, "
                            + "appointment.description, appointment.start, appointment.end, appointment.createdBy, "
                            + "appointment.location, appointment.contact, customer.customerName "
                            + "FROM appointment, customer "
                            + "WHERE appointment.customerId = customer.customerId "
            );
            ResultSet rs = stmt.executeQuery();
            while(rs.next()) {
                Appointment appt = new Appointment();
                appt.setAppointmentID(rs.getInt("appointment.appointmentId"));
                appt.setCustomerID(rs.getInt("appointment.customerId"));
                appt.setTitle(rs.getString("appointment.title"));
                appt.setDescription(rs.getString("appointment.description"));
                appt.setLocation(rs.getString("appointment.location"));
                appt.setContact(rs.getString("appointment.contact"));
                Instant startInstant = rs.getTimestamp("appointment.start").toInstant();
                appt.setStart(startInstant.toString());
                Instant endInstant = rs.getTimestamp("appointment.end").toInstant();
                appt.setEnd(endInstant.toString());
                // Timestamp startTimestamp = rs.getTimestamp("appointment.start");
                // LocalDateTime startUTC = startTimestamp.toLocalDateTime();
                // ZonedDateTime startLocalZone = startUTC.atZone(ZoneId.of("UTC"));
                // ZonedDateTime startL = startLocalZone.withZoneSameLocal(zid);
                // appt.setStart(startLocalZone.format(timeFormatter));
                //Timestamp endTimestamp = rs.getTimestamp("appointment.end");
                //LocalDateTime endUTC = endTimestamp.toLocalDateTime();
                //ZonedDateTime endLocalZone = endUTC.atZone(ZoneId.of("UTC"));
                //appt.setEnd(endLocalZone.format(timeFormatter));
                appt.setCustomerName(rs.getString("customer.customerName"));
                appointments.add(appt);
            }
        } catch (SQLException e) {
            System.out.println("Issue with SQL");
            e.printStackTrace();
        }
        return appointments;
    }

    public static void saveAppointment(Appointment appointmentToSave, String currentUser, int customerId) {
        //TODO: Fix issue with timmezone in database

        // This tells us if this is a modify appointment or a new appointment
        if (appointmentToSave.getAppointmentID() > 0) {
            // This is modify so we need to match it to an existing record
            try {
                System.out.println("Modifying appointment.");
                PreparedStatement stmt = C195.dbConnection.prepareStatement(
                        "UPDATE appointment "
                        + "SET appointment.title = ?, appointment.customerId = ?, appointment.description = ?, "
                        + "appointment.start = ?, appointment.end = ?, appointment.lastUpdate = ?, appointment.lastUpdateBy = ? "
                        + "WHERE appointmentId = ? "
                );
                stmt.setString(1, appointmentToSave.getTitle());
                stmt.setInt(2, customerId);
                stmt.setString(3, appointmentToSave.getDescription());
                Instant startInstant = Instant.parse(appointmentToSave.getStart());
                Timestamp startT = Timestamp.from(startInstant);
                stmt.setTimestamp(4, startT);
                Instant endInstant = Instant.parse(appointmentToSave.getEnd());
                Timestamp endT = Timestamp.from(endInstant);
                stmt.setTimestamp(5, endT);
                stmt.setTimestamp(6, new Timestamp(System.currentTimeMillis()));
                stmt.setString(7, currentUser);
                stmt.setInt(8, appointmentToSave.getAppointmentID());
                stmt.execute();
            } catch (SQLException e) {
                System.out.println("Issue with SQL");
                e.printStackTrace();
            }
        } else {
            try {
                System.out.println("New Apppointment");
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
                //ZonedDateTime startZ = ZonedDateTime.parse(appointmentToSave.getStart());
                //LocalDateTime start = startZ.toLocalDateTime();
                //ZonedDateTime startUTC = ZonedDateTime.parse(appointmentToSave.getStart(), dtfInstant);
                Instant startInstant = Instant.parse(appointmentToSave.getStart());
                Timestamp startT = Timestamp.from(startInstant);
                newApt.setTimestamp(7, startT);
                //ZonedDateTime endZ = ZonedDateTime.parse(appointmentToSave.getEnd());
                //LocalDateTime end = endZ.toLocalDateTime();
                //ZonedDateTime endUTC = ZonedDateTime.parse(appointmentToSave.getEnd(), dtfInstant);
                Instant endInstant = Instant.parse(appointmentToSave.getEnd());
                Timestamp endT = Timestamp.from(endInstant);
                newApt.setTimestamp(8, endT);
                newApt.setTimestamp(9, new Timestamp(System.currentTimeMillis()));
                newApt.setString(10, currentUser);
                newApt.setTimestamp(11, new Timestamp(System.currentTimeMillis()));
                newApt.setString(12, currentUser);
                newApt.execute();
            } catch (SQLException e) {
                System.out.println("Issue with SQL");
                e.printStackTrace();
            }
        }
    }

    public static void deleteAppointment(Appointment appointmentToDelete) {
        try {
            PreparedStatement stmt = C195.dbConnection.prepareStatement(
                    "DELETE appointment "
                    + "FROM appointment "
                    + "WHERE appointmentId = ?"
            );
            stmt.setInt(1, appointmentToDelete.getAppointmentID());
            stmt.execute();
        } catch (SQLException e) {
            System.out.println("Issue with SQL");
            e.printStackTrace();
        }
    }

}
