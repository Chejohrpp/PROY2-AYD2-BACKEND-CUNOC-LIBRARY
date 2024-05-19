package com.hrp.libreriacunocbackend.service.datafile;

import com.hrp.libreriacunocbackend.dto.author.AuthorRequestDTO;
import com.hrp.libreriacunocbackend.dto.book.BookRequestDTO;
import com.hrp.libreriacunocbackend.dto.borrow.BorrowRequestDTO;
import com.hrp.libreriacunocbackend.dto.career.CareerRequestDTO;
import com.hrp.libreriacunocbackend.dto.datafile.DataFileResponseDTO;
import com.hrp.libreriacunocbackend.dto.editorial.EditorialRequestDTO;
import com.hrp.libreriacunocbackend.dto.student.StudentRequestDTO;
import com.hrp.libreriacunocbackend.entities.book.Author;
import com.hrp.libreriacunocbackend.entities.datafile.ErrorEntity;
import com.hrp.libreriacunocbackend.exceptions.*;
import com.hrp.libreriacunocbackend.repository.user.StudentRepository;
import com.hrp.libreriacunocbackend.service.author.AuthorService;
import com.hrp.libreriacunocbackend.service.book.BookService;
import com.hrp.libreriacunocbackend.service.borrow.BorrowService;
import com.hrp.libreriacunocbackend.service.career.CareerService;
import com.hrp.libreriacunocbackend.service.editorial.EditorialService;
import com.hrp.libreriacunocbackend.service.student.StudentService;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;

@Service
public class DataFileServiceImpl implements DataFileService{

    private final BookService bookService;
    private final StudentService studentService;
    private final BorrowService borrowService;
    private final CareerService careerService;
    private final AuthorService authorService;
    private final EditorialService editorialService;

    public DataFileServiceImpl(BookService bookService, StudentService studentService, BorrowService borrowService, CareerService careerService, AuthorService authorService, EditorialService editorialService) {
        this.bookService = bookService;
        this.studentService = studentService;
        this.borrowService = borrowService;
        this.careerService = careerService;
        this.authorService = authorService;
        this.editorialService = editorialService;
    }

    @Override
    public DataFileResponseDTO handleDataFile(MultipartFile file) throws UploadDataFileException {
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(file.getInputStream()))) {
            ArrayList<ErrorEntity> errorsList = new ArrayList<>();
            int linesCount = 0;
            int records = 0;
            String line;

            while ((line = reader.readLine()) != null) {
                linesCount++;
                String[] columns = line.split(",");
                if (insertEntity(errorsList, columns, linesCount)) {
                    records++;
                }
            }
            return new DataFileResponseDTO(records, errorsList);
        } catch (IOException ex) {
            throw new UploadDataFileException("Error to upload datafile");
        }
    }

    @Override
    public List<String> verifySystemData() {
        List<String> itemsToInsert = new ArrayList<>();
        if (bookService.count() <= 0) {
            itemsToInsert.add("LIBRO");
        }
        if (studentService.count() <= 0) {
            itemsToInsert.add("ESTUDIANTE");
        }
        if (borrowService.count() <= 0) {
            itemsToInsert.add("PRESTAMO");
        }
        if(careerService.count() <= 0){
            itemsToInsert.add("CARRERA");
        }
        return itemsToInsert;
    }

    private boolean insertEntity(ArrayList<ErrorEntity> errorsList, String[] columns, int line) {
        try {
            if (columns[0].equalsIgnoreCase("LIBRO")) {
                handleBooks(columns);
            } else if (columns[0].equalsIgnoreCase("ESTUDIANTE")) {
                handleStudents(columns, errorsList);
            } else if (columns[0].equalsIgnoreCase("PRESTAMO")) {
                handleBorrows(columns);
            } else if (columns[0].equalsIgnoreCase("CARRERA")) {
                handleCareers(columns);
            } else if (columns[0].equalsIgnoreCase("AUTOR")) {
                handleAuthors(columns);
            } else if (columns[0].equalsIgnoreCase("EDITORIAL")) {
                handleEditorials(columns);
            } else {
                if (!columns[0].trim().equals("")) {
                    errorsList.add(new ErrorEntity(line, "Syntax", "Entidad desconocida"));
                }
            }
            return true;
        } catch (ParametersDoNotMatchException | NumberFormatException | DateFormatException | NotAcceptableException ex) {
            errorsList.add(new ErrorEntity(line, "Syntax", ex.getMessage()));
        } catch (EntityNotFoundException ex) {
            errorsList.add(new ErrorEntity(line, "Entity Not Found", ex.getMessage()));
        } catch (DuplicatedEntityException ex) {
            errorsList.add(new ErrorEntity(line, "Duplicated Entity", ex.getMessage()));
        } catch (Exception ex) {
            System.err.println("Error al leer entidad: " + ex.getMessage());
            ex.printStackTrace();
        }
        return false;
    }

    private void handleEditorials(String[] columns) throws ParametersDoNotMatchException, NotAcceptableException, DuplicatedEntityException {
        if (verifyParameters(3, columns.length, "EDITORIAL")) {
            String idEditorial = columns[1].trim();
            String name = columns[2].trim();

            EditorialRequestDTO editorialRequestDTO = new EditorialRequestDTO(idEditorial, name);

            // Llamamos al servicio para crear la editorial
            editorialService.create(editorialRequestDTO);
        }
    }

    private void handleAuthors(String[] columns) throws ParametersDoNotMatchException, NotAcceptableException, DuplicatedEntityException {
        if (verifyParameters(3, columns.length, "AUTOR")) {
            String idAuthor = columns[1].trim();
            String name = columns[2].trim();

            AuthorRequestDTO authorRequestDTO = new AuthorRequestDTO(idAuthor, name);

            // Llamamos al servicio para crear al autor
            authorService.createAuthor(authorRequestDTO);
        }
    }

    private void handleCareers(String[] columns) throws ParametersDoNotMatchException, NotAcceptableException, DuplicatedEntityException {
        if (verifyParameters(2, columns.length, "CARRERA")) {
            String name = columns[1].trim();

            CareerRequestDTO careerRequestDTO = new CareerRequestDTO(name,name);

            // Llamamos al servicio para crear la carrera
            careerService.createCareer(careerRequestDTO);
        }
    }

    private void handleBorrows(String[] columns) throws ParametersDoNotMatchException, DateFormatException, NotAcceptableException, EntityNotFoundException {
        if (verifyParameters(4, columns.length, "PRESTAMO")) {
            String carnet = columns[1].trim();
            String isbn = columns[2].trim();
            String dateBorrowStr = columns[3].trim();

            if (!isValidDateFormat(dateBorrowStr)) {
                throw new DateFormatException("La fecha de préstamo debe tener el formato yyyy-MM-dd. Valor encontrado: " + dateBorrowStr);
            }

            LocalDateTime dateBorrow = LocalDate.parse(dateBorrowStr, DateTimeFormatter.ofPattern("yyyy-MM-dd")).atStartOfDay();

            BorrowRequestDTO borrowRequestDTO = new BorrowRequestDTO(
                    carnet,
                    isbn,
                    dateBorrow
            );

            // Llamamos al servicio para crear el préstamo
            borrowService.newBorrow(borrowRequestDTO);
        }
    }

    private void handleStudents(String[] columns, ArrayList<ErrorEntity> errorsList) throws ParametersDoNotMatchException, DateFormatException, NotAcceptableException, EntityNotFoundException, DuplicatedEntityException {
        if (verifyParameters(6, columns.length, "ESTUDIANTE")) {
            String username = columns[1].trim();
            String name = columns[2].trim();
            String carnet = columns[3].trim();
            String birthDate = columns[4].trim();
            String idCareer = columns[5].trim();

            if (!isValidDateFormat(birthDate)) {
                throw new DateFormatException("La fecha de nacimiento debe tener el formato yyyy-MM-dd. Valor encontrado: " + birthDate);
            }

            LocalDateTime formattedBirthDate = LocalDate.parse(birthDate, DateTimeFormatter.ofPattern("yyyy-MM-dd")).atStartOfDay();
            StudentRequestDTO studentRequestDTO = new StudentRequestDTO(
                    username,
                    name,
                    carnet,
                    formattedBirthDate,
                    Long.parseLong(idCareer)
            );
            // Llamamos al servicio para guardar el estudiante
            studentService.createStudent(studentRequestDTO);

        }
    }

    private void handleBooks(String[] columns) throws ParametersDoNotMatchException, DuplicatedEntityException, NotAcceptableException, EntityNotFoundException, DateFormatException, NumberFormatException {
        if (verifyParameters(8, columns.length, "LIBRO")) {
            String isbn = columns[1].trim();
            String title = columns[2].trim();
            String idAuthor = columns[3].trim();
            String amount = columns[4].trim();
            String price = columns[5].trim();
            String datePublish = columns[6].trim();
            String idEditorial = columns[7].trim();

            if (!isPositiveInteger(amount)) {
                throw new NumberFormatException(String.format("La cantidad de un libro debe ser un número entero positivo. Valor encontrado: %s", amount));
            }
            if (!isValidDateFormat(datePublish)) {
                throw new DateFormatException(String.format("La fecha de la publicacion en formato yyyy-mm-dd. Valor encontrado: %s", datePublish));
            }
            if (!isValidMoneyFormat(price)) {
                throw new NumberFormatException(String.format("El precio de un libro debe ser un número entero positivo con dos decimales, valor obtenido: %s", price));
            }

            LocalDate publishDate = LocalDate.parse(datePublish, DateTimeFormatter.ofPattern("yyyy-MM-dd"));
            LocalDateTime formattedPublishDate = publishDate.atStartOfDay();


            BookRequestDTO bookRequestDTO = new BookRequestDTO(
                    isbn,
                    idAuthor,
                    title,
                    Integer.parseInt(amount),
                    Double.parseDouble(price),
                    formattedPublishDate,
                    idEditorial
            );
            bookService.createBook(bookRequestDTO);

        }
    }
    
    

    private boolean verifyParameters(int parametersRequired, int parameters, String columnName) throws ParametersDoNotMatchException {
        if (parametersRequired != parameters) {
            throw new ParametersDoNotMatchException(String.format("%s neceita tener %s parametros, solo se encontraron %s", columnName, parametersRequired, parameters));
        }
        return true;
    }

    public boolean isValidDateFormat(String str) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        try {
            LocalDate.parse(str, formatter);
            return true;
        } catch (DateTimeParseException e) {
            return false;
        }
    }

    public static boolean isPositiveInteger(String str) {
        return str.matches("^\\d+$");
    }

    public static boolean isValidMoneyFormat(String str) {
        return str.matches("^\\d+(\\.\\d{1,2})?$");
    }


}
