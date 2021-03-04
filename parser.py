from . import routes
from utils import *
from flask import jsonify, request, Response
from pprint import pprint
import json
import os

SALESFORCE_USERNAME = os.environ['SALESFORCE_USERNAME']
SALESFORCE_PASS = os.environ['SALESFORCE_PASS']
SALESFORCE_ORG = os.environ['SALESFORCE_ORG']
SALESFORCE_SECURITY_TOKEN = os.environ['SALESFORCE_SECURITY_TOKEN']
SALESFORCE_QA = os.environ['SALESFORCE_QA']

@routes.route('/parser/<salesforceObject>', methods=['GET', 'POST'])
def parse(salesforceObject):
    sf = SF({"username": SALESFORCE_USERNAME, "password": SALESFORCE_PASS, "org": SALESFORCE_ORG, "isQA": SALESFORCE_QA, "security_token": SALESFORCE_SECURITY_TOKEN})
    if request.method == 'POST':
        data = request.get_json()
        if type(data) is str :
                data = json.loads(data)
        if 'resource' in data.keys():
            print("RWT: Beetrack Request: ")
            if data['resource'] == 'dispatch' :
                print(json.dumps(data, indent=4, separators=(", ", " = ")))
                print("RWT: Dispatch ")
                salesforceObject = 'Dispatch__c'
                reponse_id = sf.sendBT(salesforceObject, data, False)
                return Response(status=200)
            if data['resource'] == 'route' :
                print(json.dumps(data, indent=4, separators=(", ", " = ")))
                print("RWT: Ruta ")
                salesforceObject = 'Route__c'
                reponse_id = sf.sendBT(salesforceObject, data, True)
                return Response(status=200)
            if data['resource'] == 'dispatch_guide' :
                print(json.dumps(data, indent=4, separators=(", ", " = ")))
                print("RWT: Creación Dispatch Guide: SKIP ")
                return Response(status=200)   
            if data['resource'] == 'review' :
                print(json.dumps(data, indent=4, separators=(", ", " = ")))
                print("RWT: Creación review: SKIP ")
                return Response(status=200)  
        print(json.dumps(data, indent=4, separators=(", ", " = ")))
        print("RWT: SAP Request: ")
        objectData = data['data']
        reponse_id = sf.send(salesforceObject, objectData)
        if ('subOject' in data):
            print("RWT: Contiene SubObject")
            data['subOject'][0]['data']['ORDERID'] = reponse_id
            print(data['subOject'][0])
            sf.send(data['subOject'][0]['objectName'], [data['subOject'][0]['data']])
        return jsonify(data)
    if request.method == 'GET':
        print("RWT Get Method")
        print("RWT request.args"+str(request.args.get("Id")))
        sf.sendApprove(salesforceObject, request.args.get("Id"),request.args.get("approve"))
        return "Su respuesta ha sido enviada a Salesforce, gracias."
