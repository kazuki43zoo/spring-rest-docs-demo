package com.example.spring.restdocs.demo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.IdGenerator;
import org.springframework.util.JdkIdGenerator;
import org.springframework.util.SimpleIdGenerator;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder;
import org.springframework.web.util.UriComponentsBuilder;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@SpringBootApplication
public class SpringRestDocsDemoApplication {

    public static void main(String[] args) {
        System.out.println(UUID.randomUUID().toString());
        SpringApplication.run(SpringRestDocsDemoApplication.class, args);
    }

    @Bean
    IdGenerator idGenerator() {
        return new JdkIdGenerator();
    }


    @RequestMapping("/api/v1/accounts")
    @RestController
    static class AccountRestController {
        private final IdGenerator idGenerator;

        public AccountRestController(IdGenerator idGenerator) {
            this.idGenerator = idGenerator;
        }

        @PostMapping
        public ResponseEntity<Account> postAccount(@RequestBody Account account, UriComponentsBuilder uriBuilder) {
            String id = idGenerator.generateId().toString();
            URI resourceUri = MvcUriComponentsBuilder.relativeTo(uriBuilder)
                    .withMethodCall(MvcUriComponentsBuilder.on(AccountRestController.class).getAccount(id))
                    .build().encode().toUri();
            return ResponseEntity.created(resourceUri).build();
        }

        @GetMapping
        public List<Account> getAccounts() {
            List<Account> accounts = new ArrayList<>();
            {
                Account account = new Account();
                account.setId("59cbbe8a-27da-484c-9859-771279460049");
                account.setName("山田 太郎");
                accounts.add(account);
            }
            {
                Account account = new Account();
                account.setId("f8339e92-d2ed-478a-9ffc-c240d1d8d4b7");
                account.setName("山田 花子");
                accounts.add(account);
            }
            return accounts;
        }

        @GetMapping("{id}")
        public Account getAccount(@PathVariable String id) {
            Account account = new Account();
            account.setId(id);
            account.setName("山田 太郎");
            return account;
        }

        @PutMapping("{id}")
        @ResponseStatus(HttpStatus.NO_CONTENT)
        public void putAccount(@PathVariable String id, @RequestBody Account account) {

        }

        @DeleteMapping("{id}")
        @ResponseStatus(HttpStatus.NO_CONTENT)
        public void putAccount(@PathVariable String id) {

        }


    }

    static class Account {
        private String id;
        @NotNull
        @Size(min = 1, max = 64)
        private String name;

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }
    }

}
