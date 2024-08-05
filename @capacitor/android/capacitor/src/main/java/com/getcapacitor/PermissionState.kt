package com.getcapacitor

/**
 * Represents the state of a permission
 *
 * @since 3.0.0
 */
enum class PermissionState(private val state: String) {
    GRANTED("granted"),
    DENIED("denied"),
    PROMPT("prompt"),
    PROMPT_WITH_RATIONALE("prompt-with-rationale");

    override fun toString(): String {
        return state
    }

    companion object {
        fun byState(state: String): PermissionState {
            var state = state
            state = state.uppercase().replace('-', '_')
            return valueOf(state)
        }
    }
}
