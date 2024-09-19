package com.documentService.document.messaging.events;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@EqualsAndHashCode(callSuper = true)
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreateAuthorEvent extends AbstractDocumentServiceEvent {


    private String firstName;
    private String lastName;
    private String username;
    private String password;

    @Override
    public DocumentServiceEventType getEventType() {
        return DocumentServiceEventType.CREATE_AUTHOR_EVENT;
    }
}
