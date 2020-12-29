package com.app.ws;
import java.util.*;

class OrderProduct {
    private String itemCode;
    private Double quantity;
    private String warehouseCode;
    private String description;
    private String barCode;
    private Integer lineType;
    private Double discountPercent;
    private String taxCode;

    public OrderProduct( String itemCode, Double quantity, String warehouseCode, String description, String barCode, Integer lineType, Double discountPercent, String taxCode ) {
        super();
        this.itemCode = itemCode;
        this.quantity = quantity;
        this.warehouseCode = warehouseCode;
        this.description = description;
        this.barCode = barCode;
        this.lineType = lineType;
        this.discountPercent = discountPercent;
        this.taxCode = taxCode;
    }

    public String getItemCode() {
        return itemCode;
    }
    public Double getQuantity() {
        return quantity;
    }
    public String getItemDescription() {
        return description;
    }
    public String getBarCode() {
        return barCode;
    }
    public Integer getLineType() {
        return lineType;
    }
    public Double getDiscountPercent() {
        return discountPercent;
    }
    public String getTaxCode() {
        return taxCode;
    }
    public String getWarehouseCode() {
        return warehouseCode;
    }
}


public class Order {
    private String cardCode;
    private Integer docType;
    private String cardName;
    private String comments;
    //private String fromWarehouse;
    private List<OrderProduct> products;
    //private String taxDate;
    private String numAtCard;
    private Integer salesPersonCode;
    private String shipToCode;
    private String address2;
    private String address;
    private String payToCode;
    private Integer transportationCode;
    private String paymentMethod;
    private String docTime;

    public Order(String docTime, String paymentMethod, Integer transportationCode, String payToCode, String cardCode, String cardName, String comments, List<OrderProduct> products, String numAtCard, Integer salesPersonCode, String shipToCode, String address2, String address) {
        super();
        this.cardCode = cardCode;
        this.cardName = cardName;
        this.comments = comments;
        this.products = products;
        //this.fromWarehouse = fromWarehouse;
        //this.taxDate = taxDate;
        this.numAtCard = numAtCard;
        this.salesPersonCode = salesPersonCode;
        this.shipToCode = shipToCode;
        this.address2 = address2;
        this.address = address;
        this.payToCode = payToCode;
        this.transportationCode = transportationCode;
        this.paymentMethod = paymentMethod;
        this.docTime = docTime;
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
    public List<OrderProduct> getProducts() { return products; }
    //public String getFromWarehouse() { return fromWarehouse; }
    public String getNumAtCard() { return numAtCard; }
    //public String getTaxDate() { return taxDate; }
    public Integer getSalesPersonCode() { return salesPersonCode; }
    public String getShipToCode() { return shipToCode; }
    public String getAddress2() { return address2; }
    public String getAddress() { return address; }
    public String getPayToCode() { return payToCode; }
    public Integer getTransportationCode() { return transportationCode; }
    public String getPaymentMethod() { return paymentMethod; }
    public String getDocTime() { return docTime; }
}
