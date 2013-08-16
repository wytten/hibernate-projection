package org.wyttenbach.dale.projection.hibernate;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.ProjectionList;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.springframework.orm.hibernate3.support.HibernateDaoSupport;
import org.wyttenbach.dale.projection.QuerySpecification;
import org.wyttenbach.dale.projection.SearchSpecification;
import org.wyttenbach.dale.projection.Specification;
import org.wyttenbach.dale.projection.Transform;
import org.wyttenbach.dale.projection.Specification.Condition;
import org.wyttenbach.dale.projection.Specification.SortOrder;

public abstract class HibernateProjectionServiceSupport<DTO extends Serializable, PERSISTABLE> {

	private Class<PERSISTABLE> persistentClass;

	public HibernateProjectionServiceSupport(Class<PERSISTABLE> class1) {
		this.persistentClass = class1;
	}

	protected abstract HibernateDaoSupport getProjectionDAO();

	protected abstract Transform<PERSISTABLE, DTO> getProjectionTransform();

	private List<?> execute(Specification spec, ProjectionList projections) {
		DetachedCriteria criteria = DetachedCriteria.forClass(persistentClass);

		// project
		if (projections != null && projections.getLength() > 0) {
			criteria.setProjection(projections);
		}

		// filter
		if (spec.getConditions() != null) {
			for (Condition condition : spec.getConditions()) {
				criteria.add(Restrictions.eq(condition.property,
						condition.value));
			}
		}

		// order
		if (spec.getSortOrders() != null) {
			for (SortOrder sort : spec.getSortOrders()) {
				switch (sort.direction) {
				case ASCENDING:
					criteria.addOrder(Order.asc(sort.property));
					break;
				case DESCENDING:
					criteria.addOrder(Order.desc(sort.property));
					break;
				}
			}
		}

		// limit
		if (spec.getMaxResults() != null) {
			return getProjectionDAO().getHibernateTemplate().findByCriteria(criteria,
					spec.getFirstResult(), spec.getMaxResults());
		}

		return getProjectionDAO().getHibernateTemplate().findByCriteria(criteria);
	}

	@SuppressWarnings("unchecked")
	public List<DTO> search(SearchSpecification spec) {
		List<PERSISTABLE> list = (List<PERSISTABLE>) execute(spec, null);
		List<DTO> results = new ArrayList<DTO>();
		for (PERSISTABLE p : list) {
			DTO dto = getProjectionTransform().doTransform(p);
			results.add(dto);
		}
		return results;
	}

	@SuppressWarnings("unchecked")
	public List<?> query(QuerySpecification spec) {
		List<?> results = new ArrayList();
		if (spec.getProjections() != null && spec.getProjections().size() > 0) {
			ProjectionList projections = Projections.projectionList();
			for (QuerySpecification.Projection projection : spec
					.getProjections()) {
				switch (projection.function) {
				case MAX:
					projections.add(Projections.max(projection.property));
					break;
				case MIN:
					projections.add(Projections.min(projection.property));
					break;
				case AVG:
					projections.add(Projections.avg(projection.property));
					break;
				case COUNT:
					projections.add(Projections.rowCount());
					break;
				case GROUP_BY:
					projections.add(Projections
							.groupProperty(projection.property));
					break;
				}
			}
			results = execute(spec, projections);
		}

		return results;
	}

}
