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

import com.google.gerrit.extensions.annotations.PluginName;
import com.google.gerrit.server.config.PluginConfig;
import com.google.gerrit.server.config.PluginConfigFactory;
import com.google.gerrit.server.project.ProjectState;
import com.google.inject.Inject;
import com.google.inject.Singleton;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

@Singleton
public class Config {
  public final static String KEY_IS_JAIL = "isJail";
  public final static String KEY_IS_PROTECTORATE = "isProtectorate";

  private static final String KEY_JAIL = "jail";
  private static final String KEY_PROTECTORATE = "protectorate";

  private Set<String> globalJails;
  private Set<String> globalProtectorates;

  private final String pluginName;
  private final PluginConfigFactory cfgFactory;

  @Inject
  Config(@PluginName String pluginName, PluginConfigFactory cfgFactory) {
    this.pluginName = pluginName;
    this.cfgFactory = cfgFactory;
    reload();
  }

  public void reload() {
    PluginConfig cfg = cfgFactory.getFromGerritConfig(pluginName);
    globalJails = new HashSet<>(Arrays.asList(cfg.getStringList(KEY_JAIL)));
    globalProtectorates =
        new HashSet<>(Arrays.asList(cfg.getStringList(KEY_PROTECTORATE)));
  }

  public Set<String> getGlobalJails() {
    return globalJails;
  }

  public Set<String> getGlobalProtectorates() {
    return globalProtectorates;
  }

  public boolean isJail(ProjectState project) {
    return globalJails.contains(project.getProject().getName())
        || cfgFactory.getFromProjectConfig(project,
            pluginName).getBoolean(KEY_IS_JAIL, false);
  }

  public boolean isProtectorate(ProjectState project) {
    return globalProtectorates.contains(project.getProject().getName())
        || cfgFactory.getFromProjectConfig(project,
            pluginName).getBoolean(KEY_IS_PROTECTORATE, false);
  }
}
