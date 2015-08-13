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
    static final         String NAME   = 'extractReleaseNotes'

    private static final String REPOSITORY_ROOT = '/'
    public static final  String VERSION_PREFIX  = 'v'

    private static final String INCLUDE_NEW_FEATURE = /[Cc]lose(s|d)? #\d+/
    private static final String EXCLUDE_NEW_FEATURE = /--no-release-note/

    private static final String INCLUDE_BUGFIX = /[Ff]ix(es|ed)? #\d+/

    @TaskAction
    void extractChanges() {
        def grgit = Grgit.open dir: REPOSITORY_ROOT

        def tag = TAG_FINDERS.findResult { tagFinder ->
            tagFinder.find project, grgit
        }
        def tagName = tag.name.startsWith(VERSION_PREFIX) ? tag.name[ 1..-1 ] : tag.name

        def commitsSinceLastTag

        if (tag) {
            LOGGER.info "Found last version tag ${tag.name} ($tag.commit.abbreviatedId)."
            commitsSinceLastTag = grgit.log { range tag.commit, grgit.head() }
        } else {
            commitsSinceLastTag = grgit.log()
        }

        LOGGER.debug "Found commits since last tag:"
        commitsSinceLastTag.each { LOGGER.debug "  * ${it.shortMessage}" }

        List<Commit> newFeatures = commitsSinceLastTag.findAll {
            it.fullMessage =~ INCLUDE_NEW_FEATURE && !(it.fullMessage =~ EXCLUDE_NEW_FEATURE)
        }
        List<Commit> bugfixes = commitsSinceLastTag.findAll {
            it.fullMessage =~ INCLUDE_BUGFIX && !(it.fullMessage =~ /--no-release-note/)
        }

        LOGGER.info "Found new features:"
        newFeatures.each { LOGGER.info "  * ${it.shortMessage}" }
        LOGGER.info "Found bugfixes:"
        bugfixes.each { LOGGER.info "  * ${it.shortMessage}" }

        project.mkdir("${project.buildDir}/docs")
        project.file("${project.buildDir}/docs/CHANGES.md").withWriter('utf-8') { writer ->
            writer.writeLine """% Changes since version $tagName
% gradle-gitflow'
% ${new Date()}"

Changes since version $tagName"""
            writer.writeLine '=' * ("Changes since version $tagName".length() + 1)

            if (newFeatures) {
                writer.writeLine """
New features
-------------
"""

                newFeatures.each { feature ->
                    def cleanFullMessage = feature.fullMessage
                                                  .replaceAll(/([Cc]lose(s|d)?|[Ff]ix(es|ed)?) #\d+\s*\p{Punct}?\s*/,
                                                              "")
                                                  .readLines()
                    def subject = cleanFullMessage[ 0 ].trim()
                    def body = cleanFullMessage.size() > 2 ? cleanFullMessage[ 2..-1 ].join('\n    ').trim() : null

                    writer.writeLine "* $subject"
                    if (body) {
                        writer.writeLine """
    $body
"""
                    }
                }
            }

            if (bugfixes) {
                writer.writeLine """
Bugfixes
---------
"""

                bugfixes.each { bugfix ->
                    def cleanFullMessage = bugfix.fullMessage
                                                 .replaceAll(/([Cc]lose(s|d)?|[Ff]ix(es|ed)?) #\d+\s*\p{Punct}?\s*/, "")
                                                 .readLines()
                    def subject = cleanFullMessage[ 0 ].trim()
                    def body = cleanFullMessage.size() > 2 ? cleanFullMessage[ 2..-1 ].join('\n    ').trim() : null

                    writer.writeLine "* $subject"
                    if (body) {
                        writer.writeLine """
    $body
"""
                    }
                }
            }
        }
    }

    @Override
    String getDescription() {
        'Extracts the changes since the last version from the commit messages on the development branch.'
    }

    @Override
    String getGroup() {
        'documentation'
    }

}
