package com.nazarukiv.dentalclinicsystem.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import com.nazarukiv.dentalclinicsystem.dto.CreateDentistRequest;
import com.nazarukiv.dentalclinicsystem.dto.DentistResponse;
import com.nazarukiv.dentalclinicsystem.dto.UpdateDentistRequest;
import com.nazarukiv.dentalclinicsystem.entity.Dentist;
import com.nazarukiv.dentalclinicsystem.entity.Specialisation;
import com.nazarukiv.dentalclinicsystem.exception.DentistNotFoundException;
import com.nazarukiv.dentalclinicsystem.mapper.DentistMapper;
import com.nazarukiv.dentalclinicsystem.repository.DentistRepository;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class DentistServiceTest {

    @Mock
    private DentistRepository dentistRepository;

    @Mock
    private DentistMapper dentistMapper;

    @InjectMocks
    private DentistService dentistService;

    @Test
    void createDentist_success() {
        // Arrange
        CreateDentistRequest request = createDentistRequest("Alice", "White", "alice.white@example.com");
        Dentist dentist = createDentist(null, request.getFirstName(), request.getLastName(), request.getEmail());
        Dentist savedDentist = createDentist(1L, request.getFirstName(), request.getLastName(), request.getEmail());
        DentistResponse response = createDentistResponse(1L, request.getFirstName(), request.getLastName(), request.getEmail());

        when(dentistRepository.existsByEmail(request.getEmail())).thenReturn(false);
        when(dentistMapper.toEntity(request)).thenReturn(dentist);
        when(dentistRepository.save(dentist)).thenReturn(savedDentist);
        when(dentistMapper.toResponse(savedDentist)).thenReturn(response);

        // Act
        DentistResponse result = dentistService.createDentist(request);

        // Assert
        assertSame(response, result);
        verify(dentistRepository, times(1)).existsByEmail(request.getEmail());
        verify(dentistMapper, times(1)).toEntity(request);
        verify(dentistRepository, times(1)).save(dentist);
        verify(dentistMapper, times(1)).toResponse(savedDentist);
        verifyNoMoreInteractions(dentistRepository, dentistMapper);
    }

    @Test
    void createDentist_emailAlreadyExistsThrowsException() {
        // Arrange
        CreateDentistRequest request = createDentistRequest("Alice", "White", "alice.white@example.com");
        when(dentistRepository.existsByEmail(request.getEmail())).thenReturn(true);

        // Act / Assert
        assertThrows(IllegalArgumentException.class, () -> dentistService.createDentist(request));
        verify(dentistRepository, times(1)).existsByEmail(request.getEmail());
        verifyNoInteractions(dentistMapper);
        verifyNoMoreInteractions(dentistRepository);
    }

    @Test
    void getDentistById_success() {
        // Arrange
        Long dentistId = 1L;
        Dentist dentist = createDentist(dentistId, "Alice", "White", "alice.white@example.com");
        DentistResponse response = createDentistResponse(dentistId, "Alice", "White", "alice.white@example.com");

        when(dentistRepository.findById(dentistId)).thenReturn(Optional.of(dentist));
        when(dentistMapper.toResponse(dentist)).thenReturn(response);

        // Act
        DentistResponse result = dentistService.getDentistById(dentistId);

        // Assert
        assertSame(response, result);
        verify(dentistRepository, times(1)).findById(dentistId);
        verify(dentistMapper, times(1)).toResponse(dentist);
        verifyNoMoreInteractions(dentistRepository, dentistMapper);
    }

    @Test
    void getDentistById_notFound() {
        // Arrange
        Long dentistId = 99L;
        when(dentistRepository.findById(dentistId)).thenReturn(Optional.empty());

        // Act / Assert
        assertThrows(DentistNotFoundException.class, () -> dentistService.getDentistById(dentistId));
        verify(dentistRepository, times(1)).findById(dentistId);
        verifyNoInteractions(dentistMapper);
        verifyNoMoreInteractions(dentistRepository);
    }

    @Test
    void getAllDentists_success() {
        // Arrange
        Dentist firstDentist = createDentist(1L, "Alice", "White", "alice.white@example.com");
        Dentist secondDentist = createDentist(2L, "Bob", "Green", "bob.green@example.com");
        DentistResponse firstResponse = createDentistResponse(1L, "Alice", "White", "alice.white@example.com");
        DentistResponse secondResponse = createDentistResponse(2L, "Bob", "Green", "bob.green@example.com");

        when(dentistRepository.findAll()).thenReturn(List.of(firstDentist, secondDentist));
        when(dentistMapper.toResponse(firstDentist)).thenReturn(firstResponse);
        when(dentistMapper.toResponse(secondDentist)).thenReturn(secondResponse);

        // Act
        List<DentistResponse> result = dentistService.getAllDentists();

        // Assert
        assertEquals(List.of(firstResponse, secondResponse), result);
        verify(dentistRepository, times(1)).findAll();
        verify(dentistMapper, times(1)).toResponse(firstDentist);
        verify(dentistMapper, times(1)).toResponse(secondDentist);
        verifyNoMoreInteractions(dentistRepository, dentistMapper);
    }

    @Test
    void updateDentist_success() {
        // Arrange
        Long dentistId = 1L;
        Dentist dentist = createDentist(dentistId, "Alice", "White", "alice.white@example.com");
        UpdateDentistRequest request = updateDentistRequest("Alicia", "Green", "alicia.green@example.com");
        DentistResponse response = createDentistResponse(
                dentistId,
                request.getFirstName(),
                request.getLastName(),
                request.getEmail()
        );

        when(dentistRepository.findById(dentistId)).thenReturn(Optional.of(dentist));
        when(dentistRepository.findByEmail(request.getEmail())).thenReturn(Optional.empty());
        when(dentistRepository.save(dentist)).thenReturn(dentist);
        when(dentistMapper.toResponse(dentist)).thenReturn(response);

        // Act
        DentistResponse result = dentistService.updateDentist(dentistId, request);

        // Assert
        assertSame(response, result);

        ArgumentCaptor<Dentist> dentistCaptor = ArgumentCaptor.forClass(Dentist.class);
        verify(dentistRepository, times(1)).save(dentistCaptor.capture());
        Dentist savedDentist = dentistCaptor.getValue();
        assertEquals(request.getFirstName(), savedDentist.getFirstName());
        assertEquals(request.getLastName(), savedDentist.getLastName());
        assertEquals(request.getSpecialisation(), savedDentist.getSpecialisation());
        assertEquals(request.getEmail(), savedDentist.getEmail());

        verify(dentistRepository, times(1)).findById(dentistId);
        verify(dentistRepository, times(1)).findByEmail(request.getEmail());
        verify(dentistMapper, times(1)).toResponse(dentist);
        verifyNoMoreInteractions(dentistRepository, dentistMapper);
    }

    @Test
    void updateDentist_emailAlreadyExistsThrowsException() {
        // Arrange
        Long dentistId = 1L;
        Dentist dentist = createDentist(dentistId, "Alice", "White", "alice.white@example.com");
        Dentist existingDentist = createDentist(2L, "Bob", "Green", "alicia.green@example.com");
        UpdateDentistRequest request = updateDentistRequest("Alicia", "Green", "alicia.green@example.com");

        when(dentistRepository.findById(dentistId)).thenReturn(Optional.of(dentist));
        when(dentistRepository.findByEmail(request.getEmail())).thenReturn(Optional.of(existingDentist));

        // Act / Assert
        assertThrows(IllegalArgumentException.class, () -> dentistService.updateDentist(dentistId, request));
        verify(dentistRepository, times(1)).findById(dentistId);
        verify(dentistRepository, times(1)).findByEmail(request.getEmail());
        verifyNoInteractions(dentistMapper);
        verifyNoMoreInteractions(dentistRepository);
    }

    @Test
    void deleteDentist_success() {
        // Arrange
        Long dentistId = 1L;
        Dentist dentist = createDentist(dentistId, "Alice", "White", "alice.white@example.com");
        when(dentistRepository.findById(dentistId)).thenReturn(Optional.of(dentist));

        // Act
        dentistService.deleteDentist(dentistId);

        // Assert
        verify(dentistRepository, times(1)).findById(dentistId);
        verify(dentistRepository, times(1)).delete(dentist);
        verifyNoInteractions(dentistMapper);
        verifyNoMoreInteractions(dentistRepository);
    }

    private CreateDentistRequest createDentistRequest(String firstName, String lastName, String email) {
        CreateDentistRequest request = new CreateDentistRequest();
        request.setFirstName(firstName);
        request.setLastName(lastName);
        request.setSpecialisation(Specialisation.ORTHODONTICS);
        request.setEmail(email);
        return request;
    }

    private UpdateDentistRequest updateDentistRequest(String firstName, String lastName, String email) {
        UpdateDentistRequest request = new UpdateDentistRequest();
        request.setFirstName(firstName);
        request.setLastName(lastName);
        request.setSpecialisation(Specialisation.ENDODONTICS);
        request.setEmail(email);
        return request;
    }

    private Dentist createDentist(Long id, String firstName, String lastName, String email) {
        Dentist dentist = new Dentist();
        dentist.setId(id);
        dentist.setFirstName(firstName);
        dentist.setLastName(lastName);
        dentist.setSpecialisation(Specialisation.ORTHODONTICS);
        dentist.setEmail(email);
        return dentist;
    }

    private DentistResponse createDentistResponse(Long id, String firstName, String lastName, String email) {
        DentistResponse response = new DentistResponse();
        response.setId(id);
        response.setFirstName(firstName);
        response.setLastName(lastName);
        response.setSpecialisation(Specialisation.ORTHODONTICS);
        response.setEmail(email);
        return response;
    }
}
