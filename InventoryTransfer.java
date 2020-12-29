package com.app.ws;
import java.util.*;

class Product {
    private String itemCode;
    private Double quantity;
    private String warehouseCode;

    public Product( String itemCode, Double quantity, String warehouseCode) {
        super();
        this.itemCode = itemCode;
        this.quantity = quantity;
        this.warehouseCode = warehouseCode;
    }

    public String getItemCode() {
        return itemCode;
    }
    public Double getQuantity() {
        return quantity;
    }
    public String getWarehouseCode() {
        return warehouseCode;
    }
}

public class InventoryTransfer {
    private String cardCode;
    private String cardName;
    private String comments;
    private String fromWarehouse;
    private List<Product> products;

    public InventoryTransfer(String cardCode, String cardName, String comments, List<Product> products, String fromWarehouse) {
        super();
        this.cardCode = cardCode;
        this.cardName = cardName;
        this.comments = comments;
        this.products = products;
        this.fromWarehouse = fromWarehouse;
    }

    public String getCardCode() {
        return cardCode;
    }
    public String getCardName() {
        return cardName;
    }
    public String getComments() {
        return comments;
    }
    public List<Product> getProducts() { return products; }
    public String getFromWarehouse() { return fromWarehouse; }
}
