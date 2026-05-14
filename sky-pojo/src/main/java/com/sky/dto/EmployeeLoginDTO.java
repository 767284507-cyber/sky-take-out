package com.sky.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

@Data
/**
 *  @Data 的作用
 *  @Data 是 Lombok 提供的一个非常实用的注解，它可以自动生成常用的样板代码，让 Java
 *  @Data 是一个组合注解，相当于同时添加了以下 5 个注解：
 *  @Data
 * // 等价于同时添加：
 * // @Getter
 * // @Setter
 * // @ToString
 * // @EqualsAndHashCode
 * // @RequiredArgsConstructor
 *
 */
@ApiModel(description = "员工登录时传递的数据模型")
public class EmployeeLoginDTO implements Serializable {

    @ApiModelProperty("用户名")
    private String username;

    @ApiModelProperty("密码")
    private String password;

}
