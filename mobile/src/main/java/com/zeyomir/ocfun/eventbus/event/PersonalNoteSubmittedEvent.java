package com.zeyomir.ocfun.eventbus.event;

import com.zeyomir.ocfun.network.response.PersonalNoteSubmissionResponse;

public class PersonalNoteSubmittedEvent extends Event {
    public final String cacheCode;
    public final PersonalNoteSubmissionResponse personalNoteSubmissionResponse;

    public PersonalNoteSubmittedEvent(String cacheCode, PersonalNoteSubmissionResponse personalNoteSubmissionResponse) {
        this.cacheCode = cacheCode;
        this.personalNoteSubmissionResponse = personalNoteSubmissionResponse;
    }
}
