import subprocess
import shutil
import test_endpoints
import traceback
import sys

def start_services():
	subprocess.run(["docker-compose", "up", "-d", "--build"], check=True)

def stop_services():
	subprocess.run(["docker-compose", "down", "--volumes"], check=True)

def print_logs():
	with open("test_server.log", 'w') as f:
		subprocess.run(["docker", "logs", "csimsoftwww_csimsoftwww_1"], stdout=f, stderr=f)
	with open("test_database.log", 'w') as f:
		subprocess.run(["docker", "logs", "csimsoftwww_csimsoftdb_1"], stdout=f, stderr=f)

def run():
	exit = False
	try:
		start_services()
		test_endpoints.test("http://localhost:1357/account")
		print("TESTS PASSED")
	except Exception:
		exit = True
		traceback.print_exc()
		print_logs()

	stop_services()
	if exit:
		sys.exit(1)

if __name__ == "__main__":
	run()

	
