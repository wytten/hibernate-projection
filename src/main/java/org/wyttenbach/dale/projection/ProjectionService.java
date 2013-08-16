package org.wyttenbach.dale.projection;

import java.io.Serializable;
import java.util.List;


public interface ProjectionService<DTO extends Serializable> {
	
	List<DTO> search(SearchSpecification spec);

	List<?> query(QuerySpecification spec);

}
