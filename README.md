# Bamboo Plan and Deployment Configuration

This project provides a tool that uses YAML files to specify
how Bamboo plans and deployment projects should be configured.

## Sample configuration files

See the bamboo-configs repository for sample files:

    https://stash.reecenet.org/projects/DE/repos/bamboo-configs

## Credentials and Authentication

Before running this program you need to configure an admin user
to the user the program will run as (ie. your Bamboo credentials)
You do this by creating a `.credentials` file with the following
contents:

    username=<admin user username>
    password=<admin user password>

Don't check this into a repository.

## Building and Running

Build the code with:

    mvn package
    
Run with:

    java -jar target/bamboo-specs-reece-1.0.0.jar permissions permissions.yaml
    java -jar target/bamboo-specs-reece-1.0.0.jar plan plan.yaml
    java -jar target/bamboo-specs-reece-1.0.0.jar deployment deployment-project.yaml
    
You can test your YAML using the -t switch passed to any of those commands, for example:

    java -jar target/bamboo-specs-reece-1.0.0.jar plan -t plan.yaml

This will just parse the YAML and not deploy it to Bamboo.

## Java SSL keystore fix

If you get this error when running the jar files, you need to add Reece's CA cert to your java keystore:

    Caused by: javax.net.ssl.SSLHandshakeException: sun.security.validator.ValidatorException: PKIX path building failed: sun.security.provider.certpath.SunCertPath

    On Ubuntu:
    ----------
    Visit https://bamboo.reecenet.org in Chrome
    Use developer tools - Security - View Certificate - details tab - export
    This will save a copy of the public certificate to a file
    (vicpjdt01.reecenet.org in the example below)
    Import that file using:
    keytool -import -alias vicpjdt01.reecenet.org -keystore cacerts \
    -trustcacerts -file ~dev/vicpjdt01.reecenet.org
    This will prompt for a password; the default is "changeit"

## Controlling Permissions

Create a permissions.yaml file:

    bambooServer: https://bamboo.reecenet.org/bamboo
    projects:
    - plans: [BST-ST, SPAM-IT]
      permissions:
      - groups: [Cyborg_Team]
        users: [islaa, tobind]
        grant: [VIEW, EDIT, BUILD, CLONE, ADMIN]
    - plans: [BST-ST]
      permissions:
      - users: [dooleyj]
        grant: [VIEW]
    deployments:
    - name: Diary Notes Python Shared Service
      permissions:
      - users: [dooleyj, poultonj]
        grant: [VIEW, EDIT]
      environments:
      - names: [Production (AU + NZ), TEST AU, TEST NZ]
        permissions:
        - groups: [Cyborg_Team]
          grant: [VIEW, EDIT, BUILD]
    - name: Customer Prices Service
      permissions:
      - users: [dooleyj, poultonj]
        grant: [VIEW, EDIT]
      environments:
      - names: [Production (AU + NZ)]
        permissions:
        - groups: [Cyborg_Team]
          grant: [VIEW, EDIT, BUILD]

There can be many entries in the permissions: list, each of which
specifies groups and/or users and permissions to grant to them.

The projects list contains plans identified by key pairs (project key, plan
key) so the first list has the ST plan in the BST project, and the IT plan
in the SPAM project. These key pairs are at the end of the URL when
viewing a project's plan, eg:

    https://bamboo.reecenet.org/bamboo/browse/DNSS-DNPSM

The deployments list contains deployments and environments identified by
their label in the Bamboo interface. Note that permissions granted to a
deployment are only for administrating the deployment project settings in
Bamboo and do *not* affect the access controls for each of the environments.

Each permission will be granted to each group and user in each plan, deployment
project or environment for a given permissions entry. So for the first group
above, the complete set of permissions will be granted to the `Cyborg_Team`
group and users `islaa` and `tobind` in the `BST-ST` and `SPAM-IT` plans.

The allowed permissions for each section are:

* **plans**: VIEW, EDIT, BUILD, CLONE, ADMIN
* **deployments**: VIEW, EDIT
* **environments**: VIEW, EDIT, BUILD

The admin user used to make the changes (see Credentials and Authentication) is
hard-coded to be granted admin user permission regardless of the other settings
in the permissions yaml, to prevent that user from having that permission
removed (which would break the program).


## Build and Test Plans

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

If you have arbitrary variables stored on a plan you may set them as key-value
pairs like so:

    variables:
      major_version_number: 1
      target_name: bamboo-spec-testing

Variables defined here (and others defined by Bamboo for you) may be reference
in SCRIPT task body texts using `${bamboo.major_version_number}` or
`${bamboo.target_name}` using the above example settings.

### Source Repositories

If there are repositories used then include as either linked repositories
(shared between plans):

    linkedRepositories: [Bamboo Spec Test Project, Other Repository]

The linked repository is typically added when a plan is created. Alternatively
you can use a locally (to this plan) defined repository:

    repository:
      name: Bamboo Spec Test Project
      projectKey: SAN
      repositorySlug: bamboo-spec-test-project

The repository above must be in the Reece Stash instance and the configuration
components above come from the repository URL like so:

    https://stash.reecenet.org/projects/<projectKey>/repos/<repositorySlug>/browse

Plan branches are local configurations based on branches in the repository and
the strategy for synchronising the two are controlled with:

    branchManagement:
      createStrategy: MANUALLY

The creation strategy is one of: `MANUALLY`, `ON_PULL_REQUEST`, `ON_NEW_BRANCH`
or `ON_BRANCH_PATTERN`. The last will create on new branches matching a name
pattern regular expression:

    branchManagement:
      createStrategy: ON_BRANCH_PATTERN
      branchPattern: feature/.*

The `issueLinkingEnabled` option enables automatic linking of the plan branch
to the Jira issue related to the repository branch, which is enabled by default.
Cleaning up plan branches is defaulted to 7 days after the repository branch is
deleted, or after 30 days of inactivity in the repository branch. These options
may be modified in the `branchManagement` section:

    branchManagement:
      issueLinkingEnabled: false
      delayCleanAfterDelete: 2
      delayCleanAfterInactivity: 60

### Triggers

Plans may also have a `triggers` section to indicate the specific circumstances
in which they are to be triggered (that is, their tasks should be executed),
say running unit tests after commits to the stash repository:

    triggers:
    - type: AFTER_STASH_COMMIT
      description: Trigger from stash changes

Or perhaps trigger a deploy from a successful build:

    triggers:
    - type: AFTER_SUCCESSFUL_BUILD_PLAN
      description: Deploy main plan branch (master)

If the plan has dependent plans which are to be triggered when
this plan completes they may be specified (as "dependencies"):

    dependencies:
      requiresPassing: true
      plans: [USRSRV-UPSDB]

### Notifications

Notifications on plan completion are supported:

    notifications:
    - when: PLAN_COMPLETED
      slack: https://hooks.slack.com/services/...the rest of the URL...|#cyborg-dev
      recipientGroups: [Cyborg_Team]
      recipientUsers: [dooleyj, poultonj]
      
At least one of the notification targets is required: `slack`,
`recipientGroups` or `recipientUsers`. The `when` values are `PLAN_COMPLETED`,
`PLAN_FAILED`, `DEPLOYMENT_FAILED` and `DEPLOYMENT_FINISHED` which mirror the options of the
same name in the Bamboo UI.

### Stages, Jobs and Tasks

Your plan may have multiple stages, which each have jobs, and each job may have
tasks and *final* tasks (tasks to run even if the other tasks fail).

Stages and jobs may be defined:

    stages:
    - name: Default Stage
      jobs:
      - name: Run Tests
        key: JOB1
        description: Run Python Unit Tests
        requirements:
        - name: system.docker.executable
        - name: DOCKER
        - name: LINUX

Requirements and artifacts are optional. The job key is arbitrary and unique
inside a plan. The job may then have a list of tasks:

    artifacts:
    - name: PACT Contracts
      pattern: "**"
      location: pacts
    - name: Coverage Report
      pattern: "**"
      location: htmlcov
    tasks:
    - type: VCS
      description: Checkout Default Repository
      cleanCheckout: true
    - type: SCRIPT
      description: Build docker image
      body: |
        set -ex
        scripts/test_image.sh bamboo/${bamboo.target_name}
    - type: SCRIPT
      description: Run tests
      body: |
        set -ex
        scripts/run_tests.sh
      
Here you can see we refer to the bamboo variable we defined way up above so that
the script body may be the same across multiple projects.

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
      cleanCheckout: true
      
If you wish to force a clean checkout of the repositories on or off use `cleanCheckout`.

Final tasks are tasks that are always run after the other tasks, regardless of whether
they were successful. These could be cleanup tasks, or more commonly including a
JUnit parser to parse the results of the tests which may have failed:

    finalTasks:
    - type: JUNIT
      description: Include XML test results
      resultFrom: "**/unittest-report/xml/*.xml"

## Deployment Projects

TODO document me
