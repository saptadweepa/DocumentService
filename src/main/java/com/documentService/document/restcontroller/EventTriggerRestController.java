package com.documentService.document.restcontroller;

import com.documentService.document.KafkaEventPublisher;
import com.documentService.document.messaging.events.CreateAuthorEvent;
import com.documentService.document.messaging.events.CreateDocumentEvent;
import com.documentService.document.messaging.events.DeleteAuthorEvent;
import com.documentService.document.messaging.events.DeleteDocumentEvent;
import com.documentService.document.restcontroller.dto.AuthorWriteDTO;
import com.documentService.document.restcontroller.dto.DocumentDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RequestMapping("/api/v1/event-trigger")
@RestController
@AllArgsConstructor
public class EventTriggerRestController {

    @Autowired
    private KafkaEventPublisher publisher;

    @Operation(summary = "Create a new author event",
            description = "Publishes an event to create a new author.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Event successfully published"),
                    @ApiResponse(responseCode = "400", description = "Invalid input")
            })
    @PostMapping("/create-author")
    public void createAuthor(@RequestBody @Valid AuthorWriteDTO request) {
        CreateAuthorEvent event = new CreateAuthorEvent(
                request.getFirstName(),
                request.getLastName(),
                request.getUsername(),
                request.getPassword()
        );

        publisher.publish(event);
    }

    @Operation(summary = "Create a new document event",
            description = "Publishes an event to create a new document.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Event successfully published"),
                    @ApiResponse(responseCode = "400", description = "Invalid input")
            })
    @PostMapping("/create-document")
    public void createDocument(@RequestBody @Valid DocumentDTO request) {
        CreateDocumentEvent event = new CreateDocumentEvent(
                request.getTitle(),
                request.getBody(),
                request.getAuthorId(),
                request.getReferenceIds()
        );

        publisher.publish(event);
    }

    @Operation(summary = "Delete an author event",
            description = "Publishes an event to delete an author.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Event successfully published"),
                    @ApiResponse(responseCode = "400", description = "Invalid input")
            })
    @DeleteMapping("/delete-author/{authorId}")
    public void deleteAuthor(@PathVariable long authorId) {
        DeleteAuthorEvent event = new DeleteAuthorEvent(authorId);

        publisher.publish(event);
    }

    @Operation(summary = "Delete a document event",
            description = "Publishes an event to delete a document.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Event successfully published"),
                    @ApiResponse(responseCode = "400", description = "Invalid input")
            })
    @DeleteMapping("/delete-document/{documentId}")
    public void deleteDocument(@PathVariable long documentId) {
        DeleteDocumentEvent event = new DeleteDocumentEvent(documentId);

        publisher.publish(event);
    }
}
