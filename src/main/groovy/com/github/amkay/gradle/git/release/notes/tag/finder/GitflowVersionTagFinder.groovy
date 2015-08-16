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
 * TODO
 *
 * @author Max Käufer
 */
class GitflowVersionTagFinder implements TagFinder {

    @Override
    Tag find(final Project project, final Grgit grgit) {
        def version = project.version

        if (!version.hasProperty('normalVersion')) {
            return null
        }

        def extension = project[ GitReleaseNotesPluginExtension.NAME ] as GitReleaseNotesPluginExtension
        def tagName = "${extension.versionPrefix}${version.normalVersion}".toString()

        grgit.tag.list().find { it.name == tagName }
    }

}