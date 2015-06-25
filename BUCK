include_defs('//bucklets/gerrit_plugin.bucklet')

gerrit_plugin(
  name = 'reparent',
  srcs = glob(['src/main/java/**/*.java']),
  resources = glob(['src/main/resources/**/*']),
  manifest_entries = [
    'Gerrit-PluginName: reparent',
    'Gerrit-ApiType: plugin',
    'Gerrit-ApiVersion: 2.12-SNAPSHOT',
    'Gerrit-Module: com.googlesource.gerrit.plugins.reparent.Module',
    'Gerrit-HttpModule: com.googlesource.gerrit.plugins.reparent.HttpModule',
  ],
)

# this is required for bucklets/tools/eclipse/project.py to work
java_library(
  name = 'classpath',
  deps = [':reparent__plugin'],
)

