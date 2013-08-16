package org.wyttenbach.dale.document.transform;

import org.wyttenbach.dale.document.dto.DocumentDTO;
import org.wyttenbach.dale.document.model.Document;
import org.wyttenbach.dale.projection.Transform;

public class DocumentToDocumentDTO implements Transform<Document, DocumentDTO> {

	public DocumentDTO doTransform(Document document) {
		DocumentDTO dto = new DocumentDTO();
		dto.setBusSeg(document.getBusSeg());
		dto.setExternalId(document.getExternalId());
		dto.setId(document.getId());
		dto.setNote(document.getNote());
		dto.setReceivedDate(document.getReceivedDate());
		dto.setSize(document.getSize());
		return dto;
	}

}
