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
import com.github.amkay.gradle.git.release.notes.dsl.ReleaseNotes
import org.ajoberstar.grgit.Commit
import org.ajoberstar.grgit.Grgit
import org.gradle.api.DefaultTask
import org.gradle.api.logging.Logger
import org.gradle.api.logging.Logging
import org.gradle.api.tasks.TaskAction

import static com.github.amkay.gradle.git.release.notes.tag.finder.TagFinder.TAG_FINDERS

/**
 * TODO
 *
 * @author Max Käufer
 */
class ExtractReleaseNotesTask extends DefaultTask {

    private static final Logger LOGGER = Logging.getLogger ExtractReleaseNotesTask
    static final         String NAME   = (ExtractReleaseNotesTask.simpleName[ 0 ].toLowerCase() +
                                          ExtractReleaseNotesTask.simpleName.substring(1)).replaceAll 'Task', ''

    public static final String HEADER_PLUGIN_NAME = 'gradle-git-release-notes'

    public static final String H1_MARKER = '='
    public static final String H2_MARKER = '-'

    public static final String HEADLINE_NEW_FEATURES = 'New features'
    public static final String HEADLINE_BUGFIXES     = 'Bugfixes'

    public static final String BODY_INDENTATION = ' ' * 4


    final String description = 'Extracts the changes since the last version from the commit messages on the ' +
                               'development branch.'
    final String group       = 'documentation'

    protected GitReleaseNotesPluginExtension extension


    ExtractReleaseNotesTask() {
        this.extension = project[ GitReleaseNotesPluginExtension.NAME ] as GitReleaseNotesPluginExtension
    }


    @TaskAction
    void extractReleaseNotes() {
        def grgit = Grgit.open dir: extension.repositoryRoot

        def tag = TAG_FINDERS.findResult { tagFinder ->
            tagFinder.find project, grgit
        }
        def tagName = tag.name.startsWith(extension.versionPrefix) ? tag.name[ 1..-1 ] : tag.name

        List<Commit> commitsSinceLastTag

        if (tag) {
            LOGGER.info "Found last version tag ${tag.name} ($tag.commit.abbreviatedId)."
            commitsSinceLastTag = grgit.log { range tag.commit, grgit.head() }
        } else {
            LOGGER.info "No version tag found."
            commitsSinceLastTag = grgit.log()
        }

        LOGGER.debug "Found commits since tag ${tag.name}:"
        commitsSinceLastTag.each { LOGGER.debug "  * ${it.shortMessage}" }

        List<Commit> newFeatures = extractNewFeatures commitsSinceLastTag
        List<Commit> bugfixes = extractBugfixes commitsSinceLastTag

        LOGGER.info 'Found new features:'
        newFeatures.each { LOGGER.info "  * ${it.shortMessage}" }
        LOGGER.info 'Found bugfixes:'
        bugfixes.each { LOGGER.info "  * ${it.shortMessage}" }

        writeReleaseNotes tagName, newFeatures, bugfixes

        grgit.close()
    }

    private List<Commit> filterCommits(final List<Commit> commits, final ReleaseNotes releaseNotes) {
        commits.findAll {
            it.fullMessage =~ releaseNotes.include && !(it.fullMessage =~ releaseNotes.exclude)
        }
    }

    private List<Commit> extractNewFeatures(final List<Commit> commits) {
        filterCommits commits, extension.newFeatures
    }

    private List<Commit> extractBugfixes(final List<Commit> commits) {
        filterCommits commits, extension.bugfixes
    }

    protected void writeHeadline(final Writer writer, final String text, final String headlineMarker) {
        writer.writeLine ''
        writer.writeLine text
        writer.writeLine headlineMarker * (text.length() + 1)
    }

    protected void writeReleaseNotes(final Writer writer, final List<Commit> commits, final String headline,
                                     final ReleaseNotes releaseNotes) {

        if (commits) {
            writeHeadline writer, headline, H2_MARKER

            commits.each { commit ->
                def cleanFullMessage = commit.fullMessage
                                             .replaceAll(releaseNotes.remove, '')
                                             .readLines()

                def subject = cleanFullMessage[ 0 ].trim()
                def body = cleanFullMessage.size() > 2 ?
                           cleanFullMessage[ 2..-1 ].join("\n$BODY_INDENTATION").trim() : null

                writer.writeLine "* $subject"
                if (body) {
                    writer.writeLine "\n$BODY_INDENTATION$body\n"
                }
            }
        }
    }

    protected void writeReleaseNotes(final String tagName, final List<Commit> newFeatures,
                                     final List<Commit> bugfixes) {

        def destination = extension.destination

        destination.parentFile.mkdirs()

        destination.withWriter('utf-8') { writer ->
            writer.writeLine """% Changes since version $tagName
                               |% $HEADER_PLUGIN_NAME
                               |% ${new Date()}"""
                               .stripMargin()

            writeHeadline writer, "Changes since version $tagName", H1_MARKER

            writeReleaseNotes writer, newFeatures, HEADLINE_NEW_FEATURES, extension.newFeatures
            writeReleaseNotes writer, bugfixes, HEADLINE_BUGFIXES, extension.bugfixes
        }
    }

}
