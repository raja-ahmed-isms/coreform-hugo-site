# Contributing using the GitLab WebIDE
Note that some of these steps are not strictly necessary, but for clarity and generality do the following: 

1. Make a new branch with a meaningful name (for example by clicking [here](https://gitlab.coreform.com/igx/coreform-website/-/branches/new))
2. Note that once you create the branch you will be redirected to a page with at least two buttons of interest: `Create Merge Request` and `WebIDE`. One way you can find your way back to this page is by searching for the branch name [here](https://gitlab.coreform.com/igx/coreform-website/-/branches).
3. Begin making your changes by clicking the `WebIDE`button and navigating to the file you want to edit using the panel on your left.
4. The changes you make are automatically accumulated and once you have a logical set of changes you can commit them using the `Commit` button in the left panel. Give a brief description of the change and why it was necessary.
5. If you have more changes you want to add you can repeat the above two steps until you are finished making changes and are ready to submit your work for review.
6. Create a merge request (MR) by going [here](https://gitlab.coreform.com/igx/coreform-website/-/merge_requests). Often, if you have recently pushed to a branch, a shortcut for creating a MR for it will appear at the top of this page. If it doesn't, simply click the `New Merge Request`button and tell it the name of branch you are using as the source of the MR and continue with the process until the MR has been created.
7. Once your MR has been created you may optionally hit the `Deploy` button to preview it on https://staging.coreform.com . However keep in mind that the staging environment is primarily a tool for the MR reviewer and is shared with the other MR (e.g., only the most recently deployed MR will appear on staging). If you need to preview the site frequently then you should develop locally using the instructions below. 
8. A reviewer (e.g., Matt) will then approve and merge your MR, or request changes. To make changes you will want to create a commit on the same branch attached to the MR. You can easily do this by clicking the `Open in WebIDE` button on your MR and then doing steps 4 and 5.

# Contributing locally using your own machine
## One time setup for local development
### Prerequisites 
1. Install [Hugo](https://github.com/gohugoio/hugo/releases).  You'll need the `hugo_extended_<latest version>_<platform>-64bit` version.  
    i. Extract the archive to `<User folder>/hugo`.  
    ii. Add the `hugo` exectuable to [PATH](https://superuser.com/questions/284342/what-are-path-and-other-environment-variables-and-how-can-i-set-or-use-them).
2. Install [git](https://git-scm.com/downloads)
3. Install a code editor of your choice.  If you don't already have one, [VS Code](https://code.visualstudio.com/download) is a user-friendly choice.  

### Generate a SSH Key for Gitlab
1. Open a terminal and run `ssh-keygen`.  Hit enter on every question, which will give you the defaults.  
2. Open `<User folder>/.ssh/id_rsa.pub` and copy the contents.
3. On gitlab.coreform.com, click your profile picture on the top right and select `Settings`.  
4. Click `SSH Keys` on the left, then paste the contents of `id_rsa.pub` in the textbox.  Click `Add Key`.  What this does is register the computer you're working on with Gitlab and marks it as trusted.  Without this step you can't download the code.  

### Configure Git
1. Open a terminal and run `git config --global user.name "<your name>"`
2. Run `git config --global user.email "<your email>"`

### Get the code
1. Open a terminal and navigate to the directory into which you want to put the folder containing the website code. Next, run `git clone git@gitlab.coreform.com:igx/coreform-website.git`.  This will create within the directory a folder called `coreform-website` with the website folders, files, and code in it.
2. Open `coreform-website` in your code editor.

## Editing workflow
There are any number of valid workflows to edit locally. However the instructions below offer a minimal and easily reproduceable approach. As you learn more you will probably want to adapt this workflow. 

### Seeing your changes locally
1. Open a terminal in `coreform-website` and run `hugo server`.  This will build a local copy of the website that you can see in your browser.  It'll take a while the first time.
2. In the output of `hugo server` there will be a link that will look something like this: `http://localhost:1313`. Copy and paste that URL into your browser, and you'll see the website.  This website is not connected in any way to https://coreform.com, it's only based on your code on your computer.
3. Whenever you save a change a website file in `coreform-website`, `hugo` will rebuild the website and your browser will automatically refresh.

### Pushing changes to Gitlab
Again there are many ways to do this. Feel free to adapt to what works best for you. The main constraint is that you must push to your own branch, not the default one (origin/develop).

For each step below an example is provided first using the git CLI and then the VSCode source control panel. For the CLI it is expected that you have `cd`'d into the repository directory already.

#### Example
0. Make sure that you are up to date with the upstream repository
  - `git fetch`
  - `F1` -> "Git: fetch" 

1. Make a new branch for your work
  - `git checkout -b <your-branch-name> origin/development`
  - `F1` -> "Git: checkout to..." -> "Create new branch from..." -> "<your-branch-name>" -> "origin/development" and give

2. Make your changes and stage them (`git status` shows the changes)
  - `git add -p` and/or `git add <file>`
  - use the source control panel on the left to select which changes you want to stage for the commit

3. Commit your changes 
  - `git commit -m "<description of what you did>"`.
  - use the source control panel to add a commit message followed by Ctrl+Enter

4. Push your changes as a new branch on GitLab 
  - `git push -u origin <your-branch-name>`
  - `F1`-> "Git: push"

5. Create a merge request. Note that in the output of the previous command there will be a link to quickly create a merge request for your new branch). You may also simply use steps 6-8 from the GitLab WebIDE instructions to create a merge request if you are using a graphical git client.


### Deploying to staging
After website changes have passed pipelines and been merged to master, the next step is to deploy the current master contents to staging.coreform.com for final review before pushing to the live public website. To do this:
1. In a terminal, navigate to the cf/cubit repo
2. Check out the master branch and run `git pull`
3. In the repo root directory (cf/cubit/), run the command `./build pipeline website`
4. In a browser, go to the gitlab interface for the cf/cubit repo and click the **CI/CD > Pipelines** link on the left side panel
5. On the Pipelines page, you should see a new, two-stage pipeline running or pending. The two stages should be "dev-test" and "deploy". 
6. When both stages are complete, navigate to staging.coreform.com in a browser to perform a final review of the changes made to the website.

 
### Deploying to public website
If the final review is satisfactory, deploy the changes viewed in staging to the public website: 
1. In a browser, navigate once again to the **CI/CD > Pipelines** gitlab page for the cf/cubit repo.
2. To the right of the completed, two-stage staging deployment pipeline, you should see a "play"-type button featuring a right-facing triangle (**▶**) and a down-facing carat drop-down indicator (**⌄**). 
3. Click the **⌄** drop-down indicator and then click the **deploy:website:public** link that appears. 
4. In a browser, navigate to coreform.com to view the public-deployed changes. 