package org.wyttenbach.dale.document.service.hibernate;

import org.springframework.transaction.annotation.Transactional;
import org.wyttenbach.dale.document.dao.DocumentDAO;
import org.wyttenbach.dale.document.dto.DocumentDTO;
import org.wyttenbach.dale.document.model.Document;
import org.wyttenbach.dale.document.service.DocumentService;
import org.wyttenbach.dale.projection.Transform;
import org.wyttenbach.dale.projection.hibernate.HibernateProjectionServiceSupport;

@Transactional
public class HibernateDocumentService extends
		HibernateProjectionServiceSupport<DocumentDTO, Document> implements
		DocumentService {

	private DocumentDAO documentDAO;

	private Transform<DocumentDTO, Document> documentDTOToDocument;

	private Transform<Document, DocumentDTO> documentToDocumentDTO;

	public HibernateDocumentService() {
		super(Document.class);
	}

	public void setDocumentDAO(DocumentDAO documentDAO) {
		this.documentDAO = documentDAO;
	}

	public void setDocumentDTOToDocument(
			Transform<DocumentDTO, Document> documentDTOToDocument) {
		this.documentDTOToDocument = documentDTOToDocument;
	}

	public void setDocumentToDocumentDTO(
			Transform<Document, DocumentDTO> documentToDocumentDTO) {
		this.documentToDocumentDTO = documentToDocumentDTO;
	}

	public void addDocument(DocumentDTO dto) {
		Document document = documentDTOToDocument.doTransform(dto);
		documentDAO.create(document);
	}

	@Transactional(readOnly = true)
	public DocumentDTO getDocument(Long documentId) {
		Document document = documentDAO.read(documentId);
		DocumentDTO dto = documentToDocumentDTO.doTransform(document);
		return dto;
	}

	public void updateDocument(DocumentDTO dto) {
		Document document = documentDTOToDocument.doTransform(dto);
		documentDAO.update(document);
	}

	public void deleteDocument(DocumentDTO dto) {
		Document document = new Document();
		document.setId(dto.getId());
		documentDAO.delete(document);
	}

	@Override
	protected Transform<Document, DocumentDTO> getProjectionTransform() {
		return documentToDocumentDTO;
	}

}
