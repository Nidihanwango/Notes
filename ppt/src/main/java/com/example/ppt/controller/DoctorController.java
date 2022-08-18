package com.example.ppt.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.example.ppt.bean.Doctor;
import com.example.ppt.service.DoctorService;
import com.example.ppt.utils.MyUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import java.io.*;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

@Controller
public class DoctorController {

    @Autowired
    private DoctorService doctorService;

    @GetMapping("/doctor/{type}")
    String getDoctor(@PathVariable("type") Integer type, Model model) {
        LambdaQueryWrapper<Doctor> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Doctor::getType, type);
        List<Doctor> doctors = doctorService.list(wrapper);
        model.addAttribute("doctors", doctors);
        return "manager/doctor_manager";
    }

    @DeleteMapping("/doctor")
    String deleteDoctor(@RequestParam("id") Integer id, @RequestParam("type") Integer type) {
        doctorService.removeById(id);
        return "redirect:/doctor/" + type;
    }

    @PostMapping("/doctor")
    String saveDoctor(Doctor doctor, @RequestPart(value = "photo", required = false) MultipartFile photo) throws Exception {
        if (!Objects.equals(photo.getOriginalFilename(), "")) {
            String originalFilename = photo.getOriginalFilename();
            String suffix = originalFilename.substring(originalFilename.lastIndexOf("."));
            String filename = UUID.randomUUID().toString() + suffix;
            doctor.setImgPath(filename);
            File photoDir = new File("D:/hope-ppt-photo");
            if (!photoDir.exists()) {
                photoDir.mkdir();
            }
            String photoDirPath = photoDir.getPath();
            photo.transferTo(new File(photoDirPath + "/" + filename));
        }
        doctorService.saveOrUpdate(doctor);
        return "redirect:/doctor/" + doctor.getType();
    }

    @RequestMapping("/manager/updateDoctor/{type}/{id}")
    String updateDoctor(@PathVariable("type") Integer type,
                        @PathVariable("id") Integer id,
                        Model model) {
        Doctor doctor = doctorService.getById(id);
        model.addAttribute("doctor", doctor);
        return "manager/updateDoctor";
    }

    @GetMapping("/work")
    String work(Model model) {
        String dayOfWeek = MyUtils.getDayOfWeek();
        LambdaQueryWrapper<Doctor> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Doctor::getType, 0).like(Doctor::getWorkTime, dayOfWeek);
        List<Doctor> yishi = doctorService.list(wrapper);
        if (yishi != null) {
            model.addAttribute("yishi", yishi);
        }
        LambdaQueryWrapper<Doctor> wrapper2 = new LambdaQueryWrapper<>();
        wrapper2.eq(Doctor::getType, 1).like(Doctor::getWorkTime, dayOfWeek);
        List<Doctor> zhiliaoshi = doctorService.list(wrapper2);
        if (zhiliaoshi != null) {
            model.addAttribute("zhiliaoshi", zhiliaoshi);
        }
        return "introduction/work";
    }

    @ResponseBody
    @GetMapping("/axios")
    List<Doctor> axios() {
        String dayOfWeek = MyUtils.getDayOfWeek();
        LambdaQueryWrapper<Doctor> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Doctor::getStatus, 0).like(Doctor::getWorkTime, dayOfWeek).orderByAsc(Doctor::getType).orderByDesc(Doctor::getConsultationFee);
        List<Doctor> list = doctorService.list(wrapper);
        return list;
    }

    @ResponseBody
    @RequestMapping("/getPhoto/{path}")
    Resource getPhoto(@PathVariable("path") String path) {
        File file = new File("D:/hope-ppt-photo/" + path);
        if (!file.exists()){
            return null;
        }
        Resource resource = new FileSystemResource(file);
        return resource;
    }
}
