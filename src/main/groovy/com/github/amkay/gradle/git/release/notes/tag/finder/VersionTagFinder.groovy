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
package com.github.amkay.gradle.git.release.notes.tag.finder

import com.github.amkay.gradle.git.release.notes.dsl.GitReleaseNotesPluginExtension
import org.ajoberstar.grgit.Grgit
import org.ajoberstar.grgit.Tag
import org.gradle.api.Project

/**
 * This strategy for finding the <em>Git tag</em> of the latest release version is used if the version of the project
 * the plugin was applied on is a normal {@link String}.
 * The strategy then searches for a tag matching the <code>version</code> of the project, including the configured
 * {@link GitReleaseNotesPluginExtension#versionPrefix}.
 *
 * @author Max Käufer
 */
class VersionTagFinder implements TagFinder {

    /**
     * See {@link TagFinder#find(Project, Grgit)}.
     * @param project
     * @param grgit
     * @return
     */
    @Override
    Tag find(final Project project, final Grgit grgit) {
        def extension = project[ GitReleaseNotesPluginExtension.NAME ] as GitReleaseNotesPluginExtension
        def tagName = "${extension.versionPrefix}${project.version}".toString()

        grgit.tag.list().find { it.name == tagName }
    }

}
