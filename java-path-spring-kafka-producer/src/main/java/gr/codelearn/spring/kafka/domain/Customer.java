package gr.codelearn.spring.kafka.domain;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString()
@Builder
public class Customer {
	private Long id;
	private String email;
	private String firstname;
	private String lastname;
	private Integer age;
}
