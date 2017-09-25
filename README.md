# Credentials and Authentication

Before running this program you need to configure an admin user
to the user the program will run as. You do this by creating a
`.credentials` file with the following contents:

   username=<admin user username>
   password=<admin user password>

Don't check this into a repository.


# Controlling Permissions

Create a permissions.yaml file:

    bambooServer: https://bamboo.reecenet.org/bamboo
    permissions:
    -
      projects: [BST-ST, SPAM-IT]
      groups: [Cyborg_Team]
      users: [islaa]
      permissions: [VIEW, EDIT, BUILD, CLONE, ADMIN]
    -
      projects: [BST-ST]
      users: [dooleyj]
      permissions: [VIEW]

There can be many entries in the permissions: list, each of which
can specify one or more projects, groups, users and permissions to apply.

The projects list contains key pairs (project key, plan key) so the
first list has the ST plan in the BST project, and the IT plan in
the SPAM project. These key pairs are at the end of the URL when
viewing a project's plan, eg:

    https://bamboo.reecenet.org/bamboo/browse/DNSS-DNPSM

Each permission will be applied to each group and user in each project for a
given permissions entry. So for the first group above, the complete set of
permissions will be granted to the Cyborg_Team group and user islaa in the
BST-ST and SPAM-IT plans.

The admin user used to make the changes (see Credentials and Authentication) is hard-coded
to be granted Admin user permission regardless of the other settings in
the permissions yaml, to prevent that user from having that permission
removed (which would break the program).
