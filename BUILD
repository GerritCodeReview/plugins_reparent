load("//tools/bzl:plugin.bzl", "gerrit_plugin")

gerrit_plugin(
    name = "reparent",
    srcs = glob(["src/main/java/**/*.java"]),
    resources = glob(["src/main/resources/**/*"]),
    manifest_entries = [
        "Gerrit-PluginName: reparent",
        "Gerrit-Module: com.googlesource.gerrit.plugins.reparent.Module",
        "Gerrit-HttpModule: com.googlesource.gerrit.plugins.reparent.HttpModule",
    ],
)
