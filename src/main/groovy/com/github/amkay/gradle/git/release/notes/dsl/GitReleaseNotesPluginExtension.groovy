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
package com.github.amkay.gradle.git.release.notes.dsl

import org.gradle.api.Project

/**
 * TODO
 *
 * @author Max Käufer
 */
class GitReleaseNotesPluginExtension {

    static final String NAME = (GitReleaseNotesPluginExtension.simpleName[ 0 ].toLowerCase() +
                                GitReleaseNotesPluginExtension.simpleName.substring(1)).replaceAll 'PluginExtension', ''


    String repositoryRoot = './'
    String versionPrefix  = 'v'
    File   destination


    GitReleaseNotesPluginExtension(final Project project) {
        destination = project.file "${project.buildDir}/docs/CHANGES.md"
    }


    void repositoryRoot(final String repositoryRoot) {
        this.repositoryRoot = repositoryRoot
    }

    void versionPrefix(final String versionPrefix) {
        this.versionPrefix = versionPrefix
    }

    void destination(final File destination) {
        this.destination = destination
    }

}
