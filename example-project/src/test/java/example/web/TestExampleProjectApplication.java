package example.web;

import org.springframework.boot.SpringApplication;

public class TestExampleProjectApplication {

	public static void main(String[] args) {
		SpringApplication.from(ExampleProjectApplication::main).with(TestcontainersConfiguration.class).run(args);
	}

}
