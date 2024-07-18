import requests
import sys
import xml.etree.ElementTree as ET
import argparse
import wait_until_up
import time

def page_exists(url):
    # The server might not be fully up, so do some retries if we get 404 errors
    for i in range(0, 10):
        resp = requests.head(url, timeout=5, allow_redirects=True)
        if resp.status_code != 404:
            break
        time.sleep(1)
    #400 means it expects a POST
    if not resp.ok and resp.status_code != 400:
        print("Url " + url + " returned error " + str(resp.status_code))
        return False
    else:
        return True

def test_pages(root, urls):
    num_failed = 0
    for url in urls:
        if not page_exists(root + url):
            num_failed += 1
    if num_failed > 0:
        print(str(num_failed) + " failed out of " + str(len(urls)))
        return False
    else:
        print("All tests passed!")
        return True

def parse_web_xml(webxml):
    urls = []
    tree = ET.parse(webxml)
    root_node = tree.getroot()
    for child in root_node:
        if child.tag == '{http://java.sun.com/xml/ns/j2ee}servlet':
            for inner in child:
                if inner.tag == '{http://java.sun.com/xml/ns/j2ee}jsp-file':
                    urls.append(inner.text)
        if child.tag == '{http://java.sun.com/xml/ns/j2ee}servlet-mapping':
            for inner in child:
                if inner.tag == '{http://java.sun.com/xml/ns/j2ee}url-pattern':
                    urls.append(inner.text)
    return urls

def run(root, webxml):
    wait_until_up.run(root, 30)
    time.sleep(10)
    urls = parse_web_xml(webxml)
    return test_pages(root, urls)

parser = argparse.ArgumentParser(description="Test URL validity")
parser.add_argument("--root", type=str, help="default base URL", default="http://localhost/account")
parser.add_argument("--webxml", type=str, help="path to web.xml", default="../src/main/webapp/WEB-INF/web.xml")
args = parser.parse_args()
if not run(args.root, args.webxml):
    sys.exit(1)
