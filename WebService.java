package com.app.ws; //Esta es la estructura de paquete creada

import com.google.gson.*;
import com.sap.smb.sbo.api.*;
import javax.ws.rs.*; //Importamos la librerÃ­a para manejar RESTful
import javax.annotation.PostConstruct;
//import javax.annotation.PreDestroy;
import javax.ws.rs.core.Application;
import com.sap.smb.sbo.wrapper.activeX.ActiveXComponent;
import com.sap.smb.sbo.wrapper.com.*;

import java.sql.Date;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;

@Path("SAPServices")
public class WebService extends Application
{
    public static ICompany company;
    /**
     * Initialize the web application
     */
    @PostConstruct
    public static void initialize() {
        if (ConectaSAP() == 0) {
            System.out.println("RWT: API Ejecutándose");
        } else {
            System.out.println("Cannot Connect: " + company.getLastError().getErrorMessage());
        }
    }
    
    public static Integer ConectaSAP(){
        company = com.sap.smb.sbo.api.SBOCOMUtil.newCompany();
        company.setServer("10.57.1.217"); // setSLDServer?
        company.setCompanyDB("Pruebas_Trata");
	//company.setUserName("redwoodtwins");
        //company.setPassword("SalesForce#19");
        company.setUserName("ti");
        company.setPassword("2710");
        company.setUseTrusted(false);
        company.setDbServerType(SBOCOMConstants.BoDataServerTypes_dst_MSSQL2008); // VERIFICAR VERSIÃ“N
        company.setLanguage(SBOCOMConstants.BoSuppLangs_ln_English); // Verificar Lenguaje
        company.setDbUserName("redwoodtwins");
        company.setDbPassword("SalesForce#19");
        company.setLicenseServer("10.57.1.217:30000");
        return company.connect();
    }

    public WebService() {
        super();
    }
    
    @Path("test/{type}") //TEST (tipo)
    @GET //TEST
    @Produces({"text/plain", "text/html","text/xml", "application/json"}) //Indicamos que el tipo de salida es texto plano, XML, HTML o JSON
    public String mostrarMensaje(@PathParam("type") String tipo)//Método que recibe como parametro el valor de type en la URL
    {
        if(tipo.equalsIgnoreCase("texto"))
        {
            return "RWT: Post Order conectado?"+company.isConnected();
        }
        else if (tipo.equalsIgnoreCase("html"))
        {
            return "<html lang='es'><head><meta charset='UTF-8'/><title>WS</title></head><body><h1>RWT: Post Order conectado?"+company.isConnected()+"</h1></body></html>";
        }
        else if(tipo.equalsIgnoreCase("xml"))
        {
            return "<?xml version='1.0' encoding='UTF-8'?><root><value>RWT: Post Order conectado?"+company.isConnected()+"</value></root>";
        }
        else if(tipo.equalsIgnoreCase("json"))
        {
            return "{\"root\":{\"value\":\"RWT: Post Order conectado?"+company.isConnected()+"\"}}";
        }
        else
        {
            return "Tipo no soportado";
        }
    }
    
    
    @POST
    @Path("upsert/{object}")
    @Consumes("application/json")
    @Produces("application/json")
    public String SAPServices(String json, @PathParam("object") String objeto){
        if(company.isConnected()) {
            Calendar Fechas = Calendar.getInstance();
            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
            java.sql.Date ourJavaDateObject = new java.sql.Date(Fechas.getTime().getTime());
            //String strDate = "2019-10-10";
            String strDate = format.format(ourJavaDateObject);
            if(objeto.equalsIgnoreCase("order")){
                //company.startTransaction();
                IDocuments salesOrder;
                Order order = new Gson().fromJson(json, Order.class);
                List<OrderProduct> products = order.getProducts();
                    try {
                            salesOrder = com.sap.smb.sbo.api.SBOCOMUtil.newDocuments(company, 17);
                            // Set Business Partner|
                            salesOrder.setCardCode(order.getCardCode());
                            salesOrder.setCardName(order.getCardName());
                            salesOrder.setNumAtCard(order.getNumAtCard());
                            //salesOrder.setTaxDate((Date.valueOf(order.getTaxDate()));
                            salesOrder.setSalesPersonCode(order.getSalesPersonCode());
                            salesOrder.setComments(order.getComments());
                            salesOrder.setShipToCode(order.getShipToCode());
                            salesOrder.setAddress2(order.getAddress2());
                            salesOrder.setPayToCode(order.getPayToCode());
                            salesOrder.setAddress(order.getAddress());
                            salesOrder.setTransportationCode(order.getTransportationCode());
                            //salesOrder.setTaxExemptionLetterNum();
                            salesOrder.setPaymentMethod(order.getPaymentMethod());
                            //salesOrder.setDocTime(Date.valueOf(order.getDocTime()));
                            // Set Item/Service Type, 0=Items, 1=Service
                            salesOrder.setDocType(0);
                            // Set Posting Date
                            salesOrder.setDocDate(Date.valueOf(strDate));
                            // Set Delivery Date
                            salesOrder.setDocDueDate(Date.valueOf(strDate));
                            for (int productIndex=0; productIndex < products.size(); productIndex++) {
                                    salesOrder.getLines().add();
                                    salesOrder.getLines().setCurrentLine(productIndex);
                                    salesOrder.getLines().setItemCode(products.get(productIndex).getItemCode());
                                    salesOrder.getLines().setQuantity(products.get(productIndex).getQuantity());
                                    salesOrder.getLines().setWarehouseCode(products.get(productIndex).getWarehouseCode());
                                    salesOrder.getLines().setItemDescription(products.get(productIndex).getItemDescription());
                                    salesOrder.getLines().setBarCode(products.get(productIndex).getBarCode());
                                    salesOrder.getLines().setLineType(products.get(productIndex).getLineType());
                                    salesOrder.getLines().setDiscountPercent(products.get(productIndex).getDiscountPercent());
                                    salesOrder.getLines().setTaxCode(products.get(productIndex).getTaxCode());
                            }
                            /*salesOrder.getLines().add();
                            salesOrder.getLines().setCurrentLine(1);
                            salesOrder.getLines().setItemCode("ITEM002");
                            salesOrder.getLines().setQuantity(5.0);*/
                            salesOrder.add();
                            salesOrder.saveDraftToDocument();
                            //company.endTransaction(0);
                            if(company.getNewObjectCode() != null && !company.getNewObjectCode().equalsIgnoreCase("")){
                                //res.status(201);
                                return "{\"id\":\"" +  company.getNewObjectCode() + "\"}";                            
                            } else {
                                SBOErrorMessage errMsg = company.getLastError();
                                return "{\"message\":\"Error: " +  errMsg.getErrorMessage() + "\"}";
                            }
                    } catch (SBOCOMException e) {
                        // get error message fom SAP Business One Server
                        SBOErrorMessage errMsg = company.getLastError();
                        //company.endTransaction(1 );
                        //res.status(400);
                        return "{\"message\":\"Error: " + e.getMessage() +  errMsg.getErrorMessage() + "\"}";
                    }
            } else if(objeto.equalsIgnoreCase("account")){
                //company.startTransaction();
                IBusinessPartners  BusinessPartner;
                //IContacts  PersonalInfo;
                IContactEmployees  EmpInfo;
                IBPAddresses Addresses;
                String IdCta;
                String IdContact;
                //String IdAddress;
                Boolean AlreadyExists = false;

                String Result = "{";
                Partner Account = new Gson().fromJson(json, Partner.class);
                    try {
                            //EmpInfo  = com.sap.smb.sbo.api.SBOCOMUtil.newContacts(company);
                            //PersonalInfo.setCardCode(Account.PersonCardCode);
                            //PersonalInfo.setCardName(Account.PersonName);
                            //PersonalInfo.setAddress(Account.PersonAddress);
                            //PersonalInfo.setPhone(Account.PersonTel1);
                            //PersonalInfo.setTel2(Account.PersonTel2);
                            //PersonalInfo.setMiddleName(Account.PersonMiddleName);
                            //PersonalInfo.setLastName(Account.PersonLastName);
                            //PersonalInfo.setFirstName(Account.PersonFirstName);
                            //PersonalInfo.setActive(Account.PersonActive);
                            //PersonalInfo.setPosition(Account.PersonPosition);
                            //PersonalInfo.setCardType(1);
                            //PersonalInfo.add();
                            //if(company.getNewObjectCode() != null && !company.getNewObjectCode().equalsIgnoreCase("")){
                            //    Result += "\"PersonId\":\"" +  company.getNewObjectCode() + "\", ";
                                //res.status(201);
                                BusinessPartner = com.sap.smb.sbo.api.SBOCOMUtil.newBusinessPartners(company);
                                if(BusinessPartner.getByKey(Account.CardCode)){
                                    AlreadyExists = true;
                                }
                                // Set Business Partner|
                                BusinessPartner.setCardCode(Account.CardCode);
                                BusinessPartner.setGroupCode(Account.GroupCode);
                                BusinessPartner.setCardName(Account.CardName);
                                BusinessPartner.setMailAddress(Account.MailAddress);

                                BusinessPartner.setPhone1(Account.Phone1);
                                BusinessPartner.setPhone2(Account.Phone2);
                                //BusinessPartner.setGroupNum(Account.GroupNum);
                                BusinessPartner.setFederalTaxID(Account.LicTradNum);

                                BusinessPartner.setPriceListNum(Account.ListNum);
                                //BusinessPartner.setCommission(Account.Commission);
                                BusinessPartner.setCommissionGroupCode(Account.Commission);

                                //BusinessPartner.setSlpCode(Account.SlpCode);
                                BusinessPartner.setSalesPersonCode(Account.SlpCode);
                                BusinessPartner.setCurrency(Account.Currency);

                                BusinessPartner.setFatherCard(Account.FatherCard);
                                //BusinessPartner.setCardFName(Account.CardFName);
                                BusinessPartner.setFatherType(Account.FatherType);

                                BusinessPartner.getUserFields().getFields().item("U_Nomina").setValue(Account.U_Nomina);
                                System.out.println("RWT: Account.U_Beneficiario "+Account.U_Beneficiario);
                                BusinessPartner.getUserFields().getFields().item("U_Beneficiario").setValue(Account.U_Beneficiario);

                                //Adicional Ubaldo
                                BusinessPartner.setAddress(Account.PersonAddress);
                                //BusinessPartner.setEmailAddress(Account.MailAddress);

                                BusinessPartner.setCardType(0);

                                if(!AlreadyExists) {
                                    BusinessPartner.add();
                                    IdCta = company.getNewObjectCode();
                                } else {
                                    IdCta = Account.CardCode;
                                }
                                //company.endTransaction(0);

                                if(IdCta != null && !IdCta.equalsIgnoreCase("")){
                                    //res.status(201);
                                    Result +=  "\"id\":\"" +  IdCta + "\", ";
                                    if(!AlreadyExists) {
                                        BusinessPartner.getByKey(IdCta);
                                    }
                                    EmpInfo = BusinessPartner.getContactEmployees();
                                    System.out.println("RWT: EmpInfo.getActive "+EmpInfo.getActive());


                                    while(EmpInfo.getCount() > 0){
                                        System.out.println("RWT: EmpInfo.Deletting... getCount "+EmpInfo.getCount());
                                        EmpInfo.delete();
                                    }

                                    EmpInfo.add();
                                    //EmpInfo.setCurrentLine(1);
                                    System.out.println("RWT: EmpInfo.getActive1 "+EmpInfo.getActive());
                                    EmpInfo.setName(IdCta);
                                    System.out.println("RWT: EmpInfo.getCode1 "+EmpInfo.getCardCode() );
                                    EmpInfo.setAddress(Account.PersonAddress);
                                    EmpInfo.setPhone1(Account.PersonTel1);
                                    EmpInfo.setPhone2(Account.PersonTel2);
                                    EmpInfo.setMiddleName(Account.PersonMiddleName);
                                    EmpInfo.setLastName(Account.PersonLastName);
                                    EmpInfo.setFirstName(Account.PersonFirstName);
                                    EmpInfo.setActive(Account.PersonActive);
                                    EmpInfo.setPosition(Account.PersonPosition);
                                    EmpInfo.add();
                                    System.out.println("RWT: EmpInfo.getActive2 "+EmpInfo.getActive());
                                    System.out.println("RWT: EmpInfo.getCode2 "+EmpInfo.getCardCode() );
                                    System.out.println("RWT: EmpInfo.add Last Erorr? "+company.getLastErrorDescription());
                                    //if(BusinessPartner.update() == 0){
                                        //IdContact = company.getNewObjectCode();
                                    //    Result +=  "\"EmpInfo\":\"BP Update OK\", ";
                                        //BusinessPartner.setContactPerson(EmpInfo.getCardCode());                                  
                                    //} else {
                                    //    Result +=  "\"EmpInfo\":\"Fail BP Update\", ";
                                    //}     
                                    Addresses = BusinessPartner.getAddresses();
                                    while(Addresses.getCount() > 0){
                                        System.out.println("RWT: Addresses.getCount "+Addresses.getCount());
                                        Addresses.delete();
                                    }
                                    Addresses.add();                                    

                                    //Addresses.setAddressCardCode(Account.AddressCardCode);
                                    //Addresses.setAddressAddress(Account.AddressAddress);
                                    Addresses.setAddressName(Account.AddressAddress);
                                    //Addresses.setTypeOfAddress();
                                    Addresses.setStreet(Account.AddressStreet);
                                    Addresses.setBlock(Account.AddressBlock);
                                    Addresses.setZipCode(Account.AddressZipCode);
                                    Addresses.setCity(Account.AddressCity);
                                    Addresses.setCounty(Account.AddressCounty);
                                    Addresses.setCountry(traductor("País", Account.AddressCountry));
                                    Addresses.setState(traductor("Estado", Account.AddressState));
                                    //BusinessPartner.setAddressLogInstanc(Account.AddressLogInstanc);
                                    //BusinessPartner.setAddressObjType(Account.AddressObjType);
                                    Addresses.setFederalTaxID(Account.AddressLicTradNum);
                                    //BusinessPartner.setAddressLineNum(Account.AddressLineNum);
                                    //BusinessPartner.setAddressTaxCode(Account.AddressTaxCode);
                                    //BusinessPartner.setAddressBuilding(Account.AddressBuilding);
                                    Addresses.setBuildingFloorRoom(Account.AddressBuilding);
                                    //BusinessPartner.setAddressAddress2(Account.AddressAddress2);
                                    Addresses.setAddressName2(Account.AddressAddress2);
                                    //BusinessPartner.setAddressAddress3(Account.AddressAddress3);
                                    Addresses.setAddressName3(Account.AddressAddress3);
                                    Addresses.setAddressType(Account.AddressObjType);
                                    Addresses.setTypeOfAddress(Account.AddressAddrTpe);
                                    Addresses.setStreetNo(Account.AddressStreetNo);
                                    Addresses.getUserFields().getFields().item("U_Latitud").setValue(Account.AddressU_Latitud);
                                    Addresses.getUserFields().getFields().item("U_Longitud").setValue(Account.AddressU_Longitud);
                                    Addresses.add();

                                    BusinessPartner.setGroupCode(Account.gGroupCode);
                                    //BusinessPartner.setgGroupName(Account.gGroupName);

                                    BusinessPartner.setPayTermsGrpCode(Account.PaymenthGroupNum);
                                    //BusinessPartner.setPaymenthPymntGroup(Account.PaymenthPymntGroup);

                                    if(BusinessPartner.update() == 0){
                                    //if(BusinessPartner.add() == 0){
                                        Result += "\"message\":\"Cuenta" +  IdCta + "_Contacto_OK_Dirección" + Addresses.getAddressName() + "\" }";
                                    } else {
                                        SBOErrorMessage errMsg = company.getLastError();
                                        Result += "\"message\":\"Error: " +  errMsg.getErrorMessage() + "\"}";
                                    }
                                    return Result;                                 

                                } else {
                                    SBOErrorMessage errMsg = company.getLastError();
                                    Result += "\"message\":\"Error: " +  errMsg.getErrorMessage() + "\"}";
                                    return Result;  
                                }
                            //} else {
                            //    SBOErrorMessage errMsg = company.getLastError();
                            //    return "{\"message\":\"Error: " +  errMsg.getErrorMessage() + "\"}";
                            //}

                    } catch (SBOCOMException e) {
                        // get error message fom SAP Business One Server
                        SBOErrorMessage errMsg = company.getLastError();
                        //company.endTransaction(1 );
                        //res.status(400);
                        return "{\"message\":\"Error SBO: " + e.getMessage() +  errMsg.getErrorMessage() + "\"}";
                    } catch (ComFailException e) {
                        //company.endTransaction(1 );
                        //res.status(400);
                        return "{\"message\":\"Error ComFailException: " + e.getMessage() + "\"}";
                    } catch (Exception e) {
                        //company.endTransaction(1 );
                        //res.status(400);
                        return "{\"message\":\"Error: " + e.getMessage() + "\"}";
                    }
            }  else if(objeto.equalsIgnoreCase("businessaccount")){
                IBusinessPartners  BusinessPartner;
                IContactEmployees  EmpInfo;
                IBPAddresses Addresses;
                String IdCta;
                Boolean AlreadyExists = false;

                String Result = "{";
                Partner Account = new Gson().fromJson(json, Partner.class);
                    try {
                        BusinessPartner = com.sap.smb.sbo.api.SBOCOMUtil.newBusinessPartners(company);
                        System.out.println("RWT: Buscando Institución preexistente: "+Account.CardCode );
                        if(BusinessPartner.getByKey(Account.CardCode)){
                            AlreadyExists = true;
                            System.out.println("RWT: Institución preexistente: "+BusinessPartner.getCardCode() );
                        } else {
                            BusinessPartner.setCardCode(Account.CardCode);                        
                        }
                        // Set Business Partner|
                        //BusinessPartner.setGroupCode(Account.GroupCode);
                        BusinessPartner.setCardName(Account.CardName);
                        BusinessPartner.setMailAddress(Account.MailAddress);
                        BusinessPartner.setPhone1(Account.Phone1);
                        BusinessPartner.setPhone2(Account.Phone2);
                        BusinessPartner.setFederalTaxID(Account.LicTradNum);
                        //BusinessPartner.setPriceListNum(Account.ListNum);
                        BusinessPartner.setCommissionGroupCode(Account.Commission);
                        BusinessPartner.setSalesPersonCode(Account.SlpCode);
                        BusinessPartner.setCurrency(Account.Currency);
                        BusinessPartner.setFatherCard(Account.FatherCard);
                        BusinessPartner.setFatherType(Account.FatherType);
                        //BusinessPartner.getUserFields().getFields().item("U_Nomina").setValue(Account.U_Nomina);
                        //BusinessPartner.getUserFields().getFields().item("U_Beneficiario").setValue(Account.U_Beneficiario);
                        BusinessPartner.setAddress(Account.PersonAddress);
                        BusinessPartner.setCardType(0);
                        if(!AlreadyExists) {
                            BusinessPartner.add();
                            IdCta = company.getNewObjectCode();
                        } else {
                            if(BusinessPartner.update() == 0){
                                IdCta = BusinessPartner.getCardCode();
                            } else {
                                SBOErrorMessage errMsg = company.getLastError();
                                IdCta = "Error: " +  errMsg.getErrorMessage();
                            }
                        }
                        if(IdCta != null && !IdCta.equalsIgnoreCase("")){
                            Result +=  "\"id\":\"" +  IdCta + "\", ";
                            if(!AlreadyExists) {
                                BusinessPartner.getByKey(IdCta);
                            }
                            Addresses = BusinessPartner.getAddresses();
                            Addresses.setCurrentLine(0);
                            if(!Addresses.getTypeOfAddress().equalsIgnoreCase("B")){
                                Addresses.setCurrentLine(1);
                            }
                            Addresses.setAddressName(Account.AddressAddress);
                            Addresses.setStreet(Account.AddressStreet);
                            //Addresses.setBlock(Account.AddressBlock);
                            Addresses.setZipCode(Account.AddressZipCode);
                            Addresses.setCity(Account.AddressCity);
                            Addresses.setCounty(Account.AddressCounty);
                            Addresses.setCountry(traductor("País", Account.AddressCountry));
                            Addresses.setState(traductor("Estado", Account.AddressState));
                            Addresses.setFederalTaxID(Account.AddressLicTradNum);
                            Addresses.setBuildingFloorRoom(Account.AddressBuilding);
                            //Addresses.setAddressName2(Account.AddressAddress2);
                            //Addresses.setAddressName3(Account.AddressAddress3);
                            Addresses.setAddressType(Account.AddressObjType);
                            Addresses.setTypeOfAddress(Account.AddressAddrTpe);
                            //Addresses.setStreetNo(Account.AddressStreetNo);
                            Addresses.getUserFields().getFields().item("U_Latitud").setValue(Account.AddressU_Latitud);
                            Addresses.getUserFields().getFields().item("U_Longitud").setValue(Account.AddressU_Longitud);
                            Addresses.add();
                            BusinessPartner.setGroupCode(Account.gGroupCode);
                            BusinessPartner.setPayTermsGrpCode(Account.PaymenthGroupNum);
                            if(BusinessPartner.update() == 0){
                                Result += "\"messageDir\":\"Dirección" + Addresses.getAddressName() + "\" }";
                            } else {
                                SBOErrorMessage errMsg = company.getLastError();
                                Result += "\"messageDir\":\"Error: " +  BusinessPartner.update()+ errMsg.getErrorMessage() + "\"}";
                            }
                            return Result;                                 
                        } else {
                            SBOErrorMessage errMsg = company.getLastError();
                            Result += "\"messageGeneral\":\"Error: " +  errMsg.getErrorMessage() + "\"}";
                            return Result;  
                        }
                    } catch (SBOCOMException e) {
                        // get error message fom SAP Business One Server
                        SBOErrorMessage errMsg = company.getLastError();
                        //company.endTransaction(1 );
                        //res.status(400);
                        return "{\"message\":\"Error: " + e.getMessage() +  errMsg.getErrorMessage() + "\"}";
                    }
            }  else if(objeto.equalsIgnoreCase("doctor")){
                IUserTable TRMEDICO;
                String IdCta;
                Boolean AlreadyExists = false;
                Doctor Medico = new Gson().fromJson(json, Doctor.class);
                    try {
                        //TRMEDICO2 = company.getCompanyService().getGeneralService("TRMEDICOS").getDataInterface(0);
                        TRMEDICO = company.getUserTables().item("TRMEDICOS");
                        //TRMEDICO = com.sap.smb.sbo.api.SBOCOMUtil.getUserTablesMD(company, "TRMEDICOS");
                        //return "{\"message\":\"RWT Tabla: "+TRMEDICO.getTableName() + "\"}";
                        System.out.println("RWT: Buscando Médico preexistente: "+Medico.Code );
                        if(TRMEDICO.getByKey(Medico.Code)){
                            AlreadyExists = true;
                            System.out.println("RWT: Médico preexistente: "+TRMEDICO.getCode() );
                        } else {
                            TRMEDICO.add();
                            TRMEDICO.setCode(Medico.Code);
                        }
                        TRMEDICO.setName(Medico.Name);
                        TRMEDICO.getUserFields().getFields().item("U_Nombre").setValue(Medico.U_Nombre);
                        TRMEDICO.getUserFields().getFields().item("U_MedApPat").setValue(Medico.U_MedApPat);
                        TRMEDICO.getUserFields().getFields().item("U_MedApMat").setValue(Medico.U_MedApMat);
                        TRMEDICO.getUserFields().getFields().item("U_ClaveMedico").setValue(Medico.U_ClaveMedico);
                        TRMEDICO.getUserFields().getFields().item("U_EspecialidadMed").setValue(Medico.U_EspecialidadMed);
                        TRMEDICO.getUserFields().getFields().item("U_EstadoRep").setValue(Medico.U_EstadoRep);
                        TRMEDICO.getUserFields().getFields().item("U_Poblacion").setValue(Medico.U_Poblacion);
                        TRMEDICO.getUserFields().getFields().item("U_Institucion").setValue(traductor("InstitucionMedico", Medico.U_Institucion));
                        TRMEDICO.getUserFields().getFields().item("U_TelMedico").setValue(Medico.U_TelMedico);
                        TRMEDICO.getUserFields().getFields().item("U_firma").setValue(Medico.firma);
                        TRMEDICO.getUserFields().getFields().item("U_CedulaProf").setValue(Medico.CedulaProf);
                        if(!AlreadyExists) {
                            if(TRMEDICO.add() == 0){
                                IdCta = company.getNewObjectCode();
                            } else {
                                SBOErrorMessage errMsg = company.getLastError();
                                IdCta = "Error: " +  errMsg.getErrorMessage();
                            }
                        } else {
                            if(TRMEDICO.update() == 0){
                                IdCta = TRMEDICO.getCode();
                            } else {
                                SBOErrorMessage errMsg = company.getLastError();
                                IdCta = "Error: " +  errMsg.getErrorMessage();
                            }
                        }
                        return "{ \"id\":\"" +  IdCta + "\" }";                 
                    } catch (Exception e) {
                        // get error message fom SAP Business One Server
                        SBOErrorMessage errMsg = company.getLastError();
                        //company.endTransaction(1 );
                        //res.status(400);
                        return "{\"message\":\"Error: " + e.getMessage() +  errMsg.getErrorMessage() + "\"}";
                    }
            }  else if(objeto.equalsIgnoreCase("invertorytransfer")){
                InventoryTransfer inventoryTransfer = new Gson().fromJson(json, InventoryTransfer.class);
                    try {
			List<Product> products = inventoryTransfer.getProducts();
			System.out.println(products);
                        //System.out.println("RWT: date: "+strDate);
                        //StockTransfer nst = company.GetBusinessObject(BoObjectTypes.oStockTransferDraft);
                        //oTransfer.DocObjectCode =  BoObjectTypes.oInventoryTransferRequest;
                        IStockTransfer nst = SBOCOMUtil.newStockTransfer(company, SBOCOMConstants.BoObjectTypes_StockTransfer_oInventoryTransferRequest);
                        //nst.setDocObjectCode(SBOCOMConstants.BoObjectTypes_StockTransfer_oInventoryTransferRequest);
			//nst.setDocObjectCode(SBOCOMConstants.BoObjectTypes_oStockTransfer);
			//nst = SBOCOMUtil.newDocuments(appMain.company,SBOCOMConstants.BoObjectTypes_Document_oInventoryGenEntry);
			//nst.setDocDate(Date.valueOf("2019-10-10"));
			nst.setDocDate(Date.valueOf(strDate));
			nst.setCardCode(inventoryTransfer.getCardCode());
			nst.setCardName(inventoryTransfer.getCardName());
			nst.setComments(inventoryTransfer.getComments());
			nst.setFromWarehouse(inventoryTransfer.getFromWarehouse());
                        //SBOCOMUtil.getSBObob(company).getCurrencyRate(strDate, ourJavaDateObject)
			for (int productIndex=0; productIndex < products.size(); productIndex++) {
                            nst.getLines().add();
                            nst.getLines().setCurrentLine(productIndex);
                            nst.getLines().setItemCode(products.get(productIndex).getItemCode());
                            nst.getLines().setQuantity(products.get(productIndex).getQuantity());
                            nst.getLines().setWarehouseCode(products.get(productIndex).getWarehouseCode());
			}
			if (nst.add() == 0){
                            //res.type("application/json");
                            //res.status(201);
                            return "{\"id\":\"" +  company.getNewObjectCode() + "\"}";
			} else {
                            // get error message fom SAP Business One Server
                            SBOErrorMessage errMsg = company.getLastError();
                            System.out.println("Cannot add Inventory Transfer: " + errMsg.getErrorMessage()+ " "+ errMsg.getErrorCode());
                            //res.type("application/json");
                            //res.status(400);
                            return "{\"message\":\"" +  errMsg.getErrorMessage() + "\"}";
                        }
               
                    } catch (Exception e) {
                        // get error message fom SAP Business One Server
                        SBOErrorMessage errMsg = company.getLastError();
                        //company.endTransaction(1 );
                        //res.status(400);
                        return "{\"message\":\"Error: " + e.getMessage() +  errMsg.getErrorMessage() + "\"}";
                    }
            } else if(objeto.equalsIgnoreCase("entrega")){
                System.out.println("RWT: json: "+json);
                Gson obGson = new GsonBuilder().registerTypeAdapter(Date.class, new DateDeserializer()).create();
                //obGson.registerTypeAdapter(Date.class, new DateDeserializer());
                Delivery DeliveryOrder = obGson.fromJson(json, Delivery.class);
                    try {
			IDocuments Entrega = SBOCOMUtil.newDocuments(company,SBOCOMConstants.BoObjectTypes_Document_oDeliveryNotes);
                        System.out.println("RWT: Buscando Entrega preexistente: "+DeliveryOrder.DocEntry);
                        if(Entrega.getByKey(DeliveryOrder.DocEntry)){
                            System.out.println("RWT: Entrega preexistente: "+Entrega.getDocEntry());
                            String FechaRecepcion = DeliveryOrder.Fechas.split("@")[0];
                            String HoraRecepcion = FechaRecepcion.split(" ")[1];
                            System.out.println("RWT: DeliveryOrder.Fecha_Entrega_Farmacia: "+FechaRecepcion );
                            System.out.println("RWT: HoraRecepcion: "+HoraRecepcion.substring(0,2));
                            if(FechaRecepcion.length() > 0) {
                                Entrega.getUserFields().getFields().item("U_FEnt_Farm_Log").setValue(FechaRecepcion );
                                Entrega.getUserFields().getFields().item("U_HoraRecepcion").setValue(Integer.parseInt(HoraRecepcion.substring(0,2)));
                            }
                            if(DeliveryOrder.Fechas.split("@")[1] != "") {
                                System.out.println("RWT: DeliveryOrder.Fecha_Entrega_Mensajeria: "+DeliveryOrder.Fechas.split("@")[1]);
                                Entrega.getUserFields().getFields().item("U_FEntregaAMens").setValue(DeliveryOrder.Fechas.split("@")[1]);
                            }
                            //Entrega.setAddress(DeliveryOrder.Address);Update of "Address" field is not possible  [DLN12.StreetS][line: 0]
                            //Entrega.setAddress2(DeliveryOrder.Address2);Update of "Address" field is not possible  [DLN12.StreetS][line: 0]
                            //Entrega.setCardName(DeliveryOrder.CardName);[ODLN.CardName] , 'Field cannot be updated (ODBC -1029)
                            Entrega.setPayToCode(DeliveryOrder.PayToCode);
                            Entrega.setTrackingNumber(DeliveryOrder.Seguimiento);
                            Entrega.setComments(DeliveryOrder.Observaciones);
                            Entrega.getUserFields().getFields().item("U_Firma").setValue(DeliveryOrder.Sello);
                            Entrega.getUserFields().getFields().item("U_CalifServicio").setValue(DeliveryOrder.Calificacion);
                            Entrega.getUserFields().getFields().item("U_ComentServicio").setValue(DeliveryOrder.Comentarios);
                            if (Entrega.update() == 0){
                                //res.type("application/json");
                                //res.status(201);
                                return "{\"id\":\"" +  Entrega.getDocEntry() + "\"}";
                            } else {
                                // get error message fom SAP Business One Server
                                SBOErrorMessage errMsg = company.getLastError();
                                System.out.println("Cannot update Delivery Order: " + errMsg.getErrorMessage()+ " "+ errMsg.getErrorCode());
                                //res.type("application/json");
                                //res.status(400);
                                return "{\"message\":\"" +  errMsg.getErrorMessage() + "\"}";
                            }
                        } else {
                           return "{\"message\":\"Entrega no encontrada - " +  DeliveryOrder.DocEntry + "\"}"; 
                        }     
                    } catch (Exception e) {
                        // get error message fom SAP Business One Server
                        SBOErrorMessage errMsg = company.getLastError();
                        System.out.println("RWT Error: "+e.getMessage() +  errMsg.getErrorMessage() );
                        //company.endTransaction(1 );
                        //res.status(400);
                        return "{\"message\":\"Error: " + e.getMessage() +  errMsg.getErrorMessage() + "\"}";
                    }
            } else {
                return "{\"message\":\"Entidad no soportada: " +  objeto + "\"}";
            }
        } else {
            return "{\"message\":\"DI API no conectado\"}";
        }  
    }
    
    public String traductor(String category, String valor){
            System.out.println("RWT: traductor "+category+valor);
            switch(category){
                case "País":
                    switch(valor){
                        case "Estados Unidos": return "US";
                        default: return "MX";
                    }                
                case "Estado":
                    switch(valor){
                        case "Nuevo León":  return "NL";
                        default: return "CDM";
                    }
                case "InstitucionMedico":
                    switch(valor){
                        case "AXA Assistance":  return "1";
                        default: return "0";
                    }
                default: return "Categoría de traducción incorrecta";
            }    
    }
}
