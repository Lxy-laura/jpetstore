package com.jpetstore.domain;

import lombok.Data;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.io.Serial;
import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
@TableName("banner")
public class Banner implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    private Integer id;
    private String title;
    private String subtitle;
    private String image;
    private String link;
    private Integer sortOrder;
    private Boolean active;
}

