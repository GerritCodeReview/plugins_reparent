Configuration
=============

The configuration of the @PLUGIN@ plugin is done in the `gerrit.config`
file.

```
  [plugin "@PLUGIN@"]
    jail = Confidential-Projects
    protectorate = Public-Projects
```

<a id="jail">
`plugin.@PLUGIN@.jail`
:	Parent project that a child project cannot leave.

	Child projects can only be reparented within the subtree of this
	project.

	E.g. parent projects that define mandatory block rules should be
	defined as 'jail' projects so that their child projects cannot
	escape the block rules by reparenting.

	The `jail` option can be specified multiple times.

	This restriction does not apply to Gerrit administrators.

<a id="protectorate">
`plugin.@PLUGIN@.protectorate`
:	Parent project that a non-child project cannot enter.

	Non-Child projects cannot be reparented under the subtree of this
	project.

	E.g. parent projects that define a group of projects for which a
	certain process applies (e.g they are released together) can be
	defined as 'protectorate' projects, so that only administrators
	can reparent new projects under it.

	The `protectorate` option can be specified multiple times.

	This restriction does not apply to Gerrit administrators.
