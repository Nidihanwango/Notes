package com.example.ppt.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.ppt.bean.Doctor;
import com.example.ppt.service.DoctorService;
import com.example.ppt.mapper.DoctorMapper;
import org.springframework.stereotype.Service;

/**
* @author Administrator
* @description 针对表【t_doctor】的数据库操作Service实现
* @createDate 2022-08-03 10:42:29
*/
@Service
public class DoctorServiceImpl extends ServiceImpl<DoctorMapper, Doctor>
    implements DoctorService{

}




