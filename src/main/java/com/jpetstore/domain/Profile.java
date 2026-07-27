package com.jpetstore.domain;

import lombok.Data;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import jakarta.validation.constraints.NotBlank;
import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
@TableName("profile")
public class Profile implements Serializable {

    private static final long serialVersionUID = 1L;

    @NotBlank(message = "用户ID不能为空")
    private String userid;

    @NotBlank(message = "语言偏好不能为空")
    private String langpref;

    @NotBlank(message = "favorite分类不能为空")
    private String favcategory;

    private Boolean mylistopt;

    private Boolean banneropt;
}
