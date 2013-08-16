package org.wyttenbach.dale.document.service;

import org.wyttenbach.dale.document.dto.DocumentDTO;
import org.wyttenbach.dale.projection.ProjectionService;


public interface DocumentService extends ProjectionService<DocumentDTO> {
	
	void addDocument(DocumentDTO document);
	
	DocumentDTO getDocument(Long documentId);
	
	void updateDocument(DocumentDTO document);

	void deleteDocument(DocumentDTO document);

}
