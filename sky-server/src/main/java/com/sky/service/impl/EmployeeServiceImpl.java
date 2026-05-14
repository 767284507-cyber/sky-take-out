package com.sky.service.impl;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.sky.constant.MessageConstant;
import com.sky.constant.PasswordConstant;
import com.sky.constant.StatusEnum;
import com.sky.context.BaseContext;
import com.sky.dto.EmployeeDTO;
import com.sky.dto.EmployeeLoginDTO;
import com.sky.dto.EmployeePageQueryDTO;
import com.sky.entity.Employee;
import com.sky.exception.AccountLockedException;
import com.sky.exception.AccountNotFoundException;
import com.sky.exception.ParameterValidationException;
import com.sky.exception.PasswordErrorException;
import com.sky.mapper.EmployeeMapper;
import com.sky.result.PageResult;
import com.sky.result.Result;
import com.sky.service.EmployeeService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class EmployeeServiceImpl implements EmployeeService {

    @Autowired
    private EmployeeMapper employeeMapper;

    /**
     * 员工登录
     *
     * @param employeeLoginDTO
     * @return
     */
    public Employee login(EmployeeLoginDTO employeeLoginDTO) {
        String username = employeeLoginDTO.getUsername();
        String password = employeeLoginDTO.getPassword();

        //1、根据用户名查询数据库中的数据
        Employee employee = employeeMapper.getByUsername(username);

        //2、处理各种异常情况（用户名不存在、密码不对、账号被锁定）
        if (employee == null) {
            //账号不存在
            throw new AccountNotFoundException(MessageConstant.ACCOUNT_NOT_FOUND);
        }

        //密码比对
        // TODO 后期需要进行md5加密，然后再进行比对
        password = DigestUtils.md5DigestAsHex(password.getBytes());
        if (!password.equals(employee.getPassword())) {
            //密码错误
            throw new PasswordErrorException(MessageConstant.PASSWORD_ERROR);
        }

        if (employee.getStatus() == StatusEnum.DISABLE.getStatus()) {
            //账号被锁定
            throw new AccountLockedException(MessageConstant.ACCOUNT_LOCKED);
        }

        //3、返回实体对象
        return employee;
    }

    /**
     * 新增员工
     * @param employeeDTO
     */
    @Override
    public void save(EmployeeDTO employeeDTO) {
        Employee employee = new Employee();

        //对象属性拷贝
        BeanUtils.copyProperties(employeeDTO, employee);

        //设置账号的状态，默认正常状态 1表示正常 0表示锁定
//        employee.setStatus(StatusConstant.ENABLE);
        employee.setStatus(StatusEnum.ENABLE.getStatus());


        //设置密码，默认密码123456
        employee.setPassword(DigestUtils.md5DigestAsHex(PasswordConstant.DEFAULT_PASSWORD.getBytes()));
        //设置当前记录的创建时间和修改时间
        employee.setCreateTime(LocalDateTime.now());
        employee.setUpdateTime(LocalDateTime.now());

//        通过ThreadLocal获取用户信息
        Long currentId = BaseContext.getCurrentId();

        //设置当前记录创建人id和修改人id
        employee.setCreateUser(currentId);//目前写个假数据，后期修改
        employee.setUpdateUser(currentId);

        employeeMapper.insert(employee);//后续步骤定义
    }

    /**
     * 员工分页查询
     * @param employeePageQueryDTO
     * @return
     */
    @Override
    public PageResult pageQuery(EmployeePageQueryDTO employeePageQueryDTO) {
//        开始分页查询
        PageHelper.startPage(employeePageQueryDTO.getPage(), employeePageQueryDTO.getPageSize());

        Page<Employee> page = employeeMapper.pageQuery(employeePageQueryDTO);

        long total = page.getTotal();
        List<Employee> records = page.getResult();

        return new PageResult(total, records);
    }

    /**
     * 启用禁用员工账户
     * @param status
     * @param id
     */
    @Override
    public void startOrStop(Integer status, Long id) {
        if(status==null){
            throw new ParameterValidationException(MessageConstant.STATUS_NOT_NULL);
        }else if(id==null){
            throw new ParameterValidationException(MessageConstant.ACCOUNT_NOT_NULL);
        }else if((employeeMapper.getById(id))==null){
            throw new AccountNotFoundException(MessageConstant.ACCOUNT_NOT_FOUND);
        }
        Employee employee = Employee.builder()
                .status(status)
                .id(id)
                .build();
        employeeMapper.update(employee);
    }

    /**
     * 根据iD查询用户信息
     * @param id
     * @return
     */
    @Override
    public Employee getById(Long id) {
        Employee employee = employeeMapper.getById(id);
        employee.setPassword("****");
        return employee;
    }

    /**
     * 编辑员工信息
     * @param employeeDTO
     */
    @Override
    public void update(EmployeeDTO employeeDTO) {
        // 1. 参数校验
        validateEmployeeDTO(employeeDTO);
        
        // 2. 检查员工是否存在
        Employee existingEmployee = employeeMapper.getById(employeeDTO.getId());
        if (existingEmployee == null) {
            throw new AccountNotFoundException(MessageConstant.ACCOUNT_NOT_FOUND);
        }
        
        // 3. 对象属性拷贝
        Employee employee = new Employee();
        BeanUtils.copyProperties(employeeDTO, employee);
        
        // 4. 设置更新时间和更新人
        employee.setUpdateTime(LocalDateTime.now());
        employee.setUpdateUser(BaseContext.getCurrentId());

        // 5. 执行更新操作
        employeeMapper.update(employee);
    }

    /**
     * 校验员工DTO参数
     * @param employeeDTO 员工DTO
     */
    private void validateEmployeeDTO(EmployeeDTO employeeDTO) {
        // 校验ID
        if (employeeDTO.getId() == null) {
            throw new ParameterValidationException(MessageConstant.ACCOUNT_NOT_NULL);
        }
        
        // 校验用户名
        if (employeeDTO.getUsername() == null || employeeDTO.getUsername().trim().isEmpty()) {
            throw new ParameterValidationException(MessageConstant.USERNAME_NOT_NULL);
        }
        
        // 校验姓名
        if (employeeDTO.getName() == null || employeeDTO.getName().trim().isEmpty()) {
            throw new ParameterValidationException(MessageConstant.EMPLOYEE_NAME_NOT_NULL);
        }
        
        // 校验手机号
        if (employeeDTO.getPhone() == null || employeeDTO.getPhone().trim().isEmpty()) {
            throw new ParameterValidationException(MessageConstant.PHONE_NOT_NULL);
        }
        // 手机号格式校验：11位数字，以1开头
        if (!employeeDTO.getPhone().matches("^1[3-9]\\d{9}$")) {
            throw new ParameterValidationException(MessageConstant.PHONE_FORMAT_ERROR);
        }
        
        // 校验性别
        if (employeeDTO.getSex() == null || employeeDTO.getSex().trim().isEmpty()) {
            throw new ParameterValidationException(MessageConstant.SEX_NOT_NULL);
        }
        // 性别只能是"男"或"女"
        if (!"1".equals(employeeDTO.getSex()) && !"0".equals(employeeDTO.getSex())) {
            throw new ParameterValidationException(MessageConstant.SEX_FORMAT_ERROR);
        }
        
        // 校验身份证号
        if (employeeDTO.getIdNumber() == null || employeeDTO.getIdNumber().trim().isEmpty()) {
            throw new ParameterValidationException(MessageConstant.ID_NUMBER_NOT_NULL);
        }
        // 身份证号格式校验：18位，前17位为数字，最后一位可以是数字或X
        if (!employeeDTO.getIdNumber().matches("^\\d{17}[\\dXx]$")) {
            throw new ParameterValidationException(MessageConstant.ID_NUMBER_FORMAT_ERROR);
        }
    }

}
