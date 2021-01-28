from dateutil.parser import parse
from dateutil.relativedelta import relativedelta
from datetime import date

class CustomFunc:
    def callOne(self, funcName, data):
        print("RWT CallOne "+funcName)
        return self.funcs[funcName](data)
        
    def is_int(self, string):
        try:
            int(string)
            return True
        except ValueError:
            return False

    def is_date(self, string, fuzzy=False):
        try:
            parse(string, fuzzy=False)
            return True
        except ValueError:
            return False

    def parseBoolean(self, data, value, key, checkMarkFields):
        if (value == 'A' or value == 'S' or value == 'a' or value == 's'):
            data[key] = True
        elif (value == 'N' or value == 'n' or value == 'I' or value == 'i'):
            data[key] = False
        elif ( (key in checkMarkFields) and value == None):
            data[key] = False 
        return data

    def case(self, data):
        for key, value in data.items():
            print("RWT case Key = "+key+" value = "+value)
            if (key == 'DOCSTATUS' and value == 'C'):
                data[key] = 'Cerrado'
            if (key == 'DOCSTATUS' and value == 'O'):
                data[key] = 'En Proceso'
        return data
    
    def opportunity(self, data):
        for key, value in data.items():
            if (key == 'DOCSTATUS' and value == 'C'):
                data[key] = 'Facturada'
            #elif (key == 'DOCSTATUS' and value == 'Cancel'):
            #    data[key] = 'Cancelada'
        return data
        
    def product(self, data):
        for key, value in data.items():
            if (value == 'Y'):
                data[key] = True
            if (value == 'N'):
                data[key] = False
            if (key == 'TaxCodeAR' and value == 'B0'):
                data[key] = True
            elif(key == 'TaxCodeAR'):
                data[key] = False
        return data
        
    def validation(self, data):
        flag = False
        toSalesforce = {}
        for key, value in data.items():
            if (key =='QryGroup10'):
                print('RWT: ESTE ES EL VALOR DE QryGroup10 ')
                print (value)
                toSalesforce['SelloFlag__c'] = True
                flag = True
            if (key =='QryGroup14' and value):
                print('RWT: ESTE ES EL VALOR QryGroup14 ')
                print (value)
                toSalesforce['NAFlag__c'] = True
                flag = True
            if (key =='QryGroup15' and value):
                print('RWT: ESTE ES EL VALOR QryGroup15 ')
                print (value)
                toSalesforce['CATFlag__c'] = True
                flag = True
        return flag,toSalesforce
               
    def __init__(self):
        self.funcs = {
            "Case": self.case,
            "Opportunity": self.opportunity,
            "Product2": self.product,
            "Validation__c": self.validation
        }

