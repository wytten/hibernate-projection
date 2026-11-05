package org.wyttenbach.dale.document.dao.hibernate;

import java.io.Serializable;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.transaction.annotation.Transactional;
import org.wyttenbach.dale.document.dao.DocumentDAO;
import org.wyttenbach.dale.document.model.Document;

@Transactional
public class HibernateDocumentDAO implements DocumentDAO {

	private SessionFactory sessionFactory;

	public void setSessionFactory(SessionFactory sessionFactory) {
		this.sessionFactory = sessionFactory;
	}

	private Session currentSession() {
		return sessionFactory.getCurrentSession();
	}

	public Serializable create(Document document) {
		currentSession().persist(document);
		return document.getId();
	}

	@Transactional(readOnly = true)
	public Document read(Long documentId) {
		return currentSession().get(Document.class, documentId);
	}

	public void update(Document document) {
		currentSession().merge(document);
	}

	public void delete(Document document) {
		Document persistent = currentSession().get(Document.class, document.getId());
		if (persistent != null) {
			currentSession().remove(persistent);
		}
	}

}
