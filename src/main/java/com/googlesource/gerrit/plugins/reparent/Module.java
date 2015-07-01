// Copyright (C) 2015 The Android Open Source Project
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
// http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.

package com.googlesource.gerrit.plugins.reparent;

import static com.googlesource.gerrit.plugins.reparent.Config.KEY_IS_JAIL;
import static com.googlesource.gerrit.plugins.reparent.Config.KEY_IS_PROTECTORATE;
import static com.google.gerrit.server.project.ProjectResource.PROJECT_KIND;
import static com.googlesource.gerrit.plugins.reparent.ReparentOwnProjectCapability.REPARENT_OWN_PROJECT;
import static com.googlesource.gerrit.plugins.reparent.ReparentProjectCapability.REPARENT_PROJECT;

import com.google.gerrit.extensions.annotations.Exports;
import com.google.gerrit.extensions.config.CapabilityDefinition;
import com.google.gerrit.extensions.restapi.RestApiModule;
import com.google.gerrit.server.config.ProjectConfigEntry;
import com.google.gerrit.server.project.ProjectState;
import com.google.inject.AbstractModule;
import com.google.inject.Inject;

class Module extends AbstractModule {
  private final Config cfg;

  @Inject
  Module(Config cfg) {
    this.cfg = cfg;
  }

  @Override
  protected void configure() {
    bind(CapabilityDefinition.class)
        .annotatedWith(Exports.named(REPARENT_PROJECT))
        .to(ReparentProjectCapability.class);
    bind(CapabilityDefinition.class)
        .annotatedWith(Exports.named(REPARENT_OWN_PROJECT))
        .to(ReparentOwnProjectCapability.class);
    install(new RestApiModule() {
      @Override
      protected void configure() {
        put(PROJECT_KIND, "parent").to(SetParent.class);
      }
    });

    bind(ProjectConfigEntry.class)
        .annotatedWith(Exports.named(KEY_IS_JAIL))
        .toInstance(new ProjectConfigEntry("Is Jail", false,
            "Whether child projects can only be reparented within"
            + " the subtree of this project.") {
          @Override
          public boolean isEditable(ProjectState project) {
            cfg.reload();
            return !cfg.getGlobalJails().contains(
                project.getProject().getName());
          }

          @Override
          public String onRead(ProjectState project, String value) {
            if (!isEditable(project)) {
              return Boolean.TRUE.toString();
            }
            return value;
          }
        });
    bind(ProjectConfigEntry.class)
        .annotatedWith(Exports.named(KEY_IS_PROTECTORATE))
        .toInstance(new ProjectConfigEntry("Is Protectorate", false,
            "Whether non-child projects cannot be reparented under"
            + " the subtree of this project.") {
          @Override
          public boolean isEditable(ProjectState project) {
            cfg.reload();
            return !cfg.getGlobalProtectorates().contains(
                project.getProject().getName());
          }

          @Override
          public String onRead(ProjectState project, String value) {
            if (!isEditable(project)) {
              return Boolean.TRUE.toString();
            }
            return value;
          }
        });
  }
}
