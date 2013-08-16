package org.wyttenbach.dale.document.service.hibernate;

import java.sql.Timestamp;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit38.AbstractJUnit38SpringContextTests;
import org.wyttenbach.dale.document.dao.DocumentDAO;
import org.wyttenbach.dale.document.dto.DocumentDTO;
import org.wyttenbach.dale.document.service.DocumentService;
import org.wyttenbach.dale.projection.QuerySpecification;
import org.wyttenbach.dale.projection.SearchSpecification;
import org.wyttenbach.dale.projection.QuerySpecification.Projection;
import org.wyttenbach.dale.projection.Specification.Condition;
import org.wyttenbach.dale.projection.Specification.SortOrder;

@ContextConfiguration
public class HibernateDocumentServiceTest extends AbstractJUnit38SpringContextTests {

	private static final String FEDERAL = "Federal";

	private static final String COMMERCIAL = "Commercial";

	private static final String STATE = "State";

	@Autowired
	private DocumentService documentService;
	
	private DocumentDTO addDocument(String busSeg, String documentId, String note, Date receivedDate, Long size) {
		DocumentDTO document = new DocumentDTO();
		document.setBusSeg(busSeg);
		document.setExternalId(documentId);
		document.setNote(note);
		document.setReceivedDate(receivedDate);
		document.setSize(size);
		documentService.addDocument(document);
		return document;
	}
	
	@Override
	protected void setUp() throws Exception {
		int i=0;
		addDocument(FEDERAL, "" + ++i, "Hello y", getDate(i), 100L);
		addDocument(STATE, "" + ++i, "Hello 3", getDate(i), 100L);
		addDocument(STATE, "" + ++i, "Hello z", getDate(i), 200L);
		addDocument(STATE, "" + ++i, "Hello 1", getDate(i), 300L);
		addDocument(STATE, "" + ++i, "Hello a", getDate(i), 400L);
		addDocument(STATE, "" + ++i, "Hello 2", getDate(i), 500L);
		addDocument(STATE, "" + ++i, "Hello ?", getDate(i), 600L);
		addDocument(COMMERCIAL, "" + ++i, "Hello", getDate(i), 100L);
	}

	private Date getDate(int i) {
		Calendar today = Calendar.getInstance();
		today.add(Calendar.DATE, -i);
		return today.getTime();
	}

	@Override
	protected void tearDown() throws Exception {
		List<DocumentDTO> docs = (List<DocumentDTO>) documentService.search(new SearchSpecification());
		for (DocumentDTO document : docs) {
			documentService.deleteDocument(document);
		}
	}

	public void testSearch() throws Exception {
		SearchSpecification spec = new SearchSpecification();
		spec.addCondition(new Condition(DocumentDAO.BUS_SEG, STATE));
		List<DocumentDTO> results = (List<DocumentDTO>) documentService.search(spec);
		assertEquals(6, results.size());
	}
	
	public void testSearchOrderBy() throws Exception {
		SearchSpecification spec = new SearchSpecification();
		spec.addSortOrder(new SortOrder(DocumentDAO.BUS_SEG));
		List<DocumentDTO> results = (List<DocumentDTO>) documentService.search(spec);
		assertEquals(8, results.size());
		DocumentDTO document;
		
		document = results.get(0);
		assertEquals(COMMERCIAL, document.getBusSeg());
		
		document = results.get(7);
		assertEquals(STATE, document.getBusSeg());
	}

	public void testSearchOrderByNote() throws Exception {
		SearchSpecification spec = new SearchSpecification();
		spec.addSortOrder(new SortOrder(DocumentDAO.NOTE));
		List<DocumentDTO> results = (List<DocumentDTO>) documentService.search(spec);
		assertEquals(8, results.size());
		DocumentDTO document;
		
		document = results.get(0);
		assertEquals("Hello", document.getNote());
		
		document = results.get(7);
		assertEquals("Hello z", document.getNote());
	}

	public void testSearchOrderByDate() throws Exception {
		SearchSpecification spec = new SearchSpecification();
		spec.addSortOrder(new SortOrder(DocumentDAO.RCVD_DATE));
		List<DocumentDTO> results = (List<DocumentDTO>) documentService.search(spec);
		assertEquals(8, results.size());
		DocumentDTO document;
		
		document = results.get(0);
		assertEquals(COMMERCIAL, document.getBusSeg());
		
		document = results.get(7);
		assertEquals(FEDERAL, document.getBusSeg());
	}

	public void testSearchOrderByDateMax() throws Exception {
		SearchSpecification spec = new SearchSpecification();
		spec.addSortOrder(new SortOrder(DocumentDAO.RCVD_DATE));
		spec.setMaxResults(2);
		List<DocumentDTO> results = (List<DocumentDTO>) documentService.search(spec);
		assertEquals(2, results.size());
		DocumentDTO document;
		
		document = results.get(0);
		assertEquals(COMMERCIAL, document.getBusSeg());
		
		document = results.get(1);
		assertEquals(STATE, document.getBusSeg());
	}
	
	public void testQueryEmpty() throws Exception {
		QuerySpecification spec = new QuerySpecification();
	    List<?> results = documentService.query(spec);
		assertEquals(0, results.size());
	}

	@SuppressWarnings("unchecked")
	public void testQueryCount() throws Exception {
		QuerySpecification spec = new QuerySpecification();
		spec.addCondition(new Condition(DocumentDAO.BUS_SEG, STATE));
		spec.addProjection(Projection.ROW_COUNT);
		List<Integer> results = (List<Integer>) documentService.query(spec);
		assertEquals(1, results.size());
		int count = results.get(0);
		assertEquals(6, count);
	}

	@SuppressWarnings("unchecked")
	public void testQueryMinGroupBy() throws Exception {
		QuerySpecification spec = new QuerySpecification();
		spec.addProjection(Projection.ROW_COUNT);
		spec.addProjection(new QuerySpecification.Projection(DocumentDAO.BUS_SEG, QuerySpecification.Function.GROUP_BY));
		spec.addProjection(new QuerySpecification.Projection(DocumentDAO.RCVD_DATE, QuerySpecification.Function.MIN));
		List<Object[]> results = (List<Object[]>) documentService.query(spec);
		assertEquals(3, results.size());
		
		int i=0;
		Object[] row;
		
		row = results.get(i++);
		assertEquals(1, row[0]);
		assertEquals(FEDERAL, row[1]);
		assertEquals(Timestamp.class, row[2].getClass());

		row = results.get(i++);
		assertEquals(6, row[0]);
		assertEquals(STATE, row[1]);
		assertEquals(Timestamp.class, row[2].getClass());
		
		row = results.get(i++);
		assertEquals(1, row[0]);
		assertEquals(COMMERCIAL, row[1]);
		assertEquals(Timestamp.class, row[2].getClass());
	}

	@SuppressWarnings("unchecked")
	public void testQueryAveGroupBy() throws Exception {
		QuerySpecification spec = new QuerySpecification();
		spec.addProjection(Projection.ROW_COUNT);
		spec.addProjection(new QuerySpecification.Projection(DocumentDAO.BUS_SEG, QuerySpecification.Function.GROUP_BY));
		spec.addProjection(new QuerySpecification.Projection(DocumentDAO.SIZE, QuerySpecification.Function.AVG));
		List<Object[]> results = (List<Object[]>) documentService.query(spec);
		assertEquals(3, results.size());
		
		int i=0;
		Object[] row;
		
		row = results.get(i++);
		assertEquals(1, row[0]);
		assertEquals(FEDERAL, row[1]);
		assertEquals(Double.class, row[2].getClass());

		row = results.get(i++);
		assertEquals(6, row[0]);
		assertEquals(STATE, row[1]);
		assertEquals(Double.class, row[2].getClass());
		
		row = results.get(i++);
		assertEquals(1, row[0]);
		assertEquals(COMMERCIAL, row[1]);
		assertEquals(Double.class, row[2].getClass());
	}

}
