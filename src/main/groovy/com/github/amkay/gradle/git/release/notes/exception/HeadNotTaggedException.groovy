package com.github.amkay.gradle.git.release.notes.exception

import org.gradle.api.GradleException

/**
 * TODO
 *
 * @author Max Käufer
 */
class HeadNotTaggedException extends GradleException {

    HeadNotTaggedException() {
        super()
    }

    HeadNotTaggedException(final String message) {
        super(message)
    }

    HeadNotTaggedException(final String message, final Throwable cause) {
        super(message, cause)
    }

}
