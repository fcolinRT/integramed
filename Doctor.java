package com.app.ws;
//import java.util.*;

public class Doctor {
    public String Code;
    public String Name;
    public String U_Nombre;
    public String U_MedApPat;
    public String U_MedApMat;
    public String U_ClaveMedico;
    public String U_EspecialidadMed;
    public String U_EstadoRep;
    public String U_Poblacion;
    public String U_Institucion;
    public String U_TelMedico;
    public String firma;
    public String CedulaProf;
        
    public Doctor(String Code, String Name, String U_Nombre, String U_MedApPat, String U_MedApMat, String U_ClaveMedico, String U_EspecialidadMed, String U_EstadoRep, String U_Poblacion, String U_Institucion, String U_TelMedico, String firma, String CedulaProf) {
      this.Code = Code; 
      this.Name = Name;
      this.U_Nombre = U_Nombre; 
      this.U_MedApPat = U_MedApPat;
      this.U_MedApMat = U_MedApMat;
      this.U_ClaveMedico = U_ClaveMedico;
      this.U_EspecialidadMed = U_EspecialidadMed;
      this.U_EstadoRep = U_EstadoRep;
      this.U_Poblacion = U_Poblacion;
      this.U_Institucion = U_Institucion;
      this.U_TelMedico = U_TelMedico;
      this.firma = firma;
      this.CedulaProf = CedulaProf;
    }
}
