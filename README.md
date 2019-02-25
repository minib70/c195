# C195 Project for WGU
### What is it?
Scheduling software for handling customers and appointments for a fictional company.
#### Features:
* Login handling.
* Database connecction.
* JavaFX User Interface.
* MySQL Database manipulation.
* Basic logging.
* Customer management.
* Appointment creation.
* Reporting.

### Requirements
This project assumes you have:
 * An existing MySQL database with the appropriate tables configured.
 * You have configured your IDE to reference the required mysql-connector-java.jar file.  This project uses `mysql-connector-java-8.0.11.jar`.
 * User accounts are already in the database.  To login to this project without creating a user account, use the built in `test` user.
   * Username: `test`
   * Password: `test`
 * The tables in the database **must** have `AUTO_INCREMENT` set on the ID columns.

### Database Settings
You can change the database settings in the `DB.java` file.  Set the values:

| Variable Name | Description|
|---------------|------------|
|`DBNAME`       |Name of the database in the server |
|`USERNAME`     |Username to log into the database server |
|`PASSWORD`     |Password to log into the database server |
|`SERVER_ADDRESS` | IP Address or hostname of the database server |
