package com.example.orderservice.models;

import com.example.commonmodule.models.ManyToOneUser;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import java.util.List;

@EqualsAndHashCode(callSuper = true)
@Data
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
@Table("order_custom")
public class Order extends ManyToOneUser {
    @Column("shipping_address")
    private String shippingAddress;
    private boolean payed;

    private List<Long> trainings;
}
