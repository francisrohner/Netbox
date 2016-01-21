package data;

import java.io.Serializable;

/**
 * Created by Francis Rohner on 6/12/15.
 */
public class SomeData implements Serializable
{
    private String data;
    private int value;
    public SomeData(String data, int value)
    {
        this.data = data;
        this.value = value;
    }
    public String toString() { return "Data: " + data + ", Value: " + value; }
}
