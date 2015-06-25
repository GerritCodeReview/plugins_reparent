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

import static com.google.gerrit.server.project.ProjectResource.PROJECT_KIND;
import static com.googlesource.gerrit.plugins.reparent.ReparentOwnProjectCapability.REPARENT_OWN_PROJECT;
import static com.googlesource.gerrit.plugins.reparent.ReparentProjectCapability.REPARENT_PROJECT;

import com.google.gerrit.extensions.annotations.Exports;
import com.google.gerrit.extensions.config.CapabilityDefinition;
import com.google.gerrit.extensions.restapi.RestApiModule;
import com.google.inject.AbstractModule;

class Module extends AbstractModule {
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
  }
}
