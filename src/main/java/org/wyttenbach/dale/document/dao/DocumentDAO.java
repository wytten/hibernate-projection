package org.wyttenbach.dale.document.dao;

import java.io.Serializable;

import org.wyttenbach.dale.document.model.Document;


public interface DocumentDAO {
	
    // public constants
    public static final String BUS_SEG = "busSeg";
    public static final String NOTE = "note";
    public static final String EXTERNAL_ID = "externalId";
    public static final String RCVD_DATE = "receivedDate";
    public static final String SIZE = "size";
	
	Serializable create(Document document);

	Document read(Long documentId);

	void update(Document document);

	void delete(Document document);

}
