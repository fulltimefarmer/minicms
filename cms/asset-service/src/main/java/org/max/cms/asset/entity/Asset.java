package org.max.cms.asset.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.max.cms.common.entity.BaseEntity;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@TableName("assets")
public class Asset extends BaseEntity {
    
    private String name;
    private String assetNumber;
    private String type;
    private String category;
    private String description;
    private String brand;
    private String model;
    private String serialNumber;
    private BigDecimal purchasePrice;
    private LocalDate purchaseDate;
    private String supplier;
    private String location;
    private String department;
    private String responsiblePerson;
    private String status; // ACTIVE, MAINTENANCE, DEPRECATED, DISPOSED
    private LocalDate warrantyExpiry;
    private String notes;
}