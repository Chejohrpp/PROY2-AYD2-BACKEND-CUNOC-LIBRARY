package com.hrp.libreriacunocbackend.service.career;

import com.hrp.libreriacunocbackend.dto.career.CareerRequestDTO;
import com.hrp.libreriacunocbackend.dto.career.CareerResponseDTO;
import com.hrp.libreriacunocbackend.entities.user.Career;
import com.hrp.libreriacunocbackend.exceptions.BadRequestException;
import com.hrp.libreriacunocbackend.exceptions.EntityNotFoundException;
import com.hrp.libreriacunocbackend.exceptions.NotAcceptableException;
import com.hrp.libreriacunocbackend.repository.career.CareerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class CareerServiceImpl implements CareerService {

    private final CareerRepository careerRepository;

    @Autowired
    public CareerServiceImpl(CareerRepository careerRepository) {
        this.careerRepository = careerRepository;
    }

    @Override
    public CareerResponseDTO createCareer(CareerRequestDTO careerRequestDTO) throws NotAcceptableException {
        validateCareerRequest(careerRequestDTO);
        Career newCareer = new Career();
        newCareer.setName(careerRequestDTO.getName());
        newCareer = careerRepository.save(newCareer);
        return new CareerResponseDTO(newCareer);
    }

    private void validateCareerRequest(CareerRequestDTO careerRequestDTO) throws NotAcceptableException {
        if (careerRequestDTO.getName() == null || careerRequestDTO.getName().isBlank()){
            throw new NotAcceptableException("the name cannot be null or empty");
        }
        Optional<Career> foundCareer = careerRepository.findByName(careerRequestDTO.getName());
        if (foundCareer.isPresent()) {
            throw new NotAcceptableException("the career name is already present");
        }
    }

    @Override
    public Optional<Career> findCarrerById(Long id) {
        return careerRepository.findById(id);
    }

    @Override
    public List<CareerResponseDTO> getByFilter(String filter) throws BadRequestException {
        return careerRepository.getByNameFilter(filter)
                .stream()
                .map(CareerResponseDTO :: new)
                .collect(Collectors.toList());
    }
}
