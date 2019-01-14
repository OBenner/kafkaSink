package ru.neoflex.kafkasink;

import lombok.*;

/**
 * The type Account.
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString
@EqualsAndHashCode
public class Account {
    private int id;
    private String firstName;
    private String lastName;
    private int phone;
    private int amount;
    private String operation;
}
