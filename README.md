# Credentials and Authentication

Before running this program you need to configure an admin user
to the user the program will run as. You do this by creating a
`.credentials` file with the following contents:

    username=<admin user username>
    password=<admin user password>

Don't check this into a repository.


# Building and Running

Build the code with:

    mvn package
    
Run with:

    java -jar target/bamboo-specs-reece-1.0.0-SNAPSHOT.jar permissions permissions.yaml
    java -jar target/bamboo-specs-reece-1.0.0-SNAPSHOT.jar plan plan.yaml

# Controlling Permissions

Create a permissions.yaml file:

    bambooServer: https://bamboo.reecenet.org/bamboo
    permissions:
    - projects: [BST-ST, SPAM-IT]
      groups: [Cyborg_Team]
      users: [islaa, tobind]
      grant: [VIEW, EDIT, BUILD, CLONE, ADMIN]
    - projects: [BST-ST]
      users: [dooleyj]
      grant: [VIEW]

There can be many entries in the permissions: list, each of which
can specify one or more projects, groups, users and permissions to grant.

The projects list contains key pairs (project key, plan key) so the
first list has the ST plan in the BST project, and the IT plan in
the SPAM project. These key pairs are at the end of the URL when
viewing a project's plan, eg:

    https://bamboo.reecenet.org/bamboo/browse/DNSS-DNPSM

Each permission will be granted to each group and user in each project for a
given permissions entry. So for the first group above, the complete set of
permissions will be granted to the `Cyborg_Team` group and users `islaa`
and `tobind` in the `BST-ST` and `SPAM-IT` plans.

The admin user used to make the changes (see Credentials and Authentication) is
hard-coded to be granted Admin user permission regardless of the other settings
in the permissions yaml, to prevent that user from having that permission
removed (which would break the program).


# Creating and Configuring Plans

Plans have a lot more options. The required minumum is:

    bambooServer: https://bamboo.reecenet.org/bamboo
    projectKey: BST
    projectName: Bamboo Spec Testing
    planKey: ST
    planName: Spec Testing
    description: This is a test plan for bamboo specs
    
If the Plan or Project do not exist in Bamboo they will be created, so please
double-check that the `projectKey` and `planKey` are correct.

The rest of the configuration is all optional chunks, though some will depend
on others (VCS tasks would require a repository, for example).

If there is a repository linked then include it as:

    repository:
      name: Bamboo Spec Test Project
      projectKey: SAN
      repositorySlug: bamboo-spec-test-project
    repositoryPolling: true

The repository must be in the Reece Stash instance and the configuration
components above come from the repository URL like so:

    https://stash.reecenet.org/projects/<projectKey>/repos/<repositorySlug>/browse

Slack notifications on plan completion are supported (others can be added):

    notifications:
    - when: PLAN_COMPLETED
      slack: https://hooks.slack.com/services/...the rest of the URL...|#cyborg-dev

Then stages and jobs may be defined:

    stages:
    - name: Default Stage
      jobs:
      - name: Run Tests
        key: JOB1
        description: Run Python Unit Tests
        requirements: [system.docker.executable, DOCKER, LINUX]
        
Requirements is optional. The job key is arbitrary and unique inside a plan.
The job may then have a list of artifacts and tasks:

    artifacts:
    - name: unittest
      pattern: "**"
      location: unittest-report
    - name: PACT Contracts
      pattern: "**"
      location: pacts
    - name: Coverage Report
      pattern: "**"
      location: htmlcov
    tasks:
    - type: VCS
      description: Checkout Default Repository
    - type: SCRIPT
      description: Build docker image
      body: |
        set -ex
        scripts/test_image.sh bamboo/%(projectPlanKey)s
    - type: SCRIPT
      description: Run tests
      body: |
        set -ex
        scripts/run_tests.sh
    - type: JUNIT
      resultFrom: "**/unittest-report/xml/*.xml"
      
The marker `%(projectPlanKey)s` will be substituted for the project-plan key
(eg. `BST-ST`) before submission to Bamboo.

The VCS task has a number of options. By default it will check out the default
repository for the plan. If you wish to check out other repositories you may list
them (and optionally include the default repository also):

    - type: VCS
      description: Checkout All Repositories
      defaultRepository: true
      repositories:
      - name: Running Man
      - name: Running Man Properties
        path: properties

Finally, if the plan has dependent plans they may be specified (as "dependencies"):

    dependencies:
      requiresPassing: true
      plans: [USRSRV-UPSDB]

A sample plan:
    
    bambooServer: https://bamboo.reecenet.org/bamboo
    projectKey: BST
    projectName: Bamboo Spec Testing
    planKey: ST
    planName: Spec Testing
    description: This is a test plan for bamboo specs
    repository:
      name: Bamboo Spec Test Project
      projectKey: SAN
      repositorySlug: bamboo-spec-test-project
    repositoryPolling: true
    notifications:
    - when: PLAN_COMPLETED
      slack: https://hooks.slack.com/...the rest of the URL|#cyborg-dev
    stages:
    - name: Default Stage
      jobs:
      - name: Run Tests
        key: JOB1
        description: Run Python Unit Tests
        artifacts:
        - name: unittest
          pattern: "**"
          location: unittest-report
        - name: PACT Contracts
          pattern: "**"
          location: pacts
        - name: Coverage Report
          pattern: "**"
          location: htmlcov
        tasks:
        - type: VCS
          description: Checkout Default Repository
        - type: SCRIPT
          description: Build docker image
          body: |
            set -ex
            scripts/test_image.sh bamboo/%(projectPlanKey)s
        - type: SCRIPT
          description: Run tests
          body: |
            set -ex
            mkdir -p htmlcov
            chmod 777 htmlcov
            mkdir -p unittest-report
            chmod 777 unittest-report
            mkdir -p pacts
            chmod 777 pacts
            docker run --rm -u root \\
            -v ${bamboo.build.working.directory}/htmlcov:/app/htmlcov:rw \\
            -v ${bamboo.build.working.directory}/unittest-report:/app/unittest-report:rw \\
            -v ${bamboo.build.working.directory}/pacts:/app/pacts:rw \\
            -e PACT_DIR=/app/pacts \\
            -t bamboo/%(projectPlanKey)s bash -c "cd /app/ && ./scripts/ci_tests.sh"
        - type: JUNIT
          resultFrom: "**/unittest-report/xml/*.xml"
        requirements: [system.docker.executable, DOCKER, LINUX]
    dependencies:
      requiresPassing: true
      plans: [USRSRV-UPSDB]
