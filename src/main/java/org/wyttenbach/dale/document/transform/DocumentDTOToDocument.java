package org.wyttenbach.dale.document.transform;

import org.wyttenbach.dale.document.dto.DocumentDTO;
import org.wyttenbach.dale.document.model.Document;
import org.wyttenbach.dale.projection.Transform;

public class DocumentDTOToDocument implements Transform<DocumentDTO, Document> {

	public Document doTransform(DocumentDTO dto) {
		Document document = new Document();
		document.setBusSeg(dto.getBusSeg());
		document.setExternalId(dto.getExternalId());
		document.setId(dto.getId());
		document.setNote(dto.getNote());
		document.setReceivedDate(dto.getReceivedDate());
		document.setSize(dto.getSize());
		return document;
	}

}
