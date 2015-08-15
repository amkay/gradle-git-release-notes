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

import org.ajoberstar.grgit.Grgit
import org.gradle.api.Project
import org.gradle.util.ConfigureUtil

/**
 * TODO
 *
 * @author Max Käufer
 */
class GitReleaseNotesPluginExtension {

    static final String NAME = (GitReleaseNotesPluginExtension.simpleName[ 0 ].toLowerCase() +
                                GitReleaseNotesPluginExtension.simpleName.substring(1)).replaceAll 'PluginExtension', ''

    private static final String INCLUDE_NEW_FEATURE = /[Cc]lose(s|d)? #\d+/
    private static final String EXCLUDE_NEW_FEATURE = /--no-release-note/
    private static final String REMOVE_NEW_FEATURE  = /([Cc]lose(s|d)?|[Ff]ix(es|ed)?) #\d+\s*\p{Punct}?\s*/

    private static final String INCLUDE_BUGFIX = /[Ff]ix(es|ed)? #\d+/
    private static final String EXCLUDE_BUGFIX = /--no-release-note/
    private static final String REMOVE_BUGFIX  = /([Cc]lose(s|d)?|[Ff]ix(es|ed)?) #\d+\s*\p{Punct}?\s*/

    private static final String CONFIG_SECTION_GITFLOW   = 'gitflow'
    private static final String CONFIG_SUBSECTION_PREFIX = 'prefix'
    private static final String CONFIG_VERSION_TAG       = 'versionTag'

    private static final String VERSION_PREFIX = 'v'

    String repositoryRoot = './'
    String versionPrefix
    File   destination

    ReleaseNotes newFeatures = new ReleaseNotes(INCLUDE_NEW_FEATURE, EXCLUDE_NEW_FEATURE, REMOVE_NEW_FEATURE)
    ReleaseNotes bugfixes    = new ReleaseNotes(INCLUDE_BUGFIX, EXCLUDE_BUGFIX, REMOVE_BUGFIX)


    GitReleaseNotesPluginExtension(final Project project) {
        setDestination project.file("${project.buildDir}/docs/CHANGES.md")
    }


    @SuppressWarnings('ConfusingMethodName')
    void repositoryRoot(final String repositoryRoot) {
        setRepositoryRoot repositoryRoot
    }

    @SuppressWarnings('ConfusingMethodName')
    void versionPrefix(final String versionPrefix) {
        setVersionPrefix versionPrefix
    }

    String getVersionPrefix() {
        if (versionPrefix == null) {
            setVersionPrefix extractVersionPrefixFromGitflow() ?: VERSION_PREFIX
        }

        versionPrefix
    }

    private String extractVersionPrefixFromGitflow() {
        def grgit = Grgit.open dir: repositoryRoot

        def gitflowVersionPrefix = grgit.repository.jgit.repository.config
                                        .getString(CONFIG_SECTION_GITFLOW,
                                                   CONFIG_SUBSECTION_PREFIX,
                                                   CONFIG_VERSION_TAG)

        grgit.close()

        gitflowVersionPrefix
    }

    @SuppressWarnings('ConfusingMethodName')
    void destination(final File destination) {
        setDestination destination
    }

    @SuppressWarnings('ConfusingMethodName')
    void newFeatures(@DelegatesTo(ReleaseNotes) final Closure cl) {
        ConfigureUtil.configure cl, newFeatures
    }

    @SuppressWarnings('ConfusingMethodName')
    void bugfixes(@DelegatesTo(ReleaseNotes) final Closure cl) {
        ConfigureUtil.configure cl, bugfixes
    }

}
