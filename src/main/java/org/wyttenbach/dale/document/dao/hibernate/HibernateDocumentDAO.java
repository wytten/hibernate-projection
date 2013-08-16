package org.wyttenbach.dale.document.dao.hibernate;

import java.io.Serializable;

import org.springframework.orm.hibernate3.support.HibernateDaoSupport;
import org.wyttenbach.dale.document.dao.DocumentDAO;
import org.wyttenbach.dale.document.model.Document;

public class HibernateDocumentDAO extends HibernateDaoSupport implements
		DocumentDAO {

	public Serializable create(Document document) {
		return getHibernateTemplate().save(document);
	}

	public Document read(Long documentId) {
		return (Document) getHibernateTemplate()
				.get(Document.class, documentId);
	}

	public void update(Document document) {
		getHibernateTemplate().update(document);
	}

	public void delete(Document document) {
		getHibernateTemplate().delete(document);
	}

}
