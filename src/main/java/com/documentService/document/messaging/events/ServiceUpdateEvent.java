package com.documentService.document.messaging.events;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;


@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class ServiceUpdateEvent extends AbstractDocumentServiceEvent {

    private Long authorId;
    private Long documentId;
    private ServiceUpdateType updateType;

    @Override
    public DocumentServiceEventType getEventType() {
        return DocumentServiceEventType.SERVICE_UPDATE_EVENT;
    }


}
