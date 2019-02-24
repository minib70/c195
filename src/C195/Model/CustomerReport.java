package C195.Model;

public class CustomerReport {
    private int count;
    private String name;

    public CustomerReport(String name) {
        this.name = name;
        count = 0;
    }

    public void setName(String name) {
        this.name = name;
    }

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
