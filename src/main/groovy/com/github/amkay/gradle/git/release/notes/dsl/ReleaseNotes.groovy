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

/**
 * A holder for configuring the extraction of various categories of release notes, e.g. <em>new features</em> and
 * em>bugfixes</em>.
 *
 * @author Max Käufer
 */
class ReleaseNotes {

    /**
     * A regular expression that commit messages <strong>must</strong> match to be included as a release note.
     */
    String include

    /**
     * A regular expression that commit messages <strong>must not</strong> match to be included as a release note.
     * All commit messages &ndash; whether they match the regular expression set in <code>include</code> or not
     * &ndash; that match this expression are excluded.
     */
    String exclude

    /**
     * A regular expression that is used to remove text from the extracted commit messages. All substring that match
     * this expression are removed from the commit messages.
     */
    String remove


    ReleaseNotes(final String include, final String exclude, final String remove) {
        setInclude include
        setExclude exclude
        setRemove remove
    }


    /**
     * Helper method to allow keyword-based configuration of the <code>include</code> property.
     * @param include
     */
    @SuppressWarnings('ConfusingMethodName')
    void include(final String include) {
        setInclude include
    }

    /**
     * Helper method to allow keyword-based configuration of the <code>exclude</code> property.
     * @param exclude
     */
    @SuppressWarnings('ConfusingMethodName')
    void exclude(final String exclude) {
        setExclude exclude
    }

    /**
     * Helper method to allow keyword-based configuration of the <code>remove</code> property.
     * @param remove
     */
    @SuppressWarnings('ConfusingMethodName')
    void remove(final String remove) {
        setRemove remove
    }

}
