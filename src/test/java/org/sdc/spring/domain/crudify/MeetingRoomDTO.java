package org.sdc.spring.domain.crudify;

import org.sdc.spring.domain.crudify.repository.dto.AbstractSpringCrudifyDTOObject;
import org.sdc.spring.domain.crudify.repository.dto.ISpringCrudifyDTOFactory;
import org.sdc.spring.domain.crudify.repository.dto.ISpringCrudifyDTOObject;
import org.springframework.data.mongodb.core.mapping.Document;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "meetingRooms")
public class MeetingRoomDTO extends AbstractSpringCrudifyDTOObject<MeetingRoomEntity> {
	
	@JsonProperty
	private String name;
	
	@JsonProperty
	private String location;
	
	@JsonProperty
	private String[] facilities;
	
	public MeetingRoomDTO(String tenantId, MeetingRoomEntity entity) {
		super(tenantId, entity);
	}

	@Override
	public void create(MeetingRoomEntity entity) {
		this.name = entity.getName();
		this.location = entity.getLocation();
		this.facilities = entity.getFacilities();
	}

	@Override
	public MeetingRoomEntity convert() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void update(ISpringCrudifyDTOObject<MeetingRoomEntity> object) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public ISpringCrudifyDTOFactory<MeetingRoomEntity, MeetingRoomDTO> getFactory() {
		ISpringCrudifyDTOFactory<MeetingRoomEntity, MeetingRoomDTO> factory = new ISpringCrudifyDTOFactory<MeetingRoomEntity, MeetingRoomDTO>() {
			
			@Override
			public MeetingRoomDTO newInstance(String tenantId, MeetingRoomEntity entity) {
				return new MeetingRoomDTO(tenantId, entity);
			}
		};
		return factory ;
	}


}
