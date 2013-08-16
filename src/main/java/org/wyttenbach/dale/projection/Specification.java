package org.wyttenbach.dale.projection;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public abstract class Specification implements Serializable {

	private static final long serialVersionUID = 9216963587342382877L;

	public static class Condition {
		public Condition(String property, Object value) {
			this.property = property;
			this.value = value;
		}
		public String property;
		public Object value;
	}

	public static class SortOrder {
		public SortOrder(String property) {
			this.property = property;
			this.direction = Direction.ASCENDING;
		}
		public SortOrder(String property, Direction direction) {
			this.property = property;
			this.direction = direction;
		}
		public String property;
		public Direction direction;
	}
	
	public static enum Direction {
		ASCENDING, DESCENDING;
	}
	
	private Integer firstResult;
	
	private Integer maxResults;

	private Set<SearchSpecification.Condition> conditions = new HashSet<SearchSpecification.Condition>();

	private List<SortOrder> sorts = new ArrayList<SortOrder>();
	
	public void addSortOrder(SortOrder sort) {
		sorts.add(sort);
	}

	public List<SortOrder> getSortOrders() {
		return sorts;
	}

	public Set<SearchSpecification.Condition> getConditions() {
		return conditions;
	}

	public void addCondition(SearchSpecification.Condition condition) {
		conditions.add(condition);
	}
	
	public Integer getFirstResult() {
		return (firstResult != null) ? firstResult : 0;
	}

	public void setFirstResult(Integer firstResult) {
		this.firstResult = firstResult;
	}

	public Integer getMaxResults() {
		return maxResults;
	}

	public void setMaxResults(Integer maxResults) {
		this.maxResults = maxResults;
	}
}
