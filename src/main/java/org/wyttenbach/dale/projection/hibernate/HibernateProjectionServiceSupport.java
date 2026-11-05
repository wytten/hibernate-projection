package org.wyttenbach.dale.projection.hibernate;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Expression;
import jakarta.persistence.criteria.Order;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import jakarta.persistence.criteria.Selection;

import org.hibernate.SessionFactory;
import org.springframework.transaction.annotation.Transactional;
import org.wyttenbach.dale.projection.QuerySpecification;
import org.wyttenbach.dale.projection.SearchSpecification;
import org.wyttenbach.dale.projection.Specification;
import org.wyttenbach.dale.projection.Transform;
import org.wyttenbach.dale.projection.Specification.Condition;
import org.wyttenbach.dale.projection.Specification.SortOrder;

@Transactional(readOnly = true)
public abstract class HibernateProjectionServiceSupport<DTO extends Serializable, PERSISTABLE> {

	private Class<PERSISTABLE> persistentClass;

	private SessionFactory sessionFactory;

	public HibernateProjectionServiceSupport(Class<PERSISTABLE> class1) {
		this.persistentClass = class1;
	}

	public void setSessionFactory(SessionFactory sessionFactory) {
		this.sessionFactory = sessionFactory;
	}

	protected SessionFactory getSessionFactory() {
		return sessionFactory;
	}

	protected abstract Transform<PERSISTABLE, DTO> getProjectionTransform();

	private <R> List<R> execute(Specification spec, CriteriaBuilder cb,
			CriteriaQuery<R> criteria, Root<PERSISTABLE> root) {

		// filter
		if (spec.getConditions() != null) {
			List<Predicate> predicates = new ArrayList<Predicate>();
			for (Condition condition : spec.getConditions()) {
				predicates.add(cb.equal(root.get(condition.property),
						condition.value));
			}
			criteria.where(predicates.toArray(new Predicate[0]));
		}

		// order
		if (spec.getSortOrders() != null) {
			List<Order> orders = new ArrayList<Order>();
			for (SortOrder sort : spec.getSortOrders()) {
				switch (sort.direction) {
				case ASCENDING:
					orders.add(cb.asc(root.get(sort.property)));
					break;
				case DESCENDING:
					orders.add(cb.desc(root.get(sort.property)));
					break;
				}
			}
			criteria.orderBy(orders);
		}

		// limit
		var query = sessionFactory.getCurrentSession().createQuery(criteria);
		if (spec.getMaxResults() != null) {
			query.setFirstResult(spec.getFirstResult());
			query.setMaxResults(spec.getMaxResults());
		}

		return query.getResultList();
	}

	public List<DTO> search(SearchSpecification spec) {
		CriteriaBuilder cb = sessionFactory.getCriteriaBuilder();
		CriteriaQuery<PERSISTABLE> criteria = cb.createQuery(persistentClass);
		Root<PERSISTABLE> root = criteria.from(persistentClass);
		criteria.select(root);

		List<PERSISTABLE> list = execute(spec, cb, criteria, root);
		List<DTO> results = new ArrayList<DTO>();
		for (PERSISTABLE p : list) {
			DTO dto = getProjectionTransform().doTransform(p);
			results.add(dto);
		}
		return results;
	}

	public List<?> query(QuerySpecification spec) {
		if (spec.getProjections() == null || spec.getProjections().isEmpty()) {
			return new ArrayList<Object>();
		}

		CriteriaBuilder cb = sessionFactory.getCriteriaBuilder();
		// an Object-typed query yields a scalar for a single selection and
		// Object[] for several, matching the old Hibernate ProjectionList
		CriteriaQuery<Object> criteria = cb.createQuery();
		Root<PERSISTABLE> root = criteria.from(persistentClass);

		List<Selection<?>> selections = new ArrayList<Selection<?>>();
		List<Expression<?>> groupings = new ArrayList<Expression<?>>();
		for (QuerySpecification.Projection projection : spec.getProjections()) {
			switch (projection.function) {
			case MAX:
				selections.add(cb.greatest(comparable(root, projection.property)));
				break;
			case MIN:
				selections.add(cb.least(comparable(root, projection.property)));
				break;
			case AVG:
				selections.add(cb.avg(root.<Number> get(projection.property)));
				break;
			case COUNT:
				selections.add(cb.count(root));
				break;
			case GROUP_BY:
				Expression<?> property = root.get(projection.property);
				selections.add(property);
				groupings.add(property);
				break;
			}
		}
		criteria.multiselect(selections);
		criteria.groupBy(groupings);

		return execute(spec, cb, criteria, root);
	}

	// MIN/MAX apply to any comparable property (e.g. dates), not just numbers
	@SuppressWarnings({ "rawtypes", "unchecked" })
	private static Expression<Comparable> comparable(Root<?> root, String property) {
		return (Expression) root.get(property);
	}

}
