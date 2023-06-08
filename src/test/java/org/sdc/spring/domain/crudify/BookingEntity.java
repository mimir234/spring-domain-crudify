package org.sdc.spring.domain.crudify;

import java.util.Date;

import org.sdc.spring.domain.crudify.engine.SpringCrudifyEntity;
import org.sdc.spring.domain.crudify.spec.AbstractSpringCrudifyEntity;
import org.sdc.spring.domain.crudify.spec.ISpringCrudifyEntityFactory;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SpringCrudifyEntity(dto = "org.sdc.spring.domain.crudify.BookingDTO")
public class BookingEntity extends AbstractSpringCrudifyEntity {
	
	private static String domain = "bookings";
	
	private Date from;
	
	private Date to;
	
	private String meetingRoomUuid; 
	
	private String ownerName;
	 
	private String ownerMail;

	@Override
	public ISpringCrudifyEntityFactory<BookingEntity> getFactory() {
		return new ISpringCrudifyEntityFactory<BookingEntity>() {
			
			@Override
			public BookingEntity newInstance(String uuid) {
				BookingEntity entity = new BookingEntity();
				entity.setUuid(uuid);
				return entity;
			}
			
			@Override
			public BookingEntity newInstance() {
				return new BookingEntity();
			}
		};
	}

	@Override
	public String getOpenApiSchema() {
		return null;
	}

	@Override
	public String getDomain() {
		return domain;
	}

}
