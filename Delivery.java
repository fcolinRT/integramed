package com.app.ws;
import java.util.*;
import java.text.SimpleDateFormat;  

public class Delivery {
    public Integer DocEntry;
    public String Fechas;
    public String CardName;
    public String Address;
    public String PayToCode;
    public String Address2;
    public String Seguimiento;
    public String Sello;
    public String Observaciones;
    public String Calificacion;
    public String Comentarios;

        
    public Delivery(Integer iDocEntry, String Fechas, String Fecha_Entrega_Farmacia, String Hora_Entrega_Farmacia, String Fecha_Entrega_Mensajeria, String CardName, String Address, String PayToCode, String Address2, String Seguimiento, String Sello, String Observaciones, String Calificacion, String Comentarios) {
        System.out.println("RWT Constructor "+iDocEntry);
        this.DocEntry = iDocEntry;
        this.Fechas = Fechas;
        this.CardName = CardName;
        this.Address = Address;
        this.PayToCode = PayToCode;
        this.Address2 = Address2;
        this.Seguimiento = Seguimiento;
        this.Sello = Sello;
        this.Observaciones = Observaciones;
        this.Calificacion = Calificacion;
        this.Comentarios = Comentarios;
    }
}
