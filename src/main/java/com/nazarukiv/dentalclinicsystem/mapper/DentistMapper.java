package com.nazarukiv.dentalclinicsystem.mapper;

import com.nazarukiv.dentalclinicsystem.dto.CreateDentistRequest;
import com.nazarukiv.dentalclinicsystem.dto.DentistResponse;
import com.nazarukiv.dentalclinicsystem.entity.Dentist;
import org.springframework.stereotype.Component;

@Component
public class DentistMapper {

    public Dentist toEntity(CreateDentistRequest request) {
        Dentist dentist = new Dentist();
        dentist.setFirstName(request.getFirstName());
        dentist.setLastName(request.getLastName());
        dentist.setSpecialisation(request.getSpecialisation());
        dentist.setEmail(request.getEmail());
        return dentist;
    }

    public DentistResponse toResponse(Dentist dentist) {
        DentistResponse response = new DentistResponse();
        response.setId(dentist.getId());
        response.setFirstName(dentist.getFirstName());
        response.setLastName(dentist.getLastName());
        response.setSpecialisation(dentist.getSpecialisation());
        response.setEmail(dentist.getEmail());
        return response;
    }
}
