package de.telran.userrepo;

import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
public class UserController {


    Logger logger = LoggerFactory.getLogger(UserController.class);

    @Autowired
    private UserRepository repository;

    @GetMapping("/users")
    public ResponseEntity<Iterable<User>> getAll() {
        logger.debug("/users called");
        return ResponseEntity.ok(repository.findAll());
    }

    @PostMapping("/users")
    ResponseEntity<String> addUser(
            @Valid @RequestBody User user
    ) {
        repository.save(user);
        return ResponseEntity.ok(
                "User is valid"
        );
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST) // 400
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public Map<String, String> handleValidationExceptions(
            MethodArgumentNotValidException ex
    ) {
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getAllErrors().forEach(
                error ->
                        errors.put(
                                ((FieldError) error).getField(),
                                error.getDefaultMessage()
                        )
        );
        return errors;
    }

    // GET http://localhost:8080/upper/hello -> HELLO

    @GetMapping("/upper/{text}")
    public ResponseEntity<String> toUpper(
            @PathVariable String text
    ){
        // написать для него тест в IntegrationTest
        // до 20:34
        return ResponseEntity.ok(text.toUpperCase());
    }

    // если есть пользователь 1, "max", "max@gmail.com"
    // GET http://localhost:8080/user/1 -> max:max@gmail.com
    @GetMapping("/user/{key}")
    public ResponseEntity<String> concatenateUser(
            @PathVariable Long key
    )
    {
        User user = repository.findById(key).orElse(null);
        if(user != null) {
            return ResponseEntity.ok(user.getName() + ":"+user.getEmail());
        }
        else
        {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }



}
