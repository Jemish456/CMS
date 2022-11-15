package com.globalvox.clinicmanagementsystem.entity;

import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.*;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.Date;

@Entity
@Getter
@Setter
public class StockItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Temporal(value=TemporalType.DATE)
    @DateTimeFormat(pattern = "dd/MM/yyyy")
    private Date expiryDate;

//    @NotNull(message = "Please Enter Buying Price")
    private Double buyingPrice;

//    @NotNull(message = "Please Enter Selling Price")
    private Double sellingPrice;

//    @NotNull(message = "Please Enter Quantity")
    @Min(value = 1)
    private Integer qty;

    @Column(nullable = true)
    private String power;

    @ManyToOne(cascade= {CascadeType.PERSIST, CascadeType.MERGE,
            CascadeType.DETACH, CascadeType.REFRESH})
    @JoinColumn(name ="inventory_id",referencedColumnName = "id")
    private Inventory inventory;

    @CreationTimestamp
    @Column(updatable = false)
    private Date createdAt;

    @UpdateTimestamp
    private Date updatedAt;
}
