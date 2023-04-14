package org.jco.spring.domain.crudify.repository.dao.mongodb;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import org.jco.spring.domain.crudify.repository.dao.ISpringCrudifyDAORepository;
import org.jco.spring.domain.crudify.repository.dto.ISpringCrudifyDTOObject;
import org.jco.spring.domain.crudify.spec.filter.SpringCrudifyLiteral;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;

@EnableMongoRepositories
public abstract class AsbtractSpringCrudifyMongoRepository<T extends ISpringCrudifyDTOObject<?>>
		implements ISpringCrudifyDAORepository<T> {

	@Inject
	private MongoTemplate mongo;

	@Override
	public T save(T object) {
		return this.mongo.save(object);
	}

	@Override
	public List<T> findByTenantId(String tenantId, Pageable pageable, SpringCrudifyLiteral filter) {
		List<T> results = new ArrayList<>();

		Query query = new Query().addCriteria(Criteria.where("tenantId").is(tenantId));

		if (filter != null) {
			Criteria criteria = AsbtractSpringCrudifyMongoRepository.getCriteriaFromFilter(filter);
			query.addCriteria(criteria);
		}

		if (pageable != null) {
			query.with(pageable);
		}

		results = this.mongo.find(query, this.getDTOClass());

		return results;
	}

	/*
	 * Not very elegant, must be refactored
	 */
	private static Criteria getCriteriaFromFilter(SpringCrudifyLiteral literal) {

		Criteria criteria = null;
		
		List<Criteria> criterias = new ArrayList<Criteria>();
		List<String> values = new ArrayList<String>();
		
		switch (literal.getName()) {
		case SpringCrudifyLiteral.OPERATOR_OR:
			for( SpringCrudifyLiteral subliteral: literal.getLiterals() ) {
				criterias.add(getCriteriaFromFilter(subliteral));
			}
			criteria = new Criteria().orOperator(criterias);
			break;
		case SpringCrudifyLiteral.OPERATOR_AND:
			for( SpringCrudifyLiteral subliteral: literal.getLiterals() ) {
				criterias.add(getCriteriaFromFilter(subliteral));
			}
			criteria = new Criteria().andOperator(criterias);
			break;
		case SpringCrudifyLiteral.OPERATOR_NOR:
			for( SpringCrudifyLiteral subliteral: literal.getLiterals() ) {
				criterias.add(getCriteriaFromFilter(subliteral));
			}
			criteria = new Criteria().norOperator(criterias);
			break;
		case SpringCrudifyLiteral.OPERATOR_FIELD:
			
			SpringCrudifyLiteral subLiteral = literal.getLiterals().get(0);
			
			switch(subLiteral.getName()) {
			case SpringCrudifyLiteral.OPERATOR_EQUAL:
				criteria = Criteria.where(literal.getValue()).is(subLiteral.getValue());
				break;
			case SpringCrudifyLiteral.OPERATOR_NOT_EQUAL:
				criteria = Criteria.where(literal.getValue()).ne(subLiteral.getValue());
				break;
			case SpringCrudifyLiteral.OPERATOR_GREATER_THAN:
				criteria = Criteria.where(literal.getValue()).gt(subLiteral.getValue());
				break;
			case SpringCrudifyLiteral.OPERATOR_GREATER_THAN_EXCLUSIVE:
				criteria = Criteria.where(literal.getValue()).gte(subLiteral.getValue());
				break;
			case SpringCrudifyLiteral.OPERATOR_LOWER_THAN:
				criteria = Criteria.where(literal.getValue()).lt(subLiteral.getValue());
				break;
			case SpringCrudifyLiteral.OPERATOR_LOWER_THAN_EXCLUSIVE:
				criteria = Criteria.where(literal.getValue()).lte(subLiteral.getValue());
				break;
			case SpringCrudifyLiteral.OPERATOR_REGEX:
				criteria = Criteria.where(literal.getValue()).regex(subLiteral.getValue());
				break;
			case SpringCrudifyLiteral.OPERATOR_IN:
				for( SpringCrudifyLiteral subliteral: literal.getLiterals() ) {
					values.add(subliteral.getValue());
				}
				criteria = Criteria.where(literal.getValue()).in(values);
				break;
			case SpringCrudifyLiteral.OPERATOR_NOT_IN:
				for( SpringCrudifyLiteral subliteral: literal.getLiterals() ) {
					values.add(subliteral.getValue());
				}
				criteria = Criteria.where(literal.getValue()).nin(values);
				break;
			case SpringCrudifyLiteral.OPERATOR_EMPTY:
				criteria = Criteria.where(literal.getValue()).isNullValue();
				break;
			}
			break;
		}

		return criteria;
	}

	@Override
	public T findOneByUuidAndTenantId(String uuid, String tenantId) {
		Query query = new Query().addCriteria(Criteria.where("tenantId").is(tenantId).and("uuid").is(uuid));
		return this.mongo.findOne(query, this.getDTOClass());
	}

	@Override
	public T findOneByIdAndTenantId(String id, String tenantId) {
		Query query = new Query().addCriteria(Criteria.where("tenantId").is(tenantId).and("id").is(id));
		return this.mongo.findOne(query, this.getDTOClass());
	}

	@Override
	public void delete(T object) {

		this.mongo.findAndRemove(null, this.getDTOClass());
	}

	@Override
	public long countByTenantId(String tenantId) {
		return this.mongo.count(new Query().addCriteria(Criteria.where("tenantId").is(tenantId)), this.getDTOClass());
	}

	protected abstract Class<T> getDTOClass();
}
