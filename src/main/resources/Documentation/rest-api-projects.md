@PLUGIN@ - /projects/ REST API
==============================

This page describes the project related REST endpoints that are added
by the @PLUGIN@.

Please also take note of the general information on the
[REST API](../../../Documentation/rest-api.html).

<a id="project-endpoints"> Project Endpoints
--------------------------------------------

### <a id="set-project"> Set Parent
_PUT /projects/[\{project-name\}](../../../Documentation/rest-api-projects.html#project-name)/@PLUGIN@~parent_

Reparents a project.

The new parent and a commit message can be specified in the request
body as a [ProjectParentInput](../../../Documentation/rest-api-projects.html#project-parent-input)
entity.

Caller must be a member of a group that is granted

* the 'Reparent Project' capability (provided by this plugin) or
* the 'Reparent Own Project' capability (provided by this plugin) and be project owner or
* the [Administrate Server](../../../Documentation/access-control.html#capability_administrateServer)
  capability.

#### Request

```
  PUT /projects/MyProject/@PLUGIN@~parent HTTP/1.0
  Content-Type: application/json;charset=UTF-8

  {
    "parent": "Demo-Projects"
  }
```


SEE ALSO
--------

* [Projects related REST endpoints](../../../Documentation/rest-api-projects.html)

GERRIT
------
Part of [Gerrit Code Review](../../../Documentation/index.html)
