import requests
import argparse
import time
import sys

def check_if_up(url):
    resp = requests.head(url)
    if resp:
        return True
    else:
        return False

def run(url, timeout):
    end = time.time() + timeout
    while time.time() < end:
        try:
            if check_if_up(url):
                return True
        except:
            pass
        time.sleep(1)
    
    return False

if __name__ == "__main__":
    parser = argparse.ArgumentParser(description="Wait for URL to be up")
    parser.add_argument("url", type=str, help="URL to check")
    parser.add_argument("--timeout", type=int, help="How long to wait", default=30)
    args = parser.parse_args()
    if run(args.url, args.timeout):
        print(args.url + " is up")
    else:
        print(args.url + " is not up after " + str(args.timeout) + " seconds")
        sys.exit(1)

    