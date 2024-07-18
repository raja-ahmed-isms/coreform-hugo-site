import requests

api_key = "7cef18509dd8c829b4bf4e2e886d28d7-us15"

def add_contact(list_id, email, first, last):
    data = {'email_address': email, 'status': 'subscribed', 'merge_fields': { 'FNAME': first, 'LNAME': last }}
    resp = requests.post('https://us15.api.mailchimp.com/3.0/lists/' + list_id + "/members/", auth=('apiuser', api_key), json=data)
    if resp.status_code == 400:
        output = resp.json()
        if output['title'] == 'Member Exists':
            print('Contact already exists')
        else:
            print(output)
    else:
        resp.raise_for_status()


def get_list():
    resp = requests.get('https://us15.api.mailchimp.com/3.0/lists', auth=('apiuser', api_key))
    resp.raise_for_status()
    output = resp.json()
    return output['lists'][0]['id']

def get_tag(list_id):
    resp = requests.get('https://us15.api.mailchimp.com/3.0/lists/' + list_id + '/segments', auth=('apiuser', api_key))
    resp.raise_for_status()
    output = resp.json()
    for segment in output['segments']:
        if segment['name'] == 'Free Trial':
            return segment['id']

    return None

def set_tag(list_id, tag_id, email):
    data = { 'email_address': email }
    resp = requests.post('https://us15.api.mailchimp.com/3.0/lists/' + str(list_id) + "/segments/" + str(tag_id) + "/members", auth=('apiuser', api_key), json=data)
    resp.raise_for_status()
    output = resp.json()
    print(output)

list_id = get_list()
tag_id = get_tag(list_id)
add_contact(list_id, "wilcox.scot@gmail.com", "Scot", "Wilcox")
set_tag(list_id, tag_id, 'wilcox.scot@gmail.com')





