# Creates a NewTrialSignup Platform Event in Salesforce
import requests
import json
import time

username = "dallin@coreform.com"
password = "bnNgwy9qUbsEIvMtCTCikhLzP5uGZvFJXN"
url = "https://login.salesforce.com/services/oauth2/token"
clientid = "3MVG9QDx8IX8nP5TDJ7Toy9o4lzexMAXWoxpB_nrAt3YajwqPh7hp2KQLcgXpynng9WWxbKI23A0GnC_y2Hme"
clientsecret = "90AC46A82538134D891664DFDF5396DE1F292FCC48858ADEA7B7188615597CD1"

data = {'grant_type': 'password', 'client_id': clientid, 'client_secret': clientsecret, 'username': username, 'password': password}

resp = requests.post(url, data=data)
print(resp.json())
resp.raise_for_status()
jsonBody = resp.json()
accessToken = jsonBody["access_token"]
instanceUrl = jsonBody["instance_url"]

event = {'Address__c': '123 NotReal St', 'Company__c': 'Coreform', 'Country__c': 'USA', 'Industry__c': 'education', 'Email__c': 'wilcox.scot@gmail.com', 'First_Name__c': 'Scot', 'Last_Name__c': 'Wilcox', 'Phone__c': '123-123-1234'}
url = instanceUrl + "/services/data/v48.0/sobjects/NewTrialSignup__e"
resp = requests.post(url, json=event, headers={"Authorization": "Bearer " + accessToken})
print(resp.json())
resp.raise_for_status()

time.sleep(5)

actevent = {'Email__c': 'wilcox.scot@gmail.com', 'Expiration__c': '30', 'First_Name__c': 'Scot', 'Last_Name__c': 'Wilcox', 'Product__c': 'Trelis 17.1.0 Trial'}
acturl = instanceUrl + "/services/data/v48.0/sobjects/New_Trial_Activation__e"
actresp = requests.post(acturl, json=actevent, headers={"Authorization": "Bearer " + accessToken})
print(actresp.json())
actresp.raise_for_status()




