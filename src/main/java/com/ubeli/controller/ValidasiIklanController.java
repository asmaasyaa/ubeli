package com.ubeli.controller;

import org.springframework.stereotype.Controller;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import com.ubeli.entity.BannerIklan;
import com.ubeli.enums.StatusIklan;

import java.time.LocalDate;

@Controller
public class ValidasiIklanController {


    public String aktifkanIklan(@PathVariable Long id) {
        

        return "redirect:/admin/dashboard";
    }
}