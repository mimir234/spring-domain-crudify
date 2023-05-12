package org.jco.spring.domain.crudify.engine;

import org.jco.spring.domain.crudify.repository.dao.SpringCrudifyDao;
import org.jco.spring.domain.crudify.spec.ISpringCrudifyEntity;
import org.jco.spring.domain.crudify.spec.ISpringCrudifyEntityFactory;
import org.junit.jupiter.api.Test;

@SpringCrudifyEntity (
		dto="",
		db=SpringCrudifyDao.mongo,
		authorize_creation=true,
		authorize_read_all=true,
		authorize_read_one=true,
		authorize_update_one=true,
		authorize_delete_all=true,
		authorize_delete_one=true,
		authorize_count=true		
)
public class TestEntity implements ISpringCrudifyEntity {

	@Override
	public String getId() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void setId(String id) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public String getUuid() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void setUuid(String uuid) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public String getDomain() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ISpringCrudifyEntityFactory<? extends ISpringCrudifyEntity> getFactory() {
		// TODO Auto-generated method stub
		return null;
	}

	
	@Test
	public void test() {
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
	}
}
