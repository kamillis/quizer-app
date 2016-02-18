package pl.kamillis.quizy.models;

import com.google.gson.annotations.Expose;

import java.io.Serializable;

/**
 * Created by Kamil on 19.12.2015.
 */
public class Tag implements Serializable {

    private int quantity;
    @Expose private String name;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getQuantity() {
        return quantity;
    }

    public String getFormattedQuantity() {
        return String.format("%d", quantity);
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

}
