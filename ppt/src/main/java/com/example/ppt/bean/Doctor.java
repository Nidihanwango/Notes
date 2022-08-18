package com.example.ppt.bean;

import com.baomidou.mybatisplus.annotation.*;

import java.io.Serializable;
import java.math.BigDecimal;
import lombok.Data;

/**
 * 
 * @TableName t_doctor
 */
@TableName(value ="t_doctor")
@Data
public class Doctor implements Serializable {
    /**
     * 
     */
    @TableId(type = IdType.AUTO)
    private Integer id;

    /**
     * 
     */
    private String name;

    /**
     * 
     */
    private Integer type;

    /**
     * 
     */
    private String introduce;

    /**
     * 
     */
    private String specialty;

    /**
     * 
     */
    private String specificDisease;

    /**
     * 
     */
    private BigDecimal registrationFee;

    /**
     * 
     */
    private BigDecimal consultationFee;

    /**
     * 
     */
    private String workTime;

    /**
     * 
     */
    private String treatment;

    /**
     * 
     */
    private String receptionScope;

    /**
     * 
     */
    private String message;

    /**
     * 
     */
    private String imgPath;

    /**
     * 
     */
//    @TableLogic
    private Integer status = 0;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}