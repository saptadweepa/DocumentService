package com.documentService.document.messaging.events;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@EqualsAndHashCode(callSuper = true)
@Data
@NoArgsConstructor
@AllArgsConstructor
public class DeleteAuthorEvent extends AbstractDocumentServiceEvent {

    private long authorId;

    @Override
    public DocumentServiceEventType getEventType() {
        return DocumentServiceEventType.DELETE_AUTHOR_EVENT;
    }
}
