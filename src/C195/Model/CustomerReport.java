/*
 * Author: Taylor Vories
 * WGU C195 Project
 * Object to assist with gathering data and presenting it in report form as a bar graph.
 */

package C195.Model;

public class CustomerReport {
    private int count;
    private String name;

    /**
     * Constructor
     * @param name Name of the customer
     */
    public CustomerReport(String name) {
        this.name = name;
        count = 0;
    }

    public void setName(String name) {
        this.name = name;
    }

    /**
     * This method tracks how many times a match was found for the report
     */
    public void hit() {
        count++;
    }

    public int getCount() {
        return count;
    }

    public String getName() {
        return name;
    }
}
