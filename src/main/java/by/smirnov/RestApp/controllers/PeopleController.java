package by.smirnov.RestApp.controllers;

import by.smirnov.RestApp.dto.PersonDTO;
import by.smirnov.RestApp.models.Person;
import by.smirnov.RestApp.services.PeopleService;
import by.smirnov.RestApp.util.PersonErrorResponse;
import by.smirnov.RestApp.util.PersonNotCreatedException;
import by.smirnov.RestApp.util.PersonNotFoundException;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/people")
public class PeopleController {
    private final PeopleService peopleService;
    private final ModelMapper modelMapper;

    @Autowired
    public PeopleController(PeopleService peopleService, ModelMapper modelMapper) {
        this.peopleService = peopleService;
        this.modelMapper = modelMapper;
    }

    @GetMapping
    public List<PersonDTO> getPeople() {
        return peopleService.findAll().stream().map(this::convertToPersonDTO).collect(Collectors.toList());
    }

    @GetMapping("/{id}")
    public PersonDTO getPerson(@PathVariable("id") int id) {
        return convertToPersonDTO(peopleService.findOne(id));
    }

    @PostMapping
    public ResponseEntity<HttpStatus> create(@RequestBody @Valid PersonDTO personDTO, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            StringBuilder errMsg = new StringBuilder();

            List<FieldError> errors = bindingResult.getFieldErrors();
            for (FieldError error : errors) {
                errMsg.append(error.getField()).append(" - ").append(error.getDefaultMessage()).append(";\n");
            }
            throw new PersonNotCreatedException(errMsg.toString());
        }
        peopleService.save(converToPerson(personDTO));
        return ResponseEntity.ok(HttpStatus.OK);
    }

    private Person converToPerson(PersonDTO personDTO) {
        return modelMapper.map(personDTO, Person.class);
    }

    private void enrich(Person person) {
        person.setCreatedAt(LocalDateTime.now());
        person.setUpdatedAt(LocalDateTime.now());
        person.setCreatedWho("ADMIN");
    }

    @ExceptionHandler
    private ResponseEntity<PersonErrorResponse> handleException(PersonNotFoundException e) {
        PersonErrorResponse response = new PersonErrorResponse(
                "Person with this id not found",
                System.currentTimeMillis());

        return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler
    private ResponseEntity<PersonErrorResponse> handleException(PersonNotCreatedException e) {
        PersonErrorResponse response = new PersonErrorResponse(
                e.getMessage(),
                System.currentTimeMillis());

        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }

    private PersonDTO convertToPersonDTO(Person person){
        return modelMapper.map(person, PersonDTO.class);
    }

}
