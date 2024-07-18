# python 3
#
# Run this script to generate release notes in a markdown file in the release_notes folder.
# The sprints must be still open in JIRA when you run this script.
#

## Import Libraries
import os
import requests
from datetime import datetime
import argparse

IGX_URL = 'https://gitlab.coreform.com/api/v4/projects/1/repository'
SIMMESH_URL = 'https://gitlab.coreform.com/api/v4/projects/27/repository'

# This only has access to the read api
IGX_ACCESS_TOKEN = "g8BzSf__mThamBAMpT-F"
SIMMESH_ACCESS_TOKEN = "GsZrh5wVip-XnKe_Yfa-"


class IgxRepo:
    url = IGX_URL
    token = IGX_ACCESS_TOKEN

class SimMeshRepo:
    url = SIMMESH_URL
    token = SIMMESH_ACCESS_TOKEN

def get(repo, suffix, no_append=False):
    url = ''
    if no_append:
        url = suffix
    else:
        url = repo.url + suffix

    resp = requests.get(url, headers={"PRIVATE-TOKEN": repo.token})
    resp.raise_for_status()
    return resp.headers, resp.json()

def get_date_of_tag(repo, tag):
    tag_url = f"/tags/{tag}"
    headers, json = get(repo, tag_url)
    return json["commit"]["committed_date"]

def get_pagination_link(headers):
    if "Link" in headers:
        comma_splits = headers["Link"].split(",")
        for comma_split in comma_splits:
            if 'rel="next"' in comma_split:
                semi_splits = comma_split.split(";")
                # Remove whitespace and <> brackets
                return semi_splits[0].strip()[1:-1]
    else:
        return None

def get_commits(repo, since_date, until_date=None):
    print(f"Getting commits since {since_date}")
    commit_url = f"/commits?&since={since_date}"
    if until_date:
        print(f"Getting commits until {until_date}")
        commit_url += f"&until={until_date}"
    headers, json = get(repo, commit_url)
    next_link = get_pagination_link(headers)
    while next_link:
        headers, next_json = get(repo, next_link, no_append=True)
        json.extend(next_json)
        next_link = get_pagination_link(headers)
    return json

# If second_tag isn't passed in, today is used
def get_commits_between_tags(repo, first_tag, second_tag=None):
    since_date = get_date_of_tag(repo, first_tag)
    until_date = None
    if second_tag:
        until_date = get_date_of_tag(repo, second_tag)

    return get_commits(repo, since_date, until_date), since_date, until_date

def sort_commit_messages(commits):
    sorted = {}
    for commit in commits:
        message = commit["message"]
        colon_splits = message.split(":")
        if len(colon_splits) > 1:
            label = colon_splits[0]
            newline_splits = colon_splits[1].split("\n")
            if len(newline_splits) > 2:
                subject = newline_splits[0]
                body = " ".join(newline_splits[2:])
                if label not in sorted:
                    sorted[label] = []
                sorted[label].append((subject, body))
    return sorted

def write_release_notes(sorted, version):
    now = datetime.now()
    ## Output to file
    script_path = os.path.abspath(os.path.dirname(__file__))
    release_notes_dir = f'content/products/coreform-cubit/release_notes/{version}'
    os.makedirs(release_notes_dir, exist_ok=True)
    release_notes_file = release_notes_dir + '/index.md'
    print(f"Writing release notes to {release_notes_file}")
    truncated_version = version[1:]

    with open(release_notes_file, 'w') as f:
        def write_commit(subject, body, defect=False):
            fixed_str = "  ***[FIXED]***" if defect else ""
            f.write(f"*{fixed_str} {subject}\n")
            f.write(f"    * {body}\n")

        def write_section(heading, keyword, defect=False):
            if keyword in sorted and len(sorted[keyword]) > 0:
                f.write( '\n' )
                f.write( f'## {heading} in Coreform Cubit {truncated_version}\n' )
                f.write( '\n' )
                for subject, body in sorted[keyword]:
                    write_commit(subject, body, defect)
                f.write( '\n' )
        just_date = now.strftime('%Y-%m-%d')
        f.write( '+++\n' )
        f.write( f'title = "Release Notes for Coreform Cubit {truncated_version}"\n')
        f.write( 'include_collapse = true\n' )
        f.write( 'layout = "release_notes"\n' )
        f.write( f'date = "{just_date}"\n' )
        f.write( '+++\n' )
        write_section("Features added", "feat")
        write_section("Defects fixed", "fix", True)
        write_section("Performance gains", "perf")
        write_section("Dependencies updated", "bump")


def run(first_tag, second_tag=None):
    print("Getting commits from igx")
    igx = IgxRepo()
    simmesh = SimMeshRepo()
    commits, since_date, until_date = get_commits_between_tags(igx, first_tag, second_tag)
    print(f"Got {len(commits)} commits from igx")
    print("Getting commits from SimMesh")
    sim_commits = get_commits(simmesh, since_date, until_date)
    print(f"Got {len(sim_commits)} commits from SimMesh")
    commits.extend(sim_commits)
    sorted = sort_commit_messages(commits)
    if not second_tag:
        dot_splits = first_tag.split(".")
        last_version_num = int(dot_splits[1])
        next_version_num = last_version_num + 1
        if next_version_num > 12:
            next_version_num = 1
        second_tag = f"{dot_splits[0]}.{str(next_version_num)}"
    write_release_notes(sorted, second_tag)

if __name__ == "__main__":
    parser = argparse.ArgumentParser()
    parser.add_argument("first_tag", type=str, help="The last released version, i.e. v2021.4")
    parser.add_argument("second_tag", type=str, default=None, nargs='?', help="The upcoming version.  Don't include if the tag hasn't been created yet.")

    args = parser.parse_args()
    run(args.first_tag, args.second_tag)