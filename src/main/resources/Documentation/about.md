The @PLUGIN@ plugin provides a self-service for reparenting projects.

In Gerrit core only administrators are allowed to reparent project.
This is because inherited access rights (in particular block rules) can
be removed by reparenting a project under a different parent.

This plugin allows users with the 'Reparent Project' capability
(provided by this plugin) and project owners with the
'Reparent Own Project' capability (provided by this plugin) to
reparent projects, while the Gerrit administrator can control by the
plugin configuration under which project subtrees reparenting is
allowed.
 