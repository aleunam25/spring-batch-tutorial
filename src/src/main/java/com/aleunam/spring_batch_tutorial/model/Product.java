package com.aleunam.spring_batch_tutorial.model;

import lombok.Data;

@Data    // @Getter, @Setter, @ToString, @EqualsAndHashCode and @RequiredArgsConstructor together
public class Product {

    private String name;
    private int quantity;
    private String company;
    private float price;
    private String section;

}