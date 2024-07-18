import requests
import argparse
import wait_until_up
import time

def find_extract_substring(text, start_find, end_find, start_index=0):
	index = text.find(start_find, start_index)
	if index == -1:
		return None, -1
	mid_index = index + len(start_find)
	close_index = text.find(end_find, mid_index)
	if close_index == -1:
		return None, -1
	return text[mid_index:close_index], close_index

def find_error_or_warn(text):
	message, index = find_extract_substring(text, '<td class="error-msg">', '</td>')
	if not message:
		message, index = find_extract_substring(text, '<td class="warn-msg">', '</td>')
	return message

def get_info_msg(text):
	message, index = find_extract_substring(text, '<td class="info-msg">', '</td>')
	return message

def throw_on_error(text):
	error = find_error_or_warn(text)
	if error:
		raise Exception(error)

def submit_form(session, host, subpath, form):
	url = host + subpath
	r = session.post(url, form)
	r.raise_for_status()
	return r.content.decode()

def get_page(session, host, subpath):
	url = host + subpath
	r = session.get(url)
	r.raise_for_status()
	return r.content.decode()

def create_account(session, host):
	form = {
		"first": "Dummy",
		"last": "Dummy",
		"email": "dummy@dummy.com",
		"pass": "testpass",
		"confirm": "testpass",
		"company": "Coreform",
		"phone": "123-123-1234",
		"address": "123 NotReal street",
		"country": "United States",
		"ind_or_edu": "industry",
		"ind_specific": "Development",
	}
	text = submit_form(session, host, "/adduser", form)
	throw_on_error(text)
	assert("Thank you for signing up at coreform.com!" in text)
	text = submit_form(session, host, "/adduser", form)
	assert("The specified email address already has an associated account." in find_error_or_warn(text))

def login_as_admin(session, host):
	form = {
		"user": "tester@coreform.com",
		"pass": "testpass"
	}
	text = submit_form(session, host, "/login", form)
	throw_on_error(text)

def login_as_user(session, host):
	form = {
		"user": "dummy@dummy.com",
		"pass": "testpass"
	}
	text = submit_form(session, host, "/login", form)
	throw_on_error(text)

def logout(session, host):
	session.post(host + "/logout")

def create_licenses(session, host):
	def create_license(name, version):
		form = {
			"licensename": name,
			"version": version
		}
		text = submit_form(session, host, "/newlicense", form)
		throw_on_error(text)
		assert("The new license has been successfully added." in get_info_msg(text))
	create_license("Coreform Cubit Free Trial", "dynamic")
	create_license("Coreform Cubit Learn", "dynamic")
	create_license("Coreform Cubit", "dynamic")

def add_features(session, host):
	def add_feature( name, key ):
		form = {
			"featurename": name,
			"featurekey": key 
		}
		text = submit_form(session, host, "/newfeature", form)
		throw_on_error(text)
		assert("The new product feature has been successfully added." in get_info_msg(text))
	add_feature( "Trelis Core", "trelis_core" )
	add_feature( "Trelis GUI", "trelis_gui" )
	add_feature( "Trelis CFD", "trelis_cfd" )
	add_feature( "Trelis FEA", "trelis_fea" )
	add_feature( "Trelis Sculpt", "trelis_sculpt" )

def add_features_to_license(session, host):
	def add_feature_to_license( licenseid, featureid ):
		form = {
			"license": licenseid,
			"feature": featureid
		}
		text = submit_form(session, host, "/newlicensefeature", form)
		throw_on_error(text)
		assert("The license feature has been successfully added." in get_info_msg(text))
	add_feature_to_license( 1, 1 )
	add_feature_to_license( 1, 2 )
	add_feature_to_license( 1, 3 )
	add_feature_to_license( 1, 4 )
	add_feature_to_license( 1, 5 )
	add_feature_to_license( 3, 1 )
	add_feature_to_license( 3, 2 )
	add_feature_to_license( 3, 3 )
	add_feature_to_license( 3, 4 )
	add_feature_to_license( 3, 5 )

def new_account_trial(session, host):
	form = {
		"first": "Dummy",
		"last": "Dummy",
		"email": "dummytrial@dummy.com",
		"pass": "testpass",
		"confirm": "testpass",
		"company": "Coreform",
		"phone": "123-123-1234",
		"address": "123 NotReal street",
		"country": "United States",
		"ind_or_edu": "industry",
		"ind_specific": "Development",
		"license": "1",
		"days": "30"
	}
	text = submit_form(session, host, "/addusertrial", form)
	throw_on_error(text)
	assert("Thank you for signing up for your free trial!" in get_info_msg(text))
	text = submit_form(session, host, "/addusertrial", form)
	assert("The specified email address already has an associated account." in find_error_or_warn(text))

def new_account_learn(session, host):
	form = {
		"first": "Dummy",
		"last": "Dummy",
		"email": "dummylearn@dummy.com",
		"pass": "testpass",
		"confirm": "testpass",
		"company": "Coreform",
		"phone": "123-123-1234",
		"address": "123 NotReal street",
		"country": "United States",
		"ind_or_edu": "industry",
		"ind_specific": "Development",
		"planneduse": "Testing",
		"license": "1",
		"days": "30"
	}
	text = submit_form(session, host, "/adduserlearn", form)
	throw_on_error(text)
	assert("Thank you for signing up for Cubit Learn!" in get_info_msg(text))
	text = submit_form(session, host, "/adduserlearn", form)
	assert("The specified email address already has an associated account." in find_error_or_warn(text))

def new_client_licenses(session, host):
	def new_client_license(user, license):
		form = {
			"user": user,
			"license": license
		}
		text = submit_form(session, host, "/newclientlicense", form)
		throw_on_error(text)
		assert("The client license has been successfully added." in get_info_msg(text))
	new_client_license(4, 3)

def activate_licenses(session, host):
	def get_license_product_keys():
		def extract_license_ids():
			ids = []
			text = get_page(session, host, "/licenses")
			throw_on_error(text)
			def extract_license_id(index):
				id, close_index = find_extract_substring( text, 'licensedetail?license=', '"', index)
				if id:
					ids.append(int(id))
				return close_index
			index = 0
			while index != -1:
				index = extract_license_id(index)
			return ids

		def get_license_product_key(license_id):
			text = get_page(session, host, "/licensedetail?license=" + str(license_id))
			throw_on_error(text)
			def extract_product_key():
				dirty_key, close_index = find_extract_substring(text, "<strong>Product Key</strong></td>", "</td>")
				key = dirty_key.strip().replace("<td>", "")
				return key
			key = extract_product_key()
			if not key:
				assert("Coreform Cubit Learn" in text)
			return key

		ids = extract_license_ids()
		return list(map(get_license_product_key, ids))
	
	keys = get_license_product_keys()
	def activate_license(key, hostid, hostname):
		form = {
			"hostid": hostid,
			"hostname": hostname,
			"licensekey": key
		}
		text = submit_form(session, host, "/activatelicense", form)
		throw_on_error(text)
		assert("ISV csimsoft port=5055" in text)
	for key in keys:
		activate_license(key, "a4bb6d3de4e5", "test")



def test(host):
	wait_until_up.run(host, 30)
	time.sleep(30)
	session = requests.Session()
	login_as_admin(session, host)
	create_licenses(session, host)
	add_features(session, host)
	add_features_to_license(session, host)
	logout(session, host)
	new_account_trial(session, host)
	logout(session, host)
	new_account_learn(session, host)
	logout(session, host)
	create_account(session, host)
	logout(session, host)
	login_as_admin(session, host)
	new_client_licenses(session, host)
	logout(session, host)
	login_as_user(session, host)
	activate_licenses(session, host)

if __name__ == "__main__":
	parser = argparse.ArgumentParser()
	parser.add_argument("host", type=str)

	args = parser.parse_args()
	test(args.host)
