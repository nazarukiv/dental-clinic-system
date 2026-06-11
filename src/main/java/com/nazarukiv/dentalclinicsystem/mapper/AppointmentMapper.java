package com.nazarukiv.dentalclinicsystem.mapper;

import com.nazarukiv.dentalclinicsystem.dto.AppointmentResponse;
import com.nazarukiv.dentalclinicsystem.dto.CreateAppointmentRequest;
import com.nazarukiv.dentalclinicsystem.entity.Appointment;
import com.nazarukiv.dentalclinicsystem.entity.Dentist;
import com.nazarukiv.dentalclinicsystem.entity.Patient;
import org.springframework.stereotype.Component;

@Component
public class AppointmentMapper {

    public Appointment toEntity(CreateAppointmentRequest request, Patient patient, Dentist dentist) {
        Appointment appointment = new Appointment();
        appointment.setPatient(patient);
        appointment.setDentist(dentist);
        appointment.setAppointmentTime(request.getAppointmentTime());
        return appointment;
    }

    public AppointmentResponse toResponse(Appointment appointment) {
        AppointmentResponse response = new AppointmentResponse();
        response.setId(appointment.getId());
        response.setPatientId(appointment.getPatient().getId());
        response.setPatientFullName(getFullName(
                appointment.getPatient().getFirstName(),
                appointment.getPatient().getLastName()
        ));
        response.setDentistId(appointment.getDentist().getId());
        response.setDentistFullName(getFullName(
                appointment.getDentist().getFirstName(),
                appointment.getDentist().getLastName()
        ));
        response.setAppointmentTime(appointment.getAppointmentTime());
        response.setStatus(appointment.getStatus());
        return response;
    }

    private String getFullName(String firstName, String lastName) {
        return firstName + " " + lastName;
    }
}
