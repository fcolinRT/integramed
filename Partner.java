package com.app.ws;
//import java.util.*;

public class Partner {
    public String CardCode;
    public Integer GroupCode;
    public String CardName;
    public String MailAddress;
    public String RFC;
    public String Phone1;
    public String Phone2;
    public String CntcPrsn;
    public String GroupNum;
    public String LicTradNum;
    public Integer ListNum;
    public Integer Commission;
    public Integer SlpCode;
    public String Currency;
    public String FatherCard;
    public String CardFName;
    public Integer FatherType;
    public String U_Nomina;
    public String U_Beneficiario;
    public String PersonCardCode;
    public String PersonName;
    public String PersonAddress;
    public String PersonTel1;
    public String PersonTel2;
    public String PersonMiddleName;
    public String PersonLastName;
    public String PersonFirstName;
    public Integer PersonActive;
    public String PersonPosition;
    public String AddressCardCode;
    public String AddressAddress;
    public String AddressAdresType;
    public String AddressStreet;
    public String AddressBlock;
    public String AddressZipCode;
    public String AddressCity;
    public String AddressCounty;
    public String AddressCountry;
    public String AddressState;
    public String AddressLogInstanc;
    public Integer AddressObjType;
    public String AddressLicTradNum;
    public String AddressLineNum;
    public String AddressTaxCode;
    public String AddressBuilding;
    public String AddressAddress2;
    public String AddressAddress3;
    public String AddressAddrTpe;
    public String AddressStreetNo;
    public String AddressU_Latitud;
    public String AddressU_Longitud;
    public Integer gGroupCode;
    public String gGroupName;
    public Integer PaymenthGroupNum;
    public String PaymenthPymntGroup;
        
    public Partner(String sCardCode, Integer iGroupCode, String sCardName, String sMailAddress, String sRFC, String sPhone1, String sPhone2, String sCntcPrsn, String sGroupNum, String sLicTradNum, Integer iListNum, Integer iCommission, Integer iSlpCode, String sCurrency, String sFatherCard, String sCardFName, Integer iFatherType, String sU_Nomina, String sU_Beneficiario, String sPersonCardCode, String sPersonName, String sPersonAddress, String sPersonTel1, String sPersonTel2, String sPersonMiddleName, String sPersonLastName, String sPersonFirstName, Integer iPersonActive, String sPersonPosition, String sAddressCardCode, String sAddressAddress, String sAddressAdresType, String sAddressStreet, String sAddressBlock, String sAddressZipCode, String sAddressCity, String sAddressCounty, String sAddressCountry, String sAddressState, String sAddressLogInstanc, Integer iAddressObjType, String sAddressLicTradNum, String sAddressLineNum, String sAddressTaxCode, String sAddressBuilding, String sAddressAddress2, String sAddressAddress3, String sAddressAddrTpe, String sAddressStreetNo, String sAddressU_Latitud, String sAddressU_Longitud, Integer sgGroupCode, String sgGroupName, String sPaymenthGroupNum, String sPaymenthPymntGroup  ) {
        super();
        this.CardCode = sCardCode;
        this.GroupCode = iGroupCode;
        this.CardName = sCardName;
        this.MailAddress = sMailAddress;
        this.RFC = sRFC;
        this.Phone1 = sPhone1;
        this.Phone2 = sPhone2;
        this.CntcPrsn = sCntcPrsn;
        this.GroupNum = sGroupNum;
        this.LicTradNum = sLicTradNum;
        this.ListNum = iListNum;
        this.Commission = iCommission;
        this.SlpCode = iSlpCode;
        this.Currency = sCurrency;
        this.FatherCard = sFatherCard;
        this.CardFName = sCardFName;
        this.FatherType = iFatherType;
        this.U_Nomina = sU_Nomina;
        this.U_Beneficiario = sU_Beneficiario;
        this.PersonCardCode = sPersonCardCode;
        this.PersonName = sPersonName;
        this.PersonAddress = sPersonAddress;
        this.PersonTel1 = sPersonTel1;
        this.PersonTel2 = sPersonTel2;
        this.PersonMiddleName = sPersonMiddleName;
        this.PersonLastName = sPersonLastName;
        this.PersonFirstName = sPersonFirstName;
        this.PersonActive = iPersonActive;
        this.PersonPosition = sPersonPosition;
        this.AddressCardCode = sAddressCardCode;
        this.AddressAddress = sAddressAddress;
        this.AddressAdresType = sAddressAdresType;
        this.AddressStreet = sAddressStreet;
        this.AddressBlock = sAddressBlock;
        this.AddressZipCode = sAddressZipCode;
        this.AddressCity = sAddressCity;
        this.AddressCounty = sAddressCounty;
        switch(sAddressCountry){
            case "Estados Unidos": this.AddressCountry = "US"; break;
            default: this.AddressCountry = "MX"; break;
        }
        switch(sAddressState){
            case "Nuevo Le�n": this.AddressState = "NL"; break;
            default: this.AddressState = "CDM"; break;
        }
        this.AddressLogInstanc = sAddressLogInstanc;
        this.AddressObjType = iAddressObjType;
        this.AddressLicTradNum = sAddressLicTradNum;
        this.AddressLineNum = sAddressLineNum;
        this.AddressTaxCode = sAddressTaxCode;
        this.AddressBuilding = sAddressBuilding;
        this.AddressAddress2 = sAddressAddress2;
        this.AddressAddress3 = sAddressAddress3;
        this.AddressAddrTpe = sAddressAddrTpe;
        this.AddressStreetNo = sAddressStreetNo;
        this.AddressU_Latitud = sAddressU_Latitud;
        this.AddressU_Longitud = sAddressU_Longitud;
        this.gGroupCode = sgGroupCode;
        this.gGroupName = sgGroupName;
        switch(sPaymenthGroupNum){
            case "15": this.PaymenthGroupNum = 41; break;
            case "20-25 d naturales": this.PaymenthGroupNum = 24; break;
            case "21": this.PaymenthGroupNum = 29; break;
            case "25d h�biles": this.PaymenthGroupNum = 26; break;
            case "28": this.PaymenthGroupNum = 12; break;
            case "30": this.PaymenthGroupNum = 1; break;
            case "30d h�biles": this.PaymenthGroupNum = 1; break;
            case "45d naturales": this.PaymenthGroupNum = 38; break;
            default:  this.PaymenthGroupNum = -1; break;
        }
        this.PaymenthPymntGroup = sPaymenthPymntGroup;
    }
}

