from simple_salesforce import Salesforce
from config import *
import datetime
import pytz
import json
import sys
from dateutil.parser import parse
from dateutil.tz import UTC

class SF:
    def __init__(self, connectionData):
        if (connectionData['isQA'] == 'true'):
            self.sf = Salesforce(domain='test', username=connectionData['username'], password=connectionData['password'], organizationId=connectionData['org'], security_token=connectionData['security_token'])
        else:
            self.sf = Salesforce(username=connectionData['username'], password=connectionData['password'], organizationId=connectionData['org'])
        self.customFunc = CustomFunc()
        
    def send(self, objectName, data):
        config = self.getObjectConfig(objectName)
        primaryKey = config['primaryKey']['salesforceFieldName']
        externalKey = config['externalId']['salesforceFieldName']
        hasMultipleIds = config['multipleIds']
        ids = config['salesforceIds']
        relatedObject = config['relatedObject']
        salesForceSubObject = config['subObject']
        salesForceObject = self.sf.__getattr__(objectName)
        print("RWT: Config")
        print(config)
        for datum in data:
            parsedData = self.parse(datum, config)
            alreadyInSF, results = self.isAlreadyInSF(ids, parsedData, hasMultipleIds, primaryKey, objectName)
            if alreadyInSF:
                objectId = results['records'][0]['Id']
                print("RWT: Update "+str(salesForceObject.update(objectId, parsedData)))
                if(salesForceSubObject != ""):
                    self.subObject('Validation__c',datum,objectId)
                return objectId
            elif (hasMultipleIds):
                if relatedObject:
                    results=self.getRequiredFields(objectName,parsedData,externalKey,primaryKey)
                    reponse_id = salesForceObject.create(results)
                    print("RWT: Create Has Multiple Ids RESULT "+str(reponse_id))
                    return reponse_id
                else:
                    reponse_id = salesForceObject.create(parsedData)
                    print("RWT: Create Has Multiple Ids RESULT "+str(reponse_id))
                    return reponse_id
            elif (self.hasExternalId(config)):
                externalId = config['externalId']['salesforceFieldName'] + '/' + str(datum[config['externalId']['erpFieldName']])
                parsedData.pop(primaryKey, None)
                print("RWT: Upsert Has External Id "+ str(externalId))
                print(salesForceObject.upsert(externalId, parsedData))
                reponse_id = self.sf.query("SELECT Id FROM "+config['salesForceTableName']+" WHERE "+config['externalId']['salesforceFieldName']+" = '" + str(datum[config['externalId']['erpFieldName']]) + "'")
                print("RWT: Response"+str(reponse_id['records'][0]['Id']))
                if(salesForceSubObject != ""):
                    self.subObject('Validation__c',datum,reponse_id['records'][0]['Id'])
                return reponse_id['records'][0]['Id']
            else:
                reponse_id = salesForceObject.create(parsedData)
                print("RWT: Create "+str(reponse_id))
                return reponse_id
    def sendBT(self, objectName, data, checkExisting):
        config = self.getObjectConfig(objectName)
        primaryKey = config['primaryKey']['salesforceFieldName']
        externalKey = config['externalId']['salesforceFieldName']
        hasMultipleIds = config['multipleIds']
        ids = config['salesforceIds']
        relatedObject = config['relatedObject']

        salesForceObject = self.sf.__getattr__(objectName)
        parsedData = self.parse(data, config)
        alreadyInSF, results = self.isAlreadyInSF(ids, parsedData, hasMultipleIds, primaryKey, objectName)
        if alreadyInSF and checkExisting:
            objectId = results['records'][0]['Id']
            print("RWT: Update")
            if (self.hasExternalId(config)):
                externalId = config['externalId']['salesforceFieldName'] + '/' + str(data[config['externalId']['erpFieldName']])
                parsedData.pop(primaryKey, None)
                print("RWT: Upsert Has External Id "+ str(externalId))
                try:
                    reponse_id = salesForceObject.upsert(externalId, parsedData)
                    print("RWT: Response"+str(reponse_id))
                    return reponse_id['records'][0]['Id']
                except:
                    print("RWT: Falla actualización de registro")
        else:
            try:
                reponse_id = salesForceObject.create(parsedData)
                print("RWT: Create "+str(reponse_id))
                return reponse_id
            except:
                print("RWT: Falla creación de nuevo registro")

    def sendApprove(self, objectName, id, resp):
        config = self.getObjectConfig(objectName)
        primaryKey = "Id"
        hasMultipleIds = config['multipleIds']
        ids = config['salesforceIds']
        relatedObject = config['relatedObject']
        salesForceSubObject = config['subObject']
        salesForceObject = self.sf.__getattr__(objectName)
        parsedData = {}
        parsedData["Id"] = str(id)
        if(str(resp) == "true"):
            parsedData["StageName__c"] = "Ingreso a SAP"        
        else:
            parsedData["Status"] = "Cancelado"      

        print("RWT: alreadyInSF?")
        alreadyInSF, results = self.isAlreadyInSF(ids, parsedData, hasMultipleIds, primaryKey, objectName)
        if alreadyInSF:
            objectId = results['records'][0]['Id']
            print("RWT: Update")
            if (self.hasExternalId(config)):
                externalId = primaryKey + '/' + str(id)
                parsedData.pop(primaryKey, None)
                print("RWT: Upsert Has External Id "+ str(externalId))
                try:
                    reponse_id = salesForceObject.upsert(externalId, parsedData)
                    print("RWT: Response"+str(reponse_id))
                    return reponse_id
                except:
                    print("RWT: Falla actualización de registro")

    def closeApprove(self, objectName, id):
        config = self.getObjectConfig(objectName)
        primaryKey = "Order_ExternalId__c"
        hasMultipleIds = config['multipleIds']
        ids = config['salesforceIds']
        relatedObject = config['relatedObject']
        salesForceSubObject = config['subObject']
        salesForceObject = self.sf.__getattr__(objectName)
        parsedData = {}
        parsedData["Order_ExternalId__c"] = str(id)
        parsedData["Status"] = "Cerrado"      
        print("RWT: Close alreadyInSF?")
        alreadyInSF, results = self.isAlreadyInSF(ids, parsedData, hasMultipleIds, primaryKey, objectName)
        if alreadyInSF:
            objectId = results['records'][0]['Id']
            print("RWT: Update")
            if (self.hasExternalId(config)):
                externalId = 'Id/' + objectId
                parsedData.pop(primaryKey, None)
                print("RWT: Upsert Has External Id "+ str(externalId))
                try:
                    reponse_id = salesForceObject.upsert(externalId, parsedData)
                    print("RWT: Response"+str(reponse_id))
                    return reponse_id
                except:
                    print("RWT: Falla actualización de registro")

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
    
    def getObjectConfig(self, objectName):
        path = "./config/{}.json".format(objectName)
        config = json.load(open(path))
        return config

    def parse(self, data, config):
        print("RWT: Parse "+str(config['hasCustomParsing']))
        toSalesforce = {}
        if (config['hasCustomParsing']):
            data = self.customFunc.callOne(config['salesForceTableName'], data)
        for field in config['fields']:
            value =  data[field['erpFieldName']]
            if (type(value).__name__ == 'Decimal'):
                value = float(value)
            if ( type(value).__name__ == 'LOB' ):
                value = value.read()
            if ( type(value).__name__ == 'str' and not self.is_int(value) and self.is_date(value) ):
                value = parse(value, fuzzy=True).astimezone(UTC).strftime("%Y-%m-%dT%H:%M:%SZ")
            toSalesforce[field['salesforceFieldName']] = value
        print('RWT Parsing Result ') 
        print(toSalesforce)        
        return toSalesforce
    
    def whereSalesforceQuery(self, primaryKeys, obj):
        objKeys = []
        for pk in primaryKeys:
            print('whereSalesforceQuery: '+pk)
            tempString = "{}='{}'".format(pk, str(obj[pk]) )
            print('whereSalesforceQuery: '+tempString)
            objKeys.append(tempString)
        return " WHERE {}".format(" AND ".join(objKeys))

    def isAlreadyInSF(self, ids, toSales, hasMultipleIds, primaryKey, salesForceName):
        print('RWT: isAlreadyInSF??'+str(toSales)+str(primaryKey))
        primaryKeyValue = str(toSales[primaryKey])
        if (isinstance(toSales[primaryKey], str)):
            primaryKeyValue = "'" + primaryKeyValue + "'"
        whereClause = self.whereSalesforceQuery(ids, toSales) if (hasMultipleIds) else " WHERE " + primaryKey + " = " + primaryKeyValue
        query = "SELECT ID FROM {} {}".format(salesForceName, whereClause)
        
        print(query)
        objectInfo = self.sf.query(query)
        if (len(objectInfo['records']) > 0) :
            return True, objectInfo
        return False, objectInfo

    def hasExternalId(self, config):
        if ('externalId' in config.keys()):
            return True
        return False
        
    ### NOT IN USE:
    def getRequiredFields(self, salesforceObjectName, objectData,externalKey,primaryKey):
        parentObject=None
        externalId=str(objectData[externalKey])
        productCode=str(objectData[primaryKey])
        if salesforceObjectName == 'PricebookEntry':
            parentObject="Pricebook2"
            whereClause=" WHERE ExternalId__c = '"+ externalId +"'"
        query = "SELECT ID FROM {} {} ".format(parentObject, whereClause)
        query2 ="SELECT ID FROM Product2 WHERE ProductCode = '"+productCode+"'"
        print(query)
        print(query2)
        objectInfo = self.sf.query(query)
        print(objectData)
        print('RWT: A VER QUE SALE DE ESTO YA ACTUALIZADO ')
        objectData['Pricebook2Id'] = objectInfo['records'][0]['Id']
        print(objectData)
        objectInfo = self.sf.query(query2)
        print('RWT: A VER QUE SALE DE ESTO YA ACTUALIZADO ')
        objectData['Product2Id'] = objectInfo['records'][0]['Id']
        print(objectData)        
        return objectData
    
    def subObject(self, salesforceSubObjectName, objectData, productId):
        salesforceSubObject = self.sf.__getattr__(salesforceSubObjectName)
        accountData = self.getAccount()
        for key,value in accountData.items():
            alreadyInSF = False
            createFlag = False
            toSalesforce = {}
            query="SELECT ID FROM Validation__c WHERE ProductId__c='"+productId+"' AND AccountId__c='"+value+"'"
            objInfo=self.sf.query(query)
            toSalesforce['ProductId__c']=productId
            toSalesforce['AccountId__c']=value
            if(len(objInfo['records'])>0):
                print('ESTO ES LO QUE HAY')
                alreadyInSF = True
            if(key == 'NAFIN' and (objectData['QryGroup10'] or objectData['QryGroup14'] or objectData['QryGroup15'])):
                toSalesforce['SelloFlag__c'] = objectData['QryGroup10']
                toSalesforce['NAFlag__c'] = objectData['QryGroup14']
                toSalesforce['CATFlag__c'] = objectData['QryGroup15']
                createFlag = True
            if(key == 'HSBC' and (objectData['QryGroup11'] or objectData['QryGroup12'])):
                toSalesforce['NAFlag__c'] = objectData['QryGroup11']
                toSalesforce['CATFlag__c'] = objectData['QryGroup12']
                createFlag = True
            if(key == 'BANCOMEXT' and (objectData['QryGroup13'] or objectData['QryGroup23'])):
                toSalesforce['NAFlag__c'] = objectData['QryGroup13']
                toSalesforce['CATFlag__c'] = objectData['QryGroup23']
                createFlag = True
            #if(key == 'CONDUSEF' and (objectData['QryGroup16'] or objectData['QryGroup17'])):
            #    toSalesforce['NAFlag__c'] = objectData['QryGroup16']
            #    toSalesforce['CATFlag__c'] = objectData['QryGroup17']
            #    createFlag = True
            if(key == 'Santander' and (objectData['QryGroup18'] or objectData['QryGroup19'])):
                toSalesforce['NAFlag__c'] = objectData['QryGroup18']
                toSalesforce['CATFlag__c'] = objectData['QryGroup19']
                createFlag = True
            if(key == 'Fonatur' and (objectData['QryGroup20'] or objectData['QryGroup21'])):
                toSalesforce['NAFlag__c'] = objectData['QryGroup20']
                toSalesforce['CATFlag__c'] = objectData['QryGroup21']
                createFlag = True
            if(key == 'Banorte' and objectData['QryGroup22']):
                toSalesforce['NAFlag__c'] = objectData['QryGroup22']
                createFlag = True
            if(key == 'Banobras' and (objectData['QryGroup24'] or objectData['QryGroup25'])):
                toSalesforce['NAFlag__c'] = objectData['QryGroup24']
                toSalesforce['CATFlag__c'] = objectData['QryGroup25']
                createFlag = True
            #if(key == 'SAE' and (objectData['QryGroup26'] or objectData['QryGroup27'])):
            #    toSalesforce['NAFlag__c'] = objectData['QryGroup26']
            #    toSalesforce['CATFlag__c'] = objectData['QryGroup27']
            #    createFlag = True
            if((key == 'AME_BANK' or key == 'AME_SERVICIOS' or key == 'AME_COMPANY') and objectData['QryGroup28']):
                toSalesforce['NAFlag__c'] = objectData['QryGroup28']
                createFlag = True
            if(key == 'Vitamedica' and objectData['QryGroup29']):
                toSalesforce['NAFlag__c'] = objectData['QryGroup29']
                createFlag = True
            #if(key == 'Metlife' and (objectData['QryGroup34'] or objectData['QryGroup35'])):
            #    toSalesforce['NAFlag__c'] = objectData['QryGroup39']
            #    toSalesforce['CATFlag__c'] = objectData['QryGroup40']
            #    createFlag = True
            #if(key == 'Metlife' and (objectData['QryGroup36'] or objectData['QryGroup37'] or objectData['QryGroup38'])):
            #    toSalesforce['NAFlag__c'] = objectData['QryGroup36']
            #    toSalesforce['CATFlag__c'] = objectData['QryGroup37']
            #    toSalesforce['SelloFlag__c'] = objectData['QryGroup38']
            #    createFlag = True
            if(key == 'Metlife' and (objectData['QryGroup39'] or objectData['QryGroup40'])):
                toSalesforce['NAFlag__c'] = objectData['QryGroup39']
                toSalesforce['CATFlag__c'] = objectData['QryGroup40']
                createFlag = True
            
            if(alreadyInSF):
                print('VA A ACTUALIZAR')
                print(toSalesforce)
                print("RWT: Update "+str(salesforceSubObject.update(objInfo['records'][0]['Id'], toSalesforce)))                
            elif(createFlag):
                print('VA A CREAR REGISTRO')
                print(toSalesforce)
                reponse_id = salesforceSubObject.create(toSalesforce)
                print("RWT: Create Has Multiple Ids RESULT "+str(reponse_id))
            #flag,data = self.customFunc.callOne("Validation__c", objectData)
            #print (key)
            #print (data)
            #print (flag)
        
    def getAccount(self):
        accountIds = {}
        query = "SELECT ID FROM ACCOUNT WHERE NAME= '"
        queryInst = query + "NAFIN'"
        objInfo=self.sf.query(queryInst)
        accountIds['NAFIN']=objInfo['records'][0]['Id']
        queryInst = query + "HSBC'"
        objInfo=self.sf.query(queryInst)
        accountIds['HSBC']=objInfo['records'][0]['Id']
        queryInst = query + "BANCOMEXT'"
        objInfo=self.sf.query(queryInst)
        accountIds['BANCOMEXT']=objInfo['records'][0]['Id']
        queryInst = query + "BANCOMEXT'"
        objInfo=self.sf.query(queryInst)
        accountIds['BANCOMEXT']=objInfo['records'][0]['Id']
        #queryInst = query + "CONDUSEF'"
        #objInfo=self.sf.query(queryInst)
        #accountIds['CONDUSEF']=objInfo['records'][0]['Id']
        queryInst = query + "Santander'"
        objInfo=self.sf.query(queryInst)
        accountIds['Santander']=objInfo['records'][0]['Id']
        queryInst = query + "Banorte'"
        objInfo=self.sf.query(queryInst)
        accountIds['Banorte']=objInfo['records'][0]['Id']
        queryInst = query + "Banobras'"
        objInfo=self.sf.query(queryInst)
        accountIds['Banobras']=objInfo['records'][0]['Id']
        queryInst = query + "Fonatur'"
        objInfo=self.sf.query(queryInst)
        accountIds['Fonatur']=objInfo['records'][0]['Id']
        #queryInst = query + "SAE'"
        #objInfo=self.sf.query(queryInst)
        #accountIds['SAE']=objInfo['records'][0]['Id']
        queryInst = query + "AME_BANK'"
        objInfo=self.sf.query(queryInst)
        accountIds['AME_BANK']=objInfo['records'][0]['Id']
        queryInst = query + "AME_SERVICIOS'"
        objInfo=self.sf.query(queryInst)
        accountIds['AME_SERVICIOS']=objInfo['records'][0]['Id']
        queryInst = query + "AME_COMPANY'"
        objInfo=self.sf.query(queryInst)
        accountIds['AME_COMPANY']=objInfo['records'][0]['Id']
        queryInst = query + "Vitamedica'"
        objInfo=self.sf.query(queryInst)
        accountIds['Vitamedica']=objInfo['records'][0]['Id']
        #queryInst = query + "Banjercito'"
        #objInfo=self.sf.query(queryInst)
        #accountIds['Banjercito']=objInfo['records'][0]['Id']
        #queryInst = query + "CNBV'"
        #objInfo=self.sf.query(queryInst)
        #accountIds['CNBV']=objInfo['records'][0]['Id']
        queryInst = query + "Metlife'"
        objInfo=self.sf.query(queryInst)
        accountIds['Metlife']=objInfo['records'][0]['Id']
        print(accountIds)
        return accountIds
        
    
    def getAllFields(self, obj):
        fields = []
        for x in obj:
            fields.append(x['name'])
        return fields, ','.join(fields)

    def getFullData(self, modifided, fieldsString, salesForceName):
        objectInfo = None
        data = []
        for objectId in modifided['ids']:
            query = "SELECT " + fieldsString + " FROM " + salesForceName + " WHERE ID = '" + objectId + "'"
            objectInfo = self.sf.query(query)
            objectInfo = objectInfo['records'][0]
            data.append(objectInfo)
        return data

    def getRecordsToUpdate(self, objectName):
        queryTime = 864000
        salesForceObject = self.sf.__getattr__(objectName)
        fields, fieldsString = self.getAllFields(salesForceObject.describe()['fields'])
        end = datetime.datetime.now(pytz.UTC)
        modifided = salesForceObject.updated(end - datetime.timedelta(seconds=queryTime), end)
        modifided = self.getFullData(modifided, fieldsString, objectName)
        return modifided
    



