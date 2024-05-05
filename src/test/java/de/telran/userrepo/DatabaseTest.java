package de.telran.userrepo;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.junit4.SpringRunner;

import static org.junit.Assert.assertEquals;

@RunWith(SpringRunner.class)
@DataJpaTest // в рамках тестов в этом классе спринг загрузит только базу данных
public class DatabaseTest {

    @Autowired
    UserRepository repository;

    @Test
    public void testUserCreation()
    {
        User user = new User();
        user.setEmail("max@gmail.com");
        user.setName("max");

        User savedUser = repository.save(user);

        // проверьте что ему присвоился id == 1
        assertEquals(savedUser.getId(), 1L);
    }

}
