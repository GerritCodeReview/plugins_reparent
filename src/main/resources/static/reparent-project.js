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

Gerrit.install(function(self) {
    function onReparentProject(c) {
      var p = c.textfield();
      var m = c.textarea();
      var b = c.button('Reparent',
        {onclick: function(){
          c.call(
            {parent: p.value, commit_message: m.value},
            function(r) {
              c.hide();
              window.alert('The project: "'
                + c.project
                + '" was reparented.');
            });
        }});
      c.popup(c.div(
        c.msg('Reparent project "'
          + c.project
          + '"'),
        c.br(),
        c.prependLabel('New Parent Project: ', p),
        c.br(),
        c.prependLabel('Commit Message: ', m),
        c.br(),
        b));
    }
    self.onAction('project', 'parent', onReparentProject);
  });
