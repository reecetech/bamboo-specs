# Controlling Permissions

Create a permissions.yaml file:

    bambooServer: https://bamboo.reecenet.org/bamboo
    permissions:
    -
      projects: [BST-ST, SPAM-IT]
      groups: [Cyborg_Team]
      permissions: [VIEW, EDIT, BUILD, CLONE, ADMIN]
    -
      projects: [BST-ST]
      users: [vergarae, dooleyj]
      permissions: [VIEW]

There can be many entries in the permissions: list, each of which
can specify one or more projects, users and permissions to apply.

The projects list contains key pairs (project key, plan key) so the
first list has the ST plan in the BST project, and the IT plan in
the SPAM project. These key pairs are at the end of the URL when
viewing a project's plan, eg:

    https://bamboo.reecenet.org/bamboo/browse/DNSS-DNPSM

Each permission will be applied to each user in each project for a
given permissions entry. So for the second group above, the VIEW
permission will be granted to vergarae and dooleyj in the
BST-ST plan.


