/*
 * Author: Taylor Vories
 * WGU C195 Project
 * Class used to assist with reporting.
 */
package C195.Model;

public class Month {
    private int count;
    private String name;

    public Month(String name) {
        this.name = name;
        count = 0;
    }

    public void setName(String name) {
        this.name = name;
    }

    /**
     * Counter to tell how many appointments per month.
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
