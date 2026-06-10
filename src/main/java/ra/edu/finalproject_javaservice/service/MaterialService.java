package ra.edu.finalproject_javaservice.service;

import com.cloudinary.Cloudinary;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import ra.edu.finalproject_javaservice.dto.CreateMaterialRequest;
import ra.edu.finalproject_javaservice.dto.MaterialResponse;
import ra.edu.finalproject_javaservice.entity.Material;
import ra.edu.finalproject_javaservice.exception.BadRequestException;
import ra.edu.finalproject_javaservice.exception.NotFoundException;
import ra.edu.finalproject_javaservice.repository.CourseRepository;
import ra.edu.finalproject_javaservice.repository.MaterialRepository;

import java.io.IOException;
import java.util.Map;

@Service
public class MaterialService {
    private final MaterialRepository materialRepository;
    private final CourseRepository courseRepository;
    private final Cloudinary cloudinary;
    public MaterialService(MaterialRepository materialRepository, CourseRepository courseRepository, Cloudinary cloudinary) {
        this.materialRepository = materialRepository; this.courseRepository = courseRepository; this.cloudinary = cloudinary;
    }
    public MaterialResponse create(CreateMaterialRequest request) {
        Material m = new Material();
        m.setCourse(courseRepository.findById(request.courseId()).orElseThrow(() -> new NotFoundException("Course not found")));
        m.setMaterialName(request.materialName());
        m.setFileUrl(request.fileUrl());
        materialRepository.save(m);
        return new MaterialResponse(m.getId(), m.getCourse().getId(), m.getMaterialName(), m.getFileUrl());
    }
    public java.util.List<MaterialResponse> findByCourse(Long courseId) {
        return materialRepository.findByCourse_Id(courseId).stream()
                .map(m -> new MaterialResponse(m.getId(), m.getCourse().getId(), m.getMaterialName(), m.getFileUrl())).toList();
    }
    public void delete(Long id) { materialRepository.deleteById(id); }
    public MaterialResponse upload(Long courseId, String materialName, MultipartFile file) {
        try {
            Map<?, ?> upload = cloudinary.uploader().upload(file.getBytes(), Map.of(
                    "resource_type", "auto",
                    "folder", "finalproject/materials"
            ));
            return create(new CreateMaterialRequest(courseId, materialName, upload.get("secure_url").toString()));
        } catch (IOException e) {
            throw new RuntimeException("Cloud upload failed");
        }
    }
}
