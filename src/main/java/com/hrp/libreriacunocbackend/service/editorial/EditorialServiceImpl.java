package com.hrp.libreriacunocbackend.service.editorial;

import com.hrp.libreriacunocbackend.dto.editorial.EditorialRequestDTO;
import com.hrp.libreriacunocbackend.dto.editorial.EditorialResponseDTO;
import com.hrp.libreriacunocbackend.entities.book.Editorial;
import com.hrp.libreriacunocbackend.exceptions.BadRequestException;
import com.hrp.libreriacunocbackend.exceptions.DuplicatedEntityException;
import com.hrp.libreriacunocbackend.exceptions.NotAcceptableException;
import com.hrp.libreriacunocbackend.repository.books.EditorialRepository;
import io.micrometer.common.util.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class EditorialServiceImpl implements EditorialService {
    private final EditorialRepository editorialRepository;

    @Autowired
    public EditorialServiceImpl(EditorialRepository editorialRepository) {
        this.editorialRepository = editorialRepository;
    }

    @Override
    public EditorialResponseDTO create(EditorialRequestDTO editorialRequestDTO) throws NotAcceptableException, DuplicatedEntityException {
        validateEditorialRequest(editorialRequestDTO);
        Editorial editorial = new Editorial();
        editorial.setIdEditorial(editorialRequestDTO.getIdEditorial());
        editorial.setName(editorialRequestDTO.getName());
        editorial = editorialRepository.save(editorial);
        return new EditorialResponseDTO(editorial);
    }

    private void validateEditorialRequest(EditorialRequestDTO editorialRequestDTO) throws NotAcceptableException, DuplicatedEntityException {
        if (editorialRequestDTO.getIdEditorial() == null || editorialRequestDTO.getIdEditorial().isBlank()){
            throw new NotAcceptableException("the id cannot be null or empty");
        }
        if (StringUtils.isBlank(editorialRequestDTO.getIdEditorial())) {
            throw new NotAcceptableException("the id cannot only by blank spaces");
        }
        if (editorialRequestDTO.getName() == null || editorialRequestDTO.getName().isEmpty()){
            throw new NotAcceptableException("the name cannot be null or empty");
        }
        if (getEditorialById(editorialRequestDTO.getIdEditorial()).isPresent()){
            throw new DuplicatedEntityException("id already present");
        }
        if (getEditorialByName(editorialRequestDTO.getName()).isPresent()){
            throw new DuplicatedEntityException("name is already present");
        }
    }

    @Override
    public Optional<Editorial> getEditorialById(String id){
        return editorialRepository.findById(id);
    }

    @Override
    public Optional<Editorial> getEditorialByName(String name) {
        return editorialRepository.findByName(name);
    }

    @Override
    public List<EditorialResponseDTO> getByFilter(String filter) throws BadRequestException {
        return editorialRepository.getEditorialsNameByFilter(filter)
                .stream()
                .map(EditorialResponseDTO:: new)
                .collect(Collectors.toList());
    }
}
