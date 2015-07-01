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

import static com.googlesource.gerrit.plugins.reparent.ReparentOwnProjectCapability.REPARENT_OWN_PROJECT;
import static com.googlesource.gerrit.plugins.reparent.ReparentProjectCapability.REPARENT_PROJECT;

import com.google.common.base.Strings;
import com.google.gerrit.extensions.annotations.PluginName;
import com.google.gerrit.extensions.restapi.AuthException;
import com.google.gerrit.extensions.restapi.ResourceConflictException;
import com.google.gerrit.extensions.restapi.ResourceNotFoundException;
import com.google.gerrit.extensions.restapi.RestModifyView;
import com.google.gerrit.extensions.restapi.UnprocessableEntityException;
import com.google.gerrit.extensions.webui.UiAction;
import com.google.gerrit.reviewdb.client.Project;
import com.google.gerrit.server.CurrentUser;
import com.google.gerrit.server.account.CapabilityControl;
import com.google.gerrit.server.config.AllProjectsName;
import com.google.gerrit.server.project.ProjectCache;
import com.google.gerrit.server.project.ProjectResource;
import com.google.gerrit.server.project.ProjectState;
import com.google.gerrit.server.project.SetParent.Input;
import com.google.inject.Inject;
import com.google.inject.Provider;
import com.google.inject.Singleton;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

@Singleton
public class SetParent implements
    RestModifyView<ProjectResource, com.google.gerrit.server.project.SetParent.Input>,
    UiAction<ProjectResource>{
  private final String pluginName;
  private final Provider<CurrentUser> userProvider;
  private final AllProjectsName allProjectsName;
  private final Config cfg;
  private final ProjectCache projectCache;
  private final com.google.gerrit.server.project.SetParent setParent;

  @Inject
  SetParent(@PluginName String pluginName,
      Provider<CurrentUser> userProvider,
      AllProjectsName allProjectsName,
      Config cfg,
      ProjectCache projectCache,
      com.google.gerrit.server.project.SetParent setParent) {
    this.pluginName = pluginName;
    this.userProvider = userProvider;
    this.allProjectsName = allProjectsName;
    this.cfg = cfg;
    this.projectCache = projectCache;
    this.setParent = setParent;
  }

  @Override
  public Object apply(ProjectResource rsrc, Input input) throws AuthException,
      ResourceConflictException, ResourceNotFoundException,
      UnprocessableEntityException, IOException {
    assertReparentPermission(rsrc);
    ProjectState newParent = getNewParentProject(input);
    assertReparent(rsrc, newParent);
    return setParent.apply(rsrc, input, false);
  }

  private void assertReparent(ProjectResource rsrc, ProjectState newParent)
      throws ResourceConflictException {
    CapabilityControl ctl = userProvider.get().getCapabilities();
    if (ctl.canAdministrateServer()) {
      return;
    }

    cfg.reload();
    Set<String> myJails = new HashSet<>();
    Set<String> myProtectorates = new HashSet<>();

    for (ProjectState p : rsrc.getControl().getProjectState().parents()) {
      String name = p.getProject().getName();
      if (cfg.isJail(p)) {
        myJails.add(name);
      }
      if (cfg.isProtectorate(p)) {
        myProtectorates.add(name);
      }
    }

    for (ProjectState p : newParent.tree()) {
      String name = p.getProject().getName();
      if (cfg.isProtectorate(p) && !myProtectorates.contains(name)) {
        throw new ResourceConflictException(
            "not allowed to reparent under project " + name);
      }
      myJails.remove(name);
    }
    if (!myJails.isEmpty()) {
      throw new ResourceConflictException(
          "not allowed to move away from project " + myJails.iterator().next());
    }
  }

  private ProjectState getNewParentProject(Input input)
      throws UnprocessableEntityException {
    if (input != null) {
      String newParent = Strings.emptyToNull(input.parent);
      if (newParent != null) {
        ProjectState parent = projectCache.get(new Project.NameKey(newParent));
        if (parent == null) {
          throw new UnprocessableEntityException("parent project " + newParent
              + " not found");
        }
        return parent;
      }
    }
    return projectCache.getAllProjects();
  }

  public void assertReparentPermission(ProjectResource rsrc)
      throws AuthException {
    if (!canReparent(rsrc)) {
      throw new AuthException("not allowed to delete project");
    }
  }

  private boolean canReparent(ProjectResource rsrc) {
    CapabilityControl ctl = userProvider.get().getCapabilities();
    return ctl.canAdministrateServer()
        || ctl.canPerform(pluginName + "-" + REPARENT_PROJECT)
        || (ctl.canPerform(pluginName + "-" + REPARENT_OWN_PROJECT)
            && rsrc.getControl().isOwner());
  }

  @Override
  public UiAction.Description getDescription(ProjectResource rsrc) {
    return new UiAction.Description()
        .setLabel("Reparent...")
        .setTitle(isAllProjects(rsrc)
            ? String.format("No reparent of %s project",
                allProjectsName)
            : String.format("Reparent project %s", rsrc.getName()))
        .setEnabled(!isAllProjects(rsrc))
        .setVisible(canReparent(rsrc));
  }

  private boolean isAllProjects(ProjectResource rsrc) {
    return (rsrc.getControl().getProject()
        .getNameKey().equals(allProjectsName));
  }
}
