package com.example.Sweetalk;
import io.github.cdimascio.dotenv.Dotenv;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.security.crypto.password.PasswordEncoder;

@SpringBootApplication
public class SweetalkApplication {


	public static void main(String[] args)
	{
		SpringApplication.run(SweetalkApplication.class, args);
	}
}
