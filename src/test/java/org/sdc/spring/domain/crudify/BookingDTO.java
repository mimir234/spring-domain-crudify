package org.sdc.spring.domain.crudify;

import org.sdc.spring.domain.crudify.repository.dto.AbstractSpringCrudifyDTOObject;
import org.sdc.spring.domain.crudify.repository.dto.ISpringCrudifyDTOFactory;
import org.sdc.spring.domain.crudify.repository.dto.ISpringCrudifyDTOObject;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "bookings")
public class BookingDTO extends AbstractSpringCrudifyDTOObject<BookingEntity> {

	public BookingDTO(String tenantId, BookingEntity entity) {
		super(tenantId, entity);
	}
	
	@Override
	public void create(BookingEntity entity) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public BookingEntity convert() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void update(ISpringCrudifyDTOObject<BookingEntity> object) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public ISpringCrudifyDTOFactory<BookingEntity, BookingDTO> getFactory() {
		return new ISpringCrudifyDTOFactory<BookingEntity, BookingDTO>() {
			
			@Override
			public BookingDTO newInstance(String tenantId, BookingEntity entity) {
				BookingDTO dto = new BookingDTO(tenantId, entity);
				return dto;
			}
		};
	}

}
