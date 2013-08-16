package org.wyttenbach.dale.projection;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class QuerySpecification extends Specification implements Serializable {

	private static final long serialVersionUID = -3528285798937633300L;

	public static class Projection {
		public final static Projection ROW_COUNT = new Projection(null, Function.COUNT);
		public Projection(String property, QuerySpecification.Function function) {
			this.property = property;
			this.function = function;
		}
		public String property;
		public QuerySpecification.Function function;
	}

	public static enum Function {
		MIN, MAX, AVG, COUNT, GROUP_BY
	}
	
	private List<QuerySpecification.Projection> projections = new ArrayList<QuerySpecification.Projection>();

	public void addProjection(QuerySpecification.Projection projection) {
		projections.add(projection);
	}
	
	public List<QuerySpecification.Projection> getProjections() {
		return projections;
	}
}
