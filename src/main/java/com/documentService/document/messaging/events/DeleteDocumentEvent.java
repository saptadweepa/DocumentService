package com.documentService.document.messaging.events;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@EqualsAndHashCode(callSuper = true)
@Data
@NoArgsConstructor
@AllArgsConstructor
public class DeleteDocumentEvent extends AbstractDocumentServiceEvent {

    private long documentId;

    @Override
    public DocumentServiceEventType getEventType() {
        return DocumentServiceEventType.DELETE_DOCUMENT_EVENT;
    }
}
