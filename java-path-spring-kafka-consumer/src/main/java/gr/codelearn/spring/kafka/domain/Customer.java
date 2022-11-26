package gr.codelearn.spring.kafka.domain;

public record Customer(Long id, String email, String firstname, String lastname, Integer age) {
}
