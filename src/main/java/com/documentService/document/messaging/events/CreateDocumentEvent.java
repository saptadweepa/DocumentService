package com.documentService.document.messaging.events;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.util.Set;

@EqualsAndHashCode(callSuper = true)
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreateDocumentEvent extends AbstractDocumentServiceEvent {

    private String title;
    private String body;
    private Long authorId;
    private Set<Long> referenceIds;

    @Override
    public DocumentServiceEventType getEventType() {
        return DocumentServiceEventType.CREATE_DOCUMENT_EVENT;
    }
}
