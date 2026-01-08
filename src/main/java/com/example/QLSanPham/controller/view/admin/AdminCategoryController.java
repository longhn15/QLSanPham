package com.example.QLSanPham.controller.view.admin;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.example.QLSanPham.entity.Category;
import com.example.QLSanPham.service.impl.CategoryService;

import jakarta.validation.Valid;

@Controller
@RequestMapping("/admin/categories")
public class AdminCategoryController {

  @Autowired
  private CategoryService categoryService;

  @GetMapping("")
  public String list(@RequestParam(value="q", required=false, defaultValue="") String q,
                     Model model,
                     @RequestParam(defaultValue="0") int page,
                     @RequestParam(defaultValue="8") int size) {
    Page<Category> categoryPage = categoryService.search(q, page, size);
    
    model.addAttribute("categories", categoryPage.getContent());
    model.addAttribute("currentPage", page);
    model.addAttribute("pageSize", size);
    model.addAttribute("totalPages", categoryPage.getTotalPages());
    model.addAttribute("totalItems", categoryPage.getTotalElements());
    model.addAttribute("searchQuery", q);
    
    return "admin/CategoryAdmin"; 
  }

  @GetMapping("/add")
  public String showAddForm(Model model) {
    model.addAttribute("category", new Category());
    model.addAttribute("isEdit", false);
    return "admin/FormCategory";
  }

  @GetMapping("/edit/{id}")
  public String showEditForm(@PathVariable("id") Long id, Model model) {
      Optional<Category> categoryOpt = categoryService.findById(id);
      if (categoryOpt.isPresent()) {
          model.addAttribute("category", categoryOpt.get());
          model.addAttribute("isEdit", true);
          return "admin/FormCategory";
      } else {
          return "redirect:/admin/categories";
      }
  }

  @GetMapping("/delete/{id}")
  public String deleteCategory(@PathVariable("id") Long id, RedirectAttributes ra) {
    try {
        categoryService.deleteById(id);
        ra.addFlashAttribute("successMessage", "Đã xóa danh mục thành công!");
    } catch (Exception e) {
        ra.addFlashAttribute("errorMessage", "Không thể xóa danh mục này vì có sản phẩm đang sử dụng!");
    }
    return "redirect:/admin/categories";
  }

  @PostMapping("/save")
  public String saveCategory(@Valid @ModelAttribute("category") Category category, 
                            BindingResult result, 
                            RedirectAttributes ra, 
                            Model model) {
    // Kiểm tra tên danh mục đã tồn tại khi thêm mới
    if (category.getId() == null && categoryService.existsByName(category.getName())) {
      result.rejectValue("name", "error.name", "Tên danh mục đã tồn tại!");
    }
    
    if (result.hasErrors()) {
        model.addAttribute("isEdit", false);
        return "admin/FormCategory";
    }
    
    categoryService.save(category);
    ra.addFlashAttribute("successMessage", "Dữ liệu đã lưu thành công!");
    return "redirect:/admin/categories";
  }

  @PostMapping("/update")
  public String updateCategory(@Valid @ModelAttribute("category") Category category, 
                              BindingResult result, 
                              RedirectAttributes ra, 
                              Model model) {
    // Kiểm tra tên danh mục đã tồn tại (ngoại trừ chính nó)
    Optional<Category> existingCategory = categoryService.findById(category.getId());
    if (existingCategory.isPresent()) {
        String oldName = existingCategory.get().getName();
        if (!oldName.equals(category.getName()) && categoryService.existsByName(category.getName())) {
            result.rejectValue("name", "error.name", "Tên danh mục đã tồn tại!");
        }
    }
    
    if (result.hasErrors()) {
        model.addAttribute("isEdit", true);
        return "admin/FormCategory";
    }
    
    categoryService.save(category);
    ra.addFlashAttribute("successMessage", "Dữ liệu đã cập nhật thành công!");
    return "redirect:/admin/categories";
  }
}
