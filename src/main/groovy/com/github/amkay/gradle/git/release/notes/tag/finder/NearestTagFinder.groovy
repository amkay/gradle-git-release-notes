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

import org.ajoberstar.grgit.Commit
import org.ajoberstar.grgit.Grgit
import org.ajoberstar.grgit.Tag
import org.gradle.api.Project
import org.gradle.api.logging.Logger
import org.gradle.api.logging.Logging

/**
 * This strategy for finding the <em>Git tag</em> of the latest release version is used if no other strategies find
 * a matching <em>Git tag</em>.
 * The strategy simply searches for the next reachable <em>Git tag</em> starting from the current <em>HEAD</em>.
 *
 * @author Max Käufer
 */
class NearestTagFinder implements TagFinder {

    private static final Logger LOGGER = Logging.getLogger NearestTagFinder

    /**
     * See {@link TagFinder#find(Project, Grgit)}.
     * @param project
     * @param grgit
     * @return
     */
    @Override
    Tag find(final Project project, final Grgit grgit) {
        LOGGER.debug "Locate beginning on branch: ${grgit.branch.current.fullName}"

        Commit head = grgit.head()

        List versionTags = grgit.tag.list().inject([ ]) { list, tag ->
            def data

            if (tag.commit == head) {
                LOGGER.debug "Tag ${tag.fullName} is at head. Including as candidate."

                data = [ tag: tag, distance: 0 ]
            } else {
                if (grgit.isAncestorOf(tag, head)) {
                    LOGGER.debug "Tag ${tag.name} is an ancestor of HEAD. Including as a candidate."

                    def reachableCommitLog = grgit.log {
                        range tag.commit.id, head.id
                    }

                    LOGGER.debug "Reachable commits after tag ${tag.fullName}: {}",
                                 reachableCommitLog*.abbreviatedId

                    def distance = reachableCommitLog.size()
                    data = [ tag: tag, distance: distance ]
                } else {
                    LOGGER.debug "Tag ${tag.name} is not an ancestor of HEAD. Excluding as a candidate."
                }
            }
            if (data) {
                LOGGER.debug "Tag data found: ${data}"

                list << data
            }
            list
        }

        Map nearest = versionTags.min { a, b ->
            a.distance <=> b.distance
        }

        nearest.tag
    }

}
