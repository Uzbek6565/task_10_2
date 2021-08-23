package uz.pdp.appjparelationships.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;
import uz.pdp.appjparelationships.entity.Address;
import uz.pdp.appjparelationships.entity.Group;
import uz.pdp.appjparelationships.entity.Student;
import uz.pdp.appjparelationships.entity.Subject;
import uz.pdp.appjparelationships.payload.StudentDto;
import uz.pdp.appjparelationships.repository.*;

import java.util.List;

@RestController
@RequestMapping("/student")
public class StudentController {
    @Autowired
    StudentRepository studentRepository;

    @Autowired
    AddressRepository addressRepository;

    @Autowired
    GroupRepository groupRepository;

    @Autowired
    FacultyRepository facultyRepository;

    @Autowired
    SubjectRepository subjectRepository;


    //1. VAZIRLIK
    @GetMapping("/forMinistry")
    public Page<Student> getStudentListForMinistry(@RequestParam int page) {
        //1-1=0     2-1=1    3-1=2    4-1=3
        //select * from student limit 10 offset (0*10)
        //select * from student limit 10 offset (1*10)
        //select * from student limit 10 offset (2*10)
        //select * from student limit 10 offset (3*10)
        Pageable pageable = PageRequest.of(page, 10);
        Page<Student> studentPage = studentRepository.findAll(pageable);
        return studentPage;
    }

    //2. UNIVERSITY
    @GetMapping("/forUniversity/{universityId}")
    public Page<Student> getStudentListForUniversity(@PathVariable Integer universityId,
                                                     @RequestParam int page) {
        //1-1=0     2-1=1    3-1=2    4-1=3
        //select * from student limit 10 offset (0*10)
        //select * from student limit 10 offset (1*10)
        //select * from student limit 10 offset (2*10)
        //select * from student limit 10 offset (3*10)
        Pageable pageable = PageRequest.of(page, 10);
        Page<Student> studentPage = studentRepository.findAllByGroup_Faculty_UniversityId(universityId, pageable);
        return studentPage;
    }

    //3. FACULTY DEKANAT
    @GetMapping("/forFaculty/{faculty_id}")
    public Page<Student> getStudentListForFaculty(@PathVariable Integer faculty_id, @PathVariable int page){
        Pageable pageable = PageRequest.of(page,10);
        Page<Student> studentPage = studentRepository.findAllByGroup_FacultyId(faculty_id, pageable);
        return studentPage;
    }

    //4. GROUP OWNER
    @GetMapping("/forGroup/{group_id}")
    public Page<Student> getStudentListForGroup(@PathVariable Integer group_id, @RequestParam int page){
        Pageable pageable = PageRequest.of(page,10);
        Page<Student> studentPage = studentRepository.findAllByGroupId(group_id, pageable);
        return studentPage;
    }

    @PostMapping
    public String addNewStudent(@RequestBody StudentDto studentDto){
        if (!groupRepository.existsById(studentDto.getGroupId()))
            return "Group not found";
        Address address = new Address(null,studentDto.getCity(), studentDto.getDistrict(), studentDto.getStreet());
        Address savedAddress = addressRepository.save(address);
        List<Subject> subjectList = subjectRepository.findAllByIdIn(studentDto.getSubjectId());
        Student student = new Student(null, studentDto.getFirstName(), studentDto.getLastName(), savedAddress, groupRepository.getById(studentDto.getGroupId()), subjectList);
        studentRepository.save(student);
        return "Student is added";
    }

    @DeleteMapping("/{id}")
    public String deleteStudent(@PathVariable Integer id){
        if (!studentRepository.existsById(id))
            return "Student not found";
        studentRepository.deleteById(id);
        return "Student is deleted";
    }

    @PutMapping("/{id}")
    public String editStudent(@PathVariable Integer id, @RequestBody StudentDto studentDto){
        if(!studentRepository.existsById(id))
            return "Student not found";
        if (!groupRepository.existsById(studentDto.getGroupId()))
            return "Group not found";

        Student student = studentRepository.getById(id);
        Address address = student.getAddress();
        address.setCity(studentDto.getCity());
        address.setDistrict(studentDto.getDistrict());
        address.setStreet(studentDto.getStreet());
        student.setGroup(groupRepository.getById(studentDto.getGroupId()));
        student.setSubjects(subjectRepository.findAllByIdIn(studentDto.getSubjectId()));

        studentRepository.save(student);
        return "Student is added";
    }

}
