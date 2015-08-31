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
package com.github.amkay.gradle.git.release.notes.task

import com.github.amkay.gradle.git.release.notes.dsl.GitReleaseNotesPluginExtension
import com.github.amkay.gradle.git.release.notes.exception.HeadNotTaggedException
import org.ajoberstar.grgit.Grgit
import org.ajoberstar.grgit.Tag
import org.gradle.api.DefaultTask
import org.gradle.api.logging.Logger
import org.gradle.api.logging.Logging
import org.gradle.api.tasks.TaskAction

/**
 * TODO
 *
 * @author Max Käufer
 */
class ExtractReleaseNotesFromTagTask extends DefaultTask {

    private static final Logger LOGGER = Logging.getLogger ExtractReleaseNotesFromTagTask

    /**
     * The name under which the task is registered on the project.
     **/
    static final String NAME = (ExtractReleaseNotesFromTagTask.simpleName[ 0 ].toLowerCase() +
                                ExtractReleaseNotesFromTagTask.simpleName.substring(1)).replaceAll 'Task', ''

    private static final String HEADER_PLUGIN_NAME = 'gradle-git-release-notes'


    final String description = 'Extracts the changes since the last version from the commit message of the current ' +
                               'HEAD.'
    final String group       = 'documentation'

    private GitReleaseNotesPluginExtension extension


    ExtractReleaseNotesFromTagTask() {
        this.extension = project[ GitReleaseNotesPluginExtension.NAME ] as GitReleaseNotesPluginExtension
    }


    @TaskAction
    void extractReleaseNotes() {
        def grgit = Grgit.open dir: extension.repositoryRoot

        def head = grgit.head()
        Tag tag = grgit.tag.list().find { it.commit == head }

        LOGGER.debug "The current HEAD is ${head.abbreviatedId}."

        if (!tag) {
            grgit.close()
            throw new HeadNotTaggedException("The current HEAD is not tagged.")
        }

        LOGGER.lifecycle "Extracting release notes from tag ${tag.name}."
        writeReleaseNotes tag

        grgit.close()
    }

    private void writeReleaseNotes(final Tag tag) {
        def destination = extension.destination
        def version = tag.name.startsWith(extension.versionPrefix) ? tag.name[ 1..-1 ] : tag.name
        def releaseNotes

        if (tag.fullMessage.startsWith("${tag.name}")) {
            def lines = tag.fullMessage.readLines()
            releaseNotes = lines.size() > 2 ? lines[ 2..-1 ] : [ ]
        } else {
            releaseNotes = tag.name.readLines()
        }

        destination.parentFile.mkdirs()

        destination.withWriter('utf-8') { writer ->
            writer.writeLine """% Changes in version $version
                               |% $HEADER_PLUGIN_NAME
                               |% ${new Date()}"""
                               .stripMargin()

            writer.writeLine ''
            releaseNotes.each { writer.writeLine it }
        }
    }

}
