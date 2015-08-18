/*
 * Copyright 2015 Max Käufer
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.github.amkay.gradle.git.release.notes

import com.github.amkay.gradle.git.release.notes.dsl.GitReleaseNotesPluginExtension
import com.github.amkay.gradle.git.release.notes.task.ExtractReleaseNotesTask
import org.gradle.api.Plugin
import org.gradle.api.Project

/**
 * The entry point of the plugin.
 *
 * <p>
 *     When the plugin is applied, it registers a {@link GitReleaseNotesPluginExtension} for configuration via a
 *     DSL and an {@link ExtractReleaseNotesTask}.
 * </p>
 *
 * @author Max Käufer
 */
class GitReleaseNotesPlugin implements Plugin<Project> {

    /**
     * See {@link Plugin#apply(Object)}.
     * @param project
     */
    @Override
    void apply(final Project project) {
        project.extensions.create GitReleaseNotesPluginExtension.NAME, GitReleaseNotesPluginExtension, project
        project.tasks.create ExtractReleaseNotesTask.NAME, ExtractReleaseNotesTask
    }

}
